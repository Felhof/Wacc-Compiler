parser grammar BasicParser;

options {
  tokenVocab=BasicLexer;
}

prog: BEG (func)* stat END EOF;

func:
      type IDENT OPEN (param_list)? CLOSE IS stat END ;

param_list:
      param (COMMA param)* ;

param:
      type IDENT ;

stat:
      SKP
    | type IDENT ASS assign_rhs
    | assign_lhs ASS assign_rhs
    | READ assign_lhs
    | FREE expr
    | RETURN expr
    | EXIT expr
    | PRINT expr
    | PRINTLN expr
    | IF expr THEN stat ELSE stat FI
    | WHILE expr DO stat DONE
    | BEG stat END
    | stat SEMICOL stat;

assign_lhs:
      IDENT
    | array_elem
    | pair_elem ;

assign_rhs:
      expr
    | array_liter
    | NEWPAIR OPEN expr COMMA expr CLOSE
    | pair_elem
    | CALL IDENT OPEN (arg_list)? CLOSE ;

arg_list:
    expr (COMMA expr)* ;

pair_elem:
      FST expr
    | SND expr ;

type:
      base_type
    | array_type
    | pair_type ;

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
      PAIR OPEN pair_elem_type COMMA pair_elem_type CLOSE ;

pair_elem_type:
      base_type
    | array_type
    | PAIR ;

expr:
      INTEGER
    | bool_liter
    | char_liter
    | str_liter
    | pair_liter
    | IDENT
    | array_elem
    | unary_oper expr
    | expr binary_oper expr
    | OPEN expr CLOSE ;

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
      APOS (character)? APOS ;

str_liter:
      QUOT (character)* QUOT ;

character:
      ~(CHAR_EXC) | ESCAPED_CHAR | WS ;

array_liter:
      OPENSQ (expr (COMMA expr)*)? CLOSESQ ;

pair_liter:
      NULL ;


