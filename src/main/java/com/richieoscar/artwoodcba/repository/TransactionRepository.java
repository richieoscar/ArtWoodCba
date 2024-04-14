package com.richieoscar.artwoodcba.repository;

import com.richieoscar.artwoodcba.domain.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Page<Transaction> findAllByCustomerId(String customerId, Pageable pageable);
    Page<Transaction> findAllByCustomerIdAndAccountId(String customerId, String accountId, Pageable pageable);
}
