package net.transmutator4j.repo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


import org.junit.Test;

public abstract class AbstractTestRepository {
	
	

	protected abstract ClassRepository getSut();
	
	@Test
	public void unknownClassAsStreamShouldReturnNull() throws IOException{
		assertNull(getSut().getClassAsStream("does.not.exist"));
	}
	
	@Test
	public void getClassAsStream() throws IOException{
		String classname  = "example.bowling.Game";
		try(	InputStream actual = new BufferedInputStream(getSut().getClassAsStream(classname));
				InputStream expected = new BufferedInputStream(getExpected(classname)); ){
			int expectedByte = 0;
			while(expectedByte != -1){
				expectedByte = expected.read();
				int actualByte = actual.read();
				assertEquals(expectedByte, actualByte);
				
			}
		}
	}
	
	@Test
	public void iterator(){		
		assertEquals( getExpectedNames(), getActualNames());
	}
	protected abstract List<String> getExpectedNames();
	
	private List<String> getActualNames() {
		List<String> actualNames = new ArrayList<>();
		for(String actualName : getSut()){
			actualNames.add(actualName);
		}
		return actualNames;
	}
	
	protected abstract InputStream getExpected(String qualifiedClassName) throws IOException;
}
