package com.xxx.stone.object;

import com.xxx.stone.ast.AbstractSyntaxLeaf;
import com.xxx.stone.ast.AbstractSyntaxList;
import com.xxx.stone.ast.AbstractSyntaxTree;
import com.xxx.stone.interpreter.Environment;
import java.util.List;

/**
 * <pre>
 * class statement
 *
 * @author 0x822a5b87
 * </pre>
 */
public class ClassStatement extends AbstractSyntaxList {

    public ClassStatement(List<AbstractSyntaxTree> children) {
        super(children);
    }

    public String getClassName() {
        return ((AbstractSyntaxLeaf) child(0)).getToken().getText();
    }

    public String getSuperClassName() {
        if (numChildren() < 3) {
            return null;
        }
        return ((AbstractSyntaxLeaf) child(1)).getToken().getText();
    }

    public ClassBody getBody() {
        return (ClassBody) child(numChildren() - 1);
    }

    @Override
    public Object eval(Environment env) {
        env.putNew(getClassName(), new ClassInfo(this, env));
        return getClassName();
    }

    @Override
    public String toString() {
        String parent = getSuperClassName();
        if (parent == null) {
            parent = "*";
        }
        return "(class " + getClassName() + " [parent]-" + parent + " " + getBody() + ")";
    }
}
