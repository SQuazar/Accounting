package net.flawe.practical.accounting.entity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class StockDto {

    @JsonProperty("product_id")
    private final Integer productId;
    @JsonProperty("product_name")
    private final String productName;
    private final int count;
    private final double price;
    @JsonProperty("delivery_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private final LocalDateTime deliveryDate;
    @JsonIgnore
    private final List<Integer> invoices;

}
