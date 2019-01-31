import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.experimental.categories.Category;

interface Basic {}
interface Medium {}

@Category(Basic.class)
public class UnitTests {


  @Test
  public void basicWACCProgram() {
    assertTrue(true);
  }

  @Test
  public void anotherBasicWaccProgram() {
    assertTrue(true);
  }

  @Category(Advanced.class)
    @Test
    public void mediumWACCProgram() {
    assertFalse(false);
  }
}

