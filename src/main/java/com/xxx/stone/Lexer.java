package com.xxx.stone;

import com.xxx.stone.ast.Token;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 0x822a5b87
 *
 */
public class Lexer {

    public static final String COMMENT_PATTERN        = "//.*";
    public static final String NUMBER_LITERAL_PATTERN = "[0-9]+";
    public static final String STRING_LITERAL_PATTERN = "\"(\\\\\"|\\\\\\\\|\\\\n|[^\"])*\"";
    public static final String IDENTIFIER_PATTERN     = "[A-Z_a-z][A-Z_a-z0-9]*|==|<=|>=|&&|\\|\\||\\p{Punct}";

    public static final String TOTAL_PATTERN = String.format("\\s*((%s)|(%s)|(%s)|%s)?",
                                                             COMMENT_PATTERN,
                                                             NUMBER_LITERAL_PATTERN,
                                                             STRING_LITERAL_PATTERN,
                                                             IDENTIFIER_PATTERN);

    private final Pattern          pattern = Pattern.compile(TOTAL_PATTERN);
    private final List<Token>      queue   = new ArrayList<Token>();
    private final LineNumberReader reader;

    private boolean hasMore;

    public Lexer(Reader r) {
        hasMore = true;
        reader = new LineNumberReader(r);
    }

    public Token read() throws ParseException {
        if (fillQueue(0)) {
            return queue.remove(0);
        } else {
            return Token.EOF;
        }
    }

    public Token peek(int i) throws ParseException {
        if (fillQueue(i)) {
            return queue.get(i);
        } else {
            return Token.EOF;
        }
    }

    private boolean fillQueue(int i) throws ParseException {
        while (i >= queue.size()) {
            if (hasMore) {
                readLine();
            } else {
                return false;
            }
        }
        return true;
    }

    protected void readLine() throws ParseException {
        String line;
        try {
            line = reader.readLine();
        } catch (IOException e) {
            throw new ParseException(e);
        }
        if (line == null) {
            hasMore = false;
            return;
        }
        int lineNo = reader.getLineNumber();
        Matcher matcher = pattern.matcher(line);
        matcher.useTransparentBounds(true).useAnchoringBounds(false);
        int pos = 0, endPos = line.length();
        while (pos < endPos) {
            matcher.region(pos, endPos);
            if (matcher.lookingAt()) {
                addToToken(lineNo, matcher);
                pos = matcher.end();
            } else {
                throw new ParseException("bad token at line " + lineNo);
            }
        }
        // 这个不能少，因为 Token.EOL 是一个非常重要的终结符
        queue.add(new IdToken(lineNo, Token.EOL));
    }

    protected void addToToken(int lineNo, Matcher matcher) {
        String m = matcher.group(1);
        // if not a space
        if (m != null) {
            // if not a comment
            if (matcher.group(2) == null) {
                Token token;
                if (matcher.group(3) != null) {
                    token = new NumToken(lineNo, Integer.parseInt(m));
                } else if (matcher.group(4) != null) {
                    token = new StrToken(lineNo, toStringLiteral(m));
                } else {
                    token = new IdToken(lineNo, m);
                }

                queue.add(token);
            }
        }
    }

    protected String toStringLiteral(String s) {
        StringBuilder sb = new StringBuilder();
        int len = s.length() - 1;
        for (int i = 1; i < len; ++i) {
            char c = s.charAt(i);
            if (c == '\\' && i + 1 < len) {
                int c2 = s.charAt(i + 1);
                if (c2 == '"' || c2 == '\\') {
                    c = s.charAt(++i);
                } else if (c2 == 'n') {
                    ++i;
                    c = '\n';
                }
            }
            sb.append(c);
        }
        System.out.println("===" + sb + "===");
        return sb.toString();
    }

    protected static class NumToken extends Token {

        private final int value;

        protected NumToken(int line, int v) {
            super(line);
            value = v;
        }

        @Override
        public boolean isNumber() {
            return true;
        }

        @Override
        public String getText() {
            return Integer.toString(value);
        }

        @Override
        public int getNumber() {
            return value;
        }
    }

    protected static class IdToken extends Token {

        private final String text;

        protected IdToken(int line, String id) {
            super(line);
            text = id;
        }

        @Override
        public boolean isIdentifier() {
            return true;
        }

        @Override
        public String getText() {
            return text;
        }
    }

    protected static class StrToken extends Token {

        private final String literal;

        StrToken(int line, String str) {
            super(line);
            literal = str;
        }

        @Override
        public boolean isString() {
            return true;
        }

        @Override
        public String getText() {
            return literal;
        }
    }
}
