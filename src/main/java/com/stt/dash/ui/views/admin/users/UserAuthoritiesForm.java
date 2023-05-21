package com.stt.dash.ui.views.admin.users;

import com.stt.dash.app.security.CurrentUser;
import com.stt.dash.backend.data.Role;
import com.stt.dash.backend.data.entity.OAuthority;
import com.stt.dash.backend.data.entity.ORole;
import com.stt.dash.backend.data.entity.User;
import com.stt.dash.backend.service.OAuthorityService;
import com.stt.dash.backend.service.ORoleService;
import com.stt.dash.backend.service.UserFriendlyDataException;
import com.stt.dash.backend.service.UserService;
import com.stt.dash.ui.MainView;
import com.stt.dash.ui.views.BaseFom;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.apache.commons.collections4.ListUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Tag("user-authorities")
@JsModule("./src/views/user/user-authorities.js")
@Route(value = "user-authorities", layout = MainView.class)
@PageTitle("BakeryConst.TITLE_SMS_SHOW_VIEW")
@Secured({Role.ADMIN, "UI_PERMISSIONS"})
public class UserAuthoritiesForm extends BaseFom {
    private final GroupHelper groupHelper = new GroupHelper();
    private final UserService clientService;
    private final OAuthorityService authorityService;
    private final ORoleService roleService;
    @Id("clients")
    private ComboBox<User> userComboBox;
    @Id("checkbox1")
    private CheckboxGroup<OAuthority> checkboxGroup1;
    @Id("checkbox2")
    private CheckboxGroup<OAuthority> checkboxGroup2;
    @Id("checkbox3")
    private CheckboxGroup<OAuthority> checkboxGroup3;
    @Id("save")
    private Button saveButton;
    @Id("select-all")
    private Button selectAllButton;
    @Id("remove-all")
    private Button removeAllButton;
    private final CheckboxGroupHelper groupHelper1;
    private final CheckboxGroupHelper groupHelper2;
    private final CheckboxGroupHelper groupHelper3;

    public UserAuthoritiesForm(CurrentUser currentUser, UserService userService, OAuthorityService authorityService, ORoleService roleService) {
        super(currentUser);
        this.clientService = userService;
        this.authorityService = authorityService;
        this.roleService = roleService;
        userComboBox.addBlurListener((event) -> saveButton.setEnabled(isValid()));
        userComboBox.setWidthFull();
        userComboBox.setItemLabelGenerator(cliente -> {
            return String.format("%s %s - %s", cliente.getFirstName(), cliente.getLastName(), cliente.getEmail());
        });
        List<OAuthority> authorityList = authorityService.findAll();
        this.groupHelper1 = new CheckboxGroupHelper(checkboxGroup1, 1, authorityList);
        this.groupHelper2 = new CheckboxGroupHelper(checkboxGroup2, 2, authorityList);
        this.groupHelper3 = new CheckboxGroupHelper(checkboxGroup3, 3, authorityList);
        groupHelper.addGroup(groupHelper1);
        groupHelper.addGroup(groupHelper2);
        groupHelper.addGroup(groupHelper3);
        /**/
        checkboxGroup1.addValueChangeListener((event) -> {
            saveButton.setEnabled(isValid());
        });

        checkboxGroup2.addValueChangeListener((event) -> {
            saveButton.setEnabled(isValid());
        });

        checkboxGroup3.addValueChangeListener((event) -> {
            saveButton.setEnabled(isValid());
        });
        /**/
        userComboBox.setItems(userService.find(currentUser, Pageable.unpaged()).getContent());
        saveButton.addClickListener(e -> {
            saveButton.setEnabled(false);
            /**/
            List<OAuthority> authorities = groupHelper.getSelectedItems();
            /**/
            String roleName = "rol-" + userComboBox.getValue().getEmail();
            ORole role = roleService.findByRolName(roleName);
            if (Objects.isNull(role)) {
                role = new ORole();
                role.setRolName(roleName);
            }
            role.setAuthorities(new HashSet<>(authorities));
//            role = roleService.save(userComboBox.getValue(), role);
//            HashSet<ORole> roleHashSet = new HashSet<>();
//            roleHashSet.add(role);
//            userComboBox.getValue().setRoles(roleHashSet);
            try {
                User userSaved = roleService.saveRolToUser(currentUser, userComboBox.getValue(), role);
//                User userSaved = userService.save(currentUser.getUser(), userComboBox.getValue());
                if (userSaved != null) {
                    showNotificationSuccess("Guardado Correctamente", false);
                    updateUserComboItems(userSaved);
                    userComboBox.setValue(userSaved);
                }
            } catch (UserFriendlyDataException ufde) {
                showNotificationError(ufde.getMessage(), true);
            } finally {
                saveButton.setEnabled(true);
            }
        });
        userComboBox.addValueChangeListener(event -> {
            saveButton.setEnabled(isValid());
            if (!event.isFromClient() || event.getValue() == null) {
                return;
            }
            groupHelper.clear();
            String roleName = "rol-" + event.getValue().getEmail();
            ORole userRole = roleService.findByRolName(roleName);
            if (Objects.nonNull(userRole)) {
                groupHelper.select(userRole.getAuthorities());
            }

        });
        saveButton.setEnabled(false);
        selectAllButton.addClickListener(e -> {
            groupHelper.deselectAll();
            groupHelper.selectAll();
        });
        removeAllButton.addClickListener(e -> {
            groupHelper.deselectAll();
        });
    }

    private void updateUserComboItems(User userSaved) {
        ListDataProvider<User> dataProvider = (ListDataProvider<User>) userComboBox.getDataProvider();
        boolean remove = dataProvider.getItems().remove(userComboBox.getValue());
        dataProvider.getItems().add(userSaved);
        userComboBox.setItems(dataProvider.getItems());
    }

    private boolean isValid() {
        return userComboBox.getValue() != null && groupHelper.thereAreSelectedItems();
    }

    private class GroupHelper {
        List<CheckboxGroupHelper> list = new ArrayList<>();

        public void addGroup(CheckboxGroupHelper groupHelper) {
            list.add(groupHelper);
        }

        public void clear() {
            list.forEach(CheckboxGroupHelper::clear);
        }

        public boolean thereAreSelectedItems() {
            return list.stream().anyMatch((groupHelper) -> groupHelper.checkboxGroup.getSelectedItems().size() > 0);
        }

        public List<OAuthority> getSelectedItems() {
            return Stream
                    .of(checkboxGroup1.getSelectedItems(), checkboxGroup2.getSelectedItems(), checkboxGroup3.getSelectedItems())
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
        }

        public void select(Set<OAuthority> userAuthoritiesSet) {
            list.stream().forEach((groupHelper) -> groupHelper.checkboxGroup.select(groupHelper.getAuthoritiesToSelect(userAuthoritiesSet)));
        }

        public void selectAll() {
            list.stream().forEach((groupHelper) -> groupHelper.checkboxGroup.select(groupHelper.getAuthoritiesToSelect()));
        }

        public void deselectAll() {
            list.stream().forEach((groupHelper) -> groupHelper.checkboxGroup.deselectAll());
        }
    }

    private class CheckboxGroupHelper {
        private final List<String> authGroup1 = Arrays.asList("Dashboard", "Evolución Operadora", "Evolución Cliente", "Tráfico por Cliente", "Crear Masivos", "Programar Masivos", "Balance");
        private final List<String> authGroup2 = Arrays.asList("Auditoria", "Búsqueda de mensaje", "Ver mensaje de Texto");
        private final List<String> authGroup3 = Arrays.asList("Usuarios", "Permisos");
        private final List<OAuthority> authorityList;
        private final CheckboxGroup<OAuthority> checkboxGroup;
        private final List<OAuthority> myAuthorityList;
        private List<String> groupItemList;

        public CheckboxGroupHelper(CheckboxGroup<OAuthority> checkboxGroup, int group, List<OAuthority> authorityList) {
            this.authorityList = authorityList;
            this.checkboxGroup = checkboxGroup;
            switch (group) {
                case 1:
                    this.groupItemList = authGroup1;
                    break;
                case 2:
                    this.groupItemList = authGroup2;
                    break;
                case 3:
                    this.groupItemList = authGroup3;
                    break;
            }
            myAuthorityList = fillAuthGroupList();
            fillAuthCheckboxGroup(myAuthorityList);
        }

        private List<OAuthority> fillAuthGroupList() {
            return authorityList
                    .stream()
                    .filter(authority -> groupItemList.contains(authority.getAuthDesc()))
                    .collect(Collectors.toList());
        }

        private void fillAuthCheckboxGroup(List<OAuthority> myAuthorityList) {
            checkboxGroup.setItems(myAuthorityList);
            checkboxGroup.setItemLabelGenerator(OAuthority::getAuthDesc);
        }

        public void clear() {
            checkboxGroup.clear();
        }

        public void deselectAll() {
            checkboxGroup.deselectAll();
        }

        /**
         * Retorna la lista de aquellos authorities que pertenence al grupo actual
         *
         * @param allUserAuthorities todos los authorities del usuario actual.
         * @return
         */
        public List<OAuthority> getAuthoritiesToSelect(Set<OAuthority> allUserAuthorities) {
            return ListUtils.retainAll(allUserAuthorities, myAuthorityList);
        }

        public List<OAuthority> getAuthoritiesToSelect() {
            return myAuthorityList;
        }
    }
}
