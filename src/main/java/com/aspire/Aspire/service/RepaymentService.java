package com.aspire.Aspire.service;

import com.aspire.Aspire.dto.RepaymentDTO;
import com.aspire.Aspire.enums.LoanApplicationStatus;
import com.aspire.Aspire.enums.RepaymentStatus;
import com.aspire.Aspire.model.LoanApplication;
import com.aspire.Aspire.model.Repayment;
import com.aspire.Aspire.repository.LoanApplicationRepository;
import com.aspire.Aspire.repository.RepaymentRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class RepaymentService {
    @Autowired
    private RepaymentRepository repaymentRepository;

    @Autowired
    private LoanApplicationRepository loanApplicationRepository;

    @Transactional
    public Repayment submitRepayment(RepaymentDTO repaymentDTO, Long loanApplicationId) {
        LoanApplication loanApplication = loanApplicationRepository.findById(loanApplicationId)
                .orElseThrow(() -> new IllegalArgumentException("Loan application not found with ID: " + loanApplicationId));

        Repayment repayment = new Repayment();
        repayment.setRepaymentAmount(repaymentDTO.getRepaymentAmount());
        repayment.setRepaymentDate(new Date());
        repayment.setStatus(RepaymentStatus.PENDING);
        repayment.setLoanApplication(loanApplication);

        // Check if repayment amount is sufficient
        if (repayment.getRepaymentAmount().compareTo(loanApplication.getTotalRepaymentAmount()) >= 0) {
            repayment.setStatus(RepaymentStatus.PAID);
            // Update loan application status if all repayments are paid
            updateLoanStatusIfAllRepaymentsPaid(loanApplication);
        }

        repaymentRepository.save(repayment);

        return repayment;
    }

    @Transactional
    public boolean areAllRepaymentsPaid(LoanApplication loanApplication) {
        return loanApplication.getRepayments().stream()
                .allMatch(repayment -> repayment.getStatus() == RepaymentStatus.PAID);
    }

    @Transactional
    public void updateLoanStatusIfAllRepaymentsPaid(LoanApplication loanApplication) {
        if (areAllRepaymentsPaid(loanApplication)) {
            loanApplication.setStatus(LoanApplicationStatus.PAID);
            loanApplicationRepository.save(loanApplication);
        }
    }
}
