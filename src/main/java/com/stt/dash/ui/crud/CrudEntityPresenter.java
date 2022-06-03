package com.stt.dash.ui.crud;

import com.stt.dash.app.HasLogger;
import com.stt.dash.app.security.CurrentUser;
import com.stt.dash.backend.data.entity.AbstractEntitySequence;
import com.stt.dash.backend.service.CrudService;
import com.stt.dash.backend.service.UIFieldDataException;
import com.stt.dash.backend.service.UserFriendlyDataException;
import com.stt.dash.ui.utils.messages.CrudErrorMessage;
import com.stt.dash.ui.views.HasNotifications;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;

import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolationException;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Es el Presenter de los View y de los Forms.
 * Las notificaciones hacia View se realizan a traves de @HasNotificacion
 * Los metodos para form se realizan con @OnUiForm
 *
 * @param <E>
 */
public class CrudEntityPresenter<E extends AbstractEntitySequence> implements HasLogger {

    private final CrudService<E> crudService;

    private final CurrentUser currentUser;

    private final HasNotifications viewForNotificacion;

    private OnUIForm viewForOnUIForm = null;

    public CrudEntityPresenter(CrudService<E> crudService, CurrentUser currentUser, HasNotifications view) {
        this.crudService = crudService;
        this.currentUser = currentUser;
        this.viewForNotificacion = view;
    }

    public CrudEntityPresenter(CrudService<E> crudService, CurrentUser currentUser, HasNotifications view, OnUIForm onUIForm) {
        this.crudService = crudService;
        this.currentUser = currentUser;
        this.viewForNotificacion = view;
        viewForOnUIForm = onUIForm;
    }

    public void delete(E entity, Consumer<E> onSuccess, Consumer<E> onFail) {
        if (executeOperation(() -> crudService.delete(currentUser.getUser(), entity))) {
            onSuccess.accept(entity);
        } else {
            onFail.accept(entity);
        }
    }

    public void save(E entity, Consumer<E> onSuccess, Consumer<E> onFail) {
        if (executeOperation(() -> saveEntity(entity))) {
            onSuccess.accept(entity);
        } else {
            onFail.accept(entity);
        }
    }

    private boolean executeOperation(Runnable operation) {
        try {
            operation.run();
            return true;
        } catch (UIFieldDataException e) {
            // Commit failed because of application-level data constraints
            consumeError();
        } catch (UserFriendlyDataException e) {
            // Commit failed because of application-level data constraints
            consumeError(e, e.getMessage(), true);
        } catch (DataIntegrityViolationException e) {
            // Commit failed because of validation errors
            consumeError(e, CrudErrorMessage.OPERATION_PREVENTED_BY_REFERENCES, true);
        } catch (OptimisticLockingFailureException e) {
            consumeError(e, CrudErrorMessage.CONCURRENT_UPDATE, true);
        } catch (EntityNotFoundException e) {
            consumeError(e, CrudErrorMessage.ENTITY_NOT_FOUND, false);
        } catch (ConstraintViolationException e) {
            consumeError(e, CrudErrorMessage.REQUIRED_FIELDS_MISSING, false);
        }
        return false;
    }

    private void consumeError() {
        if (!Objects.isNull(viewForNotificacion)) {
            viewForOnUIForm.onFieldUI();
        }
    }

    private void consumeError(Exception e, String message, boolean isPersistent) {
        getLogger().debug(message, e);
        viewForNotificacion.showNotification(message, isPersistent);
    }

    private void saveEntity(E entity) {
        /* Lama al servicio .save especifico */
        crudService.save(currentUser.getUser(), entity);
    }

    public boolean loadEntity(Long id, Consumer<E> onSuccess) {
        return executeOperation(() -> onSuccess.accept(crudService.load(id)));
    }
}
