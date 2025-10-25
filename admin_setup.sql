-- Insert Admin User
-- Password: Admin@123
-- SHA-256 hash of 'Admin@123'

INSERT INTO utilisateur (id, nom, email, mot_de_passe_hash, user_type, is_active, is_suspended, is_verified, created_at, updated_at)
VALUES (100, 'Administrateur', 'admin@eventmanager.com', 'f12ca8f9765c33f90c5c6cd0f21204713440bfeb837c55ff3dbb19e9a1e6600c', 'ADMIN', 1, 0, 1, NOW(), NOW());

INSERT INTO admin (utilisateur_id, role, permissions)
VALUES (100, 'SUPER_ADMIN', 'all');

-- Optional: Update existing organisateurs to set verified status
UPDATE utilisateur SET is_verified = 0 WHERE user_type = 'ORGANISATEUR';

-- Optional: Set some participants as active
UPDATE utilisateur SET is_active = 1, is_suspended = 0 WHERE user_type = 'PARTICIPANT';
