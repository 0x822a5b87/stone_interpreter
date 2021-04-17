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

    protected ParameterList  parameters;
    protected BlockStatement body;
    protected Environment    env;

    public Function(ParameterList parameters, BlockStatement body, Environment env) {
        this.parameters = parameters;
        this.body = body;
        this.env = env;
    }

    public ParameterList parameters() {
        return parameters;
    }

    public BlockStatement body() {
        return body;
    }

    public Environment makeEnv() {
        return new NestedEnvironment(env);
    }

    @Override
    public String toString() {
        return "<fun:" + hashCode() + ">";
    }
}
