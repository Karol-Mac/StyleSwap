package com.restapi.styleswap.utils;

public enum ClotheSize {
    XS("XS"),  S("S"), M("M"), L("L"), XL("XL"), XXL("XXL"),
    R32("32"), R34("34"), R36("36"),R38("38"),
    R40("40"), R42("42"), R44("44"), R46("46");
    final String code;

    ClotheSize(String code){
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
