package me.matl114.matlib.utils.chat.lan.pinyinAdaptor;

import me.matl114.matlib.common.lang.annotations.EnumVal;
import me.matl114.matlib.utils.reflect.classBuild.annotation.FailHard;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectClass;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectName;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectType;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MethodTarget;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MultiDescriptive;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;
import me.matl114.matlib.utils.version.Version;

import java.util.List;
import java.util.Map;

@FailHard(thresholdInclude = Version.v1_20_R1, below = false)
@MultiDescriptive(targetDefault = "com.github.houbb.pinyin.util.PinyinHelper")
public interface PinyinHelper extends TargetDescriptor {
    @MethodTarget(isStatic = true)
    public String toPinyin(String string);

    static final String PinyinStyleEnum = "Lcom/github/houbb/pinyin/constant/enums/PinyinStyleEnum;";
    @MethodTarget(isStatic = true)
    @RedirectClass("com.github.houbb.pinyin.constant.enums.PinyinStyleEnum")
    @RedirectName("valueOf")
    public Object getPinyinStyleEnum(@EnumVal({"NORMAL", "DEFAULT", "NUM_LAST", "FIRST_LETTER", "INPUT"}) String value);

    @MethodTarget(isStatic = true)
    public String toPinyin(String string, @RedirectType(PinyinStyleEnum)Object styleEnum);

    @MethodTarget(isStatic = true)
    public String toPinyin(String string, @RedirectType(PinyinStyleEnum)Object styleEnum, String connector);

    @MethodTarget(isStatic = true)
    public List<String> toPinyinList(char chinese);

    @MethodTarget(isStatic = true)
    public List<String> toPinyinList(char chinese, @RedirectType(PinyinStyleEnum)Object styleEnum);

    @MethodTarget(isStatic = true)
    public List<String> samePinyinList(String pinyinNumLast);

    @MethodTarget(isStatic = true)
    public Map<String, List<String>> samePinyinMap(char hanzi);

    public static PinyinHelper createDefaultImpl(){
        return new PinyinHelper() {
            @Override
            public String toPinyin(String string) {
                return string;
            }

            @Override
            public Object getPinyinStyleEnum(String value) {
                return null;
            }

            @Override
            public String toPinyin(String string, Object styleEnum) {
                return string;
            }

            @Override
            public String toPinyin(String string, Object styleEnum, String connector) {
                return string;
            }

            @Override
            public List<String> toPinyinList(char chinese) {
                return List.of();
            }

            @Override
            public List<String> toPinyinList(char chinese, Object styleEnum) {
                return List.of();
            }

            @Override
            public List<String> samePinyinList(String pinyinNumLast) {
                return List.of();
            }

            @Override
            public Map<String, List<String>> samePinyinMap(char hanzi) {
                return Map.of();
            }

            @Override
            public Class getTargetClass() {
                return null;
            }
        };
    }
}
