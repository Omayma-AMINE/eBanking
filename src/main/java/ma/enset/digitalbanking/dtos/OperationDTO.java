package ma.enset.digitalbanking.dtos;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.enset.digitalbanking.entities.BankAccount;
import ma.enset.digitalbanking.enums.OperationType;

import java.util.Date;

@Data
public class OperationDTO {

    private Long idOperation ;
    private Date operationDate;
    private double amount;
    private String description;
    private OperationType type;

}
