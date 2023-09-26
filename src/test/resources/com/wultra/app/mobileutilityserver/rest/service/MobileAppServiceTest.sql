insert into mus_mobile_app(id, name)
values (1, 'suggested-app'),
       (2, 'required-app'),
       (3, 'suggested-and-required-app'),
       (4, 'duplicate-entries');

insert into mus_mobile_app_version(id, app_id, platform, suggested_version, required_version, major_os_version, message_key)
values (nextval('mus_mobile_app_version_seq'), 1, 'IOS', '3.3.0', null, null, 'suggested-app.performance'),
       (nextval('mus_mobile_app_version_seq'), 1, 'ANDROID', '3.3.0', null, null, 'suggested-app.performance'),
       (nextval('mus_mobile_app_version_seq'), 1, 'IOS', '3.1.0', null, 11, null),
       (nextval('mus_mobile_app_version_seq'), 1, 'ANDROID', '3.1.0', null, 29, null),
       (nextval('mus_mobile_app_version_seq'), 2, 'IOS', null, '3.3.0', null, 'required-app.internet-banking'),
       (nextval('mus_mobile_app_version_seq'), 2, 'ANDROID', null, '3.3.0', null, null),
       (nextval('mus_mobile_app_version_seq'), 2, 'IOS', null, '3.2.0', 11, null),
       (nextval('mus_mobile_app_version_seq'), 2, 'ANDROID', null, '2.9.0', 29, null),
       (nextval('mus_mobile_app_version_seq'), 3, 'IOS', '3.0.0', '2.0.0', null, null),
       (nextval('mus_mobile_app_version_seq'), 4, 'IOS', '1.1.0', null, null, null),
       (nextval('mus_mobile_app_version_seq'), 4, 'IOS', null, '3.1.2', null, null);

insert into mus_localized_text(message_key, text, language)
values ('required-app.internet-banking', 'Upgrade is required to make internet banking working.', 'en'),
       ('suggested-app.performance', 'Doporučujeme vám aktualizovat aplikaci kvůli vylepšenému výkonu.', 'cs');
