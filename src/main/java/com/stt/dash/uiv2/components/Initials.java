package com.stt.dash.uiv2.components;

import com.stt.dash.uiv2.util.FontSize;
import com.stt.dash.uiv2.util.FontWeight;
import com.stt.dash.uiv2.util.LumoStyles;
import com.stt.dash.uiv2.util.UIUtils;
import com.stt.dash.uiv2.util.css.BorderRadius;

public class Initials extends FlexBoxLayout {

    private String CLASS_NAME = "initials";

    public Initials(String initials) {
        setAlignItems(Alignment.CENTER);
        setBackgroundColor(LumoStyles.Color.Contrast._10);
        setBorderRadius(BorderRadius.L);
        setClassName(CLASS_NAME);
        UIUtils.setFontSize(FontSize.S, this);
        UIUtils.setFontWeight(FontWeight._600, this);
        setHeight(LumoStyles.Size.M);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setWidth(LumoStyles.Size.M);

        add(initials);
    }

}
