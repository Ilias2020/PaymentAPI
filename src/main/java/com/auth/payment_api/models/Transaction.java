package com.auth.payment_api.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private BigDecimal amount;
    private Date transactionDate;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    public Transaction(BigDecimal amount, Date transactionDate, Customer customer) {
        this.amount = amount;
        this.transactionDate = transactionDate;
        this.customer = customer;
    }

}
