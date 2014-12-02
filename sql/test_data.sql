/* dvbts2.servers */
INSERT INTO dvbts2.servers VALUES ('example', 'example', 'example', 'example', 'example', 'example', NULL, NULL, NULL, 'now()');

/* dvbts2.server_setup */
INSERT INTO dvbts2.server_setup ("server_id", "ident", "setup_key", "setup_value", "date_changed", "user_changed", "note", "created") VALUES ('1', '08a32dc5-affd-dff5-0000-0000719a2e94', 'commandManagerInterface', 'eth0', NULL, NULL, NULL, 'now()');
INSERT INTO dvbts2.server_setup ("server_id", "ident", "setup_key", "setup_value", "date_changed", "user_changed", "note", "created") VALUES ('1', '08a32dc5-affd-dff5-0000-0000719a2e94', 'commandManagerHost', '127.0.0.1', NULL, NULL, NULL, 'now()');
INSERT INTO dvbts2.server_setup ("server_id", "ident", "setup_key", "setup_value", "date_changed", "user_changed", "note", "created") VALUES ('1', '08a32dc5-affd-dff5-0000-0000719a2e94', 'commandManagerPort', '2222', NULL, NULL, NULL, 'now()');
INSERT INTO dvbts2.server_setup ("server_id", "ident", "setup_key", "setup_value", "date_changed", "user_changed", "note", "created") VALUES ('1', '08a32dc5-affd-dff5-0000-0000719a2e94', 'commandManagerAllowFrom', '127.0.0.1,192.168.1.1', NULL, NULL, NULL, 'now()');
INSERT INTO dvbts2.server_setup ("server_id", "ident", "setup_key", "setup_value", "date_changed", "user_changed", "note", "created") VALUES ('1', '08a32dc5-affd-dff5-0000-0000719a2e94', 'dbManagerHost', '127.0.0.1', NULL, NULL, NULL, 'now()');
INSERT INTO dvbts2.server_setup ("server_id", "ident", "setup_key", "setup_value", "date_changed", "user_changed", "note", "created") VALUES ('1', '08a32dc5-affd-dff5-0000-0000719a2e94', 'dbManagerPort', '5432', NULL, NULL, NULL, 'now()');
INSERT INTO dvbts2.server_setup ("server_id", "ident", "setup_key", "setup_value", "date_changed", "user_changed", "note", "created") VALUES ('1', '08a32dc5-affd-dff5-0000-0000719a2e94', 'dbManagerUsername', 'test', NULL, NULL, NULL, 'now()');
INSERT INTO dvbts2.server_setup ("server_id", "ident", "setup_key", "setup_value", "date_changed", "user_changed", "note", "created") VALUES ('1', '08a32dc5-affd-dff5-0000-0000719a2e94', 'dbManagerPassword', 'test', NULL, NULL, NULL, 'now()');
INSERT INTO dvbts2.server_setup ("server_id", "ident", "setup_key", "setup_value", "date_changed", "user_changed", "note", "created") VALUES ('1', '08a32dc5-affd-dff5-0000-0000719a2e94', 'dbManagerDatabase', 'dvbts2', NULL, NULL, NULL, 'now()');
INSERT INTO dvbts2.server_setup ("server_id", "ident", "setup_key", "setup_value", "date_changed", "user_changed", "note", "created") VALUES ('1', '08a32dc5-affd-dff5-0000-0000719a2e94', 'dbManagerPeriod', '60000', NULL, NULL, NULL, 'now()');
INSERT INTO dvbts2.server_setup ("server_id", "ident", "setup_key", "setup_value", "date_changed", "user_changed", "note", "created") VALUES ('1', '08a32dc5-affd-dff5-0000-0000719a2e94', 'dbManagerTimeout', '60000', NULL, NULL, NULL, 'now()');

/* dvbts2.adapters */
INSERT INTO dvbts2.adapters ("ident", "path", "adapter_type", "frequency", "bandwidth", "transmission_mode", "guard_interval", "hierarchy", "modulation", "date_start", "date_stop", "date_failed", "failed_message", "active", "date_changed", "user_changed", "note", "date_created")
VALUES ('08a32dc5-affd-dff5-0000-0000719a2e94', '/dev/dvb/adapter0', 'DVBT', '794000000', '8', 'auto', 'auto', 'none', 'auto', NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, 'now()');

