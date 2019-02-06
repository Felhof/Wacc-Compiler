import compiler.visitors.identifiers.Identifier;
import compiler.visitors.SymbolTable;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

public class SymbolTableUnitTests {

  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();

  Identifier identifier = context.mock(Identifier.class);
  SymbolTable st = new SymbolTable(null);


  @Test
  public void canLookUpInCurrentSymbolTable() {
    st.add("x", identifier);
    assertThat(st.lookUpScope("x"), is(notNullValue()));
  }

  @Test
  public void canLookUpInEnclosingSymbolTable() {
    st.add("x", identifier);
    SymbolTable stChild = new SymbolTable(st);
    assertThat(stChild.lookUpAll("x"), is(notNullValue()));
  }

}
