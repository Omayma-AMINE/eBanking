package ma.enset.digitalbanking.repositories;

import ma.enset.digitalbanking.entities.Operation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperationRepository extends JpaRepository<Operation,Long> {
}
