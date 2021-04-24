package com.xxx.stone.ast;

import com.xxx.stone.exception.StoneException;
import com.xxx.stone.interpreter.Environment;
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
        for (AbstractSyntaxTree t : children) {
            String s = t.location();
            if (s != null) {
                return s;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append('(');
        String sep = "";
        for (AbstractSyntaxTree t: children) {
            builder.append(sep);
            sep = " ";
            builder.append(t.toString());
        }
        return builder.append(')').toString();
    }

    @Override
    public Object eval(Environment env) {
        throw new StoneException("cannot eval: " + toString(), this);
    }
}
