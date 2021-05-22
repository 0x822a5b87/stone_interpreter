package com.xxx.stone.ast;

import com.xxx.stone.interpreter.Environment;
import com.xxx.stone.vm.Code;
import com.xxx.stone.vm.InstructionSet;

/**
 * string 字面量
 * @author 0x822a5b87
 */
public class StringLiteral extends AbstractSyntaxLeaf {

    public StringLiteral(Token t) {
        super(t);
    }

    public String value() {
        return getToken().getText();
    }

    @Override
    public Object eval(Environment e) {
        return value();
    }

    @Override
    public void compile(Code code) {
        int i = code.addString(value());
        code.addByte(InstructionSet.SCONST);
        code.addShort(InstructionSet.encodeShortOffset(i));
        code.addByte(InstructionSet.encodeRegister(code.getAndIncrementNextReg()));
    }
}
