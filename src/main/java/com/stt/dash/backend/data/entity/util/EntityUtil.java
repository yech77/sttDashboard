package com.stt.dash.backend.data.entity.util;

import com.stt.dash.backend.data.entity.AbstractEntitySequence;

public final class EntityUtil {

    public static final String getName(Class<? extends AbstractEntitySequence> type) {
        if (type.getSimpleName().equalsIgnoreCase("orole")) {
            return "Rol";
        }
        if (type.getSimpleName().equalsIgnoreCase("user")) {
            return "Usuario";
        }
        if (type.getSimpleName().equalsIgnoreCase("filestosend")) {
            return "Programación";
        }
        // All main entities have simple one word names, so this is sufficient. Metadata
        // could be added to the class if necessary.
        return type.getSimpleName();
    }
}
