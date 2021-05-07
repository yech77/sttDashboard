package com.stt.dash.backend.repositories;

import com.stt.dash.backend.data.entity.OUser;
import com.stt.dash.backend.data.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OUserRepository extends JpaRepository<OUser, Long> {

	OUser findByUserEmail(String userEmail);

	OUser findByUserName(String userName);

	Page<OUser> findBy(Pageable pageable);

	List<OUser> findByUserParent(OUser userParent);

	@Query("select u from OUser u " +
			"WHERE lower(u.userName) like lower(concat('%', :filterText, '%'))")
	List<OUser> searchAll(String filterText);

	List<OUser> findByUserEmailIn(List<String> userEmails);

	List<OUser> findAllByUserParentNotNull();


	Page<OUser> findByUserEmailLikeIgnoreCaseOrUserNameLikeIgnoreCaseOrUserLastnameLikeIgnoreCase(
			String emailLike, String firstNameLike, String lastNameLike, Pageable pageable);

	long countByUserEmailLikeIgnoreCaseOrUserNameLikeIgnoreCaseOrUserLastnameLikeIgnoreCase(
			String emailLike, String firstNameLike, String lastNameLike);
}
