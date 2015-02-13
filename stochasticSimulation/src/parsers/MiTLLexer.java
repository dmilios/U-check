// $ANTLR 3.5-rc-2 /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g 2014-07-15 11:25:31
package parsers;

import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")
public class MiTLLexer extends Lexer {
	public static final int EOF=-1;
	public static final int AND=4;
	public static final int BOOL=5;
	public static final int COMMA=6;
	public static final int COMMENT=7;
	public static final int CONST=8;
	public static final int DIV=9;
	public static final int DOUBLE=10;
	public static final int EQ=11;
	public static final int EXPONENT=12;
	public static final int F=13;
	public static final int FALSE=14;
	public static final int FLOAT=15;
	public static final int G=16;
	public static final int GE=17;
	public static final int GT=18;
	public static final int ID=19;
	public static final int INT=20;
	public static final int INTEGER=21;
	public static final int LBRAT=22;
	public static final int LE=23;
	public static final int LPAR=24;
	public static final int LT=25;
	public static final int MINUS=26;
	public static final int MULT=27;
	public static final int NEQ=28;
	public static final int NEWLINE=29;
	public static final int NOT=30;
	public static final int OR=31;
	public static final int PLUS=32;
	public static final int RBRAT=33;
	public static final int RPAR=34;
	public static final int SEMICOLON=35;
	public static final int TRUE=36;
	public static final int U=37;
	public static final int WS=38;

	// delegates
	// delegators
	public Lexer[] getDelegates() {
		return new Lexer[] {};
	}

	public MiTLLexer() {} 
	public MiTLLexer(CharStream input) {
		this(input, new RecognizerSharedState());
	}
	public MiTLLexer(CharStream input, RecognizerSharedState state) {
		super(input,state);
	}
	@Override public String getGrammarFileName() { return "/home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g"; }

	// $ANTLR start "CONST"
	public final void mCONST() throws RecognitionException {
		try {
			int _type = CONST;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:115:9: ( 'const' )
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:115:17: 'const'
			{
			match("const"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "CONST"

	// $ANTLR start "INT"
	public final void mINT() throws RecognitionException {
		try {
			int _type = INT;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:116:7: ( 'int' )
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:116:15: 'int'
			{
			match("int"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "INT"

	// $ANTLR start "DOUBLE"
	public final void mDOUBLE() throws RecognitionException {
		try {
			int _type = DOUBLE;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:117:9: ( 'double' )
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:117:17: 'double'
			{
			match("double"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "DOUBLE"

	// $ANTLR start "BOOL"
	public final void mBOOL() throws RecognitionException {
		try {
			int _type = BOOL;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:118:8: ( 'bool' )
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:118:16: 'bool'
			{
			match("bool"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "BOOL"

	// $ANTLR start "COMMA"
	public final void mCOMMA() throws RecognitionException {
		try {
			int _type = COMMA;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:119:9: ( ',' )
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:119:17: ','
			{
			match(','); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "COMMA"

	// $ANTLR start "SEMICOLON"
	public final void mSEMICOLON() throws RecognitionException {
		try {
			int _type = SEMICOLON;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:120:12: ( ';' )
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:120:17: ';'
			{
			match(';'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "SEMICOLON"

	// $ANTLR start "LPAR"
	public final void mLPAR() throws RecognitionException {
		try {
			int _type = LPAR;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:121:9: ( '(' )
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:121:17: '('
			{
			match('('); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "LPAR"

	// $ANTLR start "RPAR"
	public final void mRPAR() throws RecognitionException {
		try {
			int _type = RPAR;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:122:9: ( ')' )
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:122:17: ')'
			{
			match(')'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "RPAR"

	// $ANTLR start "LBRAT"
	public final void mLBRAT() throws RecognitionException {
		try {
			int _type = LBRAT;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:124:9: ( '[' )
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:124:17: '['
			{
			match('['); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "LBRAT"

	// $ANTLR start "RBRAT"
	public final void mRBRAT() throws RecognitionException {
		try {
			int _type = RBRAT;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:125:9: ( ']' )
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:125:17: ']'
			{
			match(']'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "RBRAT"

	// $ANTLR start "U"
	public final void mU() throws RecognitionException {
		try {
			int _type = U;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:126:9: ( 'U' )
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:126:11: 'U'
			{
			match('U'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "U"

	// $ANTLR start "F"
	public final void mF() throws RecognitionException {
		try {
			int _type = F;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:127:9: ( 'F' )
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:127:11: 'F'
			{
			match('F'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "F"

	// $ANTLR start "G"
	public final void mG() throws RecognitionException {
		try {
			int _type = G;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:128:9: ( 'G' )
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:128:11: 'G'
			{
			match('G'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "G"

	// $ANTLR start "TRUE"
	public final void mTRUE() throws RecognitionException {
		try {
			int _type = TRUE;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:130:6: ( 'true' )
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:130:8: 'true'
			{
			match("true"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "TRUE"

	// $ANTLR start "FALSE"
	public final void mFALSE() throws RecognitionException {
		try {
			int _type = FALSE;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:131:7: ( 'false' )
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:131:9: 'false'
			{
			match("false"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "FALSE"

	// $ANTLR start "PLUS"
	public final void mPLUS() throws RecognitionException {
		try {
			int _type = PLUS;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:133:6: ( '+' )
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:133:8: '+'
			{
			match('+'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "PLUS"

	// $ANTLR start "MINUS"
	public final void mMINUS() throws RecognitionException {
		try {
			int _type = MINUS;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:134:7: ( '-' )
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:134:9: '-'
			{
			match('-'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "MINUS"

	// $ANTLR start "MULT"
	public final void mMULT() throws RecognitionException {
		try {
			int _type = MULT;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:135:6: ( '*' )
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:135:8: '*'
			{
			match('*'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "MULT"

	// $ANTLR start "DIV"
	public final void mDIV() throws RecognitionException {
		try {
			int _type = DIV;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:136:5: ( '/' )
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:136:7: '/'
			{
			match('/'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "DIV"

	// $ANTLR start "AND"
	public final void mAND() throws RecognitionException {
		try {
			int _type = AND;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:138:5: ( '&' )
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:138:7: '&'
			{
			match('&'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "AND"

	// $ANTLR start "OR"
	public final void mOR() throws RecognitionException {
		try {
			int _type = OR;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:139:4: ( '|' )
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:139:6: '|'
			{
			match('|'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "OR"

	// $ANTLR start "NOT"
	public final void mNOT() throws RecognitionException {
		try {
			int _type = NOT;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:140:5: ( '!' )
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:140:7: '!'
			{
			match('!'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "NOT"

	// $ANTLR start "EQ"
	public final void mEQ() throws RecognitionException {
		try {
			int _type = EQ;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:142:4: ( '=' )
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:142:6: '='
			{
			match('='); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "EQ"

	// $ANTLR start "NEQ"
	public final void mNEQ() throws RecognitionException {
		try {
			int _type = NEQ;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:143:5: ( '!=' )
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:143:7: '!='
			{
			match("!="); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "NEQ"

	// $ANTLR start "GT"
	public final void mGT() throws RecognitionException {
		try {
			int _type = GT;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:144:4: ( '>' )
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:144:6: '>'
			{
			match('>'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "GT"

	// $ANTLR start "GE"
	public final void mGE() throws RecognitionException {
		try {
			int _type = GE;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:145:4: ( '>=' )
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:145:6: '>='
			{
			match(">="); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "GE"

	// $ANTLR start "LT"
	public final void mLT() throws RecognitionException {
		try {
			int _type = LT;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:146:4: ( '<' )
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:146:6: '<'
			{
			match('<'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "LT"

	// $ANTLR start "LE"
	public final void mLE() throws RecognitionException {
		try {
			int _type = LE;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:147:4: ( '<=' )
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:147:6: '<='
			{
			match("<="); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "LE"

	// $ANTLR start "INTEGER"
	public final void mINTEGER() throws RecognitionException {
		try {
			int _type = INTEGER;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:153:9: ( ( '0' .. '9' )+ )
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:153:11: ( '0' .. '9' )+
			{
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:153:11: ( '0' .. '9' )+
			int cnt1=0;
			loop1:
			while (true) {
				int alt1=2;
				int LA1_0 = input.LA(1);
				if ( ((LA1_0 >= '0' && LA1_0 <= '9')) ) {
					alt1=1;
				}

				switch (alt1) {
				case 1 :
					// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:
					{
					if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					if ( cnt1 >= 1 ) break loop1;
					EarlyExitException eee = new EarlyExitException(1, input);
					throw eee;
				}
				cnt1++;
			}

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "INTEGER"

	// $ANTLR start "FLOAT"
	public final void mFLOAT() throws RecognitionException {
		try {
			int _type = FLOAT;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:157:2: ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( EXPONENT )? | '.' ( '0' .. '9' )+ ( EXPONENT )? | ( '0' .. '9' )+ EXPONENT )
			int alt8=3;
			alt8 = dfa8.predict(input);
			switch (alt8) {
				case 1 :
					// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:157:6: ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( EXPONENT )?
					{
					// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:157:6: ( '0' .. '9' )+
					int cnt2=0;
					loop2:
					while (true) {
						int alt2=2;
						int LA2_0 = input.LA(1);
						if ( ((LA2_0 >= '0' && LA2_0 <= '9')) ) {
							alt2=1;
						}

						switch (alt2) {
						case 1 :
							// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:
							{
							if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
								input.consume();
							}
							else {
								MismatchedSetException mse = new MismatchedSetException(null,input);
								recover(mse);
								throw mse;
							}
							}
							break;

						default :
							if ( cnt2 >= 1 ) break loop2;
							EarlyExitException eee = new EarlyExitException(2, input);
							throw eee;
						}
						cnt2++;
					}

					match('.'); 
					// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:157:22: ( '0' .. '9' )*
					loop3:
					while (true) {
						int alt3=2;
						int LA3_0 = input.LA(1);
						if ( ((LA3_0 >= '0' && LA3_0 <= '9')) ) {
							alt3=1;
						}

						switch (alt3) {
						case 1 :
							// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:
							{
							if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
								input.consume();
							}
							else {
								MismatchedSetException mse = new MismatchedSetException(null,input);
								recover(mse);
								throw mse;
							}
							}
							break;

						default :
							break loop3;
						}
					}

					// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:157:34: ( EXPONENT )?
					int alt4=2;
					int LA4_0 = input.LA(1);
					if ( (LA4_0=='E'||LA4_0=='e') ) {
						alt4=1;
					}
					switch (alt4) {
						case 1 :
							// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:157:34: EXPONENT
							{
							mEXPONENT(); 

							}
							break;

					}

					}
					break;
				case 2 :
					// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:158:6: '.' ( '0' .. '9' )+ ( EXPONENT )?
					{
					match('.'); 
					// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:158:10: ( '0' .. '9' )+
					int cnt5=0;
					loop5:
					while (true) {
						int alt5=2;
						int LA5_0 = input.LA(1);
						if ( ((LA5_0 >= '0' && LA5_0 <= '9')) ) {
							alt5=1;
						}

						switch (alt5) {
						case 1 :
							// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:
							{
							if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
								input.consume();
							}
							else {
								MismatchedSetException mse = new MismatchedSetException(null,input);
								recover(mse);
								throw mse;
							}
							}
							break;

						default :
							if ( cnt5 >= 1 ) break loop5;
							EarlyExitException eee = new EarlyExitException(5, input);
							throw eee;
						}
						cnt5++;
					}

					// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:158:22: ( EXPONENT )?
					int alt6=2;
					int LA6_0 = input.LA(1);
					if ( (LA6_0=='E'||LA6_0=='e') ) {
						alt6=1;
					}
					switch (alt6) {
						case 1 :
							// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:158:22: EXPONENT
							{
							mEXPONENT(); 

							}
							break;

					}

					}
					break;
				case 3 :
					// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:159:6: ( '0' .. '9' )+ EXPONENT
					{
					// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:159:6: ( '0' .. '9' )+
					int cnt7=0;
					loop7:
					while (true) {
						int alt7=2;
						int LA7_0 = input.LA(1);
						if ( ((LA7_0 >= '0' && LA7_0 <= '9')) ) {
							alt7=1;
						}

						switch (alt7) {
						case 1 :
							// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:
							{
							if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
								input.consume();
							}
							else {
								MismatchedSetException mse = new MismatchedSetException(null,input);
								recover(mse);
								throw mse;
							}
							}
							break;

						default :
							if ( cnt7 >= 1 ) break loop7;
							EarlyExitException eee = new EarlyExitException(7, input);
							throw eee;
						}
						cnt7++;
					}

					mEXPONENT(); 

					}
					break;

			}
			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "FLOAT"

	// $ANTLR start "EXPONENT"
	public final void mEXPONENT() throws RecognitionException {
		try {
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:164:10: ( ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+ )
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:164:12: ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+
			{
			if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
				input.consume();
			}
			else {
				MismatchedSetException mse = new MismatchedSetException(null,input);
				recover(mse);
				throw mse;
			}
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:164:22: ( '+' | '-' )?
			int alt9=2;
			int LA9_0 = input.LA(1);
			if ( (LA9_0=='+'||LA9_0=='-') ) {
				alt9=1;
			}
			switch (alt9) {
				case 1 :
					// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:
					{
					if ( input.LA(1)=='+'||input.LA(1)=='-' ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

			}

			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:164:33: ( '0' .. '9' )+
			int cnt10=0;
			loop10:
			while (true) {
				int alt10=2;
				int LA10_0 = input.LA(1);
				if ( ((LA10_0 >= '0' && LA10_0 <= '9')) ) {
					alt10=1;
				}

				switch (alt10) {
				case 1 :
					// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:
					{
					if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					if ( cnt10 >= 1 ) break loop10;
					EarlyExitException eee = new EarlyExitException(10, input);
					throw eee;
				}
				cnt10++;
			}

			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "EXPONENT"

	// $ANTLR start "ID"
	public final void mID() throws RecognitionException {
		try {
			int _type = ID;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:167:4: ( ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )* )
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:167:6: ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )*
			{
			if ( (input.LA(1) >= 'A' && input.LA(1) <= 'Z')||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
				input.consume();
			}
			else {
				MismatchedSetException mse = new MismatchedSetException(null,input);
				recover(mse);
				throw mse;
			}
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:167:30: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )*
			loop11:
			while (true) {
				int alt11=2;
				int LA11_0 = input.LA(1);
				if ( ((LA11_0 >= '0' && LA11_0 <= '9')||(LA11_0 >= 'A' && LA11_0 <= 'Z')||LA11_0=='_'||(LA11_0 >= 'a' && LA11_0 <= 'z')) ) {
					alt11=1;
				}

				switch (alt11) {
				case 1 :
					// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:
					{
					if ( (input.LA(1) >= '0' && input.LA(1) <= '9')||(input.LA(1) >= 'A' && input.LA(1) <= 'Z')||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					break loop11;
				}
			}

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "ID"

	// $ANTLR start "NEWLINE"
	public final void mNEWLINE() throws RecognitionException {
		try {
			int _type = NEWLINE;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:170:9: ( ( '\\r' | '\\n' ) )
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:
			{
			if ( input.LA(1)=='\n'||input.LA(1)=='\r' ) {
				input.consume();
			}
			else {
				MismatchedSetException mse = new MismatchedSetException(null,input);
				recover(mse);
				throw mse;
			}
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "NEWLINE"

	// $ANTLR start "COMMENT"
	public final void mCOMMENT() throws RecognitionException {
		try {
			int _type = COMMENT;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:181:5: ( '//' (~ ( '\\n' | '\\r' ) )* )
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:181:9: '//' (~ ( '\\n' | '\\r' ) )*
			{
			match("//"); 

			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:181:14: (~ ( '\\n' | '\\r' ) )*
			loop12:
			while (true) {
				int alt12=2;
				int LA12_0 = input.LA(1);
				if ( ((LA12_0 >= '\u0000' && LA12_0 <= '\t')||(LA12_0 >= '\u000B' && LA12_0 <= '\f')||(LA12_0 >= '\u000E' && LA12_0 <= '\uFFFF')) ) {
					alt12=1;
				}

				switch (alt12) {
				case 1 :
					// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:
					{
					if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '\t')||(input.LA(1) >= '\u000B' && input.LA(1) <= '\f')||(input.LA(1) >= '\u000E' && input.LA(1) <= '\uFFFF') ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					break loop12;
				}
			}

			_channel=HIDDEN;
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "COMMENT"

	// $ANTLR start "WS"
	public final void mWS() throws RecognitionException {
		try {
			int _type = WS;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:186:5: ( ( ' ' | '\\t' | NEWLINE ) )
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:186:9: ( ' ' | '\\t' | NEWLINE )
			{
			if ( (input.LA(1) >= '\t' && input.LA(1) <= '\n')||input.LA(1)=='\r'||input.LA(1)==' ' ) {
				input.consume();
			}
			else {
				MismatchedSetException mse = new MismatchedSetException(null,input);
				recover(mse);
				throw mse;
			}
			_channel=HIDDEN;
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "WS"

	@Override
	public void mTokens() throws RecognitionException {
		// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:1:8: ( CONST | INT | DOUBLE | BOOL | COMMA | SEMICOLON | LPAR | RPAR | LBRAT | RBRAT | U | F | G | TRUE | FALSE | PLUS | MINUS | MULT | DIV | AND | OR | NOT | EQ | NEQ | GT | GE | LT | LE | INTEGER | FLOAT | ID | NEWLINE | COMMENT | WS )
		int alt13=34;
		alt13 = dfa13.predict(input);
		switch (alt13) {
			case 1 :
				// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:1:10: CONST
				{
				mCONST(); 

				}
				break;
			case 2 :
				// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:1:16: INT
				{
				mINT(); 

				}
				break;
			case 3 :
				// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:1:20: DOUBLE
				{
				mDOUBLE(); 

				}
				break;
			case 4 :
				// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:1:27: BOOL
				{
				mBOOL(); 

				}
				break;
			case 5 :
				// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:1:32: COMMA
				{
				mCOMMA(); 

				}
				break;
			case 6 :
				// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:1:38: SEMICOLON
				{
				mSEMICOLON(); 

				}
				break;
			case 7 :
				// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:1:48: LPAR
				{
				mLPAR(); 

				}
				break;
			case 8 :
				// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:1:53: RPAR
				{
				mRPAR(); 

				}
				break;
			case 9 :
				// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:1:58: LBRAT
				{
				mLBRAT(); 

				}
				break;
			case 10 :
				// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:1:64: RBRAT
				{
				mRBRAT(); 

				}
				break;
			case 11 :
				// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:1:70: U
				{
				mU(); 

				}
				break;
			case 12 :
				// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:1:72: F
				{
				mF(); 

				}
				break;
			case 13 :
				// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:1:74: G
				{
				mG(); 

				}
				break;
			case 14 :
				// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:1:76: TRUE
				{
				mTRUE(); 

				}
				break;
			case 15 :
				// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:1:81: FALSE
				{
				mFALSE(); 

				}
				break;
			case 16 :
				// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:1:87: PLUS
				{
				mPLUS(); 

				}
				break;
			case 17 :
				// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:1:92: MINUS
				{
				mMINUS(); 

				}
				break;
			case 18 :
				// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:1:98: MULT
				{
				mMULT(); 

				}
				break;
			case 19 :
				// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:1:103: DIV
				{
				mDIV(); 

				}
				break;
			case 20 :
				// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:1:107: AND
				{
				mAND(); 

				}
				break;
			case 21 :
				// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:1:111: OR
				{
				mOR(); 

				}
				break;
			case 22 :
				// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:1:114: NOT
				{
				mNOT(); 

				}
				break;
			case 23 :
				// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:1:118: EQ
				{
				mEQ(); 

				}
				break;
			case 24 :
				// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:1:121: NEQ
				{
				mNEQ(); 

				}
				break;
			case 25 :
				// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:1:125: GT
				{
				mGT(); 

				}
				break;
			case 26 :
				// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:1:128: GE
				{
				mGE(); 

				}
				break;
			case 27 :
				// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:1:131: LT
				{
				mLT(); 

				}
				break;
			case 28 :
				// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:1:134: LE
				{
				mLE(); 

				}
				break;
			case 29 :
				// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:1:137: INTEGER
				{
				mINTEGER(); 

				}
				break;
			case 30 :
				// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:1:145: FLOAT
				{
				mFLOAT(); 

				}
				break;
			case 31 :
				// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:1:151: ID
				{
				mID(); 

				}
				break;
			case 32 :
				// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:1:154: NEWLINE
				{
				mNEWLINE(); 

				}
				break;
			case 33 :
				// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:1:162: COMMENT
				{
				mCOMMENT(); 

				}
				break;
			case 34 :
				// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:1:170: WS
				{
				mWS(); 

				}
				break;

		}
	}


	protected DFA8 dfa8 = new DFA8(this);
	protected DFA13 dfa13 = new DFA13(this);
	static final String DFA8_eotS =
		"\5\uffff";
	static final String DFA8_eofS =
		"\5\uffff";
	static final String DFA8_minS =
		"\2\56\3\uffff";
	static final String DFA8_maxS =
		"\1\71\1\145\3\uffff";
	static final String DFA8_acceptS =
		"\2\uffff\1\2\1\1\1\3";
	static final String DFA8_specialS =
		"\5\uffff}>";
	static final String[] DFA8_transitionS = {
			"\1\2\1\uffff\12\1",
			"\1\3\1\uffff\12\1\13\uffff\1\4\37\uffff\1\4",
			"",
			"",
			""
	};

	static final short[] DFA8_eot = DFA.unpackEncodedString(DFA8_eotS);
	static final short[] DFA8_eof = DFA.unpackEncodedString(DFA8_eofS);
	static final char[] DFA8_min = DFA.unpackEncodedStringToUnsignedChars(DFA8_minS);
	static final char[] DFA8_max = DFA.unpackEncodedStringToUnsignedChars(DFA8_maxS);
	static final short[] DFA8_accept = DFA.unpackEncodedString(DFA8_acceptS);
	static final short[] DFA8_special = DFA.unpackEncodedString(DFA8_specialS);
	static final short[][] DFA8_transition;

	static {
		int numStates = DFA8_transitionS.length;
		DFA8_transition = new short[numStates][];
		for (int i=0; i<numStates; i++) {
			DFA8_transition[i] = DFA.unpackEncodedString(DFA8_transitionS[i]);
		}
	}

	protected class DFA8 extends DFA {

		public DFA8(BaseRecognizer recognizer) {
			this.recognizer = recognizer;
			this.decisionNumber = 8;
			this.eot = DFA8_eot;
			this.eof = DFA8_eof;
			this.min = DFA8_min;
			this.max = DFA8_max;
			this.accept = DFA8_accept;
			this.special = DFA8_special;
			this.transition = DFA8_transition;
		}
		@Override
		public String getDescription() {
			return "156:1: FLOAT : ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( EXPONENT )? | '.' ( '0' .. '9' )+ ( EXPONENT )? | ( '0' .. '9' )+ EXPONENT );";
		}
	}

	static final String DFA13_eotS =
		"\1\uffff\4\34\6\uffff\1\43\1\44\1\45\2\34\3\uffff\1\51\2\uffff\1\53\1"+
		"\uffff\1\55\1\57\1\60\4\uffff\4\34\3\uffff\2\34\12\uffff\1\34\1\71\5\34"+
		"\1\uffff\1\34\1\100\1\101\1\34\1\103\1\34\2\uffff\1\105\1\uffff\1\106"+
		"\2\uffff";
	static final String DFA13_eofS =
		"\107\uffff";
	static final String DFA13_minS =
		"\1\11\1\157\1\156\2\157\6\uffff\3\60\1\162\1\141\3\uffff\1\57\2\uffff"+
		"\1\75\1\uffff\2\75\1\56\4\uffff\1\156\1\164\1\165\1\157\3\uffff\1\165"+
		"\1\154\12\uffff\1\163\1\60\1\142\1\154\1\145\1\163\1\164\1\uffff\1\154"+
		"\2\60\1\145\1\60\1\145\2\uffff\1\60\1\uffff\1\60\2\uffff";
	static final String DFA13_maxS =
		"\1\174\1\157\1\156\2\157\6\uffff\3\172\1\162\1\141\3\uffff\1\57\2\uffff"+
		"\1\75\1\uffff\2\75\1\145\4\uffff\1\156\1\164\1\165\1\157\3\uffff\1\165"+
		"\1\154\12\uffff\1\163\1\172\1\142\1\154\1\145\1\163\1\164\1\uffff\1\154"+
		"\2\172\1\145\1\172\1\145\2\uffff\1\172\1\uffff\1\172\2\uffff";
	static final String DFA13_acceptS =
		"\5\uffff\1\5\1\6\1\7\1\10\1\11\1\12\5\uffff\1\20\1\21\1\22\1\uffff\1\24"+
		"\1\25\1\uffff\1\27\3\uffff\1\36\1\37\1\40\1\42\4\uffff\1\13\1\14\1\15"+
		"\2\uffff\1\41\1\23\1\30\1\26\1\32\1\31\1\34\1\33\1\35\1\40\7\uffff\1\2"+
		"\6\uffff\1\4\1\16\1\uffff\1\1\1\uffff\1\17\1\3";
	static final String DFA13_specialS =
		"\107\uffff}>";
	static final String[] DFA13_transitionS = {
			"\1\36\1\35\2\uffff\1\35\22\uffff\1\36\1\26\4\uffff\1\24\1\uffff\1\7\1"+
			"\10\1\22\1\20\1\5\1\21\1\33\1\23\12\32\1\uffff\1\6\1\31\1\27\1\30\2\uffff"+
			"\5\34\1\14\1\15\15\34\1\13\5\34\1\11\1\uffff\1\12\1\uffff\1\34\1\uffff"+
			"\1\34\1\4\1\1\1\3\1\34\1\17\2\34\1\2\12\34\1\16\6\34\1\uffff\1\25",
			"\1\37",
			"\1\40",
			"\1\41",
			"\1\42",
			"",
			"",
			"",
			"",
			"",
			"",
			"\12\34\7\uffff\32\34\4\uffff\1\34\1\uffff\32\34",
			"\12\34\7\uffff\32\34\4\uffff\1\34\1\uffff\32\34",
			"\12\34\7\uffff\32\34\4\uffff\1\34\1\uffff\32\34",
			"\1\46",
			"\1\47",
			"",
			"",
			"",
			"\1\50",
			"",
			"",
			"\1\52",
			"",
			"\1\54",
			"\1\56",
			"\1\33\1\uffff\12\32\13\uffff\1\33\37\uffff\1\33",
			"",
			"",
			"",
			"",
			"\1\62",
			"\1\63",
			"\1\64",
			"\1\65",
			"",
			"",
			"",
			"\1\66",
			"\1\67",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"\1\70",
			"\12\34\7\uffff\32\34\4\uffff\1\34\1\uffff\32\34",
			"\1\72",
			"\1\73",
			"\1\74",
			"\1\75",
			"\1\76",
			"",
			"\1\77",
			"\12\34\7\uffff\32\34\4\uffff\1\34\1\uffff\32\34",
			"\12\34\7\uffff\32\34\4\uffff\1\34\1\uffff\32\34",
			"\1\102",
			"\12\34\7\uffff\32\34\4\uffff\1\34\1\uffff\32\34",
			"\1\104",
			"",
			"",
			"\12\34\7\uffff\32\34\4\uffff\1\34\1\uffff\32\34",
			"",
			"\12\34\7\uffff\32\34\4\uffff\1\34\1\uffff\32\34",
			"",
			""
	};

	static final short[] DFA13_eot = DFA.unpackEncodedString(DFA13_eotS);
	static final short[] DFA13_eof = DFA.unpackEncodedString(DFA13_eofS);
	static final char[] DFA13_min = DFA.unpackEncodedStringToUnsignedChars(DFA13_minS);
	static final char[] DFA13_max = DFA.unpackEncodedStringToUnsignedChars(DFA13_maxS);
	static final short[] DFA13_accept = DFA.unpackEncodedString(DFA13_acceptS);
	static final short[] DFA13_special = DFA.unpackEncodedString(DFA13_specialS);
	static final short[][] DFA13_transition;

	static {
		int numStates = DFA13_transitionS.length;
		DFA13_transition = new short[numStates][];
		for (int i=0; i<numStates; i++) {
			DFA13_transition[i] = DFA.unpackEncodedString(DFA13_transitionS[i]);
		}
	}

	protected class DFA13 extends DFA {

		public DFA13(BaseRecognizer recognizer) {
			this.recognizer = recognizer;
			this.decisionNumber = 13;
			this.eot = DFA13_eot;
			this.eof = DFA13_eof;
			this.min = DFA13_min;
			this.max = DFA13_max;
			this.accept = DFA13_accept;
			this.special = DFA13_special;
			this.transition = DFA13_transition;
		}
		@Override
		public String getDescription() {
			return "1:1: Tokens : ( CONST | INT | DOUBLE | BOOL | COMMA | SEMICOLON | LPAR | RPAR | LBRAT | RBRAT | U | F | G | TRUE | FALSE | PLUS | MINUS | MULT | DIV | AND | OR | NOT | EQ | NEQ | GT | GE | LT | LE | INTEGER | FLOAT | ID | NEWLINE | COMMENT | WS );";
		}
	}

}
