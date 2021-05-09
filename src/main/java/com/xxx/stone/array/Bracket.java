package com.xxx.stone.array;

import com.xxx.stone.ast.AbstractSyntaxTree;
import com.xxx.stone.ast.NumberLiteral;
import com.xxx.stone.ast.Postfix;
import com.xxx.stone.exception.StoneException;
import com.xxx.stone.interpreter.Environment;
import java.util.ArrayList;
import java.util.List;

/**
 * 数组的后缀调用访问
 *
 * @author 0x822a5b87
 */
public class Bracket extends Postfix {

    public Bracket(List<AbstractSyntaxTree> c) {
        super(c);
    }

    public int index() {
        AbstractSyntaxTree index = child(0);
        if (!(index instanceof NumberLiteral)) {
            throw new StoneException("bad index : " + index);
        }
        NumberLiteral num = (NumberLiteral) index;
        return num.getToken().getNumber();
    }

    @Override
    public Object eval(Environment env, Object obj) {
        if (!(obj instanceof ArrayList)) {
            throw new StoneException("bad type eval : " + obj);
        }
        ArrayList<Object> arr = (ArrayList<Object>) obj;
        return arr.get(index());
    }
}
