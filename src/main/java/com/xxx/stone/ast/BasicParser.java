package com.xxx.stone.ast;


import static com.xxx.stone.Parser.rule;

import com.xxx.stone.Lexer;
import com.xxx.stone.ParseException;
import com.xxx.stone.Parser;
import com.xxx.stone.Parser.Operators;
import java.util.HashSet;

/**
 * @author 0x822a5b87
 *
 *         basic parser
 *
 *         primary:    "(" expr ")" | NUMBER | IDENTIFIER | STRING
 *         factor:        "-" primary | primary
 *         expr:        factor { OP factor }
 *         block:        "{" [ statement ] {(";" | EOL) [ statement ]} "}"
 *         simple:        expr
 *         statement:    "if" expr block ["else" block]
 *         | "while" expr block
 *         | simple
 *         program:    [ statement ] (";" | EOL)
 */
public class BasicParser {

    HashSet<String> reserved = new HashSet<String>();

    Operators operators = new Operators();

    /**
     * exp0 == expr
     * <br/>
     * 之所以这么声明，是因为存在依赖链 expr -> factor -> primary -> expr
     * <br/>
     * 没办法直接声明 rule().expression(BinaryExpr.class, factor, operators)
     */
    Parser exp0 = rule("exp0");

    /**
     * primary 解析器
     *
     * rule(PrimaryExpr.class) 内部包含的 make(Object args) 方法可以构建一个 PrimaryExpr 的类。
     */
    Parser primary = rule("primary", PrimaryExpr.class)
            .or(rule("primary01").sep("(").ast(exp0).sep(")"),
                rule("primary02").number(NumberLiteral.class),
                rule("primary03").identifier(Name.class, reserved),
                rule("primary04").string(StringLiteral.class));

    Parser factor = rule("factor").or(rule("factor01", NegativeExpr.class).sep("-").ast(primary),
                              primary);

    Parser expr = exp0.expression(BinaryExpr.class, factor, operators);

    Parser statement0 = rule("statement0");

    /**
     * block:        "{" [ statement ] {(";" | EOL) [ statement ]} "}"
     */
    Parser block = rule("block", BlockStatement.class)
            .sep("{").option(statement0)
            .repeat(rule("block01").sep(";", Token.EOL).option(statement0))
            .sep("}");

    Parser simple = rule("simple", PrimaryExpr.class).ast(expr);

    /**
     * statement:    "if" expr block ["else" block]
     * | "while" expr block
     * | simple
     */
    Parser statement = statement0.or(
            rule("statement", IfStatement.class).sep("if").ast(expr).ast(block)
                    .option(rule("statement01").sep("else").ast(block)),
            rule("statement02", WhileStatement.class).sep("while").ast(expr).ast(block),
            simple
    );

    /**
     * program:    [ statement ] (";" | EOL)
     * 下面的代码其实表示的是这个 BNF
     * program:    ( statement | 空 ) (";" | EOL)
     *
     * 之所以这么表示，是为了能在模式仅包含 ; 或者 \n 的时候，表示这种特殊的形式。
     *
     * 如果使用 or 来作为生成规则，在语句内容为空的时候，Parser 将创建一个仅包含一颗仅包含一个 NullStatement 的对象作为语法树。
     * program 或者是一颗 statement 语法树，或者是一个 NullStatement 节点。
     */

    Parser program = rule("program").or(statement, rule("program01", NullStatement.class))
            .sep(";", Token.EOL);

    public BasicParser() {
        reserved.add(";");
        reserved.add("{");
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

    public AbstractSyntaxTree basicParse(Lexer lexer) throws ParseException {
        return program.parse(lexer);
    }
}
