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

import net.transmutator4j.mutations.ConditionalJumpMutation;

import org.junit.Test;

import static org.easymock.EasyMock.*;
import static net.transmutator4j.mutator.TestMutatorUtil.UNUSED_OPCODE;
public class TestMutateMethodAdapterJumpInstruction extends AbstractTestMutateMethodAdapter {

	private static final ConditionalJumpMutation MUTATION = ConditionalJumpMutation.IF_ACMPEQ_TO_IF_ACMPNE;
	
	private static int OPCODE = MUTATION.getOriginalOpCode();
	private static int MUTATED_OPCODE = MUTATION.getMutatedOpCode();
	
	
	@Test
	public void canNotMutateJumpShouldDelegateToParent(){
		mockVisitor.expectJumpInstr(UNUSED_OPCODE, label);	
		replay(mockMutator);
		sut.visitJumpInsn(UNUSED_OPCODE, label);
		verify(mockMutator);
		mockVisitor.verifyAllMethodsCalled();
	}
	@Test
	public void shouldNotMutateJumpShouldDelegateToParent(){
		mockVisitor.expectJumpInstr(OPCODE, label);
		
		expect(mockMutator.shouldMutate()).andReturn(false);
		replay( mockMutator);
		sut.visitJumpInsn(OPCODE, label);
		verify( mockMutator);
		mockVisitor.verifyAllMethodsCalled();
	}
	@Test
	public void shouldMutateJump(){
		mockVisitor.expectLineNumber(lineNumber, label);
		mockVisitor.expectJumpInstr(MUTATED_OPCODE, label);		
		
		mockMutator.mutate(lineNumber, MUTATION);
		expect(mockMutator.shouldMutate()).andReturn(true);
		replay(mockMutator);
		sut.visitLineNumber(lineNumber, label);
		sut.visitJumpInsn(OPCODE, label);
		verify(mockMutator);
		mockVisitor.verifyAllMethodsCalled();
	}
}
