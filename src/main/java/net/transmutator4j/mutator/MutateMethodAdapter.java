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
import net.transmutator4j.mutations.IncrementMutation;
import net.transmutator4j.mutations.ConditionalJumpMutation;
import net.transmutator4j.util.TransmorgifyUtil;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

public class MutateMethodAdapter extends MethodVisitor {

	private final Mutator mutator;
	private int mutatedLine;
	public MutateMethodAdapter(MethodVisitor mv,Mutator mutator) {
		super(TransmorgifyUtil.CURRENT_ASM_VERSION, mv);
		this.mutator = mutator;
	}

	/* (non-Javadoc)
	 * @see org.objectweb.asm.MethodAdapter#visitLineNumber(int, org.objectweb.asm.Label)
	 */
	@Override
	public void visitLineNumber(int line, Label start) {
		super.visitLineNumber(line, start);
		mutatedLine = line;
	}

	/* (non-Javadoc)
	 * @see org.objectweb.asm.MethodAdapter#visitJumpInsn(int, org.objectweb.asm.Label)
	 */
	@Override
	public void visitJumpInsn(int opcode, Label label) {
			ConditionalJumpMutation mutation = ConditionalJumpMutation.getMutationsFor(opcode);
			if(mutation !=null && mutator.shouldMutate() ){
				mutator.mutate(mutatedLine,mutation);
				super.visitJumpInsn(mutation.getMutatedOpCode(), label);
				return;
			}
	
		super.visitJumpInsn(opcode, label);
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void visitIincInsn(int var, int increment) {
		if(mutator.shouldMutate()){
			IncrementMutation mutation = new IncrementMutation(increment);
			mutator.mutate(mutatedLine,mutation);
			super.visitIincInsn(var, mutation.getMutatedIncrement());
		}
		else{
			super.visitIincInsn(var, increment);
		}
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void visitInsn(int opcode) {
		int opcodeToUse = opcode;
		if(BooleanMutation.isMutatable(opcode)){
			if(mutator.shouldMutate()){
				BooleanMutation mutation =BooleanMutation.getMutationFor(opcode);
				mutator.mutate(mutatedLine,mutation);
				opcodeToUse = mutation.getMutatedOpCode();
			}
		}else if(ArithmeticMutation.isMutatable(opcode)){
			if(mutator.shouldMutate()){
				ArithmeticMutation mutation =ArithmeticMutation.getMutationFor(opcode);
				mutator.mutate(mutatedLine,mutation);
				opcodeToUse = mutation.getMutatedOpCode();
			}
		}
		super.visitInsn(opcodeToUse);
	}

	
	

	
	

	
}
