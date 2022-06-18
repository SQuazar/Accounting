package net.flawe.practical.accounting.repository;

import net.flawe.practical.accounting.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer>, JpaSpecificationExecutor<Product> {
    @Query("select product from Product product where product.stock.id=:stockId")
    List<Product> findAllByStockId(@Param("stockId") int stockId);
}
