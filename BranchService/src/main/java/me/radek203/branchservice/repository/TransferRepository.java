package me.radek203.branchservice.repository;

import me.radek203.branchservice.entity.Transfer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * TransferRepository interface for accessing transfer data in the database.
 * It extends CrudRepository to provide CRUD operations and custom query methods.
 */
@Repository
public interface TransferRepository extends CrudRepository<Transfer, UUID> {
    List<Transfer> findAllByFromAccountOrToAccount(String fromAccount, String toAccount);
}
