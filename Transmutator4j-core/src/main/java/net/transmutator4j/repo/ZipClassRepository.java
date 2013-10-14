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
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
/**
 * {@code ZipClassRepository} is an implementation of ClassRepository
 * that can fetch classes from inside a ZIP or JAR file.
 * @author dkatzel-home
 *
 */
public class ZipClassRepository implements ClassRepository{
	private final ZipFile zipFile;
	
	/**
	 * Wraps the given {@link ZipFile} in a ZipClassRepository.
	 * @param zipFile the zip file to wrap.
	 * @throws IOException 
	 * @throws  
	 */
	public ZipClassRepository(File zipFile) throws IOException{
		this(new ZipFile(zipFile));
	}
	/**
	 * Wraps the given {@link ZipFile} in a ZipClassRepository.
	 * @param zipFile the zip file to wrap.
	 */
	public ZipClassRepository(ZipFile zipFile){
		this.zipFile = zipFile;
	}

	@Override
	public InputStream getClassAsStream(String qualifiedClassName) throws IOException {
		ZipEntry entry =zipFile.getEntry(qualifiedClassName.replace('.', '/')+".class");
		if(entry ==null){
			return null;
		}
		return zipFile.getInputStream(entry);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ZipClassRepository [zipFile=" + zipFile.getName() + "]";
	}

	/* (non-Javadoc)
	 * @see net.transmutator4j.repository.ClassRepository#getClassNames()
	 */
	@Override
	public Iterator<String> getQualifiedClassNames() {
		return new ZipEntryEnumerationIteratorAdapter(zipFile.entries());
	}
	/**
	 * {@code ZipEntryEnumerationIteratorAdapter} is an {@link Iterator}
	 * implementation that wraps a ZipFile's {@link Enumeration}
	 * instance.
	 * @author dkatzel-home
	 *
	 */
	private static final class ZipEntryEnumerationIteratorAdapter implements Iterator<String>{
		private Enumeration<? extends ZipEntry> enumeration;
		/**
		 * The Object to be returned by the {@link #next()}.
		 */
		private Object nextObject;
		/**
		 * This object represents the end of iteration.  We will know
		 * we got to the end when this is the nextObject.
		 */
		private final Object end = new Object();
		
		ZipEntryEnumerationIteratorAdapter(Enumeration<? extends ZipEntry> enumeration){
			this.enumeration = enumeration;
			getNextObject();
		}
		
		@Override
		public boolean hasNext() {
			return nextObject !=end;
		}
		private void getNextObject(){
			boolean done=false;
			while(!done && enumeration.hasMoreElements()){
				ZipEntry entry = enumeration.nextElement();
				if(entry.getName().endsWith(".class")){
					nextObject= convertToQualitiedName(entry.getName());
					done =true;					
				}
			}
			if(!done){
				nextObject =end;
			}
		}
		
		private String convertToQualitiedName(String path){
			return path.replace(".class","")
			.replace('/', '.');
		}
		@Override
		public String next() {
			if(hasNext()){
				String result = (String)nextObject;
				getNextObject();
				return result;
			}
			throw new NoSuchElementException();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
			
		}
		
	}

	@Override
	public Iterator<String> iterator() {
		return getQualifiedClassNames();
	}

}
