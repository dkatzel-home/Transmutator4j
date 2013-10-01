package net.transmutator4j.repository;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.junit.Test;
import static org.junit.Assert.*;
public class TestZipClassRepository {

	private ZipClassRepository sut;
	private ZipFile zipFile;
	
	public TestZipClassRepository() throws IOException{
		File file= new File(TestZipClassRepository.class.getResource("example.zip").getFile());
		sut = new ZipClassRepository(file);
		zipFile = new ZipFile(file);
	}
	@Test
	public void unknownClassAsStreamShouldReturnNull() throws IOException{
		assertNull(sut.getClassAsStream("does.not.exist"));
	}
	
	@Test
	public void getClassAsStream() throws IOException{
		String classname  = "example.bowling.Game";
		try(	InputStream actual = new BufferedInputStream(sut.getClassAsStream(classname));
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
	private List<String> getExpectedNames() {
		List<String> expectedNames = new ArrayList<>();
		Enumeration<? extends ZipEntry> enumeration =zipFile.entries();
		while(enumeration.hasMoreElements()){
			String name =enumeration.nextElement().getName();
			if(name.endsWith(".class")){
				expectedNames.add(name.replace(".class","")
										.replace('/', '.'));
			}
		}
		return expectedNames;
	}
	private List<String> getActualNames() {
		List<String> actualNames = new ArrayList<>();
		for(String actualName : sut){
			actualNames.add(actualName);
		}
		return actualNames;
	}
	
	private InputStream getExpected(String qualifiedClassName) throws IOException{
		ZipEntry entry =zipFile.getEntry(qualifiedClassName.replace('.', '/')+".class");
		if(entry ==null){
			return null;
		}
		return zipFile.getInputStream(entry);
	}
}
