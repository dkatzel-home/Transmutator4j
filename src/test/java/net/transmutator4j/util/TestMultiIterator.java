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


import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.Test;
import static org.junit.Assert.*;
public class TestMultiIterator {

	List<Integer> list1 = Arrays.asList(1,2,3,4,5);
	List<Integer> list2 = Arrays.asList(6,7,8,9);
	@Test
	public void oneIterator(){
		Iterator<Integer> sut = 
		 MultiIterator.create(list1.iterator());
		
		Iterator<Integer> expected = list1.iterator();
		while(expected.hasNext()){
			assertEquals(sut.next(), expected.next());
		}
		assertFalse(sut.hasNext());
	}
	@Test(expected = UnsupportedOperationException.class)
	public void removeThrowsUnsupportedOperationException(){
		Iterator<Integer> sut = MultiIterator.create(Collections.<Integer>emptyList().iterator());
		sut.remove();
	}
	@Test(expected = NoSuchElementException.class)
	public void nextWhenNoMoreElementsShouldThrowNoSuchElementException(){
		Iterator<Integer> sut = MultiIterator.create(Collections.<Integer>emptyList().iterator());
		sut.next();
	}
	@Test
	public void twoIterators(){
		Iterator<Integer> sut = MultiIterator.create(list1.iterator(), list2.iterator());
		
		Iterator<Integer> expected = list1.iterator();
		while(expected.hasNext()){
			assertEquals(sut.next(), expected.next());
		}
		expected = list2.iterator();
		while(expected.hasNext()){
			assertEquals(sut.next(), expected.next());
		}
		assertFalse(sut.hasNext());
	}
	@Test
	public void emptyIterator(){
		Iterator<Integer> sut = MultiIterator.create(Collections.<Integer>emptyList().iterator());
		assertFalse(sut.hasNext());
	}
}
