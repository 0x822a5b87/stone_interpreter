package com.xxx.stone.nat1ve;

import com.xxx.stone.exception.StoneException;
import com.xxx.stone.interpreter.Environment;
import com.xxx.stone.optimizer.Location;
import com.xxx.stone.optimizer.Symbols;
import java.lang.reflect.Method;
import javax.swing.JOptionPane;

/**
 * <pre>
 * @author 0x822a5b87
 *
 * initialize native method
 * </pre>
 */
public class Natives {

    public Environment environment(Environment env) {
        appendNatives(env);
        return env;
    }

    public void appendNatives(Environment env) {
        appendNatives2Array(env, null);
    }

    public void appendNatives2Array(Environment env, Symbols symbols) {
        append(env, symbols, "print", Natives.class, "print", Object.class);
        append(env, symbols, "read", Natives.class, "read");
        append(env, symbols, "length", Natives.class, "length", String.class);
        append(env, symbols, "toInt", Natives.class, "toInt", Object.class);
        append(env, symbols, "currentTime", Natives.class, "currentTime");
    }

    protected void append(Environment env, Symbols symbols,
                            String name, Class<?> clazz,
                            String methodName, Class<?>... params) {
        Method m;
        try {
            m = clazz.getMethod(methodName, params);
        } catch (Exception e) {
            throw new StoneException("cannot find a native function: "
                                     + methodName);
        }
        env.put(name, new NativeFunction(methodName, m));
        if (symbols != null) {
            symbols.putNew(name);
        }
    }

    public static int print(Object obj) {
        System.out.println("[native print] : " + obj.toString());
        return 0;
    }

    public static String read() {
        return JOptionPane.showInputDialog(null);
    }

    public static int length(String s) {
        return s.length();
    }

    public static int toInt(Object value) {
        if (value instanceof String) {
            return Integer.parseInt((String) value);
        } else if (value instanceof Integer) {
            return (Integer) value;
        } else {
            throw new NumberFormatException(value.toString());
        }
    }

    private static final long startTime = System.currentTimeMillis();

    public static int currentTime() {
        return (int) (System.currentTimeMillis() - startTime);
    }
}
