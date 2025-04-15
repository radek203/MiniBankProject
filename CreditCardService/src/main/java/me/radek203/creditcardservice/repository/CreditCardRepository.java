package me.radek203.creditcardservice.repository;

import me.radek203.creditcardservice.entity.CreditCard;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * CreditCardRepository interface for accessing credit card data in the database.
 * It extends CrudRepository to provide CRUD operations and custom query methods.
 */
@Repository
public interface CreditCardRepository extends CrudRepository<CreditCard, Long> {

    @Query(nativeQuery = true, value = "SELECT c.card_number FROM credit_card c")
    List<String> getAllNumbers();

    Optional<CreditCard> findByCardNumber(String cardNumber);

    List<CreditCard> findAllByAccountNumberIn(Collection<String> accountNumbers);
}
