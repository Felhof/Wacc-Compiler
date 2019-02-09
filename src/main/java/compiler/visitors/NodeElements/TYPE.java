package compiler.visitors.NodeElements;

import java.util.HashMap;
import java.util.Map;

public enum TYPE {
  BOOL("bool"), INT("int"), CHAR("char"), STRING("string");

  private String value;
  private static Map<String, TYPE> map;

  TYPE(String value) {
    this.value = value;
  }

  public String value() {
    return value;
  }

  public boolean equals(TYPE type) {
    return this == type;
  }

  static {
    map = new HashMap<>();
    for(TYPE t : TYPE.values()) {
      map.put(t.value, t);
    }
  }

  public static TYPE get(String string) {
    return map.get(string);
  }

}
