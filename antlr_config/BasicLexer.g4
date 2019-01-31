lexer grammar BasicLexer;

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

//brackets
OPEN_PARENTHESES: '(' ;
CLOSE_PARENTHESES: ')' ;

//numbers
fragment DIGIT: '0'..'9' ;

INTEGER: DIGIT+ ;
