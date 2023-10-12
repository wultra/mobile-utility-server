-- Migrate data
-- TABLES

-- For table mobile_app
INSERT INTO mus_mobile_app SELECT * FROM mobile_app;

-- There is no migration for table certificate



-- Drop old tables
DROP TABLE mobile_ssl_pinning CASCADE;
DROP TABLE mobile_app CASCADE;

-- Drop old sequences
DROP SEQUENCE mobile_ssl_pinning_seq CASCADE;
DROP SEQUENCE mobile_app_seq CASCADE;
