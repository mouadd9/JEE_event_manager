-- Backfill missing parent rows in utilisateur for JOINED inheritance
-- Ensures that existing rows in participant/organisateur have a matching utilisateur row

BEGIN;

-- Insert missing parent rows for participants
INSERT INTO public.utilisateur (id, nom, email, mot_de_passe_hash, user_type, created_at, updated_at)
SELECT p.utilisateur_id,
       'Legacy Participant ' || p.utilisateur_id,
       'legacy_participant_' || p.utilisateur_id || '@example.local',
       'e7cf3ef4f17c3999a94f2c6f612e8a888e5b1026878e4e19398b23bd38ec221a', -- sha256('password') placeholder
       'PARTICIPANT',
       CURRENT_TIMESTAMP,
       CURRENT_TIMESTAMP
FROM public.participant p
LEFT JOIN public.utilisateur u ON u.id = p.utilisateur_id
WHERE u.id IS NULL;

-- Insert missing parent rows for organisateurs
INSERT INTO public.utilisateur (id, nom, email, mot_de_passe_hash, user_type, created_at, updated_at)
SELECT o.utilisateur_id,
       'Legacy Organisateur ' || o.utilisateur_id,
       'legacy_organisateur_' || o.utilisateur_id || '@example.local',
       'e7cf3ef4f17c3999a94f2c6f612e8a888e5b1026878e4e19398b23bd38ec221a', -- sha256('password') placeholder
       'ORGANISATEUR',
       CURRENT_TIMESTAMP,
       CURRENT_TIMESTAMP
FROM public.organisateur o
LEFT JOIN public.utilisateur u ON u.id = o.utilisateur_id
WHERE u.id IS NULL;

-- Ensure utilisateur_id_seq is at least MAX(id)
DO $$
DECLARE
  max_id BIGINT;
BEGIN
  SELECT COALESCE(MAX(id), 0) INTO max_id FROM public.utilisateur;
  PERFORM setval('public.utilisateur_id_seq', max_id, true);
END $$;

COMMIT;


