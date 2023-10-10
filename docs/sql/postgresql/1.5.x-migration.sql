-- Migrate data
-- TABLES

-- For table mobile_app
INSERT INTO mus_mobile_app SELECT * FROM ssl_mobile_app;

-- For table certificate
INSERT INTO mus_certificate SELECT * FROM ssl_certificate;



-- Drop old tables
DROP TABLE ssl_certificate CASCADE;
DROP TABLE ssl_mobile_app CASCADE;

-- Drop old sequences
DROP SEQUENCE ssl_certificate_seq CASCADE;
DROP SEQUENCE ssl_mobile_app_seq CASCADE;
