package net.flawe.practical.accounting.service.impl;

import net.flawe.practical.accounting.entity.Invoice;
import net.flawe.practical.accounting.entity.InvoiceType;
import net.flawe.practical.accounting.exception.InvoiceNotFoundException;
import net.flawe.practical.accounting.exception.ProductCountException;
import net.flawe.practical.accounting.exception.ProductException;
import net.flawe.practical.accounting.repository.InvoiceRepository;
import net.flawe.practical.accounting.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;

    @Autowired
    public InvoiceServiceImpl(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    @Override
    public Invoice getById(Integer id) {
        return invoiceRepository.findById(id).orElseThrow(() ->
                new InvoiceNotFoundException(String.format("Invoice with id %d not found!", id)));
    }

    @Override @Transactional
    public Invoice save(Invoice invoice) {
        var stock = invoice.getProduct();
        if (stock == null)
            throw new NullPointerException("Product cannot be null!");
        int remaining;
        if (invoice.getInvoiceType() == InvoiceType.BILLING)
            remaining = stock.getCount() - invoice.getCount();
        else {
            if (invoice.getProduct().getRefundable() == null)
                throw new ProductException("Refundable cannot be null!");
            if (!invoice.getProduct().getRefundable())
                throw new ProductException("Этот продукт нельзя вернуть!");
            remaining = stock.getCount() + invoice.getCount();
        }
        if (remaining < 0)
            throw new ProductCountException("Этого продукта недостаточно на складе!");
        else
            stock.setCount(remaining);
        return invoiceRepository.save(invoice);
    }

    @Override
    public void delete(Invoice invoice) {
        invoiceRepository.delete(invoice);
    }

    @Override
    public List<Invoice> findAll() {
        return invoiceRepository.findAll();
    }

    @Override
    public List<Invoice> findAll(Sort sort) {
        return invoiceRepository.findAll(sort);
    }

    @Override
    public List<Invoice> findAllById(List<Integer> ids) {
        return invoiceRepository.findAllById(ids);
    }

    @Override
    public List<Invoice> findAllByDate(LocalDate date, Sort sort) {
        if (date.isEqual(LocalDate.EPOCH))
            return invoiceRepository.findAll(sort);
        return invoiceRepository.findAllByDate(date, sort);
    }

    @Override
    public List<Invoice> findAllByExamples(Invoice minExample, Invoice maxExample, boolean personNameIgnoreCase, boolean personNameStartsWith, Sort sort) {
        if (minExample == null || maxExample == null) return List.of();
        return invoiceRepository.findAll(((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (minExample.getPerson() != null) {
                predicates.add(
                        criteriaBuilder.and(
                                criteriaBuilder.like(
                                        (personNameIgnoreCase ? criteriaBuilder.upper(root.get("person")) : root.get("person")),
                                        (personNameStartsWith ? minExample.getPerson() + "%" : minExample.getPerson())
                                )
                        )
                );
            }
            if (minExample.getProduct() != null)
                predicates.add(criteriaBuilder.equal(root.get("product"), minExample.getProduct()));
            if (minExample.getInvoiceType() != null)
                predicates.add(criteriaBuilder.equal(root.get("invoiceType"), minExample.getInvoiceType()));
            if (minExample.getInvoiceDirection() != null)
                predicates.add(criteriaBuilder.equal(root.get("invoiceDirection"), minExample.getInvoiceDirection()));
            predicates.add(criteriaBuilder.and(
                    criteriaBuilder.greaterThanOrEqualTo(root.get("count"),
                            (minExample.getCount() == null ? 0 : minExample.getCount())),
                    criteriaBuilder.lessThanOrEqualTo(root.get("count"),
                            (maxExample.getCount() == null ? Integer.MAX_VALUE : maxExample.getCount()))
            ));
            predicates.add(criteriaBuilder.and(
                    criteriaBuilder.greaterThanOrEqualTo(root.get("date"),
                            (minExample.getDate() == null ? LocalDateTime.MIN : minExample.getDate())),
                    criteriaBuilder.lessThanOrEqualTo(root.get("date"),
                            (maxExample.getDate() == null ? LocalDateTime.MAX : maxExample.getDate()))
            ));
            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        }), sort);
    }

    @Override
    public void deleteAllByIds(List<Integer> ids) {
        invoiceRepository.deleteAllByIdInBatch(ids);
    }

}
