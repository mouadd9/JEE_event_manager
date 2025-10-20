--
-- PostgreSQL database dump
--

-- Dumped from database version 17.5
-- Dumped by pg_dump version 17.5

-- Started on 2025-10-20 07:00:45

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
-- TOC entry 218 (class 1259 OID 38674)
-- Name: administrateur; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.administrateur (
    utilisateur_id bigint NOT NULL
);


ALTER TABLE public.administrateur OWNER TO postgres;

--
-- TOC entry 222 (class 1259 OID 38705)
-- Name: categorie; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.categorie (
    categorie_id integer NOT NULL,
    nom character varying(100) NOT NULL,
    id integer NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    updated_at timestamp(6) without time zone,
    couleur character varying(255),
    description character varying(255)
);


ALTER TABLE public.categorie OWNER TO postgres;

--
-- TOC entry 221 (class 1259 OID 38704)
-- Name: categorie_categorie_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.categorie_categorie_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.categorie_categorie_id_seq OWNER TO postgres;

--
-- TOC entry 4962 (class 0 OID 0)
-- Dependencies: 221
-- Name: categorie_categorie_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.categorie_categorie_id_seq OWNED BY public.categorie.categorie_id;


--
-- TOC entry 236 (class 1259 OID 46847)
-- Name: categorie_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.categorie_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.categorie_id_seq OWNER TO postgres;

--
-- TOC entry 4963 (class 0 OID 0)
-- Dependencies: 236
-- Name: categorie_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.categorie_id_seq OWNED BY public.categorie.id;


--
-- TOC entry 230 (class 1259 OID 38770)
-- Name: commentaire; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.commentaire (
    commentaire_id integer NOT NULL,
    texte text NOT NULL,
    horodatage timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    participant_id bigint NOT NULL,
    evenement_id integer NOT NULL,
    id integer NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    updated_at timestamp(6) without time zone
);


ALTER TABLE public.commentaire OWNER TO postgres;

--
-- TOC entry 229 (class 1259 OID 38769)
-- Name: commentaire_commentaire_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.commentaire_commentaire_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.commentaire_commentaire_id_seq OWNER TO postgres;

--
-- TOC entry 4964 (class 0 OID 0)
-- Dependencies: 229
-- Name: commentaire_commentaire_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.commentaire_commentaire_id_seq OWNED BY public.commentaire.commentaire_id;


--
-- TOC entry 237 (class 1259 OID 46855)
-- Name: commentaire_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.commentaire_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.commentaire_id_seq OWNER TO postgres;

--
-- TOC entry 4965 (class 0 OID 0)
-- Dependencies: 237
-- Name: commentaire_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.commentaire_id_seq OWNED BY public.commentaire.id;


--
-- TOC entry 232 (class 1259 OID 38790)
-- Name: evaluation; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.evaluation (
    evaluation_id integer NOT NULL,
    note integer NOT NULL,
    texte text,
    horodatage timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    participant_id bigint NOT NULL,
    evenement_id integer NOT NULL,
    id integer NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    updated_at timestamp(6) without time zone,
    CONSTRAINT evaluation_note_check CHECK (((note >= 0) AND (note <= 5)))
);


ALTER TABLE public.evaluation OWNER TO postgres;

--
-- TOC entry 231 (class 1259 OID 38789)
-- Name: evaluation_evaluation_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.evaluation_evaluation_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.evaluation_evaluation_id_seq OWNER TO postgres;

--
-- TOC entry 4966 (class 0 OID 0)
-- Dependencies: 231
-- Name: evaluation_evaluation_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.evaluation_evaluation_id_seq OWNED BY public.evaluation.evaluation_id;


--
-- TOC entry 238 (class 1259 OID 46864)
-- Name: evaluation_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.evaluation_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.evaluation_id_seq OWNER TO postgres;

--
-- TOC entry 4967 (class 0 OID 0)
-- Dependencies: 238
-- Name: evaluation_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.evaluation_id_seq OWNED BY public.evaluation.id;


--
-- TOC entry 225 (class 1259 OID 38718)
-- Name: evenement; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.evenement (
    evenement_id integer NOT NULL,
    titre character varying(255) NOT NULL,
    description text,
    date_debut timestamp without time zone NOT NULL,
    date_fin timestamp without time zone NOT NULL,
    statut character varying(20) DEFAULT 'BROUILLON'::character varying NOT NULL,
    lieu character varying(255),
    organisateur_id bigint NOT NULL,
    capacite integer DEFAULT 100,
    created_at timestamp(6) without time zone NOT NULL,
    updated_at timestamp(6) without time zone,
    organisateur_nom character varying(255) NOT NULL,
    image_url character varying(500)
);


ALTER TABLE public.evenement OWNER TO postgres;

--
-- TOC entry 4968 (class 0 OID 0)
-- Dependencies: 225
-- Name: COLUMN evenement.capacite; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.evenement.capacite IS 'Capacité maximale de l''événement';


--
-- TOC entry 233 (class 1259 OID 38812)
-- Name: evenement_categorie; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.evenement_categorie (
    evenement_id integer NOT NULL,
    categorie_id integer NOT NULL
);


ALTER TABLE public.evenement_categorie OWNER TO postgres;

--
-- TOC entry 224 (class 1259 OID 38717)
-- Name: evenement_evenement_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.evenement_evenement_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.evenement_evenement_id_seq OWNER TO postgres;

--
-- TOC entry 4969 (class 0 OID 0)
-- Dependencies: 224
-- Name: evenement_evenement_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.evenement_evenement_id_seq OWNED BY public.evenement.evenement_id;


--
-- TOC entry 217 (class 1259 OID 38653)
-- Name: flyway_schema_history; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.flyway_schema_history (
    installed_rank integer NOT NULL,
    version character varying(50),
    description character varying(200) NOT NULL,
    type character varying(20) NOT NULL,
    script character varying(1000) NOT NULL,
    checksum integer,
    installed_by character varying(100) NOT NULL,
    installed_on timestamp without time zone DEFAULT now() NOT NULL,
    execution_time integer NOT NULL,
    success boolean NOT NULL
);


ALTER TABLE public.flyway_schema_history OWNER TO postgres;

--
-- TOC entry 228 (class 1259 OID 38744)
-- Name: inscription; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.inscription (
    inscription_id integer NOT NULL,
    participant_id bigint NOT NULL,
    evenement_id integer NOT NULL,
    date_inscription timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    statut character varying(20) DEFAULT 'EN_ATTENTE'::character varying NOT NULL,
    type_billet character varying(50) DEFAULT 'STANDARD'::character varying,
    quantite integer DEFAULT 1,
    created_at timestamp(6) without time zone NOT NULL,
    updated_at timestamp(6) without time zone
);


ALTER TABLE public.inscription OWNER TO postgres;

--
-- TOC entry 4970 (class 0 OID 0)
-- Dependencies: 228
-- Name: COLUMN inscription.type_billet; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.inscription.type_billet IS 'Type de billet: STANDARD, VIP, PREMIUM';


--
-- TOC entry 4971 (class 0 OID 0)
-- Dependencies: 228
-- Name: COLUMN inscription.quantite; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.inscription.quantite IS 'Nombre de places réservées (1-10)';


--
-- TOC entry 227 (class 1259 OID 38743)
-- Name: inscription_inscription_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.inscription_inscription_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.inscription_inscription_id_seq OWNER TO postgres;

--
-- TOC entry 4972 (class 0 OID 0)
-- Dependencies: 227
-- Name: inscription_inscription_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.inscription_inscription_id_seq OWNED BY public.inscription.inscription_id;


--
-- TOC entry 219 (class 1259 OID 38684)
-- Name: organisateur; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.organisateur (
    utilisateur_id bigint NOT NULL,
    description character varying(255),
    entreprise character varying(255),
    siret character varying(255),
    site_web character varying(255),
    id bigint NOT NULL
);


ALTER TABLE public.organisateur OWNER TO postgres;

--
-- TOC entry 220 (class 1259 OID 38694)
-- Name: participant; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.participant (
    utilisateur_id bigint NOT NULL,
    date_naissance date,
    preferences character varying(255),
    telephone character varying(255),
    id bigint NOT NULL
);


ALTER TABLE public.participant OWNER TO postgres;

--
-- TOC entry 223 (class 1259 OID 38711)
-- Name: statut_evenement; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.statut_evenement (
    statut character varying(20) NOT NULL,
    CONSTRAINT statut_evenement_statut_check CHECK (((statut)::text = ANY ((ARRAY['BROUILLON'::character varying, 'PUBLIE'::character varying, 'ANNULE'::character varying])::text[])))
);


ALTER TABLE public.statut_evenement OWNER TO postgres;

--
-- TOC entry 226 (class 1259 OID 38737)
-- Name: statut_inscription; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.statut_inscription (
    statut character varying(20) NOT NULL,
    CONSTRAINT statut_inscription_statut_check CHECK (((statut)::text = ANY ((ARRAY['EN_ATTENTE'::character varying, 'ACCEPTEE'::character varying, 'REFUSEE'::character varying, 'ANNULEE'::character varying])::text[])))
);


ALTER TABLE public.statut_inscription OWNER TO postgres;

--
-- TOC entry 234 (class 1259 OID 38840)
-- Name: utilisateur; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.utilisateur (
    nom character varying(255) NOT NULL,
    email character varying(255) NOT NULL,
    mot_de_passe_hash character varying(255) NOT NULL,
    user_type character varying(50) NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    id bigint NOT NULL,
    CONSTRAINT utilisateur_user_type_check CHECK (((user_type)::text = ANY ((ARRAY['PARTICIPANT'::character varying, 'ORGANISATEUR'::character varying])::text[])))
);


ALTER TABLE public.utilisateur OWNER TO postgres;

--
-- TOC entry 4973 (class 0 OID 0)
-- Dependencies: 234
-- Name: TABLE utilisateur; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE public.utilisateur IS 'Table des utilisateurs du système';


--
-- TOC entry 4974 (class 0 OID 0)
-- Dependencies: 234
-- Name: COLUMN utilisateur.nom; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.utilisateur.nom IS 'Nom complet de l''utilisateur';


--
-- TOC entry 4975 (class 0 OID 0)
-- Dependencies: 234
-- Name: COLUMN utilisateur.email; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.utilisateur.email IS 'Adresse email de l''utilisateur (unique)';


--
-- TOC entry 4976 (class 0 OID 0)
-- Dependencies: 234
-- Name: COLUMN utilisateur.mot_de_passe_hash; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.utilisateur.mot_de_passe_hash IS 'Mot de passe hashé de l''utilisateur';


--
-- TOC entry 4977 (class 0 OID 0)
-- Dependencies: 234
-- Name: COLUMN utilisateur.user_type; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.utilisateur.user_type IS 'Type d''utilisateur (PARTICIPANT ou ORGANISATEUR)';


--
-- TOC entry 4978 (class 0 OID 0)
-- Dependencies: 234
-- Name: COLUMN utilisateur.created_at; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.utilisateur.created_at IS 'Date de création du compte';


--
-- TOC entry 4979 (class 0 OID 0)
-- Dependencies: 234
-- Name: COLUMN utilisateur.updated_at; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.utilisateur.updated_at IS 'Date de dernière mise à jour du compte';


--
-- TOC entry 4980 (class 0 OID 0)
-- Dependencies: 234
-- Name: COLUMN utilisateur.id; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.utilisateur.id IS 'Identifiant unique de l''utilisateur';


--
-- TOC entry 235 (class 1259 OID 38869)
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
-- TOC entry 4981 (class 0 OID 0)
-- Dependencies: 235
-- Name: utilisateur_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.utilisateur_id_seq OWNED BY public.utilisateur.id;


--
-- TOC entry 4698 (class 2604 OID 38708)
-- Name: categorie categorie_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.categorie ALTER COLUMN categorie_id SET DEFAULT nextval('public.categorie_categorie_id_seq'::regclass);


--
-- TOC entry 4699 (class 2604 OID 46848)
-- Name: categorie id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.categorie ALTER COLUMN id SET DEFAULT nextval('public.categorie_id_seq'::regclass);


--
-- TOC entry 4708 (class 2604 OID 38773)
-- Name: commentaire commentaire_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.commentaire ALTER COLUMN commentaire_id SET DEFAULT nextval('public.commentaire_commentaire_id_seq'::regclass);


--
-- TOC entry 4710 (class 2604 OID 46856)
-- Name: commentaire id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.commentaire ALTER COLUMN id SET DEFAULT nextval('public.commentaire_id_seq'::regclass);


--
-- TOC entry 4711 (class 2604 OID 38793)
-- Name: evaluation evaluation_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.evaluation ALTER COLUMN evaluation_id SET DEFAULT nextval('public.evaluation_evaluation_id_seq'::regclass);


--
-- TOC entry 4713 (class 2604 OID 46865)
-- Name: evaluation id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.evaluation ALTER COLUMN id SET DEFAULT nextval('public.evaluation_id_seq'::regclass);


--
-- TOC entry 4700 (class 2604 OID 38721)
-- Name: evenement evenement_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.evenement ALTER COLUMN evenement_id SET DEFAULT nextval('public.evenement_evenement_id_seq'::regclass);


--
-- TOC entry 4703 (class 2604 OID 38747)
-- Name: inscription inscription_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.inscription ALTER COLUMN inscription_id SET DEFAULT nextval('public.inscription_inscription_id_seq'::regclass);


--
-- TOC entry 4716 (class 2604 OID 38928)
-- Name: utilisateur id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.utilisateur ALTER COLUMN id SET DEFAULT nextval('public.utilisateur_id_seq'::regclass);


--
-- TOC entry 4936 (class 0 OID 38674)
-- Dependencies: 218
-- Data for Name: administrateur; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.administrateur (utilisateur_id) FROM stdin;
\.


--
-- TOC entry 4940 (class 0 OID 38705)
-- Dependencies: 222
-- Data for Name: categorie; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.categorie (categorie_id, nom, id, created_at, updated_at, couleur, description) FROM stdin;
61	Technologie	11	2025-10-18 14:32:27.856127	2025-10-18 14:32:27.856127	\N	Événements liés aux nouvelles technologies et à l'innovation
62	Business	12	2025-10-18 14:32:27.856127	2025-10-18 14:32:27.856127	\N	Conférences et séminaires professionnels
63	Culture	13	2025-10-18 14:32:27.856127	2025-10-18 14:32:27.856127	\N	Événements culturels, expositions et spectacles
64	Sport	14	2025-10-18 14:32:27.856127	2025-10-18 14:32:27.856127	\N	Événements sportifs et compétitions
65	Éducation	15	2025-10-18 14:32:27.856127	2025-10-18 14:32:27.856127	\N	Formations, ateliers et séminaires éducatifs
66	Musique	16	2025-10-18 14:32:27.856127	2025-10-18 14:32:27.856127	\N	Concerts et festivals de musique
67	Gastronomie	17	2025-10-18 14:32:27.856127	2025-10-18 14:32:27.856127	\N	Événements culinaires et dégustations
68	Santé	18	2025-10-18 14:32:27.856127	2025-10-18 14:32:27.856127	\N	Conférences et ateliers sur la santé et le bien-être
\.


--
-- TOC entry 4948 (class 0 OID 38770)
-- Dependencies: 230
-- Data for Name: commentaire; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.commentaire (commentaire_id, texte, horodatage, participant_id, evenement_id, id, created_at, updated_at) FROM stdin;
1	cet evenement est bien organisÃ©	2025-10-18 20:38:21.394713	53	47	1	2025-10-18 20:38:21.421393	2025-10-18 20:38:21.421393
2	moi aussi	2025-10-18 20:43:12.707022	54	47	2	2025-10-18 20:43:12.707022	2025-10-18 20:43:12.707022
3	quand ?	2025-10-19 08:43:58.177819	53	52	3	2025-10-19 08:43:58.17882	2025-10-19 08:43:58.17882
4	oÃ¹?	2025-10-19 08:46:36.611759	53	52	4	2025-10-19 08:46:36.623239	2025-10-19 08:46:36.623239
\.


--
-- TOC entry 4950 (class 0 OID 38790)
-- Dependencies: 232
-- Data for Name: evaluation; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.evaluation (evaluation_id, note, texte, horodatage, participant_id, evenement_id, id, created_at, updated_at) FROM stdin;
1	4		2025-10-18 20:45:58.950913	53	47	1	2025-10-18 20:45:58.965746	2025-10-18 20:45:58.966763
2	2		2025-10-18 20:47:10.231047	54	47	2	2025-10-18 20:47:10.231047	2025-10-18 20:47:10.231047
3	5		2025-10-19 08:44:05.066828	53	52	3	2025-10-19 08:44:05.066828	2025-10-19 08:44:05.066828
\.


--
-- TOC entry 4943 (class 0 OID 38718)
-- Dependencies: 225
-- Data for Name: evenement; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.evenement (evenement_id, titre, description, date_debut, date_fin, statut, lieu, organisateur_id, capacite, created_at, updated_at, organisateur_nom, image_url) FROM stdin;
51	Hackathon IA & Data Science	48h de développement intensif sur des projets d'intelligence artificielle. Prix à gagner et networking avec des entreprises tech.	2025-12-10 09:00:00	2025-12-12 18:00:00	PUBLIE	Station F, 5 Parvis Alan Turing, 75013 Paris	58	100	2025-10-18 14:32:27.856127	2025-10-18 14:32:27.856127	TechEvents Corp	https://images.unsplash.com/photo-1540575467063-178a50c2df87?w=800
56	Masterclass Photographie	Cours intensif de photographie professionnelle. Places limitées !	2025-11-30 14:00:00	2025-11-30 18:00:00	PUBLIE	Studio Photo Pro, 15 Rue de l'Image, 75011 Paris	59	20	2025-10-18 14:32:27.856127	2025-10-18 14:32:27.856127	CulturePro	https://images.unsplash.com/photo-1540575467063-178a50c2df87?w=800
48	Festival Jazz & Blues	Trois jours de concerts exceptionnels avec les plus grands artistes de jazz et blues. Ambiance conviviale et food trucks sur place.	2025-11-25 19:00:00	2025-11-27 23:00:00	PUBLIE	Parc des Expositions, Avenue Jean Jaurès, 33000 Bordeaux	59	500	2025-10-18 14:32:27.856127	2025-10-18 14:32:27.856127	CulturePro	https://images.unsplash.com/photo-1492684223066-81342ee5ff30?w=800
53	Festival Gastronomique	Dégustez les spécialités de chefs étoilés. Ateliers cuisine et démonstrations culinaires.	2025-11-28 11:00:00	2025-11-28 20:00:00	PUBLIE	Halle Tony Garnier, 20 Place Antonin Perrin, 69007 Lyon	59	300	2025-10-18 14:32:27.856127	2025-10-18 14:32:27.856127	CulturePro	https://images.unsplash.com/photo-1492684223066-81342ee5ff30?w=800
55	Concert Rock Legends	Soirée rock avec des groupes légendaires.	2024-10-05 20:00:00	2024-10-05 23:30:00	PUBLIE	Zénith de Paris	59	2000	2024-09-20 10:00:00	2024-09-20 10:00:00	CulturePro	https://images.unsplash.com/photo-1492684223066-81342ee5ff30?w=800
52	Conférence Santé & Bien-être	Découvrez les dernières avancées en médecine préventive et bien-être. Ateliers yoga et méditation inclus.	2025-11-22 09:00:00	2025-11-22 17:00:00	PUBLIE	Centre de Congrès, 12 Rue de la Santé, 31000 Toulouse	59	150	2025-10-18 14:32:27.856127	2025-10-18 14:32:27.856127	CulturePro	https://images.unsplash.com/photo-1540575467063-178a50c2df87?w=800
46	Conférence Tech Summit 2025	La plus grande conférence technologique de l'année. Découvrez les dernières innovations en IA, Cloud Computing et Cybersécurité. Intervenants de renommée internationale.	2025-12-15 09:00:00	2025-12-15 18:00:00	PUBLIE	Paris Convention Center, 2 Place de la Porte de Versailles, 75015 Paris	58	200	2025-10-18 14:32:27.856127	2025-10-18 14:32:27.856127	TechEvents Corp	https://images.unsplash.com/photo-1540575467063-178a50c2df87?w=800
49	Marathon de Paris 2025	Le célèbre marathon de Paris. Parcours de 42,195 km à travers les plus beaux monuments de la capitale.	2025-12-01 08:00:00	2025-12-01 14:00:00	PUBLIE	Champs-Élysées, 75008 Paris	60	30000	2025-10-18 14:32:27.856127	2025-10-18 14:32:27.856127	SportMax	https://images.unsplash.com/photo-1461896836934-ffe607ba8211?w=800
47	Atelier Développement Web Moderne	Apprenez React, Vue.js et les dernières tendances du développement frontend. Atelier pratique avec des exercices hands-on.	2025-11-20 14:00:00	2025-11-20 17:00:00	PUBLIE	Campus Numérique, 10 Rue de la Tech, 69002 Lyon	58	50	2025-10-18 14:32:27.856127	2025-10-18 14:32:27.856127	TechEvents Corp	https://images.unsplash.com/photo-1523580494863-6f3031224c94?w=800
50	Salon du Livre 2025	Rencontrez vos auteurs préférés, participez à des dédicaces et découvrez les nouveautés littéraires.	2025-11-18 10:00:00	2025-11-20 19:00:00	PUBLIE	Grand Palais, Avenue Winston Churchill, 75008 Paris	59	1000	2025-10-18 14:32:27.856127	2025-10-18 14:32:27.856127	CulturePro	https://images.unsplash.com/photo-1481627834876-b7833e8f5570?w=800
54	Conférence DevOps 2024	Retour d'expérience sur les meilleures pratiques DevOps et CI/CD.	2024-09-15 09:00:00	2024-09-15 18:00:00	PUBLIE	Paris La Défense	58	150	2024-09-01 10:00:00	2024-09-01 10:00:00	TechEvents Corp	https://images.unsplash.com/photo-1540575467063-178a50c2df87?w=800
\.


--
-- TOC entry 4951 (class 0 OID 38812)
-- Dependencies: 233
-- Data for Name: evenement_categorie; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.evenement_categorie (evenement_id, categorie_id) FROM stdin;
46	61
46	62
47	61
47	65
48	66
48	63
49	64
50	63
50	65
51	61
52	68
53	67
53	63
\.


--
-- TOC entry 4935 (class 0 OID 38653)
-- Dependencies: 217
-- Data for Name: flyway_schema_history; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) FROM stdin;
1	2	index	SQL	V2__index.sql	-235384765	postgres	2025-10-18 10:39:14.849676	53	t
\.


--
-- TOC entry 4946 (class 0 OID 38744)
-- Dependencies: 228
-- Data for Name: inscription; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.inscription (inscription_id, participant_id, evenement_id, date_inscription, statut, type_billet, quantite, created_at, updated_at) FROM stdin;
3	53	47	2025-10-18 19:40:33.580835	ACCEPTEE	STANDARD	1	2025-10-18 19:40:33.587796	2025-10-18 19:40:33.587796
4	53	50	2025-10-18 19:43:50.128172	ANNULEE	STANDARD	5	2025-10-18 19:43:50.128172	2025-10-18 19:44:13.153437
5	54	47	2025-10-18 20:42:48.108282	ACCEPTEE	STANDARD	10	2025-10-18 20:42:48.109492	2025-10-18 20:42:48.109492
6	54	50	2025-10-18 20:49:04.257183	ACCEPTEE	STANDARD	10	2025-10-18 20:49:04.2582	2025-10-18 20:49:04.2582
7	53	52	2025-10-19 08:43:42.322963	ACCEPTEE	VIP	6	2025-10-19 08:43:42.327487	2025-10-19 08:43:42.327487
\.


--
-- TOC entry 4937 (class 0 OID 38684)
-- Dependencies: 219
-- Data for Name: organisateur; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.organisateur (utilisateur_id, description, entreprise, siret, site_web, id) FROM stdin;
58	\N	\N	\N	\N	58
59	\N	\N	\N	\N	59
60	\N	\N	\N	\N	60
\.


--
-- TOC entry 4938 (class 0 OID 38694)
-- Dependencies: 220
-- Data for Name: participant; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.participant (utilisateur_id, date_naissance, preferences, telephone, id) FROM stdin;
53	\N	\N	\N	53
54	\N	\N	\N	54
55	\N	\N	\N	55
56	\N	\N	\N	56
57	\N	\N	\N	57
\.


--
-- TOC entry 4941 (class 0 OID 38711)
-- Dependencies: 223
-- Data for Name: statut_evenement; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.statut_evenement (statut) FROM stdin;
BROUILLON
PUBLIE
ANNULE
\.


--
-- TOC entry 4944 (class 0 OID 38737)
-- Dependencies: 226
-- Data for Name: statut_inscription; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.statut_inscription (statut) FROM stdin;
EN_ATTENTE
ACCEPTEE
REFUSEE
ANNULEE
\.


--
-- TOC entry 4952 (class 0 OID 38840)
-- Dependencies: 234
-- Data for Name: utilisateur; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.utilisateur (nom, email, mot_de_passe_hash, user_type, created_at, updated_at, id) FROM stdin;
Bob Dupont	bob@test.com	ecd71870d1963316a97e3ac3408c9835ad8cf0f3c1bc703527c30265534f75ae	PARTICIPANT	2025-10-18 14:32:27.856127	2025-10-18 14:32:27.856127	54
Claire Dubois	claire@test.com	ecd71870d1963316a97e3ac3408c9835ad8cf0f3c1bc703527c30265534f75ae	PARTICIPANT	2025-10-18 14:32:27.856127	2025-10-18 14:32:27.856127	55
David Leroy	david@test.com	ecd71870d1963316a97e3ac3408c9835ad8cf0f3c1bc703527c30265534f75ae	PARTICIPANT	2025-10-18 14:32:27.856127	2025-10-18 14:32:27.856127	56
Emma Bernard	emma@test.com	ecd71870d1963316a97e3ac3408c9835ad8cf0f3c1bc703527c30265534f75ae	PARTICIPANT	2025-10-18 14:32:27.856127	2025-10-18 14:32:27.856127	57
TechEvents Corp	tech@events.com	ecd71870d1963316a97e3ac3408c9835ad8cf0f3c1bc703527c30265534f75ae	ORGANISATEUR	2025-10-18 14:32:27.856127	2025-10-18 14:32:27.856127	58
CulturePro	culture@pro.com	ecd71870d1963316a97e3ac3408c9835ad8cf0f3c1bc703527c30265534f75ae	ORGANISATEUR	2025-10-18 14:32:27.856127	2025-10-18 14:32:27.856127	59
SportMax	sport@max.com	ecd71870d1963316a97e3ac3408c9835ad8cf0f3c1bc703527c30265534f75ae	ORGANISATEUR	2025-10-18 14:32:27.856127	2025-10-18 14:32:27.856127	60
Alice Martin	alice@test.com	d9b5f58f0b38198293971865a14074f59eba3e82595becbe86ae51f1d9f1f65e	PARTICIPANT	2025-10-18 14:32:27.856127	2025-10-19 08:10:45.530589	53
\.


--
-- TOC entry 4982 (class 0 OID 0)
-- Dependencies: 221
-- Name: categorie_categorie_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.categorie_categorie_id_seq', 68, true);


--
-- TOC entry 4983 (class 0 OID 0)
-- Dependencies: 236
-- Name: categorie_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.categorie_id_seq', 18, true);


--
-- TOC entry 4984 (class 0 OID 0)
-- Dependencies: 229
-- Name: commentaire_commentaire_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.commentaire_commentaire_id_seq', 4, true);


--
-- TOC entry 4985 (class 0 OID 0)
-- Dependencies: 237
-- Name: commentaire_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.commentaire_id_seq', 4, true);


--
-- TOC entry 4986 (class 0 OID 0)
-- Dependencies: 231
-- Name: evaluation_evaluation_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.evaluation_evaluation_id_seq', 3, true);


--
-- TOC entry 4987 (class 0 OID 0)
-- Dependencies: 238
-- Name: evaluation_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.evaluation_id_seq', 3, true);


--
-- TOC entry 4988 (class 0 OID 0)
-- Dependencies: 224
-- Name: evenement_evenement_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.evenement_evenement_id_seq', 56, true);


--
-- TOC entry 4989 (class 0 OID 0)
-- Dependencies: 227
-- Name: inscription_inscription_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.inscription_inscription_id_seq', 7, true);


--
-- TOC entry 4990 (class 0 OID 0)
-- Dependencies: 235
-- Name: utilisateur_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.utilisateur_id_seq', 62, true);


--
-- TOC entry 4725 (class 2606 OID 38944)
-- Name: administrateur administrateur_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.administrateur
    ADD CONSTRAINT administrateur_pkey PRIMARY KEY (utilisateur_id);


--
-- TOC entry 4731 (class 2606 OID 38710)
-- Name: categorie categorie_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.categorie
    ADD CONSTRAINT categorie_pkey PRIMARY KEY (categorie_id);


--
-- TOC entry 4756 (class 2606 OID 38778)
-- Name: commentaire commentaire_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.commentaire
    ADD CONSTRAINT commentaire_pkey PRIMARY KEY (commentaire_id);


--
-- TOC entry 4761 (class 2606 OID 38958)
-- Name: evaluation evaluation_participant_id_evenement_id_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.evaluation
    ADD CONSTRAINT evaluation_participant_id_evenement_id_key UNIQUE (participant_id, evenement_id);


--
-- TOC entry 4763 (class 2606 OID 38799)
-- Name: evaluation evaluation_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.evaluation
    ADD CONSTRAINT evaluation_pkey PRIMARY KEY (evaluation_id);


--
-- TOC entry 4771 (class 2606 OID 38816)
-- Name: evenement_categorie evenement_categorie_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.evenement_categorie
    ADD CONSTRAINT evenement_categorie_pkey PRIMARY KEY (evenement_id, categorie_id);


--
-- TOC entry 4735 (class 2606 OID 38726)
-- Name: evenement evenement_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.evenement
    ADD CONSTRAINT evenement_pkey PRIMARY KEY (evenement_id);


--
-- TOC entry 4722 (class 2606 OID 38660)
-- Name: flyway_schema_history flyway_schema_history_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.flyway_schema_history
    ADD CONSTRAINT flyway_schema_history_pk PRIMARY KEY (installed_rank);


--
-- TOC entry 4750 (class 2606 OID 38968)
-- Name: inscription inscription_participant_id_evenement_id_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.inscription
    ADD CONSTRAINT inscription_participant_id_evenement_id_key UNIQUE (participant_id, evenement_id);


--
-- TOC entry 4752 (class 2606 OID 38751)
-- Name: inscription inscription_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.inscription
    ADD CONSTRAINT inscription_pkey PRIMARY KEY (inscription_id);


--
-- TOC entry 4727 (class 2606 OID 38932)
-- Name: organisateur organisateur_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.organisateur
    ADD CONSTRAINT organisateur_pkey PRIMARY KEY (utilisateur_id);


--
-- TOC entry 4729 (class 2606 OID 38938)
-- Name: participant participant_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.participant
    ADD CONSTRAINT participant_pkey PRIMARY KEY (utilisateur_id);


--
-- TOC entry 4733 (class 2606 OID 38716)
-- Name: statut_evenement statut_evenement_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.statut_evenement
    ADD CONSTRAINT statut_evenement_pkey PRIMARY KEY (statut);


--
-- TOC entry 4743 (class 2606 OID 38742)
-- Name: statut_inscription statut_inscription_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.statut_inscription
    ADD CONSTRAINT statut_inscription_pkey PRIMARY KEY (statut);


--
-- TOC entry 4754 (class 2606 OID 46881)
-- Name: inscription uk158pfrsr36iabvn6uvmjjvb2n; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.inscription
    ADD CONSTRAINT uk158pfrsr36iabvn6uvmjjvb2n UNIQUE (participant_id, evenement_id);


--
-- TOC entry 4769 (class 2606 OID 46879)
-- Name: evaluation uktb4wra7yac7ukykbhmkp2u8j0; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.evaluation
    ADD CONSTRAINT uktb4wra7yac7ukykbhmkp2u8j0 UNIQUE (participant_id, evenement_id);


--
-- TOC entry 4774 (class 2606 OID 38881)
-- Name: utilisateur utilisateur_email_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.utilisateur
    ADD CONSTRAINT utilisateur_email_key UNIQUE (email);


--
-- TOC entry 4776 (class 2606 OID 38930)
-- Name: utilisateur utilisateur_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.utilisateur
    ADD CONSTRAINT utilisateur_pkey PRIMARY KEY (id);


--
-- TOC entry 4723 (class 1259 OID 38661)
-- Name: flyway_schema_history_s_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX flyway_schema_history_s_idx ON public.flyway_schema_history USING btree (success);


--
-- TOC entry 4757 (class 1259 OID 38833)
-- Name: idx_commentaire_evenement; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_commentaire_evenement ON public.commentaire USING btree (evenement_id);


--
-- TOC entry 4758 (class 1259 OID 46895)
-- Name: idx_commentaire_horodatage; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_commentaire_horodatage ON public.commentaire USING btree (horodatage DESC);


--
-- TOC entry 4759 (class 1259 OID 46892)
-- Name: idx_commentaire_participant; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_commentaire_participant ON public.commentaire USING btree (participant_id);


--
-- TOC entry 4764 (class 1259 OID 38834)
-- Name: idx_evaluation_evenement; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_evaluation_evenement ON public.evaluation USING btree (evenement_id);


--
-- TOC entry 4765 (class 1259 OID 46896)
-- Name: idx_evaluation_horodatage; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_evaluation_horodatage ON public.evaluation USING btree (horodatage DESC);


--
-- TOC entry 4766 (class 1259 OID 46893)
-- Name: idx_evaluation_participant; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_evaluation_participant ON public.evaluation USING btree (participant_id);


--
-- TOC entry 4767 (class 1259 OID 46894)
-- Name: idx_evaluation_participant_evenement; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_evaluation_participant_evenement ON public.evaluation USING btree (participant_id, evenement_id);


--
-- TOC entry 4736 (class 1259 OID 46845)
-- Name: idx_evenement_date_debut; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_evenement_date_debut ON public.evenement USING btree (date_debut);


--
-- TOC entry 4737 (class 1259 OID 46846)
-- Name: idx_evenement_date_fin; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_evenement_date_fin ON public.evenement USING btree (date_fin);


--
-- TOC entry 4738 (class 1259 OID 38829)
-- Name: idx_evenement_dates; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_evenement_dates ON public.evenement USING btree (date_debut, date_fin);


--
-- TOC entry 4739 (class 1259 OID 38949)
-- Name: idx_evenement_organisateur; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_evenement_organisateur ON public.evenement USING btree (organisateur_id);


--
-- TOC entry 4740 (class 1259 OID 38828)
-- Name: idx_evenement_statut; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_evenement_statut ON public.evenement USING btree (statut);


--
-- TOC entry 4741 (class 1259 OID 46840)
-- Name: idx_evenement_statut_dates; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_evenement_statut_dates ON public.evenement USING btree (statut, date_debut, date_fin);


--
-- TOC entry 4744 (class 1259 OID 46844)
-- Name: idx_inscription_date_inscription; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_inscription_date_inscription ON public.inscription USING btree (date_inscription);


--
-- TOC entry 4745 (class 1259 OID 38831)
-- Name: idx_inscription_evenement; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_inscription_evenement ON public.inscription USING btree (evenement_id);


--
-- TOC entry 4746 (class 1259 OID 38969)
-- Name: idx_inscription_participant; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_inscription_participant ON public.inscription USING btree (participant_id);


--
-- TOC entry 4747 (class 1259 OID 46841)
-- Name: idx_inscription_participant_evenement; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_inscription_participant_evenement ON public.inscription USING btree (participant_id, evenement_id);


--
-- TOC entry 4748 (class 1259 OID 38832)
-- Name: idx_inscription_statut; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_inscription_statut ON public.inscription USING btree (statut);


--
-- TOC entry 4772 (class 1259 OID 38853)
-- Name: idx_utilisateur_email; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_utilisateur_email ON public.utilisateur USING btree (email);


--
-- TOC entry 4784 (class 2606 OID 38784)
-- Name: commentaire commentaire_evenement_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.commentaire
    ADD CONSTRAINT commentaire_evenement_id_fkey FOREIGN KEY (evenement_id) REFERENCES public.evenement(evenement_id) ON DELETE CASCADE;


--
-- TOC entry 4785 (class 2606 OID 38975)
-- Name: commentaire commentaire_participant_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.commentaire
    ADD CONSTRAINT commentaire_participant_id_fkey FOREIGN KEY (participant_id) REFERENCES public.participant(utilisateur_id);


--
-- TOC entry 4786 (class 2606 OID 38807)
-- Name: evaluation evaluation_evenement_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.evaluation
    ADD CONSTRAINT evaluation_evenement_id_fkey FOREIGN KEY (evenement_id) REFERENCES public.evenement(evenement_id) ON DELETE CASCADE;


--
-- TOC entry 4787 (class 2606 OID 38980)
-- Name: evaluation evaluation_participant_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.evaluation
    ADD CONSTRAINT evaluation_participant_id_fkey FOREIGN KEY (participant_id) REFERENCES public.participant(utilisateur_id);


--
-- TOC entry 4788 (class 2606 OID 38822)
-- Name: evenement_categorie evenement_categorie_categorie_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.evenement_categorie
    ADD CONSTRAINT evenement_categorie_categorie_id_fkey FOREIGN KEY (categorie_id) REFERENCES public.categorie(categorie_id) ON DELETE CASCADE;


--
-- TOC entry 4789 (class 2606 OID 38817)
-- Name: evenement_categorie evenement_categorie_evenement_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.evenement_categorie
    ADD CONSTRAINT evenement_categorie_evenement_id_fkey FOREIGN KEY (evenement_id) REFERENCES public.evenement(evenement_id) ON DELETE CASCADE;


--
-- TOC entry 4779 (class 2606 OID 38970)
-- Name: evenement evenement_organisateur_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.evenement
    ADD CONSTRAINT evenement_organisateur_id_fkey FOREIGN KEY (organisateur_id) REFERENCES public.organisateur(utilisateur_id);


--
-- TOC entry 4780 (class 2606 OID 38732)
-- Name: evenement evenement_statut_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.evenement
    ADD CONSTRAINT evenement_statut_fkey FOREIGN KEY (statut) REFERENCES public.statut_evenement(statut);


--
-- TOC entry 4778 (class 2606 OID 46887)
-- Name: participant fk7e5fq2sm9cq2f48mytt0r1ge8; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.participant
    ADD CONSTRAINT fk7e5fq2sm9cq2f48mytt0r1ge8 FOREIGN KEY (id) REFERENCES public.utilisateur(id);


--
-- TOC entry 4777 (class 2606 OID 46882)
-- Name: organisateur fk8i40q42p3k89cww2mwwyfg2km; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.organisateur
    ADD CONSTRAINT fk8i40q42p3k89cww2mwwyfg2km FOREIGN KEY (id) REFERENCES public.utilisateur(id);


--
-- TOC entry 4781 (class 2606 OID 38759)
-- Name: inscription inscription_evenement_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.inscription
    ADD CONSTRAINT inscription_evenement_id_fkey FOREIGN KEY (evenement_id) REFERENCES public.evenement(evenement_id) ON DELETE CASCADE;


--
-- TOC entry 4782 (class 2606 OID 38985)
-- Name: inscription inscription_participant_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.inscription
    ADD CONSTRAINT inscription_participant_id_fkey FOREIGN KEY (participant_id) REFERENCES public.participant(utilisateur_id);


--
-- TOC entry 4783 (class 2606 OID 38764)
-- Name: inscription inscription_statut_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.inscription
    ADD CONSTRAINT inscription_statut_fkey FOREIGN KEY (statut) REFERENCES public.statut_inscription(statut);


-- Completed on 2025-10-20 07:00:45

--
-- PostgreSQL database dump complete
--

