// $ANTLR 3.4 ../src/org/exquisite/parser/Excel.g 2013-05-17 16:00:26
 package org.exquisite.parser; 

import org.antlr.runtime.BitSet;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.Parser;
import org.antlr.runtime.ParserRuleReturnScope;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeAdaptor;
import org.antlr.runtime.tree.RewriteRuleSubtreeStream;
import org.antlr.runtime.tree.RewriteRuleTokenStream;
import org.antlr.runtime.tree.TreeAdaptor;


@SuppressWarnings({"all", "warnings", "unchecked"})
public class ExcelParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "AND", "CALL", "DIV", "EQUALS", "ERROR", "ERROR_STRING", "FALSE", "FLOAT", "FUNC", "GT", "GTEQ", "IDENT", "INT", "IS", "LT", "LTEQ", "MINUS", "MOD", "MULT", "NOT", "NOTEQUALS", "OR", "PLUS", "POW", "R1C1_REFERENCE", "REFERENCE", "ROOT", "SPACE", "TRUE", "'('", "')'", "','", "';'"
    };

    public static final int EOF=-1;
    public static final int T__33=33;
    public static final int T__34=34;
    public static final int T__35=35;
    public static final int T__36=36;
    public static final int AND=4;
    public static final int CALL=5;
    public static final int DIV=6;
    public static final int EQUALS=7;
    public static final int ERROR=8;
    public static final int ERROR_STRING=9;
    public static final int FALSE=10;
    public static final int FLOAT=11;
    public static final int FUNC=12;
    public static final int GT=13;
    public static final int GTEQ=14;
    public static final int IDENT=15;
    public static final int INT=16;
    public static final int IS=17;
    public static final int LT=18;
    public static final int LTEQ=19;
    public static final int MINUS=20;
    public static final int MOD=21;
    public static final int MULT=22;
    public static final int NOT=23;
    public static final int NOTEQUALS=24;
    public static final int OR=25;
    public static final int PLUS=26;
    public static final int POW=27;
    public static final int R1C1_REFERENCE=28;
    public static final int REFERENCE=29;
    public static final int ROOT=30;
    public static final int SPACE=31;
    public static final int TRUE=32;

    // delegates
    public Parser[] getDelegates() {
        return new Parser[] {};
    }

    // delegators


    public ExcelParser(TokenStream input) {
        this(input, new RecognizerSharedState());
    }
    public ExcelParser(TokenStream input, RecognizerSharedState state) {
        super(input, state);
    }

protected TreeAdaptor adaptor = new CommonTreeAdaptor();

public void setTreeAdaptor(TreeAdaptor adaptor) {
    this.adaptor = adaptor;
}
public TreeAdaptor getTreeAdaptor() {
    return adaptor;
}
    public String[] getTokenNames() { return ExcelParser.tokenNames; }
    public String getGrammarFileName() { return "../src/org/exquisite/parser/Excel.g"; }


    public static class parse_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "parse"
    // ../src/org/exquisite/parser/Excel.g:23:8: public parse : exp EOF -> ^( ROOT exp ) ;
    public final ExcelParser.parse_return parse() throws RecognitionException {
        ExcelParser.parse_return retval = new ExcelParser.parse_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token EOF2=null;
        ExcelParser.exp_return exp1 =null;


        CommonTree EOF2_tree=null;
        RewriteRuleTokenStream stream_EOF=new RewriteRuleTokenStream(adaptor,"token EOF");
        RewriteRuleSubtreeStream stream_exp=new RewriteRuleSubtreeStream(adaptor,"rule exp");
        try {
            // ../src/org/exquisite/parser/Excel.g:24:2: ( exp EOF -> ^( ROOT exp ) )
            // ../src/org/exquisite/parser/Excel.g:24:4: exp EOF
            {
            pushFollow(FOLLOW_exp_in_parse77);
            exp1=exp();

            state._fsp--;

            stream_exp.add(exp1.getTree());

            EOF2=(Token)match(input,EOF,FOLLOW_EOF_in_parse79);  
            stream_EOF.add(EOF2);


            // AST REWRITE
            // elements: exp
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 24:12: -> ^( ROOT exp )
            {
                // ../src/org/exquisite/parser/Excel.g:24:15: ^( ROOT exp )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(ROOT, "ROOT")
                , root_1);

                adaptor.addChild(root_1, stream_exp.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;

            }

            retval.stop = input.LT(-1);


            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "parse"


    public static class exp_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "exp"
    // ../src/org/exquisite/parser/Excel.g:27:1: exp : orExp ;
    public final ExcelParser.exp_return exp() throws RecognitionException {
        ExcelParser.exp_return retval = new ExcelParser.exp_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        ExcelParser.orExp_return orExp3 =null;



        try {
            // ../src/org/exquisite/parser/Excel.g:28:2: ( orExp )
            // ../src/org/exquisite/parser/Excel.g:28:4: orExp
            {
            root_0 = (CommonTree)adaptor.nil();


            pushFollow(FOLLOW_orExp_in_exp98);
            orExp3=orExp();

            state._fsp--;

            adaptor.addChild(root_0, orExp3.getTree());

            }

            retval.stop = input.LT(-1);


            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "exp"


    public static class orExp_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "orExp"
    // ../src/org/exquisite/parser/Excel.g:31:1: orExp : andExp ( OR ^ andExp )* ;
    public final ExcelParser.orExp_return orExp() throws RecognitionException {
        ExcelParser.orExp_return retval = new ExcelParser.orExp_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token OR5=null;
        ExcelParser.andExp_return andExp4 =null;

        ExcelParser.andExp_return andExp6 =null;


        CommonTree OR5_tree=null;

        try {
            // ../src/org/exquisite/parser/Excel.g:32:2: ( andExp ( OR ^ andExp )* )
            // ../src/org/exquisite/parser/Excel.g:32:4: andExp ( OR ^ andExp )*
            {
            root_0 = (CommonTree)adaptor.nil();


            pushFollow(FOLLOW_andExp_in_orExp109);
            andExp4=andExp();

            state._fsp--;

            adaptor.addChild(root_0, andExp4.getTree());

            // ../src/org/exquisite/parser/Excel.g:32:11: ( OR ^ andExp )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==OR) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // ../src/org/exquisite/parser/Excel.g:32:12: OR ^ andExp
            	    {
            	    OR5=(Token)match(input,OR,FOLLOW_OR_in_orExp112); 
            	    OR5_tree = 
            	    (CommonTree)adaptor.create(OR5)
            	    ;
            	    root_0 = (CommonTree)adaptor.becomeRoot(OR5_tree, root_0);


            	    pushFollow(FOLLOW_andExp_in_orExp115);
            	    andExp6=andExp();

            	    state._fsp--;

            	    adaptor.addChild(root_0, andExp6.getTree());

            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "orExp"


    public static class andExp_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "andExp"
    // ../src/org/exquisite/parser/Excel.g:35:1: andExp : eqExp ( AND ^ eqExp )* ;
    public final ExcelParser.andExp_return andExp() throws RecognitionException {
        ExcelParser.andExp_return retval = new ExcelParser.andExp_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token AND8=null;
        ExcelParser.eqExp_return eqExp7 =null;

        ExcelParser.eqExp_return eqExp9 =null;


        CommonTree AND8_tree=null;

        try {
            // ../src/org/exquisite/parser/Excel.g:36:2: ( eqExp ( AND ^ eqExp )* )
            // ../src/org/exquisite/parser/Excel.g:36:4: eqExp ( AND ^ eqExp )*
            {
            root_0 = (CommonTree)adaptor.nil();


            pushFollow(FOLLOW_eqExp_in_andExp128);
            eqExp7=eqExp();

            state._fsp--;

            adaptor.addChild(root_0, eqExp7.getTree());

            // ../src/org/exquisite/parser/Excel.g:36:10: ( AND ^ eqExp )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==AND) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // ../src/org/exquisite/parser/Excel.g:36:11: AND ^ eqExp
            	    {
            	    AND8=(Token)match(input,AND,FOLLOW_AND_in_andExp131); 
            	    AND8_tree = 
            	    (CommonTree)adaptor.create(AND8)
            	    ;
            	    root_0 = (CommonTree)adaptor.becomeRoot(AND8_tree, root_0);


            	    pushFollow(FOLLOW_eqExp_in_andExp134);
            	    eqExp9=eqExp();

            	    state._fsp--;

            	    adaptor.addChild(root_0, eqExp9.getTree());

            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "andExp"


    public static class eqExp_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "eqExp"
    // ../src/org/exquisite/parser/Excel.g:39:1: eqExp : relExp ( ( EQUALS ^| NOTEQUALS ^) relExp )* ;
    public final ExcelParser.eqExp_return eqExp() throws RecognitionException {
        ExcelParser.eqExp_return retval = new ExcelParser.eqExp_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token EQUALS11=null;
        Token NOTEQUALS12=null;
        ExcelParser.relExp_return relExp10 =null;

        ExcelParser.relExp_return relExp13 =null;


        CommonTree EQUALS11_tree=null;
        CommonTree NOTEQUALS12_tree=null;

        try {
            // ../src/org/exquisite/parser/Excel.g:40:2: ( relExp ( ( EQUALS ^| NOTEQUALS ^) relExp )* )
            // ../src/org/exquisite/parser/Excel.g:40:4: relExp ( ( EQUALS ^| NOTEQUALS ^) relExp )*
            {
            root_0 = (CommonTree)adaptor.nil();


            pushFollow(FOLLOW_relExp_in_eqExp147);
            relExp10=relExp();

            state._fsp--;

            adaptor.addChild(root_0, relExp10.getTree());

            // ../src/org/exquisite/parser/Excel.g:40:11: ( ( EQUALS ^| NOTEQUALS ^) relExp )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0==EQUALS||LA4_0==NOTEQUALS) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // ../src/org/exquisite/parser/Excel.g:40:12: ( EQUALS ^| NOTEQUALS ^) relExp
            	    {
            	    // ../src/org/exquisite/parser/Excel.g:40:12: ( EQUALS ^| NOTEQUALS ^)
            	    int alt3=2;
            	    int LA3_0 = input.LA(1);

            	    if ( (LA3_0==EQUALS) ) {
            	        alt3=1;
            	    }
            	    else if ( (LA3_0==NOTEQUALS) ) {
            	        alt3=2;
            	    }
            	    else {
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 3, 0, input);

            	        throw nvae;

            	    }
            	    switch (alt3) {
            	        case 1 :
            	            // ../src/org/exquisite/parser/Excel.g:40:14: EQUALS ^
            	            {
            	            EQUALS11=(Token)match(input,EQUALS,FOLLOW_EQUALS_in_eqExp152); 
            	            EQUALS11_tree = 
            	            (CommonTree)adaptor.create(EQUALS11)
            	            ;
            	            root_0 = (CommonTree)adaptor.becomeRoot(EQUALS11_tree, root_0);


            	            }
            	            break;
            	        case 2 :
            	            // ../src/org/exquisite/parser/Excel.g:40:24: NOTEQUALS ^
            	            {
            	            NOTEQUALS12=(Token)match(input,NOTEQUALS,FOLLOW_NOTEQUALS_in_eqExp157); 
            	            NOTEQUALS12_tree = 
            	            (CommonTree)adaptor.create(NOTEQUALS12)
            	            ;
            	            root_0 = (CommonTree)adaptor.becomeRoot(NOTEQUALS12_tree, root_0);


            	            }
            	            break;

            	    }


            	    pushFollow(FOLLOW_relExp_in_eqExp161);
            	    relExp13=relExp();

            	    state._fsp--;

            	    adaptor.addChild(root_0, relExp13.getTree());

            	    }
            	    break;

            	default :
            	    break loop4;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "eqExp"


    public static class relExp_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "relExp"
    // ../src/org/exquisite/parser/Excel.g:43:1: relExp : addExp ( ( LT ^| LTEQ ^| GT ^| GTEQ ^) addExp )* ;
    public final ExcelParser.relExp_return relExp() throws RecognitionException {
        ExcelParser.relExp_return retval = new ExcelParser.relExp_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token LT15=null;
        Token LTEQ16=null;
        Token GT17=null;
        Token GTEQ18=null;
        ExcelParser.addExp_return addExp14 =null;

        ExcelParser.addExp_return addExp19 =null;


        CommonTree LT15_tree=null;
        CommonTree LTEQ16_tree=null;
        CommonTree GT17_tree=null;
        CommonTree GTEQ18_tree=null;

        try {
            // ../src/org/exquisite/parser/Excel.g:44:2: ( addExp ( ( LT ^| LTEQ ^| GT ^| GTEQ ^) addExp )* )
            // ../src/org/exquisite/parser/Excel.g:44:4: addExp ( ( LT ^| LTEQ ^| GT ^| GTEQ ^) addExp )*
            {
            root_0 = (CommonTree)adaptor.nil();


            pushFollow(FOLLOW_addExp_in_relExp174);
            addExp14=addExp();

            state._fsp--;

            adaptor.addChild(root_0, addExp14.getTree());

            // ../src/org/exquisite/parser/Excel.g:44:11: ( ( LT ^| LTEQ ^| GT ^| GTEQ ^) addExp )*
            loop6:
            do {
                int alt6=2;
                int LA6_0 = input.LA(1);

                if ( ((LA6_0 >= GT && LA6_0 <= GTEQ)||(LA6_0 >= LT && LA6_0 <= LTEQ)) ) {
                    alt6=1;
                }


                switch (alt6) {
            	case 1 :
            	    // ../src/org/exquisite/parser/Excel.g:44:12: ( LT ^| LTEQ ^| GT ^| GTEQ ^) addExp
            	    {
            	    // ../src/org/exquisite/parser/Excel.g:44:12: ( LT ^| LTEQ ^| GT ^| GTEQ ^)
            	    int alt5=4;
            	    switch ( input.LA(1) ) {
            	    case LT:
            	        {
            	        alt5=1;
            	        }
            	        break;
            	    case LTEQ:
            	        {
            	        alt5=2;
            	        }
            	        break;
            	    case GT:
            	        {
            	        alt5=3;
            	        }
            	        break;
            	    case GTEQ:
            	        {
            	        alt5=4;
            	        }
            	        break;
            	    default:
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 5, 0, input);

            	        throw nvae;

            	    }

            	    switch (alt5) {
            	        case 1 :
            	            // ../src/org/exquisite/parser/Excel.g:44:13: LT ^
            	            {
            	            LT15=(Token)match(input,LT,FOLLOW_LT_in_relExp178); 
            	            LT15_tree = 
            	            (CommonTree)adaptor.create(LT15)
            	            ;
            	            root_0 = (CommonTree)adaptor.becomeRoot(LT15_tree, root_0);


            	            }
            	            break;
            	        case 2 :
            	            // ../src/org/exquisite/parser/Excel.g:44:19: LTEQ ^
            	            {
            	            LTEQ16=(Token)match(input,LTEQ,FOLLOW_LTEQ_in_relExp183); 
            	            LTEQ16_tree = 
            	            (CommonTree)adaptor.create(LTEQ16)
            	            ;
            	            root_0 = (CommonTree)adaptor.becomeRoot(LTEQ16_tree, root_0);


            	            }
            	            break;
            	        case 3 :
            	            // ../src/org/exquisite/parser/Excel.g:44:26: GT ^
            	            {
            	            GT17=(Token)match(input,GT,FOLLOW_GT_in_relExp187); 
            	            GT17_tree = 
            	            (CommonTree)adaptor.create(GT17)
            	            ;
            	            root_0 = (CommonTree)adaptor.becomeRoot(GT17_tree, root_0);


            	            }
            	            break;
            	        case 4 :
            	            // ../src/org/exquisite/parser/Excel.g:44:32: GTEQ ^
            	            {
            	            GTEQ18=(Token)match(input,GTEQ,FOLLOW_GTEQ_in_relExp192); 
            	            GTEQ18_tree = 
            	            (CommonTree)adaptor.create(GTEQ18)
            	            ;
            	            root_0 = (CommonTree)adaptor.becomeRoot(GTEQ18_tree, root_0);


            	            }
            	            break;

            	    }


            	    pushFollow(FOLLOW_addExp_in_relExp196);
            	    addExp19=addExp();

            	    state._fsp--;

            	    adaptor.addChild(root_0, addExp19.getTree());

            	    }
            	    break;

            	default :
            	    break loop6;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "relExp"


    public static class addExp_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "addExp"
    // ../src/org/exquisite/parser/Excel.g:47:1: addExp : multExp ( ( PLUS ^| MINUS ^) multExp )* ;
    public final ExcelParser.addExp_return addExp() throws RecognitionException {
        ExcelParser.addExp_return retval = new ExcelParser.addExp_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token PLUS21=null;
        Token MINUS22=null;
        ExcelParser.multExp_return multExp20 =null;

        ExcelParser.multExp_return multExp23 =null;


        CommonTree PLUS21_tree=null;
        CommonTree MINUS22_tree=null;

        try {
            // ../src/org/exquisite/parser/Excel.g:48:2: ( multExp ( ( PLUS ^| MINUS ^) multExp )* )
            // ../src/org/exquisite/parser/Excel.g:48:4: multExp ( ( PLUS ^| MINUS ^) multExp )*
            {
            root_0 = (CommonTree)adaptor.nil();


            pushFollow(FOLLOW_multExp_in_addExp209);
            multExp20=multExp();

            state._fsp--;

            adaptor.addChild(root_0, multExp20.getTree());

            // ../src/org/exquisite/parser/Excel.g:48:12: ( ( PLUS ^| MINUS ^) multExp )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0==MINUS||LA8_0==PLUS) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // ../src/org/exquisite/parser/Excel.g:48:13: ( PLUS ^| MINUS ^) multExp
            	    {
            	    // ../src/org/exquisite/parser/Excel.g:48:13: ( PLUS ^| MINUS ^)
            	    int alt7=2;
            	    int LA7_0 = input.LA(1);

            	    if ( (LA7_0==PLUS) ) {
            	        alt7=1;
            	    }
            	    else if ( (LA7_0==MINUS) ) {
            	        alt7=2;
            	    }
            	    else {
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 7, 0, input);

            	        throw nvae;

            	    }
            	    switch (alt7) {
            	        case 1 :
            	            // ../src/org/exquisite/parser/Excel.g:48:14: PLUS ^
            	            {
            	            PLUS21=(Token)match(input,PLUS,FOLLOW_PLUS_in_addExp213); 
            	            PLUS21_tree = 
            	            (CommonTree)adaptor.create(PLUS21)
            	            ;
            	            root_0 = (CommonTree)adaptor.becomeRoot(PLUS21_tree, root_0);


            	            }
            	            break;
            	        case 2 :
            	            // ../src/org/exquisite/parser/Excel.g:48:22: MINUS ^
            	            {
            	            MINUS22=(Token)match(input,MINUS,FOLLOW_MINUS_in_addExp218); 
            	            MINUS22_tree = 
            	            (CommonTree)adaptor.create(MINUS22)
            	            ;
            	            root_0 = (CommonTree)adaptor.becomeRoot(MINUS22_tree, root_0);


            	            }
            	            break;

            	    }


            	    pushFollow(FOLLOW_multExp_in_addExp222);
            	    multExp23=multExp();

            	    state._fsp--;

            	    adaptor.addChild(root_0, multExp23.getTree());

            	    }
            	    break;

            	default :
            	    break loop8;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "addExp"


    public static class multExp_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "multExp"
    // ../src/org/exquisite/parser/Excel.g:51:1: multExp : unaryExp ( ( MULT ^| DIV ^| MOD ^| POW ^| IS ^) unaryExp )* ;
    public final ExcelParser.multExp_return multExp() throws RecognitionException {
        ExcelParser.multExp_return retval = new ExcelParser.multExp_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token MULT25=null;
        Token DIV26=null;
        Token MOD27=null;
        Token POW28=null;
        Token IS29=null;
        ExcelParser.unaryExp_return unaryExp24 =null;

        ExcelParser.unaryExp_return unaryExp30 =null;


        CommonTree MULT25_tree=null;
        CommonTree DIV26_tree=null;
        CommonTree MOD27_tree=null;
        CommonTree POW28_tree=null;
        CommonTree IS29_tree=null;

        try {
            // ../src/org/exquisite/parser/Excel.g:52:2: ( unaryExp ( ( MULT ^| DIV ^| MOD ^| POW ^| IS ^) unaryExp )* )
            // ../src/org/exquisite/parser/Excel.g:52:4: unaryExp ( ( MULT ^| DIV ^| MOD ^| POW ^| IS ^) unaryExp )*
            {
            root_0 = (CommonTree)adaptor.nil();


            pushFollow(FOLLOW_unaryExp_in_multExp235);
            unaryExp24=unaryExp();

            state._fsp--;

            adaptor.addChild(root_0, unaryExp24.getTree());

            // ../src/org/exquisite/parser/Excel.g:52:13: ( ( MULT ^| DIV ^| MOD ^| POW ^| IS ^) unaryExp )*
            loop10:
            do {
                int alt10=2;
                int LA10_0 = input.LA(1);

                if ( (LA10_0==DIV||LA10_0==IS||(LA10_0 >= MOD && LA10_0 <= MULT)||LA10_0==POW) ) {
                    alt10=1;
                }


                switch (alt10) {
            	case 1 :
            	    // ../src/org/exquisite/parser/Excel.g:52:14: ( MULT ^| DIV ^| MOD ^| POW ^| IS ^) unaryExp
            	    {
            	    // ../src/org/exquisite/parser/Excel.g:52:14: ( MULT ^| DIV ^| MOD ^| POW ^| IS ^)
            	    int alt9=5;
            	    switch ( input.LA(1) ) {
            	    case MULT:
            	        {
            	        alt9=1;
            	        }
            	        break;
            	    case DIV:
            	        {
            	        alt9=2;
            	        }
            	        break;
            	    case MOD:
            	        {
            	        alt9=3;
            	        }
            	        break;
            	    case POW:
            	        {
            	        alt9=4;
            	        }
            	        break;
            	    case IS:
            	        {
            	        alt9=5;
            	        }
            	        break;
            	    default:
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 9, 0, input);

            	        throw nvae;

            	    }

            	    switch (alt9) {
            	        case 1 :
            	            // ../src/org/exquisite/parser/Excel.g:52:16: MULT ^
            	            {
            	            MULT25=(Token)match(input,MULT,FOLLOW_MULT_in_multExp240); 
            	            MULT25_tree = 
            	            (CommonTree)adaptor.create(MULT25)
            	            ;
            	            root_0 = (CommonTree)adaptor.becomeRoot(MULT25_tree, root_0);


            	            }
            	            break;
            	        case 2 :
            	            // ../src/org/exquisite/parser/Excel.g:52:24: DIV ^
            	            {
            	            DIV26=(Token)match(input,DIV,FOLLOW_DIV_in_multExp245); 
            	            DIV26_tree = 
            	            (CommonTree)adaptor.create(DIV26)
            	            ;
            	            root_0 = (CommonTree)adaptor.becomeRoot(DIV26_tree, root_0);


            	            }
            	            break;
            	        case 3 :
            	            // ../src/org/exquisite/parser/Excel.g:52:31: MOD ^
            	            {
            	            MOD27=(Token)match(input,MOD,FOLLOW_MOD_in_multExp250); 
            	            MOD27_tree = 
            	            (CommonTree)adaptor.create(MOD27)
            	            ;
            	            root_0 = (CommonTree)adaptor.becomeRoot(MOD27_tree, root_0);


            	            }
            	            break;
            	        case 4 :
            	            // ../src/org/exquisite/parser/Excel.g:52:37: POW ^
            	            {
            	            POW28=(Token)match(input,POW,FOLLOW_POW_in_multExp254); 
            	            POW28_tree = 
            	            (CommonTree)adaptor.create(POW28)
            	            ;
            	            root_0 = (CommonTree)adaptor.becomeRoot(POW28_tree, root_0);


            	            }
            	            break;
            	        case 5 :
            	            // ../src/org/exquisite/parser/Excel.g:52:43: IS ^
            	            {
            	            IS29=(Token)match(input,IS,FOLLOW_IS_in_multExp258); 
            	            IS29_tree = 
            	            (CommonTree)adaptor.create(IS29)
            	            ;
            	            root_0 = (CommonTree)adaptor.becomeRoot(IS29_tree, root_0);


            	            }
            	            break;

            	    }


            	    pushFollow(FOLLOW_unaryExp_in_multExp262);
            	    unaryExp30=unaryExp();

            	    state._fsp--;

            	    adaptor.addChild(root_0, unaryExp30.getTree());

            	    }
            	    break;

            	default :
            	    break loop10;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "multExp"


    public static class unaryExp_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "unaryExp"
    // ../src/org/exquisite/parser/Excel.g:55:1: unaryExp : ( NOT atom -> ^( NOT atom ) | MINUS atom -> ^( MINUS atom ) | atom );
    public final ExcelParser.unaryExp_return unaryExp() throws RecognitionException {
        ExcelParser.unaryExp_return retval = new ExcelParser.unaryExp_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token NOT31=null;
        Token MINUS33=null;
        ExcelParser.atom_return atom32 =null;

        ExcelParser.atom_return atom34 =null;

        ExcelParser.atom_return atom35 =null;


        CommonTree NOT31_tree=null;
        CommonTree MINUS33_tree=null;
        RewriteRuleTokenStream stream_NOT=new RewriteRuleTokenStream(adaptor,"token NOT");
        RewriteRuleTokenStream stream_MINUS=new RewriteRuleTokenStream(adaptor,"token MINUS");
        RewriteRuleSubtreeStream stream_atom=new RewriteRuleSubtreeStream(adaptor,"rule atom");
        try {
            // ../src/org/exquisite/parser/Excel.g:56:2: ( NOT atom -> ^( NOT atom ) | MINUS atom -> ^( MINUS atom ) | atom )
            int alt11=3;
            switch ( input.LA(1) ) {
            case NOT:
                {
                alt11=1;
                }
                break;
            case MINUS:
                {
                alt11=2;
                }
                break;
            case ERROR_STRING:
            case FALSE:
            case FLOAT:
            case IDENT:
            case INT:
            case REFERENCE:
            case TRUE:
            case 33:
                {
                alt11=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 11, 0, input);

                throw nvae;

            }

            switch (alt11) {
                case 1 :
                    // ../src/org/exquisite/parser/Excel.g:56:4: NOT atom
                    {
                    NOT31=(Token)match(input,NOT,FOLLOW_NOT_in_unaryExp275);  
                    stream_NOT.add(NOT31);


                    pushFollow(FOLLOW_atom_in_unaryExp277);
                    atom32=atom();

                    state._fsp--;

                    stream_atom.add(atom32.getTree());

                    // AST REWRITE
                    // elements: NOT, atom
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 56:13: -> ^( NOT atom )
                    {
                        // ../src/org/exquisite/parser/Excel.g:56:16: ^( NOT atom )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(
                        stream_NOT.nextNode()
                        , root_1);

                        adaptor.addChild(root_1, stream_atom.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;

                    }
                    break;
                case 2 :
                    // ../src/org/exquisite/parser/Excel.g:57:4: MINUS atom
                    {
                    MINUS33=(Token)match(input,MINUS,FOLLOW_MINUS_in_unaryExp290);  
                    stream_MINUS.add(MINUS33);


                    pushFollow(FOLLOW_atom_in_unaryExp292);
                    atom34=atom();

                    state._fsp--;

                    stream_atom.add(atom34.getTree());

                    // AST REWRITE
                    // elements: atom, MINUS
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 57:15: -> ^( MINUS atom )
                    {
                        // ../src/org/exquisite/parser/Excel.g:57:18: ^( MINUS atom )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(
                        stream_MINUS.nextNode()
                        , root_1);

                        adaptor.addChild(root_1, stream_atom.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;

                    }
                    break;
                case 3 :
                    // ../src/org/exquisite/parser/Excel.g:58:4: atom
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_atom_in_unaryExp305);
                    atom35=atom();

                    state._fsp--;

                    adaptor.addChild(root_0, atom35.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "unaryExp"


    public static class atom_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "atom"
    // ../src/org/exquisite/parser/Excel.g:61:1: atom : ( TRUE | FALSE | INT | FLOAT | function | error | '(' exp ')' -> exp | REFERENCE );
    public final ExcelParser.atom_return atom() throws RecognitionException {
        ExcelParser.atom_return retval = new ExcelParser.atom_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token TRUE36=null;
        Token FALSE37=null;
        Token INT38=null;
        Token FLOAT39=null;
        Token char_literal42=null;
        Token char_literal44=null;
        Token REFERENCE45=null;
        ExcelParser.function_return function40 =null;

        ExcelParser.error_return error41 =null;

        ExcelParser.exp_return exp43 =null;


        CommonTree TRUE36_tree=null;
        CommonTree FALSE37_tree=null;
        CommonTree INT38_tree=null;
        CommonTree FLOAT39_tree=null;
        CommonTree char_literal42_tree=null;
        CommonTree char_literal44_tree=null;
        CommonTree REFERENCE45_tree=null;
        RewriteRuleTokenStream stream_33=new RewriteRuleTokenStream(adaptor,"token 33");
        RewriteRuleTokenStream stream_34=new RewriteRuleTokenStream(adaptor,"token 34");
        RewriteRuleSubtreeStream stream_exp=new RewriteRuleSubtreeStream(adaptor,"rule exp");
        try {
            // ../src/org/exquisite/parser/Excel.g:62:2: ( TRUE | FALSE | INT | FLOAT | function | error | '(' exp ')' -> exp | REFERENCE )
            int alt12=8;
            switch ( input.LA(1) ) {
            case TRUE:
                {
                alt12=1;
                }
                break;
            case FALSE:
                {
                alt12=2;
                }
                break;
            case INT:
                {
                alt12=3;
                }
                break;
            case FLOAT:
                {
                alt12=4;
                }
                break;
            case IDENT:
                {
                alt12=5;
                }
                break;
            case ERROR_STRING:
                {
                alt12=6;
                }
                break;
            case 33:
                {
                alt12=7;
                }
                break;
            case REFERENCE:
                {
                alt12=8;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 12, 0, input);

                throw nvae;

            }

            switch (alt12) {
                case 1 :
                    // ../src/org/exquisite/parser/Excel.g:62:4: TRUE
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    TRUE36=(Token)match(input,TRUE,FOLLOW_TRUE_in_atom316); 
                    TRUE36_tree = 
                    (CommonTree)adaptor.create(TRUE36)
                    ;
                    adaptor.addChild(root_0, TRUE36_tree);


                    }
                    break;
                case 2 :
                    // ../src/org/exquisite/parser/Excel.g:63:4: FALSE
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    FALSE37=(Token)match(input,FALSE,FOLLOW_FALSE_in_atom321); 
                    FALSE37_tree = 
                    (CommonTree)adaptor.create(FALSE37)
                    ;
                    adaptor.addChild(root_0, FALSE37_tree);


                    }
                    break;
                case 3 :
                    // ../src/org/exquisite/parser/Excel.g:64:4: INT
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    INT38=(Token)match(input,INT,FOLLOW_INT_in_atom326); 
                    INT38_tree = 
                    (CommonTree)adaptor.create(INT38)
                    ;
                    adaptor.addChild(root_0, INT38_tree);


                    }
                    break;
                case 4 :
                    // ../src/org/exquisite/parser/Excel.g:65:4: FLOAT
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    FLOAT39=(Token)match(input,FLOAT,FOLLOW_FLOAT_in_atom331); 
                    FLOAT39_tree = 
                    (CommonTree)adaptor.create(FLOAT39)
                    ;
                    adaptor.addChild(root_0, FLOAT39_tree);


                    }
                    break;
                case 5 :
                    // ../src/org/exquisite/parser/Excel.g:66:4: function
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_function_in_atom336);
                    function40=function();

                    state._fsp--;

                    adaptor.addChild(root_0, function40.getTree());

                    }
                    break;
                case 6 :
                    // ../src/org/exquisite/parser/Excel.g:67:4: error
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_error_in_atom341);
                    error41=error();

                    state._fsp--;

                    adaptor.addChild(root_0, error41.getTree());

                    }
                    break;
                case 7 :
                    // ../src/org/exquisite/parser/Excel.g:68:4: '(' exp ')'
                    {
                    char_literal42=(Token)match(input,33,FOLLOW_33_in_atom346);  
                    stream_33.add(char_literal42);


                    pushFollow(FOLLOW_exp_in_atom348);
                    exp43=exp();

                    state._fsp--;

                    stream_exp.add(exp43.getTree());

                    char_literal44=(Token)match(input,34,FOLLOW_34_in_atom350);  
                    stream_34.add(char_literal44);


                    // AST REWRITE
                    // elements: exp
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 68:16: -> exp
                    {
                        adaptor.addChild(root_0, stream_exp.nextTree());

                    }


                    retval.tree = root_0;

                    }
                    break;
                case 8 :
                    // ../src/org/exquisite/parser/Excel.g:69:4: REFERENCE
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    REFERENCE45=(Token)match(input,REFERENCE,FOLLOW_REFERENCE_in_atom359); 
                    REFERENCE45_tree = 
                    (CommonTree)adaptor.create(REFERENCE45)
                    ;
                    adaptor.addChild(root_0, REFERENCE45_tree);


                    }
                    break;

            }
            retval.stop = input.LT(-1);


            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "atom"


    public static class function_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "function"
    // ../src/org/exquisite/parser/Excel.g:94:1: function : IDENT '(' ( exp ( ( ',' | ';' ) exp )* )? ')' -> ^( FUNC ^( IDENT ( exp )* ) ) ;
    public final ExcelParser.function_return function() throws RecognitionException {
        ExcelParser.function_return retval = new ExcelParser.function_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token IDENT46=null;
        Token char_literal47=null;
        Token char_literal49=null;
        Token char_literal50=null;
        Token char_literal52=null;
        ExcelParser.exp_return exp48 =null;

        ExcelParser.exp_return exp51 =null;


        CommonTree IDENT46_tree=null;
        CommonTree char_literal47_tree=null;
        CommonTree char_literal49_tree=null;
        CommonTree char_literal50_tree=null;
        CommonTree char_literal52_tree=null;
        RewriteRuleTokenStream stream_IDENT=new RewriteRuleTokenStream(adaptor,"token IDENT");
        RewriteRuleTokenStream stream_35=new RewriteRuleTokenStream(adaptor,"token 35");
        RewriteRuleTokenStream stream_36=new RewriteRuleTokenStream(adaptor,"token 36");
        RewriteRuleTokenStream stream_33=new RewriteRuleTokenStream(adaptor,"token 33");
        RewriteRuleTokenStream stream_34=new RewriteRuleTokenStream(adaptor,"token 34");
        RewriteRuleSubtreeStream stream_exp=new RewriteRuleSubtreeStream(adaptor,"rule exp");
        try {
            // ../src/org/exquisite/parser/Excel.g:95:2: ( IDENT '(' ( exp ( ( ',' | ';' ) exp )* )? ')' -> ^( FUNC ^( IDENT ( exp )* ) ) )
            // ../src/org/exquisite/parser/Excel.g:95:4: IDENT '(' ( exp ( ( ',' | ';' ) exp )* )? ')'
            {
            IDENT46=(Token)match(input,IDENT,FOLLOW_IDENT_in_function530);  
            stream_IDENT.add(IDENT46);


            char_literal47=(Token)match(input,33,FOLLOW_33_in_function532);  
            stream_33.add(char_literal47);


            // ../src/org/exquisite/parser/Excel.g:95:14: ( exp ( ( ',' | ';' ) exp )* )?
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( ((LA15_0 >= ERROR_STRING && LA15_0 <= FLOAT)||(LA15_0 >= IDENT && LA15_0 <= INT)||LA15_0==MINUS||LA15_0==NOT||LA15_0==REFERENCE||(LA15_0 >= TRUE && LA15_0 <= 33)) ) {
                alt15=1;
            }
            switch (alt15) {
                case 1 :
                    // ../src/org/exquisite/parser/Excel.g:95:16: exp ( ( ',' | ';' ) exp )*
                    {
                    pushFollow(FOLLOW_exp_in_function536);
                    exp48=exp();

                    state._fsp--;

                    stream_exp.add(exp48.getTree());

                    // ../src/org/exquisite/parser/Excel.g:95:20: ( ( ',' | ';' ) exp )*
                    loop14:
                    do {
                        int alt14=2;
                        int LA14_0 = input.LA(1);

                        if ( ((LA14_0 >= 35 && LA14_0 <= 36)) ) {
                            alt14=1;
                        }


                        switch (alt14) {
                    	case 1 :
                    	    // ../src/org/exquisite/parser/Excel.g:95:21: ( ',' | ';' ) exp
                    	    {
                    	    // ../src/org/exquisite/parser/Excel.g:95:21: ( ',' | ';' )
                    	    int alt13=2;
                    	    int LA13_0 = input.LA(1);

                    	    if ( (LA13_0==35) ) {
                    	        alt13=1;
                    	    }
                    	    else if ( (LA13_0==36) ) {
                    	        alt13=2;
                    	    }
                    	    else {
                    	        NoViableAltException nvae =
                    	            new NoViableAltException("", 13, 0, input);

                    	        throw nvae;

                    	    }
                    	    switch (alt13) {
                    	        case 1 :
                    	            // ../src/org/exquisite/parser/Excel.g:95:22: ','
                    	            {
                    	            char_literal49=(Token)match(input,35,FOLLOW_35_in_function540);  
                    	            stream_35.add(char_literal49);


                    	            }
                    	            break;
                    	        case 2 :
                    	            // ../src/org/exquisite/parser/Excel.g:95:28: ';'
                    	            {
                    	            char_literal50=(Token)match(input,36,FOLLOW_36_in_function544);  
                    	            stream_36.add(char_literal50);


                    	            }
                    	            break;

                    	    }


                    	    pushFollow(FOLLOW_exp_in_function547);
                    	    exp51=exp();

                    	    state._fsp--;

                    	    stream_exp.add(exp51.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop14;
                        }
                    } while (true);


                    }
                    break;

            }


            char_literal52=(Token)match(input,34,FOLLOW_34_in_function554);  
            stream_34.add(char_literal52);


            // AST REWRITE
            // elements: exp, IDENT
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 95:46: -> ^( FUNC ^( IDENT ( exp )* ) )
            {
                // ../src/org/exquisite/parser/Excel.g:95:49: ^( FUNC ^( IDENT ( exp )* ) )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(FUNC, "FUNC")
                , root_1);

                // ../src/org/exquisite/parser/Excel.g:95:56: ^( IDENT ( exp )* )
                {
                CommonTree root_2 = (CommonTree)adaptor.nil();
                root_2 = (CommonTree)adaptor.becomeRoot(
                stream_IDENT.nextNode()
                , root_2);

                // ../src/org/exquisite/parser/Excel.g:95:64: ( exp )*
                while ( stream_exp.hasNext() ) {
                    adaptor.addChild(root_2, stream_exp.nextTree());

                }
                stream_exp.reset();

                adaptor.addChild(root_1, root_2);
                }

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;

            }

            retval.stop = input.LT(-1);


            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "function"


    public static class error_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "error"
    // ../src/org/exquisite/parser/Excel.g:97:1: error : ERROR_STRING -> ^( ERROR ERROR_STRING ) ;
    public final ExcelParser.error_return error() throws RecognitionException {
        ExcelParser.error_return retval = new ExcelParser.error_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token ERROR_STRING53=null;

        CommonTree ERROR_STRING53_tree=null;
        RewriteRuleTokenStream stream_ERROR_STRING=new RewriteRuleTokenStream(adaptor,"token ERROR_STRING");

        try {
            // ../src/org/exquisite/parser/Excel.g:97:7: ( ERROR_STRING -> ^( ERROR ERROR_STRING ) )
            // ../src/org/exquisite/parser/Excel.g:97:9: ERROR_STRING
            {
            ERROR_STRING53=(Token)match(input,ERROR_STRING,FOLLOW_ERROR_STRING_in_error576);  
            stream_ERROR_STRING.add(ERROR_STRING53);


            // AST REWRITE
            // elements: ERROR_STRING
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 97:22: -> ^( ERROR ERROR_STRING )
            {
                // ../src/org/exquisite/parser/Excel.g:97:25: ^( ERROR ERROR_STRING )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(ERROR, "ERROR")
                , root_1);

                adaptor.addChild(root_1, 
                stream_ERROR_STRING.nextNode()
                );

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;

            }

            retval.stop = input.LT(-1);


            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "error"

    // Delegated rules


 

    public static final BitSet FOLLOW_exp_in_parse77 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_parse79 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_orExp_in_exp98 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_andExp_in_orExp109 = new BitSet(new long[]{0x0000000002000002L});
    public static final BitSet FOLLOW_OR_in_orExp112 = new BitSet(new long[]{0x0000000320918E00L});
    public static final BitSet FOLLOW_andExp_in_orExp115 = new BitSet(new long[]{0x0000000002000002L});
    public static final BitSet FOLLOW_eqExp_in_andExp128 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_AND_in_andExp131 = new BitSet(new long[]{0x0000000320918E00L});
    public static final BitSet FOLLOW_eqExp_in_andExp134 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_relExp_in_eqExp147 = new BitSet(new long[]{0x0000000001000082L});
    public static final BitSet FOLLOW_EQUALS_in_eqExp152 = new BitSet(new long[]{0x0000000320918E00L});
    public static final BitSet FOLLOW_NOTEQUALS_in_eqExp157 = new BitSet(new long[]{0x0000000320918E00L});
    public static final BitSet FOLLOW_relExp_in_eqExp161 = new BitSet(new long[]{0x0000000001000082L});
    public static final BitSet FOLLOW_addExp_in_relExp174 = new BitSet(new long[]{0x00000000000C6002L});
    public static final BitSet FOLLOW_LT_in_relExp178 = new BitSet(new long[]{0x0000000320918E00L});
    public static final BitSet FOLLOW_LTEQ_in_relExp183 = new BitSet(new long[]{0x0000000320918E00L});
    public static final BitSet FOLLOW_GT_in_relExp187 = new BitSet(new long[]{0x0000000320918E00L});
    public static final BitSet FOLLOW_GTEQ_in_relExp192 = new BitSet(new long[]{0x0000000320918E00L});
    public static final BitSet FOLLOW_addExp_in_relExp196 = new BitSet(new long[]{0x00000000000C6002L});
    public static final BitSet FOLLOW_multExp_in_addExp209 = new BitSet(new long[]{0x0000000004100002L});
    public static final BitSet FOLLOW_PLUS_in_addExp213 = new BitSet(new long[]{0x0000000320918E00L});
    public static final BitSet FOLLOW_MINUS_in_addExp218 = new BitSet(new long[]{0x0000000320918E00L});
    public static final BitSet FOLLOW_multExp_in_addExp222 = new BitSet(new long[]{0x0000000004100002L});
    public static final BitSet FOLLOW_unaryExp_in_multExp235 = new BitSet(new long[]{0x0000000008620042L});
    public static final BitSet FOLLOW_MULT_in_multExp240 = new BitSet(new long[]{0x0000000320918E00L});
    public static final BitSet FOLLOW_DIV_in_multExp245 = new BitSet(new long[]{0x0000000320918E00L});
    public static final BitSet FOLLOW_MOD_in_multExp250 = new BitSet(new long[]{0x0000000320918E00L});
    public static final BitSet FOLLOW_POW_in_multExp254 = new BitSet(new long[]{0x0000000320918E00L});
    public static final BitSet FOLLOW_IS_in_multExp258 = new BitSet(new long[]{0x0000000320918E00L});
    public static final BitSet FOLLOW_unaryExp_in_multExp262 = new BitSet(new long[]{0x0000000008620042L});
    public static final BitSet FOLLOW_NOT_in_unaryExp275 = new BitSet(new long[]{0x0000000320018E00L});
    public static final BitSet FOLLOW_atom_in_unaryExp277 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MINUS_in_unaryExp290 = new BitSet(new long[]{0x0000000320018E00L});
    public static final BitSet FOLLOW_atom_in_unaryExp292 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_atom_in_unaryExp305 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRUE_in_atom316 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FALSE_in_atom321 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_atom326 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_atom331 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_in_atom336 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_error_in_atom341 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_33_in_atom346 = new BitSet(new long[]{0x0000000320918E00L});
    public static final BitSet FOLLOW_exp_in_atom348 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_34_in_atom350 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_REFERENCE_in_atom359 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENT_in_function530 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_33_in_function532 = new BitSet(new long[]{0x0000000720918E00L});
    public static final BitSet FOLLOW_exp_in_function536 = new BitSet(new long[]{0x0000001C00000000L});
    public static final BitSet FOLLOW_35_in_function540 = new BitSet(new long[]{0x0000000320918E00L});
    public static final BitSet FOLLOW_36_in_function544 = new BitSet(new long[]{0x0000000320918E00L});
    public static final BitSet FOLLOW_exp_in_function547 = new BitSet(new long[]{0x0000001C00000000L});
    public static final BitSet FOLLOW_34_in_function554 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ERROR_STRING_in_error576 = new BitSet(new long[]{0x0000000000000002L});

}