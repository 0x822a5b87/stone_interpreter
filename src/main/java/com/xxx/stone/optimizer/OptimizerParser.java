package com.xxx.stone.optimizer;

import com.xxx.stone.ParseException;
import com.xxx.stone.array.ArrayParser;
import com.xxx.stone.ast.AbstractSyntaxTree;
import com.xxx.stone.ast.FuncParser;
import com.xxx.stone.interpreter.Environment;
import com.xxx.stone.nat1ve.Natives;
import com.xxx.stone.vm.VmEnv;
import java.io.IOException;

/**
 * 基于 {@link ArrayEnvironment} 优化执行效率的 parser
 *
 * @author 0x822a5b87
 */
public class OptimizerParser extends ArrayParser {

    public static final String CODE = "i = 0\n"
                                      + "def count(j) {\n"
                                      + "    i = i + j\n"
                                      + "}\n"
                                      + "\n"
                                      + "k = 0\n"
                                      + "while (k < 10000) {\n"
                                      + "    count(-1)\n"
                                      + "    k = k + 1"
                                      + "}\n"
                                      + "\n"
                                      + "print(i)";

    protected Environment env;

    public OptimizerParser() {
        this.env = new ResizeableEnvironment();
    }

    @Override
    protected Environment initNative() {
        Natives natives = new Natives();
        natives.appendNatives2Array(env, env.symbols());
        return env;
    }

    @Override
    public void run(String code) throws ParseException {
        run(initNative(), code);
    }

    @Override
    protected void out(Environment global, AbstractSyntaxTree t) {
        if (global instanceof ResizeableEnvironment) {
            ResizeableEnvironment env = (ResizeableEnvironment) global;
            t.lookup(env.symbols());
            super.out(env, t);
        }
    }

    public static void main(String[] args) throws ParseException, IOException {
        OptimizerParser parser = new OptimizerParser();
        long start = System.currentTimeMillis();
        parser.run(CODE);
        System.out.println("cost : " + (System.currentTimeMillis() - start));
    }
}
