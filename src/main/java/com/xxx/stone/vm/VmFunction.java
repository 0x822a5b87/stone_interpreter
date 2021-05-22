package com.xxx.stone.vm;

import com.xxx.stone.ast.BlockStatement;
import com.xxx.stone.ast.ParameterList;
import com.xxx.stone.func.Function;
import com.xxx.stone.interpreter.Environment;

/**
 * 虚拟机的函数实体
 * @author 0x822a5b87
 */
public class VmFunction extends Function {

    /**
     * 在 {@link Vm#code} 中所处位置的索引。
     */
    protected int entry;

    public VmFunction(String name, ParameterList parameters, BlockStatement body,
                      Environment callerEnv, int entry) {
        super(name, parameters, body, callerEnv);
        this.entry = entry;
    }

    public int getEntry() {
        return entry;
    }
}
