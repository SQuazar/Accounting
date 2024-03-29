package net.flawe.practical.accounting.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@AllArgsConstructor
@Getter
public enum InvoiceType {

    BILLING("Выдача"),
    REFUND("Возврат");

    private final String localed;
}
