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
    | assign_lhs ASS assign_rhs #todo
    | READ assign_lhs #todo
    | FREE expr #todo
    | RETURN expr #returnStat
    | EXIT expr #exitStat
    | PRINT expr #todo
    | PRINTLN expr #todo
    | IF expr THEN stat ELSE stat FI #ifStat
    | WHILE expr DO stat DONE #whileStat
    | BEG stat END #todo
    | stat SEMICOL stat #recursiveStat ;

assign_lhs:
      IDENT
    | array_elem
    | pair_elem ;

assign_rhs:
      expr #assignExp
    | array_liter #assignArray
    | NEWPAIR OPEN expr COMMA expr CLOSE #newPair
    | pair_elem #todoB
    | CALL IDENT OPEN (arg_list)? CLOSE #todoC ;

arg_list:
    expr (COMMA expr)* ;

pair_elem:
      FST expr
    | SND expr ;

type:
      base_type #baseType
    | array_type #arrayType
    | pair_type #pairType;

base_type:
      INT
    | BOOL
    | CHAR
    | STRING ;

array_type:
      base_type OPENSQ CLOSESQ
    | array_type OPENSQ CLOSESQ
    | pair_type OPENSQ CLOSESQ;

pair_type:
      PAIR OPEN pair_elem_type COMMA pair_elem_type CLOSE #defPairType;

pair_elem_type:
      base_type  #pairElemBaseType
    | array_type #pairElemArrayType
    | PAIR       #pairElemPairType ;

expr:
      INTEGER #intExp
    | bool_liter #boolExp
    | char_liter #charExp
    | str_liter #strExp
    | pair_liter #pairExp
    | IDENT #identExp
    | array_elem #arrayExp
    | unary_oper expr #unaryExp
    | expr binary_oper expr #binaryExp
    | OPEN expr CLOSE  #bracketExp ;

unary_oper:
      PLUS
    | NEG
    | MINUS
    | LEN
    | ORD
    | CHR ;


binary_oper:
      MUL
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
    | AND
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