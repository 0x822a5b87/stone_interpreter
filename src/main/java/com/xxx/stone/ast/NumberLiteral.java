package com.xxx.stone.ast;

import com.xxx.stone.interpreter.Environment;
import com.xxx.stone.vm.Code;
import com.xxx.stone.vm.InstructionSet;

/**
 * 数字字面量
 *
 * @author 0x822a5b87
 */
public class NumberLiteral extends AbstractSyntaxLeaf {

    public NumberLiteral(Token token) {
        super(token);
    }

    public int value() {
        return token.getNumber();
    }

    @Override
    public Object eval(Environment e) {
        return value();
    }

    @Override
    public void compile(Code code) {
        int v = value();
        if (v >= Byte.MIN_VALUE && v <= Byte.MAX_VALUE) {
            code.addByte(InstructionSet.BCONST);
            code.addByte((byte) v);
        } else {
            code.addByte(InstructionSet.ICONST);
            code.addInt(v);
        }
        code.addByte(InstructionSet.encodeRegister(code.getAndIncrementNextReg()));
    }
}
