import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.experimental.categories.Category;

public class UnitTests {

  interface Basic {}
  interface Advanced {}

  @Category(Basic.class)
    @Test
    public void basicWACCProgram() {
      assertTrue(true);
    }

  @Category(Advanced.class)
    @Test
    public void mediumWACCProgram() {
    assertFalse(false);
  }
}