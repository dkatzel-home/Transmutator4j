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

import net.transmutator4j.mutations.Mutation;
import net.transmutator4j.util.TransmutatorUtil;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;

public class MutateClassAdapter extends ClassVisitor implements Mutator{
	private final int previousMutationCount;
	private int mutationCount=0;
	private boolean hasMutated = false;
	private String mutatedClassname;
	private Mutation mutation;
	private int mutatedLine;
	private String currentVisitingClassName;
	public MutateClassAdapter(ClassVisitor cv,int previousMutationCount) {
		super(TransmutatorUtil.CURRENT_ASM_VERSION,cv);	
		this.previousMutationCount = previousMutationCount;
	}
	

	/* (non-Javadoc)
	 * @see org.objectweb.asm.ClassAdapter#visit(int, int, java.lang.String, java.lang.String, java.lang.String, java.lang.String[])
	 */
	@Override
	public void visit(int version, int access, String name, String signature,
			String superName, String[] interfaces) {
		currentVisitingClassName = name;
		super.visit(version, access, name, signature, superName, interfaces);
	}


	/* (non-Javadoc)
	 * @see org.objectweb.asm.ClassAdapter#visitMethod(int, java.lang.String, java.lang.String, java.lang.String, java.lang.String[])
	 */
	@Override
	public MethodVisitor visitMethod(int access, String name, String desc,
			String signature, String[] exceptions) {
		MethodVisitor mv= super.visitMethod(access, name, desc, signature, exceptions);
			
		if(!hasMutated() && mv !=null &&
				allowedToMutateMethod(access, name, desc,signature, exceptions)
		){
			return new MutateMethodAdapter(mv,this);
		}
		return mv;
	}

	protected boolean allowedToMutateMethod(int access, String name, String desc,
			String signature, String[] exceptions){
		return !equalsMethod(access,name,desc,signature,exceptions)
				&& !hashCodeMethod(access, name, desc, signature, exceptions)
				&& !toStringMethod(access, name, desc, signature, exceptions);
	}

	
	private boolean equalsMethod(int access, String name, String desc,
			String signature, String[] exceptions) {
		if("equals".equals(name) && (access & Type.ACC_PUBLIC) !=0
				&& "(Ljava/lang/Object;)Z".equals(desc)){
			return true;
		}
		return false;
	}
	
	private boolean hashCodeMethod(int access, String name, String desc,
			String signature, String[] exceptions) {
		if("hashCode".equals(name) && (access & Type.ACC_PUBLIC) !=0
				&& "()I".equals(desc)){
			return true;
		}
		return false;
	}
	
	private boolean toStringMethod(int access, String name, String desc,
			String signature, String[] exceptions) {
		if("toString".equals(name) && (access & Type.ACC_PUBLIC) !=0
				&& "()Ljava/lang/String;".equals(desc)){
			return true;
		}
		return false;
	}


	public synchronized boolean hasMutated(){
		return hasMutated;
	}

	@Override
	public synchronized boolean shouldMutate() {
		mutationCount++;
		boolean should= mutationCount == previousMutationCount+1;
		return !hasMutated() && should;
	}


	@Override
	public synchronized void mutate(int lineNumber,Mutation mutation) {
		if(hasMutated()){
			throw new IllegalStateException("already mutated!");
		}
		this.mutatedClassname = currentVisitingClassName.replace('/', '.');
		this.mutation = mutation;
		this.mutatedLine = lineNumber;
		this.hasMutated=true;
		System.out.printf("%d: %s%n",lineNumber, mutation.description());
	}


	/**
	 * @return the mutatedLine
	 */
	public int getMutatedLine() {
		return mutatedLine;
	}


	/**
	 * @return the mutatedClassname
	 */
	public String getMutatedClassname() {
		return mutatedClassname;
	}


	/**
	 * @return the mutation
	 */
	public Mutation getMutation() {
		return mutation;
	}

}
