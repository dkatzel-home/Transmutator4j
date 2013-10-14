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
package net.transmutator4j.repo;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
/**
 * {@code ClassRepository} abstracts how a compiled class file
 * is stored.
 * @author dkatzel-home
 *
 */
public interface ClassRepository extends Iterable<String>{
	/**
	 * Get an iterator of all the qualified class names
	 * inside this repository.
	 * @return iterator of qualified class names.
	 */
	Iterator<String> getQualifiedClassNames();
	/**
	 * Get the InputStream of the compiled bytes for the
	 * given qualified class.
	 * @param qualifiedClassName qualified class name as String.
	 * @return an inputStream or  null if class does not exist
	 * in repository.
	 * @throws IOException if there is a problem fetching
	 * the inputStream or the qualified class is not in the repository.
	 */
	InputStream getClassAsStream(String qualifiedClassName) throws IOException;
	
}
