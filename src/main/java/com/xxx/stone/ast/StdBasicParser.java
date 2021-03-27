package com.xxx.stone.ast;


import static com.xxx.stone.StdParser.rule;

import com.xxx.stone.Lexer;
import com.xxx.stone.ParseException;
import com.xxx.stone.StdParser;
import com.xxx.stone.StdParser.Operators;
import java.util.HashSet;

public class StdBasicParser {
    HashSet<String> reserved  = new HashSet<String>();
    Operators operators = new Operators();
    StdParser expr0     = rule();

    StdParser primary = rule(PrimaryExpr.class)
            .or(rule().sep("(").ast(expr0).sep(")"),
                rule().number(NumberLiteral.class),
                rule().identifier(Name.class, reserved),
                rule().string(StringLiteral.class));
    StdParser factor  = rule().or(rule(NegativeExpr.class).sep("-").ast(primary),
                               primary);
    StdParser expr    = expr0.expression(BinaryExpr.class, factor, operators);

    StdParser statement0 = rule();

    StdParser block = rule(BlockStatement.class)
            .sep("{").option(statement0)
            .repeat(rule().sep(";", Token.EOL).option(statement0))
            .sep("}");

    StdParser simple    = rule(PrimaryExpr.class).ast(expr);
    StdParser statement = statement0.or(
            rule(IfStatement.class).sep("if").ast(expr).ast(block)
                    .option(rule().sep("else").ast(block)),
            rule(WhileStatement.class).sep("while").ast(expr).ast(block),
            simple);

    StdParser program = rule().or(statement, rule(NullStatement.class))
            .sep(";", Token.EOL);

    public StdBasicParser() {
        reserved.add(";");
        reserved.add("}");
        reserved.add(Token.EOL);

        operators.add("=", 1, Operators.RIGHT);
        operators.add("==", 2, Operators.LEFT);
        operators.add(">", 2, Operators.LEFT);
        operators.add("<", 2, Operators.LEFT);
        operators.add("+", 3, Operators.LEFT);
        operators.add("-", 3, Operators.LEFT);
        operators.add("*", 4, Operators.LEFT);
        operators.add("/", 4, Operators.LEFT);
        operators.add("%", 4, Operators.LEFT);
    }

    public AbstractSyntaxTree parse(Lexer lexer) throws ParseException {
        return program.parse(lexer);
    }
}
