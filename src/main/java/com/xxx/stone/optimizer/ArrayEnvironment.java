package com.xxx.stone.optimizer;

import com.xxx.stone.exception.StoneException;
import com.xxx.stone.interpreter.Environment;

/**
 * 优化执行时间的 {@link com.xxx.stone.interpreter.Environment}
 *
 * @author 0x822a5b87
 */
public class ArrayEnvironment implements Environment {

    protected Object[] values;

    protected Environment outer;

    public ArrayEnvironment(int size, Environment outer) {
        this.values = new Object[size];
        this.outer = outer;
    }

    public Symbols symbols() {
        throw new StoneException("no symbols");
    }

    @Override
    public Object get(int nest, int index) {
        if (nest == 0) {
            return values[index];
        }

        if (outer == null) {
            throw new StoneException("variable not found!");
        }

        return outer.get(nest - 1, index);
    }

    @Override
    public void put(int nest, int index, Object value) {
        if (nest == 0) {
            values[index] = value;
            return;
        }
        if (outer == null) {
            throw new StoneException("variable not found");
        }
        outer.put(nest - 1, index, value);
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
