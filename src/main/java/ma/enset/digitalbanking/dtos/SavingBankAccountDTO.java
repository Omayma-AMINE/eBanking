package ma.enset.digitalbanking.dtos;

import lombok.Data;
import ma.enset.digitalbanking.enums.AccountStatus;
import java.util.Date;



@Data
public  class SavingBankAccountDTO extends BankAccountDTO{
    private String idAccount ;
    private Date createDate;
    private double balance;
    private AccountStatus status;
    private String currency ;
    private CustomerDTO customerDTO;
    private double interestRate ;
}
