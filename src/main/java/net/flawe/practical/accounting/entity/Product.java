package net.flawe.practical.accounting.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Table(name = "product")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY,
            generator = "product_id_generator")
    private Integer id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private Integer count;
    @Column(nullable = false)
    private Double price;
    @Column(nullable = false)
    @Builder.Default
    private Boolean refundable = true;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "stock_id", nullable = false)
    @ToString.Exclude
    private Stock stock;
    @Builder.Default
    private LocalDateTime purchaseDate = LocalDateTime.now();
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Invoice> invoices;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
