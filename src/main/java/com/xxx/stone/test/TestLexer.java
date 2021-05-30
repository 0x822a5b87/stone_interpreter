package com.xxx.stone.test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;

/**
 * 解析正则表达式
 * <code>
 * \s*([0-9][0-9]*|[A-Za-z][A-Za-z0-9]*|=|==)
 * </code>
 * 的确定有限状态自动机
 *
 * @author 0x822a5b87
 */
public class TestLexer {

    public static final int EMPTY = -1;

    private final Reader reader;

    private int lastChar = EMPTY;

    public TestLexer(Reader reader) {
        this.reader = reader;
    }

    public String read() throws IOException {
        StringBuilder sb = new StringBuilder();
        int c;
        do {
            c = getChar();
        } while (isSpace(c));

        if (c < 0) {
            // END of text
            return null;
        } else if (Character.isDigit(c)) {
            do {
                sb.append((char)c);
                c = getChar();
            } while (Character.isDigit(c));
        } else if (Character.isLetter(c)) {
            do {
                sb.append((char)c);
                c = getChar();
            } while (Character.isLetter(c) || Character.isDigit(c));
        } else if (isEqual(c)) {
            sb.append((char)c);
            c = getChar();
            if (isEqual(c)) {
                sb.append((char)c);
                // 这里需要直接返回，而不执行后续的 c != EMPTY 的逻辑
                // 因为 == 是终结符，如果读到第二个 = 就必须返回了，否则会导致多执行一次 ungetChar
                return sb.toString();
            }
        }

        if (c != EMPTY) {
            ungetChar(c);
        }
        return sb.toString();
    }

    public int getChar() throws IOException {
        if (lastChar == EMPTY) {
            return reader.read();
        } else {
            int c = lastChar;
            lastChar = EMPTY;
            return c;
        }
    }

    public void ungetChar(int ch) {
        lastChar = ch;
    }

    private static boolean isSpace(int c) {
        return 0 <= c && c <= ' ';
    }

    private static boolean isEqual(int c) {
        return '=' == c;
    }

    public static void main(String[] args) throws IOException {
        TestLexer lexer = new TestLexer(new LineNumberReader(new InputStreamReader(System.in)));
        String token;
        while ((token = lexer.read()).length() > 0) {
            System.out.println(token);
        }
    }
}
