package com.stt.dash.ui.views.admin.users.v2;

import com.stt.dash.app.security.CurrentUser;
import com.stt.dash.backend.data.entity.Client;
import com.stt.dash.backend.data.entity.User;
import com.stt.dash.backend.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.*;

class ClassofTest {
    @Mock
    CurrentUser currentUser;
    @Mock
    UserService userService;
    @InjectMocks
    Classof classof;

    User comercial, admin_empresa, empresa, lRodriguez;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Client client = new Client();
        client.setClientCod("POLAR");
        client.setClientName("POLAR INC");
        client.setCuadrante(Client.Cuandrante.EMPRESAS);
        client.setEmail("polar|soltextech.com");
        /**/
        comercial = new User();
        comercial.setActive(true);
        comercial.setClient(client);
        comercial.setCreatedBy("yecheverria@soltextech.com");
        comercial.setEmail("yecheverria@soltextech.com");
        comercial.setFirstName("Yermi");
        comercial.setLastName("Echeverria");
        comercial.setLocked(false);
        comercial.setPasswordHash("1Unica");
        comercial.setUserParent(null);
        comercial.setUserType(User.OUSER_TYPE.HAS);
        comercial.setUserTypeOrd(User.OUSER_TYPE_ORDINAL.COMERCIAL);
        /**/
        admin_empresa = new User();
        admin_empresa.setActive(true);
        admin_empresa.setClient(client);
        admin_empresa.setCreatedBy("yecheverria@soltextech.com");
        admin_empresa.setEmail("sofia.gonzalez@soltextech.com");
        admin_empresa.setFirstName("sofia");
        admin_empresa.setLastName("gonzalez");
        admin_empresa.setLocked(false);
        admin_empresa.setPasswordHash("1Unica");
        admin_empresa.setUserParent(null);
        admin_empresa.setUserType(null);
        admin_empresa.setUserTypeOrd(null);
        /**/
        empresa = new User();
        empresa.setActive(true);
        empresa.setClient(client);
        empresa.setCreatedBy("sofia.gonzalez@soltextech.com");
        empresa.setEmail("jorge.perez@soltextech.com");
        empresa.setFirstName("Jorge");
        empresa.setLastName("Pérez");
        empresa.setLocked(false);
        empresa.setPasswordHash("1Unica");
        empresa.setUserParent(null);
        empresa.setUserType(null);
        empresa.setUserTypeOrd(null);
        /**/
        lRodriguez = new User();
        lRodriguez.setActive(true);
        lRodriguez.setClient(client);
        lRodriguez.setCreatedBy("jorge.perez@soltextech.com");
        lRodriguez.setEmail("lucia.Rodríguez@soltextech.com");
        lRodriguez.setFirstName("Lucía");
        lRodriguez.setLastName("Pérez");
        lRodriguez.setLocked(false);
        lRodriguez.setPasswordHash("1Unica");
        lRodriguez.setUserParent(null);
        lRodriguez.setUserType(null);
        lRodriguez.setUserTypeOrd(null);
    }

    @Test
    @DisplayName("COMERCIAL crea un ADMIN_EMPRESAS por primera vez")
    void testUserSonOfUserCreator2() {
        admin_empresa.setUserTypeOrd(User.OUSER_TYPE_ORDINAL.ADMIN_EMPRESAS);
        /* -------------- */
        when(currentUser.getUser()).thenReturn(comercial);
        when(userService.findByUserTypeOrdAndClients(any(), any(), any())).thenReturn(new PageImpl<User>(Collections.emptyList(), PageRequest.of(0, 1, Sort.by("firstName")), 0));
        /* -------------- */
        classof.beforeSaving(0L, admin_empresa);
        Assertions.assertEquals(comercial.getEmail(), admin_empresa.getUserParent().getEmail());
        Assertions.assertEquals(User.OUSER_TYPE_ORDINAL.ADMIN_EMPRESAS, admin_empresa.getUserTypeOrd());
        Assertions.assertEquals(User.OUSER_TYPE.IS, admin_empresa.getUserType());
    }

    @Test
    @DisplayName("COMERCIAL crea un usuario para un cliente que tiene al menos un ADMIN_EMPRESAS")
    void testUserSonOfUserCreator3() {
        admin_empresa.setUserTypeOrd(User.OUSER_TYPE_ORDINAL.ADMIN_EMPRESAS);
        /* -------------- */
        when(currentUser.getUser()).thenReturn(comercial);
        when(userService.findByUserTypeOrdAndClients(any(), any(), any())).thenReturn(new PageImpl<User>(Arrays.asList(admin_empresa), PageRequest.of(0, 1, Sort.by("firstName")), 1));
        /* -------------- */
        classof.beforeSaving(0L, empresa);
        Assertions.assertEquals(admin_empresa.getEmail(), empresa.getUserParent().getEmail());
        Assertions.assertEquals(User.OUSER_TYPE_ORDINAL.EMPRESA, empresa.getUserTypeOrd());
        Assertions.assertEquals(User.OUSER_TYPE.BY, empresa.getUserType());
    }

    @Test
    @DisplayName("EMPRESA crea un usuario y es su CREADOR")
    void testUserSonOfUserCreator() {
        admin_empresa.setUserTypeOrd(User.OUSER_TYPE_ORDINAL.ADMIN_EMPRESAS);
        empresa.setUserTypeOrd(User.OUSER_TYPE_ORDINAL.EMPRESA);
        /* -------------- */
        when(currentUser.getUser()).thenReturn(empresa);
        when(userService.findByUserTypeOrdAndClients(any(), any(), any())).thenReturn(new PageImpl<User>(Arrays.asList(admin_empresa), PageRequest.of(0, 1, Sort.by("firstName")), 1));

        classof.beforeSaving(0L, lRodriguez);
        Assertions.assertEquals(empresa.getEmail(), lRodriguez.getUserParent().getEmail());
        Assertions.assertEquals(User.OUSER_TYPE_ORDINAL.USUARIO, lRodriguez.getUserTypeOrd());
        Assertions.assertEquals(User.OUSER_TYPE.BY, lRodriguez.getUserType());
    }

    @Test
    @DisplayName("Un usuario COMERCIAL crea un usuario y no se ha creado su administrador")
    void testUserWithoutParent() {
        when(currentUser.getUser()).thenReturn(comercial);
        when(userService.findByUserTypeOrdAndClients(any(), any(), any())).thenReturn(new PageImpl<User>(Collections.emptyList(), PageRequest.of(0, 1, Sort.by("firstName")), 1));

        classof.beforeSaving(0L, lRodriguez);
        Assertions.assertEquals(null, lRodriguez.getUserParent(), "No se ha creado su administrador");
    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme