package com.xxx.stone.optimizer;

import com.xxx.stone.exception.StoneException;
import com.xxx.stone.interpreter.Environment;
import com.xxx.stone.vm.Code;
import com.xxx.stone.vm.Vm;
import java.util.ArrayList;

/**
 * 优化执行时间的 {@link com.xxx.stone.interpreter.Environment}
 *
 * @author 0x822a5b87
 */
public class ArrayEnvironment implements Environment {

    protected ArrayList<Object> values;

    protected Environment outer;

    public ArrayEnvironment(int size, Environment outer) {
        this.values = new ArrayList<>();
        this.outer = outer;
    }

    @Override
    public Symbols symbols() {
        throw new StoneException("no symbols");
    }

    @Override
    public Object get(int nest, int index) {
        if (nest == 0) {
            return values.get(index);
        }

        if (outer == null) {
            throw new StoneException("variable not found!");
        }

        return outer.get(nest - 1, index);
    }

    @Override
    public void put(int nest, int index, Object value) {
        if (nest == 0) {
            values.add(index, value);
            return;
        }
        if (outer == null) {
            throw new StoneException("variable not found");
        }
        outer.put(nest - 1, index, value);
    }

    @Override
    public Vm stoneVm() {
        throw new StoneException("no such stone vm");
    }

    @Override
    public Code code() {
        throw new StoneException("no such code");
    }

    @Override
    public void put(String name, Object value) {
        throw new StoneException("unsupported method!");
    }

    @Override
    public Object get(String name) {
        throw new StoneException("unsupported method!");
    }

    @Override
    public void putNew(String name, Object value) {
        throw new StoneException("unsupported method!");
    }

    @Override
    public Environment where(String name) {
        throw new StoneException("unsupported method!");
    }

    @Override
    public void setOuter(Environment e) {
        throw new StoneException("unsupported method!");
    }
}
