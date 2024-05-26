package com.aspire.Aspire.service;

import com.aspire.Aspire.dto.LoanApplicationDTO;
import com.aspire.Aspire.enums.LoanApplicationStatus;
import com.aspire.Aspire.model.LoanApplication;
import com.aspire.Aspire.model.User;
import com.aspire.Aspire.repository.LoanApplicationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanServiceTests {

    @Mock
    private LoanApplicationRepository loanApplicationRepository;

    @InjectMocks
    private LoanService loanService;

    private LoanApplicationDTO loanApplicationDTO;

    @BeforeEach
    void setUp() {
        loanApplicationDTO = new LoanApplicationDTO();
        loanApplicationDTO.setAmountRequired(BigDecimal.valueOf(10000));
        loanApplicationDTO.setLoanTerm(3);
    }

    @Test
    void applyForLoan_Success() {
        // Arrange
        LoanApplication loanApplication = new LoanApplication();
        when(loanApplicationRepository.save(any())).thenReturn(loanApplication);

        // Act
        LoanApplication result = loanService.applyForLoan(loanApplicationDTO);

        // Assert
        assertNotNull(result);
        assertEquals(LoanApplicationStatus.PENDING, result.getStatus());
        assertEquals(BigDecimal.valueOf(10000), result.getAmountRequired());
        assertEquals(3, result.getLoanTerm());
        assertEquals(3, result.getRepayments().size());
    }

    @Test
    void approveLoan_Success() {
        // Arrange
        Long loanApplicationId = 1L;
        LoanApplication loanApplication = new LoanApplication();
        loanApplication.setStatus(LoanApplicationStatus.PENDING);
        when(loanApplicationRepository.findById(loanApplicationId)).thenReturn(Optional.of(loanApplication));
        when(loanApplicationRepository.save(any())).thenReturn(loanApplication);

        // Act
        LoanApplication result = loanService.approveLoan(loanApplicationId);

        // Assert
        assertNotNull(result);
        assertEquals(LoanApplicationStatus.APPROVED, result.getStatus());
    }

    @Test
    void approveLoan_InvalidState_ThrowsIllegalStateException() {
        // Arrange
        Long loanApplicationId = 1L;
        LoanApplication loanApplication = new LoanApplication();
        loanApplication.setStatus(LoanApplicationStatus.APPROVED);
        when(loanApplicationRepository.findById(loanApplicationId)).thenReturn(Optional.of(loanApplication));

        // Act + Assert
        assertThrows(IllegalStateException.class, () -> {
            loanService.approveLoan(loanApplicationId);
        });
    }

    @Test
    void getLoanById_BelongsToUser_Success() {
        // Arrange
        Long loanId = 1L;
        Long userId = 1L;
        LoanApplication loanApplication = new LoanApplication();
        loanApplication.setId(loanId);
        loanApplication.setUser(new User(userId));
        when(loanApplicationRepository.findById(loanId)).thenReturn(Optional.of(loanApplication));

        // Act
        LoanApplication result = loanService.getLoanById(loanId, userId);

        // Assert
        assertNotNull(result);
        assertEquals(loanId, result.getId());
    }

    @Test
    void getLoanById_DoesNotBelongToUser_ThrowsAccessDeniedException() {
        // Arrange
        Long loanId = 1L;
        Long userId = 2L;
        LoanApplication loanApplication = new LoanApplication();
        loanApplication.setUser(new User(1L));
        when(loanApplicationRepository.findById(loanId)).thenReturn(Optional.of(loanApplication));

        // Act + Assert
        assertThrows(AccessDeniedException.class, () -> {
            loanService.getLoanById(loanId, userId);
        });
    }
}

