package com.stt.dash.app.security;

import com.stt.dash.app.DataGenerator;
import com.stt.dash.backend.event.LogOutTesting;
import com.stt.dash.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import com.stt.dash.backend.data.entity.User;
import com.stt.dash.ui.utils.BakeryConst;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

/**
 * Configures spring security, doing the following:
 * <li>Bypass security checks for static resources,</li>
 * <li>Restrict access to the application, allowing only logged in users,</li>
 * <li>Set up the login form,</li>
 * <li>Configures the {@link UserDetailsServiceImpl}.</li>
 */
@EnableWebSecurity
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    public static final String LOGOUT_URL = "/logout";
    private static final String LOGIN_PROCESSING_URL = "/login";
    private static final String LOGIN_FAILURE_URL = "/login?error";
    private static final String LOGIN_URL = "/login";
    private static final String LOGOUT_SUCCESS_URL = "/" + BakeryConst.PAGE_STOREFRONT;

    @Autowired
    private LogOutTesting logOutTesting;

    @Autowired
    private final UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public SecurityConfiguration(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * The password encoder to use when encrypting passwords.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {

        System.out.println(" * * * * * * * * * * * * * * * SecurityConfiguration.configure: WebSecurity");
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public CurrentUser currentUser(UserRepository userRepository) {
        User user;
        if (!SecurityUtils.isUserLoggedIn()) {
            user = null;
        } else {
            final String username = SecurityUtils.getUsername();
            System.out.println(" * * * * * * * * * * * * * * * SecurityConfiguration.CurrentUser: " + username);
            user =
                    username != null ? userRepository.findByEmailIgnoreCase(username) :
                            null;
        }
        return () -> user;
    }

    /**
     * Registers our UserDetailsService and the password encoder to be used on login attempts.
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        super.configure(auth);
        System.out.println(" * * * * * * * * * * * * * * * SecurityConfiguration.configure: AuthenticationManagerBuilder");
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    /**
     * Require login to access internal pages and configure login form.
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        System.out.println(" * * * * * * * * * * * * * * * SecurityConfiguration.configure: HttpSecurity");
        // Not using Spring CSRF here to be able to use plain HTML for the login page
        http.csrf().disable()

                // Register our CustomRequestCache, that saves unauthorized access attempts, so
                // the user is redirected after login.
                .requestCache().requestCache(new CustomRequestCache())

                // Restrict access to our application.
                .and().authorizeRequests()

                // Allow all flow internal requests.
                .requestMatchers(SecurityUtils::isFrameworkInternalRequest).permitAll()

                // Allow all requests by logged in users.
                .anyRequest().hasAnyAuthority(DataGenerator.AUTH.CREATE_USER_BY.getAllAuth())
//				.anyRequest().hasAnyAuthority(Role.getAllRoles())

                // Configure the login page.
                .and().formLogin().loginPage(LOGIN_URL).permitAll().loginProcessingUrl(LOGIN_PROCESSING_URL)
                .failureUrl(LOGIN_FAILURE_URL)

                // Register the success handler that redirects users to the page they last tried
                // to access
                .successHandler(new SavedRequestAwareAuthenticationSuccessHandler())
                // Configure logout
                .and().logout()
                .logoutSuccessUrl("/")
                .addLogoutHandler(logOutTesting);
    }

    /**
     * Allows access to static resources, bypassing Spring security.
     */
    @Override
    public void configure(WebSecurity web) {
        System.out.println(" * * * * * * * * * * * * * * * SecurityConfiguration.configure: WebSecurity");
        web.ignoring().antMatchers(
                // client-side JS code
                "/VAADIN/**",

                // the standard favicon URI
                "/favicon.ico",

                // the robots exclusion standard
                "/robots.txt",

                // web application manifest
                "/manifest.webmanifest",
                "/sw.js",
                "/offline-page.html",

                // icons and images
                "/icons/**",
                "/images/**",

                // (development mode) H2 debugging console
                "/h2-console/**"
        );
    }
}
