import compiler.visitors.Identifiers.Variable;
import compiler.visitors.NodeElements.Types.BasicType;
import compiler.visitors.NodeElements.Types.BasicType.TYPE;
import compiler.visitors.SymbolTable;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

public class SymbolTableUnitTests {

  Variable var = new Variable(new BasicType(TYPE.INT));
  SymbolTable st = new SymbolTable(null);

  @Test
  public void canLookUpInCurrentSymbolTable() {
    st.addVar("x", var);
    assertThat(st.lookUpVarScope("x"), is(notNullValue()));
  }

  @Test
  public void canLookUpInEnclosingSymbolTable() {
    st.addVar("x", var);
    SymbolTable stChild = new SymbolTable(st);
    assertThat(stChild.lookUpAllVar("x"), is(notNullValue()));
  }


}
