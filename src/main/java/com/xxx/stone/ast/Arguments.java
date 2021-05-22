package com.xxx.stone.ast;

import com.xxx.stone.exception.StoneException;
import com.xxx.stone.func.Function;
import com.xxx.stone.interpreter.Environment;
import com.xxx.stone.nat1ve.NativeFunction;
import com.xxx.stone.vm.Code;
import com.xxx.stone.vm.InstructionSet;
import com.xxx.stone.vm.Vm;
import com.xxx.stone.vm.VmFunction;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author 0x822a5b87
 */
public class Arguments extends Postfix {

    public Arguments(List<AbstractSyntaxTree> c) {
        super(c);
    }

    /**
     * 函数的执行有几个不同的环境：
     * <ol>
     *     <li>
     *         执行 {@link Function#Function(String, ParameterList, BlockStatement, Environment)} 传入的 env，
     *         由于 stone 不支持在函数中定义函数，所以一般都对应全局作用域
     *     </li>
     *     <li>执行时传入的 callerEnv，用于计算实参</li>
     *     <li>函数自身的 env，这个 env 用于记录局部变量。</li>
     * </ol>
     * @see #eval(Environment, Object)
     * @param callerEnv 执行环境
     * @param obj 执行对象
     * @return obj
     */
    @Override
    public Object eval(Environment callerEnv, Object obj) {
        if (obj instanceof VmFunction) {
            return evalVmFunction(callerEnv, (VmFunction) obj);
        } else if (obj instanceof NativeFunction) {
            return evalNativeFunction(callerEnv, obj);
        } else if(obj instanceof Function) {
            return evalFunction(callerEnv, obj);
        } else {
            throw new StoneException(obj + " is not function");
        }
    }

    /**
     * 函数的调用有以下几个步骤：
     * <ol>
     *     <li>将所有的参数从寄存器复制到栈</li>
     *     <li>调用函数</li>
     *     <li>将返回值复制到寄存器</li>
     * </ol>
     * @param code code
     */
    @Override
    public void compile(Code code) {
        // frameSize 就是 sp
        int frameSize = code.getFrameSize();
        int numOfArgs = 0;
        for (; numOfArgs < size(); ++numOfArgs) {
            AbstractSyntaxTree arg = child(numOfArgs);
            arg.compile(code);
            code.addByte(InstructionSet.MOVE);
            code.addByte(InstructionSet.encodeRegister(code.decrementAndGetNextReg()));
            code.addByte(InstructionSet.encodeOffset(frameSize++));
        }

        code.addByte(InstructionSet.CALL);
        code.addByte(InstructionSet.encodeRegister(code.decrementAndGetNextReg()));
        code.addByte(InstructionSet.encodeOffset(numOfArgs));

        code.addByte(InstructionSet.MOVE);
        code.addByte(InstructionSet.encodeOffset(code.getFrameSize()));
        code.addByte(InstructionSet.encodeRegister(code.getAndIncrementNextReg()));
    }

    protected Object evalVmFunction(Environment callerEnv, VmFunction vmFunction) {
        ParameterList parameters = vmFunction.parameters();
        if (parameters.size() != size()) {
            throw new StoneException("bad vm function call!");
        }

        for (int index = 0; index < size(); ++index) {
            Object ret = child(index).eval(callerEnv);
            parameters.eval(callerEnv, index, ret);
        }

        Vm vm = callerEnv.stoneVm();
        vm.run(vmFunction.getEntry());
        return vm.getStack()[0];
    }

    protected Object evalNativeFunction(Environment callerEnv, Object obj) {
        NativeFunction nativeFunction = (NativeFunction) obj;
        Method method = nativeFunction.getMethod();
        int numParams = nativeFunction.getNumParams();
        if (numParams != size()) {
            throw new StoneException("bad num of arguments, expect "
                                     + nativeFunction.getNumParams() + ", got " + size());
        }

        Object[] params = new Object[numParams];
        for (int i = 0; i < numParams; i++) {
            params[i] = child(i).eval(callerEnv);
        }
        try {
            return method.invoke(this, params);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected Object evalFunction(Environment callerEnv, Object obj) {
        Function func = (Function) obj;
        ParameterList parameters = func.parameters();
        if (parameters.size() != size()) {
            throw new StoneException("bad num of arguments, expect "
                                     + func.parameters() + ", got " + size());
        }
        Environment funcEnv = func.makeEnv();
        int num = 0;
        for (AbstractSyntaxTree arg : this) {
            parameters.eval(funcEnv, num++, arg.eval(callerEnv));
        }
        return (func.body()).eval(funcEnv);
    }

    public int size() {
        return numChildren();
    }
}
