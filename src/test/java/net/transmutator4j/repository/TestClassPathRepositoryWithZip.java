package net.transmutator4j.repository;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TestClassPathRepositoryWithZip extends AbstractTestRepository{

	private final ClassRepository sut;
	private final ClassRepository delegate;
	
	public TestClassPathRepositoryWithZip() throws IOException{
		File dir =new File("externalClasses","zipDir");
		sut = new ClassPathClassRepository(dir.getAbsolutePath());
		delegate = new ZipClassRepository(new File(dir, "example.zip"));
	}
	@Override
	protected ClassRepository getSut() {
		return sut;
	}

	@Override
	protected List<String> getExpectedNames() {
		Iterator<String> iter= delegate.getQualifiedClassNames();
		List<String> list = new ArrayList<>();
		while(iter.hasNext()){
			list.add(iter.next());
		}
		return list;
		
	}

	@Override
	protected InputStream getExpected(String qualifiedClassName)
			throws IOException {
		return delegate.getClassAsStream(qualifiedClassName);
	}

	
}
