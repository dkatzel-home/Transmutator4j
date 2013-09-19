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

import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.Opcodes;
/**
 * Mutates a boolean value {@code true} into
 * {@code false} and vice versa.  Unfortunately,
 * boolean constants are the same as integer {@code 1}
 * and integer {@code 0}, so those will be mutated as well.
 * @author dkatzel-home
 *
 */
public enum BooleanMutation implements Mutation {
	/**
	 * Mutates boolean false into boolean true
	 * or Integer 0 into Integer 1.
	 */
	MUTATED_FALSE(Opcodes.ICONST_0),
	/**
	 * Mutates boolean true into boolean false
	 * or Integer 1 into Integer 0.
	 */
	MUTATED_TRUE(Opcodes.ICONST_1);
	
	
	private static final Map<Integer, BooleanMutation> MAP = new HashMap<Integer, BooleanMutation>();
	static{
		MAP.put(Integer.valueOf(Opcodes.ICONST_0), MUTATED_FALSE);
		MAP.put(Integer.valueOf(Opcodes.ICONST_1), MUTATED_TRUE);
	}
	private final int originalOpcode, mutatedOpcode;
	
	/**
	 * Get the {@link BooleanMutation} instance
	 * that can mutate the given opcode.
	 * @param opcode the opcode to mutate.
	 * @return a {@link BooleanMutation} instance; never null.
	 * @throws IllegalArgumentException if opcode can not be mutated as defined
	 * by {@link #isMutatable(int)}.
	 * @see #isMutatable(int)
	 */
	public static BooleanMutation getMutationFor(int opcode){
		Integer key = Integer.valueOf(opcode);
		if(MAP.containsKey(key)){
			return MAP.get(opcode);
		}
		throw new IllegalArgumentException("can not mutate opcode "+ opcode);
	}
	/**
	 * Is the given opcode mutatable by {@link BooleanMutation}.
	 * @param opcode the opcode to be mutated.
	 * @return {@code true} if {@link BooleanMutation} can mutate
	 * this opcode; {@code false} otherwise.
	 */
	public static boolean isMutatable(int opcode){
		return MAP.containsKey(opcode);
	}
	private BooleanMutation(int originalOpcode){
		this.originalOpcode = originalOpcode;
		this.mutatedOpcode = getOppositeConstantOf(originalOpcode);
	}
	
	private int getOppositeConstantOf(int opcode){
		if(opcode == Opcodes.ICONST_0){
			return Opcodes.ICONST_1;
		}
		return Opcodes.ICONST_0;
		
	}
	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public String description() {
		return String.format("changed constant %s to %s", 
				getConstantFor(originalOpcode),
				getConstantFor(mutatedOpcode)
				);
	}

	private String getConstantFor(int opcode){
		if(opcode == Opcodes.ICONST_0){
			return "0 (or false)";
		}
		return "1 (or true)";
	}
	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public int getMutatedOpCode() {
		return mutatedOpcode;
	}
	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public int getOriginalOpCode() {
		return originalOpcode;
	}

}
