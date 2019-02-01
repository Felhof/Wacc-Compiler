lexer grammar BasicLexer;

//operators
PLUS: '+' ;
MINUS: '-' ;

//brackets
OPEN_PARENTHESES: '(' ;
CLOSE_PARENTHESES: ')' ;

//numbers
fragment DIGIT: '0'..'9' ; 

INTEGER: DIGIT+ ;

//Ignore Whitespace and Newline
WHITESPACE : ' ' -> skip ;
NEWLINE : '\n' -> skip ;





