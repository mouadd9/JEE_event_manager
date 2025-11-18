--
-- PostgreSQL database dump
--

\restrict M79fLNVCcFzfVXUeg8xq7FhHEXjbnCfB3l6IPLC1qFGr7L16KKwpGsdgrAtlO1c

-- Dumped from database version 13.22
-- Dumped by pg_dump version 17.6

-- Started on 2025-11-17 11:33:00

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 4 (class 2615 OID 2200)
-- Name: public; Type: SCHEMA; Schema: -; Owner: postgres
--

-- *not* creating schema, since initdb creates it


ALTER SCHEMA public OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 200 (class 1259 OID 24590)
-- Name: admin; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.admin (
    permissions character varying(255),
    role character varying(255),
    id bigint NOT NULL
);


ALTER TABLE public.admin OWNER TO postgres;

--
-- TOC entry 202 (class 1259 OID 24600)
-- Name: billet; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.billet (
    billet_id bigint NOT NULL,
    chemin_fichier character varying(500),
    created_at timestamp(6) without time zone NOT NULL,
    date_generation timestamp(6) without time zone NOT NULL,
    date_utilisation timestamp(6) without time zone,
    numero_billet character varying(50) NOT NULL,
    statut character varying(255) NOT NULL,
    type_billet character varying(20) NOT NULL,
    updated_at timestamp(6) without time zone,
    utilise boolean NOT NULL,
    inscription_id bigint NOT NULL,
    CONSTRAINT billet_statut_check CHECK (((statut)::text = ANY ((ARRAY['VALIDE'::character varying, 'UTILISE'::character varying, 'ANNULE'::character varying, 'EXPIRE'::character varying])::text[])))
);


ALTER TABLE public.billet OWNER TO postgres;

--
-- TOC entry 201 (class 1259 OID 24598)
-- Name: billet_billet_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.billet_billet_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.billet_billet_id_seq OWNER TO postgres;

--
-- TOC entry 3151 (class 0 OID 0)
-- Dependencies: 201
-- Name: billet_billet_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.billet_billet_id_seq OWNED BY public.billet.billet_id;


--
-- TOC entry 204 (class 1259 OID 24612)
-- Name: categorie; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.categorie (
    categorie_id bigint NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    nom character varying(255) NOT NULL,
    updated_at timestamp(6) without time zone
);


ALTER TABLE public.categorie OWNER TO postgres;

--
-- TOC entry 203 (class 1259 OID 24610)
-- Name: categorie_categorie_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.categorie_categorie_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.categorie_categorie_id_seq OWNER TO postgres;

--
-- TOC entry 3152 (class 0 OID 0)
-- Dependencies: 203
-- Name: categorie_categorie_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.categorie_categorie_id_seq OWNED BY public.categorie.categorie_id;


--
-- TOC entry 206 (class 1259 OID 24620)
-- Name: commentaire; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.commentaire (
    commentaire_id bigint NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    horodatage timestamp(6) without time zone NOT NULL,
    texte text NOT NULL,
    updated_at timestamp(6) without time zone,
    evenement_id bigint NOT NULL,
    participant_id bigint NOT NULL
);


ALTER TABLE public.commentaire OWNER TO postgres;

--
-- TOC entry 205 (class 1259 OID 24618)
-- Name: commentaire_commentaire_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.commentaire_commentaire_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.commentaire_commentaire_id_seq OWNER TO postgres;

--
-- TOC entry 3153 (class 0 OID 0)
-- Dependencies: 205
-- Name: commentaire_commentaire_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.commentaire_commentaire_id_seq OWNED BY public.commentaire.commentaire_id;


--
-- TOC entry 208 (class 1259 OID 24631)
-- Name: evaluation; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.evaluation (
    evaluation_id bigint NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    horodatage timestamp(6) without time zone NOT NULL,
    note integer NOT NULL,
    texte text,
    updated_at timestamp(6) without time zone,
    evenement_id bigint NOT NULL,
    participant_id bigint NOT NULL,
    CONSTRAINT evaluation_note_check CHECK (((note <= 5) AND (note >= 0)))
);


ALTER TABLE public.evaluation OWNER TO postgres;

--
-- TOC entry 207 (class 1259 OID 24629)
-- Name: evaluation_evaluation_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.evaluation_evaluation_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.evaluation_evaluation_id_seq OWNER TO postgres;

--
-- TOC entry 3154 (class 0 OID 0)
-- Dependencies: 207
-- Name: evaluation_evaluation_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.evaluation_evaluation_id_seq OWNED BY public.evaluation.evaluation_id;


--
-- TOC entry 210 (class 1259 OID 24643)
-- Name: evenement; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.evenement (
    evenement_id bigint NOT NULL,
    capacite integer,
    created_at timestamp(6) without time zone NOT NULL,
    date_debut timestamp(6) without time zone NOT NULL,
    date_fin timestamp(6) without time zone NOT NULL,
    description character varying(1000),
    image_url character varying(500),
    latitude double precision,
    lieu character varying(255) NOT NULL,
    longitude double precision,
    statut character varying(255) NOT NULL,
    titre character varying(100) NOT NULL,
    updated_at timestamp(6) without time zone,
    organisateur_id bigint NOT NULL,
    CONSTRAINT evenement_capacite_check CHECK ((capacite >= 1)),
    CONSTRAINT evenement_statut_check CHECK (((statut)::text = ANY ((ARRAY['BROUILLON'::character varying, 'PUBLIE'::character varying, 'ANNULE'::character varying, 'CACHE'::character varying])::text[])))
);


ALTER TABLE public.evenement OWNER TO postgres;

--
-- TOC entry 211 (class 1259 OID 24654)
-- Name: evenement_categorie; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.evenement_categorie (
    evenement_id bigint NOT NULL,
    categorie_id bigint NOT NULL
);


ALTER TABLE public.evenement_categorie OWNER TO postgres;

--
-- TOC entry 209 (class 1259 OID 24641)
-- Name: evenement_evenement_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.evenement_evenement_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.evenement_evenement_id_seq OWNER TO postgres;

--
-- TOC entry 3155 (class 0 OID 0)
-- Dependencies: 209
-- Name: evenement_evenement_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.evenement_evenement_id_seq OWNED BY public.evenement.evenement_id;


--
-- TOC entry 213 (class 1259 OID 24661)
-- Name: evenement_report; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.evenement_report (
    id bigint NOT NULL,
    created_at timestamp(6) without time zone,
    description text,
    reason character varying(255) NOT NULL,
    evenement_id bigint NOT NULL,
    participant_id bigint NOT NULL
);


ALTER TABLE public.evenement_report OWNER TO postgres;

--
-- TOC entry 212 (class 1259 OID 24659)
-- Name: evenement_report_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.evenement_report_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.evenement_report_id_seq OWNER TO postgres;

--
-- TOC entry 3156 (class 0 OID 0)
-- Dependencies: 212
-- Name: evenement_report_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.evenement_report_id_seq OWNED BY public.evenement_report.id;


--
-- TOC entry 215 (class 1259 OID 24672)
-- Name: inscription; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.inscription (
    inscription_id bigint NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    date_inscription timestamp(6) without time zone NOT NULL,
    quantite integer,
    statut character varying(255) NOT NULL,
    type_billet character varying(50),
    updated_at timestamp(6) without time zone,
    evenement_id bigint NOT NULL,
    participant_id bigint NOT NULL,
    CONSTRAINT inscription_statut_check CHECK (((statut)::text = ANY ((ARRAY['EN_ATTENTE'::character varying, 'ACCEPTEE'::character varying, 'REFUSEE'::character varying, 'ANNULEE'::character varying])::text[])))
);


ALTER TABLE public.inscription OWNER TO postgres;

--
-- TOC entry 214 (class 1259 OID 24670)
-- Name: inscription_inscription_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.inscription_inscription_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.inscription_inscription_id_seq OWNER TO postgres;

--
-- TOC entry 3157 (class 0 OID 0)
-- Dependencies: 214
-- Name: inscription_inscription_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.inscription_inscription_id_seq OWNED BY public.inscription.inscription_id;


--
-- TOC entry 216 (class 1259 OID 24679)
-- Name: organisateur; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.organisateur (
    description character varying(255),
    entreprise character varying(255),
    siret character varying(255),
    site_web character varying(255),
    utilisateur_id bigint NOT NULL
);


ALTER TABLE public.organisateur OWNER TO postgres;

--
-- TOC entry 217 (class 1259 OID 24687)
-- Name: participant; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.participant (
    date_naissance date,
    preferences character varying(255),
    telephone character varying(255),
    utilisateur_id bigint NOT NULL
);


ALTER TABLE public.participant OWNER TO postgres;

--
-- TOC entry 219 (class 1259 OID 24697)
-- Name: utilisateur; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.utilisateur (
    id bigint NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    email character varying(255) NOT NULL,
    is_active boolean,
    is_suspended boolean,
    is_verified boolean,
    mot_de_passe_hash character varying(255) NOT NULL,
    nom character varying(255) NOT NULL,
    suspension_reason character varying(255),
    updated_at timestamp(6) without time zone,
    user_type character varying(255) NOT NULL,
    CONSTRAINT utilisateur_user_type_check CHECK (((user_type)::text = ANY ((ARRAY['ORGANISATEUR'::character varying, 'PARTICIPANT'::character varying, 'ADMIN'::character varying])::text[])))
);


ALTER TABLE public.utilisateur OWNER TO postgres;

--
-- TOC entry 218 (class 1259 OID 24695)
-- Name: utilisateur_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.utilisateur_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.utilisateur_id_seq OWNER TO postgres;

--
-- TOC entry 3158 (class 0 OID 0)
-- Dependencies: 218
-- Name: utilisateur_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.utilisateur_id_seq OWNED BY public.utilisateur.id;


--
-- TOC entry 221 (class 1259 OID 24709)
-- Name: verification_code; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.verification_code (
    id bigint NOT NULL,
    code character varying(6) NOT NULL,
    createdat timestamp(6) without time zone NOT NULL,
    email character varying(255) NOT NULL,
    expiresat timestamp(6) without time zone NOT NULL,
    type character varying(255) NOT NULL,
    used boolean NOT NULL,
    CONSTRAINT verification_code_type_check CHECK (((type)::text = ANY ((ARRAY['EMAIL_VERIFICATION'::character varying, 'PASSWORD_RESET'::character varying])::text[])))
);


ALTER TABLE public.verification_code OWNER TO postgres;

--
-- TOC entry 220 (class 1259 OID 24707)
-- Name: verification_code_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.verification_code_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.verification_code_id_seq OWNER TO postgres;

--
-- TOC entry 3159 (class 0 OID 0)
-- Dependencies: 220
-- Name: verification_code_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.verification_code_id_seq OWNED BY public.verification_code.id;


--
-- TOC entry 2924 (class 2604 OID 24603)
-- Name: billet billet_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.billet ALTER COLUMN billet_id SET DEFAULT nextval('public.billet_billet_id_seq'::regclass);


--
-- TOC entry 2925 (class 2604 OID 24615)
-- Name: categorie categorie_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.categorie ALTER COLUMN categorie_id SET DEFAULT nextval('public.categorie_categorie_id_seq'::regclass);


--
-- TOC entry 2926 (class 2604 OID 24623)
-- Name: commentaire commentaire_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.commentaire ALTER COLUMN commentaire_id SET DEFAULT nextval('public.commentaire_commentaire_id_seq'::regclass);


--
-- TOC entry 2927 (class 2604 OID 24634)
-- Name: evaluation evaluation_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.evaluation ALTER COLUMN evaluation_id SET DEFAULT nextval('public.evaluation_evaluation_id_seq'::regclass);


--
-- TOC entry 2928 (class 2604 OID 24646)
-- Name: evenement evenement_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.evenement ALTER COLUMN evenement_id SET DEFAULT nextval('public.evenement_evenement_id_seq'::regclass);


--
-- TOC entry 2929 (class 2604 OID 24664)
-- Name: evenement_report id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.evenement_report ALTER COLUMN id SET DEFAULT nextval('public.evenement_report_id_seq'::regclass);


--
-- TOC entry 2930 (class 2604 OID 24675)
-- Name: inscription inscription_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.inscription ALTER COLUMN inscription_id SET DEFAULT nextval('public.inscription_inscription_id_seq'::regclass);


--
-- TOC entry 2931 (class 2604 OID 24700)
-- Name: utilisateur id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.utilisateur ALTER COLUMN id SET DEFAULT nextval('public.utilisateur_id_seq'::regclass);


--
-- TOC entry 2932 (class 2604 OID 24712)
-- Name: verification_code id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.verification_code ALTER COLUMN id SET DEFAULT nextval('public.verification_code_id_seq'::regclass);


--
-- TOC entry 3123 (class 0 OID 24590)
-- Dependencies: 200
-- Data for Name: admin; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.admin (permissions, role, id) FROM stdin;
\.


--
-- TOC entry 3125 (class 0 OID 24600)
-- Dependencies: 202
-- Data for Name: billet; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.billet (billet_id, chemin_fichier, created_at, date_generation, date_utilisation, numero_billet, statut, type_billet, updated_at, utilise, inscription_id) FROM stdin;
\.


--
-- TOC entry 3127 (class 0 OID 24612)
-- Dependencies: 204
-- Data for Name: categorie; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.categorie (categorie_id, created_at, nom, updated_at) FROM stdin;
\.


--
-- TOC entry 3129 (class 0 OID 24620)
-- Dependencies: 206
-- Data for Name: commentaire; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.commentaire (commentaire_id, created_at, horodatage, texte, updated_at, evenement_id, participant_id) FROM stdin;
\.


--
-- TOC entry 3131 (class 0 OID 24631)
-- Dependencies: 208
-- Data for Name: evaluation; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.evaluation (evaluation_id, created_at, horodatage, note, texte, updated_at, evenement_id, participant_id) FROM stdin;
\.


--
-- TOC entry 3133 (class 0 OID 24643)
-- Dependencies: 210
-- Data for Name: evenement; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.evenement (evenement_id, capacite, created_at, date_debut, date_fin, description, image_url, latitude, lieu, longitude, statut, titre, updated_at, organisateur_id) FROM stdin;
\.


--
-- TOC entry 3134 (class 0 OID 24654)
-- Dependencies: 211
-- Data for Name: evenement_categorie; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.evenement_categorie (evenement_id, categorie_id) FROM stdin;
\.


--
-- TOC entry 3136 (class 0 OID 24661)
-- Dependencies: 213
-- Data for Name: evenement_report; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.evenement_report (id, created_at, description, reason, evenement_id, participant_id) FROM stdin;
\.


--
-- TOC entry 3138 (class 0 OID 24672)
-- Dependencies: 215
-- Data for Name: inscription; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.inscription (inscription_id, created_at, date_inscription, quantite, statut, type_billet, updated_at, evenement_id, participant_id) FROM stdin;
\.


--
-- TOC entry 3139 (class 0 OID 24679)
-- Dependencies: 216
-- Data for Name: organisateur; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.organisateur (description, entreprise, siret, site_web, utilisateur_id) FROM stdin;
\.


--
-- TOC entry 3140 (class 0 OID 24687)
-- Dependencies: 217
-- Data for Name: participant; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.participant (date_naissance, preferences, telephone, utilisateur_id) FROM stdin;
\.


--
-- TOC entry 3142 (class 0 OID 24697)
-- Dependencies: 219
-- Data for Name: utilisateur; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.utilisateur (id, created_at, email, is_active, is_suspended, is_verified, mot_de_passe_hash, nom, suspension_reason, updated_at, user_type) FROM stdin;
\.


--
-- TOC entry 3144 (class 0 OID 24709)
-- Dependencies: 221
-- Data for Name: verification_code; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.verification_code (id, code, createdat, email, expiresat, type, used) FROM stdin;
\.


--
-- TOC entry 3160 (class 0 OID 0)
-- Dependencies: 201
-- Name: billet_billet_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.billet_billet_id_seq', 1, false);


--
-- TOC entry 3161 (class 0 OID 0)
-- Dependencies: 203
-- Name: categorie_categorie_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.categorie_categorie_id_seq', 1, false);


--
-- TOC entry 3162 (class 0 OID 0)
-- Dependencies: 205
-- Name: commentaire_commentaire_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.commentaire_commentaire_id_seq', 1, false);


--
-- TOC entry 3163 (class 0 OID 0)
-- Dependencies: 207
-- Name: evaluation_evaluation_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.evaluation_evaluation_id_seq', 1, false);


--
-- TOC entry 3164 (class 0 OID 0)
-- Dependencies: 209
-- Name: evenement_evenement_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.evenement_evenement_id_seq', 1, false);


--
-- TOC entry 3165 (class 0 OID 0)
-- Dependencies: 212
-- Name: evenement_report_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.evenement_report_id_seq', 1, false);


--
-- TOC entry 3166 (class 0 OID 0)
-- Dependencies: 214
-- Name: inscription_inscription_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.inscription_inscription_id_seq', 1, false);


--
-- TOC entry 3167 (class 0 OID 0)
-- Dependencies: 218
-- Name: utilisateur_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.utilisateur_id_seq', 1, false);


--
-- TOC entry 3168 (class 0 OID 0)
-- Dependencies: 220
-- Name: verification_code_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.verification_code_id_seq', 1, false);


--
-- TOC entry 2941 (class 2606 OID 24597)
-- Name: admin admin_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.admin
    ADD CONSTRAINT admin_pkey PRIMARY KEY (id);


--
-- TOC entry 2943 (class 2606 OID 24609)
-- Name: billet billet_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.billet
    ADD CONSTRAINT billet_pkey PRIMARY KEY (billet_id);


--
-- TOC entry 2949 (class 2606 OID 24617)
-- Name: categorie categorie_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.categorie
    ADD CONSTRAINT categorie_pkey PRIMARY KEY (categorie_id);


--
-- TOC entry 2953 (class 2606 OID 24628)
-- Name: commentaire commentaire_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.commentaire
    ADD CONSTRAINT commentaire_pkey PRIMARY KEY (commentaire_id);


--
-- TOC entry 2955 (class 2606 OID 24640)
-- Name: evaluation evaluation_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.evaluation
    ADD CONSTRAINT evaluation_pkey PRIMARY KEY (evaluation_id);


--
-- TOC entry 2961 (class 2606 OID 24658)
-- Name: evenement_categorie evenement_categorie_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.evenement_categorie
    ADD CONSTRAINT evenement_categorie_pkey PRIMARY KEY (evenement_id, categorie_id);


--
-- TOC entry 2959 (class 2606 OID 24653)
-- Name: evenement evenement_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.evenement
    ADD CONSTRAINT evenement_pkey PRIMARY KEY (evenement_id);


--
-- TOC entry 2963 (class 2606 OID 24669)
-- Name: evenement_report evenement_report_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.evenement_report
    ADD CONSTRAINT evenement_report_pkey PRIMARY KEY (id);


--
-- TOC entry 2965 (class 2606 OID 24678)
-- Name: inscription inscription_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.inscription
    ADD CONSTRAINT inscription_pkey PRIMARY KEY (inscription_id);


--
-- TOC entry 2969 (class 2606 OID 24686)
-- Name: organisateur organisateur_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.organisateur
    ADD CONSTRAINT organisateur_pkey PRIMARY KEY (utilisateur_id);


--
-- TOC entry 2971 (class 2606 OID 24694)
-- Name: participant participant_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.participant
    ADD CONSTRAINT participant_pkey PRIMARY KEY (utilisateur_id);


--
-- TOC entry 2967 (class 2606 OID 24728)
-- Name: inscription uk158pfrsr36iabvn6uvmjjvb2n; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.inscription
    ADD CONSTRAINT uk158pfrsr36iabvn6uvmjjvb2n UNIQUE (participant_id, evenement_id);


--
-- TOC entry 2945 (class 2606 OID 24720)
-- Name: billet uk_59icl9rfiopkx7h7v2v9llrim; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.billet
    ADD CONSTRAINT uk_59icl9rfiopkx7h7v2v9llrim UNIQUE (numero_billet);


--
-- TOC entry 2951 (class 2606 OID 24724)
-- Name: categorie uk_89y3d23ia9ruhfhdmya9aspq7; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.categorie
    ADD CONSTRAINT uk_89y3d23ia9ruhfhdmya9aspq7 UNIQUE (nom);


--
-- TOC entry 2957 (class 2606 OID 24726)
-- Name: evaluation uk_evaluation_participant_evenement; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.evaluation
    ADD CONSTRAINT uk_evaluation_participant_evenement UNIQUE (participant_id, evenement_id);


--
-- TOC entry 2973 (class 2606 OID 24730)
-- Name: utilisateur uk_rma38wvnqfaf66vvmi57c71lo; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.utilisateur
    ADD CONSTRAINT uk_rma38wvnqfaf66vvmi57c71lo UNIQUE (email);


--
-- TOC entry 2947 (class 2606 OID 24722)
-- Name: billet uk_tswk653mhbpa38o1xq32ppsq; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.billet
    ADD CONSTRAINT uk_tswk653mhbpa38o1xq32ppsq UNIQUE (inscription_id);


--
-- TOC entry 2975 (class 2606 OID 24706)
-- Name: utilisateur utilisateur_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.utilisateur
    ADD CONSTRAINT utilisateur_pkey PRIMARY KEY (id);


--
-- TOC entry 2977 (class 2606 OID 24718)
-- Name: verification_code verification_code_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.verification_code
    ADD CONSTRAINT verification_code_pkey PRIMARY KEY (id);


--
-- TOC entry 2989 (class 2606 OID 24786)
-- Name: inscription fk1a3dd8pqia7dftu5br3abkgcw; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.inscription
    ADD CONSTRAINT fk1a3dd8pqia7dftu5br3abkgcw FOREIGN KEY (evenement_id) REFERENCES public.evenement(evenement_id);


--
-- TOC entry 2982 (class 2606 OID 24751)
-- Name: evaluation fk3qkgc7bv68f92ws78gi8evajv; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.evaluation
    ADD CONSTRAINT fk3qkgc7bv68f92ws78gi8evajv FOREIGN KEY (evenement_id) REFERENCES public.evenement(evenement_id);


--
-- TOC entry 2979 (class 2606 OID 24736)
-- Name: billet fk8w8j2nj798q173ak8ap9e8pdy; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.billet
    ADD CONSTRAINT fk8w8j2nj798q173ak8ap9e8pdy FOREIGN KEY (inscription_id) REFERENCES public.inscription(inscription_id);


--
-- TOC entry 2987 (class 2606 OID 24776)
-- Name: evenement_report fkcuvvinlwq2v4butqx3eqr1enn; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.evenement_report
    ADD CONSTRAINT fkcuvvinlwq2v4butqx3eqr1enn FOREIGN KEY (evenement_id) REFERENCES public.evenement(evenement_id);


--
-- TOC entry 2983 (class 2606 OID 24756)
-- Name: evaluation fkdv13trgw2f6a104q6xfa4e34s; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.evaluation
    ADD CONSTRAINT fkdv13trgw2f6a104q6xfa4e34s FOREIGN KEY (participant_id) REFERENCES public.participant(utilisateur_id);


--
-- TOC entry 2985 (class 2606 OID 24766)
-- Name: evenement_categorie fkeyjjgvcipuh58m3ly1f3o28co; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.evenement_categorie
    ADD CONSTRAINT fkeyjjgvcipuh58m3ly1f3o28co FOREIGN KEY (categorie_id) REFERENCES public.categorie(categorie_id);


--
-- TOC entry 2978 (class 2606 OID 24731)
-- Name: admin fkgodqjbbtwk30kf3s0xuxklkr3; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.admin
    ADD CONSTRAINT fkgodqjbbtwk30kf3s0xuxklkr3 FOREIGN KEY (id) REFERENCES public.utilisateur(id);


--
-- TOC entry 2990 (class 2606 OID 24791)
-- Name: inscription fkh2ujwv8wxusc41gcvj4bsjtk7; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.inscription
    ADD CONSTRAINT fkh2ujwv8wxusc41gcvj4bsjtk7 FOREIGN KEY (participant_id) REFERENCES public.participant(utilisateur_id);


--
-- TOC entry 2980 (class 2606 OID 24741)
-- Name: commentaire fkhmwurd0yd52bn0r3hlwblpswj; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.commentaire
    ADD CONSTRAINT fkhmwurd0yd52bn0r3hlwblpswj FOREIGN KEY (evenement_id) REFERENCES public.evenement(evenement_id);


--
-- TOC entry 2991 (class 2606 OID 24796)
-- Name: organisateur fkij8a8flbicjg4rp9adesg8c49; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.organisateur
    ADD CONSTRAINT fkij8a8flbicjg4rp9adesg8c49 FOREIGN KEY (utilisateur_id) REFERENCES public.utilisateur(id);


--
-- TOC entry 2981 (class 2606 OID 24746)
-- Name: commentaire fkm48dvx4wobf96ldqa7y2hsxvg; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.commentaire
    ADD CONSTRAINT fkm48dvx4wobf96ldqa7y2hsxvg FOREIGN KEY (participant_id) REFERENCES public.participant(utilisateur_id);


--
-- TOC entry 2992 (class 2606 OID 24801)
-- Name: participant fkm6yf1yihufyojmfo0ufwrip1q; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.participant
    ADD CONSTRAINT fkm6yf1yihufyojmfo0ufwrip1q FOREIGN KEY (utilisateur_id) REFERENCES public.utilisateur(id);


--
-- TOC entry 2988 (class 2606 OID 24781)
-- Name: evenement_report fkmd2umax2a2mofc1386q37tme1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.evenement_report
    ADD CONSTRAINT fkmd2umax2a2mofc1386q37tme1 FOREIGN KEY (participant_id) REFERENCES public.participant(utilisateur_id);


--
-- TOC entry 2984 (class 2606 OID 24761)
-- Name: evenement fknpguuiqsowqb7w9l632y74k6k; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.evenement
    ADD CONSTRAINT fknpguuiqsowqb7w9l632y74k6k FOREIGN KEY (organisateur_id) REFERENCES public.organisateur(utilisateur_id);


--
-- TOC entry 2986 (class 2606 OID 24771)
-- Name: evenement_categorie fkpoe5bw7o8ywie5gigjdwunb4f; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.evenement_categorie
    ADD CONSTRAINT fkpoe5bw7o8ywie5gigjdwunb4f FOREIGN KEY (evenement_id) REFERENCES public.evenement(evenement_id);


--
-- TOC entry 3150 (class 0 OID 0)
-- Dependencies: 4
-- Name: SCHEMA public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE USAGE ON SCHEMA public FROM PUBLIC;
GRANT ALL ON SCHEMA public TO PUBLIC;


-- Completed on 2025-11-17 11:33:01

--
-- PostgreSQL database dump complete
--

\unrestrict M79fLNVCcFzfVXUeg8xq7FhHEXjbnCfB3l6IPLC1qFGr7L16KKwpGsdgrAtlO1c

