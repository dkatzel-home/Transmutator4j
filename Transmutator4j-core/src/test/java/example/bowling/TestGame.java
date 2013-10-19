/**
 * This is example code for
 * calculating bowling scores.
 * It was written by Bob Koss  and Bob Martin
 * as an example of pair programming
 * Later, when the 
 * mutation tester "Jester" was run against it,
 * some areas of the code were 
 * shown to be unnecessary.
 * @see <a href="http://www.objectmentor.com/resources/articles/xpepisode.htm">
 * Engineer Notebook: An Extreme Programming Episode</a>
 * @see <a href = "http://tech.groups.yahoo.com/group/extremeprogramming/message/32277">
 * running Jester on this code</a>
 * @see <a href = "http://jester.sourceforge.net/">Jester </a>
 */
package example.bowling;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
/**
 * This is the original unit tests
 * written by Bob Koss and Bob Martin
 * for their bowling scoring application,
 * I have slightly modified to
 * use JUnit 4 instead of Junit 3.
 *
 */
public class TestGame 
{
  

  private Game g;
  @Before
  public void setUp()
  {
    g = new Game();
  }

  @Test public void testTwoThrowsNoMark()
  {
    g.add(5);
    g.add(4);
    assertEquals(9, g.score());
  }

  @Test public void testFourThrowsNoMark()
  {
    g.add(5);
    g.add(4);
    g.add(7);
    g.add(2);
    assertEquals(18, g.score());
    assertEquals(9, g.scoreForFrame(1));
    assertEquals(18, g.scoreForFrame(2));
  }

  @Test public void testSimpleSpare()
  {
    g.add(3);
    g.add(7);
    g.add(3);
    assertEquals(13, g.scoreForFrame(1));
  }

  @Test public void testSimpleFrameAfterSpare()
  {
    g.add(3);
    g.add(7);
    g.add(3);
    g.add(2);
    assertEquals(13, g.scoreForFrame(1));
    assertEquals(18, g.scoreForFrame(2));
    assertEquals(18, g.score());
  }

  @Test public void testSimpleStrike()
  {
    g.add(10);
    g.add(3);
    g.add(6);
    assertEquals(19, g.scoreForFrame(1));
    assertEquals(28, g.score());
  }

  @Test public void testPerfectGame()
  {
    for (int i=0; i<12; i++)
    {
      g.add(10);
    }
    assertEquals(300, g.score());
  }

  @Test public void testEndOfArray()
  {
    for (int i=0; i<9; i++)
    {
      g.add(0);
      g.add(0);
    }
    g.add(2);
    g.add(8); // 10th frame spare
    g.add(10); // Strike in last position of array.
    assertEquals(20, g.score());
   }

  @Test public void testSampleGame()
  {
    g.add(1);
    g.add(4);
    g.add(4);
    g.add(5);
    g.add(6);
    g.add(4);
    g.add(5);
    g.add(5);
    g.add(10);
    g.add(0);
    g.add(1);
    g.add(7);
    g.add(3);
    g.add(6);
    g.add(4);
    g.add(10);
    g.add(2);
    g.add(8);
    g.add(6);
    assertEquals(133, g.score());
  }

  @Test public void testHeartBreak()
  {
    for (int i=0; i<11; i++)
      g.add(10);
    g.add(9); 
    assertEquals(299, g.score());
  }

  @Test public void testTenthFrameSpare()
  {
    for (int i=0; i<9; i++)
      g.add(10);
    g.add(9);
    g.add(1);
    g.add(1); 
    assertEquals(270, g.score());
  }
}

