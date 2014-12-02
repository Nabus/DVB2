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
INSERT INTO dvbts2.server_setup ("server_id", "ident", "setup_key", "setup_value", "date_changed", "user_changed", "note", "created") VALUES ('1', '08a32dc5-affd-dff5-0000-0000719a2e94', 'epgUpdateInterval', '3600000', NULL, NULL, NULL, 'now()');
INSERT INTO dvbts2.server_setup ("server_id", "ident", "setup_key", "setup_value", "date_changed", "user_changed", "note", "created") VALUES ('1', '08a32dc5-affd-dff5-0000-0000719a2e94', 'epgTimestampOffset', '0', NULL, NULL, NULL, 'now()');

/* dvbts2.adapters */
INSERT INTO dvbts2.adapters ("ident", "path", "adapter_type", "frequency", "bandwidth", "transmission_mode", "guard_interval", "hierarchy", "modulation", "date_start", "date_stop", "date_failed", "failed_message", "active", "date_changed", "user_changed", "note", "date_created")
VALUES ('08a32dc5-affd-dff5-0000-0000719a2e94', '/dev/dvb/adapter0', 'DVBT', '794000000', '8', 'auto', 'auto', 'none', 'auto', NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, 'now()');

/* dvbts2.channels */
INSERT INTO dvbts2.channels ("adapter_id", "name", "ident", "pnr", "thumb_save_path", "thumb_save_filename_pattern", "thumb_save_period", "thumb_save_format", "date_start", "date_stop", "date_failed", "failed_message", "active", "date_changed", "user_changed", "note", "date_created")
VALUES ('1', 'Euronews', 'Euronews', '1203', '/home/bullet28/', 'Euronews_yyyy_mm_dd_hh_mm_ss', '15000', 'JPEG', NULL, NULL, NULL, NULL, '1', NULL, NULL, NULL, 'now()');

/* dvbts2.stream_forward */
INSERT INTO dvbts2.stream_forward ("channel_id", "event_type", "output_stream_protocol", "output_stream_host", "output_stream_port", "output_stream_username", "output_stream_password", "output_stream_url_path", "output_stream_timeout", "output_stream_client_limit", "date_streaming_start", "date_streaming_stop", "date_streaming_failed", "streaming_failed_message", "connection_attempts", "active", "date_changed", "user_changed", "note", "date_created")
VALUES ('1', '', 'HTTP', '0.0.0.0', '4848', NULL, NULL, NULL, '30000', '1', now(), now(), NULL, NULL, NULL, '1', NULL, NULL, NULL, 'now()');