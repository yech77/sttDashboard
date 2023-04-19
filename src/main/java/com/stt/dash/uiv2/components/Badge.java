package com.stt.dash.uiv2.components;

import com.stt.dash.uiv2.util.UIUtils;
import com.stt.dash.uiv2.util.css.lumo.BadgeColor;
import com.stt.dash.uiv2.util.css.lumo.BadgeShape;
import com.stt.dash.uiv2.util.css.lumo.BadgeSize;
import com.vaadin.flow.component.html.Span;

import java.util.StringJoiner;

import static com.stt.dash.uiv2.util.css.lumo.BadgeShape.PILL;

public class Badge extends Span {

    public Badge(String text) {
        this(text, BadgeColor.NORMAL);
    }

    public Badge(String text, BadgeColor color) {
        super(text);
        UIUtils.setTheme(color.getThemeName(), this);
    }

    public Badge(String text, BadgeColor color, BadgeSize size, BadgeShape shape) {
        super(text);
        StringJoiner joiner = new StringJoiner(" ");
        joiner.add(color.getThemeName());
        if (shape.equals(PILL)) {
            joiner.add(shape.getThemeName());
        }
        if (size.equals(BadgeSize.S)) {
            joiner.add(size.getThemeName());
        }
        UIUtils.setTheme(joiner.toString(), this);
    }

}
