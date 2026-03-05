-- Create Password Reset Tokens Table
CREATE TABLE password_reset_tokens (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(100) UNIQUE NOT NULL,
    user_id BIGINT NOT NULL REFERENCES users(id),
    expiry_date TIMESTAMP NOT NULL,
    is_used BOOLEAN DEFAULT FALSE
);

-- Create OTP Table (Optional or part of tokens)
CREATE TABLE otp_table (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(100) NOT NULL,
    otp_code VARCHAR(6) NOT NULL,
    expiry_time TIMESTAMP NOT NULL
);
