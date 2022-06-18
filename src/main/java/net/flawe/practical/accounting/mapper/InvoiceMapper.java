package net.flawe.practical.accounting.mapper;

import net.flawe.practical.accounting.entity.Invoice;
import net.flawe.practical.accounting.entity.dto.InvoiceDto;
import net.flawe.practical.accounting.service.ProductService;
import net.flawe.practical.accounting.util.DtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InvoiceMapper implements DtoMapper<Invoice, InvoiceDto> {

    private final ProductService productService;

    @Autowired
    public InvoiceMapper(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public Invoice mapFromDto(InvoiceDto invoiceDto) {
        var product = productService.getById(invoiceDto.getProduct());
        return Invoice.builder()
                .id(invoiceDto.getId())
                .product(product)
                .invoiceType(invoiceDto.getInvoiceType())
                .invoiceDirection(invoiceDto.getInvoiceDirection())
                .count(invoiceDto.getCount())
                .person(invoiceDto.getPerson())
                .date(invoiceDto.getInvoiceDate())
                .build();
    }

    @Override
    public InvoiceDto mapToDto(Invoice invoice) {
        var productId = invoice.getProduct().getId();
        return new InvoiceDto(invoice.getId(),
                productId,
                invoice.getInvoiceType(),
                invoice.getInvoiceDirection(),
                invoice.getCount(),
                invoice.getPerson(),
                invoice.getDate());
    }
}
