package net.flawe.practical.accounting.service;

import net.flawe.practical.accounting.entity.Stock;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.List;

public interface StockService {

    Stock getById(Integer id);

    Stock save(Stock stock);

    void delete(Stock stock);

    List<Stock> findAll();

    List<Stock> findAll(Sort sort);

    List<Stock> findAllByDate(LocalDate date);

    List<Stock> findAllByDate(LocalDate date, Sort sort);

    List<Stock> findAllAvailable();

    List<Stock> findBy(Stock example);

    List<Stock> findBy(Stock example, Sort sort);

    List<Stock> findByExamples(Stock minExample, Stock maxExample, boolean nameIgnoreCase, boolean nameStartsWith, Sort sort);
}
