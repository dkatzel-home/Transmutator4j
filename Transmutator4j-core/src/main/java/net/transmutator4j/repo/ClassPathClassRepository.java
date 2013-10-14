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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.transmutator4j.util.MultiIterator;

/**
 * {@code ClassPathClassRepository} is a ClassRepository implementation
 * for all the class files the classpath.
 * @author dkatzel-home
 *
 */
public class ClassPathClassRepository implements ClassRepository {

	List<ClassRepository> classRepositories = new ArrayList<ClassRepository>();
	/**
	 * Convenience constructor for creating a ClassPathClassRepository
	 * using the current Class path as defined by the Java
	 * System property {@code java.class.path}
	 * @throws IOException
	 */
	public ClassPathClassRepository() throws IOException{
		this(System.getProperty("java.class.path"));
	}
	/**
	 * Create a new ClassPathClassRepository using the given classpath.
	 * @param classPath a string representing the class path
	 * @throws IOException if there is a problem getting any classes from the 
	 * classpath.
	 */
	public ClassPathClassRepository(String classPath) throws IOException{
		for(String path : classPath.split(File.pathSeparator)){
			File file = new File(path);
			if(isZipOrJar(file)){
				addJarAsRepository(file);
			}
			else{
				classRepositories.add(new ClassDirectoryClassRepository(file));
				rescursivelyAddJars(file);
			}
		}
	}
	private boolean isZipOrJar(File file) {
		String name = file.getName();
		return name.endsWith(".jar") || name.endsWith(".zip");
	}
	private void rescursivelyAddJars(File dir) throws IOException {
		for(File subFile : dir.listFiles()){
			if(isZipOrJar(subFile)){
				addJarAsRepository(subFile);
			}
			if(subFile.isDirectory()){
				rescursivelyAddJars(subFile);
			}
		}
	}
	private void addJarAsRepository(File jarFile) throws IOException{
		classRepositories.add(new ZipClassRepository(jarFile));
	}
	
	
	@Override
	public InputStream getClassAsStream(String qualifiedClassName) throws IOException {
		for(ClassRepository repository : classRepositories){
			InputStream in =repository.getClassAsStream(qualifiedClassName);
			if(in !=null){
				return in;
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ClassPathClassRepository [classRepositories="
				+ classRepositories + "]";
	}
	
	@Override
	public Iterator<String> getQualifiedClassNames() {
		List<Iterator<String>> iterators = new ArrayList<Iterator<String>>();
		for(ClassRepository repo : classRepositories){
			iterators.add(repo.getQualifiedClassNames());
		}
		return MultiIterator.create(iterators);
	}
	@Override
	public Iterator<String> iterator() {
		return getQualifiedClassNames();
	}
	
	
}
