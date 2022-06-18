package net.flawe.practical.accounting.mapper;

import net.flawe.practical.accounting.entity.Product;
import net.flawe.practical.accounting.entity.Stock;
import net.flawe.practical.accounting.entity.dto.ProductDto;
import net.flawe.practical.accounting.service.ProductService;
import net.flawe.practical.accounting.service.StockService;
import net.flawe.practical.accounting.util.DtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductDtoMapper implements DtoMapper<Product, ProductDto> {

    private final StockService stockService;

    @Autowired
    public ProductDtoMapper(StockService stockService) {
        this.stockService = stockService;
    }

    @Override
    public Product mapFromDto(ProductDto productDto) {
        Stock stock = stockService.getById(productDto.getStockId());
        return Product.builder()
                    .id(productDto.getId())
                    .name(productDto.getName())
                    .count(productDto.getCount())
                    .price(productDto.getPrice())
                    .refundable(productDto.isRefundable())
                    .stock(stock)
                    .purchaseDate(productDto.getPurchaseDate())
                    .build();
    }

    @Override
    public ProductDto mapToDto(Product product) {
        return new ProductDto(
                product.getId(),
                product.getName(),
                product.getCount(),
                product.getPrice(),
                product.getRefundable(),
                product.getStock().getId(),
                product.getPurchaseDate(),
                null
        );
    }
}
