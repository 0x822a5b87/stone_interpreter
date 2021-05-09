package com.xxx.stone.array;

import com.xxx.stone.ast.AbstractSyntaxList;
import com.xxx.stone.ast.AbstractSyntaxTree;
import com.xxx.stone.interpreter.Environment;
import java.util.ArrayList;
import java.util.List;

/**
 * 数组对象
 *
 * @author 0x822a5b87
 */
public class ArrayLiteral extends AbstractSyntaxList {

    public ArrayLiteral(List<AbstractSyntaxTree> children) {
        super(children);
    }

    public int size() {
        return numChildren();
    }

    @Override
    public Object eval(Environment env) {
        int size = size();
        ArrayList<Object> objects = new ArrayList<>(size);
        for (AbstractSyntaxTree child : children) {
            objects.add(child.eval(env));
        }
        return objects;
    }
}
