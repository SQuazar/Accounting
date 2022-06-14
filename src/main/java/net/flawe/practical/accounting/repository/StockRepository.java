package net.flawe.practical.accounting.repository;

import net.flawe.practical.accounting.entity.Stock;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface StockRepository extends JpaRepository<Stock, Integer>, JpaSpecificationExecutor<Stock> {
    @Query(value = "select * from stock where DATE(delivery_date) = :date", nativeQuery = true)
    List<Stock> findByDeliveryDate(LocalDate date);

    @Query("""
                        select stock from Stock stock where
                        year(stock.deliveryDate) = year(:date)
                        and month(stock.deliveryDate) = month(:date)
                        and day(stock.deliveryDate) = day(:date)
            """)
    List<Stock> findByDeliveryDate(LocalDate date, Sort sort);

    @Query("select stock from Stock stock where stock.count > 0")
    List<Stock> findAllAvailable();

}
