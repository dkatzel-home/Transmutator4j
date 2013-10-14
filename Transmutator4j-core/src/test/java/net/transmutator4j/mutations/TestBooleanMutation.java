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

import org.junit.Test;
import org.objectweb.asm.Opcodes;
import static org.junit.Assert.*;
public class TestBooleanMutation {

	@Test
	public void isBooleanOpcode(){
		assertTrue(BooleanMutation.isMutatable(Opcodes.ICONST_0));
		assertTrue(BooleanMutation.isMutatable(Opcodes.ICONST_1));
	}
	
	@Test
	public void mutateBooleanFalse(){
		BooleanMutation mutatedFalse = BooleanMutation.getMutationFor(Opcodes.ICONST_0);
		assertEquals(Opcodes.ICONST_0, mutatedFalse.getOriginalOpCode());
		assertEquals(Opcodes.ICONST_1, mutatedFalse.getMutatedOpCode());
		assertEquals("changed constant 0 (or false) to 1 (or true)", mutatedFalse.description());
	}
	@Test
	public void mutateBooleanTrue(){
		BooleanMutation mutatedFalse = BooleanMutation.getMutationFor(Opcodes.ICONST_1);
		assertEquals(Opcodes.ICONST_1, mutatedFalse.getOriginalOpCode());
		assertEquals(Opcodes.ICONST_0, mutatedFalse.getMutatedOpCode());
		assertEquals("changed constant 1 (or true) to 0 (or false)", mutatedFalse.description());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void getMutationForInvalidOpcodeShouldThrowException(){
		BooleanMutation.getMutationFor(-1);
	}
}
