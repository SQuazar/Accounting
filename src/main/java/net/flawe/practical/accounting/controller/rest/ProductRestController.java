package net.flawe.practical.accounting.controller.rest;

import net.flawe.practical.accounting.entity.Product;
import net.flawe.practical.accounting.entity.Stock;
import net.flawe.practical.accounting.entity.dto.ProductDto;
import net.flawe.practical.accounting.exception.ProductException;
import net.flawe.practical.accounting.mapper.ProductDtoMapper;
import net.flawe.practical.accounting.service.ProductService;
import net.flawe.practical.accounting.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@RestController
@RequestMapping("/api/product/")
public class ProductRestController {

    private final ProductService productService;
    private final ProductDtoMapper productMapper;
    private final StockService stockService;

    @Autowired
    public ProductRestController(ProductService productService,
                                 ProductDtoMapper productMapper,
                                 StockService stockService) {
        this.productService = productService;
        this.productMapper = productMapper;
        this.stockService = stockService;
    }

    @PostMapping("/all")
    public List<ProductDto> all(@RequestParam(required = false, defaultValue = "id") String sort,
                                @RequestParam(required = false) boolean inverse,
                                @RequestParam(required = false, name = "min_date") Long minDate,
                                @RequestParam(required = false, name = "max_date") Long maxDate,
                                @RequestParam(required = false) String name,
                                @RequestParam(required = false, name = "name_ignore_case") boolean nameIgnoreCase,
                                @RequestParam(required = false, name = "name_starts_with") boolean nameStartsWith,
                                @RequestParam(required = false, defaultValue = "0", name = "min_count") Integer minCount,
                                @RequestParam(required = false, name = "max_count") Integer maxCount,
                                @RequestParam(required = false, defaultValue = "0.0", name = "min_price") Double minPrice,
                                @RequestParam(required = false, name = "max_price") Double maxPrice,
                                @RequestParam(required = false, name = "refundable") Boolean refundable,
                                @RequestParam(required = false, name = "stock_id") Integer stockId) {
        LocalDate localMinDate;
        LocalDate localMaxDate;
        if (minDate == null || minDate == 0)
            localMinDate = LocalDate.of(1970, 1, 2);
        else localMinDate = LocalDate.ofInstant(Instant.ofEpochMilli(minDate), ZoneId.of("Europe/Moscow"));
        if (maxDate == null || maxDate == 0)
            localMaxDate = LocalDate.of(9999, 12, 1);
        else localMaxDate = LocalDate.ofInstant(Instant.ofEpochMilli(maxDate), ZoneId.of("Europe/Moscow"));
        if (maxCount == null)
            maxCount = Integer.MAX_VALUE;
        if (maxPrice == null)
            maxPrice = Double.MAX_VALUE;
        if (name != null && name.isEmpty())
            name = null;
        var direction = inverse ? Sort.Direction.DESC : Sort.Direction.ASC;
        String sortProperty = switch (sort) {
            case "name" -> "name";
            case "count" -> "count";
            case "price" -> "price";
            case "refundable" -> "refundable";
            case "stock" -> "stock";
            case "date" -> "purchaseDate";
            default -> "id";
        };
        var minExample = Product.builder()
                .name(name)
                .count(minCount)
                .price(minPrice)
                .refundable(refundable)
                .stock(stockId == null ? null : Stock.builder().id(stockId).build())
                .purchaseDate(localMinDate.atStartOfDay())
                .build();
        var maxExample = Product.builder()
                .name(name)
                .count(maxCount)
                .price(maxPrice)
                .refundable(refundable)
                .stock(null)
                .purchaseDate(localMaxDate.atStartOfDay())
                .build();
        return productService.findByExamples(minExample, maxExample,
                        nameIgnoreCase, nameStartsWith,
                        Sort.by(direction, sortProperty))
                .stream()
                .map(productMapper::mapToDto)
                .toList();
    }

    @PostMapping("/save")
    public ProductDto save(@RequestBody ProductDto productDto) {
        return productMapper.mapToDto(productService.save(productMapper.mapFromDto(productDto)));
    }

    @PostMapping(value = "/delete", params = "id")
    public void delete(@RequestParam Integer id) {
        if (id <= 0) throw new ProductException("Invalid product id");
        productService.deleteById(id);
    }

    @PostMapping("/delete")
    public void delete(@RequestBody List<Integer> ids) {
        productService.deleteAll(ids);
    }

    @PostMapping("/transfer")
    public void transfer(@RequestBody List<Integer> productIds,
                         @RequestParam(name = "stock_id") Integer stockId) {
        Stock stock = stockService.getById(stockId);
        List<Product> products = productService.findByIds(productIds);
        products.forEach(product -> product.setStock(stock));
        productService.saveAll(products);
    }

}
