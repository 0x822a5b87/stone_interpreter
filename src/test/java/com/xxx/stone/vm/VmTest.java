package com.xxx.stone.vm;

import org.junit.Assert;
import org.junit.Test;

public class VmTest {

    @Test
    public void readInt() {
        byte[] array = new byte[]{1, 2, 3, 4};
        int v = Vm.readInt(array, 0);
        Assert.assertEquals(v, countInt(array, 0));
    }

    private int countInt(byte[] array, int index) {
        int ret = 0;
        for (int i = 0; i < 4; ++i) {
            int tmp = array[index + i];
            for (int power = 4 - i; power > 1; power--) {
                tmp *= 256;
            }
            ret += tmp;
        }
        return ret;
    }
}