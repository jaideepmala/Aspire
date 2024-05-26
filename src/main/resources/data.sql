CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL
);


CREATE TABLE IF NOT EXISTS LoanApplication (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    amountRequired DECIMAL(10,2) NOT NULL,
    loanTerm INT NOT NULL,
    applicationDate DATE NOT NULL,
    status VARCHAR(255) NOT NULL,
    user_id BIGINT, -- Foreign key column referencing User table
    FOREIGN KEY (user_id) REFERENCES users(id) -- Foreign key constraint
);


CREATE TABLE IF NOT EXISTS Repayment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    repaymentAmount DECIMAL(10,2) NOT NULL,
    repaymentDate DATE NOT NULL,
    status VARCHAR(255) NOT NULL,
    loan_application_id BIGINT,
    FOREIGN KEY (loan_application_id) REFERENCES LoanApplication(id)
);

