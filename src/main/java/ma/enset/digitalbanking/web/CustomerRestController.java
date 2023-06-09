package ma.enset.digitalbanking.web;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.enset.digitalbanking.dtos.CustomerDTO;
import ma.enset.digitalbanking.entities.Customer;
import ma.enset.digitalbanking.exceptions.CustomerNotFoundException;
import ma.enset.digitalbanking.services.BankAccountService;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
public class CustomerRestController  {

    private BankAccountService bankAccountService;



    @GetMapping("/customers")
    public List<CustomerDTO> customers(){

        return bankAccountService.listCustomers();
    }
    @GetMapping("/customers/{id}")
    public CustomerDTO getCustomer(@PathVariable(name = "id") Long customerID) throws CustomerNotFoundException {
        return bankAccountService.getCustomer(customerID);
    }
    @PostMapping("/customers")
    public CustomerDTO saveCustomer(@RequestBody CustomerDTO customerDTO){
        return bankAccountService.saveCustomer(customerDTO);
    }
    @PutMapping("customers/{customerId}")
    public CustomerDTO updateCustomer(@PathVariable Long customerId, @RequestBody CustomerDTO customerDTO){
       customerDTO.setIdCustomer(customerId);
       return bankAccountService.updateCustomer(customerDTO);
    }
    @DeleteMapping("customers/{id}")
    public void deleteCustomer(@PathVariable Long id){

        bankAccountService.deleteCustomer(id);
    }
}
