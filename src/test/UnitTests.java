import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.experimental.categories.Category;
import org.junit.jupiter.api.Test;

public class UnitTests {

  interface Basic {}

  @Category(Basic.class)
  public static class BasicUnitTests {

    @Test
    void basicWACCProgram() {
      assertTrue(true);
    }
  }

  @Test
  void mediumWACCProgram() {
    assertFalse(false);
  }
}