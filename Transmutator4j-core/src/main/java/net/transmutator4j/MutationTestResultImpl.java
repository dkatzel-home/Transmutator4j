package net.transmutator4j;

import java.io.Serializable;

import net.transmutator4j.mutations.Mutation;

/**
 * Default implementation of {@link MutationTestResult}.
 * @author dkatzel-home
 *
 */
class MutationTestResultImpl implements MutationTestResult, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final String mutatedClassName;
	private final int lineNumber;
	private final boolean testStillPassed;
	private final Mutation mutation;
	
	
	public MutationTestResultImpl(String mutatedClassName, int lineNumber,
			Mutation mutation, boolean testStillPassed) {
		this.mutatedClassName = mutatedClassName;
		this.lineNumber = lineNumber;
		this.mutation = mutation;
		this.testStillPassed = testStillPassed;
	}

	@Override
	public boolean testsStillPassed() {
		return testStillPassed;
	}

	@Override
	public String getMutatedClassname() {
		return mutatedClassName;
	}

	@Override
	public int getMutatedLine() {
		return lineNumber;
	}

	@Override
	public Mutation getMutation() {
		return mutation;
	}

	@Override
	public String toString() {
		return "MutationTestResultImpl [mutatedClassName=" + mutatedClassName
				+ ", lineNumber=" + lineNumber + ", testStillPassed="
				+ testStillPassed + ", mutation=" + mutation + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + lineNumber;
		result = prime
				* result
				+ ((mutatedClassName == null) ? 0 : mutatedClassName.hashCode());
		result = prime * result
				+ ((mutation == null) ? 0 : mutation.hashCode());
		result = prime * result + (testStillPassed ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MutationTestResultImpl other = (MutationTestResultImpl) obj;
		if (lineNumber != other.lineNumber)
			return false;
		if (mutatedClassName == null) {
			if (other.mutatedClassName != null)
				return false;
		} else if (!mutatedClassName.equals(other.mutatedClassName))
			return false;
		if (mutation == null) {
			if (other.mutation != null)
				return false;
		} else if (!mutation.equals(other.mutation))
			return false;
		if (testStillPassed != other.testStillPassed)
			return false;
		return true;
	}
	
	

}
