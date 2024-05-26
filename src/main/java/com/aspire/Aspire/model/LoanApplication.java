package com.aspire.Aspire.model;

import com.aspire.Aspire.enums.LoanApplicationStatus;
import jakarta.persistence.*;


import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
public class LoanApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal amountRequired;
    private int loanTerm;
    private Date applicationDate;

    @Enumerated(EnumType.STRING)
    private LoanApplicationStatus status;

    @OneToMany(mappedBy = "loanApplication")
    private List<Repayment> repayments;

    @ManyToOne(fetch = FetchType.LAZY) // Assuming FetchType.LAZY for performance
    @JoinColumn(name = "user_id") // Name of the foreign key column
    private User user; // Reference to User entity

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAmountRequired() {
        return amountRequired;
    }

    public void setAmountRequired(BigDecimal amountRequired) {
        this.amountRequired = amountRequired;
    }

    public int getLoanTerm() {
        return loanTerm;
    }

    public void setLoanTerm(int loanTerm) {
        this.loanTerm = loanTerm;
    }

    public Date getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(Date applicationDate) {
        this.applicationDate = applicationDate;
    }

    public LoanApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(LoanApplicationStatus status) {
        this.status = status;
    }

    public List<Repayment> getRepayments() {
        return repayments;
    }

    public void setRepayments(List<Repayment> repayments) {
        this.repayments = repayments;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BigDecimal getTotalRepaymentAmount() {
        if (amountRequired == null || amountRequired.compareTo(BigDecimal.ZERO) <= 0 || loanTerm <= 0) {
            return BigDecimal.ZERO;
        }
        // Calculate weekly repayment amount
        BigDecimal weeklyRepaymentAmount = amountRequired.divide(BigDecimal.valueOf(loanTerm), 2, BigDecimal.ROUND_HALF_UP);
        // Total repayment amount is weekly repayment amount * number of weeks (loan term)
        return weeklyRepaymentAmount.multiply(BigDecimal.valueOf(loanTerm));
    }

    public void setTotalRepaymentAmount(BigDecimal totalRepaymentAmount) {
    }
}