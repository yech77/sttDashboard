package com.stt.dash.ui.crud;

import com.stt.dash.app.security.CurrentUser;
import com.stt.dash.backend.data.entity.AbstractEntitySequence;
import com.stt.dash.backend.data.entity.util.EntityUtil;
import com.stt.dash.backend.service.FilterableCrudService;
import com.stt.dash.ui.components.SearchBar;
import com.stt.dash.ui.utils.TemplateUtil;
import com.stt.dash.ui.views.HasNotifications;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.crud.Crud;
import com.vaadin.flow.component.crud.CrudEditor;
import com.vaadin.flow.component.crud.CrudI18n;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;
import java.util.function.Consumer;

public abstract class
AbstractBakeryCrudView<E extends AbstractEntitySequence> extends Crud<E>
        implements HasUrlParameter<Long>, HasNotifications {

    //    private static final String DISCARD_MESSAGE = "There are unsaved modifications to the %s. Discard changes?";
//private static final String DELETE_MESSAGE = "Are you sure you want to delete the selected %s? This action cannot be undone.";
    private static final String DISCARD_MESSAGE = "Tiene modificaciones sin guardar en %s. ¿Descartar cambios?";
    private static final String DELETE_MESSAGE = "¿Seguro desea borrar a %s? Esta opción no se puede deshacer.";
    /**/
    private static String headerDialog = "Programación SMS";
    private static String textDialog = "¿Desea confirmar su programación?";
    private static String confirmTextDialog = "Confirmar";
    private static String cancelTextDialog = "Volver";
    private final Grid<E> grid;
    private OnUIForm onUiForm;
    private E oldEntity;

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

    protected void afterSaving(long idBeforeSave, E entity, E oldEntity) {
        if (idBeforeSave != 0) {
            System.out.println("----------------- Modificando: " + idBeforeSave);
            System.out.println("----------------- oldentity: " + oldEntity);
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
        String entityName = StringUtils.lowerCase(EntityUtil.getName(beanType));
        crudI18n.setNewItem("Agregar " + entityName);
        crudI18n.setEditItem("Editar " + entityName);
        crudI18n.setEditLabel("Editar " + entityName);
        crudI18n.getConfirm().getCancel().setContent(String.format(DISCARD_MESSAGE, entityName));
        crudI18n.getConfirm().getDelete().setContent(String.format(DELETE_MESSAGE, entityName));
        crudI18n.getConfirm().getCancel().setTitle("Descartar cambios");
        crudI18n.getConfirm().getCancel().getButton().setDismiss("No, continuar editando");
        crudI18n.getConfirm().getCancel().getButton().setConfirm("Sí, descartar");
        crudI18n.setDeleteItem("Borrar");
        crudI18n.setCancel("Cancelar");
        crudI18n.setSaveItem("Guardar");
        setI18n(crudI18n);
        CrudEntityDataProvider<E> dataProvider = new CrudEntityDataProvider<>(service, currentUser);
        grid.setDataProvider(dataProvider);
        setupGrid(grid);
        Crud.addEditColumn(grid);

        if (editor.getView() instanceof OnUIForm) {
            onUiForm = (OnUIForm) editor.getView();
            entityPresenter = new CrudEntityPresenter<>(service, currentUser, this, onUiForm);
        } else {
            entityPresenter = new CrudEntityPresenter<>(service, currentUser, this);
        }
        SearchBar searchBar = new SearchBar();
        searchBar.setActionText("Crear " + entityName);
        searchBar.setPlaceHolder(String.format("Buscar %s", entityName));
        searchBar.addFilterChangeListener(e -> dataProvider.setFilter(searchBar.getFilter()));
        searchBar.getActionButton().getElement().setAttribute("new-button", true);

        setToolbar(searchBar);
         setupCrudEventListeners(entityPresenter);
    }

    private void setupCrudEventListeners(CrudEntityPresenter<E> entityPresenter) {
        Consumer<E> onSuccess = entity -> navigateToEntity(null);
        Consumer<E> onSuccessSaved = entity -> {
            afterSaving(idBeforeSave, entity);
            afterSaving(idBeforeSave, entity, oldEntity);
            navigateToEntity(null);
        };
        Consumer<E> onFail = entity -> {
            throw new RuntimeException("The operation could not be performed.");
        };
        addNewListener(e -> {
            if (Optional.ofNullable(onUiForm).isPresent()) {
                onUiForm.onUI();
            }
        });
        addEditListener(e ->
                entityPresenter.loadEntity(e.getItem().getId(),
                        entity -> {
                            navigateToEntity(entity.getId().toString());
                            setOldEntity(entity);
                        }));

        addCancelListener(e -> navigateToEntity(null));
        addSaveListener(e -> {
            idBeforeSave = e.getItem().getId() == null ? 0 : e.getItem().getId();
            if (!beforeSaving(idBeforeSave, e.getItem())) {
                throw new RuntimeException("Este es un error forzado....");
            }
            entityPresenter.save(e.getItem(), onSuccessSaved, onFail);

        });
        addDeleteListener(e ->
                entityPresenter.delete(e.getItem(), onSuccess, onFail));
    }

    protected void navigateToEntity(String id) {
        getUI().ifPresent(ui -> ui.navigate(TemplateUtil.generateLocation(getBasePage(), id)));
    }

    private void setOldEntity(E entity) {
        oldEntity = entity;
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter Long id) {
        if (id != null) {
            E item = getEditor().getItem();
            if (item != null && id.equals(item.getId())) {
                return;
            }
            entityPresenter.loadEntity(id, entity -> {
                edit(entity, EditMode.EXISTING_ITEM);
                setOldEntity(entity);
            });
        } else {
            setOpened(false);
        }
    }
}
