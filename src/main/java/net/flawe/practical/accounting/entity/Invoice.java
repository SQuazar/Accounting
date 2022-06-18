package net.flawe.practical.accounting.entity;

import lombok.*;
import org.hibernate.annotations.Cascade;

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

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceType invoiceType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceDirection invoiceDirection;

    @Column(nullable = false)
    @Builder.Default
    private Integer count = 1;

    @Column(nullable = false)
    private String person;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime date = LocalDateTime.now();
}
