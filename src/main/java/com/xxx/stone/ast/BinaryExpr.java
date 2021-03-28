package com.xxx.stone.ast;

import static com.xxx.stone.interpreter.BasicEvaluator.FALSE;
import static com.xxx.stone.interpreter.BasicEvaluator.TRUE;

import com.xxx.stone.StoneException;
import com.xxx.stone.interpreter.Environment;
import java.util.List;

/**
 * @author 0x822a5b87
 *
 * 二元操作符
 */
public class BinaryExpr extends AbstractSyntaxList {

    public BinaryExpr(List<AbstractSyntaxTree> children) {
        super(children);
    }

    public String operator() {
        return ((AbstractSyntaxLeaf) children.get(1)).getToken().getText();
    }

    public AbstractSyntaxTree left() {
        return children.get(0);
    }

    public AbstractSyntaxTree right() {
        return children.get(2);
    }


    @Override
    public Object eval(Environment env) {
        String op = operator();
        if ("=".equals(op)) {
            Object right = right().eval(env);
            return computeAssign(env, right);
        } else {
            Object left = left().eval(env);
            Object right = right().eval(env);
            return computeOp(left, op, right);
        }
    }

    protected Object computeAssign(Environment env, Object rvalue) {
        AbstractSyntaxTree l = left();
        if (l instanceof Name) {
            env.put(((Name) l).name(), rvalue);
            return rvalue;
        } else {
            throw new StoneException("bad assignment", this);
        }
    }

    protected Object computeOp(Object left, String op, Object right) {
        if (left instanceof Integer && right instanceof Integer) {
            return computeNumber((Integer) left, op, (Integer) right);
        } else if ("+".equals(op)) {
            return String.valueOf(left) + String.valueOf(right);
        } else if ("==".equals(op)) {
            if (left == null) {
                return right == null ? TRUE : FALSE;
            } else {
                return left.equals(right) ? TRUE : FALSE;
            }
        } else {
            throw new StoneException("bad type", this);
        }
    }

    protected Object computeNumber(Integer left, String op, Integer right) {
        int a = left;
        int b = right;
        if ("+".equals(op)) {
            return a + b;
        } else if ("-".equals(op)) {
            return a - b;
        } else if ("*".equals(op)) {
            return a * b;
        } else if ("/".equals(op)) {
            return a / b;
        } else if ("%".equals(op)) {
            return a % b;
        } else if ("==".equals(op)) {
            return a == b ? TRUE : FALSE;
        } else if (">".equals(op)) {
            return a > b ? TRUE : FALSE;
        } else if ("<".equals(op)) {
            return a < b ? TRUE : FALSE;
        } else {
            throw new StoneException("bad operator", this);
        }
    }
}
