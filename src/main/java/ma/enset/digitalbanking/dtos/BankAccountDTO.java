package ma.enset.digitalbanking.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.enset.digitalbanking.entities.Customer;
import ma.enset.digitalbanking.entities.Operation;
import ma.enset.digitalbanking.enums.AccountStatus;

import java.util.Date;
import java.util.List;


@Data
public  class BankAccountDTO {

    private String type ;
}
