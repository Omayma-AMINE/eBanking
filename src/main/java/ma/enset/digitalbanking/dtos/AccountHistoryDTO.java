package ma.enset.digitalbanking.dtos;

import lombok.Data;

import java.util.List;
@Data
public class AccountHistoryDTO {
    private String accountID ;
    private double balance;
    private String accountType ;
    private int currentPage;
    private int totalPages;
    private int pageSize;
    private List<OperationDTO> operationDTOs;
}
