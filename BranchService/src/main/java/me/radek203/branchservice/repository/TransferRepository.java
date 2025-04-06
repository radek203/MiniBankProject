package me.radek203.branchservice.repository;

import me.radek203.branchservice.entity.Transfer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TransferRepository extends CrudRepository<Transfer, UUID> {
}
