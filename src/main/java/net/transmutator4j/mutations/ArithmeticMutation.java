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
 * {@code ArithmeticMutation} is a {@link Mutation} implementation
 * that mutates {@code + - * / and %} into its inverse operation
 * {@code - + / * and *} respectively.
 * @author dkatzel-home
 *
 */
public enum ArithmeticMutation implements Mutation{
	/**
	 * Mutates integer addition into integer subtraction.
	 */
	INTEGER_ADD(Opcodes.IADD, Opcodes.ISUB, "integer + becomes integer -"),
	/**
	 * Mutates long integer addition into long integer subtraction.
	 */
	LONG_ADD(Opcodes.LADD, Opcodes.LSUB, "long + becomes long -"),
	/**
	 * Mutates float addition into float subtraction.
	 */
	FLOAT_ADD(Opcodes.FADD, Opcodes.FSUB, "float + becomes float -"),
	/**
	 * Mutates double addition into double subtraction.
	 */
	DOUBLE_ADD(Opcodes.DADD, Opcodes.DSUB, "double + becomes double -"),
	
	/**
	 * Mutates integer subtraction into integer addition.
	 */
	INTEGER_SUB(Opcodes.ISUB, Opcodes.IADD, "integer - becomes integer +"),
	/**
	 * Mutates long integer subtraction into long integer addition.
	 */
	LONG_SUB(Opcodes.LSUB, Opcodes.LADD, "long - becomes long +"),
	/**
	 * Mutates double subtraction into double addition.
	 */
	DOUBLE_SUB(Opcodes.DSUB, Opcodes.DADD, "double - becomes double +"),
	/**
	 * Mutates float subtraction into float addition.
	 */
	FLOAT_SUB(Opcodes.FSUB, Opcodes.FADD, "float - becomes float +"),
	/**
	 * Mutates integer multiplication into integer division.
	 */
	INTEGER_MULT(Opcodes.IMUL, Opcodes.IDIV, "integer * becomes integer /"),
	/**
	 * Mutates long integer multiplication into long integer division.
	 */
	LONG_MULT(Opcodes.LMUL, Opcodes.LDIV, "long * becomes long /"),
	/**
	 * Mutates float multiplication into float division.
	 */
	FLOAT_MULT(Opcodes.FMUL, Opcodes.FDIV, "float * becomes float /"),
	/**
	 * Mutates double multiplication into double division.
	 */
	DOUBLE_MULT(Opcodes.DMUL, Opcodes.DDIV, "double * becomes double /"),
	/**
	 * Mutates integer division into integer multiplication.
	 */
	INTEGER_DIV(Opcodes.IDIV, Opcodes.IMUL, "integer / becomes integer *"),
	/**
	 * Mutates long integer division into long integer multiplication.
	 */
	LONG_DIV(Opcodes.LDIV, Opcodes.LMUL, "long / becomes long *"),
	/**
	 * Mutates float division into float multiplication.
	 */
	FLOAT_DIV(Opcodes.FDIV, Opcodes.FMUL, "float / becomes float *"),
	/**
	 * Mutates double division into double multiplication.
	 */
	DOUBLE_DIV(Opcodes.DDIV, Opcodes.DMUL, "double / becomes double *"),
	/**
	 * Mutates integer modulus into integer multiplication.
	 */
	INTEGER_MOD(Opcodes.IREM, Opcodes.IMUL, "integer % becomes integer *"),
	/**
	 * Mutates long integer modulus into long integer multiplication.
	 */
	LONG_MOD(Opcodes.LREM, Opcodes.LMUL, "long % becomes long *"),
	/**
	 * Mutates float modulus into float multiplication.
	 */
	FLOAT_MOD(Opcodes.FREM, Opcodes.FMUL, "float % becomes float *"),
	/**
	 * Mutates double modulus into double multiplication.
	 */
	DOUBLE_MOD(Opcodes.DREM, Opcodes.DMUL, "double % becomes double *")	
	;
	
	/**
	 * Map of all the mutations by their original opcode.
	 */
	private static final Map<Integer, ArithmeticMutation> OPCODE_TO_MUTATION_MAP;
	/**
	 * Populate OPCODE_TO_MUTATION_MAP.
	 */
	static{
		OPCODE_TO_MUTATION_MAP = new HashMap<Integer, ArithmeticMutation>();
		for(ArithmeticMutation mutation : values()){
			OPCODE_TO_MUTATION_MAP.put(mutation.getOriginalOpCode(), mutation);
		}
	}
	private final int originalOpcode, mutatedOpcode;
	private final String description;
	
	private ArithmeticMutation(int originalOpcode, int mutatedOpcode, String description){
		this.originalOpcode = originalOpcode;
		this.mutatedOpcode = mutatedOpcode;
		this.description = description;
	}
	
	/**
	 * Is the given opcode mutatable by {@link ArithmeticMutation}.
	 * @param opcode the opcode to be mutated.
	 * @return {@code true} if {@link ArithmeticMutation} can mutate
	 * this opcode; {@code false} otherwise.
	 */
	public static boolean isMutatable(int opcode){
		return OPCODE_TO_MUTATION_MAP.containsKey(Integer.valueOf(opcode));
	}
	/**
	 * Get the {@link ArithmeticMutation} instance
	 * that can mutate the given opcode.
	 * @param opcode the opcode to mutate.
	 * @return a {@link ArithmeticMutation} instance; never null.
	 * @throws IllegalArgumentException if opcode can not be mutated as defined
	 * by {@link #isMutatable(int)}.
	 * @see #isMutatable(int)
	 */
	public static ArithmeticMutation getMutationFor(int opcode){
		Integer key =Integer.valueOf(opcode);
		if(OPCODE_TO_MUTATION_MAP.containsKey(key)){
			return OPCODE_TO_MUTATION_MAP.get(key);
		}
		throw new IllegalArgumentException("can not mutate opcode "+ opcode);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String description() {
		return description;
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
