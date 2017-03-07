package com.southernbox.swipemenulayout.util;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by SouthernBox on 2016/3/28.
 * 尺寸转换工具类
 */

public class DisplayUtil {

    public static int getPx(Context context, int dp) {
        //获取屏幕密度
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        //屏幕密度的比例值
        float density = displayMetrics.density;
        //将dp转换为px
        return (int) (density * dp);
    }

}
