package com.xxx.stone.ast;

import com.xxx.stone.Parser;
import java.util.List;

/**
 * @author 0x822a5b87
 *
 * primary expression
 */
public class PrimaryExpr extends AbstractSyntaxList {

    public PrimaryExpr(List<AbstractSyntaxTree> c) {
        super(c);
    }

    /**
     * 为了省略节点的数量，我们规定了一条特殊的规则：
     * 如果子节点只有一个，Parser 库将不会额外的创建一个节点。
     * 在 {@link Parser#rule()} 的方法中，如果 rule() 没有接收参数，我们就会使用上面这条特殊的规则。
     * 但是，如果接收了参数 {@link Parser#rule(Class)}，那么我们将会通过构造器来构造，会额外的创建一个节点。
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
}
