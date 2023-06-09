package ma.enset.digitalbanking.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.enset.digitalbanking.dtos.*;
import ma.enset.digitalbanking.entities.*;
import ma.enset.digitalbanking.enums.OperationType;
import ma.enset.digitalbanking.exceptions.BalanceNotSufficientException;
import ma.enset.digitalbanking.exceptions.BankAccountNotFoundException;
import ma.enset.digitalbanking.exceptions.CustomerNotFoundException;
import ma.enset.digitalbanking.mappers.BankAccountMapperImpl;
import ma.enset.digitalbanking.repositories.BankAccountRepository;
import ma.enset.digitalbanking.repositories.CustomerRepository;
import ma.enset.digitalbanking.repositories.OperationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class BankAccountServiceImpl implements BankAccountService {
    private CustomerRepository customerRepository;
    private BankAccountRepository bankAccountRepository;
    private OperationRepository operationRepository;
    private BankAccountMapperImpl bankMapper ;

    @Override
    public CustomerDTO saveCustomer(CustomerDTO customerDTO) {
        log.info("Saving new customer");
        Customer customer = bankMapper.fromCustomerDTO(customerDTO) ;
        Customer savedCustomer = customerRepository.save(customer);
        return bankMapper.fromCustomer(savedCustomer);
    }

    @Override
    public CustomerDTO updateCustomer(CustomerDTO customerDTO) {
        log.info("Saving new customer");
        Customer customer = bankMapper.fromCustomerDTO(customerDTO) ;
        Customer savedCustomer = customerRepository.save(customer);
        return bankMapper.fromCustomer(savedCustomer);
    }
    @Override
    public void deleteCustomer(Long costumerId){
        customerRepository.deleteById(costumerId);
    }

    @Override
    public CurrentBankAccountDTO saveCurrentAccount(double initialBalance, double overDraft, Long customerId) throws CustomerNotFoundException {
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

        return bankMapper.fromCurrentBankAccount(savedBankAccount);
    }

    @Override
    public SavingBankAccountDTO saveSavingAccount(double initialBalance, double interestRate, Long customerId) throws CustomerNotFoundException {
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

        return bankMapper.fromSavingBankAccount(savedBankAccount);
    }


    @Override
    public List<CustomerDTO> listCustomers() {
        List<Customer> customers = customerRepository.findAll();
        List<CustomerDTO> customerDTOS = customers.stream().map(customer ->
                bankMapper.fromCustomer(customer)
                ).collect(Collectors.toList());

        return customerDTOS;
    }

    @Override
    public BankAccountDTO getBankAccount(String accountID) throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(accountID).orElseThrow(() -> new BankAccountNotFoundException("Bank Account not fount"));
        if (bankAccount instanceof SavingAccount){
            SavingAccount savingAccount=(SavingAccount) bankAccount;
            return bankMapper.fromSavingBankAccount(savingAccount);
        }else{
            CurrentAccount currentAccount = (CurrentAccount) bankAccount;
            return  bankMapper.fromCurrentBankAccount(currentAccount);
        }
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
        operation.setDescription(description);
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
        operation.setDescription(description);

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
    public List<BankAccountDTO> bankAccountList(){
       List <BankAccount> bankAccounts =  bankAccountRepository.findAll();

        List<BankAccountDTO> bankAccountDTOS = bankAccounts.stream().map(bankAccount -> {
            if (bankAccount instanceof SavingAccount) {
                SavingAccount savingAccount = (SavingAccount) bankAccount;
                return bankMapper.fromSavingBankAccount(savingAccount);
            } else {
                CurrentAccount currentAccount = (CurrentAccount) bankAccount;
                return bankMapper.fromCurrentBankAccount(currentAccount);
            }
        }).collect(Collectors.toList());
        return bankAccountDTOS;
    }
    @Override
    public CustomerDTO getCustomer(Long customerID) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerID).
                orElseThrow(() -> new CustomerNotFoundException("Customer Not found"));
        return bankMapper.fromCustomer(customer);
    }
    @Override
    public List<OperationDTO> accountHistory(String accountId){
        List<Operation> accountOperations = operationRepository.findByBankAccount_IdAccount(accountId);
        return accountOperations.stream().map(operation -> bankMapper.fromOperation(operation)).collect(Collectors.toList());
    }

    @Override
    public AccountHistoryDTO getAccountHistory(String accountId, int page, int size) throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId).orElse(null);
        if (bankAccount == null) throw new BankAccountNotFoundException("Account not founf");
        Page<Operation> accountOperations = operationRepository.findByBankAccount_IdAccount(accountId, PageRequest.of(page, size));
        AccountHistoryDTO accountHistoryDTO = new AccountHistoryDTO();
        List<OperationDTO> accountOperationDTOS = accountOperations.getContent().stream().map(operation ->
                bankMapper.fromOperation(operation)
        ).collect(Collectors.toList());
        accountHistoryDTO.setOperationDTOs(accountOperationDTOS);
        accountHistoryDTO.setAccountID(bankAccount.getIdAccount());
        accountHistoryDTO.setBalance(bankAccount.getBalance());
        accountHistoryDTO.setCurrentPage(page);
        accountHistoryDTO.setPageSize(size);
        accountHistoryDTO.setTotalPages(accountHistoryDTO.getTotalPages());
        if (bankAccount instanceof SavingAccount)
            accountHistoryDTO.setAccountType(((SavingAccount)bankAccount).getClass().getName());
        else
            accountHistoryDTO.setAccountType(((CurrentAccount)bankAccount).getClass().getName());

        return accountHistoryDTO;
    }
}
