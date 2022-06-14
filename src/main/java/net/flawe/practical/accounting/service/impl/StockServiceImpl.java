package net.flawe.practical.accounting.service.impl;

import net.flawe.practical.accounting.entity.Stock;
import net.flawe.practical.accounting.exception.StockNotFoundException;
import net.flawe.practical.accounting.repository.StockRepository;
import net.flawe.practical.accounting.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

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

    @Override
    public Stock save(Stock stock) {
        return stockRepository.save(stock);
    }

    @Override
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
    public List<Stock> findAllByDate(LocalDate date) {
        return stockRepository.findByDeliveryDate(date);
    }

    @Override
    public List<Stock> findAllByDate(LocalDate date, Sort sort) {
        if (date.isEqual(LocalDate.EPOCH))
            return findAll(sort);
        return stockRepository.findByDeliveryDate(date, sort);
    }

    @Override
    public List<Stock> findAllAvailable() {
        return stockRepository.findAllAvailable();
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
            if (stock.getProductId() != null)
                criteriaBuilder.and(criteriaBuilder.equal(root.get("productId"), stock.getProductId()));
            if (stock.getProductName() != null)
                criteriaBuilder.and(criteriaBuilder.equal(criteriaBuilder.upper(root.get("productName")), stock.getProductName().toUpperCase()));
            if (stock.getCount() != null)
                criteriaBuilder.and(criteriaBuilder.equal(root.get("count"), stock.getCount()));
            if (stock.getPrice() != null)
                criteriaBuilder.and(criteriaBuilder.equal(root.get("price"), stock.getPrice()));
            if (stock.getDeliveryDate() != null && !stock.getDeliveryDate().equals(LocalDate.EPOCH.atStartOfDay()))
                criteriaBuilder.and(
                        criteriaBuilder.greaterThanOrEqualTo(root.get("deliveryDate"), stock.getDeliveryDate()),
                        criteriaBuilder.lessThanOrEqualTo(root.get("deliveryDate"), stock.getDeliveryDate().toLocalDate().atTime(LocalTime.MAX))
                );
            return criteriaBuilder.and();
        }), sort);
    }

    @Override
    public List<Stock> findByExamples(Stock minExample, Stock maxExample, boolean nameIgnoreCase, boolean nameStartsWith, Sort sort) {
        if (minExample == null || maxExample == null) return List.of();
        return stockRepository.findAll((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (minExample.getProductName() != null) {
                predicates.add(
                        criteriaBuilder.like(
                        (nameIgnoreCase ? criteriaBuilder.upper(root.get("productName")) : root.get("productName")),
                        (nameStartsWith ? minExample.getProductName() + "%" : minExample.getProductName())
                ));
            }
            predicates.add(criteriaBuilder.and(
                    criteriaBuilder.greaterThanOrEqualTo(root.get("count"),
                            (minExample.getCount() == null ? 0 : minExample.getCount())),
                    criteriaBuilder.lessThanOrEqualTo(root.get("count"),
                            (maxExample.getCount() == null ? Integer.MAX_VALUE : maxExample.getCount()))
            ));
            predicates.add(criteriaBuilder.and(
                    criteriaBuilder.greaterThanOrEqualTo(root.get("price"),
                            (minExample.getPrice() == null ? 0 : minExample.getPrice())),
                    criteriaBuilder.lessThanOrEqualTo(root.get("price"),
                            (maxExample.getPrice() == null ? Double.MAX_VALUE : maxExample.getPrice()))
            ));
            predicates.add(criteriaBuilder.and(
                    criteriaBuilder.greaterThanOrEqualTo(root.get("deliveryDate"),
                            (minExample.getDeliveryDate() == null ? LocalDateTime.MIN : minExample.getDeliveryDate())),
                    criteriaBuilder.lessThanOrEqualTo(root.get("deliveryDate"),
                            (maxExample.getDeliveryDate() == null ? LocalDateTime.MAX : maxExample.getDeliveryDate()))
            ));
            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        }, sort);
    }

}
