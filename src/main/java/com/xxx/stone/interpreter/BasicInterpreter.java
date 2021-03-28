package com.xxx.stone.interpreter;

import com.xxx.stone.Lexer;
import com.xxx.stone.LexerRunner;
import com.xxx.stone.ParseException;
import com.xxx.stone.ast.AbstractSyntaxTree;
import com.xxx.stone.ast.BasicParser;
import com.xxx.stone.ast.NullStatement;
import com.xxx.stone.ast.Token;
import java.io.StringReader;

/**
 * @author 0x822a5b87
 */
public class BasicInterpreter {

    public static void main(String[] args) throws ParseException {
        BasicParser parser = new BasicParser();
        Environment environment = new BasicEnvironment();
        BasicInterpreter.run(parser, environment);
    }

    public static void run(BasicParser parser, Environment environment) throws ParseException {
        Lexer lexer = new Lexer(new StringReader(LexerRunner.STR_CODE));
        while (lexer.peek(0) != Token.EOF) {
            AbstractSyntaxTree t = parser.basicParse(lexer);
            if (!(t instanceof NullStatement)) {
                Object o = t.eval(environment);
                System.out.println("=>" + o);
            }
        }
    }
}
