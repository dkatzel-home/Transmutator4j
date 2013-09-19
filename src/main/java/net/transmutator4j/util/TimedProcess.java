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
package net.transmutator4j.util;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;

/**
 * {@code TimedProcess} runs a {@link Process}
 * for limited amount of time.  If the process
 * hasn't completed by that time, a {@link TimeoutException}
 * is thrown.
 * @author dkatzel-home
 *
 */
public class TimedProcess implements Callable<Integer>{

	private final long timeout;
	private final ProcessBuilder process;
	/**
	 * Creates a TimedProcess instance with the given
	 * timeout.
	 * @param processToRun the {@link ProcessBuilder} to run.
	 * @param timeout max time (in milliseconds) a 
	 * process may run.
	 */
	public TimedProcess(ProcessBuilder processToRun,long timeout) {
		this.process = processToRun;
		this.timeout = timeout;
	}
	/**
	 * Run the given process.
	 * @return the exit code of the process after it completes.
	 * @throws TimeoutException if the process takes longer
	 * than the specified max timeout.
	 * @throws Exception
	 */
	@Override
	public Integer call() throws Exception { 
		Process p = process.start();
		return call(p);

	}
	/**
	 * Package private method that actually performs
	 * work on thread.  This is not set to private
	 * for now so it can be unit tested.
	 * @param p the process being timed (should already
	 * be running).
	 * @return the exitValue as an Integer or null
	 * @throws InterruptedException
	 */
	Integer call(Process p) throws InterruptedException {
		Worker worker = new Worker(p);
		worker.start();
		
		  try {			  
			  
		    worker.join(timeout);
		    
		    if (worker.getExitValue() != null){
		      return worker.getExitValue();
		    }
		    else{
		    	return TransmutatorUtil.EXIT_STATES.TIMED_OUT.getExitValue();
		    }
		  } catch(InterruptedException ex) {			
		    Thread.currentThread().interrupt();
		    throw ex;
		  } finally {
		    p.destroy();
		  }
	}
	
	private static final class Worker extends Thread{
		private final Process process;
		private Integer exitValue = null;
		
		private Worker(Process process){
			this.process = process;
		}
		@Override
		public void run() {
			//drain input and error
			//stream in case it causes blocking
			ProcessStreamReader.create(process.getInputStream());
	    	ProcessStreamReader.create(process.getErrorStream());
	    	
		    try { 
		    	
		    	exitValue = process.waitFor();
		    } catch (InterruptedException ignore) {
		      return;
		    }
		    
		  }
		
		private Integer getExitValue(){
			return exitValue;
		}
		/* (non-Javadoc)
		 * @see java.lang.Thread#interrupt()
		 */
		@Override
		public void interrupt() {
			process.destroy();
			super.interrupt();
		}
		
	}
	
	
}
