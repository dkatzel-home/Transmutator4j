package net.transmutator4j.integrationTests;

import java.io.File;


import net.transmutator4j.RunTransmutator4j;
import net.transmutator4j.TestUtils;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import static org.junit.Assert.*;

public class TestBowlingGame {

	@Rule
	public TemporaryFolder outputFolder = new TemporaryFolder();
	
	/**
	 * Tests that we find that any mutations
	 * to the frame counter
	 * in the bowling example still pass all tests.  This
	 * shows that the frame code is not needed and the Game class
	 * can be simplified. 
	 * <p/>
	 * (This insight was originally detected by
	 * the mutation
	 * program Jester, see URLs in package-info for details.)  
	 */
	@Test
	public void catchUnecessaryFrameCode() throws Exception{
		File outputFile = new File(outputFolder.getRoot(),"output.xml");
		String srcRoot = new File("test/java/example/bowling").getAbsolutePath();
		RunTransmutator4j.main(new String[]{"-out", outputFile.getAbsolutePath(),
				"-src", srcRoot,
				"-include", "example\\.bowling\\.[G|S].+",
				"-test", "example.bowling.TestGame"
		}
				);
		
		String actualText = TestUtils.readTextFileAsString(outputFile);
		assertEquals(6, IntegrationTestUtils.getNumberOfPassedMutations(actualText));
		assertTrue(actualText.contains("<mutation class =\"example.bowling.Game\" line=\"33\" descr=\"primitive == becomes !=\">Passed</mutation>"));
		assertTrue(actualText.contains("<mutation class =\"example.bowling.Game\" line=\"33\" descr=\"changed constant 1 (or true) to 0 (or false)\">Passed</mutation>"));
		assertTrue(actualText.contains("<mutation class =\"example.bowling.Game\" line=\"65\" descr=\"changed constant 0 (or false) to 1 (or true)\">Passed</mutation>"));
		assertTrue(actualText.contains("<mutation class =\"example.bowling.Game\" line=\"66\" descr=\"changed constant 1 (or true) to 0 (or false)\">Passed</mutation>"));
		
		assertTrue(actualText.contains("<mutation class =\"example.bowling.Game\" line=\"40\" descr=\"changed constant 1 (or true) to 0 (or false)\">Passed</mutation>"));
		assertTrue(actualText.contains("<mutation class =\"example.bowling.Game\" line=\"50\" descr=\"changed constant 1 (or true) to 0 (or false)\">Passed</mutation>"));
		
		
	}

	
	
	
}
