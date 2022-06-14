package net.flawe.practical.accounting.service;

import net.flawe.practical.accounting.entity.Invoice;
import net.flawe.practical.accounting.entity.InvoiceType;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.List;

public interface InvoiceService {

    Invoice getById(Integer id);

    Invoice save(Invoice invoice);

    void delete(Invoice invoice);

    List<Invoice> findAll();

    List<Invoice> findAll(Sort sort);

    List<Invoice> findAllById(List<Integer> ids);

    List<Invoice> findAllByDate(LocalDate date, Sort sort);

    List<Invoice> findAllByType(InvoiceType type);

    List<Invoice> findAllByType(InvoiceType type, Sort sort);

    List<Invoice> findAllByDateAndType(LocalDate date, InvoiceType type);

    List<Invoice> findAllByDateAndType(LocalDate date, InvoiceType type, Sort sort);

}
