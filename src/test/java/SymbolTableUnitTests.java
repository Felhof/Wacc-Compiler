import compiler.AST.SymbolTable.VarInfo;
import compiler.AST.Types.IntType;
import compiler.AST.Types.Type;
import compiler.AST.SymbolTable.SymbolTable;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

public class SymbolTableUnitTests {

  Type varTypeDef = IntType.getInstance();
  SymbolTable st = new SymbolTable(null);

  @Test
  public void canLookUpInCurrentSymbolTable() {
    st.addVar("x", new VarInfo(varTypeDef, null));
    assertThat(st.lookUpVarScope("x"), is(notNullValue()));
  }

  @Test
  public void canLookUpInEnclosingSymbolTable() {
    st.addVar("x", new VarInfo(varTypeDef, null));
    SymbolTable stChild = new SymbolTable(st);
    assertThat(stChild.lookUpAllVar("x"), is(notNullValue()));
  }


}
