package ma.enset.digitalbanking;

import ma.enset.digitalbanking.entities.*;
import ma.enset.digitalbanking.enums.AccountStatus;
import ma.enset.digitalbanking.enums.OperationType;
import ma.enset.digitalbanking.exceptions.BalanceNotSufficientException;
import ma.enset.digitalbanking.exceptions.BankAccountNotFoundException;
import ma.enset.digitalbanking.exceptions.CustomerNotFoundException;
import ma.enset.digitalbanking.repositories.BankAccountRepository;
import ma.enset.digitalbanking.repositories.CustomerRepository;
import ma.enset.digitalbanking.repositories.OperationRepository;
import ma.enset.digitalbanking.services.BankAccountService;
import ma.enset.digitalbanking.services.BankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@SpringBootApplication
public class DigitalBankingApplication {

    public static void main(String[] args) {
        SpringApplication.run(DigitalBankingApplication.class, args);
    }
    @Bean
    CommandLineRunner startWithService(BankAccountService bankAccountService){
        return args -> {
            Stream.of("Ismail","Mariam","Nabil").forEach(name ->{
                Customer customer = new Customer();
                customer.setName(name);
                customer.setEmail(name+"@gmail.com");
                bankAccountService.saveCustomer(customer);
            });
            bankAccountService.listCustomers().forEach(customer -> {
                try {
                    bankAccountService.saveCurrentAccount(Math.round(Math.random()*9999),5000,customer.getIdCustomer());
                    bankAccountService.saveSavingAccount(Math.round(Math.random()*9999),2.5,customer.getIdCustomer());

                    bankAccountService.bankAccountList().forEach(bankAccount -> {
                        for (int i=0; i<5; i++){
                            try {
                                bankAccountService.credit(bankAccount.getIdAccount(),5000+Math.round(Math.random()*9999),"CREDIT");
                                bankAccountService.debit(bankAccount.getIdAccount(),1000+Math.round(Math.random()*5000),"DEBIT");
                            } catch (BankAccountNotFoundException | BalanceNotSufficientException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                } catch (CustomerNotFoundException e) {
                    e.printStackTrace();
                }
            });

        };
    }




   //@Bean
    CommandLineRunner start(CustomerRepository customerRepository,
                            BankAccountRepository bankAccountRepository,
                            OperationRepository operationRepository){
    return args -> {
        Stream.of("Omayma","Amine","Hamza").forEach(name ->{
            Customer customer = new Customer();
            customer.setName(name);
            customer.setEmail(name+"@gmail.com");
            customerRepository.save(customer);
        } );

        customerRepository.findAll().forEach(customer -> {
            CurrentAccount currentAccount = new CurrentAccount();
            currentAccount.setIdAccount(UUID.randomUUID().toString());
            currentAccount.setCreateDate(new Date());
            currentAccount.setBalance(Math.round(Math.random()*9950));
            currentAccount.setCurrency("MAD");
            currentAccount.setStatus(AccountStatus.CREATED);
            currentAccount.setOverDraft(10000);
            currentAccount.setCustomer(customer);

            bankAccountRepository.save(currentAccount);
        });

        customerRepository.findAll().forEach(customer -> {
            SavingAccount savingAccount = new SavingAccount();
            savingAccount.setIdAccount(UUID.randomUUID().toString());
            savingAccount.setCreateDate(new Date());
            savingAccount.setBalance(Math.round(Math.random()*9870));
            savingAccount.setCurrency("euro");
            savingAccount.setStatus(AccountStatus.ACTIVATED);
            savingAccount.setInterestRate(2.9);
            savingAccount.setCustomer(customer);

            bankAccountRepository.save(savingAccount);
        });

            bankAccountRepository.findAll().forEach(bankAccount -> {
                for (int i = 0; i<5; i++){
                    Operation operation = new Operation();
                    operation.setOperationDate(new Date());
                    operation.setType(Math.random()>0.5?OperationType.DEBIT : OperationType.CREDIT);
                    operation.setDesciption("descrition operation n"+Math.round(Math.random()*10));
                    operation.setAmount(Math.round(Math.random()*5550));
                    operation.setBankAccount(bankAccount);
                    operationRepository.save(operation);
                }
            });


    };
    }


    //@Bean
    CommandLineRunner commandLineRunner(BankService bankService){
        return args -> {
           bankService.consulter();
        };
    }



}
