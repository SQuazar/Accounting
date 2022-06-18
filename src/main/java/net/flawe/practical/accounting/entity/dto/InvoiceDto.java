package net.flawe.practical.accounting.entity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import net.flawe.practical.accounting.entity.InvoiceDirection;
import net.flawe.practical.accounting.entity.InvoiceType;

import java.time.LocalDateTime;

@Data
public class InvoiceDto {

    private final Integer id;
    private final Integer product;
    @JsonProperty("invoice_type")
    @JsonFormat(with = {
            JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES
    })
    private final InvoiceType invoiceType;
    @JsonProperty("invoice_direction")
    @JsonFormat(with = {
            JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES
    })
    private final InvoiceDirection invoiceDirection;
    private final int count;
    private final String person;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @JsonProperty(value = "invoice_date", required = true)
    private final LocalDateTime invoiceDate;

}
