package com.stt.dash.backend.repositories;

import com.stt.dash.backend.data.entity.Client;
import com.stt.dash.backend.data.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmailIgnoreCase(String email);

    Page<User> findBy(Pageable pageable);

    Page<User> findAllByUserParentIsNotNullAndEmailIsNot(String email, Pageable pageable);

    Long countAllByUserParentIsNotNullAndEmailIsNot(String email);

    Page<User> findByClientsInAndUserTypeOrdNotAndIdIsNot(Collection<Client> clients, User.OUSER_TYPE_ORDINAL userTypeOrd, Long id, Pageable pageable);

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
