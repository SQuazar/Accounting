package net.flawe.practical.accounting.service.impl;

import net.flawe.practical.accounting.entity.Stock;
import net.flawe.practical.accounting.exception.StockNotFoundException;
import net.flawe.practical.accounting.repository.StockRepository;
import net.flawe.practical.accounting.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.security.RolesAllowed;
import java.util.List;

@Service
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;

    @Autowired
    public StockServiceImpl(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @Override
    public Stock getById(Integer id) {
        return stockRepository.findById(id).orElseThrow(() ->
                new StockNotFoundException(String.format("Stock with id %d not found!", id)));
    }

    @Override @RolesAllowed("ADMIN")
    public Stock save(Stock stock) {
        return stockRepository.save(stock);
    }

    @Override @RolesAllowed("ADMIN")
    public void delete(Stock stock) {
        stockRepository.delete(stock);
    }

    @Override
    public List<Stock> findAll() {
        return stockRepository.findAll();
    }

    @Override
    public List<Stock> findAll(Sort sort) {
        return stockRepository.findAll(sort);
    }

    @Override
    public List<Stock> findBy(Stock stock) {
        if (stock == null) return findAll();
        return findBy(stock, Sort.unsorted());
    }

    @Override
    public List<Stock> findBy(Stock stock, Sort sort) {
        if (stock == null) return findAll();
        return stockRepository.findAll(((root, query, criteriaBuilder) -> {
            if (stock.getAddress() != null)
                criteriaBuilder.and(criteriaBuilder.like(root.get("address"), stock.getAddress() + "%"));
            if (stock.getName() != null)
                criteriaBuilder.and(criteriaBuilder.like(root.get("name"), stock.getName() + "%"));
            return criteriaBuilder.and();
        }), sort);
    }

}
