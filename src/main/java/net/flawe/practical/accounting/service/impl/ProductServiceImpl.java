package net.flawe.practical.accounting.service.impl;

import net.flawe.practical.accounting.entity.Product;
import net.flawe.practical.accounting.exception.ProductCountException;
import net.flawe.practical.accounting.exception.ProductException;
import net.flawe.practical.accounting.exception.ProductNotFoundException;
import net.flawe.practical.accounting.repository.ProductRepository;
import net.flawe.practical.accounting.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product getById(Integer id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(String.format("Продукт с id %d не найден!", id)));
    }

    @Override
    public Product save(Product value) {
        if (value.getName().isEmpty())
            throw new ProductException("Product name is empty!");
        if (value.getCount() <= 0)
            throw new ProductCountException("Количество продуктов не может быть меньше одного!");
        return productRepository.save(value);
    }

    @Override
    public void delete(Product value) {
        productRepository.delete(value);
    }

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> findAll(Sort sort) {
        return productRepository.findAll(sort);
    }

    @Override
    public List<Product> findByExamples(Product minExample, Product maxExample, boolean nameIgnoreCase, boolean nameStartsWith, Sort sort) {
        if (minExample == null || maxExample == null) return List.of();
        return productRepository.findAll((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (minExample.getName() != null) {
                predicates.add(
                        criteriaBuilder.like(
                                (nameIgnoreCase ? criteriaBuilder.upper(root.get("name")) : root.get("name")),
                                (nameStartsWith ? minExample.getName() + "%" : minExample.getName())
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
                    criteriaBuilder.greaterThanOrEqualTo(root.get("purchaseDate"),
                            (minExample.getPurchaseDate() == null ? LocalDateTime.MIN : minExample.getPurchaseDate())),
                    criteriaBuilder.lessThanOrEqualTo(root.get("purchaseDate"),
                            (maxExample.getPurchaseDate() == null ? LocalDateTime.MAX : maxExample.getPurchaseDate()))
            ));
            if (minExample.getStock() != null)
                predicates.add(criteriaBuilder.equal(root.get("stock"), minExample.getStock().getId()));
            if (minExample.getRefundable() != null)
                predicates.add(criteriaBuilder.equal(root.get("refundable"), minExample.getRefundable()));
            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        }, sort);
    }

    @Override
    public List<Product> findByIds(List<Integer> ids) {
        return productRepository.findAllById(ids);
    }

    @Override
    public void saveAll(List<Product> products) {
        productRepository.saveAll(products);
    }

    @Override
    public void deleteAll(List<Integer> ids) {
        productRepository.deleteAllByIdInBatch(ids);
    }

    @Override
    public void deleteById(Integer id) {
        productRepository.deleteById(id);
    }

    @Override
    public List<Product> getAllByStockId(Integer stockId) {
        return productRepository.findAllByStockId(stockId);
    }
}
