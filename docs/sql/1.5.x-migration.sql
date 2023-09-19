-- Migrate data

-- For table mobile_app
INSERT INTO mus_mobile_app SELECT * FROM ssl_mobile_app;

-- For table mobile_domain
INSERT INTO mus_mobile_domain SELECT * FROM ssl_mobile_domain;

-- For table mobile_fingerprint
INSERT INTO mus_mobile_fingerprint SELECT * FROM ssl_mobile_fingerprint;

-- For table user (from 20230306-add-user-schema.xml)
INSERT INTO mus_user SELECT * FROM ssl_user;

-- For table user_authority (from 20230306-add-user-schema.xml)
INSERT INTO mus_user_authority SELECT * FROM ssl_user_authority;


-- Drop old tables
DROP TABLE ssl_mobile_app;
DROP TABLE ssl_mobile_domain;
DROP TABLE ssl_mobile_fingerprint;
DROP TABLE ssl_user;
DROP TABLE ssl_user_authority;

-- Drop old sequences
DROP SEQUENCE ssl_mobile_app_seq;
DROP SEQUENCE ssl_mobile_domain_seq;
DROP SEQUENCE ssl_mobile_fingerprint_seq;
DROP SEQUENCE ssl_user_seq;
DROP SEQUENCE ssl_user_authority_seq;

