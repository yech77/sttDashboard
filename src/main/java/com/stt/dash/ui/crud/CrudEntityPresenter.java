package com.stt.dash.ui.crud;

import com.stt.dash.app.HasLogger;
import com.stt.dash.app.security.CurrentUser;
import com.stt.dash.backend.data.entity.AbstractEntitySequence;
import com.stt.dash.backend.service.CrudService;
import com.stt.dash.backend.service.UserFriendlyDataException;
import com.stt.dash.ui.utils.messages.CrudErrorMessage;
import com.stt.dash.ui.views.HasNotifications;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;

import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolationException;
import java.util.function.Consumer;

public class CrudEntityPresenter<E extends AbstractEntitySequence>	implements HasLogger {

	private final CrudService<E> crudService;

	private final CurrentUser currentUser;

	private final HasNotifications view;

	public CrudEntityPresenter(CrudService<E> crudService, CurrentUser currentUser, HasNotifications view) {
		this.crudService = crudService;
		this.currentUser = currentUser;
		this.view = view;
	}

	public void delete(E entity, Consumer<E> onSuccess, Consumer<E> onFail) {
		System.out.println("CrudEntityPresenter: delete ******************** ");
		if (executeOperation(() -> crudService.delete(currentUser.getUser(), entity))) {
			onSuccess.accept(entity);
		} else {
			onFail.accept(entity);
		}
	}

	public void save(E entity, Consumer<E> onSuccess, Consumer<E> onFail) {
		System.out.println("CrudEntityPresenter: save ******************** ");
		if (executeOperation(() -> saveEntity(entity))) {
			onSuccess.accept(entity);
		} else {
			onFail.accept(entity);
		}
	}

	private boolean executeOperation(Runnable operation) {
		try {
			System.out.println("CrudEntityPresenter: executeOperation ******************** ");
			operation.run();
			return true;
		} catch (UserFriendlyDataException e) {
			// Commit failed because of application-level data constraints
			consumeError(e, e.getMessage(), true);
		} catch (DataIntegrityViolationException e) {
			// Commit failed because of validation errors
			consumeError(
					e, CrudErrorMessage.OPERATION_PREVENTED_BY_REFERENCES, true);
		} catch (OptimisticLockingFailureException e) {
			consumeError(e, CrudErrorMessage.CONCURRENT_UPDATE, true);
		} catch (EntityNotFoundException e) {
			consumeError(e, CrudErrorMessage.ENTITY_NOT_FOUND, false);
		} catch (ConstraintViolationException e) {
			consumeError(e, CrudErrorMessage.REQUIRED_FIELDS_MISSING, false);
		}
		return false;
	}

	private void consumeError(Exception e, String message, boolean isPersistent) {
		getLogger().debug(message, e);
		view.showNotification(message, isPersistent);
	}

	private void saveEntity(E entity) {
		System.out.println("CrudEntityPresenter: saveEntity ******************** ");
		crudService.save(currentUser.getUser(), entity);
	}

	public boolean loadEntity(Long id, Consumer<E> onSuccess) {
		System.out.println("CrudEntityPresenter: loadEntity ******************** ");
		return executeOperation(() -> onSuccess.accept(crudService.load(id)));
	}
}
