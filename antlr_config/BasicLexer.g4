lexer grammar BasicLexer;

//program keywords
BEG: 'begin';
END: 'end' ;

//function keywords
IS: 'is' ;

//misc
COMMA: ',' ;
SEMICOL: ';' ;
COL: ':' ;
APOS: '\'' ;
QUOT: '"' ;
BACKSLASH: '\\' ;
HASH: '#' ;
DOT: '.' ;
QMARK: '?' ;
BAR: '|' ;

//statement keywords
SKP: 'skip' ;
ASS: '=' ;
READ: 'read' ;
FREE: 'free' ;
RETURN: 'return' ;
EXIT: 'exit' ;
PRINT: 'print' ;
PRINTLN: 'println' ;
IF: 'if' ;
THEN: 'then' ;
ELSE: 'else' ;
FI: 'fi' ;

WHILE: 'while' ;
DO: 'do' ;
DONE: 'done' ;

//assignment keywords
NEWPAIR: 'newpair' ;
CALL: 'call' ;

//pair-elem keywords
FST: 'fst' ;
SND: 'snd' ;

//base-type keywords
INT: 'int' ;
BOOL: 'bool' ;
CHAR: 'char' ;
STRING: 'string' ;

//pair-type keywords
PAIR: 'pair' ;

//unary operators
NEG: '!' ;
LEN: 'len' ;
ORD: 'ord' ;
CHR: 'chr' ;

//binary operators
MUL: '*' ;
DIV: '/' ;
MOD: '%' ;
PLUS: '+' ;
MINUS: '-' ;
GT: '>' ;
GE: '>=' ;
LT: '<' ;
LE: '<=' ;
EQUAL: '==' ;
NOTEQUAL: '!=' ;
AND: '&&' ;
OR: '||' ;

//ident
IDENT: ('_' | LOWER | UPPER) ('_' | LOWER | UPPER | DIGIT)*;

//brackets
OPEN: '(' ;
CLOSE: ')' ;

OPENSQ: '[' ;
CLOSESQ: ']' ;

OPENC: '{' ;
CLOSEC: '}' ;

//characters

CHAR_EXC: BACKSLASH | APOS | QUOT ;

//letters

fragment LOWER: 'a'..'z' ;
fragment UPPER: 'A'..'Z' ;

//numbers
fragment DIGIT: '0'..'9' ;

INT_SIGN: PLUS | MINUS ;

INTEGER: (INT_SIGN)?(DIGIT)+ ;

ESCAPED_CHAR: BACKSLASH ('0' | 'b' | 't' | 'n' | 'f' | 'r' | QUOT | APOS | BACKSLASH) ;

//bool-liter
TRUE: 'true' ;
FALSE: 'false' ;

//pair-liter
NULL: 'null' ;

WS: (' ')+ -> skip ;

COMMENT: HASH .*? EOL -> channel(HIDDEN) ;

EOL: ('\n' | '\r' | '\n\r' | '\r\n' |'\t' | '\n\n') -> skip;