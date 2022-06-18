package net.flawe.practical.accounting.service;

import net.flawe.practical.accounting.entity.Invoice;
import net.flawe.practical.accounting.entity.InvoiceType;
import net.flawe.practical.accounting.entity.dto.InvoiceDto;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

public interface InvoiceService extends DefaultService<Invoice, Integer> {

    List<Invoice> findAllById(List<Integer> ids);

    List<Invoice> findAllByDate(LocalDate date, Sort sort);

    List<Invoice> findAllByExamples(Invoice minExample, Invoice maxExample, boolean personNameIgnoreCase, boolean personNameStartsWith, Sort sort);

    void deleteAllByIds(List<Integer> ids);

}
