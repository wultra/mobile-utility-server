-- Drop old tables
DROP TABLE mobile_ssl_pinning CASCADE CONSTRAINTS;
DROP TABLE mobile_app CASCADE CONSTRAINTS;

-- Drop old sequences
DROP SEQUENCE mobile_ssl_pinning_seq;
DROP SEQUENCE mobile_app_seq;
