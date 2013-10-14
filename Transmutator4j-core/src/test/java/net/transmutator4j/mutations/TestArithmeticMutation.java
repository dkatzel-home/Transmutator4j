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
package net.transmutator4j.mutations;

import static org.junit.Assert.*;

import java.util.EnumSet;

import net.transmutator4j.mutator.TestMutatorUtil;

import org.junit.Test;
import static net.transmutator4j.mutations.ArithmeticMutation.*;
public class TestArithmeticMutation {
	@Test
	public void getMutationFor(){
		for(Mutation arithmeticMutation : ArithmeticMutation.values()){
			assertEquals(arithmeticMutation.toString(),arithmeticMutation, ArithmeticMutation.getMutationFor(arithmeticMutation.getOriginalOpCode()));
		}
	}
	@Test
	public void nonModulusMuationsMutatedTwiceShouldReturnOriginal(){
		EnumSet<ArithmeticMutation> modulusMutations = EnumSet.of(INTEGER_MOD,LONG_MOD,DOUBLE_MOD, FLOAT_MOD);
		for(Mutation arithmeticMutation : EnumSet.complementOf(modulusMutations)){
			ArithmeticMutation mutation = ArithmeticMutation.getMutationFor(arithmeticMutation.getMutatedOpCode());
			assertEquals(arithmeticMutation.toString(),arithmeticMutation, ArithmeticMutation.getMutationFor(mutation.getMutatedOpCode()));
		}
		assertEquals(INTEGER_MULT, ArithmeticMutation.getMutationFor(INTEGER_MOD.getMutatedOpCode()));
		assertEquals(DOUBLE_MULT, ArithmeticMutation.getMutationFor(DOUBLE_MOD.getMutatedOpCode()));
		assertEquals(FLOAT_MULT, ArithmeticMutation.getMutationFor(FLOAT_MOD.getMutatedOpCode()));
		assertEquals(LONG_MULT, ArithmeticMutation.getMutationFor(LONG_MOD.getMutatedOpCode()));
	}
	@Test
	public void mutationOfModulusIsMultiplication(){
		assertEquals(INTEGER_MULT, ArithmeticMutation.getMutationFor(INTEGER_MOD.getMutatedOpCode()));
		assertEquals(DOUBLE_MULT, ArithmeticMutation.getMutationFor(DOUBLE_MOD.getMutatedOpCode()));
		assertEquals(FLOAT_MULT, ArithmeticMutation.getMutationFor(FLOAT_MOD.getMutatedOpCode()));
		assertEquals(LONG_MULT, ArithmeticMutation.getMutationFor(LONG_MOD.getMutatedOpCode()));
	
	}
	@Test(expected = IllegalArgumentException.class)
	public void unknownOpcodeShouldThrowIllegalArgumentException(){
		ArithmeticMutation.getMutationFor(TestMutatorUtil.UNUSED_OPCODE);
	}

}
