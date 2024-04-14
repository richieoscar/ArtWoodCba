package com.richieoscar.artwoodcba.repository;

import com.richieoscar.artwoodcba.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountNumber(String accountNumber);
    List<Account> findAllByCustomer_Id(long accountNumber);
    Optional<Account> findByAccountNumberAndCustomer_Id(String accountNumber, Long id);
}
