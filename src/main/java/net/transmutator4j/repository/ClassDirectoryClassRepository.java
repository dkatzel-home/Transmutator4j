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
package net.transmutator4j.repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/**
 * {@code ClassDirectoryClassRepository} is a Class Repository
 * for compiled classes in a directory (for example a "bin" directory.
 * This implementation will include all classes in the directory and all
 * sub directories.
 * @author dkatzel-home
 *
 */
public class ClassDirectoryClassRepository implements ClassRepository {

	private final File rootDir;
	/**
	 * Creates a ClassDirectoryClassRepository starting
	 * at the given root directory.
	 * @param rootDir the directory where the .class files
	 * are located.
	 * @throws IllegalArgumentException if rootDir is not a 
	 * directory.
	 * @throws NullPointerException if rootDir is {@code null}.
	 */
	public ClassDirectoryClassRepository(File rootDir){
		if(!rootDir.isDirectory()){
			throw new IllegalArgumentException("rootDir must be a dir");
		}
		this.rootDir =rootDir;
	}
	@Override
	public InputStream getClassAsStream(String qualifiedClassName) throws IOException{
		StringBuilder builder= new StringBuilder(qualifiedClassName.length() + 6);
		builder.append(qualifiedClassName.replace('.', File.separatorChar))
		.append(".class");
		return getTranslatedClassNameAsStream(builder.toString());
	}

	private InputStream getTranslatedClassNameAsStream(String translatedName) throws IOException {
			File file = new File(rootDir.getAbsolutePath() + File.separatorChar+translatedName);
			if(file.exists()){
				return new FileInputStream(file);
			}
			return null;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ClassDirectoryClassRepository [rootDir=" + rootDir + "]";
	}
	/* (non-Javadoc)
	 * @see net.transmutator4j.repository.ClassRepository#getClassNames()
	 */
	@Override
	public Iterator<String> getQualifiedClassNames() {
		return getClassFilesInDirectory(rootDir).iterator();
	}
	
	private List<String> getClassFilesInDirectory(File dir){
		List<String> list = new ArrayList<String>();
		for(File classFile : dir.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				return dir.isDirectory() || name.endsWith(".class");
			}
		})){
			if(!classFile.isDirectory()){
				list.add(classFile.getAbsolutePath()
						.replaceAll("^"+escapeForRegularExpression(rootDir.getAbsolutePath()), "")
						.replaceAll(".class$", "")
						.replace(File.separatorChar, '.')
						.substring(1));
			}else{
				list.addAll(getClassFilesInDirectory(classFile));
			}
			
		}
		return list;
	}
	@Override
	public Iterator<String> iterator() {
		return getQualifiedClassNames();
	}

	private String escapeForRegularExpression(String s){
		StringBuilder escaped = new StringBuilder();
		for(int i=0; i<s.length(); i++){
			char c = s.charAt(i);
			if(c =='\\'){
				escaped.append("\\\\");
			}
			else{
			escaped.append(s.charAt(i));
			}
		}
		return escaped.toString();
	}
	
}
