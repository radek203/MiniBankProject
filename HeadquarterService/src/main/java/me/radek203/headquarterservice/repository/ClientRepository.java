package me.radek203.headquarterservice.repository;

import me.radek203.headquarterservice.entity.Client;
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

    Optional<Client> findByAccountNumber(String accountNumber);

    List<Client> findByUserId(int userId);
}
