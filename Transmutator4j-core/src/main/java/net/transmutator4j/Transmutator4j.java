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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import net.transmutator4j.mutator.MutateClassAdapter;
import net.transmutator4j.mutator.MutatedClassLoader;
import net.transmutator4j.repo.ClassPathClassRepository;
import net.transmutator4j.util.TransmutatorUtil;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runner.notification.StoppedByUserException;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

public class Transmutator4j {

	
	private boolean testsStillPassed;
	private int numberOfFailedTests;
	/**
	 * 
	 * @param junitCore
	 * @param testClass the class name of the test class/suite to run in JUnit.  This name
	 * may also contain packages such as {@literal my.package.myClass}.
	 * @throws ClassNotFoundException
	 * @throws IllegalArgumentException if testClass is not a valid class name.
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	public Transmutator4j(JUnitCore junitCore, String testClass) throws ClassNotFoundException, IllegalArgumentException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		if(!isValidJavaIdentifer(testClass)){
			throw new IllegalArgumentException(String.format("invalid test class name '%s'", testClass));
		}
		junitCore.addListener(new EarlyStopper(junitCore));
		try{
			Result result = junitCore.run(getLoadedClass(testClass));
			testsStillPassed = result.wasSuccessful();
			numberOfFailedTests = result.getFailureCount();
		}catch(StoppedByUserException e){
			//this will be thrown if EarlyStopper 
			//halts tests so it's OK
			numberOfFailedTests=1;
			testsStillPassed=false;
		}catch(Throwable t){
			t.printStackTrace(System.out);
			throw new RuntimeException(t);
		}
		
	}


	protected Class<?> getLoadedClass(String testClass)
			throws ClassNotFoundException {
		return Class.forName(testClass);
	}

	
	private boolean isValidJavaIdentifer(String testClass) {
		
		
		if(!Character.isJavaIdentifierStart(testClass.codePointAt(0))){
			return false;
		}
		for(int i=1; i< testClass.length(); i++){
			if(testClass.charAt(i) !='.' && !Character.isJavaIdentifierPart(testClass.codePointAt(i))){
				return false;
			}
		}
		return true;
		
	}


	/**
	 * @return the mutationsMadeTestsFail
	 */
	public boolean didTestsStillPass() {
		return testsStillPassed;
	}

	/**
	 * @return the numberOfFailedTests
	 */
	public int getNumberOfFailedTests() {
		return numberOfFailedTests;
	}

	
	public static void main(String[] args){
		String classToMutate = args[0];
		String nameOfTestSuite = args[1];
		int mutationCounter = Integer.parseInt(args[2]);
		String xmlFile = args[3];
		ClassWriter writer = new ClassWriter(0);
		System.out.println("in main");
		final MutateClassAdapter adapter = new MutateClassAdapter(writer,mutationCounter);
		try{
		ClassReader cr = new ClassReader(classToMutate);
		cr.accept(adapter, 0);
			final byte[] compiled =writer.toByteArray();
			final MutatedClassLoader classLoader = new MutatedClassLoader(Transmutator4j.class.getClassLoader(),
					new ClassPathClassRepository(), classToMutate, compiled);
			if(adapter.hasMutated()){
			
				
				Class<?> c =Class.forName("net.transmutator4j.Transmutator4j",false, classLoader);
				Constructor<?> constructor= c.getDeclaredConstructor(
						classLoader.loadClass("org.junit.runner.JUnitCore"),
						classLoader.loadClass("java.lang.String"));
				Object transmorgifier =constructor.newInstance(new JUnitCore(),nameOfTestSuite);
				boolean testsStillPass = (Boolean)c.getDeclaredMethod("didTestsStillPass").invoke(transmorgifier);
				int failCount = (Integer)c.getDeclaredMethod("getNumberOfFailedTests").invoke(transmorgifier);
				
				FileOutputStream out = new FileOutputStream(new File(xmlFile), true);
				PrintWriter pw = new PrintWriter(out);
				pw.append(String.format(
						"\t<mutation class =\"%s\" line=\"%d\" descr=\"%s\" tests_still_passed =\"%s\">%s</mutation>%n", 
						adapter.getMutatedClassname(),
						adapter.getMutatedLine(),
						TransmutatorUtil.xmlEncode(adapter.getMutation().description()),
						failCount==0,
						testsStillPass? "Passed": "Failed"));
				pw.flush();
				pw.close();
				if(testsStillPass){
					TransmutatorUtil.EXIT_STATES.TESTS_ALL_STILL_PASSED.exitSystem();
				}
				TransmutatorUtil.EXIT_STATES.TESTS_FAILED.exitSystem();
			}
		}catch(ReflectiveOperationException | IOException e){
			e.printStackTrace();
			//can't find class don't worry about it
			System.err.println("could not mutate "+ classToMutate);
			
		}
		TransmutatorUtil.EXIT_STATES.NO_MUTATIONS_MADE.exitSystem();

			
	}
	
	private static class EarlyStopper extends RunListener{
		private final RunNotifier runNotifier;
		public EarlyStopper(JUnitCore junitCore){
			//junitCore's runNotifier private field
			//is the only place that can be used
			//to halt tests currently running
			//but we need to use reflection
			//to get a reference to it.
			//We can use this to halt tests
			//as soon as possible
			//(when we know at least one test failed).
			try {
				Field field = JUnitCore.class.getDeclaredField("fNotifier");
				field.setAccessible(true);
				runNotifier = (RunNotifier) field.get(junitCore);
				    
			} catch (Exception e) {
				throw new IllegalStateException("could not get runNotifier", e);
			}
		   
		}
		@Override
		public void testFailure(Failure failure) throws Exception {
			//as soon as we get a unit test failure
			//we can stop running our test suite
			runNotifier.pleaseStop();
		}
		
	}
}
