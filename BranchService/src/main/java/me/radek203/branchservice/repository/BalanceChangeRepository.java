package me.radek203.branchservice.repository;

import me.radek203.branchservice.entity.BalanceChange;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface BalanceChangeRepository extends CrudRepository<BalanceChange, UUID> {
}
