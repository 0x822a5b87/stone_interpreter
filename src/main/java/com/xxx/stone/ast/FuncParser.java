package com.xxx.stone.ast;

import static com.xxx.stone.Parser.rule;

import com.xxx.stone.Lexer;
import com.xxx.stone.ParseException;
import com.xxx.stone.Parser;
import com.xxx.stone.interpreter.BasicEnvironment;
import com.xxx.stone.interpreter.Environment;
import java.io.StringReader;

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
public class FuncParser extends BasicParser {

    public static String FUNC_CODE = "def run(i) {\n"
                                     + "    i = 10\n"
                                     + "}\n"
                                     + "run(10)\n"
                                     + "(1 + 2 + 3)";

    Parser param = rule("param").identifier(reserved);

    Parser params = rule("params", ParameterList.class)
            .ast(param).repeat(rule("params01").sep(",").ast(param));

    /**
     * 这里不需要 rule(ParameterList.class) 是因为 maybe 会把自身的 factory 作为新的 Parser 的工厂类
     */
    Parser paramList = rule("paramList")
            .sep("(").maybe(params).sep(")");

    Parser def = rule("def", DefStatement.class)
            .sep("def").identifier(reserved).ast(paramList).ast(block);

    Parser args = rule("args", Arguments.class)
            .ast(expr).repeat(rule("args01").sep(",").ast(expr));

    Parser postfix = rule("postfix").sep("(").maybe(args).sep(")");

    public FuncParser() {
        reserved.add("(");
        reserved.add(")");
        reserved.add("def");

        primary.repeat(postfix);
        simple.option(args);
        program.insertChoice(def);
    }

    public static void main(String[] args) throws ParseException {
        Lexer lexer = new Lexer(new StringReader(FUNC_CODE));
        FuncParser funcParser = new FuncParser();
        Environment environment = new BasicEnvironment();
        while (lexer.peek(0) != Token.EOF) {
            AbstractSyntaxTree t = funcParser.basicParse(lexer);
            try {
                System.out.println(t.eval(environment));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
