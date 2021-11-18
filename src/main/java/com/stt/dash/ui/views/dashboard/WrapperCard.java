package com.stt.dash.ui.views.dashboard;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.html.Div;

public class WrapperCard extends Div {

    private Component[] items;
    private Div card;

    public WrapperCard(String className, Component[] components,
                       String... classes) {
        addClassName(className);
        this.items = components;
        card = new Div();
        card.addClassNames(classes);
        card.add(items);

        add(card);
    }

    public WrapperCard(String id, String className, Component[] components,
                       String... classes) {
        addClassName(className);
        this.items = components;
        card = new Div();
        card.setId(id);
        card.addClassNames(classes);
        card.add(items);

        add(card);
    }

    public void removeAndAdd(String id, Chart chartToAdd) {
        Div cardToDelete = null;
        Chart chartToDelete = null;
        /* Recorre buscando el Card Id*/
        for (Object object : getChildren().toArray()) {
            Div t = (Div) object;
            System.out.println("***** " + t.getId().get());
            if (t.getId().get().equalsIgnoreCase(id)) {
                cardToDelete = t;
                break;
            }
        }
        if (cardToDelete != null) {
            /* Con el cardId busca a eliminar el chart */
            for (Object object : cardToDelete.getChildren().toArray()) {
                if (object instanceof Chart) {
                    System.out.println("Es Chart. Borrando");
                    chartToDelete = (Chart) object;

                } else if (object instanceof Button) {
                    System.out.println("Es un Button");
                }
            }
            cardToDelete.remove(chartToDelete);
            cardToDelete.add(chartToAdd);
        }
    }

}
