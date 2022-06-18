package net.flawe.practical.accounting.service;

import net.flawe.practical.accounting.entity.Product;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface ProductService extends DefaultService<Product, Integer> {
    List<Product> findByExamples(Product minExample, Product maxExample,
                                 boolean nameIgnoreCase, boolean nameStartsWith,
                                 Sort sort);

    List<Product> findByIds(List<Integer> ids);

    void saveAll(List<Product> products);

    void deleteAll(List<Integer> ids);

    void deleteById(Integer id);

    List<Product> getAllByStockId(Integer stockId);
}
