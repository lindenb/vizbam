/* Generated By:JavaCC: Do not edit this line. LocParser.java */
package com.github.lindenb.vizbam.locparser;
import net.sf.samtools.SAMSequenceDictionary;
import net.sf.samtools.SAMSequenceRecord;
import com.github.lindenb.vizbam.SAMSequencePosition;
import java.math.BigInteger;

/**
 * Author:
 *	Pierre Lindenbaum PhD
 * WWW
 *	http://plindenbaum.blogspot.com
 * Motivation
 *	parsing a genomic fragment (chr:start-end)
 *
 */
 @javax.annotation.Generated(value="javacc")
  @SuppressWarnings("unused")
public class LocParser implements LocParserConstants {
        private SAMSequenceDictionary samSequenceDictionary;
        private boolean auto_trim=true;
        private static final BigInteger TWO=new BigInteger("2");


        private SAMSequenceRecord getSequenceByName(String s)
                {
                for(SAMSequenceRecord ssr:this.samSequenceDictionary.getSequences())
                        {
                        if(ssr.getSequenceName().equals(s)) return ssr;
                        }
                return null;
                }


        public LocParser(SAMSequenceDictionary samSequenceDictionary, java.io.Reader r,boolean auto_trim)
                {
                this(r);
                this.samSequenceDictionary=samSequenceDictionary;
                this.auto_trim=auto_trim;
                }

        public static SAMSequencePosition parseOne(SAMSequenceDictionary samSequenceDictionary,String s,boolean auto_trim)
                {
                try
                        {
            LocParser parser = new LocParser(samSequenceDictionary,new java.io.StringReader(s),auto_trim);
                        return parser.one();
                        }
                catch(ParseException err)
                        {
                        return null;
                        }
                }

  final public SAMSequencePosition one() throws ParseException {
                                  SAMSequencePosition pos;
    pos = segment();
    jj_consume_token(0);
                {if (true) return pos;}
    throw new Error("Missing return statement in function");
  }

  final private SAMSequencePosition segment() throws ParseException {
                SAMSequenceRecord chrom;
                BigInteger start=null;
                BigInteger end=null ;
                char sign='?';
    chrom = chromName();
    jj_consume_token(COLON);
    start = position();
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case DASH:
    case PLUS:
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case DASH:
        jj_consume_token(DASH);
        end = position();
                                                                               sign='-';
        break;
      case PLUS:
        jj_consume_token(PLUS);
        end = position();
                                                                                                                  sign='+';
        break;
      default:
        jj_la1[0] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
      break;
    default:
      jj_la1[1] = jj_gen;
      ;
    }
                if(chrom==null) {if (true) return null;}
                switch(sign)
                        {
                        case '?': end=start;break;
                        case '-': break;
                        case '+': start=start.subtract(end);end=BigInteger.ONE.add(start.add(end.multiply(TWO)));break;
                        }
                BigInteger maxChrom=new BigInteger(String.valueOf(chrom.getSequenceLength()));
                if(start.compareTo(BigInteger.ONE)<0)
                        {
                        if(!this.auto_trim) {if (true) throw new ParseException(start.toString()+" < 0)");}
                        start=BigInteger.ONE;
                        }
                if(end.compareTo(start)<0)  {if (true) throw new ParseException(start.toString()+" > "+end+")");}
                if(maxChrom.compareTo(start)<0)
                        {
                        if(!this.auto_trim) {if (true) throw new ParseException(start.toString()+" > "+ maxChrom);}
                        start=maxChrom;
                        }
                if(maxChrom.compareTo(end)<0)
                        {
                        if(!this.auto_trim) {if (true) throw new ParseException(end.toString()+" > "+ maxChrom);}
                        end=maxChrom;
                        }
                int pos=(int)((start.longValue()+end.longValue())/2L);

                {if (true) return new SAMSequencePosition(chrom,pos);}
    throw new Error("Missing return statement in function");
  }

  final private BigInteger position() throws ParseException {
                                BigInteger i=null; BigInteger mul=BigInteger.ONE;
    i = integer();
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case BP:
    case KB:
    case MB:
    case GB:
      mul = factor();
      break;
    default:
      jj_la1[2] = jj_gen;
      ;
    }
                                      {if (true) return i.multiply(mul);}
    throw new Error("Missing return statement in function");
  }

  final private BigInteger factor() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case BP:
      jj_consume_token(BP);
                 {if (true) return new BigInteger("1");}
      break;
    case KB:
      jj_consume_token(KB);
                 {if (true) return new BigInteger("1000");}
      break;
    case MB:
      jj_consume_token(MB);
                 {if (true) return new BigInteger("1000000");}
      break;
    case GB:
      jj_consume_token(GB);
                 {if (true) return new BigInteger("1000000000");}
      break;
    default:
      jj_la1[3] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  final private BigInteger integer() throws ParseException {
                              Token t;
    t = jj_consume_token(INT);
                  {if (true) return new BigInteger(t.image.replace(",",""));}
    throw new Error("Missing return statement in function");
  }

  final private SAMSequenceRecord chromName() throws ParseException {
                                       BigInteger i; String s;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case INT:
      i = integer();
                        {if (true) return   getSequenceByName(i.toString());}
      break;
    case IDENTIFIER:
      s = identifier();
                           {if (true) return getSequenceByName(s);}
      break;
    default:
      jj_la1[4] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  final private String identifier() throws ParseException {
                             Token t;
    t = jj_consume_token(IDENTIFIER);
                         {if (true) return  t.image;}
    throw new Error("Missing return statement in function");
  }

  /** Generated Token Manager. */
  public LocParserTokenManager token_source;
  SimpleCharStream jj_input_stream;
  /** Current token. */
  public Token token;
  /** Next token. */
  public Token jj_nt;
  private int jj_ntk;
  private int jj_gen;
  final private int[] jj_la1 = new int[5];
  static private int[] jj_la1_0;
  static {
      jj_la1_init_0();
   }
   private static void jj_la1_init_0() {
      jj_la1_0 = new int[] {0x6000,0x6000,0x780,0x780,0x840,};
   }

  /** Constructor with InputStream. */
  public LocParser(java.io.InputStream stream) {
     this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public LocParser(java.io.InputStream stream, String encoding) {
    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new LocParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 5; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream) {
     ReInit(stream, null);
  }
  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream, String encoding) {
    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 5; i++) jj_la1[i] = -1;
  }

  /** Constructor. */
  public LocParser(java.io.Reader stream) {
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new LocParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 5; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 5; i++) jj_la1[i] = -1;
  }

  /** Constructor with generated Token Manager. */
  public LocParser(LocParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 5; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(LocParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 5; i++) jj_la1[i] = -1;
  }

  private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }


/** Get the next Token. */
  final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

/** Get the specific Token. */
  final public Token getToken(int index) {
    Token t = token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  private java.util.List<int[]> jj_expentries = new java.util.ArrayList<int[]>();
  private int[] jj_expentry;
  private int jj_kind = -1;

  /** Generate ParseException. */
  public ParseException generateParseException() {
    jj_expentries.clear();
    boolean[] la1tokens = new boolean[16];
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 5; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 16; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.add(jj_expentry);
      }
    }
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = jj_expentries.get(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  /** Enable tracing. */
  final public void enable_tracing() {
  }

  /** Disable tracing. */
  final public void disable_tracing() {
  }

        }