package com.stt.dash.backend.service;

import com.stt.dash.app.security.CurrentUser;
import com.stt.dash.backend.data.entity.Client;
import com.stt.dash.backend.data.entity.MyAuditEventComponent;
import com.stt.dash.backend.data.entity.User;
import com.stt.dash.backend.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class UserServiceTest {
    @Mock
    UserRepository userRepository;
    @Mock
    CurrentUser currentUser;
    @Mock
    MyAuditEventComponent audit;
    @InjectMocks
    UserService userService;

    User comercial, comercial2, admin_empresa, empresa, lRodriguez;

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
        comercial2 = new User();
        comercial2.setActive(true);
        comercial2.setClient(client);
        comercial2.setCreatedBy("yecheverria@soltextech.com");
        comercial2.setEmail("yecheverria@soltextech.com");
        comercial2.setFirstName("Gleryxa");
        comercial2.setLastName("Bandres");
        comercial2.setLocked(false);
        comercial2.setPasswordHash("1Unica");
        comercial2.setUserParent(null);
        comercial2.setUserType(User.OUSER_TYPE.HAS);
        comercial2.setUserTypeOrd(User.OUSER_TYPE_ORDINAL.COMERCIAL);
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
        admin_empresa.setUserParent(comercial);
        admin_empresa.setUserType(User.OUSER_TYPE.IS);
        admin_empresa.setUserTypeOrd(User.OUSER_TYPE_ORDINAL.ADMIN_EMPRESAS);
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
        empresa.setUserParent(empresa);
        empresa.setUserType(User.OUSER_TYPE.BY);
        empresa.setUserTypeOrd(User.OUSER_TYPE_ORDINAL.EMPRESA);
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
        lRodriguez.setUserParent(empresa);
        lRodriguez.setUserType(User.OUSER_TYPE.BY);
        lRodriguez.setUserTypeOrd(User.OUSER_TYPE_ORDINAL.USUARIO);
    }

    @Test
    @DisplayName("Comercial no puede modificar data de usuario Comercial")
    void testSave1() {
        currentUser = () -> comercial;
        when(userRepository.findAllByUserParentIsNotNullAndEmailIsNot(anyString(), any())).thenReturn(new PageImpl<User>(Arrays.asList(admin_empresa, empresa, lRodriguez), PageRequest.of(0, 3, Sort.by("firstName")), 3));
        assertThrows(UserFriendlyDataException.class, () -> userService.save(currentUser.getUser(), comercial2));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Comercial puede modificar data de usuario No Comercial")
    void testSave2() {
        currentUser = () -> comercial;
        userService.save(currentUser.getUser(), admin_empresa);
        userService.save(currentUser.getUser(), empresa);
        userService.save(currentUser.getUser(), lRodriguez);
        /* Ocurre el llamado a save and flush en CrudService */
        verify(userRepository, times(1)).saveAndFlush(admin_empresa);
        verify(userRepository, times(1)).saveAndFlush(empresa);
        verify(userRepository, times(1)).saveAndFlush(lRodriguez);
    }

    @Test
    @DisplayName("COMERCIAL, debe traer todos los usuarios")
    void testFind2() {
        currentUser = () -> comercial;
        when(userRepository.findAllByUserParentIsNotNullAndEmailIsNot(anyString(), any())).thenReturn(new PageImpl<User>(Arrays.asList(admin_empresa, empresa, lRodriguez), PageRequest.of(0, 3, Sort.by("firstName")), 3));
        when(userRepository.findByClientsInAndUserTypeOrdNotAndIdIsNot(any(), any(), any(), any())).thenReturn(new PageImpl<User>(Collections.emptyList(), PageRequest.of(0, 1, Sort.by("firstName")), 0));

        Page<User> result = userService.find(currentUser, PageRequest.of(0, 3, Sort.by("firstName")));
        Assertions.assertEquals(3, result.getContent().size());
    }

    @Test
    @DisplayName("ADMIN_EMPRESAS, debe traer todos con el mismo cliente, excepto COMERCIAL")
    void testFind3() {
        currentUser = () -> admin_empresa;
        List<User> userSameClientNotComercialAndNotMe = Arrays.asList(empresa, lRodriguez);
        when(userRepository.findAllByUserParentIsNotNullAndEmailIsNot(eq(admin_empresa.getEmail()), any())).thenReturn(new PageImpl<User>(Arrays.asList(empresa, lRodriguez), PageRequest.of(0, 3, Sort.by("firstName")), 3));
        when(userRepository.findByClientsInAndUserTypeOrdNotAndIdIsNot(any(), eq(User.OUSER_TYPE_ORDINAL.COMERCIAL), any(), any())).thenReturn(new PageImpl<User>(userSameClientNotComercialAndNotMe, PageRequest.of(0, 3, Sort.by("firstName")), 3));

        Page<User> result = userService.find(currentUser, PageRequest.of(0, 3, Sort.by("firstName")));
        Assertions.assertEquals(2, result.getContent().size());
        Assertions.assertTrue(result.getContent().contains(empresa));
        Assertions.assertTrue(result.getContent().contains(lRodriguez));
        Assertions.assertFalse(result.getContent().contains(admin_empresa));
    }

    @Test
    @DisplayName("EMPRESAS, debe traer todos sus hijos")
    void testFind4() {
        currentUser = () -> empresa;
        empresa.setUserChildren(new HashSet<>(Arrays.asList(lRodriguez)));
        lRodriguez.setUserChildren(Collections.emptySet());
        /* --------- */
        when(userRepository.findAllByUserParentIsNotNullAndEmailIsNot(anyString(), any())).thenReturn(new PageImpl<User>(Arrays.asList(admin_empresa, empresa, lRodriguez), PageRequest.of(0, 3, Sort.by("firstName")), 3));
        when(userRepository.findByClientsInAndUserTypeOrdNotAndIdIsNot(any(), any(), any(), any())).thenReturn(new PageImpl<User>(Arrays.asList(admin_empresa, lRodriguez), PageRequest.of(0, 3, Sort.by("firstName")), 3));

        Page<User> result = userService.find(currentUser, PageRequest.of(0, 3, Sort.by("firstName")));
        Assertions.assertEquals(1, result.getContent().size());
        Assertions.assertTrue(result.getContent().contains(lRodriguez));
        Assertions.assertFalse(result.getContent().contains(empresa));
        Assertions.assertFalse(result.getContent().contains(admin_empresa));
        verify(userRepository, times(0)).findAllByUserParentIsNotNullAndEmailIsNot(anyString(), any());
        verify(userRepository, times(0)).findByClientsInAndUserTypeOrdNotAndIdIsNot(any(), any(), any(), any());
    }

//    @Test
//    void testFindAnyMatching() {
//        when(userRepository.findBy(any())).thenReturn(null);
//        when(userRepository.findByEmailLikeIgnoreCaseOrFirstNameLikeIgnoreCaseOrLastNameLikeIgnoreCaseOrRoleLikeIgnoreCase(anyString(), anyString(), anyString(), anyString(), any())).thenReturn(null);
//
//        Page<User> result = userService.findAnyMatching(null, null);
//        Assertions.assertEquals(null, result);
//    }

//    @Test
//    void testFindAnyMatching2() {
//        when(userRepository.findAllByUserParentIsNotNullAndEmailIsNot(anyString(), any())).thenReturn(null);
//        when(userRepository.findByClientsInAndUserTypeOrdNot(any(), any(), any())).thenReturn(null);
//        when(userRepository.findByEmailLikeIgnoreCaseOrFirstNameLikeIgnoreCaseOrLastNameLikeIgnoreCaseOrRoleLikeIgnoreCase(anyString(), anyString(), anyString(), anyString(), any())).thenReturn(null);
//
//        Page<User> result = userService.findAnyMatching(null, null, null);
//        Assertions.assertEquals(null, result);
//    }

//    @Test
//    void testCountAnyMatching() {
//        when(userRepository.countByEmailLikeIgnoreCaseOrFirstNameLikeIgnoreCaseOrLastNameLikeIgnoreCaseOrRoleLikeIgnoreCase(anyString(), anyString(), anyString(), anyString())).thenReturn(0L);
//
//        long result = userService.countAnyMatching(null);
//        Assertions.assertEquals(0L, result);
//    }

    @Test
    void testFindByUserTypeOrdAndClients() {
        when(userRepository.findByUserTypeOrdAndClients(any(), any(), any())).thenReturn(null);

        Page<User> result = userService.findByUserTypeOrdAndClients(User.OUSER_TYPE_ORDINAL.COMERCIAL, new Client("clientName"), null);
        Assertions.assertEquals(null, result);
    }

//    @Test
//    void testFindByEmailIgnoreCase() {
//        when(userRepository.findByEmailIgnoreCase(anyString())).thenReturn(new User());
//
//        User result = userService.findByEmailIgnoreCase("email");
//        Assertions.assertEquals(new User(), result);
//    }

//    @Test
//    void testFind() {
//        when(userRepository.findBy(any())).thenReturn(null);
//
//        Page<User> result = userService.find(null);
//        Assertions.assertEquals(null, result);
//    }

//    @Test
//    void testSave() {
//        User result = userService.save(new User(), new User());
//        Assertions.assertEquals(new User(), result);
//    }

//    @Test
//    void testDelete() {
//        userService.delete(new User(), new User());
//    }

//    @Test
//    void testCreateNew() {
//        User result = userService.createNew(new User());
//        Assertions.assertEquals(new User(), result);
//    }

//    @Test
//    void testDeactivateUser() {
//        when(userRepository.findByEmailIgnoreCase(anyString())).thenReturn(new User());
//
//        User result = userService.deactivateUser("userEmail", "blockReason");
//        Assertions.assertEquals(new User(), result);
//    }

//    @Test
//    void testCountAnyMatching2() {
//        long result = userService.countAnyMatching(null, null);
//        Assertions.assertEquals(0L, result);
//    }

//    @Test
//    void testDelete2() {
//        userService.delete(new User(), 0L);
//    }

//    @Test
//    void testCount() {
//        long result = userService.count();
//        Assertions.assertEquals(0L, result);
//    }

//    @Test
//    void testCount2() {
//        long result = userService.count(0L);
//        Assertions.assertEquals(0L, result);
//    }

//    @Test
//    void testLoad() {
//        User result = userService.load(0L);
//        Assertions.assertEquals(new User(), result);
//    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme