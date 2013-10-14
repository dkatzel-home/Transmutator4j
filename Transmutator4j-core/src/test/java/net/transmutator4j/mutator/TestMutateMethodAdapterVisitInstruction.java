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

import net.transmutator4j.mutations.ArithmeticMutation;
import net.transmutator4j.mutations.BooleanMutation;
import net.transmutator4j.mutations.Mutation;

import org.junit.Test;
import org.objectweb.asm.Opcodes;

import static org.easymock.EasyMock.*;
public class TestMutateMethodAdapterVisitInstruction extends AbstractTestMutateMethodAdapter{

	int booleanOpCode = Opcodes.ICONST_0;
	int arithmeticOpCode = Opcodes.IADD;
	@Test
	public void unmutateableOpcodeShouldDelegateToParent(){
		mockVisitor.expectInsn(TestMutatorUtil.UNUSED_OPCODE);
		replay( mockMutator);
		sut.visitInsn(TestMutatorUtil.UNUSED_OPCODE);
		verify( mockMutator);
		mockVisitor.verifyAllMethodsCalled();
	}
	@Test
	public void booleanInstructionShouldNotMutateShouldDelegateToParent(){
		mockVisitor.expectInsn(booleanOpCode);
		expect(mockMutator.shouldMutate()).andReturn(false);
		replay(mockMutator);
		sut.visitInsn(booleanOpCode);
		verify(mockMutator);
		mockVisitor.verifyAllMethodsCalled();
	}
	@Test
	public void booleanInstructionShouldMutate(){
		Mutation expectedMutation = BooleanMutation.getMutationFor(booleanOpCode);
		mockVisitor.expectInsn(expectedMutation.getMutatedOpCode());
		mockVisitor.expectLineNumber(lineNumber, label);
		mockMutator.mutate(lineNumber, expectedMutation);
		expect(mockMutator.shouldMutate()).andReturn(true);
		replay(mockMutator);
		sut.visitLineNumber(lineNumber, label);
		sut.visitInsn(booleanOpCode);
		verify(mockMutator);
		mockVisitor.verifyAllMethodsCalled();
	}
	
	@Test
	public void arithmeticInstructionShouldNotMutateShouldDelegateToParent(){
		mockVisitor.expectInsn(arithmeticOpCode);
		expect(mockMutator.shouldMutate()).andReturn(false);
		replay(mockMutator);
		sut.visitInsn(arithmeticOpCode);
		verify(mockMutator);
		mockVisitor.verifyAllMethodsCalled();
	}
	
	@Test
	public void arithmeticInstructionShouldMutate(){
		Mutation expectedMutation = ArithmeticMutation.getMutationFor(arithmeticOpCode);
		
		mockVisitor.expectLineNumber(lineNumber, label);
		mockVisitor.expectInsn(expectedMutation.getMutatedOpCode());
		mockMutator.mutate(lineNumber, expectedMutation);
		expect(mockMutator.shouldMutate()).andReturn(true);
		replay( mockMutator);
		sut.visitLineNumber(lineNumber, label);
		sut.visitInsn(arithmeticOpCode);
		verify(mockMutator);
		mockVisitor.verifyAllMethodsCalled();
	}
}
