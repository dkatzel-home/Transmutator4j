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

public class Game
{
  public int score()
  {
    return scoreForFrame(itsCurrentFrame);
  }

  public void add(int pins)
  {
    itsScorer.addThrow(pins);
    adjustCurrentFrame(pins);
  }

  private void adjustCurrentFrame(int pins)
  {
    if (firstThrowInFrame == true)
    {
      if (adjustFrameForStrike(pins) == false)
        firstThrowInFrame = false;
    }
    else
    {
      firstThrowInFrame=true;
      advanceFrame();
    }
  }

  private boolean adjustFrameForStrike(int pins)
  {
    if (pins == 10)
    {
      advanceFrame();
      return true;
    }
    return false;
  }  

  private void advanceFrame()
  {
    itsCurrentFrame = Math.min(10, itsCurrentFrame + 1);
  }

  public int scoreForFrame(int theFrame)
  {
    return itsScorer.scoreForFrame(theFrame);
  }

  private int itsCurrentFrame = 0;
  private boolean firstThrowInFrame = true;
  private Scorer itsScorer = new Scorer();
}

