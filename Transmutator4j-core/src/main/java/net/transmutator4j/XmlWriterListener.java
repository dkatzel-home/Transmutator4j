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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;


import net.transmutator4j.util.TransmutatorUtil;
/**
 * {@code XmlWriterListener} is a {@link MutationTestListener}
 * that writes all the mutation results in XML format
 *  to the given output file.
 *  
 *  @author dkatzel-home
 */
public class XmlWriterListener implements MutationTestListener {

	private final PrintWriter pw;
	private Integer numTotalTests = null;
	private boolean writtenHeader=false;
	/**
	 * Create a new Listener object that will write
	 * the test results to the given output file.
	 * @param xmlFile the file to write the results to;
	 * may not be null.  This file
	 * will be created if it does not already exist.  If this file
	 * already exists, it will be overwritten.
	 * @throws IOException if there is a problem creating the output file.
	 * @throws NullPointerException if xmlFile is null.
	 */
	public XmlWriterListener(File xmlFile) throws IOException{
		createDirIfNeeded(xmlFile.getParentFile());
		OutputStream out = new BufferedOutputStream( new FileOutputStream(xmlFile));
		
		pw = new PrintWriter(out,true);		
	}
	
	private void createDirIfNeeded(File dir) throws IOException{
		if(dir!=null && !dir.exists()){
			if(dir.mkdirs()){
				throw new IOException("error creating directories for output file");
			}
		}
	}
	
	
	
	@Override
	public void testInfo(int numberOfTestsRun, long runtime) {
		this.numTotalTests = numberOfTestsRun;
		
	}

	private void writeHeaderIfNeeded(){
		if(!writtenHeader){
			pw.append(String.format("<?xml version=\"1.0\" encoding=\"UTF-8\"?>%n"));
			pw.append(String.format("<transmutator4j num_tests =\"%d\">%n",
					numTotalTests==null? 0 :numTotalTests ));
			writtenHeader = true;
		}
	}
	@Override
	public void mutationResult(MutationTestResult result) {
		writeHeaderIfNeeded();
		pw.append(String.format(
				"\t<mutation class =\"%s\" line=\"%d\" descr=\"%s\">%s</mutation>%n", 
				result.getMutatedClassname(),
				result.getMutatedLine(),
				TransmutatorUtil.xmlEncode(result.getMutation().description()),
				result.testsStillPassed() ?"Passed": "Failed"));

	}
	@Override
	public void close() throws IOException {
		writeHeaderIfNeeded();
		pw.append(String.format("</transmutator4j>%n"));
		pw.close();
		
	}

	
}
