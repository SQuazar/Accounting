package net.flawe.practical.accounting.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoice")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY,
            generator = "invoice_id_generator")
    @Column(nullable = false)
    private Integer id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id", nullable = false)
    private Stock product;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceType invoiceType;

    @Column(nullable = false)
    @Builder.Default
    private int count = 1;

    @Column(nullable = false)
    private String person;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime date = LocalDateTime.now();

    public void setStock(Stock product) {
        this.product = product;
    }
}
