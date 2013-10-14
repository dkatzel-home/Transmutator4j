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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
/**
 * {@code MultiIterator} wraps many {@link Iterator}s
 * behind a single {@link Iterator} instance. When the first 
 * iterator no longer has any elements left, the next iterator is
 * started.
 * @author dkatzel-home
 *
 * @param <T> the type being iterated over.
 */
public class MultiIterator<T> implements Iterator<T>{

	private final Iterator<T> endOfIterators= new EndIterator<T>();
	private Iterator<T> currentIterator;
	private final Deque<Iterator<T>> iterators;
	
	/**
	 * Create a new {@link Iterator} instance
	 * which wraps the given iterators.
	 * @param iterators the iterators to wrap behind
	 * a single {@link Iterator} instance.
	 * @return a new {@link Iterator} of type T; never null.
	 * @throws NullPointerException if any iterators are null.
	 */
	public static <T> Iterator<T> create(Collection<Iterator<T>> iterators){
		return new MultiIterator<T>(iterators);
	}
	/**
	 * Create a new {@link Iterator} instance
	 * which wraps the given iterators.
	 * @param iterators the iterators to wrap behind
	 * a single {@link Iterator} instance.
	 * @return a new {@link Iterator} of type T; never null.
	 * @throws NullPointerException if any iterators are null.
	 */
	@SafeVarargs
	public static <T> Iterator<T> create(Iterator<T>...iterators){
		List<Iterator<T>> list = new ArrayList<Iterator<T>>();
		for(Iterator<T> iter : iterators){
			list.add(iter);
		}
		return create(list);
	}

	/**
	 * Construct a MultiIterator..
	 * @param iterators list of iterators to wrap.  Iteration order
	 * is the order in the list.
	 * @throws NullPointerException if iterators is null.
	 */
	private MultiIterator(Collection<Iterator<T>> iterators ){
		this.iterators = new ArrayDeque<Iterator<T>>(iterators);
		currentIterator = this.iterators.pop();
	}
	
	private void updateToNextIterator(){
		while(!currentIterator.hasNext() && !iterators.isEmpty()){
			currentIterator= iterators.pop();
		}
		if(!currentIterator.hasNext()){
			currentIterator=endOfIterators;
		}
	}
	@Override
	public boolean hasNext() {
		if(currentIterator == endOfIterators){
			return false;
		}
		boolean currentHasNext = currentIterator.hasNext();
		if(currentHasNext){
			return true;
		}
		updateToNextIterator();
		
		return hasNext();
	}
	@Override
	public T next() {
		if(hasNext()){
			return currentIterator.next();
		}
		throw new NoSuchElementException();
	}
	@Override
	public void remove() {
		throw new UnsupportedOperationException();
		
	}
	
	/**
	 * {@code EndIterator} is a "poison pill"
	 * to let MultiIterator know that there are no more
	 * iterators left.
	 * @author dkatzel-home
	 *
	 * @param <T>  the type being iterated over.
	 */
	private static final class EndIterator<T> implements Iterator<T>{

		/**
		 * Never has next.
		 * @return false. 
		 */
		@Override
		public boolean hasNext() {
			return false;
		}
		/**
		 * Always throws {@link NoSuchElementException}.
		 * @throws NoSuchElementException always.
		 */
		@Override
		public T next() {
			throw new NoSuchElementException("EndIterator never has any elements");
		}
		/**
		 * Does nothing since there is nothing to remove.
		 */
		@Override
		public void remove() {
			//no-op
		}
		
	}
}
