package com.xxx.stone.ast;

import static com.xxx.stone.interpreter.BasicEvaluator.FALSE;

import com.xxx.stone.interpreter.Environment;
import com.xxx.stone.vm.Code;
import com.xxx.stone.vm.InstructionSet;
import java.util.List;

/**
 * @author 0x822a5b87
 */
public class WhileStatement extends AbstractSyntaxList {

    public WhileStatement(List<AbstractSyntaxTree> c) {
        super(c);
    }

    public AbstractSyntaxTree condition() {
        return child(0);
    }

    public AbstractSyntaxTree body() {
        return child(1);
    }

    @Override
    public String toString() {
        return "(while " + condition() + " " + body() + ")";
    }

    @Override
    public void compile(Code code) {
        // 假设现在空闲的寄存器是 r0, r1, ...
        // 规定 body 的计算结果保存在 r0，条件表达式的结果保存在 r1
        int oldReg = code.getNextReg();
        code.addByte(InstructionSet.BCONST);
        code.addByte((byte) 0);
        code.addByte(InstructionSet.encodeRegister(code.getAndIncrementNextReg()));
        int conditionStart = code.getCodeSize();
        condition().compile(code);
        int conditionEnd = code.getCodeSize();
        code.addByte(InstructionSet.IFZERO);
        code.addByte(InstructionSet.encodeRegister(code.decrementAndGetNextReg()));
        code.addByte((byte) 0);

        code.setNextReg(oldReg);
        body().compile(code);
        int bodyEnd = code.getCodeSize();
        code.addByte(InstructionSet.GOTO);
        code.addShort(InstructionSet.encodeShortOffset(conditionStart - bodyEnd));

        code.set(InstructionSet.encodeShortOffset(code.getCodeSize() - conditionEnd), conditionEnd + 2);
    }

    @Override
    public Object eval(Environment env) {
        Object result = 0;
        for (; ; ) {
            Object c = condition().eval(env);
            // 如果 while 条件判断为真则在子节点上继续执行 eval
            if (c instanceof Integer && (Integer) c == FALSE) {
                return result;
            } else {
                result = body().eval(env);
            }
        }
    }
}
