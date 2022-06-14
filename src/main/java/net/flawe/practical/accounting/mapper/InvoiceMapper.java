package net.flawe.practical.accounting.mapper;

import net.flawe.practical.accounting.entity.Invoice;
import net.flawe.practical.accounting.entity.InvoiceType;
import net.flawe.practical.accounting.entity.dto.InvoiceDto;
import net.flawe.practical.accounting.service.StockService;
import net.flawe.practical.accounting.util.DtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InvoiceMapper implements DtoMapper<Invoice, InvoiceDto> {

    private final StockService stockService;

    @Autowired
    public InvoiceMapper(StockService stockService) {
        this.stockService = stockService;
    }

    @Override
    public Invoice mapFromDto(InvoiceDto invoiceDto) {
        var product = stockService.getById(invoiceDto.getProduct());
        return Invoice.builder()
                .id(invoiceDto.getId())
                .product(product)
                .invoiceType(invoiceDto.getInvoiceType())
                .count(invoiceDto.getCount())
                .person(invoiceDto.getPerson())
                .date(invoiceDto.getDate())
                .build();
    }

    @Override
    public InvoiceDto mapToDto(Invoice invoice) {
        var productId = invoice.getProduct().getProductId();
        return new InvoiceDto(invoice.getId(),
                productId,
                invoice.getInvoiceType(),
                invoice.getCount(),
                invoice.getPerson(),
                invoice.getDate());
    }
}
