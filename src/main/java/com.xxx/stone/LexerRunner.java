package com.xxx.stone;

import com.xxx.stone.ast.Token;
import java.io.InputStreamReader;

/**
 * @author 0x822a5b87
 */
public class LexerRunner {
    public static void main(String[] args) throws ParseException {
        Lexer lexer = new Lexer(new InputStreamReader(System.in));
        Token token;
        while ((token = lexer.read()) != null) {
            System.out.println(token.getText());
        }
    }
}
