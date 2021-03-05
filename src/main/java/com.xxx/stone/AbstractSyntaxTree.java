package com.xxx.stone;

import java.util.Iterator;

/**
 * @author 0x822a5b87
 *
 */
public abstract class AbstractSyntaxTree implements Iterable<AbstractSyntaxTree> {

    /**
     * 返回第 i 个子节点
     * @param i i
     * @return 子节点
     */
    public abstract AbstractSyntaxTree child(int i);

    /**
     * 返回子节点个数
     *
     * @return 子节点个数
     */
    public abstract int numChildren();

    /**
     * 返回所有的子节点
     *
     * @return 所有的子节点
     */
    public abstract Iterator<AbstractSyntaxTree> children();

    /**
     * 返回一个用于表示抽象语法书节点在程序内所处位置的字符串
     *
     * @return 节点在程序内所处位置
     */
    public abstract String location();

    public Iterator<AbstractSyntaxTree> iterator() {
        return children();
    }
}
