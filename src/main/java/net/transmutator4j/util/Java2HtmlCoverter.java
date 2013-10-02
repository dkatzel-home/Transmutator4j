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
package net.transmutator4j.util;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Scanner;

public class Java2HtmlCoverter {

	public static void convertFile(File javaFile, OutputStream out) throws IOException{
		try(Scanner scanner = new Scanner(javaFile);){
			while(scanner.hasNextLine()){
				String line = scanner.nextLine();
				out.write(TransmutatorUtil.xmlEncode(line).getBytes());
				out.write("<br/>".getBytes());
				out.flush();
			}
		}
	}
}
