package com.stt.dash.backend.repositories;

import com.stt.dash.backend.data.entity.Agenda;
import com.stt.dash.backend.data.entity.OUser;
import com.stt.dash.backend.data.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AgendaRepository extends JpaRepository<Agenda, Long> {

    @Query("select a from Agenda a "
            + "WHERE a.creator IN (:users) ")
    public List<Agenda> getAllAgendasInFamily(List<User> users);

    public List<Agenda> findByName(String name);

    @Query("select a from Agenda a "
            + "WHERE a.creator IN (:users) "
            + "AND a.status = :status")
    public List<Agenda> getAllValidAgendasInFamily(List<User> users, Agenda.Status status);
    /**/
    Agenda findByCreatorEmailIgnoreCase(String email);

    Page<Agenda> findBy(Pageable pageable);
//    @Query("select f from Agenda f "
//            + "WHERE lower(f.creator.email) like lower(concat('%', :filterText, '%')) "
//            + "OR  lower(f.description) like lower(concat('%', :filterText, '%')) "
//            + "OR  f.status = :filterText "
//            + "ORDER BY f.dateCreated ")
    Page<Agenda> findByCreator_EmailLikeIgnoreCaseOrNameLikeIgnoreCaseOrDescriptionLikeIgnoreCase(
            String emailLike, String descriptionLike, String statusLike, Pageable pageable);

    long countByCreator_EmailLikeIgnoreCaseOrNameLikeIgnoreCaseOrDescriptionLikeIgnoreCase(
            String emailLike, String descriptionLike, String statusLike);

}
