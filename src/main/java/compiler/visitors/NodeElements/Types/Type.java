package compiler.visitors.NodeElements.Types;

import compiler.visitors.Returnable;
import java.util.HashMap;
import java.util.Map;

public abstract class Type implements Returnable {
  public boolean equals(Type type) {
    return false;
  }

  public enum TYPE {
    BOOL("bool"), INT("int"), CHAR("char"), STRING("string"), RECOVERY(null);

    private String value;
    private static Map<String, TYPE> map;

    TYPE(String value) {
      this.value = value;
    }

    public String value() {
      return value;
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

    public boolean equals(TYPE o) {
      return this == o || this == RECOVERY || o == RECOVERY;
    }

  }
}
