insert into mus_mobile_app(id, name)
values (1, 'test-app');

insert into mus_mobile_app_version(id, app_id, platform, suggested_version, required_version, major_os_version, message_key)
values (nextval('mus_mobile_app_version_seq'), 1, 'IOS', '3.3.0', null, null, 'suggested-app.performance'),
       (nextval('mus_mobile_app_version_seq'), 1, 'IOS', '3.1.0', null, 11, null);
