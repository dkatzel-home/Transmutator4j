package net.transmutator4j.integrationTests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	TestCounter.class,
	TestTransmutateCounter.class,
	TestBowlingGame.class
})
public class AllIntegrationTests {

}
