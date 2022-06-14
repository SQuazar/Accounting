package net.flawe.practical.accounting.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum InvoiceType {

    BILLING("Выдача"),
    REFUND("Возврат");

    private final String localed;

    InvoiceType(String localed) {
        this.localed = localed;
    }

    public String getLocaled() {
        return localed;
    }
}
