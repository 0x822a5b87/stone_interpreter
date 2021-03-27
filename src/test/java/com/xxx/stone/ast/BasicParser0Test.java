package com.xxx.stone.ast;

import com.xxx.stone.Lexer;
import com.xxx.stone.ParseException;
import java.io.StringReader;
import org.junit.Test;

public class BasicParser0Test {

    private static final String code = "even = 0\n"
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

//    private static final String code = "even = 0";

    @Test
    public void testParser() throws ParseException {
        Lexer lexer = new Lexer(new StringReader(code));
        StdBasicParser basicParser = new StdBasicParser();
        while (lexer.peek(0) != Token.EOF) {
            AbstractSyntaxTree t = basicParser.parse(lexer);
            System.out.println("=> " + t.toString());
        }
    }
}