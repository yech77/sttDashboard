package com.stt.dash.backend.repositories;

import com.stt.dash.backend.data.entity.Agenda;
import com.stt.dash.backend.data.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AgendaRepository extends JpaRepository<Agenda, Long> {

    @Query("select a from Agenda a WHERE a.creator IN (:users) ")
    public List<Agenda> getAllAgendasInFamily(List<User> users);

    public List<Agenda> findByName(String name);

    @Override
    Optional<Agenda> findById(Long aLong);

    @Query("select a from Agenda a "
            + "WHERE a.creator IN (:users) "
            + "AND a.status = :status")
    List<Agenda> getAllValidAgendasInFamily(List<User> users, Agenda.Status status);

    Page<Agenda> findAllByStatusOrderByDateCreatedDesc(Agenda.Status status, Pageable pageable);


    @Query("select a from Agenda a where a.creator in :users")
    Page<Agenda> findAllByCreatorIn(@Param("users") List<User> users, Pageable pageable);

    @Query("select count(a) from Agenda a where a.creator in ?1")
    Long countAgendaByCreatorIn(List<User> users);

    @Query("select a from Agenda a " +
            "where a.creator in :users and (a.name like concat(:name, '%') or a.description like concat(:description, '%'))")
    Page<Agenda> findAllByCreatorInAndNameIsStartingWithOrDescriptionIsStartingWith(@Param("users") List<User> users, @Param("name") String name, @Param("description") String description, Pageable pageable);

    Long countAgendaByCreatorInAndNameIsStartingWithOrDescriptionIsStartingWith(@Param("users") List<User> users, @Param("name") String name, @Param("description") String description);

    /**
     * Busqueda de Agendas para usuarios comercial
     *
     * @param name
     * @param description
     * @param pageable
     * @return
     */
    @Query("select a from Agenda a where a.name like concat(:name, '%') or a.description like concat(:description, '%')")
    Page<Agenda> findAllByNameIsStartingWithOrDescriptionIsStartingWith(@Param("name") String name, @Param("description") String description, Pageable pageable);

    /**
     * Conteo de Agendas para usuarios comercial
     *
     * @param name
     * @param description
     * @return
     */
    @Query("select count(a) from Agenda a " +
            "where a.name like concat(:name, '%') or a.description like concat(:description, '%')")
    Long countAllByNameIsStartingWithOrDescriptionIsStartingWith(@Param("name") String name, @Param("description") String description);

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
