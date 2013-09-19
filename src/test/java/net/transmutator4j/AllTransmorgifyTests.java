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

import net.transmutator4j.mutations.AllMutationsTests;
import net.transmutator4j.mutator.AllMutatorTests;
import net.transmutator4j.util.AllUtilTests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	AllMutationsTests.class,
	AllMutatorTests.class,
	AllUtilTests.class,
	TestTransmorgify.class,
	
})
public class AllTransmorgifyTests {

}
