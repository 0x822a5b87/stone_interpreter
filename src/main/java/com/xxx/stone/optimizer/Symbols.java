package com.xxx.stone.optimizer;

import java.util.HashMap;
import java.util.Map;

/**
 * 用于映射名字和 {@link Location} 的类。
 *
 * @author 0x822a5b87
 */
public class Symbols {

    protected Symbols outer;

    protected Map<String, Integer> table;

    public Symbols() {
        this(null);
    }

    public Symbols(Symbols outer) {
        this.outer = outer;
        this.table = new HashMap<>();
    }

    public int size() {
        return table.size();
    }

    public void append(Symbols symbols) {
        this.table.putAll(symbols.table);
    }

    public Integer findInCurrentLayer(String name) {
        return table.get(name);
    }

    /**
     * 查找某一个变量对应的 {@link Location}，因为对于同一个变量，在不同的函数中，这个 Location 是不一样的。
     * @param name 变量名
     * @return Location
     */
    public Location get(String name) {
        return get(name, 0);
    }

    public Location get(String name, int nest) {
        Integer index = table.get(name);
        if (index == null) {
            if (outer == null) {
                return null;
            } else {
                return outer.get(name, nest + 1);
            }
        } else {
            return new Location(nest, index);
        }
    }

    public int putNew(String name) {
        Integer index = findInCurrentLayer(name);
        if (index == null) {
            return add(name);
        } else {
            return index;
        }
    }

    public Location put(String name) {
        Location location = get(name);
        if (location == null) {
            return new Location(0, add(name));
        } else {
            return location;
        }
    }

    protected int add(String name) {
        int index = size();
        table.put(name, index);
        return index;
    }
}
