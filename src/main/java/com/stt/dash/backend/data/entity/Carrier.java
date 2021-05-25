package com.stt.dash.backend.data.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
@Entity
public class Carrier  extends AbstractEntity{

    @NotNull
    @Size(min = 2, max = 3)
    @Column(length = 3)
    private String countryIso2;

    @NotEmpty
    @NotNull
    @Size(min = 3, max = 20)
    @Column(length = 20, unique = true)
    private String carrierCharcode;

    @NotNull
    @NotEmpty
    @Size(max = 100)
    private String carrierName;

    public String getCarrierCharcode() {
        return carrierCharcode;
    }

    public void setCarrierCharcode(String carrier_char_code) {
        this.carrierCharcode = carrier_char_code;
    }

    public String getCarrierName() {
        return carrierName;
    }

    public void setCarrierName(String carrier_name) {
        this.carrierName = carrier_name;
    }

    public String getCountry_iso2() {
        return countryIso2;
    }

    public void setCountryIso2(String country_iso2) {
        this.countryIso2 = country_iso2;
    }
//
//    @Override
//    public String toString() {
//        return "Carrier{" + "carrier_char_code=" + carrier_char_code + ", carrier_name=" + carrier_name + ", carrier_mnc=" + carrier_mnc + '}';
//    }

    @Override
    public String toString() {
        return "Carrier{" + "country_iso2=" + countryIso2 + ", carrier_char_code=" + carrierCharcode + ", carrier_name=" + carrierName + '}';
    }
}
