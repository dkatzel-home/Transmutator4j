package net.transmutator4j.repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public class TestClassDirectoryRepository extends AbstractTestRepository{

	private final File rootDir;
	private final ClassDirectoryClassRepository sut;
	
	public TestClassDirectoryRepository(){
		rootDir = new File("externalClasses/classDir");
		sut = new ClassDirectoryClassRepository(rootDir);
	}
	@Override
	protected ClassRepository getSut() {
		return sut;
	}

	@Override
	protected List<String> getExpectedNames() {
		return getClassNamesFrom(rootDir, "");
	}
	
	private List<String> getClassNamesFrom(File dir, String packageName){
		List<String> names = new ArrayList<>();
		StringBuilder packagePrefixBuilder = new StringBuilder(packageName.length()+1)
														.append(packageName);
		if(!packageName.isEmpty()){
			packagePrefixBuilder.append('.');
		}
		String packageNamePrefix = packagePrefixBuilder.toString();
		for(File f : dir.listFiles()){
			if(f.isHidden()){
				continue;
			}
			if(f.isDirectory()){
				names.addAll(getClassNamesFrom(f, packageNamePrefix+f.getName()));
			}else if(f.getName().endsWith(".class")){
				names.add(packageNamePrefix+f.getName().substring(0, f.getName().length()-6));
			}
		}
		return names;
	}

	@Override
	protected InputStream getExpected(String qualifiedClassName)
			throws IOException {
		File f = new File(rootDir, qualifiedClassName.replaceAll("\\.", Matcher.quoteReplacement(File.separator)) +".class");
		return new FileInputStream(f);
		
	}

}
