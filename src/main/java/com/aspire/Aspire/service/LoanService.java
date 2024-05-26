package com.aspire.Aspire.service;


import com.aspire.Aspire.dto.LoanApplicationDTO;
import com.aspire.Aspire.enums.LoanApplicationStatus;
import com.aspire.Aspire.enums.RepaymentStatus;
import com.aspire.Aspire.model.LoanApplication;
import com.aspire.Aspire.model.Repayment;
import com.aspire.Aspire.repository.LoanApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Service
public class LoanService {
    @Autowired
    private LoanApplicationRepository loanApplicationRepository;

    public LoanApplication applyForLoan(LoanApplicationDTO loanApplicationDTO) {
        LoanApplication loanApplication = new LoanApplication();
        loanApplication.setAmountRequired(loanApplicationDTO.getAmountRequired());
        loanApplication.setLoanTerm(loanApplicationDTO.getLoanTerm());
        loanApplication.setApplicationDate(new Date());
        loanApplication.setStatus(LoanApplicationStatus.PENDING);

        // Generate repayments based on loan term
        generateRepayments(loanApplication);

        loanApplicationRepository.save(loanApplication);
        return loanApplication;
    }

    public LoanApplication approveLoan(final Long loanApplicationId) {
        LoanApplication loanApplication = loanApplicationRepository.findById(loanApplicationId)
                .orElseThrow(() -> new IllegalArgumentException("Loan application not found with ID: " + loanApplicationId));

        if (loanApplication.getStatus() != LoanApplicationStatus.PENDING) {
            throw new IllegalStateException("Loan application is not in pending state");
        }

        loanApplication.setStatus(LoanApplicationStatus.APPROVED);
        return loanApplicationRepository.save(loanApplication);
    }

    public LoanApplication getLoanById(Long loanId, Long userId) {
        // Retrieve the loan application by its ID
        LoanApplication loanApplication = loanApplicationRepository.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Loan application not found with ID: " + loanId));

        // Check if the loan belongs to the user
        if (!loanApplication.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You do not have permission to access this loan");
        }

        return loanApplication;
    }

    private void generateRepayments(LoanApplication loanApplication) {
        final int loanTerm = loanApplication.getLoanTerm();
        BigDecimal amountRequired = loanApplication.getAmountRequired();
        BigDecimal weeklyRepaymentAmount = amountRequired.divide(BigDecimal.valueOf(loanTerm), 2, BigDecimal.ROUND_HALF_UP);
        List<Repayment> repayments = new LinkedList<>();

        for (int i = 1; i <= loanTerm; i++) {
            Repayment repayment = new Repayment();
            repayment.setRepaymentAmount(weeklyRepaymentAmount);
            // Assuming weekly repayments starting from the application date
            repayment.setRepaymentDate(new Date(loanApplication.getApplicationDate().getTime() + i * 7 * 24 * 60 * 60 * 1000));
            repayment.setStatus(RepaymentStatus.PENDING);
            repayment.setLoanApplication(loanApplication);
            repayments.add(repayment);
        }

        loanApplication.setRepayments(repayments);
    }


}