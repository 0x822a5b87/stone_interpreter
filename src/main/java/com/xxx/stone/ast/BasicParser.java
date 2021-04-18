package com.xxx.stone.ast;


import static com.xxx.stone.Parser.rule;

import com.xxx.stone.Lexer;
import com.xxx.stone.ParseException;
import com.xxx.stone.Parser;
import com.xxx.stone.Parser.Operators;
import com.xxx.stone.func.Closure;
import com.xxx.stone.interpreter.Environment;
import com.xxx.stone.interpreter.NestedEnvironment;
import java.io.StringReader;
import java.util.HashSet;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Color;
import org.fusesource.jansi.AnsiConsole;

/**
 * @author 0x822a5b87
 *         基于 {@link com.xxx.stone.ast.BasicParser} 扩展，增加函数解析功能
 *
 *         primary:    "(" expr ")" | NUMBER | IDENTIFIER | STRING | "fun" param_list block
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
    Parser expr    = rule("expr");
    Parser postfix = rule("postfix");
    Parser args    = rule("args", Arguments.class);
    Parser def     = rule("def", DefStatement.class);
    Parser closure = rule("closure", Closure.class);
    /**
     * primary 解析器
     *
     * rule(PrimaryExpr.class) 内部包含的 make(Object args) 方法可以构建一个 PrimaryExpr 的类。
     */
    Parser primary = rule("primary", PrimaryExpr.class);

    Parser factor = rule("factor");

    /**
     * statement:    "if" expr block ["else" block]
     * | "while" expr block
     * | simple
     */
    Parser statement = rule("statement");

    /**
     * block:        "{" [ statement ] {(";" | EOL) [ statement ]} "}"
     */
    Parser block = rule("block", BlockStatement.class);

    Parser simple = rule("simple", PrimaryExpr.class);

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

    Parser program = rule("program");

    Parser param = rule("param").identifier(reserved);

    Parser params = rule("params", ParameterList.class);

    /**
     * 这里不需要 rule(ParameterList.class) 是因为 maybe 会把自身的 factory 作为新的 Parser 的工厂类
     */
    Parser paramList = rule("paramList");

    public BasicParser() {
        expr.expression(BinaryExpr.class, factor, operators);

        // 注意，这里存在二义性，clojure 必须在前面，否则会被识别为 identifier
        primary.or(closure,
                   rule("primary01").sep("(").ast(expr).sep(")"),
                   rule("primary02").number(NumberLiteral.class),
                   rule("primary03").identifier(Name.class, reserved),
                   rule("primary04").string(StringLiteral.class))
                .repeat(postfix);

        factor.or(rule("factor01", NegativeExpr.class).sep("-").ast(primary),
                  primary);

        statement.or(rule("statement0-IfStatement", IfStatement.class)
                             .sep("if").ast(expr).ast(block)
                             .option(rule("statement0-else").sep("else").ast(block)),
                     rule("statement0-WhileStatement", WhileStatement.class)
                             .sep("while").ast(expr).ast(block),
                     simple
        );
        block.sep("{").option(statement)
                .repeat(rule("block01").sep(";", Token.EOL).option(statement))
                .sep("}");

        simple.ast(expr).option(args);

        program.or(def, statement, rule("program01", NullStatement.class))
                .sep(";", Token.EOL);

        params.ast(param).repeat(rule("params01").sep(",").ast(param));

        paramList.sep("(").maybe(params).sep(")");

        postfix.sep("(").maybe(args).sep(")");

        args.ast(expr).repeat(rule("args01").sep(",").ast(expr));

        def.sep("def").identifier(reserved).ast(paramList).ast(block);

        closure.sep("fun").ast(paramList).ast(block);

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

    public void run(String code) throws ParseException {
        AnsiConsole.systemInstall();
        System.out.println(Ansi.ansi().eraseScreen().fg(Color.GREEN).a("========================").reset());
        System.out.println(Ansi.ansi().fg(Color.GREEN).a(code).reset());
        System.out.println(Ansi.ansi().fg(Color.GREEN).a("========================").reset());
        Lexer lexer = new Lexer(new StringReader(code));
        Environment global = new NestedEnvironment(null);
        while (lexer.peek(0) != Token.EOF) {
            AbstractSyntaxTree t = basicParse(lexer);
            try {
                System.out.println(Ansi.ansi()
                                           .fg(Color.MAGENTA).a("console: ")
                                           .fg(Color.RED).a(t.eval(global))
                                           .reset());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        AnsiConsole.systemUninstall();
    }
}
