// $ANTLR 3.5-rc-2 /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g 2014-07-15 11:25:31
package parsers;

import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import org.antlr.runtime.tree.*;


@SuppressWarnings("all")
public class MiTLParser extends Parser {
	public static final String[] tokenNames = new String[] {
		"<invalid>", "<EOR>", "<DOWN>", "<UP>", "AND", "BOOL", "COMMA", "COMMENT", 
		"CONST", "DIV", "DOUBLE", "EQ", "EXPONENT", "F", "FALSE", "FLOAT", "G", 
		"GE", "GT", "ID", "INT", "INTEGER", "LBRAT", "LE", "LPAR", "LT", "MINUS", 
		"MULT", "NEQ", "NEWLINE", "NOT", "OR", "PLUS", "RBRAT", "RPAR", "SEMICOLON", 
		"TRUE", "U", "WS"
	};
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
	public Parser[] getDelegates() {
		return new Parser[] {};
	}

	// delegators


	public MiTLParser(TokenStream input) {
		this(input, new RecognizerSharedState());
	}
	public MiTLParser(TokenStream input, RecognizerSharedState state) {
		super(input, state);
	}

	protected TreeAdaptor adaptor = new CommonTreeAdaptor();

	public void setTreeAdaptor(TreeAdaptor adaptor) {
		this.adaptor = adaptor;
	}
	public TreeAdaptor getTreeAdaptor() {
		return adaptor;
	}
	@Override public String[] getTokenNames() { return MiTLParser.tokenNames; }
	@Override public String getGrammarFileName() { return "/home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g"; }


	public static class eval_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "eval"
	// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:10:1: eval : statementList ;
	public final MiTLParser.eval_return eval() throws RecognitionException {
		MiTLParser.eval_return retval = new MiTLParser.eval_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		ParserRuleReturnScope statementList1 =null;


		try {
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:10:6: ( statementList )
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:10:8: statementList
			{
			root_0 = (Object)adaptor.nil();


			pushFollow(FOLLOW_statementList_in_eval29);
			statementList1=statementList();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) adaptor.addChild(root_0, statementList1.getTree());

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "eval"


	public static class statementList_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "statementList"
	// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:14:1: statementList : ( NEWLINE !)* ( statement )+ ;
	public final MiTLParser.statementList_return statementList() throws RecognitionException {
		MiTLParser.statementList_return retval = new MiTLParser.statementList_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token NEWLINE2=null;
		ParserRuleReturnScope statement3 =null;

		Object NEWLINE2_tree=null;

		try {
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:15:2: ( ( NEWLINE !)* ( statement )+ )
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:15:4: ( NEWLINE !)* ( statement )+
			{
			root_0 = (Object)adaptor.nil();


			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:15:11: ( NEWLINE !)*
			loop1:
			while (true) {
				int alt1=2;
				int LA1_0 = input.LA(1);
				if ( (LA1_0==NEWLINE) ) {
					alt1=1;
				}

				switch (alt1) {
				case 1 :
					// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:15:11: NEWLINE !
					{
					NEWLINE2=(Token)match(input,NEWLINE,FOLLOW_NEWLINE_in_statementList40); if (state.failed) return retval;
					}
					break;

				default :
					break loop1;
				}
			}

			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:15:14: ( statement )+
			int cnt2=0;
			loop2:
			while (true) {
				int alt2=2;
				int LA2_0 = input.LA(1);
				if ( (LA2_0==CONST||(LA2_0 >= F && LA2_0 <= G)||LA2_0==ID||LA2_0==INTEGER||LA2_0==LPAR||LA2_0==MINUS||LA2_0==NOT||LA2_0==TRUE) ) {
					alt2=1;
				}

				switch (alt2) {
				case 1 :
					// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:15:14: statement
					{
					pushFollow(FOLLOW_statement_in_statementList44);
					statement3=statement();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, statement3.getTree());

					}
					break;

				default :
					if ( cnt2 >= 1 ) break loop2;
					if (state.backtracking>0) {state.failed=true; return retval;}
					EarlyExitException eee = new EarlyExitException(2, input);
					throw eee;
				}
				cnt2++;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "statementList"


	public static class statement_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "statement"
	// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:18:1: statement : ( declaration ( NEWLINE !)+ | exprOR ( NEWLINE !)+ );
	public final MiTLParser.statement_return statement() throws RecognitionException {
		MiTLParser.statement_return retval = new MiTLParser.statement_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token NEWLINE5=null;
		Token NEWLINE7=null;
		ParserRuleReturnScope declaration4 =null;
		ParserRuleReturnScope exprOR6 =null;

		Object NEWLINE5_tree=null;
		Object NEWLINE7_tree=null;

		try {
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:19:2: ( declaration ( NEWLINE !)+ | exprOR ( NEWLINE !)+ )
			int alt5=2;
			int LA5_0 = input.LA(1);
			if ( (LA5_0==CONST) ) {
				alt5=1;
			}
			else if ( ((LA5_0 >= F && LA5_0 <= G)||LA5_0==ID||LA5_0==INTEGER||LA5_0==LPAR||LA5_0==MINUS||LA5_0==NOT||LA5_0==TRUE) ) {
				alt5=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 5, 0, input);
				throw nvae;
			}

			switch (alt5) {
				case 1 :
					// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:19:4: declaration ( NEWLINE !)+
					{
					root_0 = (Object)adaptor.nil();


					pushFollow(FOLLOW_declaration_in_statement56);
					declaration4=declaration();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, declaration4.getTree());

					// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:19:23: ( NEWLINE !)+
					int cnt3=0;
					loop3:
					while (true) {
						int alt3=2;
						int LA3_0 = input.LA(1);
						if ( (LA3_0==NEWLINE) ) {
							alt3=1;
						}

						switch (alt3) {
						case 1 :
							// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:19:23: NEWLINE !
							{
							NEWLINE5=(Token)match(input,NEWLINE,FOLLOW_NEWLINE_in_statement58); if (state.failed) return retval;
							}
							break;

						default :
							if ( cnt3 >= 1 ) break loop3;
							if (state.backtracking>0) {state.failed=true; return retval;}
							EarlyExitException eee = new EarlyExitException(3, input);
							throw eee;
						}
						cnt3++;
					}

					}
					break;
				case 2 :
					// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:20:4: exprOR ( NEWLINE !)+
					{
					root_0 = (Object)adaptor.nil();


					pushFollow(FOLLOW_exprOR_in_statement65);
					exprOR6=exprOR();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, exprOR6.getTree());

					// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:20:18: ( NEWLINE !)+
					int cnt4=0;
					loop4:
					while (true) {
						int alt4=2;
						int LA4_0 = input.LA(1);
						if ( (LA4_0==NEWLINE) ) {
							alt4=1;
						}

						switch (alt4) {
						case 1 :
							// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:20:18: NEWLINE !
							{
							NEWLINE7=(Token)match(input,NEWLINE,FOLLOW_NEWLINE_in_statement67); if (state.failed) return retval;
							}
							break;

						default :
							if ( cnt4 >= 1 ) break loop4;
							if (state.backtracking>0) {state.failed=true; return retval;}
							EarlyExitException eee = new EarlyExitException(4, input);
							throw eee;
						}
						cnt4++;
					}

					}
					break;

			}
			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "statement"


	public static class declaration_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "declaration"
	// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:23:1: declaration : ( CONST ^ DOUBLE ID ( '=' ! ( INTEGER | FLOAT ) )? SEMICOLON !| CONST ^ INT ID ( '=' ! INTEGER )? SEMICOLON !| CONST ^ BOOL ID ( '=' ! ( TRUE | FALSE ) )? SEMICOLON !);
	public final MiTLParser.declaration_return declaration() throws RecognitionException {
		MiTLParser.declaration_return retval = new MiTLParser.declaration_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token CONST8=null;
		Token DOUBLE9=null;
		Token ID10=null;
		Token char_literal11=null;
		Token set12=null;
		Token SEMICOLON13=null;
		Token CONST14=null;
		Token INT15=null;
		Token ID16=null;
		Token char_literal17=null;
		Token INTEGER18=null;
		Token SEMICOLON19=null;
		Token CONST20=null;
		Token BOOL21=null;
		Token ID22=null;
		Token char_literal23=null;
		Token set24=null;
		Token SEMICOLON25=null;

		Object CONST8_tree=null;
		Object DOUBLE9_tree=null;
		Object ID10_tree=null;
		Object char_literal11_tree=null;
		Object set12_tree=null;
		Object SEMICOLON13_tree=null;
		Object CONST14_tree=null;
		Object INT15_tree=null;
		Object ID16_tree=null;
		Object char_literal17_tree=null;
		Object INTEGER18_tree=null;
		Object SEMICOLON19_tree=null;
		Object CONST20_tree=null;
		Object BOOL21_tree=null;
		Object ID22_tree=null;
		Object char_literal23_tree=null;
		Object set24_tree=null;
		Object SEMICOLON25_tree=null;

		try {
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:24:2: ( CONST ^ DOUBLE ID ( '=' ! ( INTEGER | FLOAT ) )? SEMICOLON !| CONST ^ INT ID ( '=' ! INTEGER )? SEMICOLON !| CONST ^ BOOL ID ( '=' ! ( TRUE | FALSE ) )? SEMICOLON !)
			int alt9=3;
			int LA9_0 = input.LA(1);
			if ( (LA9_0==CONST) ) {
				switch ( input.LA(2) ) {
				case DOUBLE:
					{
					alt9=1;
					}
					break;
				case INT:
					{
					alt9=2;
					}
					break;
				case BOOL:
					{
					alt9=3;
					}
					break;
				default:
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 9, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}
			}

			else {
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 9, 0, input);
				throw nvae;
			}

			switch (alt9) {
				case 1 :
					// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:24:4: CONST ^ DOUBLE ID ( '=' ! ( INTEGER | FLOAT ) )? SEMICOLON !
					{
					root_0 = (Object)adaptor.nil();


					CONST8=(Token)match(input,CONST,FOLLOW_CONST_in_declaration80); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					CONST8_tree = (Object)adaptor.create(CONST8);
					root_0 = (Object)adaptor.becomeRoot(CONST8_tree, root_0);
					}

					DOUBLE9=(Token)match(input,DOUBLE,FOLLOW_DOUBLE_in_declaration83); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					DOUBLE9_tree = (Object)adaptor.create(DOUBLE9);
					adaptor.addChild(root_0, DOUBLE9_tree);
					}

					ID10=(Token)match(input,ID,FOLLOW_ID_in_declaration85); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					ID10_tree = (Object)adaptor.create(ID10);
					adaptor.addChild(root_0, ID10_tree);
					}

					// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:24:21: ( '=' ! ( INTEGER | FLOAT ) )?
					int alt6=2;
					int LA6_0 = input.LA(1);
					if ( (LA6_0==EQ) ) {
						alt6=1;
					}
					switch (alt6) {
						case 1 :
							// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:24:22: '=' ! ( INTEGER | FLOAT )
							{
							char_literal11=(Token)match(input,EQ,FOLLOW_EQ_in_declaration88); if (state.failed) return retval;
							set12=input.LT(1);
							if ( input.LA(1)==FLOAT||input.LA(1)==INTEGER ) {
								input.consume();
								if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set12));
								state.errorRecovery=false;
								state.failed=false;
							}
							else {
								if (state.backtracking>0) {state.failed=true; return retval;}
								MismatchedSetException mse = new MismatchedSetException(null,input);
								throw mse;
							}
							}
							break;

					}

					SEMICOLON13=(Token)match(input,SEMICOLON,FOLLOW_SEMICOLON_in_declaration101); if (state.failed) return retval;
					}
					break;
				case 2 :
					// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:25:4: CONST ^ INT ID ( '=' ! INTEGER )? SEMICOLON !
					{
					root_0 = (Object)adaptor.nil();


					CONST14=(Token)match(input,CONST,FOLLOW_CONST_in_declaration107); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					CONST14_tree = (Object)adaptor.create(CONST14);
					root_0 = (Object)adaptor.becomeRoot(CONST14_tree, root_0);
					}

					INT15=(Token)match(input,INT,FOLLOW_INT_in_declaration110); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					INT15_tree = (Object)adaptor.create(INT15);
					adaptor.addChild(root_0, INT15_tree);
					}

					ID16=(Token)match(input,ID,FOLLOW_ID_in_declaration112); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					ID16_tree = (Object)adaptor.create(ID16);
					adaptor.addChild(root_0, ID16_tree);
					}

					// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:25:18: ( '=' ! INTEGER )?
					int alt7=2;
					int LA7_0 = input.LA(1);
					if ( (LA7_0==EQ) ) {
						alt7=1;
					}
					switch (alt7) {
						case 1 :
							// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:25:19: '=' ! INTEGER
							{
							char_literal17=(Token)match(input,EQ,FOLLOW_EQ_in_declaration115); if (state.failed) return retval;
							INTEGER18=(Token)match(input,INTEGER,FOLLOW_INTEGER_in_declaration118); if (state.failed) return retval;
							if ( state.backtracking==0 ) {
							INTEGER18_tree = (Object)adaptor.create(INTEGER18);
							adaptor.addChild(root_0, INTEGER18_tree);
							}

							}
							break;

					}

					SEMICOLON19=(Token)match(input,SEMICOLON,FOLLOW_SEMICOLON_in_declaration122); if (state.failed) return retval;
					}
					break;
				case 3 :
					// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:26:4: CONST ^ BOOL ID ( '=' ! ( TRUE | FALSE ) )? SEMICOLON !
					{
					root_0 = (Object)adaptor.nil();


					CONST20=(Token)match(input,CONST,FOLLOW_CONST_in_declaration128); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					CONST20_tree = (Object)adaptor.create(CONST20);
					root_0 = (Object)adaptor.becomeRoot(CONST20_tree, root_0);
					}

					BOOL21=(Token)match(input,BOOL,FOLLOW_BOOL_in_declaration131); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					BOOL21_tree = (Object)adaptor.create(BOOL21);
					adaptor.addChild(root_0, BOOL21_tree);
					}

					ID22=(Token)match(input,ID,FOLLOW_ID_in_declaration133); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					ID22_tree = (Object)adaptor.create(ID22);
					adaptor.addChild(root_0, ID22_tree);
					}

					// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:26:19: ( '=' ! ( TRUE | FALSE ) )?
					int alt8=2;
					int LA8_0 = input.LA(1);
					if ( (LA8_0==EQ) ) {
						alt8=1;
					}
					switch (alt8) {
						case 1 :
							// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:26:20: '=' ! ( TRUE | FALSE )
							{
							char_literal23=(Token)match(input,EQ,FOLLOW_EQ_in_declaration136); if (state.failed) return retval;
							set24=input.LT(1);
							if ( input.LA(1)==FALSE||input.LA(1)==TRUE ) {
								input.consume();
								if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set24));
								state.errorRecovery=false;
								state.failed=false;
							}
							else {
								if (state.backtracking>0) {state.failed=true; return retval;}
								MismatchedSetException mse = new MismatchedSetException(null,input);
								throw mse;
							}
							}
							break;

					}

					SEMICOLON25=(Token)match(input,SEMICOLON,FOLLOW_SEMICOLON_in_declaration149); if (state.failed) return retval;
					}
					break;

			}
			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "declaration"


	public static class exprOR_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "exprOR"
	// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:33:1: exprOR : exprAND ( OR ^ exprAND )* ;
	public final MiTLParser.exprOR_return exprOR() throws RecognitionException {
		MiTLParser.exprOR_return retval = new MiTLParser.exprOR_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token OR27=null;
		ParserRuleReturnScope exprAND26 =null;
		ParserRuleReturnScope exprAND28 =null;

		Object OR27_tree=null;

		try {
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:34:2: ( exprAND ( OR ^ exprAND )* )
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:34:4: exprAND ( OR ^ exprAND )*
			{
			root_0 = (Object)adaptor.nil();


			pushFollow(FOLLOW_exprAND_in_exprOR166);
			exprAND26=exprAND();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) adaptor.addChild(root_0, exprAND26.getTree());

			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:34:12: ( OR ^ exprAND )*
			loop10:
			while (true) {
				int alt10=2;
				int LA10_0 = input.LA(1);
				if ( (LA10_0==OR) ) {
					alt10=1;
				}

				switch (alt10) {
				case 1 :
					// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:34:13: OR ^ exprAND
					{
					OR27=(Token)match(input,OR,FOLLOW_OR_in_exprOR169); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					OR27_tree = (Object)adaptor.create(OR27);
					root_0 = (Object)adaptor.becomeRoot(OR27_tree, root_0);
					}

					pushFollow(FOLLOW_exprAND_in_exprOR172);
					exprAND28=exprAND();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, exprAND28.getTree());

					}
					break;

				default :
					break loop10;
				}
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "exprOR"


	public static class exprAND_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "exprAND"
	// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:37:1: exprAND : mitlTerm ( AND ^ mitlTerm )* ;
	public final MiTLParser.exprAND_return exprAND() throws RecognitionException {
		MiTLParser.exprAND_return retval = new MiTLParser.exprAND_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token AND30=null;
		ParserRuleReturnScope mitlTerm29 =null;
		ParserRuleReturnScope mitlTerm31 =null;

		Object AND30_tree=null;

		try {
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:38:2: ( mitlTerm ( AND ^ mitlTerm )* )
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:38:4: mitlTerm ( AND ^ mitlTerm )*
			{
			root_0 = (Object)adaptor.nil();


			pushFollow(FOLLOW_mitlTerm_in_exprAND186);
			mitlTerm29=mitlTerm();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) adaptor.addChild(root_0, mitlTerm29.getTree());

			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:38:13: ( AND ^ mitlTerm )*
			loop11:
			while (true) {
				int alt11=2;
				int LA11_0 = input.LA(1);
				if ( (LA11_0==AND) ) {
					alt11=1;
				}

				switch (alt11) {
				case 1 :
					// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:38:14: AND ^ mitlTerm
					{
					AND30=(Token)match(input,AND,FOLLOW_AND_in_exprAND189); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					AND30_tree = (Object)adaptor.create(AND30);
					root_0 = (Object)adaptor.becomeRoot(AND30_tree, root_0);
					}

					pushFollow(FOLLOW_mitlTerm_in_exprAND192);
					mitlTerm31=mitlTerm();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, mitlTerm31.getTree());

					}
					break;

				default :
					break loop11;
				}
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "exprAND"


	public static class mitlTerm_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "mitlTerm"
	// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:43:1: mitlTerm : ( booelanAtomic ( U ^ timeBound booelanAtomic )? | F ^ timeBound booelanAtomic | G ^ timeBound booelanAtomic );
	public final MiTLParser.mitlTerm_return mitlTerm() throws RecognitionException {
		MiTLParser.mitlTerm_return retval = new MiTLParser.mitlTerm_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token U33=null;
		Token F36=null;
		Token G39=null;
		ParserRuleReturnScope booelanAtomic32 =null;
		ParserRuleReturnScope timeBound34 =null;
		ParserRuleReturnScope booelanAtomic35 =null;
		ParserRuleReturnScope timeBound37 =null;
		ParserRuleReturnScope booelanAtomic38 =null;
		ParserRuleReturnScope timeBound40 =null;
		ParserRuleReturnScope booelanAtomic41 =null;

		Object U33_tree=null;
		Object F36_tree=null;
		Object G39_tree=null;

		try {
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:43:10: ( booelanAtomic ( U ^ timeBound booelanAtomic )? | F ^ timeBound booelanAtomic | G ^ timeBound booelanAtomic )
			int alt13=3;
			switch ( input.LA(1) ) {
			case FALSE:
			case FLOAT:
			case ID:
			case INTEGER:
			case LPAR:
			case MINUS:
			case NOT:
			case TRUE:
				{
				alt13=1;
				}
				break;
			case F:
				{
				alt13=2;
				}
				break;
			case G:
				{
				alt13=3;
				}
				break;
			default:
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 13, 0, input);
				throw nvae;
			}
			switch (alt13) {
				case 1 :
					// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:43:12: booelanAtomic ( U ^ timeBound booelanAtomic )?
					{
					root_0 = (Object)adaptor.nil();


					pushFollow(FOLLOW_booelanAtomic_in_mitlTerm208);
					booelanAtomic32=booelanAtomic();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, booelanAtomic32.getTree());

					// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:43:26: ( U ^ timeBound booelanAtomic )?
					int alt12=2;
					int LA12_0 = input.LA(1);
					if ( (LA12_0==U) ) {
						alt12=1;
					}
					switch (alt12) {
						case 1 :
							// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:43:27: U ^ timeBound booelanAtomic
							{
							U33=(Token)match(input,U,FOLLOW_U_in_mitlTerm211); if (state.failed) return retval;
							if ( state.backtracking==0 ) {
							U33_tree = (Object)adaptor.create(U33);
							root_0 = (Object)adaptor.becomeRoot(U33_tree, root_0);
							}

							pushFollow(FOLLOW_timeBound_in_mitlTerm214);
							timeBound34=timeBound();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) adaptor.addChild(root_0, timeBound34.getTree());

							pushFollow(FOLLOW_booelanAtomic_in_mitlTerm216);
							booelanAtomic35=booelanAtomic();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) adaptor.addChild(root_0, booelanAtomic35.getTree());

							}
							break;

					}

					}
					break;
				case 2 :
					// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:44:5: F ^ timeBound booelanAtomic
					{
					root_0 = (Object)adaptor.nil();


					F36=(Token)match(input,F,FOLLOW_F_in_mitlTerm224); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					F36_tree = (Object)adaptor.create(F36);
					root_0 = (Object)adaptor.becomeRoot(F36_tree, root_0);
					}

					pushFollow(FOLLOW_timeBound_in_mitlTerm227);
					timeBound37=timeBound();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, timeBound37.getTree());

					pushFollow(FOLLOW_booelanAtomic_in_mitlTerm229);
					booelanAtomic38=booelanAtomic();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, booelanAtomic38.getTree());

					}
					break;
				case 3 :
					// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:45:5: G ^ timeBound booelanAtomic
					{
					root_0 = (Object)adaptor.nil();


					G39=(Token)match(input,G,FOLLOW_G_in_mitlTerm235); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					G39_tree = (Object)adaptor.create(G39);
					root_0 = (Object)adaptor.becomeRoot(G39_tree, root_0);
					}

					pushFollow(FOLLOW_timeBound_in_mitlTerm238);
					timeBound40=timeBound();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, timeBound40.getTree());

					pushFollow(FOLLOW_booelanAtomic_in_mitlTerm240);
					booelanAtomic41=booelanAtomic();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, booelanAtomic41.getTree());

					}
					break;

			}
			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "mitlTerm"


	public static class timeBound_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "timeBound"
	// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:52:1: timeBound : ( '<=' ! termExpr | LBRAT ! termExpr COMMA ^ termExpr RBRAT !| termExpr );
	public final MiTLParser.timeBound_return timeBound() throws RecognitionException {
		MiTLParser.timeBound_return retval = new MiTLParser.timeBound_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token string_literal42=null;
		Token LBRAT44=null;
		Token COMMA46=null;
		Token RBRAT48=null;
		ParserRuleReturnScope termExpr43 =null;
		ParserRuleReturnScope termExpr45 =null;
		ParserRuleReturnScope termExpr47 =null;
		ParserRuleReturnScope termExpr49 =null;

		Object string_literal42_tree=null;
		Object LBRAT44_tree=null;
		Object COMMA46_tree=null;
		Object RBRAT48_tree=null;

		try {
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:53:2: ( '<=' ! termExpr | LBRAT ! termExpr COMMA ^ termExpr RBRAT !| termExpr )
			int alt14=3;
			switch ( input.LA(1) ) {
			case LE:
				{
				alt14=1;
				}
				break;
			case LBRAT:
				{
				alt14=2;
				}
				break;
			case FLOAT:
			case ID:
			case INTEGER:
			case LPAR:
			case MINUS:
				{
				alt14=3;
				}
				break;
			default:
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 14, 0, input);
				throw nvae;
			}
			switch (alt14) {
				case 1 :
					// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:53:4: '<=' ! termExpr
					{
					root_0 = (Object)adaptor.nil();


					string_literal42=(Token)match(input,LE,FOLLOW_LE_in_timeBound256); if (state.failed) return retval;
					pushFollow(FOLLOW_termExpr_in_timeBound259);
					termExpr43=termExpr();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, termExpr43.getTree());

					}
					break;
				case 2 :
					// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:54:4: LBRAT ! termExpr COMMA ^ termExpr RBRAT !
					{
					root_0 = (Object)adaptor.nil();


					LBRAT44=(Token)match(input,LBRAT,FOLLOW_LBRAT_in_timeBound264); if (state.failed) return retval;
					pushFollow(FOLLOW_termExpr_in_timeBound267);
					termExpr45=termExpr();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, termExpr45.getTree());

					COMMA46=(Token)match(input,COMMA,FOLLOW_COMMA_in_timeBound269); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					COMMA46_tree = (Object)adaptor.create(COMMA46);
					root_0 = (Object)adaptor.becomeRoot(COMMA46_tree, root_0);
					}

					pushFollow(FOLLOW_termExpr_in_timeBound272);
					termExpr47=termExpr();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, termExpr47.getTree());

					RBRAT48=(Token)match(input,RBRAT,FOLLOW_RBRAT_in_timeBound274); if (state.failed) return retval;
					}
					break;
				case 3 :
					// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:55:4: termExpr
					{
					root_0 = (Object)adaptor.nil();


					pushFollow(FOLLOW_termExpr_in_timeBound280);
					termExpr49=termExpr();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, termExpr49.getTree());

					}
					break;

			}
			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "timeBound"


	public static class booelanAtomic_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "booelanAtomic"
	// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:62:1: booelanAtomic : ( NOT ^)? ( relationalExpr | LPAR ! exprOR RPAR !| TRUE | FALSE ) ;
	public final MiTLParser.booelanAtomic_return booelanAtomic() throws RecognitionException {
		MiTLParser.booelanAtomic_return retval = new MiTLParser.booelanAtomic_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token NOT50=null;
		Token LPAR52=null;
		Token RPAR54=null;
		Token TRUE55=null;
		Token FALSE56=null;
		ParserRuleReturnScope relationalExpr51 =null;
		ParserRuleReturnScope exprOR53 =null;

		Object NOT50_tree=null;
		Object LPAR52_tree=null;
		Object RPAR54_tree=null;
		Object TRUE55_tree=null;
		Object FALSE56_tree=null;

		try {
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:63:2: ( ( NOT ^)? ( relationalExpr | LPAR ! exprOR RPAR !| TRUE | FALSE ) )
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:63:4: ( NOT ^)? ( relationalExpr | LPAR ! exprOR RPAR !| TRUE | FALSE )
			{
			root_0 = (Object)adaptor.nil();


			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:63:7: ( NOT ^)?
			int alt15=2;
			int LA15_0 = input.LA(1);
			if ( (LA15_0==NOT) ) {
				alt15=1;
			}
			switch (alt15) {
				case 1 :
					// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:63:7: NOT ^
					{
					NOT50=(Token)match(input,NOT,FOLLOW_NOT_in_booelanAtomic296); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					NOT50_tree = (Object)adaptor.create(NOT50);
					root_0 = (Object)adaptor.becomeRoot(NOT50_tree, root_0);
					}

					}
					break;

			}

			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:64:3: ( relationalExpr | LPAR ! exprOR RPAR !| TRUE | FALSE )
			int alt16=4;
			switch ( input.LA(1) ) {
			case FLOAT:
			case ID:
			case INTEGER:
			case MINUS:
				{
				alt16=1;
				}
				break;
			case LPAR:
				{
				int LA16_3 = input.LA(2);
				if ( (synpred21_MiTL()) ) {
					alt16=1;
				}
				else if ( (synpred22_MiTL()) ) {
					alt16=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 16, 3, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case TRUE:
				{
				alt16=3;
				}
				break;
			case FALSE:
				{
				alt16=4;
				}
				break;
			default:
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 16, 0, input);
				throw nvae;
			}
			switch (alt16) {
				case 1 :
					// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:64:5: relationalExpr
					{
					pushFollow(FOLLOW_relationalExpr_in_booelanAtomic304);
					relationalExpr51=relationalExpr();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, relationalExpr51.getTree());

					}
					break;
				case 2 :
					// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:65:5: LPAR ! exprOR RPAR !
					{
					LPAR52=(Token)match(input,LPAR,FOLLOW_LPAR_in_booelanAtomic310); if (state.failed) return retval;
					pushFollow(FOLLOW_exprOR_in_booelanAtomic313);
					exprOR53=exprOR();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, exprOR53.getTree());

					RPAR54=(Token)match(input,RPAR,FOLLOW_RPAR_in_booelanAtomic315); if (state.failed) return retval;
					}
					break;
				case 3 :
					// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:66:5: TRUE
					{
					TRUE55=(Token)match(input,TRUE,FOLLOW_TRUE_in_booelanAtomic322); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					TRUE55_tree = (Object)adaptor.create(TRUE55);
					adaptor.addChild(root_0, TRUE55_tree);
					}

					}
					break;
				case 4 :
					// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:67:5: FALSE
					{
					FALSE56=(Token)match(input,FALSE,FOLLOW_FALSE_in_booelanAtomic328); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					FALSE56_tree = (Object)adaptor.create(FALSE56);
					adaptor.addChild(root_0, FALSE56_tree);
					}

					}
					break;

			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "booelanAtomic"


	public static class relationalExpr_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "relationalExpr"
	// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:73:1: relationalExpr : termExpr ( EQ | NEQ | GT | LT | GE | LE ) ^ termExpr ;
	public final MiTLParser.relationalExpr_return relationalExpr() throws RecognitionException {
		MiTLParser.relationalExpr_return retval = new MiTLParser.relationalExpr_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token set58=null;
		ParserRuleReturnScope termExpr57 =null;
		ParserRuleReturnScope termExpr59 =null;

		Object set58_tree=null;

		try {
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:74:2: ( termExpr ( EQ | NEQ | GT | LT | GE | LE ) ^ termExpr )
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:74:4: termExpr ( EQ | NEQ | GT | LT | GE | LE ) ^ termExpr
			{
			root_0 = (Object)adaptor.nil();


			pushFollow(FOLLOW_termExpr_in_relationalExpr345);
			termExpr57=termExpr();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) adaptor.addChild(root_0, termExpr57.getTree());

			set58=input.LT(1);
			set58=input.LT(1);
			if ( input.LA(1)==EQ||(input.LA(1) >= GE && input.LA(1) <= GT)||input.LA(1)==LE||input.LA(1)==LT||input.LA(1)==NEQ ) {
				input.consume();
				if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot((Object)adaptor.create(set58), root_0);
				state.errorRecovery=false;
				state.failed=false;
			}
			else {
				if (state.backtracking>0) {state.failed=true; return retval;}
				MismatchedSetException mse = new MismatchedSetException(null,input);
				throw mse;
			}
			pushFollow(FOLLOW_termExpr_in_relationalExpr372);
			termExpr59=termExpr();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) adaptor.addChild(root_0, termExpr59.getTree());

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "relationalExpr"


	public static class termExpr_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "termExpr"
	// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:82:1: termExpr : factorExpr ( PLUS ^ factorExpr | MINUS ^ factorExpr )* ;
	public final MiTLParser.termExpr_return termExpr() throws RecognitionException {
		MiTLParser.termExpr_return retval = new MiTLParser.termExpr_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token PLUS61=null;
		Token MINUS63=null;
		ParserRuleReturnScope factorExpr60 =null;
		ParserRuleReturnScope factorExpr62 =null;
		ParserRuleReturnScope factorExpr64 =null;

		Object PLUS61_tree=null;
		Object MINUS63_tree=null;

		try {
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:83:5: ( factorExpr ( PLUS ^ factorExpr | MINUS ^ factorExpr )* )
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:83:10: factorExpr ( PLUS ^ factorExpr | MINUS ^ factorExpr )*
			{
			root_0 = (Object)adaptor.nil();


			pushFollow(FOLLOW_factorExpr_in_termExpr394);
			factorExpr60=factorExpr();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) adaptor.addChild(root_0, factorExpr60.getTree());

			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:84:10: ( PLUS ^ factorExpr | MINUS ^ factorExpr )*
			loop17:
			while (true) {
				int alt17=3;
				int LA17_0 = input.LA(1);
				if ( (LA17_0==MINUS) ) {
					int LA17_2 = input.LA(2);
					if ( (synpred30_MiTL()) ) {
						alt17=2;
					}

				}
				else if ( (LA17_0==PLUS) ) {
					alt17=1;
				}

				switch (alt17) {
				case 1 :
					// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:84:12: PLUS ^ factorExpr
					{
					PLUS61=(Token)match(input,PLUS,FOLLOW_PLUS_in_termExpr408); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					PLUS61_tree = (Object)adaptor.create(PLUS61);
					root_0 = (Object)adaptor.becomeRoot(PLUS61_tree, root_0);
					}

					pushFollow(FOLLOW_factorExpr_in_termExpr411);
					factorExpr62=factorExpr();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, factorExpr62.getTree());

					}
					break;
				case 2 :
					// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:85:12: MINUS ^ factorExpr
					{
					MINUS63=(Token)match(input,MINUS,FOLLOW_MINUS_in_termExpr425); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					MINUS63_tree = (Object)adaptor.create(MINUS63);
					root_0 = (Object)adaptor.becomeRoot(MINUS63_tree, root_0);
					}

					pushFollow(FOLLOW_factorExpr_in_termExpr428);
					factorExpr64=factorExpr();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, factorExpr64.getTree());

					}
					break;

				default :
					break loop17;
				}
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "termExpr"


	public static class factorExpr_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "factorExpr"
	// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:89:1: factorExpr : factor ( MULT ^ factor | DIV ^ factor )* ;
	public final MiTLParser.factorExpr_return factorExpr() throws RecognitionException {
		MiTLParser.factorExpr_return retval = new MiTLParser.factorExpr_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token MULT66=null;
		Token DIV68=null;
		ParserRuleReturnScope factor65 =null;
		ParserRuleReturnScope factor67 =null;
		ParserRuleReturnScope factor69 =null;

		Object MULT66_tree=null;
		Object DIV68_tree=null;

		try {
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:90:2: ( factor ( MULT ^ factor | DIV ^ factor )* )
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:90:4: factor ( MULT ^ factor | DIV ^ factor )*
			{
			root_0 = (Object)adaptor.nil();


			pushFollow(FOLLOW_factor_in_factorExpr455);
			factor65=factor();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) adaptor.addChild(root_0, factor65.getTree());

			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:91:3: ( MULT ^ factor | DIV ^ factor )*
			loop18:
			while (true) {
				int alt18=3;
				int LA18_0 = input.LA(1);
				if ( (LA18_0==MULT) ) {
					alt18=1;
				}
				else if ( (LA18_0==DIV) ) {
					alt18=2;
				}

				switch (alt18) {
				case 1 :
					// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:91:5: MULT ^ factor
					{
					MULT66=(Token)match(input,MULT,FOLLOW_MULT_in_factorExpr461); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					MULT66_tree = (Object)adaptor.create(MULT66);
					root_0 = (Object)adaptor.becomeRoot(MULT66_tree, root_0);
					}

					pushFollow(FOLLOW_factor_in_factorExpr464);
					factor67=factor();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, factor67.getTree());

					}
					break;
				case 2 :
					// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:92:5: DIV ^ factor
					{
					DIV68=(Token)match(input,DIV,FOLLOW_DIV_in_factorExpr471); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					DIV68_tree = (Object)adaptor.create(DIV68);
					root_0 = (Object)adaptor.becomeRoot(DIV68_tree, root_0);
					}

					pushFollow(FOLLOW_factor_in_factorExpr474);
					factor69=factor();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, factor69.getTree());

					}
					break;

				default :
					break loop18;
				}
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "factorExpr"


	public static class factor_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "factor"
	// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:96:1: factor : ( MINUS ^)? ( atomic | LPAR ! termExpr RPAR !| functionExpr ) ;
	public final MiTLParser.factor_return factor() throws RecognitionException {
		MiTLParser.factor_return retval = new MiTLParser.factor_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token MINUS70=null;
		Token LPAR72=null;
		Token RPAR74=null;
		ParserRuleReturnScope atomic71 =null;
		ParserRuleReturnScope termExpr73 =null;
		ParserRuleReturnScope functionExpr75 =null;

		Object MINUS70_tree=null;
		Object LPAR72_tree=null;
		Object RPAR74_tree=null;

		try {
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:97:2: ( ( MINUS ^)? ( atomic | LPAR ! termExpr RPAR !| functionExpr ) )
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:97:4: ( MINUS ^)? ( atomic | LPAR ! termExpr RPAR !| functionExpr )
			{
			root_0 = (Object)adaptor.nil();


			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:97:9: ( MINUS ^)?
			int alt19=2;
			int LA19_0 = input.LA(1);
			if ( (LA19_0==MINUS) ) {
				alt19=1;
			}
			switch (alt19) {
				case 1 :
					// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:97:9: MINUS ^
					{
					MINUS70=(Token)match(input,MINUS,FOLLOW_MINUS_in_factor491); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					MINUS70_tree = (Object)adaptor.create(MINUS70);
					root_0 = (Object)adaptor.becomeRoot(MINUS70_tree, root_0);
					}

					}
					break;

			}

			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:98:3: ( atomic | LPAR ! termExpr RPAR !| functionExpr )
			int alt20=3;
			switch ( input.LA(1) ) {
			case ID:
				{
				int LA20_1 = input.LA(2);
				if ( (synpred34_MiTL()) ) {
					alt20=1;
				}
				else if ( (true) ) {
					alt20=3;
				}

				}
				break;
			case LPAR:
				{
				alt20=2;
				}
				break;
			case FLOAT:
			case INTEGER:
				{
				alt20=1;
				}
				break;
			default:
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 20, 0, input);
				throw nvae;
			}
			switch (alt20) {
				case 1 :
					// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:98:4: atomic
					{
					pushFollow(FOLLOW_atomic_in_factor498);
					atomic71=atomic();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, atomic71.getTree());

					}
					break;
				case 2 :
					// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:99:5: LPAR ! termExpr RPAR !
					{
					LPAR72=(Token)match(input,LPAR,FOLLOW_LPAR_in_factor504); if (state.failed) return retval;
					pushFollow(FOLLOW_termExpr_in_factor507);
					termExpr73=termExpr();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, termExpr73.getTree());

					RPAR74=(Token)match(input,RPAR,FOLLOW_RPAR_in_factor509); if (state.failed) return retval;
					}
					break;
				case 3 :
					// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:100:5: functionExpr
					{
					pushFollow(FOLLOW_functionExpr_in_factor516);
					functionExpr75=functionExpr();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, functionExpr75.getTree());

					}
					break;

			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "factor"


	public static class functionExpr_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "functionExpr"
	// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:104:1: functionExpr : ID ^ LPAR ! termExpr ( COMMA ! termExpr )* RPAR !;
	public final MiTLParser.functionExpr_return functionExpr() throws RecognitionException {
		MiTLParser.functionExpr_return retval = new MiTLParser.functionExpr_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token ID76=null;
		Token LPAR77=null;
		Token COMMA79=null;
		Token RPAR81=null;
		ParserRuleReturnScope termExpr78 =null;
		ParserRuleReturnScope termExpr80 =null;

		Object ID76_tree=null;
		Object LPAR77_tree=null;
		Object COMMA79_tree=null;
		Object RPAR81_tree=null;

		try {
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:105:2: ( ID ^ LPAR ! termExpr ( COMMA ! termExpr )* RPAR !)
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:105:4: ID ^ LPAR ! termExpr ( COMMA ! termExpr )* RPAR !
			{
			root_0 = (Object)adaptor.nil();


			ID76=(Token)match(input,ID,FOLLOW_ID_in_functionExpr531); if (state.failed) return retval;
			if ( state.backtracking==0 ) {
			ID76_tree = (Object)adaptor.create(ID76);
			root_0 = (Object)adaptor.becomeRoot(ID76_tree, root_0);
			}

			LPAR77=(Token)match(input,LPAR,FOLLOW_LPAR_in_functionExpr534); if (state.failed) return retval;
			pushFollow(FOLLOW_termExpr_in_functionExpr537);
			termExpr78=termExpr();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) adaptor.addChild(root_0, termExpr78.getTree());

			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:105:23: ( COMMA ! termExpr )*
			loop21:
			while (true) {
				int alt21=2;
				int LA21_0 = input.LA(1);
				if ( (LA21_0==COMMA) ) {
					alt21=1;
				}

				switch (alt21) {
				case 1 :
					// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:105:24: COMMA ! termExpr
					{
					COMMA79=(Token)match(input,COMMA,FOLLOW_COMMA_in_functionExpr540); if (state.failed) return retval;
					pushFollow(FOLLOW_termExpr_in_functionExpr543);
					termExpr80=termExpr();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, termExpr80.getTree());

					}
					break;

				default :
					break loop21;
				}
			}

			RPAR81=(Token)match(input,RPAR,FOLLOW_RPAR_in_functionExpr547); if (state.failed) return retval;
			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "functionExpr"


	public static class atomic_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "atomic"
	// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:108:1: atomic : ( ( INTEGER | FLOAT ) | ID );
	public final MiTLParser.atomic_return atomic() throws RecognitionException {
		MiTLParser.atomic_return retval = new MiTLParser.atomic_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token set82=null;

		Object set82_tree=null;

		try {
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:109:2: ( ( INTEGER | FLOAT ) | ID )
			// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:
			{
			root_0 = (Object)adaptor.nil();


			set82=input.LT(1);
			if ( input.LA(1)==FLOAT||input.LA(1)==ID||input.LA(1)==INTEGER ) {
				input.consume();
				if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set82));
				state.errorRecovery=false;
				state.failed=false;
			}
			else {
				if (state.backtracking>0) {state.failed=true; return retval;}
				MismatchedSetException mse = new MismatchedSetException(null,input);
				throw mse;
			}
			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "atomic"

	// $ANTLR start synpred21_MiTL
	public final void synpred21_MiTL_fragment() throws RecognitionException {
		// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:64:5: ( relationalExpr )
		// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:64:5: relationalExpr
		{
		pushFollow(FOLLOW_relationalExpr_in_synpred21_MiTL304);
		relationalExpr();
		state._fsp--;
		if (state.failed) return;

		}

	}
	// $ANTLR end synpred21_MiTL

	// $ANTLR start synpred22_MiTL
	public final void synpred22_MiTL_fragment() throws RecognitionException {
		// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:65:5: ( LPAR exprOR RPAR )
		// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:65:5: LPAR exprOR RPAR
		{
		match(input,LPAR,FOLLOW_LPAR_in_synpred22_MiTL310); if (state.failed) return;

		pushFollow(FOLLOW_exprOR_in_synpred22_MiTL313);
		exprOR();
		state._fsp--;
		if (state.failed) return;

		match(input,RPAR,FOLLOW_RPAR_in_synpred22_MiTL315); if (state.failed) return;

		}

	}
	// $ANTLR end synpred22_MiTL

	// $ANTLR start synpred30_MiTL
	public final void synpred30_MiTL_fragment() throws RecognitionException {
		// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:85:12: ( MINUS factorExpr )
		// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:85:12: MINUS factorExpr
		{
		match(input,MINUS,FOLLOW_MINUS_in_synpred30_MiTL425); if (state.failed) return;

		pushFollow(FOLLOW_factorExpr_in_synpred30_MiTL428);
		factorExpr();
		state._fsp--;
		if (state.failed) return;

		}

	}
	// $ANTLR end synpred30_MiTL

	// $ANTLR start synpred34_MiTL
	public final void synpred34_MiTL_fragment() throws RecognitionException {
		// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:98:4: ( atomic )
		// /home/dimitrios/Dropbox/Documents/postdoc/antlr/MiTL.g:98:4: atomic
		{
		pushFollow(FOLLOW_atomic_in_synpred34_MiTL498);
		atomic();
		state._fsp--;
		if (state.failed) return;

		}

	}
	// $ANTLR end synpred34_MiTL

	// Delegated rules

	public final boolean synpred22_MiTL() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred22_MiTL_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred21_MiTL() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred21_MiTL_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred34_MiTL() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred34_MiTL_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred30_MiTL() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred30_MiTL_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}



	public static final BitSet FOLLOW_statementList_in_eval29 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_NEWLINE_in_statementList40 = new BitSet(new long[]{0x000000106529E100L});
	public static final BitSet FOLLOW_statement_in_statementList44 = new BitSet(new long[]{0x000000104529E102L});
	public static final BitSet FOLLOW_declaration_in_statement56 = new BitSet(new long[]{0x0000000020000000L});
	public static final BitSet FOLLOW_NEWLINE_in_statement58 = new BitSet(new long[]{0x0000000020000002L});
	public static final BitSet FOLLOW_exprOR_in_statement65 = new BitSet(new long[]{0x0000000020000000L});
	public static final BitSet FOLLOW_NEWLINE_in_statement67 = new BitSet(new long[]{0x0000000020000002L});
	public static final BitSet FOLLOW_CONST_in_declaration80 = new BitSet(new long[]{0x0000000000000400L});
	public static final BitSet FOLLOW_DOUBLE_in_declaration83 = new BitSet(new long[]{0x0000000000080000L});
	public static final BitSet FOLLOW_ID_in_declaration85 = new BitSet(new long[]{0x0000000800000800L});
	public static final BitSet FOLLOW_EQ_in_declaration88 = new BitSet(new long[]{0x0000000000208000L});
	public static final BitSet FOLLOW_set_in_declaration91 = new BitSet(new long[]{0x0000000800000000L});
	public static final BitSet FOLLOW_SEMICOLON_in_declaration101 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_CONST_in_declaration107 = new BitSet(new long[]{0x0000000000100000L});
	public static final BitSet FOLLOW_INT_in_declaration110 = new BitSet(new long[]{0x0000000000080000L});
	public static final BitSet FOLLOW_ID_in_declaration112 = new BitSet(new long[]{0x0000000800000800L});
	public static final BitSet FOLLOW_EQ_in_declaration115 = new BitSet(new long[]{0x0000000000200000L});
	public static final BitSet FOLLOW_INTEGER_in_declaration118 = new BitSet(new long[]{0x0000000800000000L});
	public static final BitSet FOLLOW_SEMICOLON_in_declaration122 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_CONST_in_declaration128 = new BitSet(new long[]{0x0000000000000020L});
	public static final BitSet FOLLOW_BOOL_in_declaration131 = new BitSet(new long[]{0x0000000000080000L});
	public static final BitSet FOLLOW_ID_in_declaration133 = new BitSet(new long[]{0x0000000800000800L});
	public static final BitSet FOLLOW_EQ_in_declaration136 = new BitSet(new long[]{0x0000001000004000L});
	public static final BitSet FOLLOW_set_in_declaration139 = new BitSet(new long[]{0x0000000800000000L});
	public static final BitSet FOLLOW_SEMICOLON_in_declaration149 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_exprAND_in_exprOR166 = new BitSet(new long[]{0x0000000080000002L});
	public static final BitSet FOLLOW_OR_in_exprOR169 = new BitSet(new long[]{0x000000104529E000L});
	public static final BitSet FOLLOW_exprAND_in_exprOR172 = new BitSet(new long[]{0x0000000080000002L});
	public static final BitSet FOLLOW_mitlTerm_in_exprAND186 = new BitSet(new long[]{0x0000000000000012L});
	public static final BitSet FOLLOW_AND_in_exprAND189 = new BitSet(new long[]{0x000000104529E000L});
	public static final BitSet FOLLOW_mitlTerm_in_exprAND192 = new BitSet(new long[]{0x0000000000000012L});
	public static final BitSet FOLLOW_booelanAtomic_in_mitlTerm208 = new BitSet(new long[]{0x0000002000000002L});
	public static final BitSet FOLLOW_U_in_mitlTerm211 = new BitSet(new long[]{0x0000000005E88000L});
	public static final BitSet FOLLOW_timeBound_in_mitlTerm214 = new BitSet(new long[]{0x000000104528C000L});
	public static final BitSet FOLLOW_booelanAtomic_in_mitlTerm216 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_F_in_mitlTerm224 = new BitSet(new long[]{0x0000000005E88000L});
	public static final BitSet FOLLOW_timeBound_in_mitlTerm227 = new BitSet(new long[]{0x000000104528C000L});
	public static final BitSet FOLLOW_booelanAtomic_in_mitlTerm229 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_G_in_mitlTerm235 = new BitSet(new long[]{0x0000000005E88000L});
	public static final BitSet FOLLOW_timeBound_in_mitlTerm238 = new BitSet(new long[]{0x000000104528C000L});
	public static final BitSet FOLLOW_booelanAtomic_in_mitlTerm240 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LE_in_timeBound256 = new BitSet(new long[]{0x0000000005288000L});
	public static final BitSet FOLLOW_termExpr_in_timeBound259 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LBRAT_in_timeBound264 = new BitSet(new long[]{0x0000000005288000L});
	public static final BitSet FOLLOW_termExpr_in_timeBound267 = new BitSet(new long[]{0x0000000000000040L});
	public static final BitSet FOLLOW_COMMA_in_timeBound269 = new BitSet(new long[]{0x0000000005288000L});
	public static final BitSet FOLLOW_termExpr_in_timeBound272 = new BitSet(new long[]{0x0000000200000000L});
	public static final BitSet FOLLOW_RBRAT_in_timeBound274 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_termExpr_in_timeBound280 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_NOT_in_booelanAtomic296 = new BitSet(new long[]{0x000000100528C000L});
	public static final BitSet FOLLOW_relationalExpr_in_booelanAtomic304 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LPAR_in_booelanAtomic310 = new BitSet(new long[]{0x000000104529E000L});
	public static final BitSet FOLLOW_exprOR_in_booelanAtomic313 = new BitSet(new long[]{0x0000000400000000L});
	public static final BitSet FOLLOW_RPAR_in_booelanAtomic315 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_TRUE_in_booelanAtomic322 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_FALSE_in_booelanAtomic328 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_termExpr_in_relationalExpr345 = new BitSet(new long[]{0x0000000012860800L});
	public static final BitSet FOLLOW_set_in_relationalExpr347 = new BitSet(new long[]{0x0000000005288000L});
	public static final BitSet FOLLOW_termExpr_in_relationalExpr372 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_factorExpr_in_termExpr394 = new BitSet(new long[]{0x0000000104000002L});
	public static final BitSet FOLLOW_PLUS_in_termExpr408 = new BitSet(new long[]{0x0000000005288000L});
	public static final BitSet FOLLOW_factorExpr_in_termExpr411 = new BitSet(new long[]{0x0000000104000002L});
	public static final BitSet FOLLOW_MINUS_in_termExpr425 = new BitSet(new long[]{0x0000000005288000L});
	public static final BitSet FOLLOW_factorExpr_in_termExpr428 = new BitSet(new long[]{0x0000000104000002L});
	public static final BitSet FOLLOW_factor_in_factorExpr455 = new BitSet(new long[]{0x0000000008000202L});
	public static final BitSet FOLLOW_MULT_in_factorExpr461 = new BitSet(new long[]{0x0000000005288000L});
	public static final BitSet FOLLOW_factor_in_factorExpr464 = new BitSet(new long[]{0x0000000008000202L});
	public static final BitSet FOLLOW_DIV_in_factorExpr471 = new BitSet(new long[]{0x0000000005288000L});
	public static final BitSet FOLLOW_factor_in_factorExpr474 = new BitSet(new long[]{0x0000000008000202L});
	public static final BitSet FOLLOW_MINUS_in_factor491 = new BitSet(new long[]{0x0000000001288000L});
	public static final BitSet FOLLOW_atomic_in_factor498 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LPAR_in_factor504 = new BitSet(new long[]{0x0000000005288000L});
	public static final BitSet FOLLOW_termExpr_in_factor507 = new BitSet(new long[]{0x0000000400000000L});
	public static final BitSet FOLLOW_RPAR_in_factor509 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_functionExpr_in_factor516 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_functionExpr531 = new BitSet(new long[]{0x0000000001000000L});
	public static final BitSet FOLLOW_LPAR_in_functionExpr534 = new BitSet(new long[]{0x0000000005288000L});
	public static final BitSet FOLLOW_termExpr_in_functionExpr537 = new BitSet(new long[]{0x0000000400000040L});
	public static final BitSet FOLLOW_COMMA_in_functionExpr540 = new BitSet(new long[]{0x0000000005288000L});
	public static final BitSet FOLLOW_termExpr_in_functionExpr543 = new BitSet(new long[]{0x0000000400000040L});
	public static final BitSet FOLLOW_RPAR_in_functionExpr547 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_relationalExpr_in_synpred21_MiTL304 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LPAR_in_synpred22_MiTL310 = new BitSet(new long[]{0x000000104529E000L});
	public static final BitSet FOLLOW_exprOR_in_synpred22_MiTL313 = new BitSet(new long[]{0x0000000400000000L});
	public static final BitSet FOLLOW_RPAR_in_synpred22_MiTL315 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_MINUS_in_synpred30_MiTL425 = new BitSet(new long[]{0x0000000005288000L});
	public static final BitSet FOLLOW_factorExpr_in_synpred30_MiTL428 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_atomic_in_synpred34_MiTL498 = new BitSet(new long[]{0x0000000000000002L});
}
