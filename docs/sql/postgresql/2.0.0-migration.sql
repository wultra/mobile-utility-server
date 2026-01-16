-- Changeset mobile-utility-server/2.0.x/20260116-ssl-pinning-depth.xml::1::Pavel Sindelar
-- Add depth column to mus_certificate table
ALTER TABLE mus_certificate ADD depth INTEGER DEFAULT 0 NOT NULL;

-- Changeset mobile-utility-server/2.0.x/20260116-ssl-pinning-depth.xml::2::Pavel Sindelar
-- Add ssl_pinning_required column to mus_mobile_domain table
ALTER TABLE mus_mobile_domain ADD ssl_pinning_required BOOLEAN DEFAULT TRUE NOT NULL;
