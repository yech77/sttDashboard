package com.stt.dash.ui.views.admin.users;

import com.stt.dash.app.security.CurrentUser;
import com.stt.dash.backend.data.entity.OAuthority;
import com.stt.dash.backend.data.entity.ORole;
import com.stt.dash.backend.data.entity.User;
import com.stt.dash.backend.service.OAuthorityService;
import com.stt.dash.backend.service.ORoleService;
import com.stt.dash.backend.service.UserService;
import com.stt.dash.ui.MainView;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import liquibase.pro.packaged.T;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Tag("user-authorities")
@JsModule("./src/views/user/user-authorities.ts")
@Route(value = "user-authorities", layout = MainView.class)
@PageTitle("BakeryConst.TITLE_SMS_SHOW_VIEW")
public class UserAuthoritiesForm extends LitTemplate {
    @Id("clients")
    private ComboBox<User> userComboBox;
    @Id("checkbox1")
    private CheckboxGroup<OAuthority> checkboxGroup1;
    @Id("checkbox2")
    private CheckboxGroup<OAuthority> checkboxGroup2;
    @Id("save")
    private Button saveButton;

    private final UserService clientService;
    private final OAuthorityService authorityService;
    private final ORoleService roleService;

    public UserAuthoritiesForm(CurrentUser currentUser, UserService userService, OAuthorityService authorityService, ORoleService roleService) {
        this.clientService = userService;
        this.authorityService = authorityService;
        this.roleService = roleService;
        userComboBox.setWidthFull();
        userComboBox.setItemLabelGenerator(cliente -> {
            return String.format("%s - %s", cliente.getFirstName(), cliente.getLastName());
        });
        List<OAuthority> authorityList = authorityService.findAll();
        int midIndex = (authorityList.size() - 1) / 2;
        List<List<OAuthority>> lists = new ArrayList<>(
                authorityList.stream()
                        .collect(Collectors.partitioningBy(s -> authorityList.indexOf(s) > midIndex))
                        .values()
        );
        checkboxGroup1.setItems(lists.get(0));
        checkboxGroup2.setItems(lists.get(1));
        checkboxGroup1.setItemLabelGenerator(OAuthority::getAuthDesc);
        checkboxGroup2.setItemLabelGenerator(OAuthority::getAuthDesc);
        userComboBox.setItems(userService.find(currentUser, Pageable.unpaged()).getContent());
        saveButton.addClickListener(e -> {
            /**/
            List<OAuthority> authorities = new ArrayList<>();
            authorities.addAll(checkboxGroup1.getSelectedItems());
            authorities.addAll(checkboxGroup2.getSelectedItems());
            /**/
            String roleName = "rol-" + currentUser.getUser().getEmail();
            ORole role = roleService.findByRolName(roleName);
            if (Objects.isNull(role)) {
                role = new ORole();
                role.setRolName(roleName);
            }
            role.setAuthorities(new HashSet<>(authorities));
            role = roleService.save(currentUser.getUser(), role);
            HashSet<ORole> roleHashSet = new HashSet<>();
            roleHashSet.add(role);
            userComboBox.getValue().setRoles(roleHashSet);
            userService.save(currentUser.getUser(), userComboBox.getValue());
        });
    }
}
