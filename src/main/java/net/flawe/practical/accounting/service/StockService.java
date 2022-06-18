package net.flawe.practical.accounting.service;

import net.flawe.practical.accounting.entity.Stock;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface StockService extends DefaultService<Stock, Integer> {

    List<Stock> findBy(Stock example);

    List<Stock> findBy(Stock example, Sort sort);

}
