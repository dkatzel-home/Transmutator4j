/*******************************************************************************
 * Copyright (c) 2010 - 2013 Danny Katzel.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package net.transmutator4j;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.Channels;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.transmutator4j.repo.ClassPathClassRepository;
import net.transmutator4j.util.JavaProcessBuilder;
import net.transmutator4j.util.TimedProcess;
import net.transmutator4j.util.TransmutatorUtil;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

/**
 * {@code RunTransmutator4j} is the main class to run
 * Transmutator4j to mutate source classes.
 */
public class RunTransmutator4j implements Runnable{
	private static final String DEFAULT_OUTPUT_XML = "transmutator4j.xml";
	
	private final String nameOfTestSuite;
	private final Pattern classesToInclude, classesToExclude;
	private int numberOfMutationsMade=0;
	private int numberOfMutationsThatStillPassedTests=0;
	private int numberOfMutationsThatTimedOut=0;
	private final  MutationTestListener listener;
	
	public RunTransmutator4j(String nameOfTestSuite, Pattern include, Pattern exclude,
			File xmlFile) throws IOException {
		this.nameOfTestSuite = nameOfTestSuite;
		this.classesToInclude = include;
		this.classesToExclude = exclude;
		listener = new XmlWriterListener(xmlFile);
	}
	
	private boolean shouldMutate(String qualifiedClassname){
		if(classesToInclude !=null){
			Matcher matcher = classesToInclude.matcher(qualifiedClassname);
			if(!matcher.matches()){
				return false;
			}
		}
		if(classesToExclude !=null){
			Matcher matcher = classesToExclude.matcher(qualifiedClassname);
			if(matcher.matches()){
				return false;
			}
		}
		return true;
	}
	
	@Override
	public void run(){
		
		long startTime = System.currentTimeMillis();
		InetAddress localhost;
		try {
			localhost = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			throw new IllegalStateException("can not resolve local host", e);
		}
		//socket get dynamically generated port
		SocketAddress socket = new InetSocketAddress(localhost, 0);
		AsynchronousChannelGroup group;
		try {
			group = AsynchronousChannelGroup.withFixedThreadPool(10, Executors.defaultThreadFactory());
		} catch (IOException e1) {
			throw new IllegalStateException("can not create channel group", e1);
		}

		try(    
				
				AsynchronousServerSocketChannel server =  AsynchronousServerSocketChannel.open(group).bind(socket, 1);
				
				) {
		
			int numTotalTests = runUnmutatedTests();
			long unmutatedTimeEnd = System.currentTimeMillis();
			long unmutatedElapsedTime = unmutatedTimeEnd - startTime;
			listener.testInfo(numTotalTests, unmutatedElapsedTime);
			System.out.println("unmutated tests took " + unmutatedElapsedTime + " ms");
			long timeOut = computeTimeoutTime(unmutatedElapsedTime);
			InetSocketAddress inetSocketAddress = (InetSocketAddress) server.getLocalAddress();
			int port = inetSocketAddress.getPort();
			server.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {
			    @Override
			    public void completed(AsynchronousSocketChannel resultChannel, Object attachment) {
			       try{
			    	ObjectInputStream in = new ObjectInputStream(Channels.newInputStream(resultChannel));
					MutationTestResult testResult = (MutationTestResult) in.readObject();
					
					in.close();
			       
					listener.mutationResult(testResult);
					boolean stillPassed =testResult.testsStillPassed();
					System.out.print(stillPassed? "P":".");
					System.out.flush();
					numberOfMutationsMade++;
					
					if (stillPassed) {
						numberOfMutationsThatStillPassedTests++;
					}
			       }catch(IOException | ClassNotFoundException e){
			    	   e.printStackTrace();
			    	   throw new RuntimeException("error getting test result ", e);
			       }
			       //accept a new connection
			       server.accept(null, this);
			       
			    }
			    @Override
			    public void failed(Throwable e, Object attachment) {
			     //   System.err.println(attachment + " failed with:" + e.getClass().getName());
			    //    e.printStackTrace();
			    }
			});
				    
			OUTER: for (String classToMutate : new ClassPathClassRepository()) {
				boolean shouldMutate = shouldMutate(classToMutate);
				if (shouldMutate) {
					System.out.printf("mutating %s%n", classToMutate);
					boolean done = false;
					int mutationCount = 0;
					while (!done) {
						JavaProcessBuilder builder = new JavaProcessBuilder(

						"net.transmutator4j.Transmutator4j", classToMutate,
								nameOfTestSuite,
								Integer.toString(mutationCount),
								Integer.toString(port));

						
						try {
							TimedProcess timedProcess = new TimedProcess(
									builder.getBuilder(), timeOut);
							int exitValue = timedProcess.call();

							TransmutatorUtil.EXIT_STATES exitState = TransmutatorUtil.EXIT_STATES
									.getValueFor(exitValue);
							if (exitState == TransmutatorUtil.EXIT_STATES.NO_MUTATIONS_MADE) {
								done = true;
								System.out.println();
							} else if(exitState ==TransmutatorUtil.EXIT_STATES.TIMED_OUT) {
								numberOfMutationsThatTimedOut++;

							}

						} catch (InterruptedException e) {
							System.err.println("detected cancellation...halting");
							//stop iterating through all the classes
							//by breaking out of outer for loop
							break OUTER;
						}

						mutationCount++;

					}

				}
			}
			//kill any waiting connections this will cause the completionHandler's fail to get called
			group.shutdownNow();
			//group.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
		
		long endTime = System.currentTimeMillis();
		System.out.printf("took %d ms to run %d mutations of which %d caused timeouts and %d still passed%n",
						(endTime - startTime), 
						numberOfMutationsMade,
						numberOfMutationsThatTimedOut,
						numberOfMutationsThatStillPassedTests);
		
		} catch (Exception e) {
			throw new RuntimeException(e);
		}finally{
			if(listener !=null){
				try {
					listener.close();
				} catch (IOException ignored) {
					//ignore
				}
			}
			group.shutdown();
		}
	}


	private long computeTimeoutTime(long unmutatedElapsedTime) {
		//+1 is added in case test suite is so fast that
		//it takes 0 milliseconds
		long timeOut = ((unmutatedElapsedTime/1000)+1)*10_000;
		return timeOut;
	}
	
	
	private int runUnmutatedTests() throws IOException, ClassNotFoundException {
		JUnitCore junit = new JUnitCore();
		junit.addListener(new RunListener(){

			
			@Override
			public void testFailure(Failure failure) throws Exception {
				System.out.println("failed " + failure);
			}

			
		});
		Result result = junit.run(Class.forName(nameOfTestSuite));
		if( !result.wasSuccessful()){
			throw new IllegalStateException(String.format(
					"un-mutated tests failed %d / %d", result.getRunCount()-result.getFailureCount(), result.getRunCount()));
		}
		return result.getRunCount();
	}
	/**
	 * @param args
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws SecurityException 
	 * @throws IllegalArgumentException 
	 * @throws InstantiationException 
	 */
	@SuppressWarnings("static-access")
	public static void main(String[] args) throws IOException, ClassNotFoundException, IllegalArgumentException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {
		Options options = new Options();
		
        options.addOption(
        		OptionBuilder.withArgName( "test" )
                .hasArg()
                .withDescription(  "run given unit test" )
                .isRequired()
                .create( "test" ));
        options.addOption(
        		OptionBuilder.withArgName( "src" )
                .withDescription(  "source folder(s) to transmorgified classes" )
                .hasArgs()
                .create( "src" ));
        options.addOption(
        		OptionBuilder.withArgName( "include" )
                .withDescription(  "regular expression of classes to transmutate" )
                .hasArg()
                .create( "include" ));
        options.addOption(
        		OptionBuilder.withArgName( "exclude" )
                .withDescription(  "regular expression of classes NOT to transmutate" )
                .hasArg()
                .create( "exclude" ));
        options.addOption(
        		OptionBuilder.withArgName( "out" )
                .hasArg()
                .withDescription(  "xml output file to write results to (default : "+ DEFAULT_OUTPUT_XML +")" )
                .create( "out" ));
        
     
        CommandLineParser parser = new GnuParser();
        try {
			CommandLine commandLine =parser.parse(options, args);
			
			final String nameOfTestSuite = commandLine.getOptionValue("test");
			final Pattern classesToInclude = getPattern(commandLine,"include");
			final Pattern classesToExclude =  getPattern(commandLine,"exclude");
			File xmlFile;
			if(commandLine.hasOption("out")){
				xmlFile = new File(commandLine.getOptionValue("out"));
			}
			else{
				xmlFile = new File(DEFAULT_OUTPUT_XML);
			}
			
			new RunTransmutator4j(nameOfTestSuite, classesToInclude, classesToExclude, xmlFile).run();
		
		} catch (ParseException e1) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp( "RunTransmutator4j -src <src> -test <unit test> [-out <xml file>]", options );
		}
	}
	
	private static Pattern getPattern(CommandLine commandline, String arg){
		if(commandline.hasOption(arg)){
			return Pattern.compile(commandline.getOptionValue(arg));
		}
		return null;
	}
}
