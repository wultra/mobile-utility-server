insert into ssl_mobile_app_version(id, application_name, platform, suggested_version, required_version, major_os_version, message_key)
values (nextval('ssl_mobile_app_version_seq'), 'suggested-app', 'IOS', '3.3.0', null, null, 'suggested-app.performance'),
       (nextval('ssl_mobile_app_version_seq'), 'suggested-app', 'ANDROID', '3.3.0', null, null, 'suggested-app.performance'),
       (nextval('ssl_mobile_app_version_seq'), 'suggested-app', 'IOS', '3.1.0', null, 11, null),
       (nextval('ssl_mobile_app_version_seq'), 'suggested-app', 'ANDROID', '3.1.0', null, 29, null),
       (nextval('ssl_mobile_app_version_seq'), 'required-app', 'IOS', null, '3.3.0', null, 'required-app.internet-banking'),
       (nextval('ssl_mobile_app_version_seq'), 'required-app', 'ANDROID', null, '3.3.0', null, null),
       (nextval('ssl_mobile_app_version_seq'), 'required-app', 'IOS', null, '3.2.0', 11, null),
       (nextval('ssl_mobile_app_version_seq'), 'required-app', 'ANDROID', null, '2.9.0', 29, null),
       (nextval('ssl_mobile_app_version_seq'), 'suggested-and-required-app', 'IOS', '3.0.0', '2.0.0', null, null);

insert into ssl_localized_text(message_key, text, language)
values ('required-app.internet-banking', 'Upgrade is required to make internet banking working.', 'en'),
       ('suggested-app.performance', 'Doporučujeme vám aktualizovat aplikaci kvůli vylepšenému výkonu.', 'cs');
