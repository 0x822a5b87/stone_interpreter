package com.xxx.stone.vm;

/**
 * 用于虚拟机器语言转换过程中所有必要的信息
 *
 * @author 0x822a5b87
 */
public class Code {

    /**
     * stone 虚拟机的引用
     */
    protected Vm  vm;
    /**
     * 当前代码区的大小，同时 {@link Vm#code}[codeSize] 也是我们下一段代码开始的位置。
     */
    protected int codeSize;
    /**
     * 虚拟机文字常量区常量数量
     */
    protected int     numOfStrings;
    /**
     * 当前正在使用的寄存器
     */
    protected int     nextReg;
    /**
     * 当前正在转换函数的栈帧大小（frame size）
     */
    protected int     frameSize;

    public Code(Vm vm) {
        this.vm = vm;
    }

    public int getCodeSize() {
        return codeSize;
    }

    public void set(short value, int pos) {
        vm.getCode()[pos] = (byte) (value >>> 8);
        vm.getCode()[pos + 1] = (byte) (value);
    }

    /**
     * 为 {@link Vm#code} 添加一个 byte
     * @param b operand
     */
    public void addByte(byte b) {
        vm.getCode()[codeSize++] = b;
    }

    /**
     * 为 {@link Vm#code} 添加一个 short
     * @param s operand
     */
    public void addShort(short s) {
        addByte((byte) (s >>> 8));
        addByte((byte) s);
    }

    /**
     * 为 {@link Vm#code} 添加一个 int
     * @param i operand
     */
    public void addInt(int i) {
        // TODO 为什么是 >>> 而不是 >>
        addByte((byte) (i >>> 24));
        addByte((byte) (i >>> 16));
        addByte((byte) (i >>> 8));
        addByte((byte) i);
    }

    public int addString(String s) {
        vm.getStrings()[numOfStrings] = s;
        return numOfStrings++;
    }

    public Vm getVm() {
        return vm;
    }

    public int getNumOfStrings() {
        return numOfStrings;
    }

    public int getNextReg() {
        return nextReg;
    }

    public int getAndIncrementNextReg() {
        return nextReg++;
    }

    public int incrementAndGetNextReg() {
        return ++nextReg;
    }

    public int getAndDecrementNextReg() {
        return nextReg--;
    }

    public int decrementAndGetNextReg() {
        return --nextReg;
    }

    public int getFrameSize() {
        return frameSize;
    }

    public void setCodeSize(int codeSize) {
        this.codeSize = codeSize;
    }

    public void setNumOfStrings(int numOfStrings) {
        this.numOfStrings = numOfStrings;
    }

    public void setNextReg(int nextReg) {
        this.nextReg = nextReg;
    }

    public void setFrameSize(int frameSize) {
        this.frameSize = frameSize;
    }
}
