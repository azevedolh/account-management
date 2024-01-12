package br.com.teste.accountmanagement.model;

import br.com.teste.accountmanagement.enumerator.TransactionStatusEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "origin_id")
    private Account origin;

    @ManyToOne
    @JoinColumn(name = "destination_id")
    private Account destination;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private TransactionStatusEnum status;

    @ManyToOne
    @JoinColumn(name = "reference_id")
    private Transaction referenceTransaction;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
