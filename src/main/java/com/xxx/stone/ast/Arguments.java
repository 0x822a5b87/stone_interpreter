package com.xxx.stone.ast;

import com.xxx.stone.exception.StoneException;
import com.xxx.stone.func.Function;
import com.xxx.stone.interpreter.Environment;
import com.xxx.stone.nat1ve.NativeFunction;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author 0x822a5b87
 */
public class Arguments extends Postfix {

    public Arguments(List<AbstractSyntaxTree> c) {
        super(c);
    }

    /**
     * <pre>
     * 函数的执行有几个不同的环境：
     * 1. 执行 {@link Function#Function(String, ParameterList, BlockStatement, Environment)} 传入的 env，
     *    由于 stone 不支持在函数中定义函数，所以一般都对应全局作用域
     * 2. 执行 #eval(Environment, Object) 时传入的 callerEnv，用于计算实参
     * 3. 函数自身的 env，这个 env 用于记录局部变量。
     * @see #eval(Environment, Object)
     * @param callerEnv 执行环境
     * @param obj 执行对象
     * @return
     * </pre>
     */
    @Override
    public Object eval(Environment callerEnv, Object obj) {
        if (obj instanceof Function) {
            return evalFunction(callerEnv, obj);
        } else if (obj instanceof NativeFunction) {
            return evalNativeFunction(callerEnv, obj);
        } else {
            throw new StoneException(obj + " is not function");
        }
    }

    protected Object evalNativeFunction(Environment callerEnv, Object obj) {
        NativeFunction nativeFunction = (NativeFunction) obj;
        Method method = nativeFunction.getMethod();
        int numParams = nativeFunction.getNumParams();
        if (numParams != size()) {
            throw new StoneException("bad num of arguments, expect "
                                     + nativeFunction.getNumParams() + ", got " + size());
        }

        Object[] params = new Object[numParams];
        for (int i = 0; i < numParams; i++) {
            params[i] = child(i).eval(callerEnv);
        }
        try {
            return method.invoke(this, params);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected Object evalFunction(Environment callerEnv, Object obj) {
        Function func = (Function) obj;
        ParameterList parameters = func.parameters();
        if (parameters.size() != size()) {
            throw new StoneException("bad num of arguments, expect "
                                     + func.parameters() + ", got " + size());
        }
        Environment funcEnv = func.makeEnv();
        int num = 0;
        for (AbstractSyntaxTree arg : this) {
            parameters.eval(funcEnv, num++, arg.eval(callerEnv));
        }
        return (func.body()).eval(funcEnv);
    }

    public int size() {
        return numChildren();
    }
}
