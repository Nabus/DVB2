CREATE SCHEMA adm;

CREATE TABLE adm.user_role(
	name VARCHAR(16) NOT NULL, -- Role name
	name_hum VARCHAR(255) NOT NULL, -- Role name human readable to show
	grantby VARCHAR(255), -- Who can grant this role (e.g. user can be granted by admin)
	CONSTRAINT pk_user_role PRIMARY KEY ( name )
);

INSERT INTO adm.user_role( name, name_hum, grantby ) VALUES( 'ROOT', 'Root', null );
INSERT INTO adm.user_role( name, name_hum, grantby ) VALUES( 'ADMIN', 'Administrator', 'ROOT' );
INSERT INTO adm.user_role( name, name_hum, grantby ) VALUES( 'SUPERUSER', 'Superior user', 'ADMIN,ROOT' );
INSERT INTO adm.user_role( name, name_hum, grantby ) VALUES( 'USER', 'Common user', 'ADMIN,ROOT' );

CREATE TABLE adm.partner (
	id BIGSERIAL,

	role_name VARCHAR(16) NOT NULL DEFAULT 'USER',

	username VARCHAR(128) NOT NULL, -- Username of user - can be email, common username or phone numbe in case of registration through SMS
	password VARCHAR(32) NOT NULL, -- MD5 hash 32 chars of password

	credit INTEGER DEFAULT 0, -- Partner's credit - cached from partner_account

	company VARCHAR(255),
	company_id VARCHAR(255),
	company_tax_id VARCHAR(255),
	degre_before VARCHAR(255),
	first_name VARCHAR(255) NOT NULL,
	middle_name VARCHAR(255),
	last_name VARCHAR(255) NOT NULL,
	degre_after VARCHAR(255),

	personal_id VARCHAR(255),
	date_birthday DATE, -- if it is person (not company) date of its birthday for identification

	phone VARCHAR(255),
	phone1 VARCHAR(255),
	phone2 VARCHAR(255),
	email VARCHAR(255) NOT NULL,
	email1 VARCHAR(255),
	email2 VARCHAR(255),

	address VARCHAR(255),
	street VARCHAR(255),
	street_nr CHARACTER VARYING(16),
	city CHARACTER VARYING(128),
	postal CHARACTER VARYING(16),

	-- invoicing address
	iv_address VARCHAR(255),
	iv_street VARCHAR(255),
	iv_street_nr CHARACTER VARYING(16),
	iv_city CHARACTER VARYING(128),
	iv_postal CHARACTER VARYING(16),

	activationkey VARCHAR(32), -- MD5 hash generated to email for confirming registration
	actived TIMESTAMP NOT NULL DEFAULT NOW(), -- Automatically set to now, but when admin wants to ban user, null is set and account is deactivated
	confirmed TIMESTAMP, -- When user confirm registration to email, this column is set to now and user can use his account

	date_changed TIMESTAMP, -- When somebody change anything, timestamp is set
	user_changed VARCHAR(128), -- Same as in previous case, any change is registered - who done that change

	note TEXT, -- Some useful information to Admin eyes only about partner
	date_created TIMESTAMP NOT NULL DEFAULT NOW(),

	CONSTRAINT pk_partner PRIMARY KEY ( id ),
	CONSTRAINT fk_partner_role_name FOREIGN KEY (role_name) REFERENCES adm.user_role (name)
);

CREATE UNIQUE INDEX uix_partner_username ON adm.partner ( LOWER( username ) );

-- group of partners
CREATE TABLE adm.partner_group (
	id BIGSERIAL,

	partner_id BIGINT NOT NULL, -- reference to partner table - to what partner this row belongs

	group_name VARCHAR(255) NOT NULL, -- Group name

	active INTEGER NOT NULL DEFAULT 1, -- 1 = true = row is active, 0 = false, determine whenever record is active and available, false = deleted and not active

	date_changed TIMESTAMP, -- When somebody change any detail
	user_changed VARCHAR(128), -- Same as in previous case, any change is registered - who done that change

	note TEXT, -- Some useful information to Admin eyes only
	date_created TIMESTAMP NOT NULL DEFAULT NOW(),

	CONSTRAINT pk_partner_group PRIMARY KEY ( id ),
	CONSTRAINT fk_partner_group_partner_id FOREIGN KEY (partner_id) REFERENCES adm.partner (id)
);

CREATE UNIQUE INDEX uix_partner_group ON adm.partner_group ( partner_id, LOWER( group_name ) );

-- what partner belongs to which group
CREATE TABLE adm.partner_group_partner_relation (
	id BIGSERIAL,

	partner_group_id BIGINT NOT NULL, -- reference to partner_group
	partner_id BIGINT NOT NULL, -- reference to partner

	active INTEGER NOT NULL DEFAULT 1, -- 1 = true = row is active, 0 = false, determine whenever record is active and available, false = deleted and not active

	date_changed TIMESTAMP, -- When somebody change any detail
	user_changed VARCHAR(128), -- Same as in previous case, any change is registered - who done that change

	note TEXT, -- Some useful information to Admin eyes only
	date_created TIMESTAMP NOT NULL DEFAULT NOW(),

	CONSTRAINT pk_partner_group_partner_relation PRIMARY KEY ( id ),
	CONSTRAINT fk_partner_group_partner_relation_partner_group_id FOREIGN KEY (partner_group_id) REFERENCES adm.partner_group (id),
	CONSTRAINT fk_partner_group_partner_relation_partner_id FOREIGN KEY (partner_id) REFERENCES adm.partner (id)
);

CREATE UNIQUE INDEX uix_partner_group_partner_relation ON adm.partner_group_partner_relation ( partner_group_id, partner_id );

CREATE TABLE adm.server_type (
	name VARCHAR(255) NOT NULL, -- Server type name
	name_hum VARCHAR(255) NOT NULL, -- Server type name human readable to show
	CONSTRAINT pk_server_type PRIMARY KEY ( name )
);

INSERT INTO adm.server_type( name, name_hum ) VALUES( 'DVBTS2', 'DvbTS2' );

/*
Used to store information about BOX where PadmeDVBTS2 running
*/
CREATE TABLE adm.servers (
	id BIGSERIAL,

	server_type VARCHAR(255) NOT NULL, -- type of server
	partner_id BIGINT NOT NULL, -- reference to what partner this server belongs

	name VARCHAR(255) NOT NULL, -- Name of server - for admins
	location VARCHAR(255) NOT NULL, -- Comment for admins

	ident VARCHAR(255) NOT NULL, -- assigned to some configuration Server.properties

	hostname VARCHAR(255) NOT NULL, -- Server hostname
	ip VARCHAR(255) NOT NULL, -- IP address of server
	path VARCHAR(255) NOT NULL, -- Path where are shares accessible
	url VARCHAR(255) NOT NULL, -- URL

	description TEXT, -- Some useful information

	active INTEGER NOT NULL DEFAULT 1, -- 1 = true = row is active, 0 = false, determine whenever record is active and available, false = deleted and not active

	date_changed TIMESTAMP, -- When somebody change anything, timestamp is set
	user_changed VARCHAR(128), -- Same as in previous case, any change is registered - who done that change

	note TEXT, -- Some useful information to Admin eyes only
	date_created TIMESTAMP NOT NULL DEFAULT NOW(),

	CONSTRAINT pk_servers PRIMARY KEY ( id ),
	CONSTRAINT fk_servers_server_type FOREIGN KEY (server_type) REFERENCES adm.server_type (name),
	CONSTRAINT fk_servers_partner_id FOREIGN KEY (partner_id) REFERENCES adm.partner (id)
);

/*
Used to store information about settings for PadmeDVBTS2 instance
*/
CREATE TABLE adm.server_setup (
	id BIGSERIAL,

	server_id BIGINT NOT NULL, -- reference to server table - what server this setup belongs

	setup_key VARCHAR(255) NOT NULL,
	setup_value VARCHAR(255) NOT NULL,

	active INTEGER NOT NULL DEFAULT 1, -- 1 = true = row is active, 0 = false, determine whenever record is active and available, false = deleted and not active

	date_changed TIMESTAMP, -- When somebody change any detail
	user_changed VARCHAR(128), -- Same as in previous case, any change is registered - who done that change

	note TEXT, -- Some useful information to Admin eyes only
	date_created TIMESTAMP NOT NULL DEFAULT NOW(),

	CONSTRAINT pk_server_setup PRIMARY KEY ( id ),
	CONSTRAINT fk_server_setup_server_id FOREIGN KEY (server_id) REFERENCES adm.servers (id)
);

CREATE UNIQUE INDEX uix_server_setup ON adm.server_setup ( server_id, LOWER( setup_key ) );

/*
Used to store information about clusters and into what clusters which particular server belongs
*/
CREATE TABLE adm.clusters (
	id BIGSERIAL,

	partner_id BIGINT NOT NULL, -- reference to what partner this cluster belongs

	name VARCHAR(255) NOT NULL, -- Name of cluster - for admins
	description VARCHAR(255) NOT NULL, -- Comment for admins
	ident VARCHAR(255) NOT NULL, -- identification of cluster

	date_changed TIMESTAMP, -- When somebody change anything, timestamp is set
	user_changed VARCHAR(128), -- Same as in previous case, any change is registered - who done that change

	active INTEGER NOT NULL DEFAULT 1, -- 1 = true = row is active, 0 = false, determine whenever record is active and available, false = deleted and not active

	note TEXT, -- Some useful information to Admin eyes only
	date_created TIMESTAMP NOT NULL DEFAULT NOW(),

	CONSTRAINT pk_clusters PRIMARY KEY ( id ),
	CONSTRAINT fk_clusters_partner_id FOREIGN KEY (partner_id) REFERENCES adm.partner (id)
);

/*
Settings for cluster, for example JMS_TYPE (ACTIVEMQ), JMS_JNDI (connection string), JMS_QUEUE (DVBTS2_ACTIVITY_MAIN), and so on.
*/
CREATE TABLE adm.cluster_setup (
	id BIGSERIAL,

	cluster_id BIGINT NOT NULL, -- reference to clusters table

	setup_key VARCHAR(255) NOT NULL,
	setup_value VARCHAR(255) NOT NULL,

	active INTEGER NOT NULL DEFAULT 1, -- 1 = true = row is active, 0 = false, determine whenever record is active and available, false = deleted and not active

	date_changed TIMESTAMP, -- When somebody change any detail
	user_changed VARCHAR(128), -- Same as in previous case, any change is registered - who done that change

	note TEXT, -- Some useful information to Admin eyes only
	date_created TIMESTAMP NOT NULL DEFAULT NOW(),

	CONSTRAINT pk_cluster_setup PRIMARY KEY ( id ),
	CONSTRAINT fk_cluster_setup_cluster_id FOREIGN KEY (cluster_id) REFERENCES adm.clusters (id)
);

CREATE UNIQUE INDEX uix_server_setup ON adm.cluster_setup ( cluster_id, LOWER( setup_key ) );

/*
Relations between servers a clusters
*/
CREATE TABLE adm.cluster_server_relation (
	id BIGSERIAL,

	cluster_id BIGINT NOT NULL, -- reference to clusters table
	server_id BIGINT NOT NULL, -- reference to server table

	active INTEGER NOT NULL DEFAULT 1, -- 1 = true = row is active, 0 = false, determine whenever record is active and available, false = deleted and not active

	-- fields for administration
	date_changed TIMESTAMP, -- When somebody change anything, timestamp is set
	user_changed VARCHAR(128), -- Same as in previous case, any change is registered - who done that change

	note TEXT, -- Some useful information to Admin eyes only
	date_created TIMESTAMP NOT NULL DEFAULT NOW(), -- whenever this record was created

	CONSTRAINT pk_cluster_server_relation PRIMARY KEY ( id ),
	CONSTRAINT fk_cluster_server_relation_server_id FOREIGN KEY (server_id) REFERENCES adm.servers (id),
	CONSTRAINT fk_cluster_server_relation_cluster_id FOREIGN KEY (cluster_id) REFERENCES adm.clusters (id)
);

CREATE UNIQUE INDEX uix_cluster_server_relation ON adm.cluster_server_relation ( cluster_id, server_id );

CREATE SCHEMA dvbts2;

/*
Used to store information about channels to process, information for making thumbnails and so on
*/
CREATE TABLE dvbts2.channels (
	id BIGSERIAL,

	partner_id BIGINT NOT NULL, -- reference to what partner this channel belongs

	name VARCHAR(255) NOT NULL, -- channel name e.g. CT 1
	ident VARCHAR(255) NOT NULL, -- channel identification e.g. CT1

	dvb_channel_id VARCHAR(255) NOT NULL, -- channel id from dvb e.g. I10436.labs.zap2it.com
	display_name TEXT, -- display names of channel separated by | e.g. 13 KERA|13 KERA TX42822|
	icon TEXT,

	-- statistic information
	date_available TIMESTAMP, -- timestamp of first appearance of channel in multicast
	date_start TIMESTAMP, -- timestamp of last attempt to use
	date_stop TIMESTAMP, -- timestamp when there was last stop of use before date_start, because if it is running, there is certainly no stop in present
	date_failed TIMESTAMP, -- timestamp when some error occur
	failed_message VARCHAR(255), -- last error message when some error occur

	active INTEGER NOT NULL DEFAULT 1, -- 1 = true, 0 = false, determine whenever this channel is active and it has to be processed, false = deleted and not active

	-- new added field for administration
	date_changed TIMESTAMP, -- When somebody change anything, timestamp is set
	user_changed VARCHAR(128), -- Same as in previous case, any change is registered - who done that change

	note TEXT, -- Some useful information to Admin eyes only
	date_created TIMESTAMP NOT NULL DEFAULT NOW(), -- whenever this record was created

	CONSTRAINT pk_channels PRIMARY KEY ( id ),
	CONSTRAINT fk_channels_partner_id FOREIGN KEY (partner_id) REFERENCES adm.partner (id)
);

/*
Used to store information about channel packages
*/
CREATE TABLE dvbts2.channel_packages (
	id BIGSERIAL,

	partner_id BIGINT NOT NULL, -- reference to what partner this channel package belongs

	name VARCHAR(255) NOT NULL, -- channel package name e.g. Standard DVB channels
	ident VARCHAR(255) NOT NULL, -- channel package identification e.g. STANDARD

	icon TEXT,

	active INTEGER NOT NULL DEFAULT 1, -- 1 = true, 0 = false, determine whenever this channel is active and it has to be processed, false = deleted and not active

	-- new added field for administration
	date_changed TIMESTAMP, -- When somebody change anything, timestamp is set
	user_changed VARCHAR(128), -- Same as in previous case, any change is registered - who done that change

	note TEXT, -- Some useful information to Admin eyes only
	date_created TIMESTAMP NOT NULL DEFAULT NOW(), -- whenever this record was created

	CONSTRAINT pk_channel_packages PRIMARY KEY ( id ),
	CONSTRAINT fk_channel_packages_partner_id FOREIGN KEY (partner_id) REFERENCES adm.partner (id)
);

/*
Relations between channels and channel packages
*/
CREATE TABLE dvbts2.channel_channel_packages_relation (
	id BIGSERIAL,

	channel_id BIGINT NOT NULL, -- reference to channel table
	channel_package_id BIGINT NOT NULL, -- reference to channel package table

	active INTEGER NOT NULL DEFAULT 1, -- 1 = true = row is active, 0 = false, determine whenever record is active and available, false = deleted and not active

	-- fields for administration
	date_changed TIMESTAMP, -- When somebody change anything, timestamp is set
	user_changed VARCHAR(128), -- Same as in previous case, any change is registered - who done that change

	note TEXT, -- Some useful information to Admin eyes only
	date_created TIMESTAMP NOT NULL DEFAULT NOW(), -- whenever this record was created

	CONSTRAINT pk_channel_channel_packages_relation PRIMARY KEY ( id ),
	CONSTRAINT fk_channel_channel_packages_relation_channel_id FOREIGN KEY (channel_id) REFERENCES dvbts2.channels (id),
	CONSTRAINT fk_channel_channel_packages_relation_channel_package_id FOREIGN KEY (channel_package_id) REFERENCES dvbts2.channel_packages (id)
);

CREATE UNIQUE INDEX uix_channel_channel_packages_relation ON dvbts2.channel_channel_packages_relation ( active, channel_id, channel_package_id );

/*
Relations between partners and channels
*/
CREATE TABLE dvbts2.partner_channel_relation (
	id BIGSERIAL,

	partner_id BIGINT NOT NULL, -- reference to what partner this channel package belongs
	channel_id BIGINT NOT NULL, -- reference to channel table

	active INTEGER NOT NULL DEFAULT 1, -- 1 = true = row is active, 0 = false, determine whenever record is active and available, false = deleted and not active

	-- fields for administration
	date_changed TIMESTAMP, -- When somebody change anything, timestamp is set
	user_changed VARCHAR(128), -- Same as in previous case, any change is registered - who done that change

	note TEXT, -- Some useful information to Admin eyes only
	date_created TIMESTAMP NOT NULL DEFAULT NOW(), -- whenever this record was created

	CONSTRAINT pk_partner_channel_relation PRIMARY KEY ( id ),
	CONSTRAINT fk_partner_channel_relation_partner_id FOREIGN KEY (partner_id) REFERENCES adm.partner (id),
	CONSTRAINT fk_partner_channel_relation_channel_id FOREIGN KEY (channel_id) REFERENCES dvbts2.channels (id)
);

CREATE UNIQUE INDEX uix_partner_channel_relation ON dvbts2.partner_channel_relation ( active, partner_id, channel_id );

/*
Relations between partners and packages channels
*/
CREATE TABLE dvbts2.partner_channel_packages_relation (
	id BIGSERIAL,

	partner_id BIGINT NOT NULL, -- reference to what partner this channel package belongs
	channel_package_id BIGINT NOT NULL, -- reference to channel package table

	active INTEGER NOT NULL DEFAULT 1, -- 1 = true = row is active, 0 = false, determine whenever record is active and available, false = deleted and not active

	-- fields for administration
	date_changed TIMESTAMP, -- When somebody change anything, timestamp is set
	user_changed VARCHAR(128), -- Same as in previous case, any change is registered - who done that change

	note TEXT, -- Some useful information to Admin eyes only
	date_created TIMESTAMP NOT NULL DEFAULT NOW(), -- whenever this record was created

	CONSTRAINT pk_partner_channel_packages_relation PRIMARY KEY ( id ),
	CONSTRAINT fk_partner_channel_packages_relation_partner_id FOREIGN KEY (partner_id) REFERENCES adm.partner (id),
	CONSTRAINT fk_partner_channel_packages_relation_channel_package_id FOREIGN KEY (channel_package_id) REFERENCES dvbts2.channel_packages (id)
);

CREATE UNIQUE INDEX uix_partner_channel_packages_relation ON dvbts2.partner_channel_packages_relation ( active, partner_id, channel_package_id );

/*
Relations between partner_groups and channels
*/
CREATE TABLE dvbts2.partner_group_channel_relation (
	id BIGSERIAL,

	partner_group_id BIGINT NOT NULL, -- reference to what partner_group this channel package belongs
	channel_id BIGINT NOT NULL, -- reference to channel table

	active INTEGER NOT NULL DEFAULT 1, -- 1 = true = row is active, 0 = false, determine whenever record is active and available, false = deleted and not active

	-- fields for administration
	date_changed TIMESTAMP, -- When somebody change anything, timestamp is set
	user_changed VARCHAR(128), -- Same as in previous case, any change is registered - who done that change

	note TEXT, -- Some useful information to Admin eyes only
	date_created TIMESTAMP NOT NULL DEFAULT NOW(), -- whenever this record was created

	CONSTRAINT pk_partner_group_channel_relation PRIMARY KEY ( id ),
	CONSTRAINT fk_partner_group_channel_relation_partner_group_id FOREIGN KEY (partner_group_id) REFERENCES adm.partner_group (id),
	CONSTRAINT fk_partner_group_channel_relation_channel_id FOREIGN KEY (channel_id) REFERENCES dvbts2.channels (id)
);

CREATE UNIQUE INDEX uix_partner_group_channel_relation ON dvbts2.partner_group_channel_relation ( active, partner_group_id, channel_id );

/*
Relations between partner_groups and packages channels
*/
CREATE TABLE dvbts2.partner_group_channel_packages_relation (
	id BIGSERIAL,

	partner_group_id BIGINT NOT NULL, -- reference to what partner_group this channel package belongs
	channel_package_id BIGINT NOT NULL, -- reference to channel package table

	active INTEGER NOT NULL DEFAULT 1, -- 1 = true = row is active, 0 = false, determine whenever record is active and available, false = deleted and not active

	-- fields for administration
	date_changed TIMESTAMP, -- When somebody change anything, timestamp is set
	user_changed VARCHAR(128), -- Same as in previous case, any change is registered - who done that change

	note TEXT, -- Some useful information to Admin eyes only
	date_created TIMESTAMP NOT NULL DEFAULT NOW(), -- whenever this record was created

	CONSTRAINT pk_partner_group_channel_packages_relation PRIMARY KEY ( id ),
	CONSTRAINT fk_partner_group_channel_packages_relation_partner_group_id FOREIGN KEY (partner_group_id) REFERENCES adm.partner_group (id),
	CONSTRAINT fk_partner_group_channel_packages_relation_channel_package_id FOREIGN KEY (channel_package_id) REFERENCES dvbts2.channel_packages (id)
);

CREATE UNIQUE INDEX uix_partner_group_channel_packages_relation ON dvbts2.partner_group_channel_packages_relation ( active, partner_group_id, channel_package_id );

CREATE TABLE dvbts2.adapter_type (
	name VARCHAR(255) NOT NULL, -- Adapter type name
	name_hum VARCHAR(255) NOT NULL, -- Adapter type name human readable to show
	CONSTRAINT pk_adapter_type PRIMARY KEY ( name )
);

INSERT INTO dvbts2.adapter_type( name, name_hum ) VALUES( 'DVBT', 'DVB-T' );
INSERT INTO dvbts2.adapter_type( name, name_hum ) VALUES( 'DVBT2', 'DVB-T2' );
INSERT INTO dvbts2.adapter_type( name, name_hum ) VALUES( 'DVBS', 'DVB-S' );
INSERT INTO dvbts2.adapter_type( name, name_hum ) VALUES( 'DVBS2', 'DVB-S2' );

/*
Used to store information about DVB T/S adapters to work with
*/
CREATE TABLE dvbts2.adapters (
	id BIGSERIAL,

	partner_id BIGINT NOT NULL, -- reference to what partner this adapter belongs

	server_id BIGINT NOT NULL, -- reference to server table - to what PadmeDVBTS2 instance this adapter belongs

	adapter_type VARCHAR(255) NOT NULL, -- adapter type to use - DVB-T / DVB-S / etc.
	path VARCHAR(255) NOT NULL, -- path to adapter device in system, e.g. /dev/dvb/adapter0

	frequency INTEGER NOT NULL, -- frequency to tune, e.g. 634000000
	bandwidth INTEGER NOT NULL DEFAULT 8, -- bandwidth to use, e.g. 8 MHz
	transmission_mode VARCHAR(255) NOT NULL DEFAULT 'auto', -- reserved, default auto
	guard_interval VARCHAR(255) NOT NULL DEFAULT 'auto', -- reserved, default auto
	hierarchy VARCHAR(255) NOT NULL DEFAULT 'none', -- reserved, default none
	modulation VARCHAR(255) NOT NULL DEFAULT 'auto', -- reserved, default auto

	-- statistic information
	date_start TIMESTAMP, -- timestamp of last attempt to use
	date_stop TIMESTAMP, -- timestamp when there was last stop of use before date_start, because if it is running, there is certainly no stop in present
	date_failed TIMESTAMP, -- timestamp when some error occur
	failed_message VARCHAR(255), -- last error message when some error occur

	active INTEGER NOT NULL DEFAULT 1, -- 1 = true, 0 = false, determine whenever this adapter is active and it has to be used, false = deleted and not active

	-- new added field for administration
	date_changed TIMESTAMP, -- When somebody change anything, timestamp is set
	user_changed VARCHAR(128), -- Same as in previous case, any change is registered - who done that change

	note TEXT, -- Some useful information to Admin eyes only
	date_created TIMESTAMP NOT NULL DEFAULT NOW(), -- whenever this record was created

	CONSTRAINT pk_adapters PRIMARY KEY ( id ),
	CONSTRAINT fk_adapters_adapter_type FOREIGN KEY (adapter_type) REFERENCES dvbts2.adapter_type (name),
	CONSTRAINT fk_adapters_server_id FOREIGN KEY (server_id) REFERENCES adm.servers (id),
	CONSTRAINT fk_adapters_partner_id FOREIGN KEY (partner_id) REFERENCES adm.partner (id)
);

/*
Used to store information about channels to process, information for making thumbnails and so on
*/
CREATE TABLE dvbts2.adapter_channels (
	id BIGSERIAL,

	channel_id BIGINT NOT NULL, -- identification to what channel this adapter channel belongs
	adapter_id BIGINT NOT NULL, -- identification to what adapter this channel belongs
	pnr INTEGER NOT NULL, -- pnr id of stream

	thumb_save_path VARCHAR(255), -- path to save thumbnails from source stream (/opt/thumbnails/CT1/)
	thumb_save_filename_pattern VARCHAR(255), -- pattern for creating file name by convention ('CT1'_yyyy_mm_dd_hh_mm_ss'.jpg')
	thumb_save_period INTEGER NOT NULL DEFAULT 15000, -- regular time period in miliseconds to create thumbnail from source stream
	thumb_save_format VARCHAR(255) NOT NULL DEFAULT 'JPEG', -- format of thumbnail = JPEG or PNG

	-- statistic information
	date_available TIMESTAMP, -- timestamp of first appearance of channel in multicast
	date_start TIMESTAMP, -- timestamp of last attempt to use
	date_stop TIMESTAMP, -- timestamp when there was last stop of use before date_start, because if it is running, there is certainly no stop in present
	date_failed TIMESTAMP, -- timestamp when some error occur
	failed_message VARCHAR(255), -- last error message when some error occur

	active INTEGER NOT NULL DEFAULT 1, -- 1 = true, 0 = false, determine whenever this channel is active and it has to be processed, false = deleted and not active

	-- new added field for administration
	date_changed TIMESTAMP, -- When somebody change anything, timestamp is set
	user_changed VARCHAR(128), -- Same as in previous case, any change is registered - who done that change

	note TEXT, -- Some useful information to Admin eyes only
	date_created TIMESTAMP NOT NULL DEFAULT NOW(), -- whenever this record was created

	CONSTRAINT pk_adapter_channels PRIMARY KEY ( id ),
	CONSTRAINT fk_adapter_channels_channel_id FOREIGN KEY (channel_id) REFERENCES dvbts2.channels (id),
	CONSTRAINT fk_adapter_channels_adapter_id FOREIGN KEY (adapter_id) REFERENCES dvbts2.adapters (id)
);

/*
Relations between adapter channels a clusters
*/
CREATE TABLE dvbts2.cluster_adapter_channel_relation (
	id BIGSERIAL,

	cluster_id BIGINT NOT NULL, -- reference to clusters table
	adapter_channel_id BIGINT NOT NULL, -- reference to adapter channel table

	active INTEGER NOT NULL DEFAULT 1, -- 1 = true = row is active, 0 = false, determine whenever record is active and available, false = deleted and not active

	-- fields for administration
	date_changed TIMESTAMP, -- When somebody change anything, timestamp is set
	user_changed VARCHAR(128), -- Same as in previous case, any change is registered - who done that change

	note TEXT, -- Some useful information to Admin eyes only
	date_created TIMESTAMP NOT NULL DEFAULT NOW(), -- whenever this record was created

	CONSTRAINT pk_cluster_channel_relation PRIMARY KEY ( id ),
	CONSTRAINT fk_cluster_channel_relation_adapter_channel_id FOREIGN KEY (adapter_channel_id) REFERENCES dvbts2.adapter_channels (id),
	CONSTRAINT fk_cluster_channel_relation_cluster_id FOREIGN KEY (cluster_id) REFERENCES adm.clusters (id)
);

CREATE UNIQUE INDEX uix_cluster_adapter_channel_relation ON dvbts2.cluster_adapter_channel_relation ( cluster_id, adapter_channel_id );

/*
For output streaming (live)
*/
CREATE TABLE dvbts2.stream_forward (
	id BIGSERIAL,

	adapter_channel_id BIGINT, -- reference to dvbts2.adapter_channels to what channel this stream forward belongs
	cluster_adapter_channel_relation_id BIGINT, -- reference to dvbts2.adapter_channels to what channel this stream forward belongs

	event_type VARCHAR(255) NOT NULL, -- = WAITING / PREPARING / READY / STREAMING / STOPPED / CLOSED / FAILED / PROCESSED / LIMIT_EXCEEDED

	-- example of whole output stream url: http://test:12345@192.168.1.55:4000/testOut
	output_stream_protocol VARCHAR(255) NOT NULL, -- HTTP|HTTPS|RTP|RTSP|etc.
	output_stream_host VARCHAR(255) NOT NULL, -- hostname or IP address stream output e.g. 192.168.1.55
	output_stream_port INTEGER NOT NULL, -- output stream port e.g. 2000
	output_stream_username VARCHAR(255), -- optional username for access stream output e.g. test
	output_stream_password VARCHAR(255), -- optional password for access stream output e.g. 12345
	output_stream_url_path VARCHAR(255), -- additional path information in output stream url e.g. /testOut
	output_stream_timeout INTEGER NOT NULL DEFAULT 30000, -- closes connection if there is no longer any client activity
	output_stream_client_limit INTEGER NOT NULL DEFAULT 1, -- limit for parallel connected clients in the same time, client above limit will be refused with message limit exceeded

	-- statistic information
	date_streaming_start TIMESTAMP NOT NULL, -- when output streaming started
	date_streaming_stop TIMESTAMP NOT NULL, -- when output streaming stopped
	date_streaming_failed TIMESTAMP, -- when some error occur during output streaming
	streaming_failed_message VARCHAR(255), -- specified error message what happened
	connection_attempts INTEGER, -- how many connection attempts client made

	active INTEGER NOT NULL DEFAULT 1, -- 1 = true, 0 = false, determine whenever this stream forward is active and available, false = deleted and not active

	-- new added field for administration
	date_changed TIMESTAMP, -- When somebody change anything, timestamp is set
	user_changed VARCHAR(128), -- Same as in previous case, any change is registered - who done that change

	note TEXT, -- Some useful information to Admin eyes only
	date_created TIMESTAMP NOT NULL DEFAULT NOW(), -- whenever this record was created

	CONSTRAINT pk_stream_forward PRIMARY KEY ( id ),
	CONSTRAINT fk_stream_forward_adapter_channel_id FOREIGN KEY (adapter_channel_id) REFERENCES dvbts2.adapter_channels (id),
	CONSTRAINT fk_stream_forward_adapter_cluster_adapter_channel_relation_id FOREIGN KEY (cluster_adapter_channel_relation_id) REFERENCES dvbts2.cluster_adapter_channel_relation (id)
);

/*
Used to store information about programme of some channel in EPG
*/
CREATE TABLE dvbts2.epg_programmes (
	id BIGSERIAL,

	channel_id BIGINT NOT NULL, -- reference to dvbts2.channels to what channel this epg program belongs

	date_start TIMESTAMP NOT NULL, -- when programme is scheduled to start
	date_stop TIMESTAMP NOT NULL, -- when programme is scheduled to stop
	date_make DATE, -- date of creating programme

	audio VARCHAR(255), -- values separated by | , e.g. stereo|dual
	video_aspect VARCHAR(255), -- e.g. 16:9
	video_quality VARCHAR(255), -- e.g. HDTV

	subtitles_type VARCHAR(255), -- e.g. teletext
	subtitles_lang VARCHAR(255),

	rating_system VARCHAR(255), -- e.g. VCHIP
	rating_value VARCHAR(255), -- e.g. TV-14

	orig_lang VARCHAR(255),
	length INTEGER,
	icon TEXT,
	url VARCHAR(255),
	country VARCHAR(255),
	episode_num INTEGER,
	star_rating VARCHAR(255),
	previously_shown TIMESTAMP, -- when programme was scheduled to start previously
	premiere INTEGER,
	last_chance INTEGER,
	new INTEGER,
	
	active INTEGER NOT NULL DEFAULT 1, -- 1 = true, 0 = false, determine whenever this epg programme is active and available, false = deleted and not active

	date_created TIMESTAMP NOT NULL DEFAULT NOW(), -- whenever this record was created

	CONSTRAINT pk_epg_programmes PRIMARY KEY ( id ),
	CONSTRAINT fk_epg_programmes_subtitles_lang FOREIGN KEY (subtitles_lang) REFERENCES dvbts2.lang (name),
	CONSTRAINT fk_epg_programmes_orig_lang FOREIGN KEY (orig_lang) REFERENCES dvbts2.lang (name),
	CONSTRAINT fk_epg_programmes_channel_id FOREIGN KEY (channel_id) REFERENCES dvbts2.channels (id)
);


CREATE TABLE dvbts2.lang (
	name VARCHAR(255) NOT NULL, -- Lang name
	name_hum VARCHAR(255) NOT NULL, -- Lang name human readable to show
	CONSTRAINT pk_lang PRIMARY KEY ( name )
);

INSERT INTO dvbts2.lang( name, name_hum ) VALUES( 'EN', 'En' );
INSERT INTO dvbts2.lang( name, name_hum ) VALUES( 'CZ', 'Cz' );
INSERT INTO dvbts2.lang( name, name_hum ) VALUES( 'SK', 'Sk' );

/*
Used to store information about programme title
*/
CREATE TABLE dvbts2.epg_programme_title (
	id BIGSERIAL,

	epg_programme_id BIGINT NOT NULL, -- reference to dvbts2.epg_programmes to what programme this title belongs

	lang_name VARCHAR(255) NOT NULL, -- language
	val TEXT, -- value
	sub INTEGER NOT NULL DEFAULT 0, -- 1 = true, 0 = false, determine whenever this epg programme title is sub-title (<sub-title lang="en">Foyle's War, Series IV: Casualties of War</sub-title>)

	active INTEGER NOT NULL DEFAULT 1, -- 1 = true, 0 = false, determine whenever this epg programme title is active and available, false = deleted and not active

	date_created TIMESTAMP NOT NULL DEFAULT NOW(), -- whenever this record was created

	CONSTRAINT pk_epg_programme_title PRIMARY KEY ( id ),
	CONSTRAINT fk_epg_programme_title_epg_programme_id FOREIGN KEY (epg_programme_id) REFERENCES dvbts2.epg_programmes (id),
	CONSTRAINT fk_epg_programme_title_lang_name FOREIGN KEY (lang_name) REFERENCES dvbts2.lang (name)
);

/*
Used to store information about programme description
*/
CREATE TABLE dvbts2.epg_programme_desc (
	id BIGSERIAL,

	epg_programme_id BIGINT NOT NULL, -- reference to dvbts2.epg_programmes to what programme this description belongs

	lang_name VARCHAR(255) NOT NULL, -- language
	val TEXT, -- value

	active INTEGER NOT NULL DEFAULT 1, -- 1 = true, 0 = false, determine whenever this epg programme description is active and available, false = deleted and not active

	date_created TIMESTAMP NOT NULL DEFAULT NOW(), -- whenever this record was created

	CONSTRAINT pk_epg_programme_desc PRIMARY KEY ( id ),
	CONSTRAINT fk_epg_programme_desc_epg_programme_id FOREIGN KEY (epg_programme_id) REFERENCES dvbts2.epg_programmes (id),
	CONSTRAINT fk_epg_programme_desc_lang_name FOREIGN KEY (lang_name) REFERENCES dvbts2.lang (name)
);

/*
Used to store information about programme category
*/
CREATE TABLE dvbts2.epg_programme_category (
	id BIGSERIAL,

	epg_programme_id BIGINT NOT NULL, -- reference to dvbts2.epg_programmes to what programme this category belongs

	lang_name VARCHAR(255) NOT NULL, -- language
	val TEXT, -- value

	active INTEGER NOT NULL DEFAULT 1, -- 1 = true, 0 = false, determine whenever this epg programme category is active and available, false = deleted and not active

	date_created TIMESTAMP NOT NULL DEFAULT NOW(), -- whenever this record was created

	CONSTRAINT pk_epg_programme_category PRIMARY KEY ( id ),
	CONSTRAINT fk_epg_programme_category_epg_programme_id FOREIGN KEY (epg_programme_id) REFERENCES dvbts2.epg_programmes (id),
	CONSTRAINT fk_epg_programme_category_lang_name FOREIGN KEY (lang_name) REFERENCES dvbts2.lang (name)
);


CREATE TABLE dvbts2.credit_type (
	name VARCHAR(255) NOT NULL, -- Credit type name
	name_hum VARCHAR(255) NOT NULL, -- Credit type name human readable to show
	CONSTRAINT pk_credit_type PRIMARY KEY ( name )
);

INSERT INTO dvbts2.credit_type( name, name_hum ) VALUES( 'ACTOR', 'Actor' );
INSERT INTO dvbts2.credit_type( name, name_hum ) VALUES( 'DIRECTOR', 'Director' );
INSERT INTO dvbts2.credit_type( name, name_hum ) VALUES( 'PRODUCER', 'Producer' );
INSERT INTO dvbts2.credit_type( name, name_hum ) VALUES( 'PRESENTER', 'Presenter' );

/*
Used to store information about programme credits
*/
CREATE TABLE dvbts2.epg_programme_credit (
	id BIGSERIAL,

	epg_programme_id BIGINT NOT NULL, -- reference to dvbts2.epg_programmes to what programme this credit belongs

	credit_type VARCHAR(255) NOT NULL, -- credit type
	val TEXT, -- value = name

	active INTEGER NOT NULL DEFAULT 1, -- 1 = true, 0 = false, determine whenever this epg programme credit is active and available, false = deleted and not active

	date_created TIMESTAMP NOT NULL DEFAULT NOW(), -- whenever this record was created

	CONSTRAINT pk_epg_programme_credit PRIMARY KEY ( id ),
	CONSTRAINT fk_epg_programme_credit_epg_programme_id FOREIGN KEY (epg_programme_id) REFERENCES dvbts2.epg_programmes (id),
	CONSTRAINT fk_epg_programme_credit_credit_type FOREIGN KEY (credit_type) REFERENCES dvbts2.credit_type (name)
);

/*
Used to store information about channel thumbnails
*/
CREATE TABLE dvbts2.channel_thumbs (
	id BIGSERIAL,

	channel_id BIGINT NOT NULL, -- reference to dvbts2.channels to what channel this thumbnail belongs

	path VARCHAR(255) NOT NULL, -- where is thumbnail saved
	filename VARCHAR(255) NOT NULL, -- name of the file where is thumbnail data stored
	size INTEGER NOT NULL, -- size of stored thumbnail
	format VARCHAR(128) NOT NULL, -- stored thumbnail format = JPEG / PNG

	active INTEGER NOT NULL DEFAULT 1, -- 1 = true, 0 = false, determine whenever this thumbnail is active and available, false = deleted and not active

	date_created TIMESTAMP NOT NULL DEFAULT NOW(), -- whenever this record was created

	CONSTRAINT pk_channel_thumbs PRIMARY KEY ( id ),
	CONSTRAINT fk_channel_thumbs_channel_id FOREIGN KEY (channel_id) REFERENCES dvbts2.channels (id)
);

/*
For information about throughput of each channel
*/
CREATE TABLE dvbts2.stream_throughput (
	id BIGSERIAL,

	adapter_channel_id BIGINT NOT NULL, -- reference to dvbts2.adapter_channels to what channel this stream throughput belongs

	val INTEGER NOT NULL, -- actual throughput value in kbps

	note TEXT, -- Some useful information to Admin eyes only
	date_created TIMESTAMP NOT NULL DEFAULT NOW(), -- whenever this record was created

	CONSTRAINT pk_stream_throughput PRIMARY KEY ( id ),
	CONSTRAINT fk_stream_throughput_channel_id FOREIGN KEY (adapter_channel_id) REFERENCES dvbts2.adapter_channels (id)
);

/*
For information about stream statistics of each channel
*/
CREATE TABLE dvbts2.stream_statistics (
	id BIGSERIAL,

	adapter_channel_id BIGINT NOT NULL, -- reference to dvbts2.adapter_channels to what channel this stream statistics belongs

	err INTEGER NOT NULL, -- actual stream packet error value
	packets INTEGER NOT NULL, -- actual stream packet value

	-- todo add more info columns

	note TEXT, -- Some useful information to Admin eyes only
	date_created TIMESTAMP NOT NULL DEFAULT NOW(), -- whenever this record was created

	CONSTRAINT pk_stream_statistics PRIMARY KEY ( id ),
	CONSTRAINT fk_stream_statistics_adapter_channel_id FOREIGN KEY (adapter_channel_id) REFERENCES dvbts2.adapter_channels (id)
);

/*
For teletext data
*/
CREATE TABLE dvbts2.txt_data (
	id BIGSERIAL,

	adapter_channel_id BIGINT NOT NULL, -- reference to dvbts2.adapter_channels to what channel this stream forward belongs

	-- todo teletext

	active INTEGER NOT NULL DEFAULT 1, -- 1 = true, 0 = false, determine whenever this stream forward is active and available, false = deleted and not active

	date_created TIMESTAMP NOT NULL DEFAULT NOW(), -- whenever this record was created

	CONSTRAINT pk_stream_forward PRIMARY KEY ( id ),
	CONSTRAINT fk_stream_forward_adapter_channel_id FOREIGN KEY (adapter_channel_id) REFERENCES dvbts2.adapter_channels (id)
);


/*
Used to strore logging information what is happening in all instances of PadmeDVBTS2 in real time
*/
CREATE TABLE dvbts2.debuglog (
	id BIGSERIAL,

	server_id BIGINT, -- reference to server table - what server this setup belongs
	ident VARCHAR(255), -- Specifies PadmeDVBTS2 instance, this log belongs
	adapter_id BIGINT, -- Specifies adapter, this log belongs
	adapter_channel_id BIGINT, -- Specifies adapter channel, this log belongs
	thread_id BIGINT, -- Specifies thread, this log belongs

	level VARCHAR(255) NOT NULL, -- INFO / DEBUG / WARN / ERROR / FATAL

	logger_name VARCHAR(255),
	message VARCHAR(255),
	parameters VARCHAR(255),
	source_class_name VARCHAR(255),
	source_method_name VARCHAR(255),
	thrown TEXT,

	date_created TIMESTAMP NOT NULL DEFAULT NOW(),

	CONSTRAINT pk_debuglog PRIMARY KEY (id)
);
