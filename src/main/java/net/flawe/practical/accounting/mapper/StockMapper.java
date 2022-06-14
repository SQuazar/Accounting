package net.flawe.practical.accounting.mapper;

import net.flawe.practical.accounting.entity.Invoice;
import net.flawe.practical.accounting.entity.Stock;
import net.flawe.practical.accounting.entity.dto.StockDto;
import net.flawe.practical.accounting.util.DtoMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StockMapper implements DtoMapper<Stock, StockDto> {

    @Override
    public Stock mapFromDto(StockDto stockDto) {
        return Stock.builder()
                .productId(stockDto.getProductId())
                .productName(stockDto.getProductName())
                .count(stockDto.getCount())
                .price(stockDto.getPrice())
                .deliveryDate(stockDto.getDeliveryDate())
                .invoices(List.of())
                .build();
    }

    @Override
    public StockDto mapToDto(Stock stock) {
        List<Integer> invoices = stock.getInvoices()
                .stream()
                .map(Invoice::getId).toList();
        return new StockDto(stock.getProductId(),
                stock.getProductName(),
                stock.getCount(),
                stock.getPrice(),
                stock.getDeliveryDate(),
                invoices);
    }
}
