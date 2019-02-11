package compiler.visitors.NodeElements.Types;

import java.util.HashMap;
import java.util.Map;

public class BasicType extends Type {
  private TYPE type;

  public BasicType(TYPE type) {
    this.type = type;
  }

  @Override
  public boolean equals(Type type) {
    if (type instanceof GenericType) {
      return true;
    }
    return type instanceof BasicType
        && this.type.equals(((BasicType) type).type());
  }

  public TYPE type() {
    return type;
  }

  @Override
  public int hashCode() {
    return type.ordinal();
  }

  @Override
  public String toString() {
    return type.value().toUpperCase();
  }

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
      return this == o;
    }

  }

}
