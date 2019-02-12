import compiler.visitors.NodeElements.Types.BasicType;
import compiler.visitors.NodeElements.Types.BasicType.TYPE;
import compiler.visitors.NodeElements.Types.Type;
import compiler.visitors.SymbolTable;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

public class SymbolTableUnitTests {

  Type varTypeDef = new BasicType(TYPE.INT);
  SymbolTable st = new SymbolTable(null);

  @Test
  public void canLookUpInCurrentSymbolTable() {
    st.addVar("x", varTypeDef);
    assertThat(st.lookUpVarScope("x"), is(notNullValue()));
  }

  @Test
  public void canLookUpInEnclosingSymbolTable() {
    st.addVar("x", varTypeDef);
    SymbolTable stChild = new SymbolTable(st);
    assertThat(stChild.lookUpAllVar("x"), is(notNullValue()));
  }


}
