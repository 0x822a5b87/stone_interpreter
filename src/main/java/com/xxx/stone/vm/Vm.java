package com.xxx.stone.vm;

import static com.xxx.stone.vm.InstructionSet.ADD;
import static com.xxx.stone.vm.InstructionSet.EQUAL;
import static com.xxx.stone.vm.InstructionSet.decodeOffset;
import static com.xxx.stone.vm.InstructionSet.decodeRegister;
import static com.xxx.stone.vm.InstructionSet.isRegister;

import com.google.common.base.Objects;
import com.xxx.stone.ast.AbstractSyntaxList;
import com.xxx.stone.ast.AbstractSyntaxTree;
import com.xxx.stone.exception.StoneException;
import com.xxx.stone.nat1ve.NativeFunction;
import java.util.ArrayList;

/**
 * stone 虚拟机
 *
 * @author 0x822a5b87
 */
public class Vm {

    /**
     * 方法区，一条指令的结构一般如下所示：
     *
     * INSTRUCT operand1 operand2
     *
     * INSTRUCT 的长度都是一个 byte，而 operand1 以及 operand2 的长度根据 INSTRUCT 不同而各有区别
     */
    protected byte[]     code;
    /**
     * 栈，访问一个函数时，它的实际位置应该是 {@link #fp} + offset。这个 offset 就是 {@link InstructionSet#MOVE} 的参数之一（byte）。
     * <br />
     * 也可以看到，我们的栈的最大大小为 {@link Byte#MAX_VALUE} + 1
     */
    protected Object[]   stack;
    /**
     * 文字常量区
     */
    protected String[]   strings;
    /**
     * 堆
     */
    protected HeapMemory heap;

    /**
     * program counter
     */
    public int pc;
    /**
     * frame pointer, aka call stack
     */
    public int fp;
    /**
     * stack pointer
     */
    public int sp;
    /**
     * return address register
     */
    public int ret;

    public Object[] registers;

    public static final int NUM_OF_REG     = 6;
    /**
     * 在调用 {@link #save()} 时，需要保存所有寄存器的值，所以大小是 {@link #NUM_OF_REG} 加上 fp 和 ret。
     */
    public final static int SAVE_AREA_SIZE = NUM_OF_REG + 2;

    public final static int TRUE  = 1;
    public final static int FALSE = 0;

    public Vm(int codeSize, int stackSize, int stringsSize, HeapMemory heapMemory) {
        this.code = new byte[codeSize];
        this.stack = new Object[stackSize];
        this.strings = new String[stringsSize];
        this.registers = new Object[NUM_OF_REG];
        this.heap = heapMemory;
    }

    public Object getRegister(int i) {
        return registers[i];
    }

    public String[] getStrings() {
        return strings;
    }

    public byte[] getCode() {
        return code;
    }

    public Object[] getStack() {
        return stack;
    }

    public HeapMemory getHeap() {
        return heap;
    }

    /**
     * 执行代码
     *
     * @param entry 代码的入口地址
     */
    public void run(int entry) {
        pc = entry;
        fp = 0;
        sp = 0;
        ret = -1;
        while (pc >= 0) {
            mainLoop();
        }
    }

    public void mainLoop() {
        switch (code[pc]) {
            case InstructionSet.ICONST:
                iconst();
                break;
            case InstructionSet.BCONST:
                bconst();
                break;
            case InstructionSet.SCONST:
                sconst();
                break;
            case InstructionSet.MOVE:
                move();
                break;
            case InstructionSet.GMOVE:
                gmove();
                break;
            case InstructionSet.IFZERO:
                ifZero();
                break;
            case InstructionSet.GOTO:
                gotoInstruction();
                break;
            case InstructionSet.CALL:
                call();
                break;
            case InstructionSet.RETURN:
                returnInstruction();
                break;
            case InstructionSet.SAVE:
                save();
                break;
            case InstructionSet.RESTORE:
                restore();
                break;
            case InstructionSet.NEG:
                negative();
                break;
            default:
                if (code[pc] > InstructionSet.LESS) {
                    throw new StoneException("bad instruction");
                } else {
                    computeNumber();
                }
                break;
        }
    }

    /**
     * 从 byte 数组中连续读取 4 个 byte 并组成一个 int。
     *
     * @param array byte数组
     * @param index 初始索引
     * @return 读取到的 int
     */
    public static int readInt(byte[] array, int index) {
        return ((array[index] & 0xff) << 24)
               | ((array[index + 1] & 0xff) << 16)
               | ((array[index + 2] & 0xff) << 8)
               | ((array[index + 3] & 0xff));
    }

    public static int readShort(byte[] array, int index) {
        return (short) (((array[index] & 0xff) << 8)
                        | ((array[index + 1] & 0xff)));
    }

    private void move() {
        byte src = code[pc + 1];
        byte dst = code[pc + 2];
        Object value;
        if (isRegister(src)) {
            value = registers[decodeRegister(src)];
        } else {
            value = stack[fp + decodeOffset(src)];
        }
        if (isRegister(dst)) {
            registers[decodeRegister(dst)] = value;
        } else {
            stack[fp + decodeOffset(dst)] = value;
        }
        pc += 3;
    }

    private void gotoInstruction() {
        pc += readShort(code, code[pc + 1]);
    }

    /**
     * 注意，call 指令只是负责调用函数，并不负责诸如保存当前调用状态（如通用寄存器，栈）。
     * 这些功能由 {@link InstructionSet#SAVE} 和 {@link InstructionSet#RESTORE} 来实现。
     */
    private void call() {
        Object value = registers[decodeRegister(code[pc + 1])];
        short numOfArgs = code[pc + 2];
        if ((value instanceof VmFunction)
            && ((VmFunction) value).parameters().size() == numOfArgs) {
            ret = pc + 3;
            pc = ((VmFunction) value).getEntry();
        } else if ((value instanceof NativeFunction)
                   && ((NativeFunction) value).getNumParams() == numOfArgs) {
            // 执行 native 方法，native 方法的参数在栈上的 [sp, sp + numOfArgs) 位置。
            // 并且在执行完之后，返回值存储在 stack[sp]
            Object[] args = new Object[numOfArgs];
            if (numOfArgs >= 0) {
                System.arraycopy(stack, sp, args, 0, numOfArgs);
            }
            stack[sp] = ((NativeFunction) value).invoke(args,
                                                        new AbstractSyntaxList(new ArrayList<AbstractSyntaxTree>()));
            pc += 3;
        } else {
            throw new StoneException("bad function call");
        }
    }

    private void returnInstruction() {
        pc = ret;
    }

    /**
     * {@link InstructionSet#SAVE} 指令的参数是函数的形参长度+局部变量长度
     */
    private void save() {
        int size = decodeOffset(code[pc + 1]);
        int dst = size + sp;
        for (Object register : registers) {
            stack[dst++] = register;
        }
        stack[dst++] = fp;
        fp = sp;
        stack[dst] = ret;
        sp += size + SAVE_AREA_SIZE;
        pc += 2;
    }

    /**
     * {@link InstructionSet#RESTORE} 指令的参数是函数的形参长度+局部变量长度
     */
    private void restore() {
        int dst = decodeOffset(code[pc + 1]) + fp;
        for (int i = 0; i < NUM_OF_REG; i++) {
            registers[i] = stack[dst++];
        }
        sp = fp;
        fp = (int) stack[dst++];
        ret = (int) stack[dst];
        pc += 2;
    }

    private void negative() {
        int register = decodeRegister(code[pc + 1]);
        Object value = registers[register];
        if (value instanceof Integer) {
            registers[register] = -((int) value);
        } else {
            throw new StoneException("bad operand value");
        }
        pc += 2;
    }

    private void computeNumber() {
        int left = decodeRegister(code[pc + 1]);
        int right = decodeRegister(code[pc + 2]);
        Object l = registers[left];
        Object r = registers[right];
        boolean areNumbers = (l instanceof Integer) && (r instanceof Integer);
        if (!areNumbers) {
            if (code[pc] == ADD) {
                registers[left] = String.valueOf(l) + String.valueOf(r);
            } else if (code[pc] == EQUAL) {
                registers[left] = Objects.equal(l, r);
            } else {
                throw new StoneException("bad compute number!");
            }
        } else {
            int i0 = (int) l;
            int i1 = (int) r;
            switch (code[pc]) {
                case InstructionSet.ADD:
                    registers[left] = i0 + i1;
                    break;
                case InstructionSet.SUB:
                    registers[left] = i0 - i1;
                    break;
                case InstructionSet.MUL:
                    registers[left] = i0 * i1;
                    break;
                case InstructionSet.DIV:
                    registers[left] = i0 / i1;
                    break;
                case InstructionSet.REM:
                    registers[left] = i0 % i1;
                    break;
                case InstructionSet.EQUAL:
                    registers[left] = (i0 == i1) ? TRUE : FALSE;
                    break;
                case InstructionSet.MORE:
                    registers[left] = (i0 > i1) ? TRUE : FALSE;
                    break;
                case InstructionSet.LESS:
                    registers[left] = (i0 < i1) ? TRUE : FALSE;
                    break;
                default:
                    throw new StoneException("never reach here");
            }
        }
        pc += 3;
    }

    private void ifZero() {
        Object value = registers[decodeRegister(code[pc + 1])];
        if ((value instanceof Integer) && ((Integer) value) == 0) {
            pc += readShort(code, pc + 2);
        }
        pc += 4;
    }

    /**
     * <pre>
     *     {@link InstructionSet#GMOVE} 指令
     *     这里需要注意一个地方，我们只能从堆复制数据到寄存器，或者从寄存器复制数据到堆。
     *     同时，堆的大小是 int16 表示的范围。
     *     // TODO 有一个问题，我们首先判断第一个字符是否为寄存器。有没有可能我们的堆的 int16 的高八位是一个负数正好对应了寄存器。
     * </pre>
     */
    private void gmove() {
        byte rand = code[pc + 1];
        if (isRegister(rand)) {
            int dst = readShort(code, pc + 2);
            heap.write(dst, registers[decodeRegister(rand)]);
        } else {
            int src = readShort(code, pc + 1);
            registers[decodeRegister(code[pc + 3])] = heap.read(src);
        }
        pc += 4;
    }

    private void iconst() {
        registers[decodeRegister(code[pc + 5])] = readInt(code, pc + 1);
        pc += 6;
    }

    private void bconst() {
        registers[decodeRegister(code[pc + 2])] = code[pc + 1];
        pc += 3;
    }

    private void sconst() {
        registers[decodeRegister(code[pc + 3])] = strings[readShort(code, pc + 1)];
        pc += 4;
    }
}
