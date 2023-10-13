-- Migrate data
-- TABLES

-- For table mobile_app
INSERT INTO mus_mobile_app SELECT * FROM mobile_app;

-- There is no migration for table certificate
