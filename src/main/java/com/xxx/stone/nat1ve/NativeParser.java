package com.xxx.stone.nat1ve;

import com.xxx.stone.ParseException;
import com.xxx.stone.ast.ClosureParser;
import com.xxx.stone.interpreter.Environment;
import com.xxx.stone.interpreter.NestedEnvironment;
import java.io.IOException;

/**
 * <pre>
 * @author 0x822a5b87
 *
 * native parser
 * </pre>
 */
public class NativeParser extends ClosureParser {

    private static final String NATIVE_CODE =
            "print(10 + 10 * 10)\n"
            + "length(\"I am your father!\")\n"
            + "toInt(\"-10\")\n"
            + "\"-10\"\n"
            + "currentTime()";

    protected Environment initNative() {
        Environment env = new NestedEnvironment(null);
        Natives natives = new Natives();
        natives.appendNatives(env);
        return env;
    }

    @Override
    public void run(String code) throws ParseException {
        run(initNative(), code);
    }

    public static void main(String[] args) throws ParseException, IOException {
        NativeParser nativeParser = new NativeParser();
        nativeParser.run(NATIVE_CODE);
    }
}
