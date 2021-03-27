package com.xxx.stone;

import com.xxx.stone.ast.AbstractSyntaxLeaf;
import com.xxx.stone.ast.AbstractSyntaxList;
import com.xxx.stone.ast.AbstractSyntaxTree;
import com.xxx.stone.ast.Token;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * @author 0x822a5b87
 *
 *         解析器
 */
public class Parser0 {

    protected static abstract class Element {

        /**
         * 执行语法分析
         *
         * @param lexer 词法解析器
         * @param res 抽象语法树
         * @throws ParseException parser异常
         */
        protected abstract void parse(Lexer lexer, List<AbstractSyntaxTree> res)
                throws ParseException;

        /**
         * @param lexer
         * @return
         * @throws ParseException
         */
        protected abstract boolean match(Lexer lexer) throws ParseException;
    }

    protected static class Tree extends Element {

        protected Parser0 parser0;

        protected Tree(Parser0 p) {
            parser0 = p;
        }

        @Override
        protected void parse(Lexer lexer, List<AbstractSyntaxTree> res)
                throws ParseException {
            AbstractSyntaxTree ast = parser0.parse(lexer);
            res.add(ast);
        }

        @Override
        protected boolean match(Lexer lexer) throws ParseException {
            return parser0.match(lexer);
        }
    }

    protected static class OrTree extends Element {

        protected Parser0[] parser0s;

        protected OrTree(Parser0[] p) {
            parser0s = p;
        }

        @Override
        protected void parse(Lexer lexer, List<AbstractSyntaxTree> res)
                throws ParseException {
            Parser0 p = choose(lexer);
            if (p == null) {
                throw new ParseException(lexer.peek(0));
            } else {
                AbstractSyntaxTree ast = p.parse(lexer);
                res.add(ast);
            }
        }

        @Override
        protected boolean match(Lexer lexer) throws ParseException {
            return choose(lexer) != null;
        }

        protected Parser0 choose(Lexer lexer) throws ParseException {
            for (Parser0 p : parser0s) {
                if (p.match(lexer)) {
                    return p;
                }
            }

            return null;
        }

        protected void insert(Parser0 p) {
            Parser0[] newParser0s = new Parser0[parser0s.length + 1];
            newParser0s[0] = p;
            System.arraycopy(parser0s, 0, newParser0s, 1, parser0s.length);
            parser0s = newParser0s;
        }
    }

    protected static class Repeat extends Element {

        protected Parser0 parser0;
        protected boolean onlyOnce;

        protected Repeat(Parser0 p, boolean once) {
            parser0 = p;
            onlyOnce = once;
        }

        @Override
        protected void parse(Lexer lexer, List<AbstractSyntaxTree> res)
                throws ParseException {
            while (parser0.match(lexer)) {
                AbstractSyntaxTree t = parser0.parse(lexer);
                if (t.getClass() != AbstractSyntaxList.class || t.numChildren() > 0) {
                    res.add(t);
                }
                if (onlyOnce) {
                    break;
                }
            }
        }

        @Override
        protected boolean match(Lexer lexer) throws ParseException {
            return parser0.match(lexer);
        }
    }

    protected static abstract class AToken extends Element {

        protected Factory factory;

        protected AToken(Class<? extends AbstractSyntaxLeaf> type) {
            if (type == null) {
                type = AbstractSyntaxLeaf.class;
            }
            factory = Factory.get(type, Token.class);
        }

        @Override
        protected void parse(Lexer lexer, List<AbstractSyntaxTree> res)
                throws ParseException {
            Token t = lexer.read();
            if (test(t)) {
                AbstractSyntaxTree leaf = factory.make(t);
                res.add(leaf);
            } else {
                throw new ParseException(t);
            }
        }

        @Override
        protected boolean match(Lexer lexer) throws ParseException {
            return test(lexer.peek(0));
        }

        protected abstract boolean test(Token t);
    }

    protected static class IdToken extends AToken {

        HashSet<String> reserved;

        protected IdToken(Class<? extends AbstractSyntaxLeaf> type, HashSet<String> r) {
            super(type);
            reserved = r != null ? r : new HashSet<String>();
        }

        @Override
        protected boolean test(Token t) {
            return t.isIdentifier() && !reserved.contains(t.getText());
        }
    }

    protected static class NumToken extends AToken {

        protected NumToken(Class<? extends AbstractSyntaxLeaf> type) {
            super(type);
        }

        @Override
        protected boolean test(Token t) {
            return t.isNumber();
        }
    }

    protected static class StrToken extends AToken {

        protected StrToken(Class<? extends AbstractSyntaxLeaf> type) {
            super(type);
        }

        @Override
        protected boolean test(Token t) {
            return t.isString();
        }
    }

    protected static class Leaf extends Element {

        protected String[] tokens;

        protected Leaf(String[] pat) {
            tokens = pat;
        }

        @Override
        protected void parse(Lexer lexer, List<AbstractSyntaxTree> res)
                throws ParseException {
            Token t = lexer.read();
            if (t.isIdentifier()) {
                for (String token : tokens) {
                    if (token.equals(t.getText())) {
                        find(res, t);
                        return;
                    }
                }
            }

            if (tokens.length > 0) {
                throw new ParseException(tokens[0] + " expected.", t);
            } else {
                throw new ParseException(t);
            }
        }

        protected void find(List<AbstractSyntaxTree> res, Token t) {
            res.add(new AbstractSyntaxLeaf(t));
        }

        @Override
        protected boolean match(Lexer lexer) throws ParseException {
            Token t = lexer.peek(0);
            if (t.isIdentifier()) {
                for (String token : tokens) {
                    if (token.equals(t.getText())) {
                        return true;
                    }
                }
            }

            return false;
        }
    }

    protected static class Skip extends Leaf {

        protected Skip(String[] t) {
            super(t);
        }

        @Override
        protected void find(List<AbstractSyntaxTree> res, Token t) {
        }
    }

    public static class Precedence {

        int     value;
        boolean leftAssoc; // left associative

        public Precedence(int v, boolean a) {
            value = v;
            leftAssoc = a;
        }
    }

    public static class Operators extends HashMap<String, Precedence> {

        public static boolean LEFT  = true;
        public static boolean RIGHT = false;

        public void add(String name, int prec, boolean leftAssoc) {
            put(name, new Precedence(prec, leftAssoc));
        }
    }

    protected static class Expr extends Element {

        protected Factory   factory;
        protected Operators ops;
        protected Parser0   factor;

        protected Expr(Class<? extends AbstractSyntaxTree> clazz, Parser0 exp,
                       Operators map) {
            factory = Factory.getForAbstractSyntaxList(clazz);
            ops = map;
            factor = exp;
        }

        @Override
        public void parse(Lexer lexer, List<AbstractSyntaxTree> res) throws ParseException {
            AbstractSyntaxTree right = factor.parse(lexer);
            Precedence prec;
            while ((prec = nextOperator(lexer)) != null) {
                right = doShift(lexer, right, prec.value);
            }

            res.add(right);
        }

        private AbstractSyntaxTree doShift(Lexer lexer, AbstractSyntaxTree left, int prec)
                throws ParseException {
            ArrayList<AbstractSyntaxTree> list = new ArrayList<AbstractSyntaxTree>();
            list.add(left);
            list.add(new AbstractSyntaxLeaf(lexer.read()));
            AbstractSyntaxTree right = factor.parse(lexer);
            Precedence next;
            while ((next = nextOperator(lexer)) != null
                   && rightIsExpr(prec, next)) {
                right = doShift(lexer, right, next.value);
            }

            list.add(right);
            return factory.make(list);
        }

        private Precedence nextOperator(Lexer lexer) throws ParseException {
            Token t = lexer.peek(0);
            if (t.isIdentifier()) {
                return ops.get(t.getText());
            } else {
                return null;
            }
        }

        private static boolean rightIsExpr(int prec, Precedence nextPrec) {
            if (nextPrec.leftAssoc) {
                return prec < nextPrec.value;
            } else {
                return prec <= nextPrec.value;
            }
        }

        @Override
        protected boolean match(Lexer lexer) throws ParseException {
            return factor.match(lexer);
        }
    }

    public static final String factoryName = "create";

    /**
     * 工厂类，在工厂类中提供的 make 方法可以为我们构造一个 AST 的实例
     */
    protected static abstract class Factory {

        protected abstract AbstractSyntaxTree make0(Object arg) throws Exception;

        protected AbstractSyntaxTree make(Object arg) {
            try {
                return make0(arg);
            } catch (IllegalArgumentException e1) {
                throw e1;
            } catch (Exception e2) {
                throw new RuntimeException(e2); // this compiler is broken.
            }
        }

        /**
         * 获取 AbstractSyntaxList 的工厂方法。
         *
         * 如果 clazz 不为 null，那么从 clazz 中获取一个名为 create，参数为 List 的方法作为工厂的 provider
         * 这里注意一个细节，由于 java 的泛型是基于类型擦除来实现，所以
         * test(List<String> l) 和 test(List<Integer> l) 是一个方法。
         *
         * 如果 clazz 为 null，那么构建一个简单的 provider。
         * 当参数 size == 1，那么就是 arg.get(0)；
         * 当参数 size != 1，那么就是 new AbstractSyntaxList(args)
         * @param clazz
         * @return
         */
        protected static Factory getForAbstractSyntaxList(Class<? extends AbstractSyntaxTree> clazz) {
            Factory f = get(clazz, List.class);
            if (f == null) {
                f = new Factory() {
                    @Override
                    protected AbstractSyntaxTree make0(Object arg) throws Exception {
                        List<AbstractSyntaxTree> results = (List<AbstractSyntaxTree>) arg;
                        if (results.size() == 1) {
                            return results.get(0);
                        } else {
                            return new AbstractSyntaxList(results);
                        }
                    }
                };
            }
            return f;
        }

        /**
         * 创建一个工厂类，工厂类包含三种情况。
         *
         * 1. clazz == null
         *
         * 会获取一个默认的工厂类，工厂类的 make(Object args) 方法会将 args 转换为 List 并以 list 作为返回值
         *
         * 2. clazz != null && clazz.hasMethod("create", List<AbstractSyntaxTree>)
         *
         * 工厂会基于反射来从 clazz 中获取参数为 argType，名字为 create 的方法。
         * 这个获取到的方法会在工厂需要创建 AST 时被调用来创建一个 AST。
         *
         * 3. else
         *
         * 直接调用构造器来获取实例
         *
         * @param clazz 对应的class
         * @param argType class 的 create 方法的参数类型列表
         * @return 工厂
         */
        protected static Factory get(Class<? extends AbstractSyntaxTree> clazz,
                                     Class<?> argType) {
            if (clazz == null) {
                return null;
            }
            try {
                final Method m = clazz.getMethod(factoryName,
                                                 new Class<?>[]{argType});
                return new Factory() {
                    @Override
                    protected AbstractSyntaxTree make0(Object arg) throws Exception {
                        return (AbstractSyntaxTree) m.invoke(null, arg);
                    }
                };
            } catch (NoSuchMethodException e) {
            }
            try {
                final Constructor<? extends AbstractSyntaxTree> c
                        = clazz.getConstructor(argType);
                return new Factory() {
                    @Override
                    protected AbstractSyntaxTree make0(Object arg) throws Exception {
                        return c.newInstance(arg);
                    }
                };
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected List<Element> elements;
    protected Factory       factory;

    public Parser0(Class<? extends AbstractSyntaxTree> clazz) {
        reset(clazz);
    }

    protected Parser0(Parser0 p) {
        elements = p.elements;
        factory = p.factory;
    }

    /**
     * parse 方法是 Parser 的实例方法，每个 Parser 内部包含了多个 {@link Parser0#elements}。
     * 因为 BNF 中的每一个定义都有可能是不同的类型，比如 program 可能是 statement，也有可能是终结符 ;
     * 所以每一个 Parser 实例都包含了多个 element，然后 element 会去判断是否可以正常解析 BNF 元素
     *
     * @param lexer lexer
     * @return ast
     * @throws ParseException parse 异常
     */
    public AbstractSyntaxTree parse(Lexer lexer) throws ParseException {
        ArrayList<AbstractSyntaxTree> results = new ArrayList<AbstractSyntaxTree>();
        for (Element e : elements) {
            e.parse(lexer, results);
        }

        return factory.make(results);
    }

    protected boolean match(Lexer lexer) throws ParseException {
        if (elements.size() == 0) {
            return true;
        } else {
            Element e = elements.get(0);
            return e.match(lexer);
        }
    }

    /**
     * 创建parser对象，parser 对象内部的 factory 提供的 make 方法可以构建一个 AST
     * 由于没有提供 clazz，所以 make 方法的实现是根据 make(List.class)
     */
    public static Parser0 rule() {
        return rule(null);
    }

    /**
     * 创建parser对象，parser对象内部包含了一个 factory，这个 factory 的 make 方法可以构建一个 AST
     * make 方法会基于 clazz 方法内部提供的 create(List.class) 方法来构造一个 AST
     */
    public static Parser0 rule(Class<? extends AbstractSyntaxTree> clazz) {
        return new Parser0(clazz);
    }

    public Parser0 reset() {
        elements = new ArrayList<Element>();
        return this;
    }

    public Parser0 reset(Class<? extends AbstractSyntaxTree> clazz) {
        elements = new ArrayList<Element>();
        factory = Factory.getForAbstractSyntaxList(clazz);
        return this;
    }

    /**
     * 向语法规则中添加终结符（整型字面量）
     *
     * @return parser
     */
    public Parser0 number() {
        return number(null);
    }

    /**
     * 向语法规则中添加终结符（整型字面量）
     *
     * @return parser
     */
    public Parser0 number(Class<? extends AbstractSyntaxLeaf> clazz) {
        elements.add(new NumToken(clazz));
        return this;
    }

    /**
     * 向语法规则中添加终结符（除了保留字 r 外的标识符）
     *
     * @return parser
     */
    public Parser0 identifier(HashSet<String> reserved) {
        return identifier(null, reserved);
    }

    /**
     * 向语法规则中添加终结符（除了保留字 r 外的标识符）
     *
     * @return parser
     */
    public Parser0 identifier(Class<? extends AbstractSyntaxLeaf> clazz,
                              HashSet<String> reserved) {
        elements.add(new IdToken(clazz, reserved));
        return this;
    }

    /**
     * 向语法规则中添加终结符（字符串常量）
     *
     * @return parser
     */
    public Parser0 string() {
        return string(null);
    }

    /**
     * 向语法规则中添加终结符（字符串常量）
     *
     * @return parser
     */
    public Parser0 string(Class<? extends AbstractSyntaxLeaf> clazz) {
        elements.add(new StrToken(clazz));
        return this;
    }

    /**
     * 向语法规则中添加终结符（与pattern匹配的标识符）
     *
     * @return parser
     */
    public Parser0 token(String... pat) {
        elements.add(new Leaf(pat));
        return this;
    }

    /**
     * 向语法规则中添加未包含于抽象语法树的终结符（与pattern匹配的标识符）
     *
     * @return parser
     */
    public Parser0 sep(String... pat) {
        elements.add(new Skip(pat));
        return this;
    }

    /**
     * 向语法规则中添加非终结符p
     *
     * @return parser
     */
    public Parser0 ast(Parser0 p) {
        elements.add(new Tree(p));
        return this;
    }

    /**
     * 向语法规则中添加若干个由or关系连接的非终结符
     *
     * @return parser
     */
    public Parser0 or(Parser0... p) {
        elements.add(new OrTree(p));
        return this;
    }

    /**
     * 向语法规则中添加可省略的非终结符（如果省略，则作为一颗仅有根节点的抽象语法树处理）
     *
     * @return parser
     */
    public Parser0 maybe(Parser0 p) {
        Parser0 p2 = new Parser0(p);
        p2.reset();
        elements.add(new OrTree(new Parser0[]{p, p2}));
        return this;
    }

    /**
     * 向语法规则中添加可省略的非终结符
     *
     * @return parser
     */
    public Parser0 option(Parser0 p) {
        elements.add(new Repeat(p, true));
        return this;
    }

    /**
     * 向语法规则中添加至少重复出现0次的非终结符
     *
     * @return parser
     */
    public Parser0 repeat(Parser0 p) {
        elements.add(new Repeat(p, false));
        return this;
    }

    /**
     * 向语法规则中添加双目运算表达式
     *
     * @return parser
     */
    public Parser0 expression(Parser0 subexp, Operators operators) {
        elements.add(new Expr(null, subexp, operators));
        return this;
    }

    /**
     * 向语法规则中添加双目运算表达式
     *
     * @return parser
     */
    public Parser0 expression(Class<? extends AbstractSyntaxTree> clazz, Parser0 subexp,
                              Operators operators) {
        elements.add(new Expr(clazz, subexp, operators));
        return this;
    }

    /**
     * 为语法规则起始处的or添加新的分支选项
     */
    public Parser0 insertChoice(Parser0 p) {
        Element e = elements.get(0);
        if (e instanceof OrTree) {
            ((OrTree) e).insert(p);
        } else {
            Parser0 otherwise = new Parser0(this);
            reset(null);
            or(p, otherwise);
        }
        return this;
    }
}