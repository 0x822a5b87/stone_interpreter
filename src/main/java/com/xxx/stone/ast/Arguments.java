package com.xxx.stone.ast;

import com.xxx.stone.StoneException;
import com.xxx.stone.func.Function;
import com.xxx.stone.interpreter.Environment;
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
     * 2. 执行 {@link Arguments#eval(Environment, Object)} 时传入的 callerEnv，用于计算实参
     * 3. 函数自身的 env，这个 env 用于记录局部变量。
     * @param callerEnv 执行环境
     * @param obj 执行对象
     * @return
     * </pre>
     */
    @Override
    public Object eval(Environment callerEnv, Object obj) {
        if (!(obj instanceof Function)) {
            throw new StoneException(obj + " is not function");
        }
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
