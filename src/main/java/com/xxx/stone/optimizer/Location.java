package com.xxx.stone.optimizer;

/**
 * 保存了对象在 {@link com.xxx.stone.interpreter.Environment} 的位置信息
 *
 * @author 0x822a5b87
 */
public class Location {
    public final int nest;
    public final int index;

    public Location(int nest, int index) {
        this.nest = nest;
        this.index = index;
    }
}
