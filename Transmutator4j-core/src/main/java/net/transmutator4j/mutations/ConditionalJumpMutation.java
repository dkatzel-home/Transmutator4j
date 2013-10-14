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
 * {@code ConditionalJumpMutation} changes changes the condition
 * in a conditional jump to its negative condition.
 * @author dkatzel-home
 *
 */
public enum ConditionalJumpMutation implements Mutation{
	
	//because of the way assembly jumps work
	//the actual opcode is actually the 
	//opposite of what the user types
	//this is why the opcodes are the opposite
	//of the description
	IFEQ_TO_IFNE(Opcodes.IFEQ, Opcodes.IFNE, "constant != becomes =="),
	IFNE_TO_IFEQ(Opcodes.IFNE, Opcodes.IFEQ, "constant == becomes !="),
	IF_ACMPEQ_TO_IF_ACMPNE(Opcodes.IF_ACMPEQ, Opcodes.IF_ACMPNE, "Object != becomes =="),
	IF_ACMPNE_TO_IF_ACMPEQ(Opcodes.IF_ACMPNE, Opcodes.IF_ACMPEQ, "Object == becomes !="),
	
	IF_ICMPEQ_TO_IF_ICMPNE(Opcodes.IF_ICMPEQ, Opcodes.IF_ICMPNE, "primitive != becomes =="),
	IF_ICMPNE_TO_IF_ICMPEQ(Opcodes.IF_ICMPNE, Opcodes.IF_ICMPEQ, "primitive == becomes !="),
	

	IFNULL_TO_IFNONNULL(Opcodes.IFNULL, Opcodes.IFNONNULL, "Object !=null becomes ==null"),
	IFNONNULL_TO_IFNULL(Opcodes.IFNONNULL, Opcodes.IFNULL, "Object ==null becomes !=null"),

	IFLT_TO_IFGE(Opcodes.IFLT, Opcodes.IFGE, "constant >= becomes <"),
	IFGE_TO_IFLT(Opcodes.IFGE, Opcodes.IFLT, "constant < becomes >="),
	

	IF_ICMPLT_TO_IF_ICMPGE(Opcodes.IF_ICMPLT, Opcodes.IF_ICMPGE, "primitive >= becomes <"),
	IF_ICMPGE_TO_IF_ICMPLT(Opcodes.IF_ICMPGE, Opcodes.IF_ICMPLT, "primitive < becomes >="),
	
	IF_ICMPGT_TO_IF_ICMPLE(Opcodes.IF_ICMPGT, Opcodes.IF_ICMPLE, "primitive <= becomes >"),
	IF_ICMPLE_TO_IF_ICMPGT(Opcodes.IF_ICMPLE, Opcodes.IF_ICMPGT, "primitive > becomes <="),
	
	IFGT_TO_IFLE(Opcodes.IFGT, Opcodes.IFLE, "constant <= becomes >"),
	IFLE_TO_IFGT(Opcodes.IFLE, Opcodes.IFGT, "constant > becomes <="),
	;
	
	private int originalOpCode, mutatedOpCode;
	private final String desc;
	private static final Map<Integer, ConditionalJumpMutation> POSSIBLE_MUTATIONS;
	static{
		POSSIBLE_MUTATIONS = new HashMap<Integer, ConditionalJumpMutation>();
		for(ConditionalJumpMutation mutation : values()){
			Integer opCode = Integer.valueOf(mutation.getOriginalOpCode());
			//if(opCode != 186){
			
				POSSIBLE_MUTATIONS.put(opCode,mutation);
			//}
		}
	}
	private ConditionalJumpMutation(int originalOpCode, int mutatedOpCode, String desc){
		this.originalOpCode = originalOpCode;
		this.mutatedOpCode = mutatedOpCode;
		this.desc = desc;
	}
	@Override
	public String description() {
		return desc;
	}
	@Override
	public int getMutatedOpCode() {
		return mutatedOpCode;
	}
	@Override
	public int getOriginalOpCode() {
		return originalOpCode;
	}
	public static ConditionalJumpMutation getMutationsFor(int opcode){
		return  POSSIBLE_MUTATIONS.get(Integer.valueOf(opcode));
		
	}
	
	
	
}
