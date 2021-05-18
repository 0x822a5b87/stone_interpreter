package com.xxx.stone.optimizer;

import com.xxx.stone.ParseException;
import com.xxx.stone.array.ArrayParser;
import com.xxx.stone.ast.AbstractSyntaxTree;
import com.xxx.stone.ast.FuncParser;
import com.xxx.stone.interpreter.Environment;
import com.xxx.stone.nat1ve.Natives;
import java.io.IOException;

/**
 * 基于 {@link ArrayEnvironment} 优化执行效率的 parser
 *
 * @author 0x822a5b87
 */
public class OptimizerParser extends ArrayParser {

    private final Symbols globalSymbols = new Symbols();

    @Override
    protected Environment initNative() {
        Environment env = new ResizeableEnvironment();
        Natives natives = new Natives();
        natives.appendNatives2Array(env, globalSymbols);
        return env;
    }

    @Override
    public void run(String code) throws ParseException {
        run(initNative(), code);
    }

    @Override
    protected void out(Environment global, AbstractSyntaxTree t) {
        t.lookup(globalSymbols);
        super.out(global, t);
    }

    public static void main(String[] args) throws ParseException, IOException {
        OptimizerParser parser = new OptimizerParser();
        long start = System.currentTimeMillis();
        parser.run(FuncParser.FIB_CODE);
        System.out.println("cost : " + (System.currentTimeMillis() - start));
    }
}
