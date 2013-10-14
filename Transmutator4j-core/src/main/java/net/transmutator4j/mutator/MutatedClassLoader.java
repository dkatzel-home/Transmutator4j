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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import net.transmutator4j.repo.ClassRepository;

public class MutatedClassLoader extends ClassLoader {
	private static final List<Pattern> DEFAULT_DEFERRED_PATTERNS = Arrays.asList(
			Pattern.compile("java.*"), 
			Pattern.compile("junit.*"), 
			Pattern.compile("org.junit.*"), 
			Pattern.compile("sun.*"));

	private final List<Pattern> deferredPatterns = new ArrayList<Pattern>(DEFAULT_DEFERRED_PATTERNS);
	ClassLoader parentClassLoader;
	Map<String, Class<?>> classes = new HashMap<String, Class<?>>();

	private final ClassRepository repository;
	
	public MutatedClassLoader(ClassLoader parentClassLoader,
			ClassRepository repository, String mutatedClassName, byte[] compiledBytes)
			throws ClassNotFoundException {
		super(parentClassLoader);
		this.repository = repository;
		Class<?> mutatedClass =defineClass(mutatedClassName, compiledBytes, 0,
				compiledBytes.length);
		classes.put(mutatedClassName, mutatedClass);
	}

	public void addDefferedPattern(Pattern pattern){
		deferredPatterns.add(pattern);
	}
	public void removeDefferedPattern(Pattern pattern){
		deferredPatterns.remove(pattern);
	}
	private boolean matchesDefferedPattern(String name){
		for (Pattern deferredPattern : deferredPatterns) {
			if(deferredPattern.matcher(name).matches()){
				return true;
			}
		}
		return false;
	}
	private Class<?> loadClassFromRepository(String name){
		try {
			InputStream in = repository.getClassAsStream(name);
			if(in ==null){
				return null;
			}
			ByteArrayOutputStream out = new ByteArrayOutputStream();

			byte[] buf = new byte[1024];
			int bytesRead;
			
				while ((bytesRead = in.read(buf)) > 0) {
					out.write(buf, 0, bytesRead);
				}
				byte[] compiledByteArray = out.toByteArray();
				return defineClass(name, compiledByteArray, 0,
						compiledByteArray.length);
			} catch (Exception e) {
				return null;
			}
	}
	private synchronized Class<?> loadClassUsingInvertedHierarchy(String name) throws ClassNotFoundException{
		if(classes.containsKey(name)){
			return classes.get(name);
		}
		if(!matchesDefferedPattern(name)){
			Class<?> c= loadClassFromRepository(name);
			if(c !=null){
				return c;
			}
		}
		return super.loadClass(name, false);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.ClassLoader#loadClass(java.lang.String)
	 */
	@Override
	public synchronized Class<?> loadClass(String name, boolean resolve)
			throws ClassNotFoundException {		
		try {
			Class<?> c = loadClassUsingInvertedHierarchy(name);
			if (resolve) {
				resolveClass(c);
			}
			classes.put(name, c);
			return c;
		} catch (Throwable t) {
			t.printStackTrace();
			throw new RuntimeException(t);
		}

	}

}
