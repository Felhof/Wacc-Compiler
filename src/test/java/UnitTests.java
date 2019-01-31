import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class UnitTests {

  interface Basic {}

//  @Category(Basic.class)
//  public static class BasicUnitTests {

    @Test
    public void basicWACCProgram() {
      assertTrue(true);
    }
//  }

  @Test
  public void mediumWACCProgram() {
    assertFalse(false);
  }
}