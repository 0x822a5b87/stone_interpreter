package com.xxx.stone.ast;

import com.xxx.stone.exception.StoneException;
import com.xxx.stone.interpreter.Environment;
import com.xxx.stone.optimizer.Location;
import com.xxx.stone.optimizer.Symbols;
import com.xxx.stone.vm.Code;
import com.xxx.stone.vm.InstructionSet;

/**
 * 操作符
 *
 * @author 0x822a5b87
 */
public class Name extends AbstractSyntaxLeaf {

    public static final int UNKNOWN = -1;

    protected int nest;
    protected int index;

    public Name(Token token) {
        super(token);
        index = UNKNOWN;
    }

    public String name() {
        return token.getText();
    }

    @Override
    public Object eval(Environment env) {
        if (index == UNKNOWN) {
            return env.get(name());
        } else {
            return env.get(nest, index);
        }
    }

    @Override
    public void lookup(Symbols symbols) {
        Location location = symbols.get(name());
        if (location == null) {
            throw new StoneException("undefined name : " + name());
        } else {
            nest = location.nest;
            index = location.index;
        }
    }

    public void evalForAssign(Environment env, Object value) {
        if (index == UNKNOWN) {
            env.put(name(), value);
        } else {
            env.put(nest, index, value);
        }
    }

    /**
     * 为 {@link Name} 赋值
     *
     * @param symbols symbols
     */
    public void lookupForAssign(Symbols symbols) {
        Location location = symbols.put(name());
        nest = location.nest;
        index = location.index;
    }

    @Override
    public void compile(Code code) {
        if (nest > 0) {
            code.addByte(InstructionSet.GMOVE);
            code.addShort(InstructionSet.encodeShortOffset(index));
        } else {
            code.addByte(InstructionSet.MOVE);
            code.addByte(InstructionSet.encodeOffset(index));
        }
        code.addByte(InstructionSet.encodeRegister(code.getAndIncrementNextReg()));
    }

    public void compileForAssign(Code code) {
        if (nest > 0) {
            code.addByte(InstructionSet.GMOVE);
            code.addByte(InstructionSet.encodeRegister(code.getNextReg() - 1));
            code.addShort(InstructionSet.encodeShortOffset(index));
        } else {
            code.addByte(InstructionSet.MOVE);
            code.addByte(InstructionSet.encodeRegister(code.getNextReg() - 1));
            code.addByte(InstructionSet.encodeOffset(index));
        }
    }
}
