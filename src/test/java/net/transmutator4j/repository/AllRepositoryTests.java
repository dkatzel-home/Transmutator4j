package net.transmutator4j.repository;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	TestClassDirectoryRepository.class,
	TestZipClassRepository.class
})
public class AllRepositoryTests {

}
