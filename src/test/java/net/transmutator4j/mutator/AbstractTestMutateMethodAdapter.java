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

import static org.easymock.EasyMock.*;

import java.util.ArrayList;
import java.util.List;

import net.transmutator4j.util.TransmorgifyUtil;

import org.junit.Before;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

public abstract class AbstractTestMutateMethodAdapter {

	protected Mutator mockMutator;
	protected MockMethodVisitor mockVisitor;
	protected MutateMethodAdapter sut;
	protected final Label label = new Label();
	protected final int lineNumber = 99999;
	@Before
	public void setup(){
		mockMutator = createMock(Mutator.class);
		mockVisitor = new MockMethodVisitor();
		sut = new MutateMethodAdapter(mockVisitor, mockMutator);
	}
	
	
	public static class MockMethodVisitor extends MethodVisitor{
		private final List<Expectations> jumpExpectations;
		private final List<Expectations> lineNumberExpectations;
		private final List<Expectations> incrementInstructionExpectations;
		private final List<Expectations> instructionExpectations;
		public MockMethodVisitor() {
			super(TransmorgifyUtil.CURRENT_ASM_VERSION);
			jumpExpectations = new ArrayList<>();
			lineNumberExpectations = new ArrayList<>();
			incrementInstructionExpectations = new ArrayList<>();
			instructionExpectations = new ArrayList<>();
		}
		
		public void expectJumpInstr(int opcode, Label label){
			jumpExpectations.add(new Expectations(opcode, label));
		}
		public void expectIncreInstr(int var, int increment){
			incrementInstructionExpectations.add(new Expectations(var, Integer.valueOf(increment)));
		}
		public void expectLineNumber(int lineNumber, Label label){
			lineNumberExpectations.add(new Expectations(lineNumber, label));
		}
		public void expectInsn(int lineNumber){
			instructionExpectations.add(new Expectations(lineNumber, null));
		}
		
		@Override
		public void visitInsn(int opcode) {
			Expectations expected = new Expectations(opcode, null);
			if(!instructionExpectations.contains(expected)){
				throw new IllegalStateException("unexpected instruction "+ expected);
			}
			instructionExpectations.remove(expected);

			super.visitInsn(opcode);
		}

		@Override
		public void visitIincInsn(int var, int increment) {
			Expectations expected = new Expectations(var, Integer.valueOf(increment));
			if(!incrementInstructionExpectations.contains(expected)){
				throw new IllegalStateException("unexpected increment instruction "+ expected);
			}
			incrementInstructionExpectations.remove(expected);

			super.visitIincInsn(var, increment);
		}

		@Override
		public void visitLineNumber(int lineNumber, Label label) {
			Expectations expected = new Expectations(lineNumber, label);
			if(!lineNumberExpectations.contains(expected)){
				throw new IllegalStateException("unexpected line number instruction "+ expected);
			}
			lineNumberExpectations.remove(expected);
			super.visitLineNumber(lineNumber, label);
		}

		@Override
		public void visitJumpInsn(int opcode, Label label) {
			Expectations expected = new Expectations(opcode, label);
			if(!jumpExpectations.contains(expected)){
				throw new IllegalStateException("unexpected jump instruction "+ expected);
			}
			jumpExpectations.remove(expected);
			super.visitJumpInsn(opcode, label);
		}

		public void verifyAllMethodsCalled(){
			if(!jumpExpectations.isEmpty()){
				throw new AssertionError("not called : "+ jumpExpectations);
			}
			if(!lineNumberExpectations.isEmpty()){
				throw new AssertionError("not called : "+ lineNumberExpectations);
			}
			if(!incrementInstructionExpectations.isEmpty()){
				throw new AssertionError("not called : "+ incrementInstructionExpectations);
			}
			if(!instructionExpectations.isEmpty()){
				throw new AssertionError("not called : "+ instructionExpectations);
			}
		}


		private static class Expectations{
			private final int opcode;
			private final Object label;
			@Override
			public int hashCode() {
				final int prime = 31;
				int result = 1;
				result = prime * result
						+ ((label == null) ? 0 : label.hashCode());
				result = prime * result + opcode;
				return result;
			}
			@Override
			public boolean equals(Object obj) {
				if (this == obj)
					return true;
				if (obj == null)
					return false;
				if (getClass() != obj.getClass())
					return false;
				Expectations other = (Expectations) obj;
				if (label == null) {
					if (other.label != null)
						return false;
				} else if (!label.equals(other.label))
					return false;
				if (opcode != other.opcode)
					return false;
				return true;
			}
			public Expectations(int opcode, Object label) {
				super();
				this.opcode = opcode;
				this.label = label;
			}
			@Override
			public String toString() {
				return "Expectations [opcode=" + opcode + ", label=" + label
						+ "]";
			}
			
			
		}
	}
}
