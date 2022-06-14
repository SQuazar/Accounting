package net.flawe.practical.accounting.controller.rest;

import net.flawe.practical.accounting.entity.Invoice;
import net.flawe.practical.accounting.entity.InvoiceType;
import net.flawe.practical.accounting.entity.dto.InvoiceDto;
import net.flawe.practical.accounting.mapper.InvoiceMapper;
import net.flawe.practical.accounting.service.InvoiceService;
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
@RequestMapping("/api/invoice/")
@RolesAllowed("USER")
public class InvoiceRestController {

    private final InvoiceService invoiceService;
    private final InvoiceMapper invoiceMapper;

    @Autowired
    public InvoiceRestController(InvoiceService invoiceService, InvoiceMapper invoiceMapper) {
        this.invoiceService = invoiceService;
        this.invoiceMapper = invoiceMapper;
    }

    @PostMapping("/all")
    public List<InvoiceDto> all(@RequestParam(required = false, defaultValue = "id") String sort,
                                @RequestParam(required = false) boolean inverse,
                                @RequestParam(required = false) Long date,
                                @RequestParam(required = false) String type) {
        LocalDate localDate;
        if (date == null)
            localDate = LocalDate.EPOCH;
        else localDate = LocalDate.ofInstant(Instant.ofEpochMilli(date), ZoneId.of("Europe/Moscow"));
        var direction = inverse ? Sort.Direction.DESC : Sort.Direction.ASC;
        InvoiceType invoiceType;
        if (type == null)
            invoiceType = null;
        else
            invoiceType = switch (type.toLowerCase()) {
            case "billing" -> InvoiceType.BILLING;
            case "refund" -> InvoiceType.REFUND;
            default -> null;
        };
        return switch (sort) {
            case "date" -> invoiceService.findAllByDateAndType(localDate, invoiceType, Sort.by(direction, "date"))
                    .stream().map(invoiceMapper::mapToDto)
                    .toList();
            case "person" -> invoiceService.findAllByDateAndType(localDate, invoiceType, Sort.by(direction, "person"))
                    .stream().map(invoiceMapper::mapToDto)
                    .toList();
            case "count" -> invoiceService.findAllByDateAndType(localDate, invoiceType, Sort.by(direction, "count"))
                    .stream().map(invoiceMapper::mapToDto)
                    .toList();
            case "invoice_type" ->
                    invoiceService.findAllByDateAndType(localDate, invoiceType, Sort.by(direction, "invoiceType"))
                            .stream().map(invoiceMapper::mapToDto)
                            .toList();
            default ->  invoiceService.findAllByDateAndType(localDate, invoiceType, Sort.by(direction, "id"))
                        .stream().map(invoiceMapper::mapToDto)
                        .toList();
        };
    }

    @PostMapping("/add")
    public ResponseEntity<List<Invoice>> add(@RequestBody InvoiceDto invoiceDto) {
        return ResponseEntity.ok(List.of(invoiceService.save(invoiceMapper.mapFromDto(invoiceDto))));
    }

    public ResponseEntity<?> delete(@RequestParam Integer id) {
        if (id == null)
            return ResponseEntity.badRequest().build();
        var product = invoiceService.getById(id);
        invoiceService.delete(product);
        return ResponseEntity.ok().build();
    }


}
