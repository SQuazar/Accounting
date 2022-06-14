package net.flawe.practical.accounting.repository;

import net.flawe.practical.accounting.entity.Invoice;
import net.flawe.practical.accounting.entity.InvoiceType;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {
    List<Invoice> findAllByInvoiceType(InvoiceType type, Sort sort);

    List<Invoice> findAllByInvoiceType(InvoiceType type);

    @Query(nativeQuery = true,
            value = "select * from invoice where DATE(`date`) = :date"
    )
    List<Invoice> findAllByDate(LocalDate date);

    @Query("""
            select invoice from Invoice invoice where
            year(invoice.date) = year(:date)
            and month(invoice.date) = month(:date)
            and day(invoice.date) = day(:date)
            """)
    List<Invoice> findAllByDate(LocalDate date, Sort sort);

    @Query(nativeQuery = true,
            value = "select * from invoice where DATE(`date`) = :date and invoice_type = :type")
    List<Invoice> findAllByDateAndType(LocalDate date, InvoiceType type);

    @Query("""
            select invoice from Invoice invoice where
            year(invoice.date) = year(:date)
            and month(invoice.date) = month(:date)
            and day(invoice.date) = day(:date)
            and invoice.invoiceType = :type
            """)
    List<Invoice> findAllByDateAndType(LocalDate date, InvoiceType type, Sort sort);

}
