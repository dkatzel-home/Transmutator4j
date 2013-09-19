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

import org.junit.Test;
import static org.junit.Assert.*;
public class TestTransmorgifyUtil {

	@Test
	public void xmlEncodeEmptyStringShouldDoNothing(){
		String emptyString = "";
		assertEquals(emptyString, TransmutatorUtil.xmlEncode(emptyString));
	}
	@Test
	public void xmlEncodeNoSpecialCharactersShouldDoNothing(){
		String string = "blah blah 123";
		assertEquals(string, TransmutatorUtil.xmlEncode(string));
	}
	
	@Test
	public void xmlEncodeGreaterThan(){
		assertEquals("x &#62; 54", TransmutatorUtil.xmlEncode("x > 54"));
	}
	@Test
	public void xmlEncodeLessThan(){
		assertEquals("x &#60; 54", TransmutatorUtil.xmlEncode("x < 54"));
	}
	@Test
	public void xmlEncodeAmpersand(){
		assertEquals("you &#38; me", TransmutatorUtil.xmlEncode("you & me"));
	}
}
