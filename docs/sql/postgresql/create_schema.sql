-- Changeset mobile-utility-server/1.5.x/20230227-initial-import.xml::1::Petr Dvorak
-- Create a new sequence for mobile app entities.
CREATE SEQUENCE  IF NOT EXISTS mus_mobile_app_seq START WITH 1 INCREMENT BY 1 CACHE 20;

-- Changeset mobile-utility-server/1.5.x/20230227-initial-import.xml::2::Petr Dvorak
-- Create a new sequence for mobile domain entities.
CREATE SEQUENCE  IF NOT EXISTS mus_mobile_domain_seq START WITH 1 INCREMENT BY 1 CACHE 20;

-- Changeset mobile-utility-server/1.5.x/20230227-initial-import.xml::3::Petr Dvorak
-- Create a new sequence for mobile SSL fingerprints entities.
CREATE SEQUENCE  IF NOT EXISTS mus_mobile_fingerprint_seq START WITH 1 INCREMENT BY 1 CACHE 20;

-- Changeset mobile-utility-server/1.5.x/20230227-initial-import.xml::4::Petr Dvorak
-- Create a new table for mobile app entities.
CREATE TABLE mus_mobile_app (id INTEGER NOT NULL, name VARCHAR(255) NOT NULL, display_name VARCHAR(255) NOT NULL, sign_private_key VARCHAR(255) NOT NULL, sign_public_key VARCHAR(255) NOT NULL, CONSTRAINT mus_mobile_app_pkey PRIMARY KEY (id));

-- Changeset mobile-utility-server/1.5.x/20230227-initial-import.xml::5::Petr Dvorak
-- Create a new table for mobile domain entities.
CREATE TABLE mus_mobile_domain (id INTEGER NOT NULL, app_id INTEGER NOT NULL, domain VARCHAR(255) NOT NULL, CONSTRAINT mus_mobile_domain_pkey PRIMARY KEY (id), CONSTRAINT mus_mobile_domain_app_id_fk FOREIGN KEY (app_id) REFERENCES mus_mobile_app(id) ON DELETE CASCADE);

-- Changeset mobile-utility-server/1.5.x/20230227-initial-import.xml::6::Petr Dvorak
-- Create a new table for mobile SSL fingerprint entities.
CREATE TABLE mus_mobile_fingerprint (id INTEGER NOT NULL, fingerprint VARCHAR(255) NOT NULL, expires INTEGER NOT NULL, mobile_domain_id INTEGER NOT NULL, CONSTRAINT mus_mobile_fingerprint_pkey PRIMARY KEY (id), CONSTRAINT mus_mobile_fingerprint_mobile_domain_id_fk FOREIGN KEY (mobile_domain_id) REFERENCES mus_mobile_domain(id) ON DELETE CASCADE);

-- Changeset mobile-utility-server/1.5.x/20230306-add-user-schema.xml::1::Petr Dvorak
-- Create a new sequence for user entities.
CREATE SEQUENCE  IF NOT EXISTS mus_user_seq START WITH 1 INCREMENT BY 1 CACHE 20;

-- Changeset mobile-utility-server/1.5.x/20230306-add-user-schema.xml::2::Petr Dvorak
-- Create a new sequence for user authority entities.
CREATE SEQUENCE  IF NOT EXISTS mus_user_authority_seq START WITH 1 INCREMENT BY 1 CACHE 20;

-- Changeset mobile-utility-server/1.5.x/20230306-add-user-schema.xml::3::Petr Dvorak
-- Create a new table for user entities.
CREATE TABLE mus_user (id INTEGER NOT NULL, username VARCHAR(255) NOT NULL, password VARCHAR(255) NOT NULL, enabled BOOLEAN NOT NULL, CONSTRAINT mus_user_pkey PRIMARY KEY (id), UNIQUE (username));

-- Changeset mobile-utility-server/1.5.x/20230306-add-user-schema.xml::4::Petr Dvorak
-- Create a new table for user authority entities.
CREATE TABLE mus_user_authority (id INTEGER NOT NULL, user_id INTEGER NOT NULL, authority VARCHAR(255) NOT NULL, CONSTRAINT mus_user_authority_pkey PRIMARY KEY (id), CONSTRAINT mus_user_authority_user_id_fk FOREIGN KEY (user_id) REFERENCES mus_user(id) ON DELETE CASCADE);

-- Changeset mobile-utility-server/1.5.x/20230308-additional-cert-info.xml::1::Petr Dvorak
-- Create a new column to store the certificate in PEM format.
ALTER TABLE mus_mobile_fingerprint ADD pem TEXT NOT NULL;

-- Changeset mobile-utility-server/1.5.x/20230308-additional-cert-info.xml::2::Petr Dvorak
-- Rename the mus_mobile_fingerprint table to mus_certificate.
ALTER TABLE mus_mobile_fingerprint RENAME TO mus_certificate;

-- Changeset mobile-utility-server/1.5.x/20230308-additional-cert-info.xml::3::Petr Dvorak
-- Rename the mus_mobile_fingerprint_seq sequence to mus_certificate_seq.
ALTER SEQUENCE mus_mobile_fingerprint_seq RENAME TO mus_certificate_seq;

-- Changeset mobile-utility-server/1.5.x/20230308-additional-cert-info.xml::4::Jan Dusil
ALTER TABLE mus_certificate DROP CONSTRAINT mus_mobile_fingerprint_mobile_domain_id_fk;

-- Changeset mobile-utility-server/1.5.x/20230609-add-application-version.xml::1::Lubos Racansky
-- Create a new sequence mus_mobile_app_version_seq.
CREATE SEQUENCE  IF NOT EXISTS mus_mobile_app_version_seq START WITH 1 INCREMENT BY 1 CACHE 20;

-- Changeset mobile-utility-server/1.5.x/20230609-add-application-version.xml::2::Lubos Racansky
-- Create a new table mus_mobile_app_version.
CREATE TABLE mus_mobile_app_version (id INTEGER NOT NULL, app_id INTEGER NOT NULL, platform VARCHAR(10) NOT NULL, major_os_version INTEGER, suggested_version VARCHAR(24), required_version VARCHAR(24), message_key VARCHAR(255), CONSTRAINT mus_mobile_app_version_pkey PRIMARY KEY (id), CONSTRAINT mus_mobile_app_version_app_id_fk FOREIGN KEY (app_id) REFERENCES mus_mobile_app(id));

COMMENT ON COLUMN mus_mobile_app_version.platform IS 'ANDROID, IOS';

COMMENT ON COLUMN mus_mobile_app_version.major_os_version IS 'For iOS e.g. 12.4.2 it is 12. For Android, it is API level e.g. 29.';

COMMENT ON COLUMN mus_mobile_app_version.suggested_version IS 'SemVer 2.0';

COMMENT ON COLUMN mus_mobile_app_version.required_version IS 'SemVer 2.0';

COMMENT ON COLUMN mus_mobile_app_version.message_key IS 'Together with language identifies row in mus_localized_text';

-- Changeset mobile-utility-server/1.5.x/20230609-add-application-version.xml::3::Lubos Racansky
-- Create a new table mus_localized_text.
CREATE TABLE mus_localized_text (message_key VARCHAR(255) NOT NULL, language VARCHAR(2) NOT NULL, text TEXT NOT NULL, CONSTRAINT mus_localized_text_pkey PRIMARY KEY (message_key, language));

COMMENT ON COLUMN mus_localized_text.language IS 'ISO 639-1 two-letter language code.';

-- Changeset mobile-utility-server/1.5.x/20230622-add-foreign-keys.xml::1::Lubos Racansky
ALTER TABLE mus_user_authority ADD CONSTRAINT mus_user_authority_user_id_fk FOREIGN KEY (user_id) REFERENCES mus_user (id);

-- Changeset mobile-utility-server/1.5.x/20230622-add-foreign-keys.xml::2::Lubos Racansky
ALTER TABLE mus_mobile_domain ADD CONSTRAINT mus_mobile_domain_app_id_fk FOREIGN KEY (app_id) REFERENCES mus_mobile_app (id);

-- Changeset mobile-utility-server/1.5.x/20230622-add-foreign-keys.xml::3::Lubos Racansky
ALTER TABLE mus_certificate ADD CONSTRAINT mus_certificate_mobile_domain_id_fk FOREIGN KEY (mobile_domain_id) REFERENCES mus_mobile_domain (id);

-- Changeset mobile-utility-server/1.5.x/20230905-add-tag-1.5.0.xml::1::Lubos Racansky
