package com.xxx.stone.interpreter;

import javassist.gluonj.Reviser;

/**
 * 执行器，用于计算AST上每一个节点的结果
 *
 * reviser 看起来像是继承，但是实际上它会直接修改它所继承的类的定义。
 *
 * @author 0x822a5b87
 */
@Reviser
public class BasicEvaluator {

    public static final int TRUE  = 1;
    public static final int FALSE = 0;
//
//    @Reviser
//    public static abstract class AbstractSyntaxTreeEx extends AbstractSyntaxTree {
//
//        @Override
//        public abstract Object eval(Environment env);
//    }
//
//    @Reviser
//    public static class NumberEx extends NumberLiteral {
//
//        public NumberEx(Token t) {
//            super(t);
//        }
//    }
//
//    @Reviser
//    public static class StringEx extends StringLiteral {
//
//        public StringEx(Token t) {
//            super(t);
//        }
//
//    }
//
//    @Reviser
//    public static class NameEx extends Name {
//
//        public NameEx(Token t) {
//            super(t);
//        }
//
//    }
//
//    @Reviser
//    public static class NegativeEx extends NegativeExpr {
//
//        public NegativeEx(List<AbstractSyntaxTree> c) {
//            super(c);
//        }
//
//    }
//
//    @Reviser
//    public static class BinaryEx extends BinaryExpr {
//
//        public BinaryEx(List<AbstractSyntaxTree> c) {
//            super(c);
//        }
//
//    }
//
//    @Reviser
//    public static class BlockEx extends BlockStatement {
//
//        public BlockEx(List<AbstractSyntaxTree> c) {
//            super(c);
//        }
//
//    }
//
//    @Reviser
//    public static class IfEx extends IfStatement {
//
//        public IfEx(List<AbstractSyntaxTree> c) {
//            super(c);
//        }
//
//    }
//
//    @Reviser
//    public static class WhileEx extends WhileStatement {
//
//        public WhileEx(List<AbstractSyntaxTree> c) {
//            super(c);
//        }
//
//    }
}
