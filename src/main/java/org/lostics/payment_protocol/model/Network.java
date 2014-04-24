package org.lostics.payment_protocol.model;

public enum Network {

    BitcoinMain("main"),
    BitcoinTest("test"),
    DogecoinMain("doge-main"),
    DogecoinTest("doge-test");

    private final String code;

    Network(final String setCode) {
        this.code = setCode;
    }

    public String getCode() {
        return this.code;
    }
}
