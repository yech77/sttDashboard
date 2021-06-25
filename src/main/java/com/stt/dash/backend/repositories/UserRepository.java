package com.stt.dash.backend.repositories;

import com.stt.dash.backend.data.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmailIgnoreCase(String email);

    Page<User> findBy(Pageable pageable);

    Page<User> findAllByUserParentIsNotNull(Pageable pageable);

    Page<User> findByEmailLikeIgnoreCaseOrFirstNameLikeIgnoreCaseOrLastNameLikeIgnoreCaseOrRoleLikeIgnoreCase(
            String emailLike, String firstNameLike, String lastNameLike, String roleLike, Pageable pageable);

    long countByEmailLikeIgnoreCaseOrFirstNameLikeIgnoreCaseOrLastNameLikeIgnoreCaseOrRoleLikeIgnoreCase(
            String emailLike, String firstNameLike, String lastNameLike, String roleLike);

//    @Query(value = "WITH RECURSIVE subordinates AS ( SELECT * " +
//            "FROM user_info	u WHERE u.id = :keyid " +
//            "UNION " +
//            "SELECT e.* FROM user_info e " +
//            "INNER JOIN subordinates s ON s.id = e.user_parent_id " +
//            ") SELECT * FROM subordinates where id !=:keyid", nativeQuery = true)
//            Page<User> findDescent(Long keyid, Pageable pageable);
}
