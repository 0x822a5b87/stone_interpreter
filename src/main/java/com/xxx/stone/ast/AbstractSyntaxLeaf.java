package com.xxx.stone.ast;

import com.xxx.stone.exception.StoneException;
import com.xxx.stone.interpreter.Environment;
import com.xxx.stone.vm.Code;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 抽象语法树叶子节点
 * @author 0x822a5b87
 */
public class AbstractSyntaxLeaf extends AbstractSyntaxTree {

    private final static List<AbstractSyntaxTree> EMPTY = new ArrayList<AbstractSyntaxTree>();

    protected Token token;

    public AbstractSyntaxLeaf(Token token) {
        this.token = token;
    }

    @Override
    public AbstractSyntaxTree child(int i) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public int numChildren() {
        return 0;
    }

    @Override
    public Iterator<AbstractSyntaxTree> children() {
        return EMPTY.iterator();
    }

    @Override
    public String location() {
        return "at line " + token.getLineNumber();
    }

    public Token getToken() {
        return token;
    }

    @Override
    public String toString() { return token.getText(); }

    @Override
    public Object eval(Environment env) {
        throw new StoneException("cannot eval: " + toString(), this);
    }

    @Override
    public void compile(Code code) {
    }

}
