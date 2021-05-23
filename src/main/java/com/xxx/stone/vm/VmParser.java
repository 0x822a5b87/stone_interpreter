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

    /**
     * 10 1
     * 4 0 -1
     * 2 15 -2
     * 19 -1 -2
     * 6 -1 0 38
     * 5 0 5 -1
     * 4 0 -2
     * 13 -1 -2
     * 5 -1 0 5
     * 5 0 0 -1
     * 5 0 5 -2
     * 4 -2 9
     * 8 -1 1
     * 4 9 -1
     * 7 0 6
     *
     * 2 0 -1 4 -1 0 11 1 9
     *
     * ```asm
     * SAVE 1
     * MOVE #0 $1
     * BCONST {5} $2
     * MORE $1 $2
     * IFZERO $1 #38
     * GMOVE #5 $1					i -> $1
     * MOVE  #0 $2					j -> $2
     * ADD  $1 $2
     * GMOVE $1 #5					i + j -> #5
     * GMOVE #0 $1					print -> $1
     * GMOVE #5 $2					i -> $2
     * MOVE $2 #9
     * call $1 #1
     * move #9 $1
     * goto 6
     * ```
     */
    public static final String VM_CODE = "i = 0\n"
                                         + "def add(j) {\n"
                                         + "   if (j > 5) {\n"
                                         + "       i = i + j\n"
                                         + "       print(i)\n"
                                         + "   }\n"
                                         + "}\n"
                                         + "def sub(j) {\n"
                                         + "    \n"
                                         + "}\n"
                                         + "add(i)";

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
