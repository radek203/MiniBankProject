package me.radek203.creditcardservice.repository;

import me.radek203.creditcardservice.entity.Bank;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for accessing Bank entities in the database.
 * It extends CrudRepository to provide basic CRUD operations.
 */
@Repository
public interface BankRepository extends CrudRepository<Bank, Long> {
}
