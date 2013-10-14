package net.transmutator4j.repo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class TestClassPathRepositoryMultipleDirs extends AbstractTestRepository{

	private final ClassRepository sut;
	private final ClassRepository delegate,delegate2;
	
	public TestClassPathRepositoryMultipleDirs() throws IOException{
		File dir =new File("externalClasses","classDir");
		File dir2 =new File("externalClasses","otherClasses");
		sut = new ClassPathClassRepository(dir.getAbsolutePath()+File.pathSeparator + dir2.getAbsolutePath());
		delegate = new ClassDirectoryClassRepository(dir);
		delegate2 = new ClassDirectoryClassRepository(dir2);
	}
	@Override
	protected ClassRepository getSut() {
		return sut;
	}

	@Override
	protected List<String> getExpectedNames() {
		List<String> list = getNamesFromRepository(delegate);
		list.addAll(getNamesFromRepository(delegate2));
		return list;
		
	}
	private List<String> getNamesFromRepository(ClassRepository rep) {
		Iterator<String> iter= rep.getQualifiedClassNames();
		List<String> list = new ArrayList<>();
		while(iter.hasNext()){
			list.add(iter.next());
		}
		return list;
	}

	@Override
	protected InputStream getExpected(String qualifiedClassName)
			throws IOException {
		InputStream in= delegate.getClassAsStream(qualifiedClassName);
		if(in !=null){
			return in;
		}
		return delegate2.getClassAsStream(qualifiedClassName);
	}

}
