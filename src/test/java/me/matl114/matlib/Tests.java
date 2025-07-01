package me.matl114.matlib;

import me.matl114.matlib.utils.security.CryptoUtils;
import org.junit.jupiter.api.Test;

public class Tests {
    private void log(String val){
        System.out.println(val);
    }
    @Test
    public void test_encode(){
        log(CryptoUtils.codeStr("invokeDynamicThis"));

        log(CryptoUtils.codeStr("耝耚耂耛耟耑耰耍耚耕耙耝耗耠耜耝耇"));
        log(CryptoUtils.codecode("invokeDynamicThis",4));
        log(CryptoUtils.codeStr("牛魔酬宾"));
    }
}
