--
-- Name: dvbts2; Type: SCHEMA; Schema: -; Owner: dvbts2
--

CREATE SCHEMA dvbts2;
ALTER SCHEMA dvbts2 OWNER TO dvbts2;

--
-- Name: adapter_type; Type: TABLE; Schema: dvbts2; Owner: dvbts2; Tablespace: 
--

CREATE TABLE adapter_type (
    name character varying(255) NOT NULL,
    name_hum character varying(255) NOT NULL
);


ALTER TABLE adapter_type OWNER TO dvbts2;

--
-- Name: adapters; Type: TABLE; Schema: dvbts2; Owner: dvbts2; Tablespace: 
--

CREATE TABLE adapters (
    id integer NOT NULL,
    ident character varying(255) NOT NULL,
    path character varying(255) NOT NULL,
    adapter_type character varying(255) NOT NULL,
    frequency integer NOT NULL,
    bandwidth integer DEFAULT 8 NOT NULL,
    transmission_mode character varying(255) DEFAULT 'auto'::character varying NOT NULL,
    guard_interval character varying(255) DEFAULT 'auto'::character varying NOT NULL,
    hierarchy character varying(255) DEFAULT 'none'::character varying NOT NULL,
    modulation character varying(255) DEFAULT 'auto'::character varying NOT NULL,
    date_start timestamp without time zone,
    date_stop timestamp without time zone,
    date_failed timestamp without time zone,
    failed_message character varying(255),
    active integer DEFAULT 1 NOT NULL,
    date_changed timestamp without time zone,
    user_changed character varying(128),
    note text,
    date_created timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE adapters OWNER TO dvbts2;

--
-- Name: adapters_id_seq; Type: SEQUENCE; Schema: dvbts2; Owner: dvbts2
--

CREATE SEQUENCE adapters_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE adapters_id_seq OWNER TO dvbts2;

--
-- Name: adapters_id_seq; Type: SEQUENCE OWNED BY; Schema: dvbts2; Owner: dvbts2
--

ALTER SEQUENCE adapters_id_seq OWNED BY adapters.id;


--
-- Name: channel_thumbs; Type: TABLE; Schema: dvbts2; Owner: dvbts2; Tablespace: 
--

CREATE TABLE channel_thumbs (
    id integer NOT NULL,
    channel_id integer NOT NULL,
    path character varying(255) NOT NULL,
    filename character varying(255) NOT NULL,
    size integer NOT NULL,
    format character varying(128) NOT NULL,
    active integer DEFAULT 1 NOT NULL,
    date_created timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE channel_thumbs OWNER TO dvbts2;

--
-- Name: channel_thumbs_id_seq; Type: SEQUENCE; Schema: dvbts2; Owner: dvbts2
--

CREATE SEQUENCE channel_thumbs_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE channel_thumbs_id_seq OWNER TO dvbts2;

--
-- Name: channel_thumbs_id_seq; Type: SEQUENCE OWNED BY; Schema: dvbts2; Owner: dvbts2
--

ALTER SEQUENCE channel_thumbs_id_seq OWNED BY channel_thumbs.id;


--
-- Name: channels; Type: TABLE; Schema: dvbts2; Owner: dvbts2; Tablespace: 
--

CREATE TABLE channels (
    id integer NOT NULL,
    adapter_id integer NOT NULL,
    name character varying(255) NOT NULL,
    ident character varying(255) NOT NULL,
    pnr integer NOT NULL,
    thumb_save_path character varying(255),
    thumb_save_filename_pattern character varying(255),
    thumb_save_period integer DEFAULT 15000 NOT NULL,
    thumb_save_format character varying(255) DEFAULT 'JPEG'::character varying NOT NULL,
    date_start timestamp without time zone,
    date_stop timestamp without time zone,
    date_failed timestamp without time zone,
    failed_message character varying(255),
    active integer DEFAULT 1 NOT NULL,
    date_changed timestamp without time zone,
    user_changed character varying(128),
    note text,
    date_created timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE channels OWNER TO dvbts2;

--
-- Name: channels_id_seq; Type: SEQUENCE; Schema: dvbts2; Owner: dvbts2
--

CREATE SEQUENCE channels_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE channels_id_seq OWNER TO dvbts2;

--
-- Name: channels_id_seq; Type: SEQUENCE OWNED BY; Schema: dvbts2; Owner: dvbts2
--

ALTER SEQUENCE channels_id_seq OWNED BY channels.id;


--
-- Name: credit_type; Type: TABLE; Schema: dvbts2; Owner: dvbts2; Tablespace: 
--

CREATE TABLE credit_type (
    name character varying(255) NOT NULL,
    name_hum character varying(255) NOT NULL
);


ALTER TABLE credit_type OWNER TO dvbts2;

--
-- Name: debuglog; Type: TABLE; Schema: dvbts2; Owner: dvbts2; Tablespace: 
--

CREATE TABLE debuglog (
    id integer NOT NULL,
    server_id integer,
    ident character varying(255),
    channel_id integer,
    thread_id integer,
    level character varying(255) NOT NULL,
    logger_name character varying(255),
    message character varying(255),
    parameters character varying(255),
    source_class_name character varying(255),
    source_method_name character varying(255),
    thrown text,
    date_created timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE debuglog OWNER TO dvbts2;

--
-- Name: debuglog_id_seq; Type: SEQUENCE; Schema: dvbts2; Owner: dvbts2
--

CREATE SEQUENCE debuglog_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE debuglog_id_seq OWNER TO dvbts2;

--
-- Name: debuglog_id_seq; Type: SEQUENCE OWNED BY; Schema: dvbts2; Owner: dvbts2
--

ALTER SEQUENCE debuglog_id_seq OWNED BY debuglog.id;


--
-- Name: epg_programme_credit; Type: TABLE; Schema: dvbts2; Owner: dvbts2; Tablespace: 
--

CREATE TABLE epg_programme_credit (
    id integer NOT NULL,
    epg_programme_id integer NOT NULL,
    credit_type character varying(255) NOT NULL,
    val text,
    active integer DEFAULT 1 NOT NULL,
    date_created timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE epg_programme_credit OWNER TO dvbts2;

--
-- Name: epg_programme_credit_id_seq; Type: SEQUENCE; Schema: dvbts2; Owner: dvbts2
--

CREATE SEQUENCE epg_programme_credit_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE epg_programme_credit_id_seq OWNER TO dvbts2;

--
-- Name: epg_programme_credit_id_seq; Type: SEQUENCE OWNED BY; Schema: dvbts2; Owner: dvbts2
--

ALTER SEQUENCE epg_programme_credit_id_seq OWNED BY epg_programme_credit.id;


--
-- Name: epg_programmes; Type: TABLE; Schema: dvbts2; Owner: dvbts2; Tablespace: 
--

CREATE TABLE epg_programmes (
    id integer NOT NULL,
    channel_id integer NOT NULL,
    date_start timestamp without time zone NOT NULL,
    date_stop timestamp without time zone NOT NULL,
    date_make date,
    title character varying(255),
    title_lang character varying(255),
    subtitle text,
    subtitle_lang character varying(255),
    description text,
    description_lang character varying(255),
    lang character varying(255),
    video_aspect character varying(255),
    audio character varying(255),
    rating_system character varying(255),
    rating_value character varying(255),
    subtitles_type character varying(255),
    subtitles_lang character varying(255),
    active integer DEFAULT 1 NOT NULL
);


ALTER TABLE epg_programmes OWNER TO dvbts2;

--
-- Name: epg_programmes_id_seq; Type: SEQUENCE; Schema: dvbts2; Owner: dvbts2
--

CREATE SEQUENCE epg_programmes_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE epg_programmes_id_seq OWNER TO dvbts2;

--
-- Name: epg_programmes_id_seq; Type: SEQUENCE OWNED BY; Schema: dvbts2; Owner: dvbts2
--

ALTER SEQUENCE epg_programmes_id_seq OWNED BY epg_programmes.id;


--
-- Name: lang; Type: TABLE; Schema: dvbts2; Owner: dvbts2; Tablespace: 
--

CREATE TABLE lang (
    name character varying(255) NOT NULL,
    name_hum character varying(255) NOT NULL
);


ALTER TABLE lang OWNER TO dvbts2;

--
-- Name: server_setup; Type: TABLE; Schema: dvbts2; Owner: dvbts2; Tablespace: 
--

CREATE TABLE server_setup (
    id integer NOT NULL,
    server_id integer NOT NULL,
    ident character varying(255) NOT NULL,
    setup_key character varying(255) NOT NULL,
    setup_value character varying(255) NOT NULL,
    date_changed timestamp without time zone,
    user_changed character varying(128),
    note text,
    created timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE server_setup OWNER TO dvbts2;

--
-- Name: server_setup_id_seq; Type: SEQUENCE; Schema: dvbts2; Owner: dvbts2
--

CREATE SEQUENCE server_setup_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE server_setup_id_seq OWNER TO dvbts2;

--
-- Name: server_setup_id_seq; Type: SEQUENCE OWNED BY; Schema: dvbts2; Owner: dvbts2
--

ALTER SEQUENCE server_setup_id_seq OWNED BY server_setup.id;


--
-- Name: servers; Type: TABLE; Schema: dvbts2; Owner: dvbts2; Tablespace: 
--

CREATE TABLE servers (
    id integer NOT NULL,
    name character varying(255) NOT NULL,
    location character varying(255) NOT NULL,
    ident character varying(255) NOT NULL,
    hostname character varying(255) NOT NULL,
    ip character varying(255) NOT NULL,
    path character varying(255) NOT NULL,
    date_changed timestamp without time zone,
    user_changed character varying(128),
    note text,
    created timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE servers OWNER TO dvbts2;

--
-- Name: servers_id_seq; Type: SEQUENCE; Schema: dvbts2; Owner: dvbts2
--

CREATE SEQUENCE servers_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE servers_id_seq OWNER TO dvbts2;

--
-- Name: servers_id_seq; Type: SEQUENCE OWNED BY; Schema: dvbts2; Owner: dvbts2
--

ALTER SEQUENCE servers_id_seq OWNED BY servers.id;


--
-- Name: stream_forward; Type: TABLE; Schema: dvbts2; Owner: dvbts2; Tablespace: 
--

CREATE TABLE stream_forward (
    id integer NOT NULL,
    channel_id integer NOT NULL,
    event_type character varying(255) NOT NULL,
    output_stream_protocol character varying(255) NOT NULL,
    output_stream_host character varying(255) NOT NULL,
    output_stream_port integer NOT NULL,
    output_stream_username character varying(255),
    output_stream_password character varying(255),
    output_stream_url_path character varying(255),
    output_stream_timeout integer DEFAULT 30000 NOT NULL,
    output_stream_client_limit integer DEFAULT 1 NOT NULL,
    date_streaming_start timestamp without time zone NOT NULL,
    date_streaming_stop timestamp without time zone NOT NULL,
    date_streaming_failed timestamp without time zone,
    streaming_failed_message character varying(255),
    connection_attempts integer,
    active integer DEFAULT 1 NOT NULL,
    date_changed timestamp without time zone,
    user_changed character varying(128),
    note text,
    date_created timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE stream_forward OWNER TO dvbts2;

--
-- Name: stream_forward_id_seq; Type: SEQUENCE; Schema: dvbts2; Owner: dvbts2
--

CREATE SEQUENCE stream_forward_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE stream_forward_id_seq OWNER TO dvbts2;

--
-- Name: stream_forward_id_seq; Type: SEQUENCE OWNED BY; Schema: dvbts2; Owner: dvbts2
--

ALTER SEQUENCE stream_forward_id_seq OWNED BY stream_forward.id;


--
-- Name: stream_statistics; Type: TABLE; Schema: dvbts2; Owner: dvbts2; Tablespace: 
--

CREATE TABLE stream_statistics (
    id integer NOT NULL,
    channel_id integer NOT NULL,
    err integer NOT NULL,
    packets integer NOT NULL,
    note text,
    date_created timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE stream_statistics OWNER TO dvbts2;

--
-- Name: stream_statistics_id_seq; Type: SEQUENCE; Schema: dvbts2; Owner: dvbts2
--

CREATE SEQUENCE stream_statistics_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE stream_statistics_id_seq OWNER TO dvbts2;

--
-- Name: stream_statistics_id_seq; Type: SEQUENCE OWNED BY; Schema: dvbts2; Owner: dvbts2
--

ALTER SEQUENCE stream_statistics_id_seq OWNED BY stream_statistics.id;


--
-- Name: stream_throughput; Type: TABLE; Schema: dvbts2; Owner: dvbts2; Tablespace: 
--

CREATE TABLE stream_throughput (
    id integer NOT NULL,
    channel_id integer NOT NULL,
    val integer NOT NULL,
    note text,
    date_created timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE stream_throughput OWNER TO dvbts2;

--
-- Name: stream_throughput_id_seq; Type: SEQUENCE; Schema: dvbts2; Owner: dvbts2
--

CREATE SEQUENCE stream_throughput_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE stream_throughput_id_seq OWNER TO dvbts2;

--
-- Name: stream_throughput_id_seq; Type: SEQUENCE OWNED BY; Schema: dvbts2; Owner: dvbts2
--

ALTER SEQUENCE stream_throughput_id_seq OWNED BY stream_throughput.id;


--
-- Name: txt_data; Type: TABLE; Schema: dvbts2; Owner: dvbts2; Tablespace: 
--

CREATE TABLE txt_data (
    id integer NOT NULL,
    channel_id integer NOT NULL,
    active integer DEFAULT 1 NOT NULL,
    date_created timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE txt_data OWNER TO dvbts2;

--
-- Name: txt_data_id_seq; Type: SEQUENCE; Schema: dvbts2; Owner: dvbts2
--

CREATE SEQUENCE txt_data_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE txt_data_id_seq OWNER TO dvbts2;

--
-- Name: txt_data_id_seq; Type: SEQUENCE OWNED BY; Schema: dvbts2; Owner: dvbts2
--

ALTER SEQUENCE txt_data_id_seq OWNED BY txt_data.id;


--
-- Name: id; Type: DEFAULT; Schema: dvbts2; Owner: dvbts2
--

ALTER TABLE ONLY adapters ALTER COLUMN id SET DEFAULT nextval('adapters_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: dvbts2; Owner: dvbts2
--

ALTER TABLE ONLY channel_thumbs ALTER COLUMN id SET DEFAULT nextval('channel_thumbs_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: dvbts2; Owner: dvbts2
--

ALTER TABLE ONLY channels ALTER COLUMN id SET DEFAULT nextval('channels_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: dvbts2; Owner: dvbts2
--

ALTER TABLE ONLY debuglog ALTER COLUMN id SET DEFAULT nextval('debuglog_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: dvbts2; Owner: dvbts2
--

ALTER TABLE ONLY epg_programme_credit ALTER COLUMN id SET DEFAULT nextval('epg_programme_credit_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: dvbts2; Owner: dvbts2
--

ALTER TABLE ONLY epg_programmes ALTER COLUMN id SET DEFAULT nextval('epg_programmes_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: dvbts2; Owner: dvbts2
--

ALTER TABLE ONLY server_setup ALTER COLUMN id SET DEFAULT nextval('server_setup_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: dvbts2; Owner: dvbts2
--

ALTER TABLE ONLY servers ALTER COLUMN id SET DEFAULT nextval('servers_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: dvbts2; Owner: dvbts2
--

ALTER TABLE ONLY stream_forward ALTER COLUMN id SET DEFAULT nextval('stream_forward_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: dvbts2; Owner: dvbts2
--

ALTER TABLE ONLY stream_statistics ALTER COLUMN id SET DEFAULT nextval('stream_statistics_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: dvbts2; Owner: dvbts2
--

ALTER TABLE ONLY stream_throughput ALTER COLUMN id SET DEFAULT nextval('stream_throughput_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: dvbts2; Owner: dvbts2
--

ALTER TABLE ONLY txt_data ALTER COLUMN id SET DEFAULT nextval('txt_data_id_seq'::regclass);


--
-- Data for Name: adapter_type; Type: TABLE DATA; Schema: dvbts2; Owner: dvbts2
--

COPY adapter_type (name, name_hum) FROM stdin;
DVBT	DVB-T
DVBT2	DVB-T2
DVBS	DVB-S
DVBS2	DVB-S2
\.


--
-- Data for Name: adapters; Type: TABLE DATA; Schema: dvbts2; Owner: dvbts2
--

COPY adapters (id, ident, path, adapter_type, frequency, bandwidth, transmission_mode, guard_interval, hierarchy, modulation, date_start, date_stop, date_failed, failed_message, active, date_changed, user_changed, note, date_created) FROM stdin;
1	08a32dc5-affd-dff5-0000-0000719a2e94	/dev/dvb/adapter0	DVBT	610000000	8	auto	auto	none	auto	2014-12-10 02:08:02.362582	\N	2014-12-09 20:23:13.708398	Unable to get event data from multiplex. Adapter #0	1	\N	\N	\N	2014-12-09 13:46:58.283555
\.


--
-- Name: adapters_id_seq; Type: SEQUENCE SET; Schema: dvbts2; Owner: dvbts2
--

SELECT pg_catalog.setval('adapters_id_seq', 3, true);


--
-- Data for Name: channel_thumbs; Type: TABLE DATA; Schema: dvbts2; Owner: dvbts2
--

COPY channel_thumbs (id, channel_id, path, filename, size, format, active, date_created) FROM stdin;
\.


--
-- Name: channel_thumbs_id_seq; Type: SEQUENCE SET; Schema: dvbts2; Owner: dvbts2
--

SELECT pg_catalog.setval('channel_thumbs_id_seq', 1, false);


--
-- Data for Name: channels; Type: TABLE DATA; Schema: dvbts2; Owner: dvbts2
--

COPY channels (id, adapter_id, name, ident, pnr, thumb_save_path, thumb_save_filename_pattern, thumb_save_period, thumb_save_format, date_start, date_stop, date_failed, failed_message, active, date_changed, user_changed, note, date_created) FROM stdin;
1	1	CT 1 HD	CT1HD	261	/home/bullet28/	'CT1'_yyyy_MM_dd_HH_mm_ss	15000	JPEG	2014-12-10 02:08:15.60276	\N	\N	\N	1	\N	\N	\N	2014-12-09 13:47:35.602598
\.


--
-- Name: channels_id_seq; Type: SEQUENCE SET; Schema: dvbts2; Owner: dvbts2
--

SELECT pg_catalog.setval('channels_id_seq', 4, true);


--
-- Data for Name: credit_type; Type: TABLE DATA; Schema: dvbts2; Owner: dvbts2
--

COPY credit_type (name, name_hum) FROM stdin;
\.


--
-- Data for Name: debuglog; Type: TABLE DATA; Schema: dvbts2; Owner: dvbts2
--

COPY debuglog (id, server_id, ident, channel_id, thread_id, level, logger_name, message, parameters, source_class_name, source_method_name, thrown, date_created) FROM stdin;
\.


--
-- Name: debuglog_id_seq; Type: SEQUENCE SET; Schema: dvbts2; Owner: dvbts2
--

SELECT pg_catalog.setval('debuglog_id_seq', 1, false);


--
-- Data for Name: epg_programme_credit; Type: TABLE DATA; Schema: dvbts2; Owner: dvbts2
--

COPY epg_programme_credit (id, epg_programme_id, credit_type, val, active, date_created) FROM stdin;
\.


--
-- Name: epg_programme_credit_id_seq; Type: SEQUENCE SET; Schema: dvbts2; Owner: dvbts2
--

SELECT pg_catalog.setval('epg_programme_credit_id_seq', 1, false);


--
-- Data for Name: epg_programmes; Type: TABLE DATA; Schema: dvbts2; Owner: dvbts2
--

COPY epg_programmes (id, channel_id, date_start, date_stop, date_make, title, title_lang, subtitle, subtitle_lang, description, description_lang, lang, video_aspect, audio, rating_system, rating_value, subtitles_type, subtitles_lang, active) FROM stdin;
\.


--
-- Name: epg_programmes_id_seq; Type: SEQUENCE SET; Schema: dvbts2; Owner: dvbts2
--

SELECT pg_catalog.setval('epg_programmes_id_seq', 9587, true);


--
-- Data for Name: lang; Type: TABLE DATA; Schema: dvbts2; Owner: dvbts2
--

COPY lang (name, name_hum) FROM stdin;
cs	Cs
\.


--
-- Data for Name: server_setup; Type: TABLE DATA; Schema: dvbts2; Owner: dvbts2
--

COPY server_setup (id, server_id, ident, setup_key, setup_value, date_changed, user_changed, note, created) FROM stdin;
16	1	08a32dc5-affd-dff5-0000-0000719a2e94	commandManagerInterface	eth0	\N	\N	\N	2014-12-09 13:45:01.744243
17	1	08a32dc5-affd-dff5-0000-0000719a2e94	commandManagerHost	127.0.0.1	\N	\N	\N	2014-12-09 13:45:01.836161
18	1	08a32dc5-affd-dff5-0000-0000719a2e94	commandManagerPort	2222	\N	\N	\N	2014-12-09 13:45:01.913795
19	1	08a32dc5-affd-dff5-0000-0000719a2e94	commandManagerAllowFrom	127.0.0.1,192.168.1.1	\N	\N	\N	2014-12-09 13:45:02.042408
20	1	08a32dc5-affd-dff5-0000-0000719a2e94	dbManagerHost	127.0.0.1	\N	\N	\N	2014-12-09 13:45:02.282801
21	1	08a32dc5-affd-dff5-0000-0000719a2e94	dbManagerPort	5432	\N	\N	\N	2014-12-09 13:45:02.372115
24	1	08a32dc5-affd-dff5-0000-0000719a2e94	dbManagerDatabase	dvbts2	\N	\N	\N	2014-12-09 13:45:02.638372
25	1	08a32dc5-affd-dff5-0000-0000719a2e94	dbManagerPeriod	60000	\N	\N	\N	2014-12-09 13:45:02.727577
26	1	08a32dc5-affd-dff5-0000-0000719a2e94	dbManagerTimeout	60000	\N	\N	\N	2014-12-09 13:45:03.124542
28	1	08a32dc5-affd-dff5-0000-0000719a2e94	epgTimestampOffset	0	\N	\N	\N	2014-12-09 13:45:03.293994
29	1	08a32dc5-affd-dff5-0000-0000719a2e94	inputServiceIp	127.0.0.1	\N	\N	\N	2014-12-09 13:45:03.384051
30	1	08a32dc5-affd-dff5-0000-0000719a2e94	inputServiceStartPort	27000	\N	\N	\N	2014-12-09 13:45:03.47119
22	1	08a32dc5-affd-dff5-0000-0000719a2e94	dbManagerUsername	dvbts2	\N	\N	\N	2014-12-09 13:45:02.459361
23	1	08a32dc5-affd-dff5-0000-0000719a2e94	dbManagerPassword	dvbts2	\N	\N	\N	2014-12-09 13:45:02.549065
27	1	08a32dc5-affd-dff5-0000-0000719a2e94	epgUpdateInterval	60000	\N	\N	\N	2014-12-09 13:45:03.204925
\.


--
-- Name: server_setup_id_seq; Type: SEQUENCE SET; Schema: dvbts2; Owner: dvbts2
--

SELECT pg_catalog.setval('server_setup_id_seq', 30, true);


--
-- Data for Name: servers; Type: TABLE DATA; Schema: dvbts2; Owner: dvbts2
--

COPY servers (id, name, location, ident, hostname, ip, path, date_changed, user_changed, note, created) FROM stdin;
1	example	example	example	example	example	example	\N	\N	\N	2014-12-09 13:40:12.688607
\.


--
-- Name: servers_id_seq; Type: SEQUENCE SET; Schema: dvbts2; Owner: dvbts2
--

SELECT pg_catalog.setval('servers_id_seq', 4, true);


--
-- Data for Name: stream_forward; Type: TABLE DATA; Schema: dvbts2; Owner: dvbts2
--

COPY stream_forward (id, channel_id, event_type, output_stream_protocol, output_stream_host, output_stream_port, output_stream_username, output_stream_password, output_stream_url_path, output_stream_timeout, output_stream_client_limit, date_streaming_start, date_streaming_stop, date_streaming_failed, streaming_failed_message, connection_attempts, active, date_changed, user_changed, note, date_created) FROM stdin;
5	1	WAITING	HTTP	192.168.1.7	27015	\N	\N	\N	30000	1	2014-12-09 13:48:06.9428	2014-12-09 13:48:06.9428	\N	\N	\N	1	\N	\N	\N	2014-12-09 13:48:06.9428
\.


--
-- Name: stream_forward_id_seq; Type: SEQUENCE SET; Schema: dvbts2; Owner: dvbts2
--

SELECT pg_catalog.setval('stream_forward_id_seq', 5, true);


--
-- Data for Name: stream_statistics; Type: TABLE DATA; Schema: dvbts2; Owner: dvbts2
--

COPY stream_statistics (id, channel_id, err, packets, note, date_created) FROM stdin;
\.


--
-- Name: stream_statistics_id_seq; Type: SEQUENCE SET; Schema: dvbts2; Owner: dvbts2
--

SELECT pg_catalog.setval('stream_statistics_id_seq', 1, false);


--
-- Data for Name: stream_throughput; Type: TABLE DATA; Schema: dvbts2; Owner: dvbts2
--

COPY stream_throughput (id, channel_id, val, note, date_created) FROM stdin;
\.


--
-- Name: stream_throughput_id_seq; Type: SEQUENCE SET; Schema: dvbts2; Owner: dvbts2
--

SELECT pg_catalog.setval('stream_throughput_id_seq', 1, false);


--
-- Data for Name: txt_data; Type: TABLE DATA; Schema: dvbts2; Owner: dvbts2
--

COPY txt_data (id, channel_id, active, date_created) FROM stdin;
\.


--
-- Name: txt_data_id_seq; Type: SEQUENCE SET; Schema: dvbts2; Owner: dvbts2
--

SELECT pg_catalog.setval('txt_data_id_seq', 1, false);


--
-- Name: pk_adapter_type; Type: CONSTRAINT; Schema: dvbts2; Owner: dvbts2; Tablespace: 
--

ALTER TABLE ONLY adapter_type
    ADD CONSTRAINT pk_adapter_type PRIMARY KEY (name);


--
-- Name: pk_adapters; Type: CONSTRAINT; Schema: dvbts2; Owner: dvbts2; Tablespace: 
--

ALTER TABLE ONLY adapters
    ADD CONSTRAINT pk_adapters PRIMARY KEY (id);


--
-- Name: pk_channel_thumbs; Type: CONSTRAINT; Schema: dvbts2; Owner: dvbts2; Tablespace: 
--

ALTER TABLE ONLY channel_thumbs
    ADD CONSTRAINT pk_channel_thumbs PRIMARY KEY (id);


--
-- Name: pk_channels; Type: CONSTRAINT; Schema: dvbts2; Owner: dvbts2; Tablespace: 
--

ALTER TABLE ONLY channels
    ADD CONSTRAINT pk_channels PRIMARY KEY (id);


--
-- Name: pk_credit_type; Type: CONSTRAINT; Schema: dvbts2; Owner: dvbts2; Tablespace: 
--

ALTER TABLE ONLY credit_type
    ADD CONSTRAINT pk_credit_type PRIMARY KEY (name);


--
-- Name: pk_debuglog; Type: CONSTRAINT; Schema: dvbts2; Owner: dvbts2; Tablespace: 
--

ALTER TABLE ONLY debuglog
    ADD CONSTRAINT pk_debuglog PRIMARY KEY (id);


--
-- Name: pk_epg_programme_credit; Type: CONSTRAINT; Schema: dvbts2; Owner: dvbts2; Tablespace: 
--

ALTER TABLE ONLY epg_programme_credit
    ADD CONSTRAINT pk_epg_programme_credit PRIMARY KEY (id);


--
-- Name: pk_epg_programmes; Type: CONSTRAINT; Schema: dvbts2; Owner: dvbts2; Tablespace: 
--

ALTER TABLE ONLY epg_programmes
    ADD CONSTRAINT pk_epg_programmes PRIMARY KEY (id);


--
-- Name: pk_lang; Type: CONSTRAINT; Schema: dvbts2; Owner: dvbts2; Tablespace: 
--

ALTER TABLE ONLY lang
    ADD CONSTRAINT pk_lang PRIMARY KEY (name);


--
-- Name: pk_server_setup; Type: CONSTRAINT; Schema: dvbts2; Owner: dvbts2; Tablespace: 
--

ALTER TABLE ONLY server_setup
    ADD CONSTRAINT pk_server_setup PRIMARY KEY (id);


--
-- Name: pk_servers; Type: CONSTRAINT; Schema: dvbts2; Owner: dvbts2; Tablespace: 
--

ALTER TABLE ONLY servers
    ADD CONSTRAINT pk_servers PRIMARY KEY (id);


--
-- Name: pk_stream_forward; Type: CONSTRAINT; Schema: dvbts2; Owner: dvbts2; Tablespace: 
--

ALTER TABLE ONLY stream_forward
    ADD CONSTRAINT pk_stream_forward PRIMARY KEY (id);


--
-- Name: pk_stream_statistics; Type: CONSTRAINT; Schema: dvbts2; Owner: dvbts2; Tablespace: 
--

ALTER TABLE ONLY stream_statistics
    ADD CONSTRAINT pk_stream_statistics PRIMARY KEY (id);


--
-- Name: pk_stream_throughput; Type: CONSTRAINT; Schema: dvbts2; Owner: dvbts2; Tablespace: 
--

ALTER TABLE ONLY stream_throughput
    ADD CONSTRAINT pk_stream_throughput PRIMARY KEY (id);


--
-- Name: uix_server_setup; Type: INDEX; Schema: dvbts2; Owner: dvbts2; Tablespace: 
--

CREATE UNIQUE INDEX uix_server_setup ON server_setup USING btree (lower((ident)::text), lower((setup_key)::text));


--
-- Name: fk_adapters_adapter_type; Type: FK CONSTRAINT; Schema: dvbts2; Owner: dvbts2
--

ALTER TABLE ONLY adapters
    ADD CONSTRAINT fk_adapters_adapter_type FOREIGN KEY (adapter_type) REFERENCES adapter_type(name);


--
-- Name: fk_channel_thumbs_channel_id; Type: FK CONSTRAINT; Schema: dvbts2; Owner: dvbts2
--

ALTER TABLE ONLY channel_thumbs
    ADD CONSTRAINT fk_channel_thumbs_channel_id FOREIGN KEY (channel_id) REFERENCES channels(id);


--
-- Name: fk_channels_adapter_id; Type: FK CONSTRAINT; Schema: dvbts2; Owner: dvbts2
--

ALTER TABLE ONLY channels
    ADD CONSTRAINT fk_channels_adapter_id FOREIGN KEY (adapter_id) REFERENCES adapters(id);


--
-- Name: fk_epg_programme_credit_credit_type; Type: FK CONSTRAINT; Schema: dvbts2; Owner: dvbts2
--

ALTER TABLE ONLY epg_programme_credit
    ADD CONSTRAINT fk_epg_programme_credit_credit_type FOREIGN KEY (credit_type) REFERENCES credit_type(name);


--
-- Name: fk_epg_programme_credit_epg_programme_id; Type: FK CONSTRAINT; Schema: dvbts2; Owner: dvbts2
--

ALTER TABLE ONLY epg_programme_credit
    ADD CONSTRAINT fk_epg_programme_credit_epg_programme_id FOREIGN KEY (epg_programme_id) REFERENCES epg_programmes(id);


--
-- Name: fk_epg_programmes_channel_id; Type: FK CONSTRAINT; Schema: dvbts2; Owner: dvbts2
--

ALTER TABLE ONLY epg_programmes
    ADD CONSTRAINT fk_epg_programmes_channel_id FOREIGN KEY (channel_id) REFERENCES channels(id);


--
-- Name: fk_epg_programmes_description_lang_name; Type: FK CONSTRAINT; Schema: dvbts2; Owner: dvbts2
--

ALTER TABLE ONLY epg_programmes
    ADD CONSTRAINT fk_epg_programmes_description_lang_name FOREIGN KEY (description_lang) REFERENCES lang(name);


--
-- Name: fk_epg_programmes_subtitle_lang_name; Type: FK CONSTRAINT; Schema: dvbts2; Owner: dvbts2
--

ALTER TABLE ONLY epg_programmes
    ADD CONSTRAINT fk_epg_programmes_subtitle_lang_name FOREIGN KEY (subtitle_lang) REFERENCES lang(name);


--
-- Name: fk_epg_programmes_subtitles_lang_name; Type: FK CONSTRAINT; Schema: dvbts2; Owner: dvbts2
--

ALTER TABLE ONLY epg_programmes
    ADD CONSTRAINT fk_epg_programmes_subtitles_lang_name FOREIGN KEY (subtitles_lang) REFERENCES lang(name);


--
-- Name: fk_epg_programmes_title_lang_name; Type: FK CONSTRAINT; Schema: dvbts2; Owner: dvbts2
--

ALTER TABLE ONLY epg_programmes
    ADD CONSTRAINT fk_epg_programmes_title_lang_name FOREIGN KEY (title_lang) REFERENCES lang(name);


--
-- Name: fk_server_setup_server_id; Type: FK CONSTRAINT; Schema: dvbts2; Owner: dvbts2
--

ALTER TABLE ONLY server_setup
    ADD CONSTRAINT fk_server_setup_server_id FOREIGN KEY (server_id) REFERENCES servers(id);


--
-- Name: fk_stream_forward_channel_id; Type: FK CONSTRAINT; Schema: dvbts2; Owner: dvbts2
--

ALTER TABLE ONLY stream_forward
    ADD CONSTRAINT fk_stream_forward_channel_id FOREIGN KEY (channel_id) REFERENCES channels(id);


--
-- Name: fk_stream_forward_channel_id; Type: FK CONSTRAINT; Schema: dvbts2; Owner: dvbts2
--

ALTER TABLE ONLY txt_data
    ADD CONSTRAINT fk_stream_forward_channel_id FOREIGN KEY (channel_id) REFERENCES channels(id);


--
-- Name: fk_stream_statistics_channel_id; Type: FK CONSTRAINT; Schema: dvbts2; Owner: dvbts2
--

ALTER TABLE ONLY stream_statistics
    ADD CONSTRAINT fk_stream_statistics_channel_id FOREIGN KEY (channel_id) REFERENCES channels(id);


--
-- Name: fk_stream_throughput_channel_id; Type: FK CONSTRAINT; Schema: dvbts2; Owner: dvbts2
--

ALTER TABLE ONLY stream_throughput
    ADD CONSTRAINT fk_stream_throughput_channel_id FOREIGN KEY (channel_id) REFERENCES channels(id);


--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--

