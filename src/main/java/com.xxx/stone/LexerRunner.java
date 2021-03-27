package com.xxx.stone;

import com.xxx.stone.ast.AbstractSyntaxTree;
import com.xxx.stone.ast.BasicParser;
import com.xxx.stone.ast.Token;
import java.io.StringReader;

/**
 * @author 0x822a5b87
 */
public class LexerRunner {

    private static final String STR_CODE = "even = 0\n"
                                           + "odd = 0\n"
                                           + "i = 1\n"
                                           + "while i < 10 {\n"
                                           + "    if i % 2 == 0 {\n"
                                           + "        even = even + i\n"
                                           + "    } else {\n"
                                           + "        odd = odd + i\n"
                                           + "    }\n"
                                           + "    i = i + 1\n"
                                           + "}\n"
                                           + "even + odd";

    public static void main(String[] args) throws ParseException {
        Lexer lexer = new Lexer(new StringReader(STR_CODE));
        BasicParser basicParser = new BasicParser();
        while (lexer.peek(0) != Token.EOF) {
            AbstractSyntaxTree t = basicParser.basicParse(lexer);
            System.out.println("=> " + t.toString());
        }
    }
}
