package net.flawe.practical.accounting.controller.rest;

import net.flawe.practical.accounting.entity.Invoice;
import net.flawe.practical.accounting.entity.InvoiceDirection;
import net.flawe.practical.accounting.entity.InvoiceType;
import net.flawe.practical.accounting.entity.Product;
import net.flawe.practical.accounting.entity.dto.InvoiceDto;
import net.flawe.practical.accounting.mapper.InvoiceMapper;
import net.flawe.practical.accounting.service.InvoiceService;
import net.flawe.practical.accounting.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@RestController
@RequestMapping("/api/invoice/")
@RolesAllowed("USER")
public class InvoiceRestController {

    private final InvoiceService invoiceService;
    private final InvoiceMapper invoiceMapper;

    @Autowired
    public InvoiceRestController(InvoiceService invoiceService,
                                 InvoiceMapper invoiceMapper) {
        this.invoiceService = invoiceService;
        this.invoiceMapper = invoiceMapper;
    }

    @PostMapping("/all")
    public List<InvoiceDto> all(@RequestParam(required = false, defaultValue = "id", name = "sort") String sortType,
                                @RequestParam(required = false) boolean inverse,
                                @RequestParam(required = false, name = "min_date") Long minDate,
                                @RequestParam(required = false, name = "max_date") Long maxDate,
                                @RequestParam(required = false) String person,
                                @RequestParam(required = false, name = "person_name_ignore_case") boolean personNameIgnoreCase,
                                @RequestParam(required = false, name = "person_name_starts_with") boolean personNameStartsWith,
                                @RequestParam(required = false, name = "min_count", defaultValue = "0") Integer minCount,
                                @RequestParam(required = false, name = "max_count") Integer maxCount,
                                @RequestParam(required = false, name = "product_id") Integer productId,
                                @RequestParam(required = false) String type,
                                @RequestParam(required = false, name = "invoice_direction") String direction) {
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
        InvoiceDirection invoiceDirection;
        if (direction == null)
            invoiceDirection = null;
        else
            invoiceDirection = switch (direction.toLowerCase()) {
                case "realization" -> InvoiceDirection.REALIZATION;
                case "selling" -> InvoiceDirection.SELLING;
                default -> null;
            };
        var sortDirection = inverse ? Sort.Direction.DESC : Sort.Direction.ASC;
        InvoiceType invoiceType;
        if (type == null)
            invoiceType = null;
        else
            invoiceType = switch (type.toLowerCase()) {
                case "billing" -> InvoiceType.BILLING;
                case "refund" -> InvoiceType.REFUND;
                default -> null;
            };
        String sortProperty = switch (sortType) {
            case "invoice_type" -> "invoiceType";
            case "invoice_direction" -> "invoiceDirection";
            case "count" -> "count";
            case "person" -> "person";
            case "date" -> "date";
            default -> "id";
        };
        Sort sort = Sort.by(sortDirection, sortProperty);
        var minExample = Invoice.builder()
                .product(productId == null ? null : Product.builder().id(productId).build())
                .invoiceType(invoiceType)
                .invoiceDirection(invoiceDirection)
                .count(minCount)
                .person(person)
                .date(localMinDate.atStartOfDay())
                .build();
        var maxExample = Invoice.builder()
                .product(productId == null ? null : Product.builder().id(productId).build())
                .invoiceType(null)
                .invoiceDirection(null)
                .count(maxCount)
                .person(null)
                .date(localMaxDate.atStartOfDay())
                .build();
        return invoiceService.findAllByExamples(minExample, maxExample, personNameIgnoreCase, personNameStartsWith, sort)
                .stream()
                .map(invoiceMapper::mapToDto)
                .toList();
    }

    @PostMapping("/add")
    public ResponseEntity<List<InvoiceDto>> add(@RequestBody InvoiceDto invoiceDto) {
        return ResponseEntity.ok(List.of(invoiceMapper.mapToDto(invoiceService.save(invoiceMapper.mapFromDto(invoiceDto)))));
    }

    @PostMapping(value = "/delete", params = "id")
    public ResponseEntity<?> delete(@RequestParam("id") Integer id) {
        if (id == null)
            return ResponseEntity.badRequest().build();
        var product = invoiceService.getById(id);
        invoiceService.delete(product);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/delete")
    public ResponseEntity<?> delete(@RequestBody List<Integer> ids) {
        if (ids == null)
            return ResponseEntity.badRequest().build();
        invoiceService.deleteAllByIds(ids);
        return ResponseEntity.ok().build();
    }


}
