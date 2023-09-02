/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stt.dash.backend.util.ws;


import java.time.LocalDate;

/**
 * @author yech
 */
public class SystemIdBalanceOResponse extends OResponse {

    private int balance_credit;
    private int credit_used;
    private int credit_plus;
    private LocalDate expiration_date;
    private boolean auto_reboot;
    private int locked_balance;
    private SystemIdOResponse systemid;

//    public SystemIdBalanceOResponse(SystemIdBalance s) {
//        setAuto_reboot(s.isAuto_reboot());
//        setBalance_credit(s.getBalance_credit());
//        setCredit_used(s.getCredit_used());
//        setCredit_plus(s.getCredit_plus());
//        setExpiration_date(s.getExpiration_date());
//        setId(s.getId());
//        setSystemid(new SystemIdOResponse(s.getSystemid()));
//    }

    public int getBalance_credit() {
        return balance_credit;
    }

    public void setBalance_credit(int balance_credit) {
        this.balance_credit = balance_credit;
    }

    public SystemIdOResponse getSystemid() {
        return systemid;
    }

    public void setSystemid(SystemIdOResponse systemid) {
        this.systemid = systemid;
    }

    public LocalDate getExpiration_date() {
        return expiration_date;
    }

    public void setExpiration_date(LocalDate expiration_date) {
        this.expiration_date = expiration_date;
    }

    public boolean isAuto_reboot() {
        return auto_reboot;
    }

    public void setAuto_reboot(boolean auto_reboot) {
        this.auto_reboot = auto_reboot;
    }

    public int getCredit_used() {
        return credit_used;
    }

    public void setCredit_used(int credit_used) {
        this.credit_used = credit_used;
    }

    public int getCredit_plus() {
        return credit_plus;
    }

    public void setCredit_plus(int credit_plus) {
        this.credit_plus = credit_plus;
    }


    public int getLocked_balance() {
        return locked_balance;
    }

    public void setLocked_balance(int locked_balance) {
        this.locked_balance = locked_balance;
    }

    @Override
    public String toString() {
        return "SystemIdBalanceOResponse{" + "balance_credit=" + balance_credit + ", credit_used=" + credit_used + ", credit_plus=" + credit_plus + ", expiration_date=" + expiration_date + ", auto_reboot=" + auto_reboot + ", systemid=" + systemid + '}';
    }

}
