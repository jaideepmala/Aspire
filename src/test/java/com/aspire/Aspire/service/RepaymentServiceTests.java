package com.aspire.Aspire.service;

import com.aspire.Aspire.dto.RepaymentDTO;
import com.aspire.Aspire.enums.LoanApplicationStatus;
import com.aspire.Aspire.enums.RepaymentStatus;
import com.aspire.Aspire.model.LoanApplication;
import com.aspire.Aspire.model.Repayment;
import com.aspire.Aspire.repository.LoanApplicationRepository;
import com.aspire.Aspire.repository.RepaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RepaymentServiceTests {

    @Mock
    private RepaymentRepository repaymentRepository;

    @Mock
    private LoanApplicationRepository loanApplicationRepository;

    @InjectMocks
    private RepaymentService repaymentService;

    private RepaymentDTO repaymentDTO;
    private Long loanApplicationId;
    private LoanApplication loanApplication;
    private List<Repayment> repayments;

    @BeforeEach
    void setUp() {
        // Create repayment DTO
        repaymentDTO = new RepaymentDTO();
        repaymentDTO.setRepaymentAmount(BigDecimal.valueOf(1000));

        // Create loan application ID
        loanApplicationId = 1L;

        // Create loan application object
        loanApplication = new LoanApplication();
        loanApplication.setId(loanApplicationId);

        // Initialize repayments list and add repayments
        repayments = new ArrayList<>();
        Repayment repayment1 = new Repayment();
        repayment1.setRepaymentAmount(BigDecimal.valueOf(500));
        // Set other properties of repayment1 as needed
        repayments.add(repayment1);
        Repayment repayment2 = new Repayment();
        repayment2.setRepaymentAmount(BigDecimal.valueOf(500));
        // Set other properties of repayment2 as needed
        repayments.add(repayment2);

        // Set repayments list to loan application
        loanApplication.setRepayments(repayments);
    }

    @Test
    void submitRepayment_Success() {
        // Arrange
        when(loanApplicationRepository.findById(loanApplicationId)).thenReturn(Optional.of(loanApplication));
        when(repaymentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Repayment result = repaymentService.submitRepayment(repaymentDTO, loanApplicationId);

        // Assert
        assertNotNull(result);
        assertEquals(RepaymentStatus.PAID, result.getStatus());
        assertEquals(loanApplication, result.getLoanApplication());
    }

    @Test
    void submitRepayment_RepaymentAmountSufficient_StatusPaid() {
        // Arrange
        loanApplication.setTotalRepaymentAmount(BigDecimal.valueOf(1000));
        when(loanApplicationRepository.findById(loanApplicationId)).thenReturn(Optional.of(loanApplication));
        when(repaymentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        repayments.get(0).setStatus(RepaymentStatus.PAID);
        repayments.get(1).setStatus(RepaymentStatus.PAID);
        // Act
        Repayment result = repaymentService.submitRepayment(repaymentDTO, loanApplicationId);

        // Assert
        assertEquals(RepaymentStatus.PAID, result.getStatus());
        verify(loanApplicationRepository, times(1)).save(loanApplication);
    }

    @Test
    void updateLoanStatusIfAllRepaymentsPaid_AllRepaymentsPaid() {
        // Arrange
        loanApplication.setRepayments(repayments);
        repayments.add(new Repayment());
        repayments.add(new Repayment());
        repayments.get(0).setStatus(RepaymentStatus.PAID);
        repayments.get(1).setStatus(RepaymentStatus.PAID);
        repayments.get(2).setStatus(RepaymentStatus.PAID);
        repayments.get(3).setStatus(RepaymentStatus.PAID);

        // Act
        repaymentService.updateLoanStatusIfAllRepaymentsPaid(loanApplication);

        // Assert
        assertEquals(LoanApplicationStatus.PAID, loanApplication.getStatus());
        verify(loanApplicationRepository, times(1)).save(loanApplication);
    }

    @Test
    void updateLoanStatusIfAllRepaymentsPaid_NotAllRepaymentsPaid() {
        // Arrange
        loanApplication.setRepayments(repayments);
        repayments.add(new Repayment());
        repayments.add(new Repayment());
        repayments.get(0).setStatus(RepaymentStatus.PAID);
        repayments.get(1).setStatus(RepaymentStatus.PENDING);

        // Act
        repaymentService.updateLoanStatusIfAllRepaymentsPaid(loanApplication);

        // Assert
        assertNotEquals(LoanApplicationStatus.PAID, loanApplication.getStatus());
        verify(loanApplicationRepository, never()).save(loanApplication);
    }

    @Test
    void areAllRepaymentsPaid_AllRepaymentsPaid() {
        // Arrange
        repayments.add(new Repayment());
        repayments.add(new Repayment());
        repayments.get(0).setStatus(RepaymentStatus.PAID);
        repayments.get(1).setStatus(RepaymentStatus.PAID);
        repayments.get(2).setStatus(RepaymentStatus.PAID);
        repayments.get(3).setStatus(RepaymentStatus.PAID);
        loanApplication.setRepayments(repayments);

        // Act
        boolean result = repaymentService.areAllRepaymentsPaid(loanApplication);

        // Assert
        assertTrue(result);
    }

    @Test
    void areAllRepaymentsPaid_NotAllRepaymentsPaid() {
        // Arrange
        repayments.add(new Repayment());
        repayments.add(new Repayment());
        repayments.get(0).setStatus(RepaymentStatus.PAID);
        repayments.get(1).setStatus(RepaymentStatus.PENDING);
        loanApplication.setRepayments(repayments);

        // Act
        boolean result = repaymentService.areAllRepaymentsPaid(loanApplication);

        // Assert
        assertFalse(result);
    }
}
