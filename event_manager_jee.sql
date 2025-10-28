--
-- PostgreSQL database dump
--

-- Dumped from database version 17.5
-- Dumped by pg_dump version 17.5

-- Started on 2025-10-27 13:55:26

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

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 217 (class 1259 OID 55182)
-- Name: admin; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.admin (
    permissions character varying(255),
    role character varying(255),
    id bigint NOT NULL
);


ALTER TABLE public.admin OWNER TO postgres;

--
-- TOC entry 219 (class 1259 OID 55190)
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
-- TOC entry 218 (class 1259 OID 55189)
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
-- TOC entry 4922 (class 0 OID 0)
-- Dependencies: 218
-- Name: categorie_categorie_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.categorie_categorie_id_seq OWNED BY public.categorie.categorie_id;


--
-- TOC entry 221 (class 1259 OID 55197)
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
-- TOC entry 220 (class 1259 OID 55196)
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
-- TOC entry 4923 (class 0 OID 0)
-- Dependencies: 220
-- Name: commentaire_commentaire_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.commentaire_commentaire_id_seq OWNED BY public.commentaire.commentaire_id;


--
-- TOC entry 223 (class 1259 OID 55206)
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
-- TOC entry 222 (class 1259 OID 55205)
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
-- TOC entry 4924 (class 0 OID 0)
-- Dependencies: 222
-- Name: evaluation_evaluation_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.evaluation_evaluation_id_seq OWNED BY public.evaluation.evaluation_id;


--
-- TOC entry 225 (class 1259 OID 55216)
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
-- TOC entry 226 (class 1259 OID 55226)
-- Name: evenement_categorie; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.evenement_categorie (
    evenement_id bigint NOT NULL,
    categorie_id bigint NOT NULL
);


ALTER TABLE public.evenement_categorie OWNER TO postgres;

--
-- TOC entry 224 (class 1259 OID 55215)
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
-- TOC entry 4925 (class 0 OID 0)
-- Dependencies: 224
-- Name: evenement_evenement_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.evenement_evenement_id_seq OWNED BY public.evenement.evenement_id;


--
-- TOC entry 228 (class 1259 OID 55232)
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
-- TOC entry 227 (class 1259 OID 55231)
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
-- TOC entry 4926 (class 0 OID 0)
-- Dependencies: 227
-- Name: evenement_report_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.evenement_report_id_seq OWNED BY public.evenement_report.id;


--
-- TOC entry 230 (class 1259 OID 55241)
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
-- TOC entry 229 (class 1259 OID 55240)
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
-- TOC entry 4927 (class 0 OID 0)
-- Dependencies: 229
-- Name: inscription_inscription_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.inscription_inscription_id_seq OWNED BY public.inscription.inscription_id;


--
-- TOC entry 231 (class 1259 OID 55248)
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
-- TOC entry 232 (class 1259 OID 55255)
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
-- TOC entry 234 (class 1259 OID 55263)
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
-- TOC entry 233 (class 1259 OID 55262)
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
-- TOC entry 4928 (class 0 OID 0)
-- Dependencies: 233
-- Name: utilisateur_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.utilisateur_id_seq OWNED BY public.utilisateur.id;


--
-- TOC entry 236 (class 1259 OID 55273)
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
-- TOC entry 235 (class 1259 OID 55272)
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
-- TOC entry 4929 (class 0 OID 0)
-- Dependencies: 235
-- Name: verification_code_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.verification_code_id_seq OWNED BY public.verification_code.id;


--
-- TOC entry 4692 (class 2604 OID 55193)
-- Name: categorie categorie_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.categorie ALTER COLUMN categorie_id SET DEFAULT nextval('public.categorie_categorie_id_seq'::regclass);


--
-- TOC entry 4693 (class 2604 OID 55200)
-- Name: commentaire commentaire_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.commentaire ALTER COLUMN commentaire_id SET DEFAULT nextval('public.commentaire_commentaire_id_seq'::regclass);


--
-- TOC entry 4694 (class 2604 OID 55209)
-- Name: evaluation evaluation_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.evaluation ALTER COLUMN evaluation_id SET DEFAULT nextval('public.evaluation_evaluation_id_seq'::regclass);


--
-- TOC entry 4695 (class 2604 OID 55219)
-- Name: evenement evenement_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.evenement ALTER COLUMN evenement_id SET DEFAULT nextval('public.evenement_evenement_id_seq'::regclass);


--
-- TOC entry 4696 (class 2604 OID 55235)
-- Name: evenement_report id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.evenement_report ALTER COLUMN id SET DEFAULT nextval('public.evenement_report_id_seq'::regclass);


--
-- TOC entry 4697 (class 2604 OID 55244)
-- Name: inscription inscription_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.inscription ALTER COLUMN inscription_id SET DEFAULT nextval('public.inscription_inscription_id_seq'::regclass);


--
-- TOC entry 4698 (class 2604 OID 55266)
-- Name: utilisateur id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.utilisateur ALTER COLUMN id SET DEFAULT nextval('public.utilisateur_id_seq'::regclass);


--
-- TOC entry 4699 (class 2604 OID 55276)
-- Name: verification_code id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.verification_code ALTER COLUMN id SET DEFAULT nextval('public.verification_code_id_seq'::regclass);


--
-- TOC entry 4897 (class 0 OID 55182)
-- Dependencies: 217
-- Data for Name: admin; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.admin (permissions, role, id) FROM stdin;
\.


--
-- TOC entry 4899 (class 0 OID 55190)
-- Dependencies: 219
-- Data for Name: categorie; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.categorie (categorie_id, created_at, nom, updated_at) FROM stdin;
\.


--
-- TOC entry 4901 (class 0 OID 55197)
-- Dependencies: 221
-- Data for Name: commentaire; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.commentaire (commentaire_id, created_at, horodatage, texte, updated_at, evenement_id, participant_id) FROM stdin;
\.


--
-- TOC entry 4903 (class 0 OID 55206)
-- Dependencies: 223
-- Data for Name: evaluation; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.evaluation (evaluation_id, created_at, horodatage, note, texte, updated_at, evenement_id, participant_id) FROM stdin;
\.


--
-- TOC entry 4905 (class 0 OID 55216)
-- Dependencies: 225
-- Data for Name: evenement; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.evenement (evenement_id, capacite, created_at, date_debut, date_fin, description, image_url, latitude, lieu, longitude, statut, titre, updated_at, organisateur_id) FROM stdin;
\.


--
-- TOC entry 4906 (class 0 OID 55226)
-- Dependencies: 226
-- Data for Name: evenement_categorie; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.evenement_categorie (evenement_id, categorie_id) FROM stdin;
\.


--
-- TOC entry 4908 (class 0 OID 55232)
-- Dependencies: 228
-- Data for Name: evenement_report; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.evenement_report (id, created_at, description, reason, evenement_id, participant_id) FROM stdin;
\.


--
-- TOC entry 4910 (class 0 OID 55241)
-- Dependencies: 230
-- Data for Name: inscription; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.inscription (inscription_id, created_at, date_inscription, quantite, statut, type_billet, updated_at, evenement_id, participant_id) FROM stdin;
\.


--
-- TOC entry 4911 (class 0 OID 55248)
-- Dependencies: 231
-- Data for Name: organisateur; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.organisateur (description, entreprise, siret, site_web, utilisateur_id) FROM stdin;
\N	\N	\N	\N	2
\.


--
-- TOC entry 4912 (class 0 OID 55255)
-- Dependencies: 232
-- Data for Name: participant; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.participant (date_naissance, preferences, telephone, utilisateur_id) FROM stdin;
\N	\N	\N	1
\.


--
-- TOC entry 4914 (class 0 OID 55263)
-- Dependencies: 234
-- Data for Name: utilisateur; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.utilisateur (id, created_at, email, is_active, is_suspended, is_verified, mot_de_passe_hash, nom, suspension_reason, updated_at, user_type) FROM stdin;
1	2025-10-27 13:42:42.493848	fatimazahraederaoui04@gmail.com	t	f	t	db15ed8803d828737ef871498e81d5da8bc23196c85a2aea93f3a071e2feeabf	fatimazahrae	\N	2025-10-27 13:42:42.493848	PARTICIPANT
2	2025-10-27 13:45:36.274914	fz748290@gmail.com	f	f	t	723be7af6a7447d7e64f6bb2d1ee849767a3d5dfe9b108c46fd8eff81900bcec	sara	\N	2025-10-27 13:45:36.274914	ORGANISATEUR
\.


--
-- TOC entry 4916 (class 0 OID 55273)
-- Dependencies: 236
-- Data for Name: verification_code; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.verification_code (id, code, createdat, email, expiresat, type, used) FROM stdin;
1	345220	2025-10-27 13:41:30.561556	Fati@test.com	2025-10-27 13:56:30.561556	EMAIL_VERIFICATION	f
2	040660	2025-10-27 13:42:09.317537	fatimazahraederaoui04@gmail.com	2025-10-27 13:57:09.317537	EMAIL_VERIFICATION	t
3	199324	2025-10-27 13:45:09.056878	fz748290@gmail.com	2025-10-27 14:00:09.056878	EMAIL_VERIFICATION	t
\.


--
-- TOC entry 4930 (class 0 OID 0)
-- Dependencies: 218
-- Name: categorie_categorie_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.categorie_categorie_id_seq', 1, false);


--
-- TOC entry 4931 (class 0 OID 0)
-- Dependencies: 220
-- Name: commentaire_commentaire_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.commentaire_commentaire_id_seq', 1, false);


--
-- TOC entry 4932 (class 0 OID 0)
-- Dependencies: 222
-- Name: evaluation_evaluation_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.evaluation_evaluation_id_seq', 1, false);


--
-- TOC entry 4933 (class 0 OID 0)
-- Dependencies: 224
-- Name: evenement_evenement_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.evenement_evenement_id_seq', 1, false);


--
-- TOC entry 4934 (class 0 OID 0)
-- Dependencies: 227
-- Name: evenement_report_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.evenement_report_id_seq', 1, false);


--
-- TOC entry 4935 (class 0 OID 0)
-- Dependencies: 229
-- Name: inscription_inscription_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.inscription_inscription_id_seq', 1, false);


--
-- TOC entry 4936 (class 0 OID 0)
-- Dependencies: 233
-- Name: utilisateur_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.utilisateur_id_seq', 2, true);


--
-- TOC entry 4937 (class 0 OID 0)
-- Dependencies: 235
-- Name: verification_code_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.verification_code_id_seq', 3, true);


--
-- TOC entry 4707 (class 2606 OID 55188)
-- Name: admin admin_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.admin
    ADD CONSTRAINT admin_pkey PRIMARY KEY (id);


--
-- TOC entry 4709 (class 2606 OID 55195)
-- Name: categorie categorie_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.categorie
    ADD CONSTRAINT categorie_pkey PRIMARY KEY (categorie_id);


--
-- TOC entry 4713 (class 2606 OID 55204)
-- Name: commentaire commentaire_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.commentaire
    ADD CONSTRAINT commentaire_pkey PRIMARY KEY (commentaire_id);


--
-- TOC entry 4715 (class 2606 OID 55214)
-- Name: evaluation evaluation_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.evaluation
    ADD CONSTRAINT evaluation_pkey PRIMARY KEY (evaluation_id);


--
-- TOC entry 4721 (class 2606 OID 55230)
-- Name: evenement_categorie evenement_categorie_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.evenement_categorie
    ADD CONSTRAINT evenement_categorie_pkey PRIMARY KEY (evenement_id, categorie_id);


--
-- TOC entry 4719 (class 2606 OID 55225)
-- Name: evenement evenement_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.evenement
    ADD CONSTRAINT evenement_pkey PRIMARY KEY (evenement_id);


--
-- TOC entry 4723 (class 2606 OID 55239)
-- Name: evenement_report evenement_report_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.evenement_report
    ADD CONSTRAINT evenement_report_pkey PRIMARY KEY (id);


--
-- TOC entry 4725 (class 2606 OID 55247)
-- Name: inscription inscription_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.inscription
    ADD CONSTRAINT inscription_pkey PRIMARY KEY (inscription_id);


--
-- TOC entry 4729 (class 2606 OID 55254)
-- Name: organisateur organisateur_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.organisateur
    ADD CONSTRAINT organisateur_pkey PRIMARY KEY (utilisateur_id);


--
-- TOC entry 4731 (class 2606 OID 55261)
-- Name: participant participant_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.participant
    ADD CONSTRAINT participant_pkey PRIMARY KEY (utilisateur_id);


--
-- TOC entry 4727 (class 2606 OID 55287)
-- Name: inscription uk158pfrsr36iabvn6uvmjjvb2n; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.inscription
    ADD CONSTRAINT uk158pfrsr36iabvn6uvmjjvb2n UNIQUE (participant_id, evenement_id);


--
-- TOC entry 4711 (class 2606 OID 55283)
-- Name: categorie uk_89y3d23ia9ruhfhdmya9aspq7; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.categorie
    ADD CONSTRAINT uk_89y3d23ia9ruhfhdmya9aspq7 UNIQUE (nom);


--
-- TOC entry 4717 (class 2606 OID 55285)
-- Name: evaluation uk_evaluation_participant_evenement; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.evaluation
    ADD CONSTRAINT uk_evaluation_participant_evenement UNIQUE (participant_id, evenement_id);


--
-- TOC entry 4733 (class 2606 OID 55289)
-- Name: utilisateur uk_rma38wvnqfaf66vvmi57c71lo; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.utilisateur
    ADD CONSTRAINT uk_rma38wvnqfaf66vvmi57c71lo UNIQUE (email);


--
-- TOC entry 4735 (class 2606 OID 55271)
-- Name: utilisateur utilisateur_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.utilisateur
    ADD CONSTRAINT utilisateur_pkey PRIMARY KEY (id);


--
-- TOC entry 4737 (class 2606 OID 55281)
-- Name: verification_code verification_code_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.verification_code
    ADD CONSTRAINT verification_code_pkey PRIMARY KEY (id);


--
-- TOC entry 4748 (class 2606 OID 55340)
-- Name: inscription fk1a3dd8pqia7dftu5br3abkgcw; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.inscription
    ADD CONSTRAINT fk1a3dd8pqia7dftu5br3abkgcw FOREIGN KEY (evenement_id) REFERENCES public.evenement(evenement_id);


--
-- TOC entry 4741 (class 2606 OID 55305)
-- Name: evaluation fk3qkgc7bv68f92ws78gi8evajv; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.evaluation
    ADD CONSTRAINT fk3qkgc7bv68f92ws78gi8evajv FOREIGN KEY (evenement_id) REFERENCES public.evenement(evenement_id);


--
-- TOC entry 4746 (class 2606 OID 55330)
-- Name: evenement_report fkcuvvinlwq2v4butqx3eqr1enn; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.evenement_report
    ADD CONSTRAINT fkcuvvinlwq2v4butqx3eqr1enn FOREIGN KEY (evenement_id) REFERENCES public.evenement(evenement_id);


--
-- TOC entry 4742 (class 2606 OID 55310)
-- Name: evaluation fkdv13trgw2f6a104q6xfa4e34s; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.evaluation
    ADD CONSTRAINT fkdv13trgw2f6a104q6xfa4e34s FOREIGN KEY (participant_id) REFERENCES public.participant(utilisateur_id);


--
-- TOC entry 4744 (class 2606 OID 55320)
-- Name: evenement_categorie fkeyjjgvcipuh58m3ly1f3o28co; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.evenement_categorie
    ADD CONSTRAINT fkeyjjgvcipuh58m3ly1f3o28co FOREIGN KEY (categorie_id) REFERENCES public.categorie(categorie_id);


--
-- TOC entry 4738 (class 2606 OID 55290)
-- Name: admin fkgodqjbbtwk30kf3s0xuxklkr3; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.admin
    ADD CONSTRAINT fkgodqjbbtwk30kf3s0xuxklkr3 FOREIGN KEY (id) REFERENCES public.utilisateur(id);


--
-- TOC entry 4749 (class 2606 OID 55345)
-- Name: inscription fkh2ujwv8wxusc41gcvj4bsjtk7; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.inscription
    ADD CONSTRAINT fkh2ujwv8wxusc41gcvj4bsjtk7 FOREIGN KEY (participant_id) REFERENCES public.participant(utilisateur_id);


--
-- TOC entry 4739 (class 2606 OID 55295)
-- Name: commentaire fkhmwurd0yd52bn0r3hlwblpswj; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.commentaire
    ADD CONSTRAINT fkhmwurd0yd52bn0r3hlwblpswj FOREIGN KEY (evenement_id) REFERENCES public.evenement(evenement_id);


--
-- TOC entry 4750 (class 2606 OID 55350)
-- Name: organisateur fkij8a8flbicjg4rp9adesg8c49; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.organisateur
    ADD CONSTRAINT fkij8a8flbicjg4rp9adesg8c49 FOREIGN KEY (utilisateur_id) REFERENCES public.utilisateur(id);


--
-- TOC entry 4740 (class 2606 OID 55300)
-- Name: commentaire fkm48dvx4wobf96ldqa7y2hsxvg; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.commentaire
    ADD CONSTRAINT fkm48dvx4wobf96ldqa7y2hsxvg FOREIGN KEY (participant_id) REFERENCES public.participant(utilisateur_id);


--
-- TOC entry 4751 (class 2606 OID 55355)
-- Name: participant fkm6yf1yihufyojmfo0ufwrip1q; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.participant
    ADD CONSTRAINT fkm6yf1yihufyojmfo0ufwrip1q FOREIGN KEY (utilisateur_id) REFERENCES public.utilisateur(id);


--
-- TOC entry 4747 (class 2606 OID 55335)
-- Name: evenement_report fkmd2umax2a2mofc1386q37tme1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.evenement_report
    ADD CONSTRAINT fkmd2umax2a2mofc1386q37tme1 FOREIGN KEY (participant_id) REFERENCES public.participant(utilisateur_id);


--
-- TOC entry 4743 (class 2606 OID 55315)
-- Name: evenement fknpguuiqsowqb7w9l632y74k6k; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.evenement
    ADD CONSTRAINT fknpguuiqsowqb7w9l632y74k6k FOREIGN KEY (organisateur_id) REFERENCES public.organisateur(utilisateur_id);


--
-- TOC entry 4745 (class 2606 OID 55325)
-- Name: evenement_categorie fkpoe5bw7o8ywie5gigjdwunb4f; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.evenement_categorie
    ADD CONSTRAINT fkpoe5bw7o8ywie5gigjdwunb4f FOREIGN KEY (evenement_id) REFERENCES public.evenement(evenement_id);


--
-- Test Data: Événements
--

-- Insert test events (organisateur_id = 2 is the organizer "sara")
INSERT INTO public.evenement (evenement_id, titre, description, lieu, date_debut, date_fin, capacite, statut, latitude, longitude, image_url, organisateur_id, created_at, updated_at) VALUES
(1, 'Conférence JEE 2025', 'Une conférence complète sur les technologies JEE modernes et les meilleures pratiques de développement.', 'Paris, France', '2025-11-15 09:00:00', '2025-11-15 17:00:00', 150, 'PUBLIE', 48.8566, 2.3522, 'https://via.placeholder.com/300x200?text=JEE+Conference', 2, NOW(), NOW()),
(2, 'Atelier Spring Boot', 'Atelier pratique sur Spring Boot avec des exercices en direct et Q&A avec les experts.', 'Lyon, France', '2025-11-20 10:00:00', '2025-11-20 16:00:00', 50, 'PUBLIE', 45.7640, 4.8357, 'https://via.placeholder.com/300x200?text=Spring+Boot+Workshop', 2, NOW(), NOW()),
(3, 'Hackathon JEE', 'Compétition de 24 heures pour développer des applications JEE innovantes. Équipes de 3-5 personnes.', 'Toulouse, France', '2025-12-01 08:00:00', '2025-12-02 08:00:00', 200, 'PUBLIE', 43.6047, 1.4442, 'https://via.placeholder.com/300x200?text=JEE+Hackathon', 2, NOW(), NOW()),
(4, 'Webinaire: Microservices avec JEE', 'Découvrez comment construire des architectures microservices scalables avec JEE et Kubernetes.', 'En ligne', '2025-11-22 14:00:00', '2025-11-22 15:30:00', 500, 'PUBLIE', NULL, NULL, 'https://via.placeholder.com/300x200?text=Microservices+Webinar', 2, NOW(), NOW()),
(5, 'Formation Avancée JPA', 'Formation intensive de 3 jours sur JPA, Hibernate et les patterns de persistance avancés.', 'Bordeaux, France', '2025-12-10 09:00:00', '2025-12-12 17:00:00', 30, 'PUBLIE', 44.8378, -0.5792, 'https://via.placeholder.com/300x200?text=JPA+Training', 2, NOW(), NOW()),
(6, 'Meetup Développeurs JEE', 'Rencontre informelle entre développeurs JEE pour partager expériences et networking.', 'Marseille, France', '2025-11-25 18:00:00', '2025-11-25 20:00:00', 80, 'PUBLIE', 43.2965, 5.3698, 'https://via.placeholder.com/300x200?text=JEE+Meetup', 2, NOW(), NOW()),
(7, 'Séminaire Sécurité JEE', 'Sécurité des applications JEE: authentification, autorisation, chiffrement et protection contre les attaques.', 'Nice, France', '2025-12-05 09:00:00', '2025-12-05 17:00:00', 100, 'BROUILLON', 43.7102, 7.2620, 'https://via.placeholder.com/300x200?text=Security+Seminar', 2, NOW(), NOW()),
(8, 'Conférence Cloud Native', 'Déploiement d''applications JEE sur le cloud avec Docker, Kubernetes et les services cloud.', 'Nantes, France', '2025-12-15 10:00:00', '2025-12-15 16:00:00', 120, 'BROUILLON', 47.2184, -1.5536, 'https://via.placeholder.com/300x200?text=Cloud+Native', 2, NOW(), NOW());

--
-- Update sequences for evenement
--
SELECT pg_catalog.setval('public.evenement_evenement_id_seq', 8, true);

--
-- Update event images with working URLs
--
UPDATE public.evenement SET image_url = 'https://images.unsplash.com/photo-1552664730-d307ca884978?w=400&h=300&fit=crop' WHERE evenement_id = 1;
UPDATE public.evenement SET image_url = 'https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=400&h=300&fit=crop' WHERE evenement_id = 2;
UPDATE public.evenement SET image_url = 'https://images.unsplash.com/photo-1552664730-d307ca884978?w=400&h=300&fit=crop' WHERE evenement_id = 3;
UPDATE public.evenement SET image_url = 'https://images.unsplash.com/photo-1552664730-d307ca884978?w=400&h=300&fit=crop' WHERE evenement_id = 4;
UPDATE public.evenement SET image_url = 'https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=400&h=300&fit=crop' WHERE evenement_id = 5;
UPDATE public.evenement SET image_url = 'https://images.unsplash.com/photo-1552664730-d307ca884978?w=400&h=300&fit=crop' WHERE evenement_id = 6;
UPDATE public.evenement SET image_url = 'https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=400&h=300&fit=crop' WHERE evenement_id = 7;
UPDATE public.evenement SET image_url = 'https://images.unsplash.com/photo-1552664730-d307ca884978?w=400&h=300&fit=crop' WHERE evenement_id = 8;

-- Completed on 2025-10-27 13:55:27

--
-- PostgreSQL database dump complete
--

