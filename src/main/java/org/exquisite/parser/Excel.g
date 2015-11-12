grammar Excel;

options {
	output=AST;
	ASTLabelType=CommonTree;
}


tokens {
	FUNC;
	CALL;
	ROOT;
	ERROR;
}

@header { package org.exquisite.parser; }
@lexer::package { org.exquisite.parser; }
@parser::package { org.exquisite.parser; }




public parse
	:	exp EOF -> ^(ROOT exp)
	;

exp
	:	orExp
	;

orExp
	:	andExp (OR^ andExp)*
	;

andExp
	:	eqExp (AND^ eqExp)*
	;

eqExp
	:	relExp (( EQUALS^ | NOTEQUALS^) relExp)*
	;

relExp
	:	addExp ((LT^ | LTEQ^ |GT^ | GTEQ^) addExp)*
	;

addExp
	:	multExp ((PLUS^ | MINUS^) multExp)*
	;

multExp
	:	unaryExp (( MULT^ | DIV^ | MOD^ |POW^| IS^) unaryExp)*
	;

unaryExp
	:	NOT atom -> ^(NOT atom)
	|	MINUS atom -> ^(MINUS atom)
	|	atom
	;

atom
	:	TRUE
	|	FALSE
	|	INT
	|	FLOAT
	|	function
	|	error
	|	'(' exp ')' -> exp
	|	REFERENCE
	;

POW	:	'^';
DIV	:	'/';
MOD	:	'%';
MULT	:	'*';
PLUS	:	'+';
MINUS	:	'-';
LT	:	'<';
LTEQ	:	'<=';
GT	:	'>';
GTEQ	:	'>=';
EQUALS	:	'=';
NOTEQUALS	
	:	'<>';
INT	:	'0'..'9'+;
FLOAT	:	('0'..'9')* '.' ('0'..'9')+;
OR	:	'or';
AND	:	'and';
IS	:	'is';
NOT	:	'not';
TRUE	:	'true';
FALSE	:	'false';

function
	:	IDENT '(' ( exp ((',' | ';') exp)* )? ')' -> ^(FUNC ^(IDENT exp*));
	
error	:	ERROR_STRING -> ^(ERROR ERROR_STRING);

//REFERENCE
REFERENCE
	:	('$')? ('a'..'z' | 'A'..'Z')+ ('$')? INT
	|	('$')? ('a'..'z' | 'A'..'Z')+ ('$')? INT ':' ('$')? ('a'..'z' | 'A'..'Z')+ ('$')? INT 
	|	IDENT '!' ('$')? ('a'..'z' | 'A'..'Z')+ ('$')? INT
	|	IDENT '!' ('$')? ('a'..'z' | 'A'..'Z')+ ('$')? INT ':' ('$')? ('a'..'z' | 'A'..'Z')+ ('$')? INT 
	;

R1C1_REFERENCE
	:	('R') (INT)? ('C')
	|	('R') ('[' (('-' | '+')? INT) ']')? 
	;

IDENT
	: 	('a'..'z' | 'A'..'Z') ('a'..'z' | 'A'..'Z' |'0'..'9')*
	;

ERROR_STRING
	: '#DIV/0!' 
	| '#N/A' 
	| '#NAME?' 
	| '#NULL!' 
	| '#NUM!' 
	| '#REF!' 
	| '#VALUE!' 
	| '#####'
	;

SPACE	:	(' ' | '\t' | '\r' | '\n') {skip();} ;
