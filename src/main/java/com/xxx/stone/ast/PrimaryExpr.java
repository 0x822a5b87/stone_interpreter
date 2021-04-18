package com.xxx.stone.ast;

import com.sun.java.swing.action.OpenAction;
import com.xxx.stone.Parser;
import com.xxx.stone.func.Function;
import com.xxx.stone.interpreter.Environment;
import com.xxx.stone.interpreter.NestedEnvironment;
import java.util.List;

/**
 * <em>
 * {@link PrimaryExpr} 是一个特殊的类，因为他提供了 {@link PrimaryExpr#create(List)} 方法，
 * 这个方法的优先级是高于构造器的。
 * <br/>
 * 在构造函数的 List<AbstractSyntaxTree> 的长度 == 1 时，它会忽略掉 PrimaryExpr 而直接
 * 返回它的子节点。
 * </em>
 * <br/>
 * 基础表达式，可能是 {@link NumberLiteral}, {@link Name}, {@link StringLiteral}
 * 括号括起来的 {@link  BasicParser#expr}, {@link BasicParser#simple}
 * <br/>
 * 其中 {@link BasicParser#expr} 和 {@link BasicParser#simple} 都是多元操作符
 * 另外，由于在 {@link FuncParser#FuncParser()} 中扩展了 simple 和 primary，
 * 所以它也可能是一个函数调用。
 * <br/>
 * 前面三种类型都是终结符，后面则不是。
 * <br/>
 *
 * @author 0x822a5b87
 */
public class PrimaryExpr extends AbstractSyntaxList {

    public PrimaryExpr(List<AbstractSyntaxTree> c) {
        super(c);
    }

    /**
     * 为了省略节点的数量，我们规定了一条特殊的规则：
     * 如果子节点只有一个，Parser 库将不会额外的创建一个节点。
     * 在 {@link Parser#rule(String)} 的方法中，如果 rule() 没有接收参数，我们就会使用上面这条特殊的规则。
     * 但是，如果接收了参数 {@link Parser#rule(String, Class)}，那么我们将会通过构造器来构造，会额外的创建一个节点。
     * 为了在接收参数时也可以适应上面这条规则，我们可以通过使用 create 方法来实现。
     * 因为在 getForAbstractSyntaxList 方法中我们会优先判断是否存在 create 方法。
     * 如果不存在 create 方法我们才会去构造器
     *
     * 在这个方法中，如果调用构造器，我们将会建立一个额外的 {@link AbstractSyntaxList}
     * 这个对象里包含了我们生成的语法树
     */
    public static AbstractSyntaxTree create(List<AbstractSyntaxTree> c) {
        return c.size() == 1 ? c.get(0) : new PrimaryExpr(c);
    }

    /**
     * <pre>
     *     返回操作数。
     *     返回的 {@link PrimaryExpr} 可能是嵌套的，对于 (1 + 2 + 3 + 4) 会形成如下的结构：
     *     children[0] = BinaryExpr("((1 + 2) + 3)")
     *     children[1] = +
     *     children[2] = 4
     * </pre>
     *
     * @return 操作数
     */
    public AbstractSyntaxTree operand() {
        return child(0);
    }

    public Postfix postfix(int nest) {
        return (Postfix) child(numChildren() - nest - 1);
    }

    public boolean hasPostfix(int nest) {
        return numChildren() - nest > 1;
    }

    /**
     * <pre>
     * 函数调用，stone 允许 f(1)(2)(3) 这种级联调用的方式。
     * 并且，由于 stone 中函数是一等公民，所以它也是一个 {@link PrimaryExpr} 对象
     * 通过这种简单的方式我们实现了闭包。
     * @param env   执行环境
     * @param nest  递归次数
     * @return 执行结果
     * </pre>
     */
    public Object evalSubExpr(Environment env, int nest) {
        if (hasPostfix(nest)) {
            Object target = evalSubExpr(env, nest + 1);
            Postfix p = postfix(nest);
            return p.eval(env, target);
        } else {
            // 在我们的实现中，函数是一等公民，所以它也是一个 PrimaryExpr 对象
            return operand().eval(env);
        }
    }

    @Override
    public Object eval(Environment env) {
        return evalSubExpr(env, 0);
    }
}
