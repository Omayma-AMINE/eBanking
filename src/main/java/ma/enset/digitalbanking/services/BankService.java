package ma.enset.digitalbanking.services;

import ma.enset.digitalbanking.entities.BankAccount;
import ma.enset.digitalbanking.entities.CurrentAccount;
import ma.enset.digitalbanking.entities.SavingAccount;
import ma.enset.digitalbanking.repositories.BankAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@Transactional
public class BankService {
    @Autowired
    private BankAccountRepository bankAccountRepository;

    public void consulter(){
        List<BankAccount> bankAccountList = bankAccountRepository.findAll();
        for (BankAccount bankAccount:bankAccountList) {

            if(bankAccount instanceof CurrentAccount){
                System.out.println("\nCurrent Account : ");
                System.out.print("\t\t"+bankAccount.getIdAccount());
                System.out.print("\t\t"+bankAccount.getBalance());
                System.out.print("\t\t"+bankAccount.getCurrency());
                System.out.print("\t\t"+bankAccount.getStatus());
                System.out.print("\t\t"+bankAccount.getCreateDate());
                System.out.print("\t\t"+((CurrentAccount) bankAccount).getOverDraft());
            }
            else if(bankAccount instanceof SavingAccount){
                System.out.println("\nSaving Account : ");
                System.out.print("\t\t"+bankAccount.getIdAccount());
                System.out.print("\t\t"+bankAccount.getBalance());
                System.out.print("\t\t"+bankAccount.getCurrency());
                System.out.print("\t\t"+bankAccount.getStatus());
                System.out.print("\t\t"+bankAccount.getCreateDate());
                System.out.print("\t\t"+((SavingAccount) bankAccount).getInterestRate());
            }
        }
    }
}
