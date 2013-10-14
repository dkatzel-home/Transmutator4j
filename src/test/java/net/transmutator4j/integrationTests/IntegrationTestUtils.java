package net.transmutator4j.integrationTests;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class IntegrationTestUtils {

	private static final Pattern PASSED_PATTERN = Pattern.compile(">Passed<");
	
	private static final Pattern MUTATION_PATTERN = Pattern.compile("<mutation class");
	
	
	private IntegrationTestUtils(){
		//can not instantiate
	}
	
	public static int getNumberOfPassedMutations(String xmlOutput){
		
		Matcher matcher = PASSED_PATTERN.matcher(xmlOutput);
		int count=0;
		while(matcher.find()){
			count++;
		}
		
		return count;
	}
	public static int getNumberOfTotalMutations(String xmlOutput){
		
		Matcher matcher = MUTATION_PATTERN.matcher(xmlOutput);
		int count=0;
		while(matcher.find()){
			count++;
		}
		
		return count;
	}
}
