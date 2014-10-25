package com.github.b2ojustin.irclibrary.net

import java.util.function.Consumer


public enum ResponseType {
    WELCOME_MESSAGE("001")

    String rCode
    private ResponseType(String rCode) {
        this.rCode = rCode
    }

    static ResponseType byCode(String rCode) {
        values().each { if(it.rCode == rCode) return it }
        return null
    }
}