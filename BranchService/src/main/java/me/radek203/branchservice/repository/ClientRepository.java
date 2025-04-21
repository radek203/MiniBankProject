package me.radek203.branchservice.repository;

import me.radek203.branchservice.entity.Client;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * ClientRepository interface for accessing client data in the database.
 * It extends CrudRepository to provide CRUD operations and custom query methods.
 */
@Repository
public interface ClientRepository extends CrudRepository<Client, UUID> {

    @Query(nativeQuery = true, value = "SELECT c.account_number FROM client c")
    List<String> getAllAccountNumbers();

    Optional<Client> findByAccountNumber(String accountNumber);

    Optional<Client> findByIdAndUserId(UUID id, int userId);

    Optional<Client> findByAccountNumberAndUserId(String accountNumber, int userId);
}
