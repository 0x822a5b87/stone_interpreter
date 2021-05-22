package com.xxx.stone.vm;

import com.xxx.stone.optimizer.ResizeableEnvironment;

/**
 * stoneVm 表示堆区
 *
 * @author 0x822a5b87
 */
public class VmEnv extends ResizeableEnvironment implements HeapMemory {

    protected Vm   vm;
    protected Code code;

    public VmEnv(int codeSize, int stackSize, int stringsSize) {
        vm = new Vm(codeSize, stackSize, stringsSize, this);
        code = new Code(vm);
    }

    public Vm getVm() {
        return vm;
    }

    @Override
    public Object read(int index) {
        return values.get(index);
    }

    @Override
    public void write(int index, Object value) {
        values.set(index, value);
    }

    @Override
    public Vm stoneVm() {
        return vm;
    }

    @Override
    public Code code() {
        return code;
    }
}
