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

/**
 * <pre>
 * 四则运算表达式的parser
 *     digit      : "0" | "1" | ... | "9"
 *     number     : digit | digit number
 *     factor     : number | "(" expression ")"
 *     term       : factor { ("*" | "/") factor }
 *     expression : term { ("+" | "-") term }
 *
 * 在这里，我们不用手动的去处理四则运算的优先级：
 * 因为我们采用的是递归下降的算法，在递归的过程中，我们会在 {@link #term()} 优先解析 * 和 /。
 * </pre>
 *
 * @author 0x822a5b87
 */
public class ExprParser {

    private final Lexer lexer;

    public ExprParser(Lexer lexer) {
        this.lexer = lexer;
    }

    public AbstractSyntaxTree factor() throws ParseException {
        if (isNextToken(StrConstant.LEFT_PARENTHESES)) {
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

    public AbstractSyntaxTree term() throws ParseException {
        AbstractSyntaxTree left = factor();
        while (isNextToken(StrConstant.MUL) | isNextToken(StrConstant.DIV)) {
            AbstractSyntaxTree op = new AbstractSyntaxLeaf(lexer.read());
            AbstractSyntaxTree right = factor();
            left = new BinaryExpr(Arrays.asList(left, op, right));
        }
        return left;
    }

    public AbstractSyntaxTree expression() throws ParseException {
        AbstractSyntaxTree left = term();
        while (isNextToken(StrConstant.ADD) || isNextToken(StrConstant.SUB)) {
            AbstractSyntaxLeaf op = new AbstractSyntaxLeaf(lexer.read());
            AbstractSyntaxTree right = term();
            left = new BinaryExpr(Arrays.asList(left, op, right));
        }
        return left;
    }

    protected void nextToken(String name) throws ParseException {
        Token token = lexer.read();
        if (!token.isIdentifier() || !token.getText().equals(name)) {
            throw new ParseException(token);
        }
    }

    protected boolean isNextToken(String name) throws ParseException {
        Token token = lexer.peek(0);
        return token.isIdentifier() && name.equals(token.getText());
    }

    public static void main(String[] args) throws ParseException {
        ExprParser parser = new ExprParser(new Lexer(new InputStreamReader(System.in)));
        AbstractSyntaxTree expr = parser.expression();
        System.out.println(expr);
    }
}
