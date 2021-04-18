package com.xxx.stone.func;

import com.xxx.stone.ast.BlockStatement;
import com.xxx.stone.ast.ParameterList;
import com.xxx.stone.interpreter.Environment;
import com.xxx.stone.interpreter.NestedEnvironment;

/**
 * @author 0x822a5b87
 *
 *         函数
 */
public class Function {

    protected String name;

    protected ParameterList  parameters;
    protected BlockStatement body;
    protected Environment    callerEnv;

    public Function(String name, ParameterList parameters, BlockStatement body, Environment callerEnv) {
        this.name = name;
        this.parameters = parameters;
        this.body = body;
        this.callerEnv = callerEnv;
    }

    public ParameterList parameters() {
        return parameters;
    }

    public BlockStatement body() {
        return body;
    }

    public Environment makeEnv() {
        return new NestedEnvironment(callerEnv);
    }

    @Override
    public String toString() {
        return "<fun:[" + name + "]:" + hashCode() + ">";
    }
}
