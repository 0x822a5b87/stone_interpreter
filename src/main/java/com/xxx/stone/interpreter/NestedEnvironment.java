package com.xxx.stone.interpreter;

import com.xxx.stone.exception.StoneException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 0x822a5b87
 */
public class NestedEnvironment implements Environment {

    private final Map<String, Object> values;

    private Environment         outer;

    public NestedEnvironment(Environment outer) {
        this.outer = outer;
        this.values = new HashMap<>();
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
    public Object get(String name) {
        Object o = values.get(name);
        if (o != null) {
            return o;
        }
        if (outer == null) {
            return null;
        }
        return outer.get(name);
    }

    @Override
    public void putNew(String name, Object value) {
        values.put(name, value);
    }

    @Override
    public Environment where(String name) {
        if (values.containsKey(name)) {
            return this;
        } else if (outer == null) {
            return null;
        } else {
            return outer.where(name);
        }
    }

    @Override
    public void setOuter(Environment e) {
        outer = e;
    }

    @Override
    public Object get(int nest, int index) {
        throw new StoneException("unsupported method!");
    }

    @Override
    public void put(int nest, int index, Object value) {
        throw new StoneException("unsupported method!");
    }
}
