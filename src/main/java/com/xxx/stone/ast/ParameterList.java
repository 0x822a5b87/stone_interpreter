package com.xxx.stone.ast;

import com.xxx.stone.interpreter.Environment;
import java.util.List;

/**
 * @author 0x822a5b87
 */
public class ParameterList extends AbstractSyntaxList {

    public ParameterList(List<AbstractSyntaxTree> c) {
        super(c);
    }

    public String name(int i) {
        return ((AbstractSyntaxLeaf) child(i)).getToken().getText();
    }

    public int size() {
        return numChildren();
    }

    public void eval(Environment env, int index, Object value) {
        env.putNew(name(index), value);
    }
}
