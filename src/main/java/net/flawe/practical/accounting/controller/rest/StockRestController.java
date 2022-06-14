package net.flawe.practical.accounting.controller.rest;

import net.flawe.practical.accounting.entity.Stock;
import net.flawe.practical.accounting.entity.dto.StockDto;
import net.flawe.practical.accounting.mapper.StockMapper;
import net.flawe.practical.accounting.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@RestController
@RequestMapping("/api/stock/")
@RolesAllowed("USER")
public class StockRestController {

    private final StockService stockService;
    private final StockMapper stockMapper;

    @Autowired
    public StockRestController(StockService stockService, StockMapper stockMapper) {
        this.stockService = stockService;
        this.stockMapper = stockMapper;
    }

    @PostMapping("/add")
    public ResponseEntity<List<StockDto>> addProduct(@RequestBody StockDto stock) {
        return ResponseEntity.ok(List.of(stockMapper.mapToDto(stockService.save(stockMapper.mapFromDto(stock)))));
    }

    @PostMapping("/all")
    public List<StockDto> products(@RequestParam(required = false, defaultValue = "id") String sort,
                                   @RequestParam(required = false) boolean inverse,
                                   @RequestParam(required = false, name = "min_date") Long minDate,
                                   @RequestParam(required = false, name = "max_date") Long maxDate,
                                   @RequestParam(required = false) String name,
                                   @RequestParam(required = false, name = "name_ignore_case") boolean nameIgnoreCase,
                                   @RequestParam(required = false, name = "name_starts_with") boolean nameStartsWith,
                                   @RequestParam(required = false, defaultValue = "0", name = "min_count") Integer minCount,
                                   @RequestParam(required = false, name = "max_count") Integer maxCount,
                                   @RequestParam(required = false, defaultValue = "0.0", name = "min_price") Double minPrice,
                                   @RequestParam(required = false, name = "max_price") Double maxPrice) {
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
        Stock minExample = Stock.builder()
                .productName(name)
                .count(minCount)
                .price(minPrice)
                .deliveryDate(localMinDate.atStartOfDay())
                .build();
        Stock maxExample = Stock.builder()
                .productName(name)
                .count(maxCount)
                .price(maxPrice)
                .deliveryDate(localMaxDate.atStartOfDay())
                .build();
        String sortProperty = switch (sort) {
            case "name" -> "productName";
            case "count" -> "count";
            case "price" -> "price";
            case "date" -> "deliveryDate";
            default -> "productId";
        };
        return stockService.findByExamples(minExample, maxExample, nameIgnoreCase, nameStartsWith, Sort.by(direction, sortProperty))
                .stream().map(stockMapper::mapToDto)
                .toList();
    }

    @PostMapping("/get/{id}")
    public Stock get(@PathVariable("id") int id) {
        return stockService.getById(id);
    }

    @PostMapping("/available")
    public List<Stock> available() {
        return stockService.findAllAvailable();
    }

    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestParam Integer id) {
        if (id == null)
            return ResponseEntity.badRequest().build();
        var product = stockService.getById(id);
        stockService.delete(product);
        return ResponseEntity.ok().build();
    }

}
