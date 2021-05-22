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

    public static final String VM_CODE = "i = 100\n"
                                         + "def count() {\n"
                                         + "    print(i)\n"
                                         + "}\n"
                                         + "count()";

//    public static final String VM_CODE = "i = 100\n"
//                                         + "def count(j) {\n"
//                                         + "    print(j)\n"
//                                         + "}\n"
//                                         + "k = i + 100\n"
//                                         + "count(k)";

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
