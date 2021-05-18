package com.xxx.stone.optimizer;

import com.xxx.stone.ast.BlockStatement;
import com.xxx.stone.ast.ParameterList;
import com.xxx.stone.func.Function;
import com.xxx.stone.interpreter.Environment;

/**
 * 和 {@link Function} 的区别是，OptFunction 使用了 {@link ArrayEnvironment}
 *
 * @author 0x822a5b87
 */
public class OptFunction extends Function {

    protected int size;

    public OptFunction(String name, ParameterList parameters,
                       BlockStatement body, Environment callerEnv,
                       int memorySize) {

        super(name, parameters, body, callerEnv);
        this.size = memorySize;
    }

    @Override
    public Environment makeEnv() {
        return new ArrayEnvironment(size, callerEnv);
    }
}
