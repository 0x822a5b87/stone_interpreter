package com.xxx.stone.ast;


import static com.xxx.stone.Parser.rule;

import com.xxx.stone.Lexer;
import com.xxx.stone.ParseException;
import com.xxx.stone.Parser;
import com.xxx.stone.Parser.Operators;
import java.util.HashSet;

/**
 * @author 0x822a5b87
 *         基于 {@link com.xxx.stone.ast.BasicParser} 扩展，增加函数解析功能
 *
 *         primary:    "(" expr ")" | NUMBER | IDENTIFIER | STRING
 *         factor:        "-" primary | primary
 *         expr:        factor { OP factor }
 *         block:        "{" [ statement ] {(";" | EOL) [ statement ]} "}"
 *         statement:    "if" expr block ["else" block]
 *         | "while" expr block
 *         | simple
 *
 *         param: IDENTIFIER
 *         params: IDENTIFIER { "," params }
 *         param_list: "(" [ params ] ")"
 *         def: "def" IDENTIFIER param_list block
 *         args: expr { "," expr }
 *         postfix: "(" args ")"
 *         primary:    ("(" expr ")" | NUMBER | IDENTIFIER | STRING) { postfix }
 *         simple: expr [ args ]
 *         program: [ def | statemen ] ( ";" | EOL )
 *
 *         注意 simple: expr { "(" args ")" } 是不正确的
 *         但是类似于 fun(10) 的语法仍然可以支持，因为它被匹配成了 primary
 *
 *         primary 是基础类型，为了让函数成为一等公民，我们将它也定义成了一种基础类型
 *
 *         primary 既可以是 expr 也可以是 factor
 *
 *         fun(10) 可以被识别为仅由 expr 构成。
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

    Parser postfix = rule("postfix");
    Parser args    = rule("args", Arguments.class);
    Parser def     = rule("def", DefStatement.class);

    /**
     * primary 解析器
     *
     * rule(PrimaryExpr.class) 内部包含的 make(Object args) 方法可以构建一个 PrimaryExpr 的类。
     */
    Parser primary = rule("primary", PrimaryExpr.class)
            .or(rule("primary01").sep("(").ast(exp0).sep(")"),
                rule("primary02").number(NumberLiteral.class),
                rule("primary03").identifier(Name.class, reserved),
                rule("primary04").string(StringLiteral.class))
            .repeat(postfix);

    Parser factor = rule("factor")
            .or(rule("factor01", NegativeExpr.class).sep("-").ast(primary),
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

    Parser simple = rule("simple", PrimaryExpr.class).ast(expr).option(args);

    /**
     * statement:    "if" expr block ["else" block]
     * | "while" expr block
     * | simple
     */
    Parser statement = statement0.or(
            rule("statement0-IfStatement", IfStatement.class).sep("if").ast(expr).ast(block)
                    .option(rule("statement0-else").sep("else").ast(block)),
            rule("statement0-WhileStatement", WhileStatement.class).sep("while").ast(expr).ast(block),
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

    Parser program = rule("program")
            .or(def, statement, rule("program01", NullStatement.class))
            .sep(";", Token.EOL);

    Parser param = rule("param").identifier(reserved);

    Parser params = rule("params", ParameterList.class)
            .ast(param).repeat(rule("params01").sep(",").ast(param));

    /**
     * 这里不需要 rule(ParameterList.class) 是因为 maybe 会把自身的 factory 作为新的 Parser 的工厂类
     */
    Parser paramList = rule("paramList")
            .sep("(").maybe(params).sep(")");

    public BasicParser() {
        postfix.sep("(").maybe(args).sep(")");
        args.ast(expr).repeat(rule("args01").sep(",").ast(expr));
        def.sep("def").identifier(reserved).ast(paramList).ast(block);

        reserved.add(";");
        reserved.add("{");
        reserved.add("}");
        reserved.add("(");
        reserved.add(")");
        reserved.add("def");

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
