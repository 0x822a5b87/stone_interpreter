package com.xxx.stone.optimizer;

import com.xxx.stone.interpreter.Environment;

/**
 * 由于程序新增的语句中可能包含了新的全局变量，而 {@link ArrayEnvironment#values} 的长度是不可变的。
 *
 * @author 0x822a5b87
 */
public class ResizeableEnvironment extends ArrayEnvironment {

    protected Symbols names;

    public ResizeableEnvironment() {
        super(10, null);
        this.names = new Symbols();
    }

    @Override
    public Symbols symbols() {
        return names;
    }

    @Override
    public Object get(String name) {
        Integer index = names.findInCurrentLayer(name);
        if (index != null) {
            return values[index];
        }
        if (outer == null) {
            return null;
        }
        return outer.get(name);
    }

    @Override
    public void put(String name, Object value) {
        Environment env = where(name);
        if (env == null) {
            env = this;
        }
        env.putNew(name, value);
    }

    @Override
    public void put(int nest, int index, Object value) {
        if (nest == 0) {
            assign(index, value);
        } else {
            super.put(nest, index, value);
        }
    }

    @Override
    public void putNew(String name, Object value) {
        assign(names.putNew(name), value);
    }

    @Override
    public Environment where(String name) {
        Integer index = names.findInCurrentLayer(name);
        if (index != null) {
            return this;
        }
        if (outer == null) {
            return null;
        }
        return outer.where(name);
    }

    /**
     * 为 {@link ArrayEnvironment#values} 重新分配空间。
     * @param index 索引
     * @param value 值
     */
    protected void assign(int index, Object value) {
        if (values.length <= index) {
            int newLen = values.length * 2;
            if (newLen == 0) {
                newLen = 1;
            }
            Object[] newValues = new Object[newLen];
            System.arraycopy(values, 0, newValues, 0, values.length);
            values = newValues;
        }
        values[index] = value;
    }
}
