package me.radek203.creditcardservice.repository;

import me.radek203.creditcardservice.entity.Bank;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankRepository extends CrudRepository<Bank, Long> {
}
