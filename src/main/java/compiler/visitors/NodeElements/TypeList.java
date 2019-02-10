package compiler.visitors.NodeElements;

import compiler.visitors.Returnable;
import java.util.ArrayList;
import java.util.List;

public class TypeList implements Returnable {
  List<Type> listOfTypes;

  public TypeList() {
    this.listOfTypes = new ArrayList<>();
  }

  public void add(Type type) {
    listOfTypes.add(type);
  }

  public List<Type> listOfTypes() {
    return listOfTypes;
  }

  public boolean equals(TypeList typeList) {
    if (listOfTypes.size() != typeList.listOfTypes().size()) {
      return false;
    } else {
      for (int i = 0; i < listOfTypes.size(); i++) {
        if (!listOfTypes.get(i).equals(typeList.listOfTypes().get(i))) {
          return false;
        }
      }
      return true;
    }
  }

}
