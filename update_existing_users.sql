-- Update existing users with default values for new fields
-- Run this after the schema has been updated by Hibernate

UPDATE utilisateur 
SET 
    is_active = COALESCE(is_active, 1),
    is_suspended = COALESCE(is_suspended, 0),
    is_verified = COALESCE(is_verified, 0)
WHERE is_active IS NULL OR is_suspended IS NULL OR is_verified IS NULL;

-- Set all existing organisateurs as verified (optional)
-- Comment this out if you want to manually verify them
UPDATE utilisateur 
SET is_verified = 1 
WHERE user_type = 'ORGANISATEUR';

-- Ensure all users are active by default
UPDATE utilisateur 
SET is_active = 1 
WHERE is_active = 0 OR is_active IS NULL;
