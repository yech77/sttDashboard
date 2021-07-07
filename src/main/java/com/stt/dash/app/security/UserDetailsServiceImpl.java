package com.stt.dash.app.security;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.stt.dash.backend.data.entity.User;
import com.stt.dash.backend.repositories.UserRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implements the {@link UserDetailsService}.
 * 
 * This implementation searches for {@link User} entities by the e-mail address
 * supplied in the login screen.
 */
@Service
@Primary
public class UserDetailsServiceImpl implements UserDetailsService {

	private final UserRepository userRepository;

	@Autowired
	public UserDetailsServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	/**
	 *
	 * Recovers the {@link User} from the database using the e-mail address supplied
	 * in the login screen. If the user is found, returns a
	 * {@link org.springframework.security.core.userdetails.User}.
	 *
	 * @param username User's e-mail address
	 * 
	 */
	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByEmailIgnoreCase(username);
		if (null == user) {
			throw new UsernameNotFoundException("No user present with username: " + username);
		} else {
			/**/
			Set<GrantedAuthority> grantedAuthoritySet = new HashSet<>();
			user.getRoles().forEach(role -> {
				role.getAuthorities().forEach(authority -> {
					System.out.println("PUEDE VER ********** " + authority.getAuthName());
					grantedAuthoritySet.add(new SimpleGrantedAuthority(authority.getAuthName()));
				});
			});
//			List<String> l = new ArrayList<>(s);
//			String[] a = new String[l.size()];
//			a = l.toArray(a);
//			UserDetails thing = org.springframework.security.core.userdetails.User.withUsername(user.getUserEmail())
//					.password("{noop}" + user.getUserPassword())
//					//                    .roles("user"/*user.getRoles() */)
//					.authorities(a)
////                    .accountLocked(user.getUserStatus()==OUser.OUSER_STATUS.DESACTIVADO)
//					.build();
//			details[count] = thing;

			return new
					org.springframework.security.core.userdetails.User(
							user.getEmail(),
					user.getPasswordHash(),
					user.isActive(),
					true,
					true,
					true,
					grantedAuthoritySet);
		}
	}
}