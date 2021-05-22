package com.xxx.stone.vm;

import com.xxx.stone.exception.StoneException;

/**
 * 指令集
 *
 * @author 0x822a5b87
 */
public class InstructionSet {
    public static final byte ICONST  = 1;
    public static final byte BCONST  = 2;
    public static final byte SCONST  = 3;
    public static final byte MOVE    = 4;
    public static final byte GMOVE   = 5;
    public static final byte IFZERO  = 6;
    public static final byte GOTO    = 7;
    public static final byte CALL    = 8;
    public static final byte RETURN  = 9;
    public static final byte SAVE    = 10;
    public static final byte RESTORE = 11;
    public static final byte NEG     = 12;
    public static final byte ADD     = 13;
    public static final byte SUB     = 14;
    public static final byte MUL     = 15;
    public static final byte DIV     = 16;
    public static final byte REM     = 17;
    public static final byte EQUAL   = 18;
    public static final byte MORE    = 19;
    public static final byte LESS    = 20;

    public static int decodeOffset(byte offset) {
        return offset;
    }

    public static boolean isOffset(byte operand) {
        return operand >= 0;
    }

    public static boolean isRegister(byte operand) {
        return operand < 0 && operand >= -Vm.NUM_OF_REG;
    }

    public static short encodeShortOffset(int offset) {
        if (offset > Short.MAX_VALUE || offset < Short.MIN_VALUE) {
            throw new StoneException("too big short offset : " + offset);
        }
        return (short) offset;
    }

    public static byte encodeOffset(int offset) {
        if (offset > Byte.MAX_VALUE) {
            throw new StoneException("too big byte offset : " + offset);
        }

        return (byte) offset;
    }

    public static int decodeRegister(byte operand) {
        return -operand - 1;
    }

    /**
     * encode register, the register is encoded to [-7, -1]
     * @param reg register
     * @return encoded register
     */
    public static byte encodeRegister(int reg) {
        if (reg > Vm.NUM_OF_REG || reg < 0) {
            throw new StoneException("too many register required : " + reg);
        }
        return (byte) (-(reg + 1));
    }
}
