package com.xxx.stone.ast;

import static com.xxx.stone.Parser.rule;

import com.xxx.stone.Parser;
import com.xxx.stone.Parser.Operators;
import java.util.HashSet;

/**
 * @author 0x822a5b87
 *
 * basic parser
 *
 * primary:    "(" expr ")" | NUMBER | IDENTIFIER | STRING
 * factor:        "-" primary | primary
 * expr:        factor { OP factor }
 * block:        "{" [ statement ] {(";" | EOL) [ statement ]} "}"
 * simple:        expr
 * statement:    "if" expr block ["else" block]
 *                 | "while" expr block
 *                 | simple
 * program:    [ statement ] (";" | EOL)
 */
public class BasicParser {

    HashSet<String> reserved = new HashSet<String>();

    Operators operators = new Operators();

    Parser exp0 = rule();

    /**
     * primary 解析器
     *
     * rule(PrimaryExpr.class) 内部包含的 make(Object args) 方法可以构建一个 PrimaryExpr 的类。
     */
    Parser primary = rule(PrimaryExpr.class)
            .or(rule().sep("(").ast(exp0).sep(")"),
                rule().number(NumberLiteral.class),
                rule().identifier(Name.class, reserved),
                rule().string(StringLiteral.class));

    Parser factor = rule().or(rule(NegativeExpr.class).sep("-").ast(primary),
                              rule().ast(primary));

    Parser expr = exp0.expression(BinaryExpr.class, factor, operators);

    public void test() {
    }
}
