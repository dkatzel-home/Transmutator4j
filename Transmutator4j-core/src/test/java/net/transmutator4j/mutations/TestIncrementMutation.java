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
public class TestIncrementMutation {

	IncrementMutation positiveMutation = new IncrementMutation(5);
	IncrementMutation negativeMutation = new IncrementMutation(-5);
	@Test
	public void positiveMutation(){
		assertEquals(-5,positiveMutation.getMutatedIncrement());
		assertEquals(Opcodes.IINC,positiveMutation.getOriginalOpCode());
		assertEquals(Opcodes.IINC,positiveMutation.getMutatedOpCode());
		assertEquals("increment of 5 became increment of -5", positiveMutation.description());
		assertFalse(positiveMutation.equals(negativeMutation));
		assertFalse(positiveMutation.hashCode() == negativeMutation.hashCode());
	}
	@Test
	public void negativeMutation(){
		assertEquals(5,negativeMutation.getMutatedIncrement());
		assertEquals(Opcodes.IINC,negativeMutation.getOriginalOpCode());
		assertEquals(Opcodes.IINC,negativeMutation.getMutatedOpCode());
		assertEquals("increment of -5 became increment of 5", negativeMutation.description());
		assertFalse(negativeMutation.equals(positiveMutation));
		assertFalse(negativeMutation.hashCode() == positiveMutation.hashCode());
	}
	
	@Test
	public void notEqualToNull(){
		assertFalse(positiveMutation.equals(null));
	}
	
	@Test
	public void notEqualToNonIncrementMutation(){
		assertFalse(positiveMutation.equals("something completely different"));
	}
}
