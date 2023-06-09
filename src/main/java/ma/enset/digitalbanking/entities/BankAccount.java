package ma.enset.digitalbanking.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.enset.digitalbanking.enums.AccountStatus;

import java.util.Date;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TYPE",length = 4)
@Data @NoArgsConstructor @AllArgsConstructor
public abstract class BankAccount {
    @Id
    private String idAccount ;
    @Temporal(TemporalType.DATE)
    private Date createDate;
    private double balance;
    @Enumerated(EnumType.STRING)
    private AccountStatus status;
    private String currency ;
    @ManyToOne
    private Customer customer;
    @OneToMany(mappedBy = "bankAccount")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<Operation> operationList;
}
