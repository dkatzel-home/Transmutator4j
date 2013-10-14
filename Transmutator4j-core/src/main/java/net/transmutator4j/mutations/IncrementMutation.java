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

import org.objectweb.asm.Opcodes;
/**
 * {@code IncrementMutation} is a Mutation implementation
 * that changes increment instructions {@code ++ and +=} into
 * {@code -- and -=} and vice versa.
 * @author dkatzel-home
 *
 */
public final class IncrementMutation implements Mutation {

	private final int mutatedIncrement;
	/**
	 * Construct a new instance of {@link IncrementMutation}
	 * with the given increment.
	 * @param increment the amount to increment.  A negative
	 * value may be provided to create a decrement.
	 */
	public IncrementMutation(int increment){		
		mutatedIncrement = -1 *increment;
	}
	@Override
	public String description() {
		return String.format("increment of %d became increment of %d",
				-1* mutatedIncrement, 
				mutatedIncrement);
	}

	@Override
	public int getMutatedOpCode() {
		return Opcodes.IINC;
	}

	@Override
	public int getOriginalOpCode() {
		return Opcodes.IINC;
	}

	public int getMutatedIncrement(){
		return mutatedIncrement;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + mutatedIncrement;
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IncrementMutation other = (IncrementMutation) obj;
		return mutatedIncrement == other.mutatedIncrement;
			
	}
	
	
}
