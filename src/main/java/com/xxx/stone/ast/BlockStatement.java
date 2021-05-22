package com.xxx.stone.ast;

import com.xxx.stone.interpreter.Environment;
import com.xxx.stone.vm.Code;
import com.xxx.stone.vm.InstructionSet;
import java.util.List;

/**
 * @author 0x822a5b87
 */
public class BlockStatement extends AbstractSyntaxList {

    public BlockStatement(List<AbstractSyntaxTree> c) {
        super(c);
    }


    @Override
    public Object eval(Environment env) {
        Object result = 0;
        for (AbstractSyntaxTree t : this) {
            if (!(t instanceof NullStatement)) {
                result = t.eval(env);
            }
        }
        return result;
    }

    @Override
    public void compile(Code code) {
        if (numChildren() > 0) {
            int initReg = code.getNextReg();
            for (AbstractSyntaxTree ast : this) {
                code.setNextReg(initReg);
                ast.compile(code);
            }
        } else {
            code.addByte(InstructionSet.MOVE);
            code.addByte((byte) 0);
            code.addByte(InstructionSet.encodeRegister(code.getAndIncrementNextReg()));
        }
    }
}
