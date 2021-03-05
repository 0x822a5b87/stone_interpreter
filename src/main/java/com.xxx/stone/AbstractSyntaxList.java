package com.xxx.stone;

import java.util.Iterator;
import java.util.List;

/**
 * 抽象语法树中含有数值的节点对象
 *
 * @author 0x822a5b87
 */
public class AbstractSyntaxList extends AbstractSyntaxTree {

    protected List<AbstractSyntaxTree> children;

    public AbstractSyntaxList(List<AbstractSyntaxTree> children) {
        this.children = children;
    }

    @Override
    public AbstractSyntaxTree child(int i) {
        return children.get(i);
    }

    @Override
    public int numChildren() {
        return children.size();
    }

    @Override
    public Iterator<AbstractSyntaxTree> children() {
        return children.iterator();
    }

    @Override
    public String location() {
        // TODO 这里的代码不是很理解，为什么 location 是遍历 children 呢？
        for (AbstractSyntaxTree t : children) {
            String s = t.location();
            if (s != null) {
                return s;
            }
        }
        return null;
    }
}
