package com.aspire.Aspire.controller;

import com.aspire.Aspire.dto.LoanApplicationDTO;
import com.aspire.Aspire.model.LoanApplication;
import com.aspire.Aspire.service.LoanService;
import com.aspire.Aspire.user.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/loans")
public class LoanController {
    @Autowired
    private LoanService loanService;

    @PostMapping("/apply")
    public ResponseEntity<LoanApplication> applyForLoan(@RequestBody LoanApplicationDTO loanApplicationDTO) {
        LoanApplication loanApplication = loanService.applyForLoan(loanApplicationDTO);
        return new ResponseEntity<>(loanApplication, HttpStatus.CREATED);
    }

    @GetMapping("/{loanId}")
    public ResponseEntity<LoanApplication> getLoan(@PathVariable Long loanId,
                                                   @AuthenticationPrincipal UserDetails userDetails) {
        // Extract user ID from UserDetails
        Long userId = ((CustomUserDetails) userDetails).getUserId(); // Assuming CustomUserDetails

        // Fetch the loan belonging to the user
        LoanApplication loanApplication = loanService.getLoanById(loanId, userId);
        return ResponseEntity.ok(loanApplication);
    }
}
