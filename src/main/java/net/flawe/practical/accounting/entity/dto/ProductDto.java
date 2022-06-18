package net.flawe.practical.accounting.entity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProductDto {
    private final Integer id;
    @JsonProperty(required = true)
    private final String name;
    @JsonProperty(required = true)
    private final Integer count;
    @JsonProperty(required = true)
    private final Double price;
    @JsonProperty(required = true)
    private final boolean refundable;
    @JsonProperty(value = "stock_id", required = true)
    private final Integer stockId;
    @JsonProperty(value = "purchase_date", required = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private final LocalDateTime purchaseDate;
    private final List<Integer> invoices;
}
