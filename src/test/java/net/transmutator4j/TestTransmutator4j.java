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

import java.lang.reflect.InvocationTargetException;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

import static org.junit.Assert.*;
/**
 * Unit tests for {@link Transmutator4j} class.
 * Since there are still problems with "jar-hell" with conflicting
 * versions of cglib for EasyMock and the latest version of ASM library
 * we can't use class mocking of JUnit classes so we have to create our own
 * stubs.
 * @author dkatzel-home
 *
 */
public class TestTransmutator4j {
	
	private static class TransmorgifyTestDouble extends Transmutator4j{
		
		
		public TransmorgifyTestDouble(JUnitCore junitCore, String testClass)
				throws ClassNotFoundException, IllegalArgumentException,
				SecurityException, IllegalAccessException,
				InvocationTargetException, NoSuchMethodException {
			super(junitCore, testClass);
		}

		@Override
		protected Class<?> getLoadedClass(String testClass)
				throws ClassNotFoundException {
			//this class doesn't exist so don't load it
			//our test doesn't care what the return value of this is anyway
			return null;
		}
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void invalidClassNameHasSpaceShouldThrowException() throws ClassNotFoundException, IllegalArgumentException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		Result result = createSuccessfulResult();
		JUnitCore mockCore = createMockCore(result);
		new TransmorgifyTestDouble(mockCore,"invalid class");
	}
	@Test
	public void validClassNameWith$() throws ClassNotFoundException, IllegalArgumentException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		
		Result result = createSuccessfulResult();
		JUnitCore mockCore = createMockCore(result);		
		Transmutator4j sut = new TransmorgifyTestDouble(mockCore,"package.myClass$innerClass");
		assertTrue(sut.didTestsStillPass());
		assertEquals(0, sut.getNumberOfFailedTests());
	}
	
	@Test
	public void allUnitTestsPass() throws ClassNotFoundException, IllegalArgumentException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		
		Result result = createSuccessfulResult();
		JUnitCore mockCore = createMockCore(result);		
		Transmutator4j sut = new TransmorgifyTestDouble(mockCore,this.getClass().getName());		
		assertTrue(sut.didTestsStillPass());
		assertEquals(0, sut.getNumberOfFailedTests());
	}
	
	@Test
	public void someTestsFail() throws ClassNotFoundException, IllegalArgumentException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		Result result = createFailedResult(10);
		JUnitCore mockCore = createMockCore(result);		
		Transmutator4j sut = new TransmorgifyTestDouble(mockCore,this.getClass().getName());		
		assertFalse(sut.didTestsStillPass());
		assertEquals(10, sut.getNumberOfFailedTests());
	}
	
	@Test
	public void somekindOfExceptionIsThrown(){
		RuntimeException expectedException = new RuntimeException("expected");
		JUnitCore mockCore = createMockCoreThatThrowsExceptionOnRun(expectedException);
		try{
			new TransmorgifyTestDouble(mockCore,this.getClass().getName());
			fail("should throw exception");
		}catch(Exception e){
			assertEquals(expectedException, e.getCause());
		}
	}
	protected JUnitCore createMockCoreThatThrowsExceptionOnRun(final RuntimeException e){
		return new JUnitCore(){

			@Override
			public Result run(Class<?>... classes) {
				throw e;
			}
			
		};
	}
	protected JUnitCore createMockCore(final Result result){
		return new JUnitCore(){
			@Override
			public Result run(Class<?>... classes) {
				return result;
			}
			
		};
	}
	protected Result createFailedResult(final int numberOfFailures){
		return new Result(){

			@Override
			public int getFailureCount() {
				return numberOfFailures;
			}

			@Override
			public boolean wasSuccessful() {
				return false;
			}
			
		};
		
	}
	
	protected Result createSuccessfulResult(){
		return new Result(){

			@Override
			public int getFailureCount() {
				return 0;
			}

			@Override
			public boolean wasSuccessful() {
				return true;
			}
			
		};
	}
}
