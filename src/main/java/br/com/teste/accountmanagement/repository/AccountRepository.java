package br.com.teste.accountmanagement.repository;

import br.com.teste.accountmanagement.model.Account;
import br.com.teste.accountmanagement.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Page<Account> findAllByCustomer(PageRequest pageRequest, Customer customer);
}
