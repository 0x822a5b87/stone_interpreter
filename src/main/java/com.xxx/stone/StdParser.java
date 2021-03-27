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

public class StdParser {
    protected static abstract class Element {
        protected abstract void parse(Lexer lexer, List<AbstractSyntaxTree> res)
                throws ParseException;
        protected abstract boolean match(Lexer lexer) throws ParseException;
    }

    protected static class Tree extends Element {
        protected StdParser parser;
        protected Tree(StdParser p) { parser = p; }
        @Override
        protected void parse(Lexer lexer, List<AbstractSyntaxTree> res)
                throws ParseException
        {
            res.add(parser.parse(lexer));
        }
        @Override
        protected boolean match(Lexer lexer) throws ParseException {
            return parser.match(lexer);
        }
    }

    protected static class OrTree extends Element {
        protected StdParser[] parsers;
        protected OrTree(StdParser[] p) { parsers = p; }
        @Override
        protected void parse(Lexer lexer, List<AbstractSyntaxTree> res)
                throws ParseException
        {
            StdParser p = choose(lexer);
            if (p == null) {
                throw new ParseException(lexer.peek(0));
            } else {
                res.add(p.parse(lexer));
            }
        }
        @Override
        protected boolean match(Lexer lexer) throws ParseException {
            return choose(lexer) != null;
        }
        protected StdParser choose(Lexer lexer) throws ParseException {
            for (StdParser p: parsers) {
                if (p.match(lexer)) {
                    return p;
                }
            }

            return null;
        }
        protected void insert(StdParser p) {
            StdParser[] newStdParsers = new StdParser[parsers.length + 1];
            newStdParsers[0] = p;
            System.arraycopy(parsers, 0, newStdParsers, 1, parsers.length);
            parsers = newStdParsers;
        }
    }

    protected static class Repeat extends Element {
        protected StdParser parser;
        protected boolean onlyOnce;
        protected Repeat(StdParser p, boolean once) { parser = p; onlyOnce = once; }
        @Override
        protected void parse(Lexer lexer, List<AbstractSyntaxTree> res)
                throws ParseException
        {
            while (parser.match(lexer)) {
                AbstractSyntaxTree t = parser.parse(lexer);
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
                throws ParseException
        {
            Token t = lexer.read();
            if (test(t)) {
                AbstractSyntaxTree leaf = factory.make(t);
                res.add(leaf);
            }
            else {
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
        protected NumToken(Class<? extends AbstractSyntaxLeaf> type) { super(type); }
        @Override
        protected boolean test(Token t) { return t.isNumber(); }
    }

    protected static class StrToken extends AToken {
        protected StrToken(Class<? extends AbstractSyntaxLeaf> type) { super(type); }
        @Override
        protected boolean test(Token t) { return t.isString(); }
    }

    protected static class Leaf extends Element {
        protected String[] tokens;
        protected Leaf(String[] pat) { tokens = pat; }
        @Override
        protected void parse(Lexer lexer, List<AbstractSyntaxTree> res)
                throws ParseException
        {
            Token t = lexer.read();
            if (t.isIdentifier()) {
                for (String token: tokens) {
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
                for (String token: tokens) {
                    if (token.equals(t.getText())) {
                        return true;
                    }
                }
            }

            return false;
        }
    }

    protected static class Skip extends Leaf {
        protected Skip(String[] t) { super(t); }
        @Override
        protected void find(List<AbstractSyntaxTree> res, Token t) {}
    }

    public static class Precedence {
        int value;
        boolean leftAssoc; // left associative
        public Precedence(int v, boolean a) {
            value = v; leftAssoc = a;
        }
    }

    public static class Operators extends HashMap<String,Precedence> {
        public static boolean LEFT = true;
        public static boolean RIGHT = false;
        public void add(String name, int prec, boolean leftAssoc) {
            put(name, new Precedence(prec, leftAssoc));
        }
    }

    protected static class Expr extends Element {
        protected Factory factory;
        protected Operators ops;
        protected StdParser factor;
        protected Expr(Class<? extends AbstractSyntaxTree> clazz, StdParser exp,
                       Operators map)
        {
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
                throws ParseException
        {
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
        protected static Factory getForAbstractSyntaxList(Class<? extends AbstractSyntaxTree> clazz) {
            Factory f = get(clazz, List.class);
            if (f == null) {
                f = new Factory() {
                    @Override
                    protected AbstractSyntaxTree make0(Object arg) throws Exception {
                        List<AbstractSyntaxTree> results = (List<AbstractSyntaxTree>)arg;
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
        protected static Factory get(Class<? extends AbstractSyntaxTree> clazz,
                                     Class<?> argType)
        {
            if (clazz == null) {
                return null;
            }
            try {
                final Method m = clazz.getMethod(factoryName,
                                                 new Class<?>[] { argType });
                return new Factory() {
                    @Override
                    protected AbstractSyntaxTree make0(Object arg) throws Exception {
                        return (AbstractSyntaxTree)m.invoke(null, arg);
                    }
                };
            } catch (NoSuchMethodException e) {}
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
    protected Factory factory;

    public StdParser(Class<? extends AbstractSyntaxTree> clazz) {
        reset(clazz);
    }
    protected StdParser(StdParser p) {
        elements = p.elements;
        factory = p.factory;
    }
    public AbstractSyntaxTree parse(Lexer lexer) throws ParseException {
        ArrayList<AbstractSyntaxTree> results = new ArrayList<AbstractSyntaxTree>();
        for (Element e: elements) {
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
    public static StdParser rule() { return rule(null); }
    public static StdParser rule(Class<? extends AbstractSyntaxTree> clazz) {
        return new StdParser(clazz);
    }
    public StdParser reset() {
        elements = new ArrayList<Element>();
        return this;
    }
    public StdParser reset(Class<? extends AbstractSyntaxTree> clazz) {
        elements = new ArrayList<Element>();
        factory = Factory.getForAbstractSyntaxList(clazz);
        return this;
    }
    public StdParser number() {
        return number(null);
    }
    public StdParser number(Class<? extends AbstractSyntaxLeaf> clazz) {
        elements.add(new NumToken(clazz));
        return this;
    }
    public StdParser identifier(HashSet<String> reserved) {
        return identifier(null, reserved);
    }
    public StdParser identifier(Class<? extends AbstractSyntaxLeaf> clazz,
                                HashSet<String> reserved)
    {
        elements.add(new IdToken(clazz, reserved));
        return this;
    }
    public StdParser string() {
        return string(null);
    }
    public StdParser string(Class<? extends AbstractSyntaxLeaf> clazz) {
        elements.add(new StrToken(clazz));
        return this;
    }
    public StdParser token(String... pat) {
        elements.add(new Leaf(pat));
        return this;
    }
    public StdParser sep(String... pat) {
        elements.add(new Skip(pat));
        return this;
    }
    public StdParser ast(StdParser p) {
        elements.add(new Tree(p));
        return this;
    }
    public StdParser or(StdParser... p) {
        elements.add(new OrTree(p));
        return this;
    }
    public StdParser maybe(StdParser p) {
        StdParser p2 = new StdParser(p);
        p2.reset();
        elements.add(new OrTree(new StdParser[] { p, p2 }));
        return this;
    }
    public StdParser option(StdParser p) {
        elements.add(new Repeat(p, true));
        return this;
    }
    public StdParser repeat(StdParser p) {
        elements.add(new Repeat(p, false));
        return this;
    }
    public StdParser expression(StdParser subexp, Operators operators) {
        elements.add(new Expr(null, subexp, operators));
        return this;
    }
    public StdParser expression(Class<? extends AbstractSyntaxTree> clazz, StdParser subexp,
                                Operators operators) {
        elements.add(new Expr(clazz, subexp, operators));
        return this;
    }
    public StdParser insertChoice(StdParser p) {
        Element e = elements.get(0);
        if (e instanceof OrTree) {
            ((OrTree)e).insert(p);
        } else {
            StdParser otherwise = new StdParser(this);
            reset(null);
            or(p, otherwise);
        }
        return this;
    }
}
