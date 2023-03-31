package com.stt.dash.ui.views.admin.users.v2;

import com.stt.dash.app.security.CurrentUser;
import com.stt.dash.backend.data.entity.AbstractEntitySequence;
import com.stt.dash.backend.data.entity.Client;
import com.stt.dash.backend.data.entity.SystemId;
import com.stt.dash.backend.data.entity.User;
import com.stt.dash.backend.service.UserService;
import com.stt.dash.ui.crud.CrudEntityPresenter;
import com.stt.dash.ui.crud.OnUIForm;
import com.stt.dash.ui.utils.BeforeSavingResponse;
import com.vaadin.flow.component.ComponentEventBus;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.PropertyDescriptor;
import com.vaadin.flow.component.crud.Crud;
import com.vaadin.flow.component.crud.CrudEditor;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.server.Attributes;
import com.vaadin.flow.shared.Registration;
import liquibase.pro.packaged.E;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.*;

@Disabled
class UsersViewv2Test {
    @Mock
    Logger log;
    @Mock
    CurrentUser currentUser;
    @Mock
    UserService userService;
    @Mock
    Grid<E> grid;
    @Mock
    OnUIForm onUiForm;
    @Mock
    E oldEntity;
    @Mock
    CrudEntityPresenter entityPresenter;
    @Mock
    Set<ComponentEventListener<Crud.NewEvent<E>>> newListeners;
    @Mock
    Set<ComponentEventListener<Crud.EditEvent<E>>> editListeners;
    @Mock
    Set<ComponentEventListener<Crud.SaveEvent<E>>> saveListeners;
    @Mock
    Set<ComponentEventListener<Crud.CancelEvent<E>>> cancelListeners;
    @Mock
    Set<ComponentEventListener<Crud.DeleteEvent<E>>> deleteListeners;
    //Field beanType of type Class - was not mocked since Mockito doesn't mock a Final class when 'mock-maker-inline' option is not set
    @Mock
    CrudEditor<E> editor;
    @Mock
    E gridActiveItem;
    @Mock
    Registration gridItemClickRegistration;
    @Mock
    PropertyDescriptor<String, Optional<String>> idDescriptor;
    //    @Mock
//    ThreadLocal<Component.MapToExistingElement> elementToMapTo;
    @Mock
    Element element;
    @Mock
    Attributes attributes;
    @Mock
    ComponentEventBus eventBus;
    @InjectMocks
    UsersViewv2 usersViewv2;
    User yecheverria, sgonzalez, jperez, lRodriguez;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        /**/

    }

    @Test
    void testSetupGrid() {
        usersViewv2.setupGrid(null);
    }

    @Test
    void testGetBasePage() {
        String result = usersViewv2.getBasePage();
        Assertions.assertEquals("replaceMeWithExpectedResult", result);
    }

    @Test
    void testBeforeSaving() {
        when(currentUser.getUser()).thenReturn(jperez);
        when(userService.findByUserTypeOrdAndClients(any(), any(), any())).thenReturn(new PageImpl<User>(Arrays.asList(sgonzalez), PageRequest.of(0, 1, Sort.by("firstName")), 1));

        BeforeSavingResponse result = usersViewv2.beforeSaving(0L, lRodriguez);
        Assertions.assertEquals(lRodriguez.getUserParent(), jperez);
    }

//    @Test
//    void testSetParameter() {
//        when(entityPresenter.loadEntity(anyLong(), any())).thenReturn(true);
//
//        usersViewv2.setParameter(null, Long.valueOf(1));
//    }

    @Test
    void testShowNotification() {
        usersViewv2.showNotification("message");
    }

    @Test
    void testShowNotification2() {
        usersViewv2.showNotification("message", true);
    }

    @Test
    void testGetLogger() {
        Logger result = usersViewv2.getLogger();
        Assertions.assertEquals(null, result);
    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme