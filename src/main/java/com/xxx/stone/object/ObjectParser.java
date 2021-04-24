package com.xxx.stone.object;

import com.xxx.stone.ParseException;
import com.xxx.stone.nat1ve.NativeParser;
import java.io.IOException;

/**
 * <pre>
 * @author 0x822a5b87
 *
 * 面向对象支持
 * </pre>
 */
public class ObjectParser extends NativeParser {

    public static void main(String[] args) throws ParseException, IOException {
        ObjectParser objectParser = new ObjectParser();
//        objectParser.run(CLAZZ_CODE);
        objectParser.run(NESTED_CLASS_CODE);
    }

    private static final String NESTED_CLASS_CODE = "class Human {\n"
                                                    + "    age = 20\n"
                                                    + "}\n"
                                                    + "class Male {\n"
                                                    + "    human = Human.new\n"
                                                    + "}\n"
                                                    + "class Teenager {\n"
                                                    + "\tteenager = Male.new\n"
                                                    + "}\n"
                                                    + "t = Teenager.new\n"
                                                    + "print t.teenager.human.age\n"
                                                    + "t.teenager.human.age = 10\n"
                                                    + "print t.teenager.human.age";

    private static final String CLAZZ_CODE = "class Position {\n"
                                             + "    x = y = 0\n"
                                             + "    def move(nx, ny) {\n"
                                             + "        x = nx\n"
                                             + "        y = ny\n"
                                             + "    }\n"
                                             + "}\n"
                                             + "p = Position.new\n"
                                             + ""
                                             + "p.move(3, 4)\n"
                                             + "p.x = 10\n"
                                             + "print p.x + p.y\n"
                                             + "class Pos3D extends Position {\n"
                                             + "    z = 0\n"
                                             + "    def set(nx, ny, nz) {\n"
                                             + "        x = nx\n"
                                             + "        y = ny\n"
                                             + "        z = nz\n"
                                             + "    }\n"
                                             + "}\n"
                                             + "p2 = Pos3D.new\n"
                                             + "p2.move(3, 4)\n"
                                             + "print p2.x\n"
                                             + "p2.set(5, 6, 7)\n"
                                             + "print p2.z";
}
