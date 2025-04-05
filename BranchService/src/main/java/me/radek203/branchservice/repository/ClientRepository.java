package me.radek203.branchservice.repository;

import me.radek203.branchservice.entity.Client;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ClientRepository extends CrudRepository<Client, UUID> {

    @Query(nativeQuery = true, value = "SELECT c.account_number FROM client c")
    List<String> getAllAccountNumbers();
}
