package com.xxx.stone.ast;

import com.xxx.stone.func.Closure;
import com.xxx.stone.interpreter.Environment;
import com.xxx.stone.optimizer.OptFunction;
import com.xxx.stone.optimizer.Symbols;
import com.xxx.stone.vm.Code;
import com.xxx.stone.vm.InstructionSet;
import com.xxx.stone.vm.Vm;
import com.xxx.stone.vm.VmEnv;
import com.xxx.stone.vm.VmFunction;
import java.util.List;

/**
 * @author 0x822a5b87
 */
public class DefStatement extends AbstractSyntaxList {

    /**
     * DefStatement 也是一个访问量非常大，所以也需要做个优化
     */
    protected int index;
    protected int size;

    public DefStatement(List<AbstractSyntaxTree> c) {
        super(c);
    }

    public String name() {
        return ((AbstractSyntaxLeaf) child(0)).getToken().getText();
    }

    public ParameterList parameters() {
        return (ParameterList) child(1);
    }

    public BlockStatement body() {
        return (BlockStatement) child(2);
    }

    @Override
    public String toString() {
        return "(def " + name() + " " + parameters() + " " + body() + ")";
    }

    @Override
    public Object eval(Environment callerEnv) {
        if (callerEnv instanceof VmEnv) {
            return evalForVm((VmEnv) callerEnv);
        } else {
            return evalForInterpreter(callerEnv);
        }
    }

    @Override
    public void lookup(Symbols symbols) {
        index = symbols.putNew(name());
        size = Closure.lookup(symbols, parameters(), body());
    }

    /**
     * 编译 {@link DefStatement} 并生成代码，一次函数编译应该分为如下几个步骤：
     * <ol>
     *     <li>初始化自己的通用寄存器</li>
     *     <li>初始化自己的栈区，并保存 caller 的所有寄存器状态（包括通用寄存器以及一些特殊寄存器）</li>
     *     <li>编译方法体</li>
     *     <li>保存返回值</li>
     *     <li>恢复 caller 寄存器状态并返回</li>
     * </li>
     * 返回值的说明可以参考 {@link InstructionSet#MOVE}
     * @param code code
     */
    @Override
    public void compile(Code code) {
        // 初始化通用寄存器
        code.setNextReg(0);
        // 初始化栈，保存 caller 状态
        code.setFrameSize(size + Vm.SAVE_AREA_SIZE);
        code.addByte(InstructionSet.SAVE);
        code.addByte(InstructionSet.encodeOffset(size));
        // 编译方法体
        body().compile(code);
        // 保存返回值
        code.addByte(InstructionSet.MOVE);
        // 按照我们的约定，为了保存上一条指令的计算结果，第 0~i 个寄存器将处于占用状态
        // 虚拟机可以使用第 i+1 个寄存器及以后的寄存器，结果保存在第 i+1 个寄存器中。
        code.addByte(InstructionSet.encodeRegister(code.getNextReg() - 1));
        // 返回值存在 fp + 0
        code.addByte(InstructionSet.encodeOffset(0));
        // 恢复 caller 寄存器状态并返回
        code.addByte(InstructionSet.RESTORE);
        code.addByte(InstructionSet.encodeOffset(size));
        code.addByte(InstructionSet.RETURN);
    }

    private Object evalForVm(VmEnv vmEnv) {
        String funcName = name();
        Code code = vmEnv.code();
        int codeSize = code.getCodeSize();
        compile(code);
        vmEnv.putNew(funcName, new VmFunction(funcName, parameters(), body(), vmEnv, codeSize));
        return funcName;
    }

    private Object evalForInterpreter(Environment callerEnv) {
        callerEnv.put(0, index, new OptFunction(name(), parameters(), body(), callerEnv, size));
        return "<func: [" + name() + "]>";
    }
}
