package com.xxx.stone.ast;

import static com.xxx.stone.interpreter.BasicEvaluator.FALSE;
import static com.xxx.stone.interpreter.BasicEvaluator.TRUE;

import com.xxx.stone.exception.StoneException;
import com.xxx.stone.interpreter.Environment;
import com.xxx.stone.object.Dot;
import com.xxx.stone.object.StoneObject;
import java.util.List;

/**
 * @author 0x822a5b87
 *
 *         二元操作符
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
        } else if (l instanceof PrimaryExpr) {
            return computePrimaryExpr(env, (PrimaryExpr) l, rvalue);
        } else {
            throw new StoneException("bad assignment", this);
        }
    }

    /**
     * <pre>
     * 计算 {@link PrimaryExpr}，目前我们计算的都是 {@code p.x = 10} 这种调用。
     * 因为 p.x 会直接返回 x，在这种情况下我们是没有办法赋值的，需要特殊处理。
     *
     * 以代码 {@code t.teenager.human.age} 为例
     *
     * {@code expr.evalSubExpr(callerEnv, 1);}
     *
     * 是一次递归调用，首先代码一直向下执行，直到找到 {@link PrimaryExpr#operand()}，这个时候 operand 返回
     * 的是一个 {@link Name}，也就是 t。
     *
     * 随后 {@link PrimaryExpr#evalSubExpr(Environment, int)} 在 t 上调用了 {@link Name#eval(Environment)}
     * 得到了 t 的值。
     *
     * 此时我们得到了 t 的值，以及紧跟在 t 后面的 {@link Dot} 也就是 .teenager。
     * 随后在 t 上调用 {@link Dot#eval(Environment, Object)} 并返回了一个 {@link StoneObject}，就是 t.teenager
     *
     * 此后，循环的在 {@link StoneObject} 上调用 {@link Dot#eval(Environment, Object)} 方法直到最后一个 {@link StoneObject}
     *
     * 最后，我们修改 {@link StoneObject} 的值即可。
     * </pre>
     *
     * @param callerEnv caller env
     * @param expr      expr
     * @param rvalue    right value
     * @return stoneObject
     */
    protected Object computePrimaryExpr(Environment callerEnv, PrimaryExpr expr, Object rvalue) {
        if (expr.hasPostfix(0) && expr.postfix(0) instanceof Dot) {
            Object o = expr.evalSubExpr(callerEnv, 1);
            if (o instanceof StoneObject) {
                StoneObject stoneObject = (StoneObject) o;
                AbstractSyntaxTree child = expr.postfix(0);
                setValue(stoneObject, (Dot) child, rvalue);
                return stoneObject;
            }
        }

        throw new StoneException("bad assignment", this);
    }

    private void setValue(StoneObject stoneObject, Dot dot, Object rvalue) {
        String name = dot.getName();
        stoneObject.write(name, rvalue);
    }

    protected Object computeOp(Object left, String op, Object right) {
        if (left instanceof Integer && right instanceof Integer) {
            return computeNumber((Integer) left, op, (Integer) right);
        } else if ("+".equals(op)) {
            return left + String.valueOf(right);
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
