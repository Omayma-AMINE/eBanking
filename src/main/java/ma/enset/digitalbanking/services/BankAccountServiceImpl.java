package ma.enset.digitalbanking.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.enset.digitalbanking.entities.*;
import ma.enset.digitalbanking.enums.OperationType;
import ma.enset.digitalbanking.exceptions.BalanceNotSufficientException;
import ma.enset.digitalbanking.exceptions.BankAccountNotFoundException;
import ma.enset.digitalbanking.exceptions.CustomerNotFoundException;
import ma.enset.digitalbanking.repositories.BankAccountRepository;
import ma.enset.digitalbanking.repositories.CustomerRepository;
import ma.enset.digitalbanking.repositories.OperationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class BankAccountServiceImpl implements BankAccountService {
    private CustomerRepository customerRepository;
    private BankAccountRepository bankAccountRepository;
    private OperationRepository operationRepository;


    @Override
    public Customer saveCustomer(Customer customer) {
        log.info("Saving new customer");
        Customer newCustomer = customerRepository.save(customer);
        return newCustomer;
    }

    @Override
    public CurrentAccount saveCurrentAccount(double initialBalance, double overDraft, Long customerId) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer==null)
            throw new CustomerNotFoundException("Customer not found");
        CurrentAccount currentAccount = new CurrentAccount();
        currentAccount.setIdAccount(UUID.randomUUID().toString());
        currentAccount.setCreateDate(new Date());
        currentAccount.setBalance(initialBalance);
        currentAccount.setCurrency("MAD");
        currentAccount.setOverDraft(overDraft);
        currentAccount.setCustomer(customer);

        CurrentAccount savedBankAccount = bankAccountRepository.save(currentAccount);

        return savedBankAccount;
    }

    @Override
    public SavingAccount saveSavingAccount(double initialBalance, double interestRate, Long customerId) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer==null)
            throw new CustomerNotFoundException("Customer not found");
        SavingAccount savingAccount = new SavingAccount();
        savingAccount.setIdAccount(UUID.randomUUID().toString());
        savingAccount.setCreateDate(new Date());
        savingAccount.setBalance(initialBalance);
        savingAccount.setCurrency("MAD");
        savingAccount.setInterestRate(interestRate);
        savingAccount.setCustomer(customer);

        SavingAccount savedBankAccount = bankAccountRepository.save(savingAccount);

        return savedBankAccount;
    }


    @Override
    public List<Customer> listCustomers() {
        return customerRepository.findAll();
    }

    @Override
    public BankAccount getBankAccount(String accountID) throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(accountID).orElseThrow(() -> new BankAccountNotFoundException("Bank Account not fount"));

        return bankAccount;
    }

    @Override
    public void debit(String accountID, double amount, String description) throws BankAccountNotFoundException, BalanceNotSufficientException {
        BankAccount bankAccount = bankAccountRepository.findById(accountID).orElseThrow(() -> new BankAccountNotFoundException("Bank Account not fount"));
        if (bankAccount instanceof CurrentAccount)
            if (bankAccount.getBalance()+((CurrentAccount) bankAccount).getOverDraft() < amount )
                throw new BalanceNotSufficientException("Balance not sufficient");
        if (bankAccount instanceof SavingAccount)
            if (bankAccount.getBalance() < amount)
                throw new BalanceNotSufficientException("Balance not sufficient");

        Operation operation = new Operation();
        operation.setType(OperationType.DEBIT);
        operation.setOperationDate(new Date());
        operation.setAmount(amount);
        operation.setDesciption(description);
        operation.setBankAccount(bankAccount);
        operationRepository.save(operation);
        bankAccount.setBalance(bankAccount.getBalance()-amount);
        bankAccountRepository.save(bankAccount);

    }

    @Override
    public void credit(String accountID, double amount, String description) throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(accountID).orElseThrow(() -> new BankAccountNotFoundException("Bank Account not fount"));

        Operation operation = new Operation();
        operation.setType(OperationType.CREDIT);
        operation.setOperationDate(new Date());
        operation.setAmount(amount);
        operation.setDesciption(description);

        operation.setBankAccount(bankAccount);
        operationRepository.save(operation);
        bankAccount.setBalance(bankAccount.getBalance()+amount);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void transfer(String accountIdSource, String accountIdDestination, double amount) throws BankAccountNotFoundException, BalanceNotSufficientException {
        debit(accountIdSource,amount,"Transfer to"+accountIdDestination);
        credit(accountIdDestination,amount,"Transfer from"+accountIdSource);

    }
    @Override
    public List<BankAccount> bankAccountList(){
        return bankAccountRepository.findAll();
    }
}
