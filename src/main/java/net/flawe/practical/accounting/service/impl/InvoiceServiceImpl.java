package net.flawe.practical.accounting.service.impl;

import net.flawe.practical.accounting.entity.Invoice;
import net.flawe.practical.accounting.entity.InvoiceType;
import net.flawe.practical.accounting.exception.InvoiceNotFoundException;
import net.flawe.practical.accounting.exception.ProductCountException;
import net.flawe.practical.accounting.repository.InvoiceRepository;
import net.flawe.practical.accounting.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

    @Override
    public Invoice save(Invoice invoice) {
        var stock = invoice.getProduct();

        if (stock == null)
            throw new NullPointerException("Product cannot be null!");
        int remaining;
        if (invoice.getInvoiceType() == InvoiceType.BILLING)
            remaining = stock.getCount() - invoice.getCount();
        else
            remaining = stock.getCount() + invoice.getCount();
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
    public List<Invoice> findAllByType(InvoiceType type) {
        return invoiceRepository.findAllByInvoiceType(type);
    }

    @Override
    public List<Invoice> findAllByType(InvoiceType type, Sort sort) {
        return invoiceRepository.findAllByInvoiceType(type, sort);
    }

    @Override
    public List<Invoice> findAllByDateAndType(LocalDate date, InvoiceType type) {
        if (date.isEqual(LocalDate.EPOCH)) {
            if (type == null)
                return findAll();
            return findAllByType(type);
        }
        if (type == null)
            return invoiceRepository.findAllByDate(date);
        return invoiceRepository.findAllByDateAndType(date, type);
    }

    @Override
    public List<Invoice> findAllByDateAndType(LocalDate date, InvoiceType type, Sort sort) {
        if (date.isEqual(LocalDate.EPOCH)) {
            if (type == null)
                return findAll(sort);
            return findAllByType(type, sort);
        }
        if (type == null)
            return findAllByDate(date, sort);
        return invoiceRepository.findAllByDateAndType(date, type, sort);
    }

}
