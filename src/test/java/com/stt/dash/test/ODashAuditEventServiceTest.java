package com.stt.dash.test;

import com.stt.dash.app.DataGenerator;
import com.stt.dash.app.security.SecurityConfiguration;
import com.stt.dash.app.security.UserDetailsServiceImpl;
import com.stt.dash.backend.data.entity.*;
import com.stt.dash.backend.repositories.OAuthorityRepository;
import com.stt.dash.backend.repositories.ODashAuditEventRepository;
import com.stt.dash.backend.repositories.ORoleRepository;
import com.stt.dash.backend.repositories.UserRepository;
import com.stt.dash.backend.service.ODashAuditEventService;
import com.stt.dash.backend.service.TempSmsService;
import com.stt.dash.backend.service.UserService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.stt.dash.backend.data.entity.MyAuditEventComponent.EVENT_AUTHENTICATION_SUCCESS;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
//@WebMvcTest(EntryOverviewController.class)
@Import({UserDetailsServiceImpl.class, SecurityConfiguration.class, TempSmsService.class})
@Disabled
public class ODashAuditEventServiceTest {
    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    ODashAuditEventRepository eventRepository;
    MyAuditEventComponent myAuditEventComponent;
    UserService user_serv;
    @Autowired
    ODashAuditEventRepository oDashAuditEventRepository;
    ODashAuditEventService eventService;
    @Autowired
    UserRepository user_repo;
    //    @Autowired
//    PasswordEncoder passwordEncoder;
    @Autowired
    OAuthorityRepository oauth_repo;
    @Autowired
    ORoleRepository orole_repo;
//    @Autowired
//    ClientRepository client_repo;

//        @BeforeAll
//    static void beforeAllTests() {
//        loaddata();
//    }


    public void setUp() throws Exception {
    }

    public void tearDown() throws Exception {
    }

    public void testSave() {
    }

    @Test
    @DisplayName("Eventos de Usuario")
    public void testAddUserEvent() {
        eventService = new ODashAuditEventService(oDashAuditEventRepository);
        myAuditEventComponent = new MyAuditEventComponent(eventService);
        user_serv = new UserService(user_repo, myAuditEventComponent);
        loaddata();
        addUser("admin@soltextech.com");
        UserDetails userDetails = login(myAuditEventComponent, "admin@soltextech.com");
        User currentUser = findUser(userDetails.getUsername());
        /*CREAR USUARIO */
        addUser("yechev@soltextech.com");
        /* ACTUALIZAR */
        addUser("yechev@soltextech.com");
        /* BORRAR EL USUARIO CREADO */
        deleteUser(currentUser, findUser("yechev@soltextech.com"));
        /* BUSCAR TODOS LOS EVENTOS */
        List<ODashAuditEvent> l = eventRepository.findAll();
        /* SOLO EVENTOS CREATE_USER */
        List<ODashAuditEvent> createList = l.stream()
                .filter(o -> o.getEventType() == ODashAuditEvent.OEVENT_TYPE.CREATE_USER)
                .collect(Collectors.toList());
        /* SOLO EVENTOS DELETE_USER */
        List<ODashAuditEvent> deleteList = l.stream()
                .filter(o -> o.getEventType() == ODashAuditEvent.OEVENT_TYPE.DELETE_USER)
                .collect(Collectors.toList());
        /* SOLO EVENTOS UPDATE_USER */
        List<ODashAuditEvent> updateList = l.stream()
                .filter(o -> o.getEventType() == ODashAuditEvent.OEVENT_TYPE.UPDATE_USER)
                .collect(Collectors.toList());

        assertAll("lista",
                () -> {
                    assertTrue(l != null, "Lista de eventos vacia");
                    assertAll("listalista",
                            () -> {
                                assertEquals(5, l.size(), "Eventos generados");
                                assertEquals(2, createList.size(), "Usuarios creados");
                                assertEquals(1, deleteList.size(), "Usuarios borrados");
                                assertEquals(1, updateList.size(), "Usuarios Actualizados");
                            });
                });
    }

    public void testTestFindAll() {
    }

    public void testTestFindAll1() {
    }

    public void testTestFindAll2() {
    }

    public void testTestFindAll3() {
    }

    public void testGetRepository() {
    }

    public void testCreateNew() {
    }

    public void testFindAnyMatching() {
    }

    public void testCountAnyMatching() {
    }

    public void testFind() {
    }

    private UserDetails login(MyAuditEventComponent myAuditEventComponent, String email) {
        UserDetailsServiceImpl ud = applicationContext.getBean(UserDetailsServiceImpl.class);
        /* LOGIN */
        UserDetails userDetails = ud.loadUserByUsername(email);
        Authentication authToken = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
        myAuditEventComponent.add(ODashAuditEvent.OEVENT_TYPE.LOGIN, EVENT_AUTHENTICATION_SUCCESS, email);
        return userDetails;
    }

    private void loadAuthority() {

        OAuthority oauth = new OAuthority();
        /* UI */
        oauth = new OAuthority();
        oauth.setAuthName(DataGenerator.AUTH.UI_AUDIT.name());
        oauth.setAuthDesc("Permite ver la Pantalla AUDITEVEN");
        oauth_repo.saveAndFlush(oauth);

        oauth = new OAuthority();
        oauth.setAuthName(DataGenerator.AUTH.UI_ROL.name());
        oauth.setAuthDesc("Permite ver la Pantalla ROL");
        oauth_repo.save(oauth);

        oauth = new OAuthority();
        oauth.setAuthName(DataGenerator.AUTH.UI_USER.name());
        oauth.setAuthDesc("Permite ver la Pantalla USER");
        oauth_repo.save(oauth);
        oauth = new OAuthority();
        oauth.setAuthName(DataGenerator.AUTH.UI_EVOLUTION_CARRIER.name());
        oauth.setAuthDesc("Permite ver la Pantalla AUDITEVEN");
        oauth_repo.save(oauth);

        oauth = new OAuthority();
        oauth.setAuthName(DataGenerator.AUTH.UI_EVOLUTION_CLIENT.name());
        oauth.setAuthDesc("Permite ver la Pantalla ROL");
        oauth_repo.save(oauth);

        oauth = new OAuthority();
        oauth.setAuthName(DataGenerator.AUTH.UI_EVOLUTION_SYSTEMID.name());
        oauth.setAuthDesc("Permite ver la Pantalla USER");
        oauth_repo.save(oauth);
        oauth = new OAuthority();
        oauth.setAuthName(DataGenerator.AUTH.UI_SEARCH_SMS.name());
        oauth.setAuthDesc("Permite ver la Pantalla BUSCAR SMS");
        oauth_repo.save(oauth);
        oauth = new OAuthority();
        oauth.setAuthName(DataGenerator.AUTH.UI_TRAFFIC_SMS.name());
        oauth.setAuthDesc("Permite ver la Pantalla TRAFICO");
        oauth_repo.save(oauth);
        oauth = new OAuthority();
        oauth.setAuthName(DataGenerator.AUTH.UI_AGENDA_SMS.name());
        oauth.setAuthDesc("Permite ver la Pantalla AGENDA");
        oauth_repo.save(oauth);
        oauth = new OAuthority();
        oauth.setAuthName(DataGenerator.AUTH.UI_PROGRAM_SMS.name());
        oauth.setAuthDesc("Permite ver la Pantalla PROGRAMAR SMS");
        oauth_repo.save(oauth);
    }

    private void loadRole() {

        ORole orole = new ORole();
        Set<OAuthority> o = new HashSet<>();
        /**/
        orole = new ORole();
        orole.setRolName(DataGenerator.ROL.AGENDAR_SMS.name());
        o = new HashSet<>();
        o.add(oauth_repo.findByAuthName(DataGenerator.AUTH.UI_AGENDA_SMS.name()).get(0));
        orole.setAuthorities(o);
        orole_repo.saveAndFlush(orole);
        /**/
        orole = new ORole();
        orole.setRolName(DataGenerator.ROL.AUDITORIA.name());
        o = new HashSet<>();
        o.add(oauth_repo.findByAuthName(DataGenerator.AUTH.UI_AUDIT.name()).get(0));
        orole.setAuthorities(o);
        orole_repo.save(orole);
        /**/
        orole = new ORole();
        orole.setRolName(DataGenerator.ROL.BUSQUEDA_SMS.name());
        o = new HashSet<>();
        o.add(oauth_repo.findByAuthName(DataGenerator.AUTH.UI_SEARCH_SMS.name()).get(0));
        orole.setAuthorities(o);
        orole_repo.save(orole);
        /**/
        orole = new ORole();
        orole.setRolName(DataGenerator.ROL.EVOLUCION_CLIENTE.name());
        o = new HashSet<>();
        o.add(oauth_repo.findByAuthName(DataGenerator.AUTH.UI_EVOLUTION_CLIENT.name()).get(0));
        orole.setAuthorities(o);
        orole_repo.save(orole);
        /**/
        orole = new ORole();
        orole.setRolName(DataGenerator.ROL.EVOLUCION_OPERADORA.name());
        o = new HashSet<>();
        o.add(oauth_repo.findByAuthName(DataGenerator.AUTH.UI_EVOLUTION_CARRIER.name()).get(0));
        orole.setAuthorities(o);
        orole_repo.save(orole);
        /**/
        orole = new ORole();
        orole.setRolName(DataGenerator.ROL.EVOLUCION_PASAPORTES.name());
        o = new HashSet<>();
        o.add(oauth_repo.findByAuthName(DataGenerator.AUTH.UI_EVOLUTION_SYSTEMID.name()).get(0));
        orole.setAuthorities(o);
        orole_repo.save(orole);
        /**/
        orole = new ORole();
        orole.setRolName(DataGenerator.ROL.PROGRAMAR_SMS.name());
        o = new HashSet<>();
        o.add(oauth_repo.findByAuthName(DataGenerator.AUTH.UI_PROGRAM_SMS.name()).get(0));
        orole.setAuthorities(o);
        orole_repo.save(orole);
        /**/
        orole = new ORole();
        orole.setRolName(DataGenerator.ROL.ROLES.name());
        o = new HashSet<>();
        o.add(oauth_repo.findByAuthName(DataGenerator.AUTH.UI_ROL.name()).get(0));
        orole.setAuthorities(o);
        orole_repo.save(orole);
        /**/
        orole = new ORole();
        orole.setRolName(DataGenerator.ROL.TRAFICO_SMS.name());
        o = new HashSet<>();
        o.add(oauth_repo.findByAuthName(DataGenerator.AUTH.UI_TRAFFIC_SMS.name()).get(0));
        orole.setAuthorities(o);
        orole_repo.save(orole);
        /**/
        orole = new ORole();
        orole.setRolName(DataGenerator.ROL.USUARIOS.name());
        o = new HashSet<>();
        o.add(oauth_repo.findByAuthName(DataGenerator.AUTH.UI_USER.name()).get(0));
        orole.setAuthorities(o);
        orole_repo.save(orole);
    }

    private void deleteUser(User currentUSer, User user) {
        user_serv.delete(currentUSer, user);
    }

    private User findUser(String email) {
        return user_repo.findByEmailIgnoreCase(email);
    }

    private User addUser(String email) {
        User ouser = user_repo.findByEmailIgnoreCase(email);
        if (ouser == null) {
            ouser = new User();
        } else {
            System.out.println("** FOUNDED " + ouser.getEmail());
        }
        ouser.setFirstName("Administrador");
        ouser.setLastName("");
        ouser.setEmail(email);
        ouser.setUserType(User.OUSER_TYPE.HAS);
        ouser.setUserTypeOrd(User.OUSER_TYPE_ORDINAL.COMERCIAL);
//        ouser.setPasswordHash(passwordEncoder.encode("admin"));
        ouser.setPasswordHash(("admin"));
        ouser.setLocked(false);
        return user_serv.save(ouser, ouser);
    }

    void loaddata() {

        if (oauth_repo.count() < 1) {
            loadAuthority();
        }
//
        if (orole_repo.count() < 1) {
            loadRole();
        }
//            /**/
//
//            User ouser = user_repo.findByEmailIgnoreCase("admin@soltextech.com");
//            if (ouser == null) {
//                ouser = new User();
//            } else {
//                System.out.println("** FOUNDED " + ouser.getEmail());
//            }
//            ouser.setFirstName("Administrador");
//            ouser.setLastName("");
//            ouser.setEmail("admin@soltextech.com");
//            ouser.setUserType(User.OUSER_TYPE.HAS);
//            ouser.setUserTypeOrd(User.OUSER_TYPE_ORDINAL.COMERCIAL);
//            ouser.setPasswordHash(passwordEncoder.encode("admin"));
//            ouser.setLocked(true);
//            List<Client> c = client_repo.findAll();
//            if (c != null) {
//                ouser.setClients(new HashSet<>(c));
//            }
//
//            /*Roles*/
//            List<ORole> r1 = orole_repo.findAll();
//            if (r1 != null) {
//                r1 = new ArrayList<>();
//            }
//            ouser.setRoles(new HashSet<>(r1));
//            user_repo.saveAndFlush(ouser);
//            /**/
//            ouser = user_repo.findByEmailIgnoreCase("enavas@soltextech.com");
//            if (ouser == null) {
//                ouser = new User();
//            } else {
//                System.out.println("** FOUNDED " + ouser.getEmail());
//            }
//            ouser.setFirstName("Elizabeth");
//            ouser.setLastName("Navas");
//            ouser.setEmail("enavas@soltextech.com");
//            ouser.setUserType(User.OUSER_TYPE.HAS);
//            ouser.setUserTypeOrd(User.OUSER_TYPE_ORDINAL.COMERCIAL);
//            ouser.setPasswordHash(passwordEncoder.encode("enavas"));
//            c = client_repo.findAll();
//            if (c != null) {
//                ouser.setClients(new HashSet<>(c));
//            }
//
//            /*Roles*/
//            Set<ORole> r = new HashSet<>();
//            r.add(orole_repo.findByRolName(DataGenerator.ROL.AGENDAR_SMS.name()).get(0));
//            r.add(orole_repo.findByRolName(DataGenerator.ROL.AUDITORIA.name()).get(0));
//            r.add(orole_repo.findByRolName(DataGenerator.ROL.BUSQUEDA_SMS.name()).get(0));
//            r.add(orole_repo.findByRolName(DataGenerator.ROL.EVOLUCION_CLIENTE.name()).get(0));
//            r.add(orole_repo.findByRolName(DataGenerator.ROL.EVOLUCION_OPERADORA.name()).get(0));
//            r.add(orole_repo.findByRolName(DataGenerator.ROL.EVOLUCION_PASAPORTES.name()).get(0));
//            r.add(orole_repo.findByRolName(DataGenerator.ROL.PROGRAMAR_SMS.name()).get(0));
//            r.add(orole_repo.findByRolName(DataGenerator.ROL.ROLES.name()).get(0));
//            r.add(orole_repo.findByRolName(DataGenerator.ROL.TRAFICO_SMS.name()).get(0));
//            r.add(orole_repo.findByRolName(DataGenerator.ROL.USUARIOS.name()).get(0));
//            ouser.setRoles(r);
//            /* Gleryxa fue creada por enavas*/
//            ouser.setUserParent(user_repo.findByEmailIgnoreCase("admin@soltextech.com"));
//            user_repo.saveAndFlush(ouser);
//            /**/
//
//            ouser = user_repo.findByEmailIgnoreCase("gbandres@soltextech.com");
//            if (ouser == null) {
//                ouser = new User();
//            } else {
//                System.out.println("** FOUNDED " + ouser.getEmail());
//            }
//            ouser.setFirstName("Gleryxa");
//            ouser.setLastName("Bandres");
//            ouser.setEmail("gbandres@soltextech.com");
//            ouser.setUserType(User.OUSER_TYPE.HAS);
//            ouser.setUserTypeOrd(User.OUSER_TYPE_ORDINAL.COMERCIAL);
//            ouser.setPasswordHash(passwordEncoder.encode("gbandres"));
//
//            if (c != null) {
//                ouser.setClients(new HashSet<>(c));
//            }
//            /*Roles*/
//            r = new HashSet<>();
//            r.add(orole_repo.findByRolName(DataGenerator.ROL.AGENDAR_SMS.name()).get(0));
//            r.add(orole_repo.findByRolName(DataGenerator.ROL.AUDITORIA.name()).get(0));
//            r.add(orole_repo.findByRolName(DataGenerator.ROL.BUSQUEDA_SMS.name()).get(0));
//            r.add(orole_repo.findByRolName(DataGenerator.ROL.EVOLUCION_CLIENTE.name()).get(0));
//            r.add(orole_repo.findByRolName(DataGenerator.ROL.EVOLUCION_OPERADORA.name()).get(0));
//            r.add(orole_repo.findByRolName(DataGenerator.ROL.EVOLUCION_PASAPORTES.name()).get(0));
//            r.add(orole_repo.findByRolName(DataGenerator.ROL.PROGRAMAR_SMS.name()).get(0));
//            r.add(orole_repo.findByRolName(DataGenerator.ROL.ROLES.name()).get(0));
//            r.add(orole_repo.findByRolName(DataGenerator.ROL.TRAFICO_SMS.name()).get(0));
//            r.add(orole_repo.findByRolName(DataGenerator.ROL.USUARIOS.name()).get(0));
//            ouser.setRoles(r);
//
//            /* Gleryxa fue creada por enavas*/
//            ouser.setUserParent(user_repo.findByEmailIgnoreCase("enavas@soltextech.com"));
//            user_repo.saveAndFlush(ouser);
//            /**
//             * ***************
//             */
//
//            ouser = user_repo.findByEmailIgnoreCase("lsuarez@soltextech.com");
//            if (ouser == null) {
//                ouser = new User();
//            } else {
//                System.out.println("** FOUNDED " + ouser.getEmail());
//            }
//
//            ouser.setFirstName("Luis");
//            ouser.setLastName("Suarez");
//            ouser.setEmail("lsuarez@soltextech.com");
//            ouser.setUserType(User.OUSER_TYPE.HAS);
//            ouser.setUserTypeOrd(User.OUSER_TYPE_ORDINAL.COMERCIAL);
//            ouser.setPasswordHash(passwordEncoder.encode("lsuarez"));
//
//            if (c != null) {
//                ouser.setClients(new HashSet<>(c));
//            }
//            /*Roles*/
//            r = new HashSet<>();
//            r.add(orole_repo.findByRolName(DataGenerator.ROL.AGENDAR_SMS.name()).get(0));
//            r.add(orole_repo.findByRolName(DataGenerator.ROL.AUDITORIA.name()).get(0));
//            r.add(orole_repo.findByRolName(DataGenerator.ROL.BUSQUEDA_SMS.name()).get(0));
//            r.add(orole_repo.findByRolName(DataGenerator.ROL.EVOLUCION_CLIENTE.name()).get(0));
//            r.add(orole_repo.findByRolName(DataGenerator.ROL.EVOLUCION_OPERADORA.name()).get(0));
//            r.add(orole_repo.findByRolName(DataGenerator.ROL.EVOLUCION_PASAPORTES.name()).get(0));
//            r.add(orole_repo.findByRolName(DataGenerator.ROL.PROGRAMAR_SMS.name()).get(0));
//            r.add(orole_repo.findByRolName(DataGenerator.ROL.ROLES.name()).get(0));
//            r.add(orole_repo.findByRolName(DataGenerator.ROL.TRAFICO_SMS.name()).get(0));
//            r.add(orole_repo.findByRolName(DataGenerator.ROL.USUARIOS.name()).get(0));
//            ouser.setRoles(r);
//
//            /* Luis fue creada por enavas*/
//            ouser.setUserParent(user_repo.findByEmailIgnoreCase("enavas@soltextech.com"));
//            user_repo.saveAndFlush(ouser);
//            /**
//             *
//             * /**
//             * ***************
//             */
//            ouser = user_repo.findByEmailIgnoreCase("dsolorzano@soltextech.com");
//            if (ouser == null) {
//                ouser = new User();
//            } else {
//                System.out.println("** FOUNDED " + ouser.getEmail());
//            }
//            ouser.setFirstName("Denny");
//            ouser.setLastName("Solorzano");
//            ouser.setEmail("dsolorzano@soltextech.com");
//            ouser.setUserType(User.OUSER_TYPE.HAS);
//            ouser.setUserTypeOrd(User.OUSER_TYPE_ORDINAL.COMERCIAL);
//            ouser.setPasswordHash(passwordEncoder.encode("dsolorzano"));
//
//            if (c != null) {
//                ouser.setClients(new HashSet<>(c));
//            }
//
//            /*Roles*/
//            r = new HashSet<>();
//            r.add(orole_repo.findByRolName(DataGenerator.ROL.AGENDAR_SMS.name()).get(0));
//            r.add(orole_repo.findByRolName(DataGenerator.ROL.AUDITORIA.name()).get(0));
//            r.add(orole_repo.findByRolName(DataGenerator.ROL.BUSQUEDA_SMS.name()).get(0));
//            r.add(orole_repo.findByRolName(DataGenerator.ROL.EVOLUCION_CLIENTE.name()).get(0));
//            r.add(orole_repo.findByRolName(DataGenerator.ROL.EVOLUCION_OPERADORA.name()).get(0));
//            r.add(orole_repo.findByRolName(DataGenerator.ROL.EVOLUCION_PASAPORTES.name()).get(0));
//            r.add(orole_repo.findByRolName(DataGenerator.ROL.PROGRAMAR_SMS.name()).get(0));
//            r.add(orole_repo.findByRolName(DataGenerator.ROL.ROLES.name()).get(0));
//            r.add(orole_repo.findByRolName(DataGenerator.ROL.TRAFICO_SMS.name()).get(0));
//            r.add(orole_repo.findByRolName(DataGenerator.ROL.USUARIOS.name()).get(0));
//            ouser.setRoles(r);
//
//            /* Denny fue creada por enavas*/
//            ouser.setUserParent(user_repo.findByEmailIgnoreCase("enavas@soltextech.com"));
//            user_repo.saveAndFlush(ouser);
//        }
    }

    public enum AUTH {
        CREATE_USER_IS, CREATE_USER_HAS, CREATE_USER_BY,
        SCREEN_CLIENTS, SCREEN_USER,
        FILE_DOWNLOAD_SMS, FILE_UPLOAD,
        UI_ROL,
        UI_USER,
        UI_AUDIT,
        UI_TRAFFIC_SMS,
        UI_SEARCH_SMS,
        UI_AGENDA_SMS,
        UI_PROGRAM_SMS,
        UI_EVOLUTION_CARRIER,
        UI_EVOLUTION_CLIENT,
        UI_EVOLUTION_SYSTEMID;

        public String[] getAllAuth() {
            String[] s = new String[DataGenerator.AUTH.values().length];
            for (int i = 0; i < DataGenerator.AUTH.values().length; i++) {
                s[i] = DataGenerator.AUTH.values()[i].name();
            }
            return s;
        }

        ;
    }

    public enum ROL {
        AUDITORIA, ROLES, USUARIOS, TRAFICO_SMS, BUSQUEDA_SMS,
        AGENDAR_SMS, PROGRAMAR_SMS, EVOLUCION_CLIENTE, EVOLUCION_OPERADORA,
        EVOLUCION_PASAPORTES
    }
}