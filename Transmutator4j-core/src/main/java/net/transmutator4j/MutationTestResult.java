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

package net.transmutator4j;

import java.io.Serializable;

import net.transmutator4j.mutations.Mutation;
/**
 * {@code MutationTestResult} is an interface
 * explaining the results of a given {@link Mutation}.
 *
 * 
 * @author dkatzel-home
 *
 */
public interface MutationTestResult extends Serializable{
	/**
	 * Did the all the tests tested still pass even
	 * after the code was mutated.  Remember
	 * that with mutation testing the tests still
	 * passing is a bad thing!
	 * @return {@code true} if all the tests still passed;
	 * {@code false} if at least one test failed.
	 */
	boolean testsStillPassed();
	/**
	 * Get the qualified class name of the class that was mutated.
	 * @return a String; never null.
	 */
	String getMutatedClassname();
	/**
	 * Get the line that was mutated.
	 * @return a number will always be >=1.
	 */
	int getMutatedLine();
	/**
	 * Get the {@link Mutation} made to the class
	 * on the given line number.
	 * @return a {@link Mutation} instance;
	 * will never be null.
	 */
	Mutation getMutation();

}