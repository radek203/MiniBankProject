package me.radek203.branchservice.repository;

import me.radek203.branchservice.entity.BalanceChange;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * BalanceChangeRepository interface for accessing balance change data in the database.
 * It extends CrudRepository to provide CRUD operations and custom query methods.
 */
@Repository
public interface BalanceChangeRepository extends CrudRepository<BalanceChange, UUID> {
    List<BalanceChange> getBalanceChangesByAccount(String account);
}
