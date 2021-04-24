package com.xxx.stone.object;

import com.xxx.stone.ast.AbstractSyntaxLeaf;
import com.xxx.stone.ast.AbstractSyntaxTree;
import com.xxx.stone.ast.BasicParser;
import com.xxx.stone.ast.Postfix;
import com.xxx.stone.exception.AccessException;
import com.xxx.stone.interpreter.Environment;
import com.xxx.stone.interpreter.NestedEnvironment;
import java.util.List;

/**
 * <pre>
 *     dot postfix
 *
 * @author 0x822a5b87
 * </pre>
 */
public class Dot extends Postfix {

    public Dot(List<AbstractSyntaxTree> c) {
        super(c);
    }

    public String getName() {
        return ((AbstractSyntaxLeaf) child(0)).getToken().getText();
    }

    @Override
    public Object eval(Environment env, Object obj) {
        String member = getName();
        if (obj instanceof ClassInfo && BasicParser.RESERVED_NEW.equals(member)) {
            return evalClassInfo(env, (ClassInfo) obj);
        } else if (obj instanceof StoneObject) {
            return evalStoneObject(env, (StoneObject) obj, member);
        } else {
            throw new AccessException("bad member access : " + member + " for " + obj);
        }
    }

    private Object evalClassInfo(Environment env, ClassInfo classInfo) {
        Environment e = new NestedEnvironment(classInfo.getEnv());
        StoneObject so = new StoneObject(e);
        e.putNew("this", so);
        initObject(classInfo, e);
        return so;
    }

    /**
     * 递归的初始化所有的父类
     */
    protected void initObject(ClassInfo ci, Environment env) {
        if (ci.getSuperClass() != null) {
            initObject(ci.getSuperClass(), env);
        }
        ci.getClassBody().eval(env);
    }

    private Object evalStoneObject(Environment env, StoneObject classInfo, String member) {
        return classInfo.read(member);
    }
}
