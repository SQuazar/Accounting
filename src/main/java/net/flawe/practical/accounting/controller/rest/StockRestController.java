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
    public ResponseEntity<List<StockDto>> addStock(@RequestBody StockDto stock) {
        return ResponseEntity.ok(List.of(stockMapper.mapToDto(stockService.save(stockMapper.mapFromDto(stock)))));
    }

    @PostMapping("/all")
    public List<StockDto> stocks(@RequestParam(required = false, defaultValue = "id") String sort,
                                   @RequestParam(required = false) boolean inverse) {
        var direction = inverse ? Sort.Direction.DESC : Sort.Direction.ASC;
        String sortProperty = switch (sort) {
            case "name" -> "name";
            case "address" -> "address";
            default -> "id";
        };
        return stockService.findAll(Sort.by(direction, sortProperty)).stream()
                .map(stockMapper::mapToDto)
                .toList();
    }

    @PostMapping("/get/{id}")
    public Stock get(@PathVariable("id") int id) {
        return stockService.getById(id);
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
