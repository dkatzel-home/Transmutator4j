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

import net.transmutator4j.mutator.TestMutatorUtil;

import org.junit.Test;
import static org.junit.Assert.*;
public class TestConditionalJumpMutation {

	@Test
	public void getMutationFor(){
		for(Mutation jumpMutation : ConditionalJumpMutation.values()){
			assertEquals(jumpMutation.toString(),jumpMutation, ConditionalJumpMutation.getMutationsFor(jumpMutation.getOriginalOpCode()));
		}
	}
	@Test
	public void mutatedTwiceShouldReturnOriginal(){
		for(Mutation jumpMutation : ConditionalJumpMutation.values()){
			ConditionalJumpMutation mutation = ConditionalJumpMutation.getMutationsFor(jumpMutation.getMutatedOpCode());
			assertEquals(jumpMutation.toString(),jumpMutation, ConditionalJumpMutation.getMutationsFor(mutation.getMutatedOpCode()));
		}
	}
	@Test
	public void unknownOpcodeShouldReturnNull(){
		assertNull(ConditionalJumpMutation.getMutationsFor(TestMutatorUtil.UNUSED_OPCODE));
	}
}
