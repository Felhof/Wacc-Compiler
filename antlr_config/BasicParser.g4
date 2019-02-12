parser grammar BasicParser;

options {
  tokenVocab=BasicLexer;
}

prog: BEG (func)* stat (SEMICOL stat)* END EOF;

func:
      type IDENT OPEN (param_list)? CLOSE IS stat END ;

param_list:
      param (COMMA param)* ;

param:
      type IDENT ;

stat:
      SKP #todo
    | type IDENT ASS assign_rhs #varDeclarationStat
    | assign_lhs ASS assign_rhs #assignLhs
    | READ assign_lhs #readStat
    | FREE expr #freeStat
    | RETURN expr #returnStat
    | EXIT expr #exitStat
    | PRINT expr #printStat
    | PRINTLN expr #printlnStat
    | IF expr THEN stat ELSE stat FI #ifStat
    | WHILE expr DO stat DONE #whileStat
    | BEG stat END #newScopeStat
    | stat SEMICOL stat #recursiveStat ;

assign_lhs:
      IDENT #identLhs
    | array_elem #arrayElemLhs
    | pair_elem #pairElemLhs;

assign_rhs:
      expr #assignExp
    | array_liter #assignArray
    | NEWPAIR OPEN expr COMMA expr CLOSE #newPair
    | pair_elem #pairElemRhs
    | CALL IDENT OPEN (arg_list)? CLOSE #funcCall ;

arg_list:
    expr (COMMA expr)* ;

pair_elem:
      FST expr
    | SND expr ;

type:
      base_type #baseType
    | type OPENSQ CLOSESQ #arrayType
    | pair_type #pairType;

base_type:
      INT
    | BOOL
    | CHAR
    | STRING ;

pair_type:
      PAIR OPEN pair_elem_type COMMA pair_elem_type CLOSE ;

pair_elem_type:
      base_type  #pairElemBaseType
    | type OPENSQ CLOSESQ #pairElemArrayType
    | PAIR #pairElemPairType ;

expr:
      INTEGER #intExp
    | bool_liter #boolExp
    | char_liter #charExp
    | str_liter #strExp
    | pair_liter #pairExp
    | IDENT #identExp
    | array_elem #arrayExp
    | unary_oper expr #unaryExp
    | OPEN expr CLOSE  #bracketExp
    | expr MUL expr #binaryExp
    | expr DIV expr #binaryExp
    | expr MOD expr #binaryExp
    | expr PLUS expr #binaryExp
    | expr MINUS expr #binaryExp
    | expr GT expr #binaryExp
    | expr GE expr #binaryExp
    | expr LT expr #binaryExp
    | expr LE expr #binaryExp
    | expr EQUAL expr #binaryExp
    | expr NOTEQUAL expr #binaryExp
    | expr AND expr #binaryExp
    | expr OR expr #binaryExp;



unary_oper:
      PLUS
    | NEG
    | MINUS
    | LEN
    | ORD
    | CHR ;


binary_oper:
      AND
    | MUL
    | DIV
    | MOD
    | PLUS
    | MINUS
    | GT
    | GE
    | LT
    | LE
    | EQUAL
    | NOTEQUAL
    | OR ;

array_elem:
      IDENT (OPENSQ expr CLOSESQ)+ ;

bool_liter:
      TRUE
    | FALSE ;

char_liter:
      CHAR_LITER;

str_liter:
      STRING_LITER;

character:
        CHAR_LITER | ESCAPED_CHAR ;

array_liter:
      OPENSQ (expr (COMMA expr)*)? CLOSESQ ;

pair_liter:
      NULL ;