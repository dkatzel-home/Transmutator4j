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
package net.transmutator4j.mutator;

import net.transmutator4j.mutations.IncrementMutation;
import net.transmutator4j.mutations.Mutation;

import org.junit.Test;
import static org.easymock.EasyMock.*;
public class TestMutateMethodAdapterIncrementIns extends AbstractTestMutateMethodAdapter{

	int var = 1234;
	int increment = 1;
	@Test
	public void shouldNotMutateShouldDelegateToParent(){
		expect(mockMutator.shouldMutate()).andReturn(false);
		mockVisitor.expectIncreInstr(var, increment);
		
		replay(mockMutator);
		sut.visitIincInsn(var, increment);
		verify(mockMutator);
		mockVisitor.verifyAllMethodsCalled();
	}
	@Test
	public void mutatePositiveIncrement(){
		Mutation expectedMutation = new IncrementMutation(increment);
		expect(mockMutator.shouldMutate()).andReturn(true);
		mockVisitor.expectLineNumber(lineNumber, label);
		mockVisitor.expectIncreInstr(var, -1*increment);
		
		mockMutator.mutate(lineNumber, expectedMutation);
		replay( mockMutator);
		sut.visitLineNumber(lineNumber, label);
		sut.visitIincInsn(var, increment);
		verify( mockMutator);
		mockVisitor.verifyAllMethodsCalled();
	}
	@Test
	public void mutateNegativeIncrement(){
		Mutation expectedMutation = new IncrementMutation(-1*increment);
		expect(mockMutator.shouldMutate()).andReturn(true);
		mockVisitor.expectLineNumber(lineNumber, label);
		mockVisitor.expectIncreInstr(var, increment);
		
		mockMutator.mutate(lineNumber, expectedMutation);
		replay( mockMutator);
		sut.visitLineNumber(lineNumber, label);
		sut.visitIincInsn(var, -1*increment);
		verify( mockMutator);
		mockVisitor.verifyAllMethodsCalled();
	}
}
