package com.xxx.stone;

/**
 * @author 0x822a5b87
 */
public class LexerRunner {
    public static void main(String[] args) throws ParseException {
        Lexer l = new Lexer(new CodeDialog());
        for (Token t; (t = l.read()) != Token.EOF; )  {
            System.out.println("=> " + t.getText());
        }
    }
}
