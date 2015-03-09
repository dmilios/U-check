grammar MiTL;


options {
	backtrack=true;
	output=AST;
}


eval	:	statementList;



statementList
	:	NEWLINE!* statement+
	;

statement
	:	declaration NEWLINE!+
	|	exprOR NEWLINE!+
	;

declaration
	:	CONST^ DOUBLE ID ('='! (INTEGER | FLOAT))? SEMICOLON!
	|	CONST^ INT ID ('='! INTEGER)? SEMICOLON!
	|	CONST^ BOOL ID ('='! (TRUE | FALSE))? SEMICOLON!
	;



// $<Boolean Expressions

exprOR	
	:	exprAND (OR^ exprAND)*
	;

exprAND	
	:	mitlTerm (AND^ mitlTerm)*
	;
	
	

mitlTerm	:	booelanAtomic (U^ timeBound booelanAtomic)?
		|	F^ timeBound booelanAtomic
		|	G^ timeBound booelanAtomic
		;
// $>



// $<Temporal Operators
timeBound
	:	'<='! termExpr
	|	LBRAT! termExpr COMMA^ termExpr RBRAT!
	|	termExpr
	;
// $>




booelanAtomic 
	:	NOT^?
		(	relationalExpr
		|	LPAR! exprOR RPAR!
		|	TRUE
		|	FALSE
		)
	;



relationalExpr
	:	termExpr (EQ | NEQ | GT | LT | GE | LE)^ termExpr
	;




// $<Arithmetic Expressions

termExpr
    :    factorExpr 
         ( PLUS^ factorExpr 
         | MINUS^ factorExpr
         )* 
    ;

factorExpr
	:	factor
		( MULT^ factor 
		| DIV^ factor
		)* 
	;

factor
	:	MINUS^?
		(functionExpr
		|	atomic
		|	LPAR! termExpr RPAR!

		)
	;

functionExpr
	:	ID^ LPAR! termExpr (COMMA! termExpr)* RPAR!
	;

atomic
	:	(INTEGER | FLOAT)
	|	ID;

// $>
    

CONST   :       'const';
INT  	:       'int';
DOUBLE  :       'double';
BOOL  	:       'bool';
COMMA   :       ',';
SEMICOLON  :    ';';
LPAR    :       '(';
RPAR    :       ')';

LBRAT   :       '[';
RBRAT   :       ']';
U       :	'U';
F       :	'F';
G       :	'G';

TRUE	:	'true';
FALSE	:	'false';

PLUS	:	'+';
MINUS	:	'-';
MULT	:	'*';
DIV	:	'/';

AND	:	'&';
OR	:	'|';
NOT	:	'!';

EQ	:	'=';
NEQ	:	'!=';
GT	:	'>';
GE	:	'>=';
LT	:	'<';
LE	:	'<=';



// $<Terminal

INTEGER	:	'0'..'9'+
	;

FLOAT
	:   ('0'..'9')+ '.' ('0'..'9')* EXPONENT?
	|   '.' ('0'..'9')+ EXPONENT?
	|   ('0'..'9')+ EXPONENT
	;
    
fragment
EXPONENT : ('e'|'E') ('+'|'-')? ('0'..'9')+ ;



ID	:	('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'0'..'9'|'_')*
	;

NEWLINE	:	( '\r'| '\n' )
	;

// $>




// $<White space

COMMENT
    :   '//' ~('\n'|'\r')* {$channel=HIDDEN;}
    ;

/* Ignore white space characters, except from newline */
WS  
    :   (' ' | '\t' | NEWLINE ) {$channel=HIDDEN;}
    ; 
// $>

 
