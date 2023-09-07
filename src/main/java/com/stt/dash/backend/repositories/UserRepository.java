package com.stt.dash.backend.repositories;

import com.stt.dash.backend.data.entity.Client;
import com.stt.dash.backend.data.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmailIgnoreCase(String email);

    Page<User> findBy(Pageable pageable);

    Page<User> findAllByUserParentIsNotNullAndEmailIsNot(String email, Pageable pageable);

    @Query("select u from UserInfo u " +
            "where u.userParent is not null and u.email <> ?1 and (u.firstName like concat(?2, '%') or u.lastName like concat(?3, '%'))")
    Page<User> findAllByUserParentIsNotNullAndEmailIsNotAndFirstNameIsStartingWithOrLastNameIsStartingWith(String email,
                                                                                                           String firstName,
                                                                                                           String lastName,
                                                                                                           Pageable pageable);

    @Query("select count(u) from UserInfo u " +
            "where u.userParent is not null and u.email <> ?1 and (u.firstName like concat(?2, '%') or u.lastName like concat(?3, '%'))")
    Long countAllByUserParentIsNotNullAndEmailIsNotAndFirstNameIsStartingWithOrLastNameIsStartingWith(String email,
                                                                                                      String firstName,
                                                                                                      String lastName);

    Long countAllByUserParentIsNotNullAndEmailIsNot(String email);

    Page<User> findByClientsInAndUserTypeOrdNotAndIdIsNot(Collection<Client> clients, User.OUSER_TYPE_ORDINAL userTypeOrd, Long id, Pageable pageable);

    @Query("select u from UserInfo u left join u.clients clients " +
            "where clients in ?1 and u.userTypeOrd <> ?2 and u.id <> ?3 and (u.firstName like concat(?4, '%') or u.lastName like concat(?5, '%'))")
    Page<User> findByClientsInAndUserTypeOrdNotAndIdIsNotAndFirstNameIsStartingWithOrLastNameIsStartingWith(Collection<Client> clients, User.OUSER_TYPE_ORDINAL userTypeOrd, Long id, String firstName, String lastName, Pageable pageable);

    Long countAllByClientsInAndUserTypeOrdNotAndIdIsNot(Collection<Client> clients, User.OUSER_TYPE_ORDINAL userTypeOrd, Long id);


    Page<User> findByEmailLikeIgnoreCaseOrFirstNameLikeIgnoreCaseOrLastNameLikeIgnoreCaseOrRoleLikeIgnoreCase(
            String emailLike, String firstNameLike, String lastNameLike, String roleLike, Pageable pageable);

    long countByEmailLikeIgnoreCaseOrFirstNameLikeIgnoreCaseOrLastNameLikeIgnoreCaseOrRoleLikeIgnoreCase(
            String emailLike, String firstNameLike, String lastNameLike, String roleLike);

    Page<User> findByUserTypeOrdAndClients(User.OUSER_TYPE_ORDINAL userTypeOrd, Client clients, Pageable pageable);


//    @Query(value = "WITH RECURSIVE subordinates AS ( SELECT * " +
//            "FROM user_info	u WHERE u.id = :keyid " +
//            "UNION " +
//            "SELECT e.* FROM user_info e " +
//            "INNER JOIN subordinates s ON s.id = e.user_parent_id " +
//            ") SELECT * FROM subordinates where id !=:keyid", nativeQuery = true)
//            Page<User> findDescent(Long keyid, Pageable pageable);
}
