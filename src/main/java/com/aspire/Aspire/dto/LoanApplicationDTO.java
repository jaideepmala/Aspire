package com.aspire.Aspire.dto;

import java.math.BigDecimal;

public class LoanApplicationDTO {
    private BigDecimal amountRequired;
    private int loanTerm;

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
}