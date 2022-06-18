package net.flawe.practical.accounting.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum InvoiceDirection {
    REALIZATION("Реализация"),
    SELLING("Продажа");

    private final String localed;
}
