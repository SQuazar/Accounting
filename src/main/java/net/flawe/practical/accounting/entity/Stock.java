package net.flawe.practical.accounting.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "stock")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY,
            generator = "stock_id_generator")
    @JsonProperty("product_id")
    private Integer productId;

    @Column(nullable = false)
    @JsonProperty("product_name")
    private String productName;

    @Column(nullable = false)
    private Integer count;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    @Builder.Default
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @JsonProperty("delivery_date")
    private LocalDateTime deliveryDate = LocalDateTime.now();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Invoice> invoices;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stock stock = (Stock) o;
        return Objects.equals(productId, stock.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId);
    }
}
