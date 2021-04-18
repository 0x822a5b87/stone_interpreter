package com.xxx.stone.ast;

import com.xxx.stone.ParseException;
import java.io.IOException;

/**
 * @author 0x822a5b87
 *
 * 增加闭包支持
 */
public class ClosureParser extends FuncParser {

    private static final String CLOSURE_CODE =
            "def counter(c) {\n"
            + "    fun() {\n"
            + "        c = c + 1\n"
            + "    }\n"
            + "}\n"
            + "\n"
            + "c1 = counter(0)\n"
            + "c2 = counter(0)\n"
            + "c1()\n"
            + "c1()\n"
            + "c2()";

    public static void main(String[] args) throws ParseException, IOException {
        ClosureParser closureParser = new ClosureParser();
        closureParser.run(CLOSURE_CODE);
        System.out.println(CLOSURE_CODE);
    }
}
