package net.flawe.practical.accounting.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class StockDto {

    @JsonProperty("stock_id")
    private final Integer stockId;
    @JsonProperty("stock_name")
    private final String stockName;
    @JsonProperty("stock_address")
    private final String stockAddress;
    private final List<Integer> products;

}
