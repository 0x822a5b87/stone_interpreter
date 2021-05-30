package com.xxx.stone.test;

import com.xxx.stone.Lexer;
import com.xxx.stone.ParseException;
import com.xxx.stone.ast.AbstractSyntaxLeaf;
import com.xxx.stone.ast.AbstractSyntaxTree;
import com.xxx.stone.ast.BinaryExpr;
import com.xxx.stone.ast.NumberLiteral;
import com.xxx.stone.ast.Token;
import com.xxx.stone.constant.StrConstant;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
 * factor     : NUMBER | "(" expression ")"
 * expression : term | expression ("+" | "-") term
 *
 * digit      : "0" | "1" | ... | "9"
 * number     : digit | digit number
 * term       : factor { ("*" | "/") factor }
 * add_expr   : term { ("+" | "-") term }
 * rel_expr   : add_expr { ("<" | ">") add_expr }
 * eqe_xpr    : rel_expr { ("==" | "!=") rel_expr }
 * and_expr   : eqe_expr { "&&" eqe_expr }
 * or_expr    : and_expr { "||" and_expr } *
 * </pre>
 *
 * @author 0x822a5b87
 */
public class OpPrecedenceParser {

    private final Lexer                   lexer;
    private final Map<String, Precedence> operators;

    public OpPrecedenceParser(Lexer lexer) {
        this.lexer = lexer;
        operators = new HashMap<>();
        operators.put("<", new Precedence(1, true));
        operators.put(">", new Precedence(1, true));
        operators.put("+", new Precedence(2, true));
        operators.put("-", new Precedence(2, true));
        operators.put("*", new Precedence(3, true));
        operators.put("/", new Precedence(3, true));
        operators.put("^", new Precedence(4, false));
    }

    public AbstractSyntaxTree expression() throws ParseException {
        AbstractSyntaxTree right = factor();
        Precedence nextOperator;
        while ((nextOperator = peekNextOperator()) != null) {
            right = doShift(right, nextOperator.precedence);
        }
        return right;
    }

    public AbstractSyntaxTree factor() throws ParseException {
        if (isNextTokenMatch(StrConstant.LEFT_PARENTHESES)) {
            nextToken(StrConstant.LEFT_PARENTHESES);
            AbstractSyntaxTree expr = expression();
            nextToken(StrConstant.RIGHT_PARENTHESES);
            return expr;
        } else {
            Token token = lexer.read();
            if (token.isNumber()) {
                return new NumberLiteral(token);
            } else {
                throw new ParseException(token);
            }
        }
    }

    private Precedence peekNextOperator() throws ParseException {
        Token token = lexer.peek(0);
        if (token.isIdentifier()) {
            return operators.get(token.getText());
        }
        return null;
    }

    /**
     * 根据优先级对表达式进行移进。
     * 移进的思路很简单：{@link #doShift(AbstractSyntaxTree, int)} 会在两个地方被调用。一个是自身的递归调用，另外一个就是
     * {@link #expression()} 内的调用。
     * 在{@link #expression()}解析表达式的第一步，解析到了一个 factor 并作为 left 传入，
     * 在 doShift 中，我们读取 operator 和 right factor。这个时候面临的问题是，right factor 是直接作为 operator 的右操作数，
     * 还是与 next Operator 结合之后作为 operator 的右操作数，我们递归的去做这个事情即可。
     *
     * @param left left
     * @param precedence 优先级
     * @return AST
     * @throws ParseException parse exception
     */
    private AbstractSyntaxTree doShift(AbstractSyntaxTree left, int precedence) throws ParseException {
        AbstractSyntaxLeaf operator = new AbstractSyntaxLeaf(lexer.read());
        AbstractSyntaxTree right = factor();
        Precedence next;
        while ((next = peekNextOperator()) != null && rightIsExpr(precedence, next)) {
            right = doShift(right, next.precedence);
        }
        return new BinaryExpr(Arrays.asList(left, operator, right));
    }

    private boolean rightIsExpr(int prevPrecedence, Precedence nextPrecedence) {
        if (nextPrecedence.leftAssociative) {
            return prevPrecedence < nextPrecedence.precedence;
        } else {
            return prevPrecedence <= nextPrecedence.precedence;
        }
    }

    private void nextToken(String name) throws ParseException {
        Token token = lexer.read();
        if (!token.isIdentifier() || !token.getText().equals(name)) {
            throw new ParseException(token);
        }
    }

    private boolean isNextTokenMatch(String name) throws ParseException {
        Token token = lexer.peek(0);
        return token.isIdentifier() && name.equals(token.getText());
    }

    public static class Precedence {

        /**
         * 优先级，越大优先级越高
         */
        private final int     precedence;
        /**
         * 左结合
         */
        private final boolean leftAssociative;

        public Precedence(int precedence, boolean leftAssociative) {
            this.precedence = precedence;
            this.leftAssociative = leftAssociative;
        }
    }

    public static void main(String[] args) throws ParseException {
        OpPrecedenceParser parser = new OpPrecedenceParser(new Lexer(new InputStreamReader(System.in)));
        AbstractSyntaxTree expr = parser.expression();
        System.out.println(expr);
    }

}
