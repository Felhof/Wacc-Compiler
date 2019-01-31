import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

interface Advanced {}

@Category(Advanced.class)
public class AdvancedUnitTests {

  @Test
  public void veryAdvancedWaccProgram() {
    assertTrue(true);
    assertFalse(false);
  }

  @Test
  public void otherAdvancedWaccProgram() {
    assertTrue(true);
    assertFalse(false);
  }


}
