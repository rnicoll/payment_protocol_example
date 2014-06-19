package uk.me.jrn.payment_protocol.model;

public enum Network {
    DOGECOIN_MAIN("main"),
    DOGECOIN_TEST("test");

    private final String code;

    Network(final String setCode) {
        this.code = setCode;
    }

    public String getCode() {
        return this.code;
    }
}
