package net.transmutator4j.integrationTests;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import net.transmutator4j.RunTransmutator4j;
import net.transmutator4j.TestUtils;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class TestTransmutateCounter {
	@Rule
	public TemporaryFolder outputFolder = new TemporaryFolder();
	
	
	@Test
	public void runOnCounter() throws Exception{
		PrintStream oldSystemOut =System.out;
		
		try{
			//used to ignore STDOUT
			System.setOut(new PrintStream(new ByteArrayOutputStream()));
			File outputFile = new File(outputFolder.getRoot(),"output.xml");
			String srcRoot = new File(".").getAbsolutePath();
			RunTransmutator4j.main(new String[]{"-out", outputFile.getAbsolutePath(),
					"-src", srcRoot,
					"-include", "net\\.transmutator4j\\.integrationTests\\.Counter.*",
					"-test", "net.transmutator4j.integrationTests.TestCounter"
					});
			
			String actualText = TestUtils.readTextFileAsString(outputFile);
			System.out.println(actualText);
			assertEquals(0, IntegrationTestUtils.getNumberOfPassedMutations(actualText));
			assertEquals(3, IntegrationTestUtils.getNumberOfTotalMutations(actualText));
		}finally{
			//restore system.out
			System.setOut(oldSystemOut);
		}
	}
}
