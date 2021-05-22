package com.xxx.stone.ast;

import com.xxx.stone.exception.StoneException;
import com.xxx.stone.interpreter.Environment;
import com.xxx.stone.vm.Code;
import com.xxx.stone.vm.InstructionSet;
import java.util.List;

public class NegativeExpr extends AbstractSyntaxList {

    public NegativeExpr(List<AbstractSyntaxTree> c) {
        super(c);
    }

    public AbstractSyntaxTree operand() {
        return child(0);
    }

    @Override
    public String toString() {
        return "-" + operand();
    }

    @Override
    public Object eval(Environment env) {
        Object v = operand().eval(env);
        if (v instanceof Integer) {
            return -((Integer) v);
        } else {
            throw new StoneException("bad type for -", this);
        }
    }

    @Override
    public void compile(Code code) {
        // TODO，这里会不会导致 NumberLiteral 的 compile 被调用两次？
        operand().compile(code);
        // NumberLiteral 的 compile 会生成指令为 register 赋值。
        // 我们生成一条新的指令，去将赋值后的寄存器取反
        code.addByte(InstructionSet.NEG);
        code.addByte(InstructionSet.encodeRegister(code.getNextReg() - 1));
    }
}
