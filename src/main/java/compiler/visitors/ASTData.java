package compiler.visitors;

public interface ASTData {

  CodeGenData accept(ASTVisitor visitor);
}
