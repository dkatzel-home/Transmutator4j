package net.transmutator4j.repo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class TestClassPathRepositoryDir extends AbstractTestRepository{

	private final ClassRepository sut;
	private final ClassRepository delegate;
	
	public TestClassPathRepositoryDir() throws IOException{
		File dir =new File("externalClasses","classDir");
		sut = new ClassPathClassRepository(dir.getAbsolutePath());
		delegate = new ClassDirectoryClassRepository(dir);
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
