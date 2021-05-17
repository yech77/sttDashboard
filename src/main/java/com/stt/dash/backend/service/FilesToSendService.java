package com.stt.dash.backend.service;

import com.stt.dash.backend.data.entity.FIlesToSend;
import com.stt.dash.backend.data.entity.User;
import com.stt.dash.backend.repositories.FilesToSendRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public class FilesToSendService implements FilterableCrudService<FIlesToSend>{
    private FilesToSendRepository filesToSendRepository;

    public FilesToSendService(FilesToSendRepository filesToSendRepository) {
        this.filesToSendRepository = filesToSendRepository;
    }

    @Override
    public FIlesToSend save(User currentUser, FIlesToSend entity) {
        try {
            return FilterableCrudService.super.save(currentUser, entity);
        } catch (DataIntegrityViolationException e) {
            throw new UserFriendlyDataException(
                    "There is already a Masivo Agendado with that name. Please select a unique name for the Masivo a enviar.");
        }
    }

    @Override
    public JpaRepository<FIlesToSend, Long> getRepository() {
        return filesToSendRepository;
    }

    @Override
    public FIlesToSend createNew(User currentUser) {
        return new FIlesToSend();
    }

    @Override
    public Page<FIlesToSend> findAnyMatching(Optional<String> filter, Pageable pageable) {
        return null;
    }

    @Override
    public long countAnyMatching(Optional<String> filter) {
        return 0;
    }
}
