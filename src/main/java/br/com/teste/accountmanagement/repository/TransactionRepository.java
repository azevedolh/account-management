package br.com.teste.accountmanagement.repository;

import br.com.teste.accountmanagement.model.Account;
import br.com.teste.accountmanagement.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Page<Transaction> findAllByOriginOrDestination(PageRequest pageRequest, Account origin, Account destination);
}
