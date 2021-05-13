package com.stt.dash.backend.data.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
public class SystemId extends AbstractEntity{

    public enum PaymentMode {
        POSTPAGO, PREPAGO
    }

    //    public enum Status {
//        ON, OFF
//    }
    @ManyToOne
    @JoinColumn(name = "client_id")
    Client client;

    //    @OneToMany(mappedBy = "campaing_systemid", fetch = FetchType.LAZY)
//    private List<Campaing> campaings = new LinkedList<>();
//    @OneToOne(mappedBy = "systemid", fetch = FetchType.LAZY)
//    private SystemIdBalance systemid_balance;
    @NotEmpty
    @NotNull
    @Size(min = 3, max = 20)
    @Column(length = 20, unique = true)
    private String systemId = "";

    //    @NotEmpty
//    @NotNull
////    @Size(min = 3, max = 20)
//    @Column(length = 10)
//    private String password = "";
    @Enumerated(EnumType.STRING)
    @NotNull
    private PaymentMode paymentType;

    //    @Enumerated(EnumType.STRING)
//    private Status systemid_status;
    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    //    public String getPassword() {
//        return password;
//    }
//    public void setPassword(String password) {
//        this.password = password;
//    }
    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public PaymentMode getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentMode payment_type) {
        this.paymentType = payment_type;
    }

    //    public List<Campaing> getCampaings() {
//        return campaings;
//    }
//
//    public void setCampaings(List<Campaing> campaings) {
//        this.campaings = campaings;
//    }
//    public Status getSystemid_status() {
//        return systemid_status;
//    }
//
//    public void setSystemid_status(Status systemid_status) {
//        this.systemid_status = systemid_status;
//    }
//    public SystemIdBalance getSystemid_balance() {
//        return systemid_balance;
//    }
//
//    public void setSystemid_balance(SystemIdBalance systemid_balance) {
//        this.systemid_balance = systemid_balance;
//    }
//    @Override
//    public String toString() {
//        return systemId + " " + paymentType.name() + " " + password;
//    }
    @Override
    public String toString() {
        return "SystemId{" + "client=" + client + ", systemId=" + systemId + ", payment_type=" + paymentType + '}';
    }

}
