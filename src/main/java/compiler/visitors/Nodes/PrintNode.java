package compiler.visitors.Nodes;

import compiler.visitors.identifiers.Identifier;

public class PrintNode {

  boolean newLine;

  public PrintNode(boolean newLine) {
    this.newLine = newLine;
  }
}
