package com.xxx.stone.ast;

import static com.xxx.stone.interpreter.BasicEvaluator.FALSE;

import com.xxx.stone.interpreter.Environment;
import com.xxx.stone.vm.Code;
import com.xxx.stone.vm.InstructionSet;
import java.util.List;

/**
 * @author 0x822a5b87
 */
public class IfStatement extends AbstractSyntaxList {

    public IfStatement(List<AbstractSyntaxTree> c) {
        super(c);
    }

    public AbstractSyntaxTree condition() {
        return child(0);
    }

    public AbstractSyntaxTree thenBlock() {
        return child(1);
    }

    public AbstractSyntaxTree elseBlock() {
        return numChildren() > 2 ? child(2) : null;
    }

    @Override
    public String toString() {
        return "(if " + condition() + " " + thenBlock()
               + " else " + elseBlock() + ")";
    }

    @Override
    public void compile(Code code) {
        condition().compile(code);
        int ifPos = code.getCodeSize();
        code.addByte(InstructionSet.IFZERO);
        code.addByte(InstructionSet.encodeRegister(code.decrementAndGetNextReg()));
        // 占位，因为现在还知道要跳转到哪里
        code.addShort(InstructionSet.encodeShortOffset(0));

        // 这里很容易出错，因为 thenBlock 和 elseBlock 是两个互斥的寄存器
        // 所以一个寄存器的执行不会占用另外一个代码块的寄存器
        int oldReg = code.getNextReg();
        thenBlock().compile(code);
        int thenPos = code.getCodeSize();
        // 如果执行了 thenBlock，那么我们要跳过 elseBlock 的代码
        code.addByte(InstructionSet.GOTO);
        // 占位，因为现在还不知道要跳转到哪里
        code.addShort(InstructionSet.encodeShortOffset(0));
        // 设置 IFZERO 的跳转
        code.set(InstructionSet.encodeShortOffset(code.getCodeSize() - ifPos), ifPos + 2);

        code.setNextReg(oldReg);
        if (elseBlock() != null) {
            elseBlock().compile(code);
        } else {
            code.addByte(InstructionSet.BCONST);
            code.addByte((byte)0);
            code.addByte(InstructionSet.encodeRegister(code.getAndIncrementNextReg()));
        }
        code.set(InstructionSet.encodeShortOffset(code.getCodeSize() - thenPos), thenPos + 1);
    }

    @Override
    public Object eval(Environment env) {
        // condition() 返回条件对应的抽象语法树，如果该语法树的执行结果为真则执行 block 中的内容。
        Object c = condition().eval(env);
        if (c instanceof Integer && ((Integer) c) != FALSE) {
            return thenBlock().eval(env);
        } else {
            AbstractSyntaxTree b = elseBlock();
            if (b == null) {
                return 0;
            } else {
                return b.eval(env);
            }
        }
    }
}
