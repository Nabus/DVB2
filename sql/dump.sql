CREATE SCHEMA dvbts2;

/*
Used to store information about BOX where PadmeDVBTS2 running
*/
CREATE TABLE dvbts2.servers (
	id serial,

	name VARCHAR(255) NOT NULL, -- Name of server - for admins
	location VARCHAR(255) NOT NULL, -- Comment for admins
	ident VARCHAR(255) NOT NULL, -- assigned to some configuration Server.properties - here just for information admin - important is in server_setup

	hostname VARCHAR(255) NOT NULL, -- Server hostname
	ip VARCHAR(255) NOT NULL, -- IP address of server
	path VARCHAR(255) NOT NULL, -- Path where are shares accessible

	date_changed TIMESTAMP, -- When somebody change anything, timestamp is set
	user_changed VARCHAR(128), -- Same as in previous case, any change is registered - who done that change

	note TEXT, -- Some useful information to Admin eyes only
	created TIMESTAMP NOT NULL DEFAULT NOW(),

	CONSTRAINT pk_servers PRIMARY KEY ( id )
);

/*
Used to store information about settings for PadmeDVBTS2 instance
*/
CREATE TABLE dvbts2.server_setup (
	id SERIAL,

	server_id INTEGER NOT NULL, -- reference to server table - what server this setup belongs

	ident VARCHAR(255) NOT NULL, -- assigned to some configuration Server.properties
	setup_key VARCHAR(255) NOT NULL,
	setup_value VARCHAR(255) NOT NULL,

	date_changed TIMESTAMP, -- When somebody change any of server details
	user_changed VARCHAR(128), -- Same as in previous case, any change is registered - who done that change

	note TEXT, -- Some useful information to Admin eyes only about server
	created TIMESTAMP NOT NULL DEFAULT NOW(),

	CONSTRAINT pk_server_setup PRIMARY KEY ( id ),
	CONSTRAINT fk_server_setup_server_id FOREIGN KEY (server_id) REFERENCES dvbts2.servers (id)
);

CREATE UNIQUE INDEX uix_server_setup ON dvbts2.server_setup ( LOWER( ident ), LOWER( setup_key ) );


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
	id SERIAL,
	
	ident VARCHAR(255) NOT NULL, -- identification to what PadmeDVBTS2 instance this adapter belongs

	path VARCHAR(255) NOT NULL, -- path to adapter device in system, e.g. /dev/dvb/adapter0
	adapter_type VARCHAR(255) NOT NULL, -- adapter type to use - DVB-T / DVB-S / etc.

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
	CONSTRAINT fk_adapters_adapter_type FOREIGN KEY (adapter_type) REFERENCES dvbts2.adapter_type (name)
);

/*
Used to store information about channels to process, information for making thumbnails and so on
*/
CREATE TABLE dvbts2.channels (
	id SERIAL,

	adapter_id INTEGER NOT NULL, -- identification to what adapter this channel belongs

	name VARCHAR(255) NOT NULL, -- channel name e.g. CT 1
	ident VARCHAR(255) NOT NULL, -- channel identification e.g. CT1

	pnr INTEGER NOT NULL, -- pnr id of stream

	thumb_save_path VARCHAR(255), -- path to save thumbnails from source stream (/opt/thumbnails/CT1/)
	thumb_save_filename_pattern VARCHAR(255), -- pattern for creating file name by convention ('CT1'_yyyy_mm_dd_hh_mm_ss'.jpg')
	thumb_save_period INTEGER NOT NULL DEFAULT 15000, -- regular time period in miliseconds to create thumbnail from source stream
	thumb_save_format VARCHAR(255) NOT NULL DEFAULT 'JPEG', -- format of thumbnail = JPEG or PNG

	-- statistic information
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
	CONSTRAINT fk_channels_adapter_id FOREIGN KEY (adapter_id) REFERENCES dvbts2.adapters (id)
);

/*
Used to store information about channels in EPG
*/
CREATE TABLE dvbts2.epg_channels (
	id SERIAL,
	
	channel_id INTEGER NOT NULL, -- reference to dvbts2.channels to what channel this epg channel belongs

	dvb_channel_id VARCHAR(255) NOT NULL, -- channel id from dvb e.g. I10436.labs.zap2it.com
	display_name TEXT, -- display names of channel separated by | e.g. 13 KERA|13 KERA TX42822|
	icon TEXT,

	active INTEGER NOT NULL DEFAULT 1, -- 1 = true, 0 = false, determine whenever this epg channel is active and available, false = deleted and not active

	date_created TIMESTAMP NOT NULL DEFAULT NOW(), -- whenever this record was created

	CONSTRAINT pk_epg_channels PRIMARY KEY ( id ),
	CONSTRAINT fk_epg_channels_channel_id FOREIGN KEY (channel_id) REFERENCES dvbts2.channels (id)
);

/*
Used to store information about programme of some channel in EPG
*/
CREATE TABLE dvbts2.epg_programmes (
	id SERIAL,

	epg_channel_id INTEGER NOT NULL, -- reference to dvbts2.epg_channels to what channel this epg program belongs

	date_start TIMESTAMP NOT NULL, -- when programme is scheduled to start
	date_stop TIMESTAMP NOT NULL, -- when programme is scheduled to stop
	date_make DATE, -- date of creating programme

	episode_dd_progid VARCHAR(255), -- e.g. EP00003847.0074
	episode_onscreen VARCHAR(255), -- e.g. 901

	audio VARCHAR(255), -- values separated by | , e.g. stereo|dual

	previously_shown TIMESTAMP, -- when programme was scheduled to start previously

	subtitles_type VARCHAR(255), -- e.g. teletext
	subtitles_value VARCHAR(255),

	rating_system VARCHAR(255), -- e.g. VCHIP
	rating_value VARCHAR(255), -- e.g. TV-14


	active INTEGER NOT NULL DEFAULT 1, -- 1 = true, 0 = false, determine whenever this epg programme is active and available, false = deleted and not active

	date_created TIMESTAMP NOT NULL DEFAULT NOW(), -- whenever this record was created

	CONSTRAINT pk_epg_programmes PRIMARY KEY ( id ),
	CONSTRAINT fk_epg_programmes_epg_channel_id FOREIGN KEY (epg_channel_id) REFERENCES dvbts2.epg_channels (id)
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
	id SERIAL,

	epg_programme_id INTEGER NOT NULL, -- reference to dvbts2.epg_programmes to what programme this title belongs

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
	id SERIAL,

	epg_programme_id INTEGER NOT NULL, -- reference to dvbts2.epg_programmes to what programme this description belongs

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
	id SERIAL,

	epg_programme_id INTEGER NOT NULL, -- reference to dvbts2.epg_programmes to what programme this category belongs

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
	id SERIAL,

	epg_programme_id INTEGER NOT NULL, -- reference to dvbts2.epg_programmes to what programme this credit belongs

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
	id SERIAL,

	channel_id INTEGER NOT NULL, -- reference to dvbts2.channels to what channel this thumbnail belongs

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
For output streaming (live)
*/
CREATE TABLE dvbts2.stream_forward (
	id SERIAL,

	channel_id INTEGER NOT NULL, -- reference to dvbts2.channels to what channel this stream forward belongs

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
	CONSTRAINT fk_stream_forward_channel_id FOREIGN KEY (channel_id) REFERENCES dvbts2.channels (id)
);

/*
For information about throughput of each channel
*/
CREATE TABLE dvbts2.stream_throughput (
	id SERIAL,

	channel_id INTEGER NOT NULL, -- reference to dvbts2.channels to what channel this stream throughput belongs

	val INTEGER NOT NULL, -- actual throughput value

	note TEXT, -- Some useful information to Admin eyes only
	date_created TIMESTAMP NOT NULL DEFAULT NOW(), -- whenever this record was created

	CONSTRAINT pk_stream_throughput PRIMARY KEY ( id ),
	CONSTRAINT fk_stream_throughput_channel_id FOREIGN KEY (channel_id) REFERENCES dvbts2.channels (id)
);

/*
For information about stream statistics of each channel
*/
CREATE TABLE dvbts2.stream_statistics (
	id SERIAL,

	channel_id INTEGER NOT NULL, -- reference to dvbts2.channels to what channel this stream statistics belongs

	err INTEGER NOT NULL, -- actual stream packet error value
	packets INTEGER NOT NULL, -- actual stream packet value

	-- todo add more info columns

	note TEXT, -- Some useful information to Admin eyes only
	date_created TIMESTAMP NOT NULL DEFAULT NOW(), -- whenever this record was created

	CONSTRAINT pk_stream_statistics PRIMARY KEY ( id ),
	CONSTRAINT fk_stream_statistics_channel_id FOREIGN KEY (channel_id) REFERENCES dvbts2.channels (id)
);

/*
For teletext data
*/
CREATE TABLE dvbts2.txt_data (
	id SERIAL,

	channel_id INTEGER NOT NULL, -- reference to dvbts2.channels to what channel this stream forward belongs

	-- todo teletext

	active INTEGER NOT NULL DEFAULT 1, -- 1 = true, 0 = false, determine whenever this stream forward is active and available, false = deleted and not active

	date_created TIMESTAMP NOT NULL DEFAULT NOW(), -- whenever this record was created

	CONSTRAINT fk_stream_forward_channel_id FOREIGN KEY (channel_id) REFERENCES dvbts2.channels (id)
);


/*
Used to strore logging information what is happening in all instances of PadmeDVBTS2 in real time
*/
CREATE TABLE dvbts2.debuglog (
	id SERIAL,

	server_id INTEGER, -- reference to server table - what server this setup belongs
	ident VARCHAR(255), -- Specifies PadmeDVBTS2 instance, this log belongs
	channel_id INTEGER, -- Specifies channel, this log belongs
	thread_id INTEGER, -- Specifies thread, this log belongs

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
