// $ANTLR 3.4 ../src/org/exquisite/parser/Excel.g 2013-05-17 16:00:27
package org.exquisite.parser;

import org.antlr.runtime.BaseRecognizer;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.DFA;
import org.antlr.runtime.EarlyExitException;
import org.antlr.runtime.Lexer;
import org.antlr.runtime.MismatchedSetException;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;

@SuppressWarnings({"all", "warnings", "unchecked"})
public class ExcelLexer extends Lexer {
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
    // delegators
    public Lexer[] getDelegates() {
        return new Lexer[] {};
    }

    public ExcelLexer() {} 
    public ExcelLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public ExcelLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);
    }
    public String getGrammarFileName() { return "../src/org/exquisite/parser/Excel.g"; }

    // $ANTLR start "T__33"
    public final void mT__33() throws RecognitionException {
        try {
            int _type = T__33;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../src/org/exquisite/parser/Excel.g:4:7: ( '(' )
            // ../src/org/exquisite/parser/Excel.g:4:9: '('
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
    // $ANTLR end "T__33"

    // $ANTLR start "T__34"
    public final void mT__34() throws RecognitionException {
        try {
            int _type = T__34;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../src/org/exquisite/parser/Excel.g:5:7: ( ')' )
            // ../src/org/exquisite/parser/Excel.g:5:9: ')'
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
    // $ANTLR end "T__34"

    // $ANTLR start "T__35"
    public final void mT__35() throws RecognitionException {
        try {
            int _type = T__35;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../src/org/exquisite/parser/Excel.g:6:7: ( ',' )
            // ../src/org/exquisite/parser/Excel.g:6:9: ','
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
    // $ANTLR end "T__35"

    // $ANTLR start "T__36"
    public final void mT__36() throws RecognitionException {
        try {
            int _type = T__36;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../src/org/exquisite/parser/Excel.g:7:7: ( ';' )
            // ../src/org/exquisite/parser/Excel.g:7:9: ';'
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
    // $ANTLR end "T__36"

    // $ANTLR start "POW"
    public final void mPOW() throws RecognitionException {
        try {
            int _type = POW;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../src/org/exquisite/parser/Excel.g:72:5: ( '^' )
            // ../src/org/exquisite/parser/Excel.g:72:7: '^'
            {
            match('^'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "POW"

    // $ANTLR start "DIV"
    public final void mDIV() throws RecognitionException {
        try {
            int _type = DIV;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../src/org/exquisite/parser/Excel.g:73:5: ( '/' )
            // ../src/org/exquisite/parser/Excel.g:73:7: '/'
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

    // $ANTLR start "MOD"
    public final void mMOD() throws RecognitionException {
        try {
            int _type = MOD;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../src/org/exquisite/parser/Excel.g:74:5: ( '%' )
            // ../src/org/exquisite/parser/Excel.g:74:7: '%'
            {
            match('%'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "MOD"

    // $ANTLR start "MULT"
    public final void mMULT() throws RecognitionException {
        try {
            int _type = MULT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../src/org/exquisite/parser/Excel.g:75:6: ( '*' )
            // ../src/org/exquisite/parser/Excel.g:75:8: '*'
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

    // $ANTLR start "PLUS"
    public final void mPLUS() throws RecognitionException {
        try {
            int _type = PLUS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../src/org/exquisite/parser/Excel.g:76:6: ( '+' )
            // ../src/org/exquisite/parser/Excel.g:76:8: '+'
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
            // ../src/org/exquisite/parser/Excel.g:77:7: ( '-' )
            // ../src/org/exquisite/parser/Excel.g:77:9: '-'
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

    // $ANTLR start "LT"
    public final void mLT() throws RecognitionException {
        try {
            int _type = LT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../src/org/exquisite/parser/Excel.g:78:4: ( '<' )
            // ../src/org/exquisite/parser/Excel.g:78:6: '<'
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

    // $ANTLR start "LTEQ"
    public final void mLTEQ() throws RecognitionException {
        try {
            int _type = LTEQ;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../src/org/exquisite/parser/Excel.g:79:6: ( '<=' )
            // ../src/org/exquisite/parser/Excel.g:79:8: '<='
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
    // $ANTLR end "LTEQ"

    // $ANTLR start "GT"
    public final void mGT() throws RecognitionException {
        try {
            int _type = GT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../src/org/exquisite/parser/Excel.g:80:4: ( '>' )
            // ../src/org/exquisite/parser/Excel.g:80:6: '>'
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

    // $ANTLR start "GTEQ"
    public final void mGTEQ() throws RecognitionException {
        try {
            int _type = GTEQ;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../src/org/exquisite/parser/Excel.g:81:6: ( '>=' )
            // ../src/org/exquisite/parser/Excel.g:81:8: '>='
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
    // $ANTLR end "GTEQ"

    // $ANTLR start "EQUALS"
    public final void mEQUALS() throws RecognitionException {
        try {
            int _type = EQUALS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../src/org/exquisite/parser/Excel.g:82:8: ( '=' )
            // ../src/org/exquisite/parser/Excel.g:82:10: '='
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
    // $ANTLR end "EQUALS"

    // $ANTLR start "NOTEQUALS"
    public final void mNOTEQUALS() throws RecognitionException {
        try {
            int _type = NOTEQUALS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../src/org/exquisite/parser/Excel.g:84:2: ( '<>' )
            // ../src/org/exquisite/parser/Excel.g:84:4: '<>'
            {
            match("<>"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "NOTEQUALS"

    // $ANTLR start "INT"
    public final void mINT() throws RecognitionException {
        try {
            int _type = INT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../src/org/exquisite/parser/Excel.g:85:5: ( ( '0' .. '9' )+ )
            // ../src/org/exquisite/parser/Excel.g:85:7: ( '0' .. '9' )+
            {
            // ../src/org/exquisite/parser/Excel.g:85:7: ( '0' .. '9' )+
            int cnt1=0;
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( ((LA1_0 >= '0' && LA1_0 <= '9')) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // ../src/org/exquisite/parser/Excel.g:
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
                        EarlyExitException eee =
                            new EarlyExitException(1, input);
                        throw eee;
                }
                cnt1++;
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "INT"

    // $ANTLR start "FLOAT"
    public final void mFLOAT() throws RecognitionException {
        try {
            int _type = FLOAT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../src/org/exquisite/parser/Excel.g:86:7: ( ( '0' .. '9' )* '.' ( '0' .. '9' )+ )
            // ../src/org/exquisite/parser/Excel.g:86:9: ( '0' .. '9' )* '.' ( '0' .. '9' )+
            {
            // ../src/org/exquisite/parser/Excel.g:86:9: ( '0' .. '9' )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( ((LA2_0 >= '0' && LA2_0 <= '9')) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // ../src/org/exquisite/parser/Excel.g:
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
            	    break loop2;
                }
            } while (true);


            match('.'); 

            // ../src/org/exquisite/parser/Excel.g:86:25: ( '0' .. '9' )+
            int cnt3=0;
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( ((LA3_0 >= '0' && LA3_0 <= '9')) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // ../src/org/exquisite/parser/Excel.g:
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
            	    if ( cnt3 >= 1 ) break loop3;
                        EarlyExitException eee =
                            new EarlyExitException(3, input);
                        throw eee;
                }
                cnt3++;
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "FLOAT"

    // $ANTLR start "OR"
    public final void mOR() throws RecognitionException {
        try {
            int _type = OR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../src/org/exquisite/parser/Excel.g:87:4: ( 'or' )
            // ../src/org/exquisite/parser/Excel.g:87:6: 'or'
            {
            match("or"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "OR"

    // $ANTLR start "AND"
    public final void mAND() throws RecognitionException {
        try {
            int _type = AND;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../src/org/exquisite/parser/Excel.g:88:5: ( 'and' )
            // ../src/org/exquisite/parser/Excel.g:88:7: 'and'
            {
            match("and"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "AND"

    // $ANTLR start "IS"
    public final void mIS() throws RecognitionException {
        try {
            int _type = IS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../src/org/exquisite/parser/Excel.g:89:4: ( 'is' )
            // ../src/org/exquisite/parser/Excel.g:89:6: 'is'
            {
            match("is"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "IS"

    // $ANTLR start "NOT"
    public final void mNOT() throws RecognitionException {
        try {
            int _type = NOT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../src/org/exquisite/parser/Excel.g:90:5: ( 'not' )
            // ../src/org/exquisite/parser/Excel.g:90:7: 'not'
            {
            match("not"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "NOT"

    // $ANTLR start "TRUE"
    public final void mTRUE() throws RecognitionException {
        try {
            int _type = TRUE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../src/org/exquisite/parser/Excel.g:91:6: ( 'true' )
            // ../src/org/exquisite/parser/Excel.g:91:8: 'true'
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
            // ../src/org/exquisite/parser/Excel.g:92:7: ( 'false' )
            // ../src/org/exquisite/parser/Excel.g:92:9: 'false'
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

    // $ANTLR start "REFERENCE"
    public final void mREFERENCE() throws RecognitionException {
        try {
            int _type = REFERENCE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../src/org/exquisite/parser/Excel.g:101:2: ( ( '$' )? ( 'a' .. 'z' | 'A' .. 'Z' )+ ( '$' )? INT | ( '$' )? ( 'a' .. 'z' | 'A' .. 'Z' )+ ( '$' )? INT ':' ( '$' )? ( 'a' .. 'z' | 'A' .. 'Z' )+ ( '$' )? INT | IDENT '!' ( '$' )? ( 'a' .. 'z' | 'A' .. 'Z' )+ ( '$' )? INT | IDENT '!' ( '$' )? ( 'a' .. 'z' | 'A' .. 'Z' )+ ( '$' )? INT ':' ( '$' )? ( 'a' .. 'z' | 'A' .. 'Z' )+ ( '$' )? INT )
            int alt22=4;
            alt22 = dfa22.predict(input);
            switch (alt22) {
                case 1 :
                    // ../src/org/exquisite/parser/Excel.g:101:4: ( '$' )? ( 'a' .. 'z' | 'A' .. 'Z' )+ ( '$' )? INT
                    {
                    // ../src/org/exquisite/parser/Excel.g:101:4: ( '$' )?
                    int alt4=2;
                    int LA4_0 = input.LA(1);

                    if ( (LA4_0=='$') ) {
                        alt4=1;
                    }
                    switch (alt4) {
                        case 1 :
                            // ../src/org/exquisite/parser/Excel.g:101:5: '$'
                            {
                            match('$'); 

                            }
                            break;

                    }


                    // ../src/org/exquisite/parser/Excel.g:101:11: ( 'a' .. 'z' | 'A' .. 'Z' )+
                    int cnt5=0;
                    loop5:
                    do {
                        int alt5=2;
                        int LA5_0 = input.LA(1);

                        if ( ((LA5_0 >= 'A' && LA5_0 <= 'Z')||(LA5_0 >= 'a' && LA5_0 <= 'z')) ) {
                            alt5=1;
                        }


                        switch (alt5) {
                    	case 1 :
                    	    // ../src/org/exquisite/parser/Excel.g:
                    	    {
                    	    if ( (input.LA(1) >= 'A' && input.LA(1) <= 'Z')||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
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
                                EarlyExitException eee =
                                    new EarlyExitException(5, input);
                                throw eee;
                        }
                        cnt5++;
                    } while (true);


                    // ../src/org/exquisite/parser/Excel.g:101:34: ( '$' )?
                    int alt6=2;
                    int LA6_0 = input.LA(1);

                    if ( (LA6_0=='$') ) {
                        alt6=1;
                    }
                    switch (alt6) {
                        case 1 :
                            // ../src/org/exquisite/parser/Excel.g:101:35: '$'
                            {
                            match('$'); 

                            }
                            break;

                    }


                    mINT(); 


                    }
                    break;
                case 2 :
                    // ../src/org/exquisite/parser/Excel.g:102:4: ( '$' )? ( 'a' .. 'z' | 'A' .. 'Z' )+ ( '$' )? INT ':' ( '$' )? ( 'a' .. 'z' | 'A' .. 'Z' )+ ( '$' )? INT
                    {
                    // ../src/org/exquisite/parser/Excel.g:102:4: ( '$' )?
                    int alt7=2;
                    int LA7_0 = input.LA(1);

                    if ( (LA7_0=='$') ) {
                        alt7=1;
                    }
                    switch (alt7) {
                        case 1 :
                            // ../src/org/exquisite/parser/Excel.g:102:5: '$'
                            {
                            match('$'); 

                            }
                            break;

                    }


                    // ../src/org/exquisite/parser/Excel.g:102:11: ( 'a' .. 'z' | 'A' .. 'Z' )+
                    int cnt8=0;
                    loop8:
                    do {
                        int alt8=2;
                        int LA8_0 = input.LA(1);

                        if ( ((LA8_0 >= 'A' && LA8_0 <= 'Z')||(LA8_0 >= 'a' && LA8_0 <= 'z')) ) {
                            alt8=1;
                        }


                        switch (alt8) {
                    	case 1 :
                    	    // ../src/org/exquisite/parser/Excel.g:
                    	    {
                    	    if ( (input.LA(1) >= 'A' && input.LA(1) <= 'Z')||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
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
                    	    if ( cnt8 >= 1 ) break loop8;
                                EarlyExitException eee =
                                    new EarlyExitException(8, input);
                                throw eee;
                        }
                        cnt8++;
                    } while (true);


                    // ../src/org/exquisite/parser/Excel.g:102:34: ( '$' )?
                    int alt9=2;
                    int LA9_0 = input.LA(1);

                    if ( (LA9_0=='$') ) {
                        alt9=1;
                    }
                    switch (alt9) {
                        case 1 :
                            // ../src/org/exquisite/parser/Excel.g:102:35: '$'
                            {
                            match('$'); 

                            }
                            break;

                    }


                    mINT(); 


                    match(':'); 

                    // ../src/org/exquisite/parser/Excel.g:102:49: ( '$' )?
                    int alt10=2;
                    int LA10_0 = input.LA(1);

                    if ( (LA10_0=='$') ) {
                        alt10=1;
                    }
                    switch (alt10) {
                        case 1 :
                            // ../src/org/exquisite/parser/Excel.g:102:50: '$'
                            {
                            match('$'); 

                            }
                            break;

                    }


                    // ../src/org/exquisite/parser/Excel.g:102:56: ( 'a' .. 'z' | 'A' .. 'Z' )+
                    int cnt11=0;
                    loop11:
                    do {
                        int alt11=2;
                        int LA11_0 = input.LA(1);

                        if ( ((LA11_0 >= 'A' && LA11_0 <= 'Z')||(LA11_0 >= 'a' && LA11_0 <= 'z')) ) {
                            alt11=1;
                        }


                        switch (alt11) {
                    	case 1 :
                    	    // ../src/org/exquisite/parser/Excel.g:
                    	    {
                    	    if ( (input.LA(1) >= 'A' && input.LA(1) <= 'Z')||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
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
                    	    if ( cnt11 >= 1 ) break loop11;
                                EarlyExitException eee =
                                    new EarlyExitException(11, input);
                                throw eee;
                        }
                        cnt11++;
                    } while (true);


                    // ../src/org/exquisite/parser/Excel.g:102:79: ( '$' )?
                    int alt12=2;
                    int LA12_0 = input.LA(1);

                    if ( (LA12_0=='$') ) {
                        alt12=1;
                    }
                    switch (alt12) {
                        case 1 :
                            // ../src/org/exquisite/parser/Excel.g:102:80: '$'
                            {
                            match('$'); 

                            }
                            break;

                    }


                    mINT(); 


                    }
                    break;
                case 3 :
                    // ../src/org/exquisite/parser/Excel.g:103:4: IDENT '!' ( '$' )? ( 'a' .. 'z' | 'A' .. 'Z' )+ ( '$' )? INT
                    {
                    mIDENT(); 


                    match('!'); 

                    // ../src/org/exquisite/parser/Excel.g:103:14: ( '$' )?
                    int alt13=2;
                    int LA13_0 = input.LA(1);

                    if ( (LA13_0=='$') ) {
                        alt13=1;
                    }
                    switch (alt13) {
                        case 1 :
                            // ../src/org/exquisite/parser/Excel.g:103:15: '$'
                            {
                            match('$'); 

                            }
                            break;

                    }


                    // ../src/org/exquisite/parser/Excel.g:103:21: ( 'a' .. 'z' | 'A' .. 'Z' )+
                    int cnt14=0;
                    loop14:
                    do {
                        int alt14=2;
                        int LA14_0 = input.LA(1);

                        if ( ((LA14_0 >= 'A' && LA14_0 <= 'Z')||(LA14_0 >= 'a' && LA14_0 <= 'z')) ) {
                            alt14=1;
                        }


                        switch (alt14) {
                    	case 1 :
                    	    // ../src/org/exquisite/parser/Excel.g:
                    	    {
                    	    if ( (input.LA(1) >= 'A' && input.LA(1) <= 'Z')||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
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
                    	    if ( cnt14 >= 1 ) break loop14;
                                EarlyExitException eee =
                                    new EarlyExitException(14, input);
                                throw eee;
                        }
                        cnt14++;
                    } while (true);


                    // ../src/org/exquisite/parser/Excel.g:103:44: ( '$' )?
                    int alt15=2;
                    int LA15_0 = input.LA(1);

                    if ( (LA15_0=='$') ) {
                        alt15=1;
                    }
                    switch (alt15) {
                        case 1 :
                            // ../src/org/exquisite/parser/Excel.g:103:45: '$'
                            {
                            match('$'); 

                            }
                            break;

                    }


                    mINT(); 


                    }
                    break;
                case 4 :
                    // ../src/org/exquisite/parser/Excel.g:104:4: IDENT '!' ( '$' )? ( 'a' .. 'z' | 'A' .. 'Z' )+ ( '$' )? INT ':' ( '$' )? ( 'a' .. 'z' | 'A' .. 'Z' )+ ( '$' )? INT
                    {
                    mIDENT(); 


                    match('!'); 

                    // ../src/org/exquisite/parser/Excel.g:104:14: ( '$' )?
                    int alt16=2;
                    int LA16_0 = input.LA(1);

                    if ( (LA16_0=='$') ) {
                        alt16=1;
                    }
                    switch (alt16) {
                        case 1 :
                            // ../src/org/exquisite/parser/Excel.g:104:15: '$'
                            {
                            match('$'); 

                            }
                            break;

                    }


                    // ../src/org/exquisite/parser/Excel.g:104:21: ( 'a' .. 'z' | 'A' .. 'Z' )+
                    int cnt17=0;
                    loop17:
                    do {
                        int alt17=2;
                        int LA17_0 = input.LA(1);

                        if ( ((LA17_0 >= 'A' && LA17_0 <= 'Z')||(LA17_0 >= 'a' && LA17_0 <= 'z')) ) {
                            alt17=1;
                        }


                        switch (alt17) {
                    	case 1 :
                    	    // ../src/org/exquisite/parser/Excel.g:
                    	    {
                    	    if ( (input.LA(1) >= 'A' && input.LA(1) <= 'Z')||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
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
                    	    if ( cnt17 >= 1 ) break loop17;
                                EarlyExitException eee =
                                    new EarlyExitException(17, input);
                                throw eee;
                        }
                        cnt17++;
                    } while (true);


                    // ../src/org/exquisite/parser/Excel.g:104:44: ( '$' )?
                    int alt18=2;
                    int LA18_0 = input.LA(1);

                    if ( (LA18_0=='$') ) {
                        alt18=1;
                    }
                    switch (alt18) {
                        case 1 :
                            // ../src/org/exquisite/parser/Excel.g:104:45: '$'
                            {
                            match('$'); 

                            }
                            break;

                    }


                    mINT(); 


                    match(':'); 

                    // ../src/org/exquisite/parser/Excel.g:104:59: ( '$' )?
                    int alt19=2;
                    int LA19_0 = input.LA(1);

                    if ( (LA19_0=='$') ) {
                        alt19=1;
                    }
                    switch (alt19) {
                        case 1 :
                            // ../src/org/exquisite/parser/Excel.g:104:60: '$'
                            {
                            match('$'); 

                            }
                            break;

                    }


                    // ../src/org/exquisite/parser/Excel.g:104:66: ( 'a' .. 'z' | 'A' .. 'Z' )+
                    int cnt20=0;
                    loop20:
                    do {
                        int alt20=2;
                        int LA20_0 = input.LA(1);

                        if ( ((LA20_0 >= 'A' && LA20_0 <= 'Z')||(LA20_0 >= 'a' && LA20_0 <= 'z')) ) {
                            alt20=1;
                        }


                        switch (alt20) {
                    	case 1 :
                    	    // ../src/org/exquisite/parser/Excel.g:
                    	    {
                    	    if ( (input.LA(1) >= 'A' && input.LA(1) <= 'Z')||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
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
                    	    if ( cnt20 >= 1 ) break loop20;
                                EarlyExitException eee =
                                    new EarlyExitException(20, input);
                                throw eee;
                        }
                        cnt20++;
                    } while (true);


                    // ../src/org/exquisite/parser/Excel.g:104:89: ( '$' )?
                    int alt21=2;
                    int LA21_0 = input.LA(1);

                    if ( (LA21_0=='$') ) {
                        alt21=1;
                    }
                    switch (alt21) {
                        case 1 :
                            // ../src/org/exquisite/parser/Excel.g:104:90: '$'
                            {
                            match('$'); 

                            }
                            break;

                    }


                    mINT(); 


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
    // $ANTLR end "REFERENCE"

    // $ANTLR start "R1C1_REFERENCE"
    public final void mR1C1_REFERENCE() throws RecognitionException {
        try {
            int _type = R1C1_REFERENCE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../src/org/exquisite/parser/Excel.g:108:2: ( ( 'R' ) ( INT )? ( 'C' ) | ( 'R' ) ( '[' ( ( '-' | '+' )? INT ) ']' )? )
            int alt26=2;
            int LA26_0 = input.LA(1);

            if ( (LA26_0=='R') ) {
                int LA26_1 = input.LA(2);

                if ( ((LA26_1 >= '0' && LA26_1 <= '9')||LA26_1=='C') ) {
                    alt26=1;
                }
                else {
                    alt26=2;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 26, 0, input);

                throw nvae;

            }
            switch (alt26) {
                case 1 :
                    // ../src/org/exquisite/parser/Excel.g:108:4: ( 'R' ) ( INT )? ( 'C' )
                    {
                    // ../src/org/exquisite/parser/Excel.g:108:4: ( 'R' )
                    // ../src/org/exquisite/parser/Excel.g:108:5: 'R'
                    {
                    match('R'); 

                    }


                    // ../src/org/exquisite/parser/Excel.g:108:10: ( INT )?
                    int alt23=2;
                    int LA23_0 = input.LA(1);

                    if ( ((LA23_0 >= '0' && LA23_0 <= '9')) ) {
                        alt23=1;
                    }
                    switch (alt23) {
                        case 1 :
                            // ../src/org/exquisite/parser/Excel.g:108:11: INT
                            {
                            mINT(); 


                            }
                            break;

                    }


                    // ../src/org/exquisite/parser/Excel.g:108:17: ( 'C' )
                    // ../src/org/exquisite/parser/Excel.g:108:18: 'C'
                    {
                    match('C'); 

                    }


                    }
                    break;
                case 2 :
                    // ../src/org/exquisite/parser/Excel.g:109:4: ( 'R' ) ( '[' ( ( '-' | '+' )? INT ) ']' )?
                    {
                    // ../src/org/exquisite/parser/Excel.g:109:4: ( 'R' )
                    // ../src/org/exquisite/parser/Excel.g:109:5: 'R'
                    {
                    match('R'); 

                    }


                    // ../src/org/exquisite/parser/Excel.g:109:10: ( '[' ( ( '-' | '+' )? INT ) ']' )?
                    int alt25=2;
                    int LA25_0 = input.LA(1);

                    if ( (LA25_0=='[') ) {
                        alt25=1;
                    }
                    switch (alt25) {
                        case 1 :
                            // ../src/org/exquisite/parser/Excel.g:109:11: '[' ( ( '-' | '+' )? INT ) ']'
                            {
                            match('['); 

                            // ../src/org/exquisite/parser/Excel.g:109:15: ( ( '-' | '+' )? INT )
                            // ../src/org/exquisite/parser/Excel.g:109:16: ( '-' | '+' )? INT
                            {
                            // ../src/org/exquisite/parser/Excel.g:109:16: ( '-' | '+' )?
                            int alt24=2;
                            int LA24_0 = input.LA(1);

                            if ( (LA24_0=='+'||LA24_0=='-') ) {
                                alt24=1;
                            }
                            switch (alt24) {
                                case 1 :
                                    // ../src/org/exquisite/parser/Excel.g:
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


                            mINT(); 


                            }


                            match(']'); 

                            }
                            break;

                    }


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
    // $ANTLR end "R1C1_REFERENCE"

    // $ANTLR start "IDENT"
    public final void mIDENT() throws RecognitionException {
        try {
            int _type = IDENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../src/org/exquisite/parser/Excel.g:113:2: ( ( 'a' .. 'z' | 'A' .. 'Z' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' )* )
            // ../src/org/exquisite/parser/Excel.g:113:5: ( 'a' .. 'z' | 'A' .. 'Z' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' )*
            {
            if ( (input.LA(1) >= 'A' && input.LA(1) <= 'Z')||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            // ../src/org/exquisite/parser/Excel.g:113:27: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' )*
            loop27:
            do {
                int alt27=2;
                int LA27_0 = input.LA(1);

                if ( ((LA27_0 >= '0' && LA27_0 <= '9')||(LA27_0 >= 'A' && LA27_0 <= 'Z')||(LA27_0 >= 'a' && LA27_0 <= 'z')) ) {
                    alt27=1;
                }


                switch (alt27) {
            	case 1 :
            	    // ../src/org/exquisite/parser/Excel.g:
            	    {
            	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9')||(input.LA(1) >= 'A' && input.LA(1) <= 'Z')||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
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
            	    break loop27;
                }
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "IDENT"

    // $ANTLR start "ERROR_STRING"
    public final void mERROR_STRING() throws RecognitionException {
        try {
            int _type = ERROR_STRING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../src/org/exquisite/parser/Excel.g:117:2: ( '#DIV/0!' | '#N/A' | '#NAME?' | '#NULL!' | '#NUM!' | '#REF!' | '#VALUE!' | '#####' )
            int alt28=8;
            int LA28_0 = input.LA(1);

            if ( (LA28_0=='#') ) {
                switch ( input.LA(2) ) {
                case 'D':
                    {
                    alt28=1;
                    }
                    break;
                case 'N':
                    {
                    switch ( input.LA(3) ) {
                    case '/':
                        {
                        alt28=2;
                        }
                        break;
                    case 'A':
                        {
                        alt28=3;
                        }
                        break;
                    case 'U':
                        {
                        int LA28_9 = input.LA(4);

                        if ( (LA28_9=='L') ) {
                            alt28=4;
                        }
                        else if ( (LA28_9=='M') ) {
                            alt28=5;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 28, 9, input);

                            throw nvae;

                        }
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("", 28, 3, input);

                        throw nvae;

                    }

                    }
                    break;
                case 'R':
                    {
                    alt28=6;
                    }
                    break;
                case 'V':
                    {
                    alt28=7;
                    }
                    break;
                case '#':
                    {
                    alt28=8;
                    }
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 28, 1, input);

                    throw nvae;

                }

            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 28, 0, input);

                throw nvae;

            }
            switch (alt28) {
                case 1 :
                    // ../src/org/exquisite/parser/Excel.g:117:4: '#DIV/0!'
                    {
                    match("#DIV/0!"); 



                    }
                    break;
                case 2 :
                    // ../src/org/exquisite/parser/Excel.g:118:4: '#N/A'
                    {
                    match("#N/A"); 



                    }
                    break;
                case 3 :
                    // ../src/org/exquisite/parser/Excel.g:119:4: '#NAME?'
                    {
                    match("#NAME?"); 



                    }
                    break;
                case 4 :
                    // ../src/org/exquisite/parser/Excel.g:120:4: '#NULL!'
                    {
                    match("#NULL!"); 



                    }
                    break;
                case 5 :
                    // ../src/org/exquisite/parser/Excel.g:121:4: '#NUM!'
                    {
                    match("#NUM!"); 



                    }
                    break;
                case 6 :
                    // ../src/org/exquisite/parser/Excel.g:122:4: '#REF!'
                    {
                    match("#REF!"); 



                    }
                    break;
                case 7 :
                    // ../src/org/exquisite/parser/Excel.g:123:4: '#VALUE!'
                    {
                    match("#VALUE!"); 



                    }
                    break;
                case 8 :
                    // ../src/org/exquisite/parser/Excel.g:124:4: '#####'
                    {
                    match("#####"); 



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
    // $ANTLR end "ERROR_STRING"

    // $ANTLR start "SPACE"
    public final void mSPACE() throws RecognitionException {
        try {
            int _type = SPACE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../src/org/exquisite/parser/Excel.g:127:7: ( ( ' ' | '\\t' | '\\r' | '\\n' ) )
            // ../src/org/exquisite/parser/Excel.g:127:9: ( ' ' | '\\t' | '\\r' | '\\n' )
            {
            if ( (input.LA(1) >= '\t' && input.LA(1) <= '\n')||input.LA(1)=='\r'||input.LA(1)==' ' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            skip();

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "SPACE"

    public void mTokens() throws RecognitionException {
        // ../src/org/exquisite/parser/Excel.g:1:8: ( T__33 | T__34 | T__35 | T__36 | POW | DIV | MOD | MULT | PLUS | MINUS | LT | LTEQ | GT | GTEQ | EQUALS | NOTEQUALS | INT | FLOAT | OR | AND | IS | NOT | TRUE | FALSE | REFERENCE | R1C1_REFERENCE | IDENT | ERROR_STRING | SPACE )
        int alt29=29;
        alt29 = dfa29.predict(input);
        switch (alt29) {
            case 1 :
                // ../src/org/exquisite/parser/Excel.g:1:10: T__33
                {
                mT__33(); 


                }
                break;
            case 2 :
                // ../src/org/exquisite/parser/Excel.g:1:16: T__34
                {
                mT__34(); 


                }
                break;
            case 3 :
                // ../src/org/exquisite/parser/Excel.g:1:22: T__35
                {
                mT__35(); 


                }
                break;
            case 4 :
                // ../src/org/exquisite/parser/Excel.g:1:28: T__36
                {
                mT__36(); 


                }
                break;
            case 5 :
                // ../src/org/exquisite/parser/Excel.g:1:34: POW
                {
                mPOW(); 


                }
                break;
            case 6 :
                // ../src/org/exquisite/parser/Excel.g:1:38: DIV
                {
                mDIV(); 


                }
                break;
            case 7 :
                // ../src/org/exquisite/parser/Excel.g:1:42: MOD
                {
                mMOD(); 


                }
                break;
            case 8 :
                // ../src/org/exquisite/parser/Excel.g:1:46: MULT
                {
                mMULT(); 


                }
                break;
            case 9 :
                // ../src/org/exquisite/parser/Excel.g:1:51: PLUS
                {
                mPLUS(); 


                }
                break;
            case 10 :
                // ../src/org/exquisite/parser/Excel.g:1:56: MINUS
                {
                mMINUS(); 


                }
                break;
            case 11 :
                // ../src/org/exquisite/parser/Excel.g:1:62: LT
                {
                mLT(); 


                }
                break;
            case 12 :
                // ../src/org/exquisite/parser/Excel.g:1:65: LTEQ
                {
                mLTEQ(); 


                }
                break;
            case 13 :
                // ../src/org/exquisite/parser/Excel.g:1:70: GT
                {
                mGT(); 


                }
                break;
            case 14 :
                // ../src/org/exquisite/parser/Excel.g:1:73: GTEQ
                {
                mGTEQ(); 


                }
                break;
            case 15 :
                // ../src/org/exquisite/parser/Excel.g:1:78: EQUALS
                {
                mEQUALS(); 


                }
                break;
            case 16 :
                // ../src/org/exquisite/parser/Excel.g:1:85: NOTEQUALS
                {
                mNOTEQUALS(); 


                }
                break;
            case 17 :
                // ../src/org/exquisite/parser/Excel.g:1:95: INT
                {
                mINT(); 


                }
                break;
            case 18 :
                // ../src/org/exquisite/parser/Excel.g:1:99: FLOAT
                {
                mFLOAT(); 


                }
                break;
            case 19 :
                // ../src/org/exquisite/parser/Excel.g:1:105: OR
                {
                mOR(); 


                }
                break;
            case 20 :
                // ../src/org/exquisite/parser/Excel.g:1:108: AND
                {
                mAND(); 


                }
                break;
            case 21 :
                // ../src/org/exquisite/parser/Excel.g:1:112: IS
                {
                mIS(); 


                }
                break;
            case 22 :
                // ../src/org/exquisite/parser/Excel.g:1:115: NOT
                {
                mNOT(); 


                }
                break;
            case 23 :
                // ../src/org/exquisite/parser/Excel.g:1:119: TRUE
                {
                mTRUE(); 


                }
                break;
            case 24 :
                // ../src/org/exquisite/parser/Excel.g:1:124: FALSE
                {
                mFALSE(); 


                }
                break;
            case 25 :
                // ../src/org/exquisite/parser/Excel.g:1:130: REFERENCE
                {
                mREFERENCE(); 


                }
                break;
            case 26 :
                // ../src/org/exquisite/parser/Excel.g:1:140: R1C1_REFERENCE
                {
                mR1C1_REFERENCE(); 


                }
                break;
            case 27 :
                // ../src/org/exquisite/parser/Excel.g:1:155: IDENT
                {
                mIDENT(); 


                }
                break;
            case 28 :
                // ../src/org/exquisite/parser/Excel.g:1:161: ERROR_STRING
                {
                mERROR_STRING(); 


                }
                break;
            case 29 :
                // ../src/org/exquisite/parser/Excel.g:1:174: SPACE
                {
                mSPACE(); 


                }
                break;

        }

    }


    protected DFA22 dfa22 = new DFA22(this);
    protected DFA29 dfa29 = new DFA29(this);
    static final String DFA22_eotS =
        "\5\uffff\1\11\2\uffff\1\11\6\uffff\1\20\2\uffff";
    static final String DFA22_eofS =
        "\22\uffff";
    static final String DFA22_minS =
        "\1\44\1\101\1\41\1\44\1\60\2\41\1\44\1\60\2\uffff\1\41\1\101\1\44"+
        "\2\60\2\uffff";
    static final String DFA22_maxS =
        "\4\172\1\71\3\172\1\72\2\uffff\3\172\1\71\1\72\2\uffff";
    static final String DFA22_acceptS =
        "\11\uffff\1\1\1\2\5\uffff\1\3\1\4";
    static final String DFA22_specialS =
        "\22\uffff}>";
    static final String[] DFA22_transitionS = {
            "\1\1\34\uffff\32\2\6\uffff\32\2",
            "\32\3\6\uffff\32\3",
            "\1\7\2\uffff\1\4\13\uffff\12\5\7\uffff\32\6\6\uffff\32\6",
            "\1\4\13\uffff\12\10\7\uffff\32\3\6\uffff\32\3",
            "\12\10",
            "\1\7\16\uffff\12\5\1\12\6\uffff\32\13\6\uffff\32\13",
            "\1\7\2\uffff\1\4\13\uffff\12\5\7\uffff\32\6\6\uffff\32\6",
            "\1\14\34\uffff\32\15\6\uffff\32\15",
            "\12\10\1\12",
            "",
            "",
            "\1\7\16\uffff\12\13\7\uffff\32\13\6\uffff\32\13",
            "\32\15\6\uffff\32\15",
            "\1\16\13\uffff\12\17\7\uffff\32\15\6\uffff\32\15",
            "\12\17",
            "\12\17\1\21",
            "",
            ""
    };

    static final short[] DFA22_eot = DFA.unpackEncodedString(DFA22_eotS);
    static final short[] DFA22_eof = DFA.unpackEncodedString(DFA22_eofS);
    static final char[] DFA22_min = DFA.unpackEncodedStringToUnsignedChars(DFA22_minS);
    static final char[] DFA22_max = DFA.unpackEncodedStringToUnsignedChars(DFA22_maxS);
    static final short[] DFA22_accept = DFA.unpackEncodedString(DFA22_acceptS);
    static final short[] DFA22_special = DFA.unpackEncodedString(DFA22_specialS);
    static final short[][] DFA22_transition;

    static {
        int numStates = DFA22_transitionS.length;
        DFA22_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA22_transition[i] = DFA.unpackEncodedString(DFA22_transitionS[i]);
        }
    }

    class DFA22 extends DFA {

        public DFA22(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 22;
            this.eot = DFA22_eot;
            this.eof = DFA22_eof;
            this.min = DFA22_min;
            this.max = DFA22_max;
            this.accept = DFA22_accept;
            this.special = DFA22_special;
            this.transition = DFA22_transition;
        }
        public String getDescription() {
            return "100:1: REFERENCE : ( ( '$' )? ( 'a' .. 'z' | 'A' .. 'Z' )+ ( '$' )? INT | ( '$' )? ( 'a' .. 'z' | 'A' .. 'Z' )+ ( '$' )? INT ':' ( '$' )? ( 'a' .. 'z' | 'A' .. 'Z' )+ ( '$' )? INT | IDENT '!' ( '$' )? ( 'a' .. 'z' | 'A' .. 'Z' )+ ( '$' )? INT | IDENT '!' ( '$' )? ( 'a' .. 'z' | 'A' .. 'Z' )+ ( '$' )? INT ':' ( '$' )? ( 'a' .. 'z' | 'A' .. 'Z' )+ ( '$' )? INT );";
        }
    }
    static final String DFA29_eotS =
        "\13\uffff\1\35\1\37\1\uffff\1\40\1\uffff\6\44\1\uffff\1\54\1\44"+
        "\10\uffff\1\55\1\26\1\44\1\uffff\1\44\1\60\3\44\1\26\1\54\2\uffff"+
        "\1\44\1\65\1\uffff\1\66\2\44\1\54\2\uffff\1\71\1\44\1\uffff\1\73"+
        "\1\uffff";
    static final String DFA29_eofS =
        "\74\uffff";
    static final String DFA29_minS =
        "\1\11\12\uffff\2\75\1\uffff\1\56\1\uffff\6\41\1\uffff\2\41\10\uffff"+
        "\1\41\1\60\1\41\1\uffff\5\41\1\60\1\41\2\uffff\2\41\1\uffff\4\41"+
        "\2\uffff\2\41\1\uffff\1\41\1\uffff";
    static final String DFA29_maxS =
        "\1\172\12\uffff\1\76\1\75\1\uffff\1\71\1\uffff\6\172\1\uffff\2\172"+
        "\10\uffff\3\172\1\uffff\7\172\2\uffff\2\172\1\uffff\4\172\2\uffff"+
        "\2\172\1\uffff\1\172\1\uffff";
    static final String DFA29_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\2\uffff\1\17"+
        "\1\uffff\1\22\6\uffff\1\31\2\uffff\1\34\1\35\1\14\1\20\1\13\1\16"+
        "\1\15\1\21\3\uffff\1\33\7\uffff\1\32\1\23\2\uffff\1\25\4\uffff\1"+
        "\24\1\26\2\uffff\1\27\1\uffff\1\30";
    static final String DFA29_specialS =
        "\74\uffff}>";
    static final String[] DFA29_transitionS = {
            "\2\32\2\uffff\1\32\22\uffff\1\32\2\uffff\1\31\1\26\1\7\2\uffff"+
            "\1\1\1\2\1\10\1\11\1\3\1\12\1\17\1\6\12\16\1\uffff\1\4\1\13"+
            "\1\15\1\14\2\uffff\21\30\1\27\10\30\3\uffff\1\5\2\uffff\1\21"+
            "\4\30\1\25\2\30\1\22\4\30\1\23\1\20\4\30\1\24\6\30",
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
            "\1\33\1\34",
            "\1\36",
            "",
            "\1\17\1\uffff\12\16",
            "",
            "\1\26\2\uffff\1\26\13\uffff\12\42\7\uffff\32\43\6\uffff\21"+
            "\43\1\41\10\43",
            "\1\26\2\uffff\1\26\13\uffff\12\42\7\uffff\32\43\6\uffff\15"+
            "\43\1\45\14\43",
            "\1\26\2\uffff\1\26\13\uffff\12\42\7\uffff\32\43\6\uffff\22"+
            "\43\1\46\7\43",
            "\1\26\2\uffff\1\26\13\uffff\12\42\7\uffff\32\43\6\uffff\16"+
            "\43\1\47\13\43",
            "\1\26\2\uffff\1\26\13\uffff\12\42\7\uffff\32\43\6\uffff\21"+
            "\43\1\50\10\43",
            "\1\26\2\uffff\1\26\13\uffff\12\42\7\uffff\32\43\6\uffff\1\51"+
            "\31\43",
            "",
            "\1\26\2\uffff\1\26\13\uffff\12\52\7\uffff\2\43\1\53\27\43\6"+
            "\uffff\32\43",
            "\1\26\2\uffff\1\26\13\uffff\12\42\7\uffff\32\43\6\uffff\32"+
            "\43",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\26\2\uffff\1\26\13\uffff\12\42\7\uffff\32\43\6\uffff\32"+
            "\43",
            "\12\42\7\uffff\32\56\6\uffff\32\56",
            "\1\26\2\uffff\1\26\13\uffff\12\42\7\uffff\32\43\6\uffff\32"+
            "\43",
            "",
            "\1\26\2\uffff\1\26\13\uffff\12\42\7\uffff\32\43\6\uffff\3\43"+
            "\1\57\26\43",
            "\1\26\2\uffff\1\26\13\uffff\12\42\7\uffff\32\43\6\uffff\32"+
            "\43",
            "\1\26\2\uffff\1\26\13\uffff\12\42\7\uffff\32\43\6\uffff\23"+
            "\43\1\61\6\43",
            "\1\26\2\uffff\1\26\13\uffff\12\42\7\uffff\32\43\6\uffff\24"+
            "\43\1\62\5\43",
            "\1\26\2\uffff\1\26\13\uffff\12\42\7\uffff\32\43\6\uffff\13"+
            "\43\1\63\16\43",
            "\12\52\7\uffff\2\56\1\64\27\56\6\uffff\32\56",
            "\1\26\2\uffff\1\26\13\uffff\12\42\7\uffff\32\43\6\uffff\32"+
            "\43",
            "",
            "",
            "\1\26\16\uffff\12\56\7\uffff\32\56\6\uffff\32\56",
            "\1\26\2\uffff\1\26\13\uffff\12\42\7\uffff\32\43\6\uffff\32"+
            "\43",
            "",
            "\1\26\2\uffff\1\26\13\uffff\12\42\7\uffff\32\43\6\uffff\32"+
            "\43",
            "\1\26\2\uffff\1\26\13\uffff\12\42\7\uffff\32\43\6\uffff\4\43"+
            "\1\67\25\43",
            "\1\26\2\uffff\1\26\13\uffff\12\42\7\uffff\32\43\6\uffff\22"+
            "\43\1\70\7\43",
            "\1\26\16\uffff\12\56\7\uffff\32\56\6\uffff\32\56",
            "",
            "",
            "\1\26\2\uffff\1\26\13\uffff\12\42\7\uffff\32\43\6\uffff\32"+
            "\43",
            "\1\26\2\uffff\1\26\13\uffff\12\42\7\uffff\32\43\6\uffff\4\43"+
            "\1\72\25\43",
            "",
            "\1\26\2\uffff\1\26\13\uffff\12\42\7\uffff\32\43\6\uffff\32"+
            "\43",
            ""
    };

    static final short[] DFA29_eot = DFA.unpackEncodedString(DFA29_eotS);
    static final short[] DFA29_eof = DFA.unpackEncodedString(DFA29_eofS);
    static final char[] DFA29_min = DFA.unpackEncodedStringToUnsignedChars(DFA29_minS);
    static final char[] DFA29_max = DFA.unpackEncodedStringToUnsignedChars(DFA29_maxS);
    static final short[] DFA29_accept = DFA.unpackEncodedString(DFA29_acceptS);
    static final short[] DFA29_special = DFA.unpackEncodedString(DFA29_specialS);
    static final short[][] DFA29_transition;

    static {
        int numStates = DFA29_transitionS.length;
        DFA29_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA29_transition[i] = DFA.unpackEncodedString(DFA29_transitionS[i]);
        }
    }

    class DFA29 extends DFA {

        public DFA29(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 29;
            this.eot = DFA29_eot;
            this.eof = DFA29_eof;
            this.min = DFA29_min;
            this.max = DFA29_max;
            this.accept = DFA29_accept;
            this.special = DFA29_special;
            this.transition = DFA29_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( T__33 | T__34 | T__35 | T__36 | POW | DIV | MOD | MULT | PLUS | MINUS | LT | LTEQ | GT | GTEQ | EQUALS | NOTEQUALS | INT | FLOAT | OR | AND | IS | NOT | TRUE | FALSE | REFERENCE | R1C1_REFERENCE | IDENT | ERROR_STRING | SPACE );";
        }
    }
 

}