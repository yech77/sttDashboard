package com.stt.dash.app;

import com.stt.dash.backend.data.OrderState;
import com.stt.dash.backend.data.Role;
import com.stt.dash.backend.data.entity.*;
import com.stt.dash.backend.repositories.*;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.PostConstruct;
import java.io.Reader;
import java.io.StringReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Supplier;

@SpringComponent
public class DataGenerator implements HasLogger {

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
            String[] s = new String[AUTH.values().length];
            for (int i = 0; i < AUTH.values().length; i++) {
                s[i] = AUTH.values()[i].name();
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

    private static final String[] FILLING = new String[]{"Strawberry", "Chocolate", "Blueberry", "Raspberry",
            "Vanilla"};
    private static final String[] TYPE = new String[]{"Cake", "Pastry", "Tart", "Muffin", "Biscuit", "Bread", "Bagel",
            "Bun", "Brownie", "Cookie", "Cracker", "Cheese Cake"};
    private static final String[] FIRST_NAME = new String[]{"Ori", "Amanda", "Octavia", "Laurel", "Lael", "Delilah",
            "Jason", "Skyler", "Arsenio", "Haley", "Lionel", "Sylvia", "Jessica", "Lester", "Ferdinand", "Elaine",
            "Griffin", "Kerry", "Dominique"};
    private static final String[] LAST_NAME = new String[]{"Carter", "Castro", "Rich", "Irwin", "Moore", "Hendricks",
            "Huber", "Patton", "Wilkinson", "Thornton", "Nunez", "Macias", "Gallegos", "Blevins", "Mejia", "Pickett",
            "Whitney", "Farmer", "Henry", "Chen", "Macias", "Rowland", "Pierce", "Cortez", "Noble", "Howard", "Nixon",
            "Mcbride", "Leblanc", "Russell", "Carver", "Benton", "Maldonado", "Lyons"};

    private final Random random = new Random(1L);

    private OrderRepository orderRepository;
    private UserRepository user_repo;
    private ProductRepository productRepository;
    private PickupLocationRepository pickupLocationRepository;
    private PasswordEncoder passwordEncoder;
    private OAuthorityRepository oauth_repo;
    private ORoleRepository orole_repo;
    private ClientRepository client_repo;

    @Autowired
    public DataGenerator(OrderRepository orderRepository, UserRepository user_repo,
                         ProductRepository productRepository, PickupLocationRepository pickupLocationRepository,
                         PasswordEncoder passwordEncoder,
                         OAuthorityRepository oauth_repo,
                         ORoleRepository orole_repo,
                         ClientRepository client_repo) {
        this.orderRepository = orderRepository;
        this.user_repo = user_repo;
        this.productRepository = productRepository;
        this.pickupLocationRepository = pickupLocationRepository;
        this.passwordEncoder = passwordEncoder;
        this.oauth_repo = oauth_repo;
        this.orole_repo = orole_repo;
        this.client_repo = client_repo;
    }

    @PostConstruct
    public void loadData() {
        getLogger().info("********************* * * * * * * * * * * * " + passwordEncoder.encode("admin"));
        getLogger().info("********************* * * * * * * * * * * * " + passwordEncoder.encode("admin"));
        /**/
        if (true) {
            getLogger().info("Using existing database");
            return;
        }

        /**/
        if (oauth_repo.count() < 1) {
            OAuthority oauth = new OAuthority();
            /* UI */
            oauth = new OAuthority();
            oauth.setAuthName(AUTH.UI_AUDIT.name());
            oauth.setAuthDesc("Permite ver la Pantalla AUDITEVEN");
            oauth_repo.saveAndFlush(oauth);

            oauth = new OAuthority();
            oauth.setAuthName(AUTH.UI_ROL.name());
            oauth.setAuthDesc("Permite ver la Pantalla ROL");
            oauth_repo.save(oauth);

            oauth = new OAuthority();
            oauth.setAuthName(AUTH.UI_USER.name());
            oauth.setAuthDesc("Permite ver la Pantalla USER");
            oauth_repo.save(oauth);
            oauth = new OAuthority();
            oauth.setAuthName(AUTH.UI_EVOLUTION_CARRIER.name());
            oauth.setAuthDesc("Permite ver la Pantalla AUDITEVEN");
            oauth_repo.save(oauth);

            oauth = new OAuthority();
            oauth.setAuthName(AUTH.UI_EVOLUTION_CLIENT.name());
            oauth.setAuthDesc("Permite ver la Pantalla ROL");
            oauth_repo.save(oauth);

            oauth = new OAuthority();
            oauth.setAuthName(AUTH.UI_EVOLUTION_SYSTEMID.name());
            oauth.setAuthDesc("Permite ver la Pantalla USER");
            oauth_repo.save(oauth);
            oauth = new OAuthority();
            oauth.setAuthName(AUTH.UI_SEARCH_SMS.name());
            oauth.setAuthDesc("Permite ver la Pantalla BUSCAR SMS");
            oauth_repo.save(oauth);
            oauth = new OAuthority();
            oauth.setAuthName(AUTH.UI_TRAFFIC_SMS.name());
            oauth.setAuthDesc("Permite ver la Pantalla TRAFICO");
            oauth_repo.save(oauth);
            oauth = new OAuthority();
            oauth.setAuthName(AUTH.UI_AGENDA_SMS.name());
            oauth.setAuthDesc("Permite ver la Pantalla AGENDA");
            oauth_repo.save(oauth);
            oauth = new OAuthority();
            oauth.setAuthName(AUTH.UI_PROGRAM_SMS.name());
            oauth.setAuthDesc("Permite ver la Pantalla PROGRAMAR SMS");
            oauth_repo.save(oauth);
        }

        if (orole_repo.count() < 1) {
            ORole orole = new ORole();
            Set<OAuthority> o = new HashSet<>();
            /**/
            orole = new ORole();
            orole.setRolName(ROL.AGENDAR_SMS.name());
            o = new HashSet<>();
            o.add(oauth_repo.findByAuthName(AUTH.UI_AGENDA_SMS.name()).get(0));
            orole.setAuthorities(o);
            orole_repo.saveAndFlush(orole);
            /**/
            orole = new ORole();
            orole.setRolName(ROL.AUDITORIA.name());
            o = new HashSet<>();
            o.add(oauth_repo.findByAuthName(AUTH.UI_AUDIT.name()).get(0));
            orole.setAuthorities(o);
            orole_repo.save(orole);
            /**/
            orole = new ORole();
            orole.setRolName(ROL.BUSQUEDA_SMS.name());
            o = new HashSet<>();
            o.add(oauth_repo.findByAuthName(AUTH.UI_SEARCH_SMS.name()).get(0));
            orole.setAuthorities(o);
            orole_repo.save(orole);
            /**/
            orole = new ORole();
            orole.setRolName(ROL.EVOLUCION_CLIENTE.name());
            o = new HashSet<>();
            o.add(oauth_repo.findByAuthName(AUTH.UI_EVOLUTION_CLIENT.name()).get(0));
            orole.setAuthorities(o);
            orole_repo.save(orole);
            /**/
            orole = new ORole();
            orole.setRolName(ROL.EVOLUCION_OPERADORA.name());
            o = new HashSet<>();
            o.add(oauth_repo.findByAuthName(AUTH.UI_EVOLUTION_CARRIER.name()).get(0));
            orole.setAuthorities(o);
            orole_repo.save(orole);
            /**/
            orole = new ORole();
            orole.setRolName(ROL.EVOLUCION_PASAPORTES.name());
            o = new HashSet<>();
            o.add(oauth_repo.findByAuthName(AUTH.UI_EVOLUTION_SYSTEMID.name()).get(0));
            orole.setAuthorities(o);
            orole_repo.save(orole);
            /**/
            orole = new ORole();
            orole.setRolName(ROL.PROGRAMAR_SMS.name());
            o = new HashSet<>();
            o.add(oauth_repo.findByAuthName(AUTH.UI_PROGRAM_SMS.name()).get(0));
            orole.setAuthorities(o);
            orole_repo.save(orole);
            /**/
            orole = new ORole();
            orole.setRolName(ROL.ROLES.name());
            o = new HashSet<>();
            o.add(oauth_repo.findByAuthName(AUTH.UI_ROL.name()).get(0));
            orole.setAuthorities(o);
            orole_repo.save(orole);
            /**/
            orole = new ORole();
            orole.setRolName(ROL.TRAFICO_SMS.name());
            o = new HashSet<>();
            o.add(oauth_repo.findByAuthName(AUTH.UI_TRAFFIC_SMS.name()).get(0));
            orole.setAuthorities(o);
            orole_repo.save(orole);
            /**/
            orole = new ORole();
            orole.setRolName(ROL.USUARIOS.name());
            o = new HashSet<>();
            o.add(oauth_repo.findByAuthName(AUTH.UI_USER.name()).get(0));
            orole.setAuthorities(o);
            orole_repo.save(orole);
        }
        try (Reader in = new StringReader("43,TEST00,NOMBRE TEST00,ALIADO,corre@test.com\n" +
                "49,TEST02,RAZON TEST02,ALIADO,itjoye@yahoo.com\n" +
                "53,TEST03,\"INVERSIONES JLC 20-20, C.A\",EMPRESAS,aarongonzalezv@hotmail.com\n" +
                "59,TESTGB01,\"GLERYXA J. BANDRES B, FP\",EMPRESAS,gleryxab@gmail.com")) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .parse(in);
            for (CSVRecord record : records) {
                System.out.println("*******************" + record.get(0));
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        /**
         * ***************
         * LOS USUARIOS LOS VA A CREAR SIEMPRE. SI EXISTEN LOS MODIFICA.
         *
         * ***************
         */
        boolean doUser = true;
        if (doUser) {
            User ouser = user_repo.findByEmailIgnoreCase("admin@soltextech.com");
            if (ouser == null) {
                ouser = new User();
            } else {
                System.out.println("** FOUNDED " + ouser.getEmail());
            }
            ouser.setFirstName("Administrador");
            ouser.setLastName("");
            ouser.setEmail("admin@soltextech.com");
            ouser.setUserType(User.OUSER_TYPE.HAS);
            ouser.setUserTypeOrd(User.OUSER_TYPE_ORDINAL.COMERCIAL);
            ouser.setPasswordHash(passwordEncoder.encode("admin"));
            ouser.setLocked(true);
            List<Client> c = client_repo.findAll();
            if (c != null) {
                ouser.setClients(new HashSet<>(c));
            }

            /*Roles*/
            List<ORole> r1 = orole_repo.findAll();
            if (r1 != null) {
                r1 = new ArrayList<>();
            }
            ouser.setRoles(new HashSet<>(r1));
            user_repo.saveAndFlush(ouser);
            /**/
            ouser = user_repo.findByEmailIgnoreCase("enavas@soltextech.com");
            if (ouser == null) {
                ouser = new User();
            } else {
                System.out.println("** FOUNDED " + ouser.getEmail());
            }
            ouser.setFirstName("Elizabeth");
            ouser.setLastName("Navas");
            ouser.setEmail("enavas@soltextech.com");
            ouser.setUserType(User.OUSER_TYPE.HAS);
            ouser.setUserTypeOrd(User.OUSER_TYPE_ORDINAL.COMERCIAL);
            ouser.setPasswordHash(passwordEncoder.encode("enavas"));
            c = client_repo.findAll();
            if (c != null) {
                ouser.setClients(new HashSet<>(c));
            }

            /*Roles*/
            Set<ORole> r = new HashSet<>();
            r.add(orole_repo.findByRolName(ROL.AGENDAR_SMS.name()).get(0));
            r.add(orole_repo.findByRolName(ROL.AUDITORIA.name()).get(0));
            r.add(orole_repo.findByRolName(ROL.BUSQUEDA_SMS.name()).get(0));
            r.add(orole_repo.findByRolName(ROL.EVOLUCION_CLIENTE.name()).get(0));
            r.add(orole_repo.findByRolName(ROL.EVOLUCION_OPERADORA.name()).get(0));
            r.add(orole_repo.findByRolName(ROL.EVOLUCION_PASAPORTES.name()).get(0));
            r.add(orole_repo.findByRolName(ROL.PROGRAMAR_SMS.name()).get(0));
            r.add(orole_repo.findByRolName(ROL.ROLES.name()).get(0));
            r.add(orole_repo.findByRolName(ROL.TRAFICO_SMS.name()).get(0));
            r.add(orole_repo.findByRolName(ROL.USUARIOS.name()).get(0));
            ouser.setRoles(r);
            /* Gleryxa fue creada por enavas*/
            ouser.setUserParent(user_repo.findByEmailIgnoreCase("admin@soltextech.com"));
            user_repo.saveAndFlush(ouser);
            /**/

            ouser = user_repo.findByEmailIgnoreCase("gbandres@soltextech.com");
            if (ouser == null) {
                ouser = new User();
            } else {
                System.out.println("** FOUNDED " + ouser.getEmail());
            }
            ouser.setFirstName("Gleryxa");
            ouser.setLastName("Bandres");
            ouser.setEmail("gbandres@soltextech.com");
            ouser.setUserType(User.OUSER_TYPE.HAS);
            ouser.setUserTypeOrd(User.OUSER_TYPE_ORDINAL.COMERCIAL);
            ouser.setPasswordHash(passwordEncoder.encode("gbandres"));

            if (c != null) {
                ouser.setClients(new HashSet<>(c));
            }
            /*Roles*/
            r = new HashSet<>();
            r.add(orole_repo.findByRolName(ROL.AGENDAR_SMS.name()).get(0));
            r.add(orole_repo.findByRolName(ROL.AUDITORIA.name()).get(0));
            r.add(orole_repo.findByRolName(ROL.BUSQUEDA_SMS.name()).get(0));
            r.add(orole_repo.findByRolName(ROL.EVOLUCION_CLIENTE.name()).get(0));
            r.add(orole_repo.findByRolName(ROL.EVOLUCION_OPERADORA.name()).get(0));
            r.add(orole_repo.findByRolName(ROL.EVOLUCION_PASAPORTES.name()).get(0));
            r.add(orole_repo.findByRolName(ROL.PROGRAMAR_SMS.name()).get(0));
            r.add(orole_repo.findByRolName(ROL.ROLES.name()).get(0));
            r.add(orole_repo.findByRolName(ROL.TRAFICO_SMS.name()).get(0));
            r.add(orole_repo.findByRolName(ROL.USUARIOS.name()).get(0));
            ouser.setRoles(r);

            /* Gleryxa fue creada por enavas*/
            ouser.setUserParent(user_repo.findByEmailIgnoreCase("enavas@soltextech.com"));
            user_repo.saveAndFlush(ouser);
            /**
             * ***************
             */

            ouser = user_repo.findByEmailIgnoreCase("lsuarez@soltextech.com");
            if (ouser == null) {
                ouser = new User();
            } else {
                System.out.println("** FOUNDED " + ouser.getEmail());
            }

            ouser.setFirstName("Luis");
            ouser.setLastName("Suarez");
            ouser.setEmail("lsuarez@soltextech.com");
            ouser.setUserType(User.OUSER_TYPE.HAS);
            ouser.setUserTypeOrd(User.OUSER_TYPE_ORDINAL.COMERCIAL);
            ouser.setPasswordHash(passwordEncoder.encode("lsuarez"));

            if (c != null) {
                ouser.setClients(new HashSet<>(c));
            }
            /*Roles*/
            r = new HashSet<>();
            r.add(orole_repo.findByRolName(ROL.AGENDAR_SMS.name()).get(0));
            r.add(orole_repo.findByRolName(ROL.AUDITORIA.name()).get(0));
            r.add(orole_repo.findByRolName(ROL.BUSQUEDA_SMS.name()).get(0));
            r.add(orole_repo.findByRolName(ROL.EVOLUCION_CLIENTE.name()).get(0));
            r.add(orole_repo.findByRolName(ROL.EVOLUCION_OPERADORA.name()).get(0));
            r.add(orole_repo.findByRolName(ROL.EVOLUCION_PASAPORTES.name()).get(0));
            r.add(orole_repo.findByRolName(ROL.PROGRAMAR_SMS.name()).get(0));
            r.add(orole_repo.findByRolName(ROL.ROLES.name()).get(0));
            r.add(orole_repo.findByRolName(ROL.TRAFICO_SMS.name()).get(0));
            r.add(orole_repo.findByRolName(ROL.USUARIOS.name()).get(0));
            ouser.setRoles(r);

            /* Luis fue creada por enavas*/
            ouser.setUserParent(user_repo.findByEmailIgnoreCase("enavas@soltextech.com"));
            user_repo.saveAndFlush(ouser);
            /**
             *
             * /**
             * ***************
             */
            ouser = user_repo.findByEmailIgnoreCase("dsolorzano@soltextech.com");
            if (ouser == null) {
                ouser = new User();
            } else {
                System.out.println("** FOUNDED " + ouser.getEmail());
            }
            ouser.setFirstName("Denny");
            ouser.setLastName("Solorzano");
            ouser.setEmail("dsolorzano@soltextech.com");
            ouser.setUserType(User.OUSER_TYPE.HAS);
            ouser.setUserTypeOrd(User.OUSER_TYPE_ORDINAL.COMERCIAL);
            ouser.setPasswordHash(passwordEncoder.encode("dsolorzano"));

            if (c != null) {
                ouser.setClients(new HashSet<>(c));
            }

            /*Roles*/
            r = new HashSet<>();
            r.add(orole_repo.findByRolName(ROL.AGENDAR_SMS.name()).get(0));
            r.add(orole_repo.findByRolName(ROL.AUDITORIA.name()).get(0));
            r.add(orole_repo.findByRolName(ROL.BUSQUEDA_SMS.name()).get(0));
            r.add(orole_repo.findByRolName(ROL.EVOLUCION_CLIENTE.name()).get(0));
            r.add(orole_repo.findByRolName(ROL.EVOLUCION_OPERADORA.name()).get(0));
            r.add(orole_repo.findByRolName(ROL.EVOLUCION_PASAPORTES.name()).get(0));
            r.add(orole_repo.findByRolName(ROL.PROGRAMAR_SMS.name()).get(0));
            r.add(orole_repo.findByRolName(ROL.ROLES.name()).get(0));
            r.add(orole_repo.findByRolName(ROL.TRAFICO_SMS.name()).get(0));
            r.add(orole_repo.findByRolName(ROL.USUARIOS.name()).get(0));
            ouser.setRoles(r);

            /* Denny fue creada por enavas*/
            ouser.setUserParent(user_repo.findByEmailIgnoreCase("enavas@soltextech.com"));
            user_repo.saveAndFlush(ouser);
        }

        getLogger().info("Generating demo data");

        getLogger().info("... generating users");
        User baker = createBaker(user_repo, passwordEncoder);
        User barista = createBarista(user_repo, passwordEncoder);
        createAdmin(user_repo, passwordEncoder);
        // A set of products without constrains that can be deleted
        createDeletableUsers(user_repo, passwordEncoder);

        getLogger().info("... generating products");
        // A set of products that will be used for creating orders.
        Supplier<Product> productSupplier = createProducts(productRepository, 8);
        // A set of products without relationships that can be deleted
        createProducts(productRepository, 4);

        getLogger().info("... generating pickup locations");
        Supplier<PickupLocation> pickupLocationSupplier = createPickupLocations(pickupLocationRepository);

        getLogger().info("... generating orders");
        createOrders(orderRepository, productSupplier, pickupLocationSupplier, barista, baker);

        getLogger().info("Generated demo data");
    }

    private void fillCustomer(Customer customer) {
        String first = getRandom(FIRST_NAME);
        String last = getRandom(LAST_NAME);
        customer.setFullName(first + " " + last);
        customer.setPhoneNumber(getRandomPhone());
        if (random.nextInt(10) == 0) {
            customer.setDetails("Very important customer");
        }
    }

    private String getRandomPhone() {
        return "+1-555-" + String.format("%04d", random.nextInt(10000));
    }

    private void createOrders(OrderRepository orderRepo, Supplier<Product> productSupplier,
                              Supplier<PickupLocation> pickupLocationSupplier, User barista, User baker) {
        int yearsToInclude = 2;
        LocalDate now = LocalDate.now();
        LocalDate oldestDate = LocalDate.of(now.getYear() - yearsToInclude, 1, 1);
        LocalDate newestDate = now.plusMonths(1L);

        // Create first today's order
        Order order = createOrder(productSupplier, pickupLocationSupplier, barista, baker, now);
        order.setDueTime(LocalTime.of(8, 0));
        order.setHistory(order.getHistory().subList(0, 1));
        order.setItems(order.getItems().subList(0, 1));
        orderRepo.save(order);

        for (LocalDate dueDate = oldestDate; dueDate.isBefore(newestDate); dueDate = dueDate.plusDays(1)) {
            // Create a slightly upwards trend - everybody wants to be
            // successful
            int relativeYear = dueDate.getYear() - now.getYear() + yearsToInclude;
            int relativeMonth = relativeYear * 12 + dueDate.getMonthValue();
            double multiplier = 1.0 + 0.03 * relativeMonth;
            int ordersThisDay = (int) (random.nextInt(10) + 1 * multiplier);
            for (int i = 0; i < ordersThisDay; i++) {
                orderRepo.save(createOrder(productSupplier, pickupLocationSupplier, barista, baker, dueDate));
            }
        }
    }

    private Order createOrder(Supplier<Product> productSupplier, Supplier<PickupLocation> pickupLocationSupplier,
                              User barista, User baker, LocalDate dueDate) {
        Order order = new Order(barista);

        fillCustomer(order.getCustomer());
        order.setPickupLocation(pickupLocationSupplier.get());
        order.setDueDate(dueDate);
        order.setDueTime(getRandomDueTime());
        order.changeState(barista, getRandomState(order.getDueDate()));

        int itemCount = random.nextInt(3);
        List<OrderItem> items = new ArrayList<>();
        for (int i = 0; i <= itemCount; i++) {
            OrderItem item = new OrderItem();
            Product product;
            do {
                product = productSupplier.get();
            } while (containsProduct(items, product));
            item.setProduct(product);
            item.setQuantity(random.nextInt(10) + 1);
            if (random.nextInt(5) == 0) {
                if (random.nextBoolean()) {
                    item.setComment("Lactose free");
                } else {
                    item.setComment("Gluten free");
                }
            }
            items.add(item);
        }
        order.setItems(items);

        order.setHistory(createOrderHistory(order, barista, baker));

        return order;
    }

    private List<HistoryItem> createOrderHistory(Order order, User barista, User baker) {
        ArrayList<HistoryItem> history = new ArrayList<>();
        HistoryItem item = new HistoryItem(barista, "Order placed");
        item.setNewState(OrderState.NEW);
        LocalDateTime orderPlaced = order.getDueDate().minusDays(random.nextInt(5) + 2L).atTime(random.nextInt(10) + 7,
                00);
        item.setTimestamp(orderPlaced);
        history.add(item);
        if (order.getState() == OrderState.CANCELLED) {
            item = new HistoryItem(barista, "Order cancelled");
            item.setNewState(OrderState.CANCELLED);
            item.setTimestamp(orderPlaced.plusDays(random
                    .nextInt((int) orderPlaced.until(order.getDueDate().atTime(order.getDueTime()), ChronoUnit.DAYS))));
            history.add(item);
        } else if (order.getState() == OrderState.CONFIRMED || order.getState() == OrderState.DELIVERED
                || order.getState() == OrderState.PROBLEM || order.getState() == OrderState.READY) {
            item = new HistoryItem(baker, "Order confirmed");
            item.setNewState(OrderState.CONFIRMED);
            item.setTimestamp(orderPlaced.plusDays(random.nextInt(2)).plusHours(random.nextInt(5)));
            history.add(item);

            if (order.getState() == OrderState.PROBLEM) {
                item = new HistoryItem(baker, "Can't make it. Did not get any ingredients this morning");
                item.setNewState(OrderState.PROBLEM);
                item.setTimestamp(order.getDueDate().atTime(random.nextInt(4) + 4, 0));
                history.add(item);
            } else if (order.getState() == OrderState.READY || order.getState() == OrderState.DELIVERED) {
                item = new HistoryItem(baker, "Order ready for pickup");
                item.setNewState(OrderState.READY);
                item.setTimestamp(order.getDueDate().atTime(random.nextInt(2) + 8, random.nextBoolean() ? 0 : 30));
                history.add(item);
                if (order.getState() == OrderState.DELIVERED) {
                    item = new HistoryItem(baker, "Order delivered");
                    item.setNewState(OrderState.DELIVERED);
                    item.setTimestamp(order.getDueDate().atTime(order.getDueTime().minusMinutes(random.nextInt(120))));
                    history.add(item);
                }
            }
        }

        return history;
    }

    private boolean containsProduct(List<OrderItem> items, Product product) {
        for (OrderItem item : items) {
            if (item.getProduct() == product) {
                return true;
            }
        }
        return false;
    }

    private LocalTime getRandomDueTime() {
        int time = 8 + 4 * random.nextInt(3);

        return LocalTime.of(time, 0);
    }

    private OrderState getRandomState(LocalDate due) {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        LocalDate twoDays = today.plusDays(2);

        if (due.isBefore(today)) {
            if (random.nextDouble() < 0.9) {
                return OrderState.DELIVERED;
            } else {
                return OrderState.CANCELLED;
            }
        } else {
            if (due.isAfter(twoDays)) {
                return OrderState.NEW;
            } else if (due.isAfter(tomorrow)) {
                // in 1-2 days
                double resolution = random.nextDouble();
                if (resolution < 0.8) {
                    return OrderState.NEW;
                } else if (resolution < 0.9) {
                    return OrderState.PROBLEM;
                } else {
                    return OrderState.CANCELLED;
                }
            } else {
                double resolution = random.nextDouble();
                if (resolution < 0.6) {
                    return OrderState.READY;
                } else if (resolution < 0.8) {
                    return OrderState.DELIVERED;
                } else if (resolution < 0.9) {
                    return OrderState.PROBLEM;
                } else {
                    return OrderState.CANCELLED;
                }
            }

        }
    }

    private <T> T getRandom(T[] array) {
        return array[random.nextInt(array.length)];
    }

    private Supplier<PickupLocation> createPickupLocations(PickupLocationRepository pickupLocationRepository) {
        List<PickupLocation> pickupLocations = Arrays.asList(
                pickupLocationRepository.save(createPickupLocation("Store")),
                pickupLocationRepository.save(createPickupLocation("Bakery")));
        return () -> pickupLocations.get(random.nextInt(pickupLocations.size()));
    }

    private PickupLocation createPickupLocation(String name) {
        PickupLocation store = new PickupLocation();
        store.setName(name);
        return store;
    }

    private Supplier<Product> createProducts(ProductRepository productsRepo, int numberOfItems) {
        List<Product> products = new ArrayList<>();
        for (int i = 0; i < numberOfItems; i++) {
            Product product = new Product();
            product.setName(getRandomProductName());
            double doublePrice = 2.0 + random.nextDouble() * 100.0;
            product.setPrice((int) (doublePrice * 100.0));
            products.add(productsRepo.save(product));
        }
        return () -> {
            double cutoff = 2.5;
            double g = random.nextGaussian();
            g = Math.min(cutoff, g);
            g = Math.max(-cutoff, g);
            g += cutoff;
            g /= (cutoff * 2.0);
            return products.get((int) (g * (products.size() - 1)));
        };
    }

    private String getRandomProductName() {
        String firstFilling = getRandom(FILLING);
        String name;
        if (random.nextBoolean()) {
            String secondFilling;
            do {
                secondFilling = getRandom(FILLING);
            } while (secondFilling.equals(firstFilling));

            name = firstFilling + " " + secondFilling;
        } else {
            name = firstFilling;
        }
        name += " " + getRandom(TYPE);

        return name;
    }

    private User createBaker(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return userRepository.save(
                createUser("baker@vaadin.com", "Heidi", "Carter", passwordEncoder.encode("baker"), Role.BAKER, false));
    }

    private User createBarista(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return userRepository.save(createUser("barista@vaadin.com", "Malin", "Castro",
                passwordEncoder.encode("barista"), Role.BARISTA, true));
    }

    private User createAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return userRepository.save(
                createUser("admin@vaadin.com", "G??ran", "Rich", passwordEncoder.encode("admin"), Role.ADMIN, true));
    }

    private void createDeletableUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        userRepository.save(
                createUser("peter@vaadin.com", "Peter", "Bush", passwordEncoder.encode("peter"), Role.BARISTA, false));
        userRepository
                .save(createUser("mary@vaadin.com", "Mary", "Ocon", passwordEncoder.encode("mary"), Role.BAKER, true));
    }

    private User createUser(String email, String firstName, String lastName, String passwordHash, String role,
                            boolean locked) {
        User user = new User();
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPasswordHash(passwordHash);
        user.setRole(role);
        user.setLocked(locked);
        return user;
    }
}
