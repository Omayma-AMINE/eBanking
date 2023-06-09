package ma.enset.digitalbanking.repositories;

import ma.enset.digitalbanking.entities.Operation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OperationRepository extends JpaRepository<Operation,Long> {
    List<Operation> findByBankAccount_IdAccount(String accountId);
    Page<Operation> findByBankAccount_IdAccount(String accountId, Pageable pageable);
}
