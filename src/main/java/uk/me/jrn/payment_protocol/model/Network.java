package uk.me.jrn.payment_protocol.model;

public enum Network {

    BITCOIN_MAIN("main"),
    BITCOIN_TEST("test"),
    DOGECOIN_MAIN("doge-main"),
    DOGECOIN_TEST("doge-test");

    private final String code;

    Network(final String setCode) {
        this.code = setCode;
    }

    public String getCode() {
        return this.code;
    }
}
