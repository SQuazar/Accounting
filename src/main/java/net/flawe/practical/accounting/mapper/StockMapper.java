package net.flawe.practical.accounting.mapper;

import net.flawe.practical.accounting.entity.Invoice;
import net.flawe.practical.accounting.entity.Product;
import net.flawe.practical.accounting.entity.Stock;
import net.flawe.practical.accounting.entity.dto.StockDto;
import net.flawe.practical.accounting.util.DtoMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class StockMapper implements DtoMapper<Stock, StockDto> {

    @Override
    public Stock mapFromDto(StockDto stockDto) {
        return Stock.builder()
                .id(stockDto.getStockId())
                .name(stockDto.getStockName())
                .address(stockDto.getStockAddress())
                .build();
    }

    @Override
    public StockDto mapToDto(Stock stock) {
        List<Integer> products;
        if (stock.getProducts() != null)
            products = stock.getProducts().stream().map(Product::getId).toList();
        else products = List.of();
        return new StockDto(
                stock.getId(),
                stock.getName(),
                stock.getAddress(),
                products
        );
    }
}
