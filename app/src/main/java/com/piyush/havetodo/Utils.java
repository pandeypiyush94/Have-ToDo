package com.piyush.havetodo;

import android.content.Context;
import android.content.res.TypedArray;


class Utils {
        static int getToolbarHeight(Context context) {
            final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(new int[]{android.R.attr.actionBarSize});
            int toolbarHeight = (int) styledAttributes.getDimension(0, 0);
            styledAttributes.recycle();
            return toolbarHeight;
        }
}
