-- Changeset mobile-utility-server/2.1.x/20260325-change-certificate-expires-to-bigint.xml::1::Pavel Sindelar
-- Change expires column type from integer to bigint to support certificates expiring after 2038-01-19.
ALTER TABLE mus_certificate MODIFY expires NUMBER(38, 0);
