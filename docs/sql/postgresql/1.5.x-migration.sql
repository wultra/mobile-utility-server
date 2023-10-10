-- Migrate data
-- TABLES

-- For table mobile_app
INSERT INTO mus_mobile_app SELECT * FROM ssl_mobile_app;

-- For table mobile_domain
INSERT INTO mus_mobile_domain SELECT * FROM ssl_mobile_domain;

-- For table user
INSERT INTO mus_user SELECT * FROM ssl_user;

-- For table user_authority
INSERT INTO mus_user_authority SELECT * FROM ssl_user_authority;

-- For table certificate
INSERT INTO mus_certificate SELECT * FROM ssl_certificate;



-- Drop old tables
DROP TABLE ssl_certificate CASCADE;
DROP TABLE ssl_mobile_app CASCADE;
DROP TABLE ssl_mobile_domain CASCADE;
DROP TABLE ssl_user CASCADE;
DROP TABLE ssl_user_authority CASCADE;

-- Drop old sequences
DROP SEQUENCE ssl_certificate_seq CASCADE;
DROP SEQUENCE ssl_mobile_app_seq CASCADE;
DROP SEQUENCE ssl_mobile_domain_seq CASCADE;
DROP SEQUENCE ssl_user_authority_seq CASCADE;
DROP SEQUENCE ssl_user_seq CASCADE;
