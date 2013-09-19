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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;


public class TestTimedProcess {
	private final long timeout=100;
	private TimedProcess sut;
	ProcessBuilder builder;
	@Before
	public void setup() throws IOException{
		
		sut = new TimedProcess(new ProcessBuilder(),timeout);
	}
	@Test
	public void timeOutShouldThrowInterruptedException() throws Exception{

		LongRunningProcess process = new LongRunningProcess();
		sut.call(process);
		assertTrue(process.isDestroyed());
	}
	
	@Test
	public void processThatSuccessfullyRunsToCompletion() throws Exception{
		processThatRunsToCompletion(0);
	}
	@Test
	public void processThatErrorsOutButRunsToCompletion() throws Exception{
		processThatRunsToCompletion(1);
	}
	private void processThatRunsToCompletion(int returnValue)
			throws Exception {
		FakeProcess process = new FakeProcess(returnValue);
		
		assertEquals(Integer.valueOf(returnValue),sut.call(process));
		assertTrue(process.isDestroyed());
	}
	private class LongRunningProcess extends AbstractFakeProcess{

		@Override
		public int waitFor() throws InterruptedException {
			Thread.sleep(timeout*2);
			return 0;
		}

		@Override
		public int exitValue() {
			return 0;
		}
		
		
		
	}
	private static class FakeProcess extends AbstractFakeProcess{
		private final int returnCode;
		
		public FakeProcess(int returnCode) {
			this.returnCode = returnCode;
		}
		@Override
		public int exitValue() {
			return returnCode;
		}
		@Override
		public int waitFor() throws InterruptedException {
			return returnCode;
		}

		
	}
	
	private static abstract class AbstractFakeProcess extends Process{
		private boolean destroyed=false;
		
		public AbstractFakeProcess(){
			
		}

		@Override
		public OutputStream getOutputStream() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public InputStream getInputStream() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public InputStream getErrorStream() {
			// TODO Auto-generated method stub
			return null;
		}

		
		@Override
		public void destroy() {
			destroyed=true;
			
		}

		public boolean isDestroyed() {
			return destroyed;
		}
		
	}
}
