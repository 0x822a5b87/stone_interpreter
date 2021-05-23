package com.xxx.stone.vm;

import com.xxx.stone.ParseException;
import com.xxx.stone.interpreter.Environment;
import com.xxx.stone.nat1ve.Natives;
import com.xxx.stone.optimizer.OptimizerParser;
import java.io.IOException;

/**
 * @author 0x822a5b87
 */
public class VmParser extends OptimizerParser {

    public static final String VM_CODE = "i = 0\n"
                                         + "def add(j) {\n"
                                         + "   if (j > 5) {\n"
                                         + "       i = i + j\n"
                                         + "       print(i)\n"
                                         + "   }\n"
                                         + "}\n"
                                         + "\n"
                                         + "k = 5\n"
                                         + "while (k < 10) {\n"
                                         + "    add(k)\n"
                                         + "    k = k + 1\n"
                                         + "}\n"
                                         + "print(i)";

    public VmParser() {
        env = new VmEnv(100000, 10000, 10000);
    }

    @Override
    protected Environment initNative() {
        Natives natives = new Natives();
        natives.appendNatives2Array(env, env.symbols());
        return env;
    }

    public static void main(String[] args) throws ParseException, IOException {
        VmParser parser = new VmParser();
        long start = System.currentTimeMillis();
        parser.run(VM_CODE);
        System.out.println("cost : " + (System.currentTimeMillis() - start));
    }
}
