package net.transmutator4j.integrationTests;
import static org.junit.Assert.*;

import org.junit.Test;
public class TestCounter {

	@Test
	public void startsAtZero(){
		Counter sut = new Counter();
		assertEquals(0, sut.getCount());
	}
	@Test
	public void increment(){
		Counter sut = new Counter();
		sut.increment();
		assertEquals(1, sut.getCount());
	}
}
