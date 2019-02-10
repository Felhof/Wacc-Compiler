package compiler.visitors.NodeElements;

import compiler.visitors.Returnable;
import java.util.ArrayList;
import java.util.List;

public class ParamList implements Returnable {
  List<Type> paramList;

  public ParamList() {
    this.paramList = new ArrayList<>();
  }

  public void add(Type type) {
    paramList.add(type);
  }
}
