package net.transmutator4j;

import java.io.Closeable;

import net.transmutator4j.mutations.Mutation;

/**
 * {@code MutationTestListener} is a Listener
 * that listens for {@link MutationTestResult}s.
 * 
 * @author dkatzel-home
 *
 */
public interface MutationTestListener extends Closeable {
	/**
	 * Get general test information about the code under test
	 * BEFORE it has been mutated.  This method will only be called
	 * once before any mutations occur.
	 * @param numberOfTestsRun the number of tests run;
	 * will be a number >=0.
	 * @param runtime the number of milliseconds all the tests
	 * take to run UNMUTATED.
	 */
	void testInfo(int numberOfTestsRun, long runtime);
	/**
	 * A {@link Mutation} has been made on the code under test
	 * with the given {@link MutationTestResult}.  
	 * @param result the {@link MutationTestResult}
	 * will never be null.
	 */
	void mutationResult(MutationTestResult result);

}
