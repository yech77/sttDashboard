package com.stt.dash.ui.crud;

import com.stt.dash.app.security.CurrentUser;
import com.stt.dash.backend.data.entity.AbstractEntitySequence;
import com.stt.dash.backend.data.entity.util.EntityUtil;
import com.stt.dash.backend.service.FilterableCrudService;
import com.stt.dash.ui.components.SearchBar;
import com.stt.dash.ui.utils.TemplateUtil;
import com.stt.dash.ui.views.HasNotifications;
import com.vaadin.flow.component.crud.Crud;
import com.vaadin.flow.component.crud.CrudEditor;
import com.vaadin.flow.component.crud.CrudI18n;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;

import java.util.function.Consumer;

public abstract class
AbstractBakeryCrudView<E extends AbstractEntitySequence> extends Crud<E>
        implements HasUrlParameter<Long>, HasNotifications {

    //    private static final String DISCARD_MESSAGE = "There are unsaved modifications to the %s. Discard changes?";
//private static final String DELETE_MESSAGE = "Are you sure you want to delete the selected %s? This action cannot be undone.";
    private static final String DISCARD_MESSAGE = "Tiene modificaciones sin guardar en %s. ¿Descartar cambios?";
    private static final String DELETE_MESSAGE = "¿Seguro desea borrar a %s? Esta opción no se puede deshacer.";

    private final Grid<E> grid;

    private final CrudEntityPresenter<E> entityPresenter;

    protected abstract String getBasePage();

    protected long idBeforeSave;

    /**
     * En caso de que se necesite realizar una tarea luego de
     * Salvar exitosamente el entity, la clase hija puede sobreescribir
     * este metodo.
     *
     * @param idBeforeSave si es 0 es un SAVE. Si es distinto de 0, es un UPDATE
     */
    protected void afterSaving(long idBeforeSave, E entity) {
        if (idBeforeSave != 0) {
            System.out.println("----------------- Modificando: " + idBeforeSave);
        } else {
            System.out.println("--------------------- UN ON SAVED");
        }
    }
    /**
     * En caso de que se necesite realizar una tarea(ejm.completar datos) antes de
     * Salvar el entity, la clase hija puede sobreescribir este metodo.
     *
     * @param idBeforeSave si es 0 es un SAVE. Si es distinto de 0, es un UPDATE
     * @return true si se desea salvar.
     */
    protected boolean beforeSaving(long idBeforeSave, E entity) {
        return true;
    }

    protected abstract void setupGrid(Grid<E> grid);

    public AbstractBakeryCrudView(Class<E> beanType, FilterableCrudService<E> service,
                                  Grid<E> grid, CrudEditor<E> editor, CurrentUser currentUser) {
        super(beanType, grid, editor);
        this.grid = grid;
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        CrudI18n crudI18n = CrudI18n.createDefault();
        String entityName = EntityUtil.getName(beanType);
        crudI18n.setNewItem("Nuevo " + entityName);
        crudI18n.setEditItem("Editar " + entityName);
        crudI18n.setEditLabel("Editar " + entityName);
        crudI18n.getConfirm().getCancel().setContent(String.format(DISCARD_MESSAGE, entityName));
        crudI18n.getConfirm().getDelete().setContent(String.format(DELETE_MESSAGE, entityName));
        crudI18n.setDeleteItem("Borrar");
        setI18n(crudI18n);

        CrudEntityDataProvider<E> dataProvider = new CrudEntityDataProvider<>(service);
        grid.setDataProvider(dataProvider);
        setupGrid(grid);
        Crud.addEditColumn(grid);

        entityPresenter = new CrudEntityPresenter<>(service, currentUser, this);

        SearchBar searchBar = new SearchBar();
        searchBar.setActionText("Nuevo " + entityName);
        searchBar.setPlaceHolder("Buscar");
        searchBar.addFilterChangeListener(e -> dataProvider.setFilter(searchBar.getFilter()));
        searchBar.getActionButton().getElement().setAttribute("new-button", true);

        setToolbar(searchBar);
        setupCrudEventListeners(entityPresenter);
    }

    private void setupCrudEventListeners(CrudEntityPresenter<E> entityPresenter) {
        Consumer<E> onSuccess = entity -> navigateToEntity(null);
        Consumer<E> onSuccessSaved = entity -> {
            afterSaving(idBeforeSave, entity);
            navigateToEntity(null);
        };
        Consumer<E> onFail = entity -> {
//            throw new RuntimeException("The operation could not be performed.");
            throw new RuntimeException("La operación no pudo ser realizada.");
        };

        addEditListener(e ->
                entityPresenter.loadEntity(e.getItem().getId(),
                        entity -> navigateToEntity(entity.getId().toString())));

        addCancelListener(e -> navigateToEntity(null));
        addSaveListener(e -> {
            idBeforeSave = e.getItem().getId() == null ? 0 : e.getItem().getId();
            if (beforeSaving(idBeforeSave, e.getItem())) {
                entityPresenter.save(e.getItem(), onSuccessSaved, onFail);
            }
        });

        addDeleteListener(e ->
                entityPresenter.delete(e.getItem(), onSuccess, onFail));
    }

    protected void navigateToEntity(String id) {
        getUI().ifPresent(ui -> ui.navigate(TemplateUtil.generateLocation(getBasePage(), id)));
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter Long id) {
        if (id != null) {
            E item = getEditor().getItem();
            if (item != null && id.equals(item.getId())) {
                return;
            }
            entityPresenter.loadEntity(id, entity -> edit(entity, EditMode.EXISTING_ITEM));
        } else {
            setOpened(false);
        }
    }
}
