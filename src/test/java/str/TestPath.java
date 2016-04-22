package str;

import com.city.common.pojo.Constant;
import com.city.resourcecategory.themes.pojo.EsiTheme;
import com.city.resourcecategory.themes.util.FormatThemeUtil;
import org.junit.Test;
import java.util.List;
import java.util.Map;

/**
 * Created by wxl on 2016/3/10.
 */
public class TestPath {
    @Test
    public void t1(){
        List<String> themeConfigs = FormatThemeUtil.getThemeConfigs();
        System.out.println(themeConfigs);
        EsiTheme themeByConfig = FormatThemeUtil.getThemeByConfig(themeConfigs.get(0));
        System.out.println(themeByConfig);

        Map<String, EsiTheme> themes = Constant.themes;
        System.out.println(themes);
    }
}
