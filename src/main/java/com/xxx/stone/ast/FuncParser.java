package com.xxx.stone.ast;

import static com.xxx.stone.Parser.rule;

import com.xxx.stone.Lexer;
import com.xxx.stone.ParseException;
import com.xxx.stone.interpreter.Environment;
import com.xxx.stone.interpreter.NestedEnvironment;
import java.io.StringReader;

/**
 * <pre>
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
 *
 *         run(10)(1)(2) 的解析流程:
 *         program -> statement0 -> simple -> exp0 -> factor -> primary -> primary03 -> repeat(postfix)
 * </pre>
 * @author 0x822a5b87
 */
public class FuncParser extends BasicParser {

    public static String CLOSURE_CODE =
            "def closure(i) {\n"
            + "    i = i + 10\n"
            + "}\n"
            + "def run(i) {\n"
            + "    i + 10\n"
            + "    closure\n"
            + "}\n"
            + "run(1)(2)\n";

    public static String FIB_CODE = "def fib (n) {\n"
                                    + "    if n < 2 {\n"
                                    + "        n\n"
                                    + "    } else {\n"
                                    + "        fib(n - 1) + fib(n - 2)\n"
                                    + "    }\n"
                                    + "}\n"
                                    + "fib(10)";

    public static void run(String code) throws ParseException {
        System.out.println("========================");
        System.out.println("execute code :" );
        System.out.println(code);
        System.out.println("========================");
        Lexer lexer = new Lexer(new StringReader(code));
        FuncParser funcParser = new FuncParser();
        Environment global = new NestedEnvironment(null);
        while (lexer.peek(0) != Token.EOF) {
            AbstractSyntaxTree t = funcParser.basicParse(lexer);
            try {
                System.out.println(t.eval(global));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws ParseException {
        run(CLOSURE_CODE);
        run(FIB_CODE);
    }
}
