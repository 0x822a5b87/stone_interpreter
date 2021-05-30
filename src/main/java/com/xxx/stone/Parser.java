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
public class Parser {

    private String name;

    /**
     * <pre>
     * {@link Parser} 的实际工作类，表示了我们的 BNF，以下面的 BNF 为例：
     *     {@code
     *          rule().or(statement, rule(NullStatement.class))
     *                .sep(";", Token.EOL);
     *     }
     *
     * 在整个解析器中，我们其实包含了两个元素：
     *
     * 1. 表示 or(statement, rule(NullStatement.class)) 的 Element
     * 2. 表示 sep(";", Token.EOL); 的 Element
     *
     * 按照我们的 BNF 的定义，我们首先使用第一个 Element 去解析第一个表达式，
     * 并用过解析剩余的 token 继续解析第二个表达式。
     *
     * 在解析的过程中，我们必须使用 {@link Element#match(Lexer)} 方法先判断当前 token 是否匹配，
     * 随后调用 {@link Element#elementParse(Lexer, List)} 进行实际的解析；
     * </pre>
     */
    protected static abstract class Element {

        /**
         * 执行语法分析
         *
         * @param lexer 词法解析器
         * @param res 抽象语法树
         * @throws ParseException parser异常
         */
        protected abstract void elementParse(Lexer lexer, List<AbstractSyntaxTree> res)
                throws ParseException;

        /**
         * 检查输入是否匹配当前的 Parser
         *
         * @param lexer 词法解析器
         * @return 是否匹配Parser
         * @throws ParseException 解析异常
         */
        protected abstract boolean match(Lexer lexer) throws ParseException;
    }

    protected static class TreeElement extends Element {

        protected Parser parser;

        protected TreeElement(Parser p) {
            parser = p;
        }

        @Override
        protected void elementParse(Lexer lexer, List<AbstractSyntaxTree> res)
                throws ParseException {
            AbstractSyntaxTree ast = parser.parse(lexer);
            res.add(ast);
        }

        @Override
        protected boolean match(Lexer lexer) throws ParseException {
            return parser.match(lexer);
        }
    }

    protected static class OrTreeElement extends Element {

        protected Parser[] parsers;

        protected OrTreeElement(Parser[] p) {
            parsers = p;
        }

        @Override
        protected void elementParse(Lexer lexer, List<AbstractSyntaxTree> res)
                throws ParseException {
            Parser p = choose(lexer);
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

        protected Parser choose(Lexer lexer) throws ParseException {
            for (Parser p : parsers) {
                if (p.match(lexer)) {
                    return p;
                }
            }

            return null;
        }

        protected void insert(Parser p) {
            Parser[] newParsers = new Parser[parsers.length + 1];
            newParsers[0] = p;
            System.arraycopy(parsers, 0, newParsers, 1, parsers.length);
            parsers = newParsers;
        }
    }

    protected static class RepeatElement extends Element {

        protected Parser  parser;
        protected boolean onlyOnce;

        protected RepeatElement(Parser p, boolean once) {
            parser = p;
            onlyOnce = once;
        }

        @Override
        protected void elementParse(Lexer lexer, List<AbstractSyntaxTree> res)
                throws ParseException {
            while (parser.match(lexer)) {
                AbstractSyntaxTree t = parser.parse(lexer);
                // 忽略所有为空的 ast
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
            return parser.match(lexer);
        }
    }

    protected static abstract class ATokenElement extends Element {

        protected Factory factory;

        protected ATokenElement(Class<? extends AbstractSyntaxLeaf> type) {
            if (type == null) {
                type = AbstractSyntaxLeaf.class;
            }
            factory = Factory.get(type, Token.class);
        }

        @Override
        protected void elementParse(Lexer lexer, List<AbstractSyntaxTree> res)
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

    protected static class IdTokenElement extends ATokenElement {

        HashSet<String> reserved;

        protected IdTokenElement(Class<? extends AbstractSyntaxLeaf> type, HashSet<String> r) {
            super(type);
            reserved = r != null ? r : new HashSet<String>();
        }

        @Override
        protected boolean test(Token t) {
            return t.isIdentifier() && !reserved.contains(t.getText());
        }
    }

    protected static class NumTokenElement extends ATokenElement {

        protected NumTokenElement(Class<? extends AbstractSyntaxLeaf> type) {
            super(type);
        }

        @Override
        protected boolean test(Token t) {
            return t.isNumber();
        }
    }

    protected static class StrTokenElement extends ATokenElement {

        protected StrTokenElement(Class<? extends AbstractSyntaxLeaf> type) {
            super(type);
        }

        @Override
        protected boolean test(Token t) {
            return t.isString();
        }
    }

    protected static class LeafElement extends Element {

        protected String[] tokens;

        protected LeafElement(String[] pat) {
            tokens = pat;
        }

        @Override
        protected void elementParse(Lexer lexer, List<AbstractSyntaxTree> res)
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

    protected static class SkipElement extends LeafElement {

        protected SkipElement(String[] t) {
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

    /**
     * 操作符
     */
    public static class Operators extends HashMap<String, Precedence> {

        public static boolean LEFT  = true;
        public static boolean RIGHT = false;

        public void add(String name, int prec, boolean leftAssoc) {
            put(name, new Precedence(prec, leftAssoc));
        }
    }

    protected static class ExprElement extends Element {

        protected Factory   factory;
        protected Operators ops;
        protected Parser    factor;

        protected ExprElement(Class<? extends AbstractSyntaxTree> clazz, Parser exp,
                              Operators map) {
            factory = Factory.getForAbstractSyntaxList(clazz);
            ops = map;
            factor = exp;
        }

        @Override
        public void elementParse(Lexer lexer, List<AbstractSyntaxTree> res) throws ParseException {
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

        /**
         * make 方法基于 Object 对象以及在初始化 {@link Factory#get(Class, Class)} 时传入的参数来构造一颗 ast；
         *
         * make 方法在一下几个位置调用：
         * <br />
         * {@link ATokenElement#elementParse(Lexer, List)}
         * <br />
         * {@link ExprElement#doShift(Lexer, AbstractSyntaxTree, int)}
         * <br />
         * {@link Parser#parse(Lexer)}
         * <br />
         * 其中 AToken 解析 Token 的抽象类，注意，AToken 会自动的设置工厂方法的 class 为 {@link AbstractSyntaxLeaf}
         * <br />
         * Expr 解析 Expr 表达式
         * <br />
         * AToken 和 Expr 都是执行实际的解析，而 Parser 的 parse 方法是调用了内部的 {@link Parser#elements} 的所有
         * parse 方法并将得到的结果返回构造。
         *
         * @param arg
         * @return
         */
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
         *
         * @param clazz
         * @return
         */
        @SuppressWarnings("unchecked")
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

    public Parser(Class<? extends AbstractSyntaxTree> clazz) {
        reset(clazz);
    }

    protected Parser(Parser p) {
        elements = p.elements;
        factory = p.factory;
    }

    /**
     * parse 方法是 Parser 的实例方法，每个 Parser 内部包含了多个 {@link Parser#elements}。
     * 在 BNF 中的复合类型，例如 expr:factor { OP factor }
     * 需要先解析 factor，随后解析 { OP factor }
     * 所以一个 Parser 内部是有一个 {@link Parser#elements} 的
     *
     * @param lexer lexer
     * @return ast
     * @throws ParseException parse 异常
     */
    public AbstractSyntaxTree parse(Lexer lexer) throws ParseException {
        ArrayList<AbstractSyntaxTree> results = new ArrayList<AbstractSyntaxTree>();
        for (Element e : elements) {
            e.elementParse(lexer, results);
        }

        return factory.make(results);
    }

    /**
     * <pre>
     * 再回想下我们对 {@link Element} 的定义，其实 elements 表示的就是一个合法的 BNF。
     * 所以我们只需要判断第一个 element 是否匹配即可。
     * 如果这个 element 是一个 {@link OrTreeElement}，那么它也会有自己的判断方法。
     * </pre>
     */
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
     * 由于没有提供 clazz，所以 make 方法的实现是根据 {@link Factory#make(Object)}
     */
    public static Parser rule(String name) {
        return rule(name, null);
    }

    /**
     * 创建parser对象，parser对象内部包含了一个 factory，这个 factory 的 make 方法可以构建一个 AST
     * make 方法会基于 clazz 方法内部提供的 create(List.class) 方法来构造一个 AST
     */
    public static Parser rule(String name, Class<? extends AbstractSyntaxTree> clazz) {
        Parser p = new Parser(clazz);
        p.name = name;
        return p;
    }

    public Parser reset() {
        elements = new ArrayList<Element>();
        return this;
    }

    public Parser reset(Class<? extends AbstractSyntaxTree> clazz) {
        elements = new ArrayList<Element>();
        factory = Factory.getForAbstractSyntaxList(clazz);
        return this;
    }

    /**
     * 向语法规则中添加终结符（整型字面量）
     *
     * @return parser
     */
    public Parser number() {
        return number(null);
    }

    /**
     * 向语法规则中添加终结符（整型字面量）
     *
     * @return parser
     */
    public Parser number(Class<? extends AbstractSyntaxLeaf> clazz) {
        elements.add(new NumTokenElement(clazz));
        return this;
    }

    /**
     * 向语法规则中添加终结符（除了保留字 r 外的标识符）
     *
     * @return parser
     */
    public Parser identifier(HashSet<String> reserved) {
        return identifier(null, reserved);
    }

    /**
     * 向语法规则中添加终结符（除了保留字 r 外的标识符）
     *
     * @return parser
     */
    public Parser identifier(Class<? extends AbstractSyntaxLeaf> clazz,
                             HashSet<String> reserved) {
        elements.add(new IdTokenElement(clazz, reserved));
        return this;
    }

    /**
     * 向语法规则中添加终结符（字符串常量）
     *
     * @return parser
     */
    public Parser string() {
        return string(null);
    }

    /**
     * 向语法规则中添加终结符（字符串常量）
     *
     * @return parser
     */
    public Parser string(Class<? extends AbstractSyntaxLeaf> clazz) {
        elements.add(new StrTokenElement(clazz));
        return this;
    }

    /**
     * 向语法规则中添加终结符（与pattern匹配的标识符）
     *
     * @return parser
     */
    public Parser token(String... pat) {
        elements.add(new LeafElement(pat));
        return this;
    }

    /**
     * 向语法规则中添加未包含于抽象语法树的终结符（与pattern匹配的标识符）
     *
     * @return parser
     */
    public Parser sep(String... pat) {
        elements.add(new SkipElement(pat));
        return this;
    }

    /**
     * 向语法规则中添加非终结符p
     *
     * @return parser
     */
    public Parser ast(Parser p) {
        elements.add(new TreeElement(p));
        return this;
    }

    /**
     * 向语法规则中添加若干个由or关系连接的非终结符
     *
     * @return parser
     */
    public Parser or(Parser... p) {
        elements.add(new OrTreeElement(p));
        return this;
    }

    /**
     * 向语法规则中添加可省略的非终结符（如果省略，则作为一颗仅有根节点的抽象语法树处理）
     *
     * @return parser
     */
    public Parser maybe(Parser p) {
        Parser p2 = new Parser(p);
        p2.reset();
        elements.add(new OrTreeElement(new Parser[]{p, p2}));
        return this;
    }

    /**
     * 向语法规则中添加可省略的非终结符
     *
     * @return parser
     */
    public Parser option(Parser p) {
        elements.add(new RepeatElement(p, true));
        return this;
    }

    /**
     * 向语法规则中添加至少重复出现0次的非终结符
     *
     * @return parser
     */
    public Parser repeat(Parser p) {
        elements.add(new RepeatElement(p, false));
        return this;
    }

    /**
     * 向语法规则中添加双目运算表达式
     *
     * @return parser
     */
    public Parser expression(Parser subexp, Operators operators) {
        elements.add(new ExprElement(null, subexp, operators));
        return this;
    }

    /**
     * 向语法规则中添加双目运算表达式
     *
     * @return parser
     */
    public Parser expression(Class<? extends AbstractSyntaxTree> clazz, Parser subexp,
                             Operators operators) {
        elements.add(new ExprElement(clazz, subexp, operators));
        return this;
    }

    /**
     * 为语法规则起始处的or添加新的分支选项
     */
    public Parser insertChoice(Parser p) {
        Element e = elements.get(0);
        if (e instanceof OrTreeElement) {
            ((OrTreeElement) e).insert(p);
        } else {
            Parser otherwise = new Parser(this);
            reset(null);
            or(p, otherwise);
        }
        return this;
    }

    public String getName() {
        return name;
    }
}
