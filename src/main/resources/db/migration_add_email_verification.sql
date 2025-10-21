-- Migration script to add email verification columns
-- Run this on your existing database to add email verification support

-- Add verification columns to utilisateurs table
ALTER TABLE utilisateurs 
ADD COLUMN verification_code VARCHAR(6) AFTER mot_de_passe,
ADD COLUMN verification_code_expiry DATETIME AFTER verification_code,
ADD COLUMN email_verified BOOLEAN NOT NULL DEFAULT FALSE AFTER verification_code_expiry;

-- Optional: Set existing users as verified (if you don't want to force them to verify)
-- Uncomment the following line if you want existing users to be automatically verified:
-- UPDATE utilisateurs SET email_verified = TRUE;

-- Create index for faster email verification lookups
CREATE INDEX idx_email_verified ON utilisateurs(email_verified);
CREATE INDEX idx_verification_code ON utilisateurs(verification_code);

-- Verify the changes
SELECT 
    COLUMN_NAME, 
    DATA_TYPE, 
    IS_NULLABLE, 
    COLUMN_DEFAULT
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_NAME = 'utilisateurs' 
  AND COLUMN_NAME IN ('verification_code', 'verification_code_expiry', 'email_verified')
ORDER BY ORDINAL_POSITION;
