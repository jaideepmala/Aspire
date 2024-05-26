package com.aspire.Aspire.controller;

import com.aspire.Aspire.dto.RepaymentDTO;
import com.aspire.Aspire.model.LoanApplication;
import com.aspire.Aspire.model.Repayment;
import com.aspire.Aspire.service.RepaymentService;
import com.aspire.Aspire.user.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/repayments")
public class RepaymentController {
    @Autowired
    private RepaymentService repaymentService;

    @PostMapping("/submit/{loanApplicationId}")
    public ResponseEntity<Repayment> submitRepayment(@PathVariable Long loanApplicationId,
                                                     @RequestBody RepaymentDTO repaymentDTO) {
        Repayment repayment = repaymentService.submitRepayment(repaymentDTO, loanApplicationId);
        return new ResponseEntity<>(repayment, HttpStatus.CREATED);
    }


}
