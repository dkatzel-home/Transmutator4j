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
/**
 * Utility class for common Transmorgify 
 * methods and constants.
 * 
 * @author dkatzel-home
 *
 */
public final class TransmorgifyUtil {
	
	private TransmorgifyUtil(){
		//private constructor can not be instantiated.
	}
	/**
	 * Convert given string in XML friendly version.
	 * This method will replace {@code < > and &} into
	 * {@code &#} equivalents.
	 * @param s string to xml encode
	 * @return xml encoded version of s.
	 */
	public static String xmlEncode(String s){
		StringBuilder builder = new StringBuilder();
		for(int i=0; i<s.length(); i++){
			char c = s.charAt(i);
			if(c == '<' || c=='>' || c =='&'){
				builder.append("&#" + (int) c + ";");
			}
			else{
				builder.append(c);
			}
		}
		return builder.toString();
	}
	/**
	 * Version of ASM library used, currently set to {@value}.
	 */
	public static final int CURRENT_ASM_VERSION =4;
	
	public enum EXIT_STATES{
		TESTS_FAILED(0, '.'),
		TESTS_ALL_STILL_PASSED(2,'P'),
		NO_MUTATIONS_MADE(1,' '),
		TIMED_OUT(99, '!')
		;
		
		
		private EXIT_STATES(int exitValue, char charValue) {
			this.exitValue = exitValue;
			this.charValue = charValue;
		}

		private final int exitValue;
		private final char charValue;
		
		public void exitSystem(){
			System.exit(exitValue);
		}

		public char getCharValue() {
			return charValue;
		}

		public int getExitValue() {
			return exitValue;
		}
		
		public static EXIT_STATES getValueFor(int exitCode){
			for(EXIT_STATES s : values()){
				if(s.getExitValue() == exitCode){
					return s;
				}
			}
			throw new IllegalArgumentException("unknown exit code " + exitCode);
		}
		
	}
}
