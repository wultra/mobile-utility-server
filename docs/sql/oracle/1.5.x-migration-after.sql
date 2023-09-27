-- Migrate data
-- TABLES

-- For table mobile_app
INSERT INTO mus_mobile_app SELECT * FROM ssl_mobile_app;

-- For table mobile_app_version
INSERT INTO mus_mobile_app_version SELECT * FROM ssl_mobile_app_version;

-- For table mobile_domain
INSERT INTO mus_mobile_domain SELECT * FROM ssl_mobile_domain;

-- For table user
INSERT INTO mus_user SELECT * FROM ssl_user;

-- For table user_authority
INSERT INTO mus_user_authority SELECT * FROM ssl_user_authority;

-- For table certificate
INSERT INTO mus_certificate SELECT * FROM ssl_certificate;

-- For table localized_text
INSERT INTO mus_localized_text SELECT * FROM ssl_localized_text;



-- Drop old tables
DROP TABLE ssl_certificate CASCADE CONSTRAINTS;
DROP TABLE ssl_localized_text CASCADE CONSTRAINTS;
DROP TABLE ssl_mobile_app CASCADE CONSTRAINTS;
DROP TABLE ssl_mobile_app_version CASCADE CONSTRAINTS;
DROP TABLE ssl_mobile_domain CASCADE CONSTRAINTS;
DROP TABLE ssl_user CASCADE CONSTRAINTS;
DROP TABLE ssl_user_authority CASCADE CONSTRAINTS;

-- Drop old sequences
DROP SEQUENCE ssl_certificate_seq;
DROP SEQUENCE ssl_mobile_app_seq;
DROP SEQUENCE ssl_mobile_app_version_seq;
DROP SEQUENCE ssl_mobile_domain_seq;
DROP SEQUENCE ssl_user_authority_seq;
DROP SEQUENCE ssl_user_seq;

