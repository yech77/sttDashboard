package com.stt.dash.ui.popup;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.stt.dash.backend.data.SmsByYearMonthDayHour;
import com.stt.dash.backend.repositories.SmsHourRepository;
import com.stt.dash.backend.service.SmsHourService;
import com.stt.dash.ui.Viewnable;
import com.vaadin.flow.data.provider.ListDataProvider;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class DailySmsPopupPresenterTest {
    /**
     * Method under test: {@link DailySmsPopupPresenter#DailySmsPopupPresenter(SmsHourService, int, int, int, java.util.List, java.util.List, java.util.List, com.stt.dash.ui.Viewnable)}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testConstructor() {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R026 Failed to create Spring context.
        //   Attempt to initialize test context failed with
        //   java.security.PrivilegedActionException
        //       at java.security.AccessController.doPrivileged(Native Method)
        //       at org.springframework.beans.factory.support.SimpleInstantiationStrategy.instantiate(SimpleInstantiationStrategy.java:74)
        //       at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.lambda$instantiateBean$3(AbstractAutowireCapableBeanFactory.java:1322)
        //       at java.security.AccessController.doPrivileged(Native Method)
        //       at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.instantiateBean(AbstractAutowireCapableBeanFactory.java:1321)
        //       at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBeanInstance(AbstractAutowireCapableBeanFactory.java:1232)
        //       at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:582)
        //       at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:542)
        //       at org.springframework.beans.factory.support.AbstractBeanFactory.lambda$doGetBean$0(AbstractBeanFactory.java:335)
        //       at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:234)
        //       at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:333)
        //       at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:208)
        //       at org.springframework.beans.factory.support.DefaultListableBeanFactory.preInstantiateSingletons(DefaultListableBeanFactory.java:953)
        //       at org.springframework.context.support.AbstractApplicationContext.finishBeanFactoryInitialization(AbstractApplicationContext.java:918)
        //       at org.springframework.context.support.AbstractApplicationContext.refresh(AbstractApplicationContext.java:583)
        //       at org.springframework.test.context.support.AbstractGenericContextLoader.loadContext(AbstractGenericContextLoader.java:127)
        //       at org.springframework.test.context.support.AbstractGenericContextLoader.loadContext(AbstractGenericContextLoader.java:60)
        //       at org.springframework.test.context.support.AbstractDelegatingSmartContextLoader.delegateLoading(AbstractDelegatingSmartContextLoader.java:275)
        //       at org.springframework.test.context.support.AbstractDelegatingSmartContextLoader.loadContext(AbstractDelegatingSmartContextLoader.java:243)
        //       at org.springframework.test.context.cache.DefaultCacheAwareContextLoaderDelegate.loadContextInternal(DefaultCacheAwareContextLoaderDelegate.java:99)
        //       at org.springframework.test.context.cache.DefaultCacheAwareContextLoaderDelegate.loadContext(DefaultCacheAwareContextLoaderDelegate.java:124)
        //       at org.springframework.test.context.support.DefaultTestContext.getApplicationContext(DefaultTestContext.java:124)
        //   java.lang.NoSuchMethodException: com.stt.dash.ui.popup.DailySmsPopupPresenter.<init>()
        //       at java.lang.Class.getConstructor0(Class.java:3082)
        //       at java.lang.Class.getDeclaredConstructor(Class.java:2178)
        //       at org.springframework.beans.factory.support.SimpleInstantiationStrategy.lambda$instantiate$0(SimpleInstantiationStrategy.java:75)
        //       at java.security.AccessController.doPrivileged(Native Method)
        //       at org.springframework.beans.factory.support.SimpleInstantiationStrategy.instantiate(SimpleInstantiationStrategy.java:74)
        //       at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.lambda$instantiateBean$3(AbstractAutowireCapableBeanFactory.java:1322)
        //       at java.security.AccessController.doPrivileged(Native Method)
        //       at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.instantiateBean(AbstractAutowireCapableBeanFactory.java:1321)
        //       at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBeanInstance(AbstractAutowireCapableBeanFactory.java:1232)
        //       at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:582)
        //       at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:542)
        //       at org.springframework.beans.factory.support.AbstractBeanFactory.lambda$doGetBean$0(AbstractBeanFactory.java:335)
        //       at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:234)
        //       at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:333)
        //       at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:208)
        //       at org.springframework.beans.factory.support.DefaultListableBeanFactory.preInstantiateSingletons(DefaultListableBeanFactory.java:953)
        //       at org.springframework.context.support.AbstractApplicationContext.finishBeanFactoryInitialization(AbstractApplicationContext.java:918)
        //       at org.springframework.context.support.AbstractApplicationContext.refresh(AbstractApplicationContext.java:583)
        //       at org.springframework.test.context.support.AbstractGenericContextLoader.loadContext(AbstractGenericContextLoader.java:127)
        //       at org.springframework.test.context.support.AbstractGenericContextLoader.loadContext(AbstractGenericContextLoader.java:60)
        //       at org.springframework.test.context.support.AbstractDelegatingSmartContextLoader.delegateLoading(AbstractDelegatingSmartContextLoader.java:275)
        //       at org.springframework.test.context.support.AbstractDelegatingSmartContextLoader.loadContext(AbstractDelegatingSmartContextLoader.java:243)
        //       at org.springframework.test.context.cache.DefaultCacheAwareContextLoaderDelegate.loadContextInternal(DefaultCacheAwareContextLoaderDelegate.java:99)
        //       at org.springframework.test.context.cache.DefaultCacheAwareContextLoaderDelegate.loadContext(DefaultCacheAwareContextLoaderDelegate.java:124)
        //       at org.springframework.test.context.support.DefaultTestContext.getApplicationContext(DefaultTestContext.java:124)
        //   See https://diff.blue/R026 to resolve this issue.

        // TODO: Complete this test.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.stt.dash.ui.popup.DailySmsPopupPresenter.updateInView(DailySmsPopupPresenter.java:135)
        //       at com.stt.dash.ui.popup.DailySmsPopupPresenter.<init>(DailySmsPopupPresenter.java:112)
        //   In order to prevent <init>(SmsHourService, int, int, int, List, List, List, Viewnable)
        //   from throwing NullPointerException, add constructors or factory
        //   methods that make it easier to construct fully initialized objects used in
        //   <init>(SmsHourService, int, int, int, List, List, List, Viewnable).
        //   See https://diff.blue/R013 to resolve this issue.

        SmsHourRepository smsHourRepository = mock(SmsHourRepository.class);
        when(smsHourRepository.groupSmsCarrierAndMessageTypeByYeMoDaHoWhYeMoDaSyIn_CarrierInTyIn(anyInt(), anyInt(), anyInt(),
                (java.util.List<String>) any(), (java.util.List<String>) any(), (java.util.List<String>) any()))
                .thenReturn(new ArrayList<>());
        SmsHourService smsHourService = new SmsHourService(smsHourRepository);
        ArrayList<String> carrierList = new ArrayList<>();
        ArrayList<String> messageTypeList = new ArrayList<>();
        new DailySmsPopupPresenter(smsHourService, 1000, 1, 1, carrierList, messageTypeList, new ArrayList<>(), null);

    }

    /**
     * Method under test: {@link DailySmsPopupPresenter#DailySmsPopupPresenter(SmsHourService, int, int, int, java.util.List, java.util.List, java.util.List, com.stt.dash.ui.Viewnable)}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testConstructor2() {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R026 Failed to create Spring context.
        //   Attempt to initialize test context failed with
        //   java.security.PrivilegedActionException
        //       at java.security.AccessController.doPrivileged(Native Method)
        //       at org.springframework.beans.factory.support.SimpleInstantiationStrategy.instantiate(SimpleInstantiationStrategy.java:74)
        //       at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.lambda$instantiateBean$3(AbstractAutowireCapableBeanFactory.java:1322)
        //       at java.security.AccessController.doPrivileged(Native Method)
        //       at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.instantiateBean(AbstractAutowireCapableBeanFactory.java:1321)
        //       at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBeanInstance(AbstractAutowireCapableBeanFactory.java:1232)
        //       at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:582)
        //       at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:542)
        //       at org.springframework.beans.factory.support.AbstractBeanFactory.lambda$doGetBean$0(AbstractBeanFactory.java:335)
        //       at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:234)
        //       at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:333)
        //       at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:208)
        //       at org.springframework.beans.factory.support.DefaultListableBeanFactory.preInstantiateSingletons(DefaultListableBeanFactory.java:953)
        //       at org.springframework.context.support.AbstractApplicationContext.finishBeanFactoryInitialization(AbstractApplicationContext.java:918)
        //       at org.springframework.context.support.AbstractApplicationContext.refresh(AbstractApplicationContext.java:583)
        //       at org.springframework.test.context.support.AbstractGenericContextLoader.loadContext(AbstractGenericContextLoader.java:127)
        //       at org.springframework.test.context.support.AbstractGenericContextLoader.loadContext(AbstractGenericContextLoader.java:60)
        //       at org.springframework.test.context.support.AbstractDelegatingSmartContextLoader.delegateLoading(AbstractDelegatingSmartContextLoader.java:275)
        //       at org.springframework.test.context.support.AbstractDelegatingSmartContextLoader.loadContext(AbstractDelegatingSmartContextLoader.java:243)
        //       at org.springframework.test.context.cache.DefaultCacheAwareContextLoaderDelegate.loadContextInternal(DefaultCacheAwareContextLoaderDelegate.java:99)
        //       at org.springframework.test.context.cache.DefaultCacheAwareContextLoaderDelegate.loadContext(DefaultCacheAwareContextLoaderDelegate.java:124)
        //       at org.springframework.test.context.support.DefaultTestContext.getApplicationContext(DefaultTestContext.java:124)
        //   java.lang.NoSuchMethodException: com.stt.dash.ui.popup.DailySmsPopupPresenter.<init>()
        //       at java.lang.Class.getConstructor0(Class.java:3082)
        //       at java.lang.Class.getDeclaredConstructor(Class.java:2178)
        //       at org.springframework.beans.factory.support.SimpleInstantiationStrategy.lambda$instantiate$0(SimpleInstantiationStrategy.java:75)
        //       at java.security.AccessController.doPrivileged(Native Method)
        //       at org.springframework.beans.factory.support.SimpleInstantiationStrategy.instantiate(SimpleInstantiationStrategy.java:74)
        //       at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.lambda$instantiateBean$3(AbstractAutowireCapableBeanFactory.java:1322)
        //       at java.security.AccessController.doPrivileged(Native Method)
        //       at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.instantiateBean(AbstractAutowireCapableBeanFactory.java:1321)
        //       at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBeanInstance(AbstractAutowireCapableBeanFactory.java:1232)
        //       at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:582)
        //       at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:542)
        //       at org.springframework.beans.factory.support.AbstractBeanFactory.lambda$doGetBean$0(AbstractBeanFactory.java:335)
        //       at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:234)
        //       at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:333)
        //       at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:208)
        //       at org.springframework.beans.factory.support.DefaultListableBeanFactory.preInstantiateSingletons(DefaultListableBeanFactory.java:953)
        //       at org.springframework.context.support.AbstractApplicationContext.finishBeanFactoryInitialization(AbstractApplicationContext.java:918)
        //       at org.springframework.context.support.AbstractApplicationContext.refresh(AbstractApplicationContext.java:583)
        //       at org.springframework.test.context.support.AbstractGenericContextLoader.loadContext(AbstractGenericContextLoader.java:127)
        //       at org.springframework.test.context.support.AbstractGenericContextLoader.loadContext(AbstractGenericContextLoader.java:60)
        //       at org.springframework.test.context.support.AbstractDelegatingSmartContextLoader.delegateLoading(AbstractDelegatingSmartContextLoader.java:275)
        //       at org.springframework.test.context.support.AbstractDelegatingSmartContextLoader.loadContext(AbstractDelegatingSmartContextLoader.java:243)
        //       at org.springframework.test.context.cache.DefaultCacheAwareContextLoaderDelegate.loadContextInternal(DefaultCacheAwareContextLoaderDelegate.java:99)
        //       at org.springframework.test.context.cache.DefaultCacheAwareContextLoaderDelegate.loadContext(DefaultCacheAwareContextLoaderDelegate.java:124)
        //       at org.springframework.test.context.support.DefaultTestContext.getApplicationContext(DefaultTestContext.java:124)
        //   See https://diff.blue/R026 to resolve this issue.

        // TODO: Complete this test.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.stt.dash.ui.popup.DailySmsPopupPresenter.<init>(DailySmsPopupPresenter.java:110)
        //   In order to prevent <init>(SmsHourService, int, int, int, List, List, List, Viewnable)
        //   from throwing NullPointerException, add constructors or factory
        //   methods that make it easier to construct fully initialized objects used in
        //   <init>(SmsHourService, int, int, int, List, List, List, Viewnable).
        //   See https://diff.blue/R013 to resolve this issue.

        ArrayList<String> carrierList = new ArrayList<>();
        ArrayList<String> messageTypeList = new ArrayList<>();
        new DailySmsPopupPresenter(null, 1000, 1, 1, carrierList, messageTypeList, new ArrayList<>(), null);

    }

    /**
     * Method under test: {@link DailySmsPopupPresenter#DailySmsPopupPresenter(SmsHourService, int, int, int, java.util.List, java.util.List, java.util.List, com.stt.dash.ui.Viewnable)}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testConstructor3() {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R026 Failed to create Spring context.
        //   Attempt to initialize test context failed with
        //   java.security.PrivilegedActionException
        //       at java.security.AccessController.doPrivileged(Native Method)
        //       at org.springframework.beans.factory.support.SimpleInstantiationStrategy.instantiate(SimpleInstantiationStrategy.java:74)
        //       at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.lambda$instantiateBean$3(AbstractAutowireCapableBeanFactory.java:1322)
        //       at java.security.AccessController.doPrivileged(Native Method)
        //       at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.instantiateBean(AbstractAutowireCapableBeanFactory.java:1321)
        //       at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBeanInstance(AbstractAutowireCapableBeanFactory.java:1232)
        //       at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:582)
        //       at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:542)
        //       at org.springframework.beans.factory.support.AbstractBeanFactory.lambda$doGetBean$0(AbstractBeanFactory.java:335)
        //       at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:234)
        //       at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:333)
        //       at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:208)
        //       at org.springframework.beans.factory.support.DefaultListableBeanFactory.preInstantiateSingletons(DefaultListableBeanFactory.java:953)
        //       at org.springframework.context.support.AbstractApplicationContext.finishBeanFactoryInitialization(AbstractApplicationContext.java:918)
        //       at org.springframework.context.support.AbstractApplicationContext.refresh(AbstractApplicationContext.java:583)
        //       at org.springframework.test.context.support.AbstractGenericContextLoader.loadContext(AbstractGenericContextLoader.java:127)
        //       at org.springframework.test.context.support.AbstractGenericContextLoader.loadContext(AbstractGenericContextLoader.java:60)
        //       at org.springframework.test.context.support.AbstractDelegatingSmartContextLoader.delegateLoading(AbstractDelegatingSmartContextLoader.java:275)
        //       at org.springframework.test.context.support.AbstractDelegatingSmartContextLoader.loadContext(AbstractDelegatingSmartContextLoader.java:243)
        //       at org.springframework.test.context.cache.DefaultCacheAwareContextLoaderDelegate.loadContextInternal(DefaultCacheAwareContextLoaderDelegate.java:99)
        //       at org.springframework.test.context.cache.DefaultCacheAwareContextLoaderDelegate.loadContext(DefaultCacheAwareContextLoaderDelegate.java:124)
        //       at org.springframework.test.context.support.DefaultTestContext.getApplicationContext(DefaultTestContext.java:124)
        //   java.lang.NoSuchMethodException: com.stt.dash.ui.popup.DailySmsPopupPresenter.<init>()
        //       at java.lang.Class.getConstructor0(Class.java:3082)
        //       at java.lang.Class.getDeclaredConstructor(Class.java:2178)
        //       at org.springframework.beans.factory.support.SimpleInstantiationStrategy.lambda$instantiate$0(SimpleInstantiationStrategy.java:75)
        //       at java.security.AccessController.doPrivileged(Native Method)
        //       at org.springframework.beans.factory.support.SimpleInstantiationStrategy.instantiate(SimpleInstantiationStrategy.java:74)
        //       at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.lambda$instantiateBean$3(AbstractAutowireCapableBeanFactory.java:1322)
        //       at java.security.AccessController.doPrivileged(Native Method)
        //       at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.instantiateBean(AbstractAutowireCapableBeanFactory.java:1321)
        //       at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBeanInstance(AbstractAutowireCapableBeanFactory.java:1232)
        //       at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:582)
        //       at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:542)
        //       at org.springframework.beans.factory.support.AbstractBeanFactory.lambda$doGetBean$0(AbstractBeanFactory.java:335)
        //       at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:234)
        //       at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:333)
        //       at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:208)
        //       at org.springframework.beans.factory.support.DefaultListableBeanFactory.preInstantiateSingletons(DefaultListableBeanFactory.java:953)
        //       at org.springframework.context.support.AbstractApplicationContext.finishBeanFactoryInitialization(AbstractApplicationContext.java:918)
        //       at org.springframework.context.support.AbstractApplicationContext.refresh(AbstractApplicationContext.java:583)
        //       at org.springframework.test.context.support.AbstractGenericContextLoader.loadContext(AbstractGenericContextLoader.java:127)
        //       at org.springframework.test.context.support.AbstractGenericContextLoader.loadContext(AbstractGenericContextLoader.java:60)
        //       at org.springframework.test.context.support.AbstractDelegatingSmartContextLoader.delegateLoading(AbstractDelegatingSmartContextLoader.java:275)
        //       at org.springframework.test.context.support.AbstractDelegatingSmartContextLoader.loadContext(AbstractDelegatingSmartContextLoader.java:243)
        //       at org.springframework.test.context.cache.DefaultCacheAwareContextLoaderDelegate.loadContextInternal(DefaultCacheAwareContextLoaderDelegate.java:99)
        //       at org.springframework.test.context.cache.DefaultCacheAwareContextLoaderDelegate.loadContext(DefaultCacheAwareContextLoaderDelegate.java:124)
        //       at org.springframework.test.context.support.DefaultTestContext.getApplicationContext(DefaultTestContext.java:124)
        //   See https://diff.blue/R026 to resolve this issue.

        // TODO: Complete this test.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.stt.dash.ui.popup.DailySmsPopupPresenter.updateInView(DailySmsPopupPresenter.java:135)
        //       at com.stt.dash.ui.popup.DailySmsPopupPresenter.<init>(DailySmsPopupPresenter.java:112)
        //   In order to prevent <init>(SmsHourService, int, int, int, List, List, List, Viewnable)
        //   from throwing NullPointerException, add constructors or factory
        //   methods that make it easier to construct fully initialized objects used in
        //   <init>(SmsHourService, int, int, int, List, List, List, Viewnable).
        //   See https://diff.blue/R013 to resolve this issue.

        SmsHourService smsHourService = mock(SmsHourService.class);
        when(smsHourService.groupSmsCarrierAndMessageTypeByYeMoDaWhYeMoDaSyIn_CarrierInTyIn(anyInt(), anyInt(), anyInt(),
                (java.util.List<String>) any(), (java.util.List<String>) any(), (java.util.List<String>) any()))
                .thenReturn(new ArrayList<>());
        ArrayList<String> carrierList = new ArrayList<>();
        ArrayList<String> messageTypeList = new ArrayList<>();
        new DailySmsPopupPresenter(smsHourService, 1000, 1, 1, carrierList, messageTypeList, new ArrayList<>(), null);

    }

    /**
     * Method under test: {@link DailySmsPopupPresenter#DailySmsPopupPresenter(SmsHourService, int, int, int, List, List, List, Viewnable)}
     */
    @Test
    void testConstructor4() {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R026 Failed to create Spring context.
        //   Attempt to initialize test context failed with
        //   java.security.PrivilegedActionException
        //       at java.security.AccessController.doPrivileged(Native Method)
        //       at org.springframework.beans.factory.support.SimpleInstantiationStrategy.instantiate(SimpleInstantiationStrategy.java:74)
        //       at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.lambda$instantiateBean$3(AbstractAutowireCapableBeanFactory.java:1322)
        //       at java.security.AccessController.doPrivileged(Native Method)
        //       at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.instantiateBean(AbstractAutowireCapableBeanFactory.java:1321)
        //       at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBeanInstance(AbstractAutowireCapableBeanFactory.java:1232)
        //       at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:582)
        //       at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:542)
        //       at org.springframework.beans.factory.support.AbstractBeanFactory.lambda$doGetBean$0(AbstractBeanFactory.java:335)
        //       at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:234)
        //       at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:333)
        //       at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:208)
        //       at org.springframework.beans.factory.support.DefaultListableBeanFactory.preInstantiateSingletons(DefaultListableBeanFactory.java:953)
        //       at org.springframework.context.support.AbstractApplicationContext.finishBeanFactoryInitialization(AbstractApplicationContext.java:918)
        //       at org.springframework.context.support.AbstractApplicationContext.refresh(AbstractApplicationContext.java:583)
        //       at org.springframework.test.context.support.AbstractGenericContextLoader.loadContext(AbstractGenericContextLoader.java:127)
        //       at org.springframework.test.context.support.AbstractGenericContextLoader.loadContext(AbstractGenericContextLoader.java:60)
        //       at org.springframework.test.context.support.AbstractDelegatingSmartContextLoader.delegateLoading(AbstractDelegatingSmartContextLoader.java:275)
        //       at org.springframework.test.context.support.AbstractDelegatingSmartContextLoader.loadContext(AbstractDelegatingSmartContextLoader.java:243)
        //       at org.springframework.test.context.cache.DefaultCacheAwareContextLoaderDelegate.loadContextInternal(DefaultCacheAwareContextLoaderDelegate.java:99)
        //       at org.springframework.test.context.cache.DefaultCacheAwareContextLoaderDelegate.loadContext(DefaultCacheAwareContextLoaderDelegate.java:124)
        //       at org.springframework.test.context.support.DefaultTestContext.getApplicationContext(DefaultTestContext.java:124)
        //   java.lang.NoSuchMethodException: com.stt.dash.ui.popup.DailySmsPopupPresenter.<init>()
        //       at java.lang.Class.getConstructor0(Class.java:3082)
        //       at java.lang.Class.getDeclaredConstructor(Class.java:2178)
        //       at org.springframework.beans.factory.support.SimpleInstantiationStrategy.lambda$instantiate$0(SimpleInstantiationStrategy.java:75)
        //       at java.security.AccessController.doPrivileged(Native Method)
        //       at org.springframework.beans.factory.support.SimpleInstantiationStrategy.instantiate(SimpleInstantiationStrategy.java:74)
        //       at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.lambda$instantiateBean$3(AbstractAutowireCapableBeanFactory.java:1322)
        //       at java.security.AccessController.doPrivileged(Native Method)
        //       at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.instantiateBean(AbstractAutowireCapableBeanFactory.java:1321)
        //       at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBeanInstance(AbstractAutowireCapableBeanFactory.java:1232)
        //       at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:582)
        //       at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:542)
        //       at org.springframework.beans.factory.support.AbstractBeanFactory.lambda$doGetBean$0(AbstractBeanFactory.java:335)
        //       at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:234)
        //       at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:333)
        //       at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:208)
        //       at org.springframework.beans.factory.support.DefaultListableBeanFactory.preInstantiateSingletons(DefaultListableBeanFactory.java:953)
        //       at org.springframework.context.support.AbstractApplicationContext.finishBeanFactoryInitialization(AbstractApplicationContext.java:918)
        //       at org.springframework.context.support.AbstractApplicationContext.refresh(AbstractApplicationContext.java:583)
        //       at org.springframework.test.context.support.AbstractGenericContextLoader.loadContext(AbstractGenericContextLoader.java:127)
        //       at org.springframework.test.context.support.AbstractGenericContextLoader.loadContext(AbstractGenericContextLoader.java:60)
        //       at org.springframework.test.context.support.AbstractDelegatingSmartContextLoader.delegateLoading(AbstractDelegatingSmartContextLoader.java:275)
        //       at org.springframework.test.context.support.AbstractDelegatingSmartContextLoader.loadContext(AbstractDelegatingSmartContextLoader.java:243)
        //       at org.springframework.test.context.cache.DefaultCacheAwareContextLoaderDelegate.loadContextInternal(DefaultCacheAwareContextLoaderDelegate.java:99)
        //       at org.springframework.test.context.cache.DefaultCacheAwareContextLoaderDelegate.loadContext(DefaultCacheAwareContextLoaderDelegate.java:124)
        //       at org.springframework.test.context.support.DefaultTestContext.getApplicationContext(DefaultTestContext.java:124)
        //   See https://diff.blue/R026 to resolve this issue.

        SmsHourService smsHourService = mock(SmsHourService.class);
        when(smsHourService.groupSmsCarrierAndMessageTypeByYeMoDaWhYeMoDaSyIn_CarrierInTyIn(anyInt(), anyInt(), anyInt(),
                (List<String>) any(), (List<String>) any(), (List<String>) any())).thenReturn(new ArrayList<>());
        ArrayList<String> carrierList = new ArrayList<>();
        ArrayList<String> messageTypeList = new ArrayList<>();
        ArrayList<String> systemidStringList = new ArrayList<>();
        Viewnable<SmsByYearMonthDayHour> viewnable = (Viewnable<SmsByYearMonthDayHour>) mock(Viewnable.class);
        doNothing().when(viewnable).setGridDataProvider((ListDataProvider<SmsByYearMonthDayHour>) any());
        DailySmsPopupPresenter actualDailySmsPopupPresenter = new DailySmsPopupPresenter(smsHourService, 1000, 1, 1,
                carrierList, messageTypeList, systemidStringList, viewnable);

        assertTrue(actualDailySmsPopupPresenter.systemidStringList.isEmpty());
        ListDataProvider<SmsByYearMonthDayHour> listDataProvider = actualDailySmsPopupPresenter.dataProvider;
        assertTrue(listDataProvider.getItems().isEmpty());
        assertNull(listDataProvider.getSortComparator());
        verify(smsHourService).groupSmsCarrierAndMessageTypeByYeMoDaWhYeMoDaSyIn_CarrierInTyIn(anyInt(), anyInt(), anyInt(),
                (List<String>) any(), (List<String>) any(), (List<String>) any());
        verify(viewnable).setGridDataProvider((ListDataProvider<SmsByYearMonthDayHour>) any());
    }
}

