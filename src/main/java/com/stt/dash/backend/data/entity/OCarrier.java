package com.stt.dash.backend.data.entity;

public class OCarrier {

    private Long id;

    private String countryIso2;

    private String carrierCharcode;

    private String carrierName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    @Override
    public String toString() {
        return "OCarrier{" + "id=" + id + ", countryIso2=" + countryIso2 + ", carrierCharcode=" + carrierCharcode + ", carrierName=" + carrierName + '}';
    }
}
