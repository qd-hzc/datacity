package str;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wxl on 2016/3/2.
 */
public class TestReg {
    @Test
    public void t1() {
        String str = "dfa${y-1}$da${y-2}$";
        String value = null;

        // 去掉<>标签及其之间的内容
        Pattern p = Pattern.compile("\\$\\{y-\\d\\}\\$");
        Matcher m = p.matcher(str);
        String temp = str;
        // 下面的while循环式进行循环匹配替换，把找到的所有
        // 符合匹配规则的字串都替换为你想替换的内容
        while (m.find()) {
            value = m.group(0);
            System.out.println(value);
            temp = temp.replace(value, "~~~~");
        }
        System.out.println(temp);
    }

    @Test
    public void t2(){
        String aa="1";
        double a= Double.parseDouble(aa);
        System.out.println(a);
    }
}
