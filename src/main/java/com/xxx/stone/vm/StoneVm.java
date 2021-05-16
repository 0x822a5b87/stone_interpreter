package com.xxx.stone.vm;

/**
 * stone 虚拟机
 *
 * @author 0x822a5b87
 */
public class StoneVm {

    /**
     * 方法区
     */
    protected byte[]     code;
    /**
     * 栈
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

    public static final int NUM_OF_REG = 6;
}
