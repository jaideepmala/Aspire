package com.aspire.Aspire.controller;

import com.aspire.Aspire.model.LoanApplication;
import com.aspire.Aspire.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/loans")
public class AdminController {

    @Autowired
    private LoanService loanService;

    @PutMapping("/{loanApplicationId}/approve")
    public ResponseEntity<LoanApplication> approveLoan(@PathVariable Long loanApplicationId) {
        LoanApplication approvedLoan = loanService.approveLoan(loanApplicationId);
        return ResponseEntity.ok(approvedLoan);
    }
}
