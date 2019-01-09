--
-- PostgreSQL database dump
--

-- Dumped from database version 10.6 (Ubuntu 10.6-1.pgdg16.04+1)
-- Dumped by pg_dump version 11.1 (Ubuntu 11.1-1.pgdg16.04+1)

-- Started on 2019-01-07 22:35:48 VET

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 2961 (class 1262 OID 13014)
-- Name: postgres; Type: DATABASE; Schema: -; Owner: -
--

CREATE DATABASE postgres WITH TEMPLATE = template0 ENCODING = 'UTF8' LC_COLLATE = 'es_VE.UTF-8' LC_CTYPE = 'es_VE.UTF-8';


\connect postgres

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 2962 (class 0 OID 0)
-- Dependencies: 2961
-- Name: DATABASE postgres; Type: COMMENT; Schema: -; Owner: -
--

COMMENT ON DATABASE postgres IS 'default administrative connection database';


--
-- TOC entry 8 (class 2615 OID 16384)
-- Name: gestion; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA gestion;


--
-- TOC entry 209 (class 1259 OID 24584)
-- Name: empresa_id_sequence; Type: SEQUENCE; Schema: gestion; Owner: -
--

CREATE SEQUENCE gestion.empresa_id_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 205 (class 1259 OID 16452)
-- Name: empresa; Type: TABLE; Schema: gestion; Owner: -
--

CREATE TABLE gestion.empresa (
    empresa_id integer DEFAULT nextval('gestion.empresa_id_sequence'::regclass) NOT NULL,
    nombre character varying(50) NOT NULL
);


--
-- TOC entry 200 (class 1259 OID 16396)
-- Name: material_id_sequence; Type: SEQUENCE; Schema: gestion; Owner: -
--

CREATE SEQUENCE gestion.material_id_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 199 (class 1259 OID 16393)
-- Name: material; Type: TABLE; Schema: gestion; Owner: -
--

CREATE TABLE gestion.material (
    material_id integer DEFAULT nextval('gestion.material_id_sequence'::regclass) NOT NULL,
    nombre character varying(100) NOT NULL,
    tipo_material_id integer NOT NULL,
    costo double precision NOT NULL,
    tipo_unidad_id integer NOT NULL,
    cantidad_compra double precision NOT NULL,
    empresa_id integer NOT NULL,
    activo boolean NOT NULL
);


--
-- TOC entry 198 (class 1259 OID 16390)
-- Name: producto_id_sequence; Type: SEQUENCE; Schema: gestion; Owner: -
--

CREATE SEQUENCE gestion.producto_id_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 197 (class 1259 OID 16385)
-- Name: producto; Type: TABLE; Schema: gestion; Owner: -
--

CREATE TABLE gestion.producto (
    producto_id integer DEFAULT nextval('gestion.producto_id_sequence'::regclass) NOT NULL,
    nombre character varying(100) NOT NULL,
    empresa_id integer NOT NULL,
    activo boolean NOT NULL
);


--
-- TOC entry 204 (class 1259 OID 16449)
-- Name: producto_material_id_sequence; Type: SEQUENCE; Schema: gestion; Owner: -
--

CREATE SEQUENCE gestion.producto_material_id_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 201 (class 1259 OID 16401)
-- Name: producto_material; Type: TABLE; Schema: gestion; Owner: -
--

CREATE TABLE gestion.producto_material (
    producto_material_id integer DEFAULT nextval('gestion.producto_material_id_sequence'::regclass) NOT NULL,
    producto_id integer NOT NULL,
    material_id integer NOT NULL,
    cantidad double precision NOT NULL,
    tipo_unidad_id integer NOT NULL
);


--
-- TOC entry 210 (class 1259 OID 24587)
-- Name: tipo_material_id_sequence; Type: SEQUENCE; Schema: gestion; Owner: -
--

CREATE SEQUENCE gestion.tipo_material_id_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 207 (class 1259 OID 16468)
-- Name: tipo_material; Type: TABLE; Schema: gestion; Owner: -
--

CREATE TABLE gestion.tipo_material (
    tipo_material_id integer DEFAULT nextval('gestion.tipo_material_id_sequence'::regclass) NOT NULL,
    tipo character varying NOT NULL,
    empresa_id integer NOT NULL
);


--
-- TOC entry 208 (class 1259 OID 24576)
-- Name: tipo_unidad; Type: TABLE; Schema: gestion; Owner: -
--

CREATE TABLE gestion.tipo_unidad (
    tipo_unidad_id integer NOT NULL,
    tipo character varying NOT NULL,
    unidad character varying NOT NULL,
    referencia_en_gramos double precision NOT NULL
);


--
-- TOC entry 206 (class 1259 OID 16459)
-- Name: tipo_usuario; Type: TABLE; Schema: gestion; Owner: -
--

CREATE TABLE gestion.tipo_usuario (
    tipo_usuario_id integer NOT NULL,
    tipo character varying NOT NULL
);


--
-- TOC entry 203 (class 1259 OID 16417)
-- Name: usuario_id_sequence; Type: SEQUENCE; Schema: gestion; Owner: -
--

CREATE SEQUENCE gestion.usuario_id_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 202 (class 1259 OID 16412)
-- Name: usuario; Type: TABLE; Schema: gestion; Owner: -
--

CREATE TABLE gestion.usuario (
    usuario_id integer DEFAULT nextval('gestion.usuario_id_sequence'::regclass) NOT NULL,
    nombre character varying(25) NOT NULL,
    email character varying(25) NOT NULL,
    password character varying(200) NOT NULL,
    empresa_id integer,
    tipo_usuario_id integer NOT NULL
);


--
-- TOC entry 2950 (class 0 OID 16452)
-- Dependencies: 205
-- Data for Name: empresa; Type: TABLE DATA; Schema: gestion; Owner: -
--

INSERT INTO gestion.empresa (empresa_id, nombre) VALUES (1, 'la de lore');


--
-- TOC entry 2944 (class 0 OID 16393)
-- Dependencies: 199
-- Data for Name: material; Type: TABLE DATA; Schema: gestion; Owner: -
--

INSERT INTO gestion.material (material_id, nombre, tipo_material_id, costo, tipo_unidad_id, cantidad_compra, empresa_id, activo) VALUES (1, 'aceite', 1, 50, 1, 20, 1, true);
INSERT INTO gestion.material (material_id, nombre, tipo_material_id, costo, tipo_unidad_id, cantidad_compra, empresa_id, activo) VALUES (2, 'carbon', 1, 50, 1, 20, 1, true);
INSERT INTO gestion.material (material_id, nombre, tipo_material_id, costo, tipo_unidad_id, cantidad_compra, empresa_id, activo) VALUES (3, 'agua', 1, 50, 2, 20, 1, true);
INSERT INTO gestion.material (material_id, nombre, tipo_material_id, costo, tipo_unidad_id, cantidad_compra, empresa_id, activo) VALUES (5, 'Este ', 1, 50, 1, 1, 1, false);


--
-- TOC entry 2942 (class 0 OID 16385)
-- Dependencies: 197
-- Data for Name: producto; Type: TABLE DATA; Schema: gestion; Owner: -
--

INSERT INTO gestion.producto (producto_id, nombre, empresa_id, activo) VALUES (1, 'mayonesa', 1, true);
INSERT INTO gestion.producto (producto_id, nombre, empresa_id, activo) VALUES (2, 'este veeeee', 1, false);


--
-- TOC entry 2946 (class 0 OID 16401)
-- Dependencies: 201
-- Data for Name: producto_material; Type: TABLE DATA; Schema: gestion; Owner: -
--

INSERT INTO gestion.producto_material (producto_material_id, producto_id, material_id, cantidad, tipo_unidad_id) VALUES (1, 1, 1, 1, 2);


--
-- TOC entry 2952 (class 0 OID 16468)
-- Dependencies: 207
-- Data for Name: tipo_material; Type: TABLE DATA; Schema: gestion; Owner: -
--

INSERT INTO gestion.tipo_material (tipo_material_id, tipo, empresa_id) VALUES (1, 'comida', 1);


--
-- TOC entry 2953 (class 0 OID 24576)
-- Dependencies: 208
-- Data for Name: tipo_unidad; Type: TABLE DATA; Schema: gestion; Owner: -
--

INSERT INTO gestion.tipo_unidad (tipo_unidad_id, tipo, unidad, referencia_en_gramos) VALUES (1, 'gramos', 'gr', 1);
INSERT INTO gestion.tipo_unidad (tipo_unidad_id, tipo, unidad, referencia_en_gramos) VALUES (2, 'kilogramos', 'kg', 1000);


--
-- TOC entry 2951 (class 0 OID 16459)
-- Dependencies: 206
-- Data for Name: tipo_usuario; Type: TABLE DATA; Schema: gestion; Owner: -
--

INSERT INTO gestion.tipo_usuario (tipo_usuario_id, tipo) VALUES (1, 'Administrador');
INSERT INTO gestion.tipo_usuario (tipo_usuario_id, tipo) VALUES (2, 'Usuario');


--
-- TOC entry 2947 (class 0 OID 16412)
-- Dependencies: 202
-- Data for Name: usuario; Type: TABLE DATA; Schema: gestion; Owner: -
--

INSERT INTO gestion.usuario (usuario_id, nombre, email, password, empresa_id, tipo_usuario_id) VALUES (1, 'Juan', 'juanchavez@gmail.com', '1234', 1, 1);
INSERT INTO gestion.usuario (usuario_id, nombre, email, password, empresa_id, tipo_usuario_id) VALUES (3, 'juancho', 'este@veee.com', '$2a$10$/RnySJ/HtR1lq8AqV9WT9eSB7OiloAvkwWz7fOkuUHPoqClkcQSXO', 1, 1);
INSERT INTO gestion.usuario (usuario_id, nombre, email, password, empresa_id, tipo_usuario_id) VALUES (4, 'juancho', 'este@veeee.com', '$2a$10$6/GEoFt4WRcAYz7KEMALWuDiaJ5AWBT.ElA862X8.KDk./B/RNOqW', 1, 1);


--
-- TOC entry 2963 (class 0 OID 0)
-- Dependencies: 209
-- Name: empresa_id_sequence; Type: SEQUENCE SET; Schema: gestion; Owner: -
--

SELECT pg_catalog.setval('gestion.empresa_id_sequence', 1, true);


--
-- TOC entry 2964 (class 0 OID 0)
-- Dependencies: 200
-- Name: material_id_sequence; Type: SEQUENCE SET; Schema: gestion; Owner: -
--

SELECT pg_catalog.setval('gestion.material_id_sequence', 5, true);


--
-- TOC entry 2965 (class 0 OID 0)
-- Dependencies: 198
-- Name: producto_id_sequence; Type: SEQUENCE SET; Schema: gestion; Owner: -
--

SELECT pg_catalog.setval('gestion.producto_id_sequence', 2, true);


--
-- TOC entry 2966 (class 0 OID 0)
-- Dependencies: 204
-- Name: producto_material_id_sequence; Type: SEQUENCE SET; Schema: gestion; Owner: -
--

SELECT pg_catalog.setval('gestion.producto_material_id_sequence', 1, true);


--
-- TOC entry 2967 (class 0 OID 0)
-- Dependencies: 210
-- Name: tipo_material_id_sequence; Type: SEQUENCE SET; Schema: gestion; Owner: -
--

SELECT pg_catalog.setval('gestion.tipo_material_id_sequence', 1, true);


--
-- TOC entry 2968 (class 0 OID 0)
-- Dependencies: 203
-- Name: usuario_id_sequence; Type: SEQUENCE SET; Schema: gestion; Owner: -
--

SELECT pg_catalog.setval('gestion.usuario_id_sequence', 4, true);


--
-- TOC entry 2804 (class 2606 OID 16456)
-- Name: empresa empresa_pk; Type: CONSTRAINT; Schema: gestion; Owner: -
--

ALTER TABLE ONLY gestion.empresa
    ADD CONSTRAINT empresa_pk PRIMARY KEY (empresa_id);


--
-- TOC entry 2796 (class 2606 OID 16399)
-- Name: material ingrediente_pk; Type: CONSTRAINT; Schema: gestion; Owner: -
--

ALTER TABLE ONLY gestion.material
    ADD CONSTRAINT ingrediente_pk PRIMARY KEY (material_id);


--
-- TOC entry 2798 (class 2606 OID 16405)
-- Name: producto_material producto_ingrediente_pk; Type: CONSTRAINT; Schema: gestion; Owner: -
--

ALTER TABLE ONLY gestion.producto_material
    ADD CONSTRAINT producto_ingrediente_pk PRIMARY KEY (producto_material_id);


--
-- TOC entry 2794 (class 2606 OID 16389)
-- Name: producto producto_pk; Type: CONSTRAINT; Schema: gestion; Owner: -
--

ALTER TABLE ONLY gestion.producto
    ADD CONSTRAINT producto_pk PRIMARY KEY (producto_id);


--
-- TOC entry 2808 (class 2606 OID 16472)
-- Name: tipo_material tipo_material_pk; Type: CONSTRAINT; Schema: gestion; Owner: -
--

ALTER TABLE ONLY gestion.tipo_material
    ADD CONSTRAINT tipo_material_pk PRIMARY KEY (tipo_material_id);


--
-- TOC entry 2810 (class 2606 OID 24580)
-- Name: tipo_unidad tipo_unidad_pk; Type: CONSTRAINT; Schema: gestion; Owner: -
--

ALTER TABLE ONLY gestion.tipo_unidad
    ADD CONSTRAINT tipo_unidad_pk PRIMARY KEY (tipo_unidad_id);


--
-- TOC entry 2806 (class 2606 OID 16463)
-- Name: tipo_usuario tipo_usuario_pk; Type: CONSTRAINT; Schema: gestion; Owner: -
--

ALTER TABLE ONLY gestion.tipo_usuario
    ADD CONSTRAINT tipo_usuario_pk PRIMARY KEY (tipo_usuario_id);


--
-- TOC entry 2800 (class 2606 OID 16416)
-- Name: usuario usuario_pk; Type: CONSTRAINT; Schema: gestion; Owner: -
--

ALTER TABLE ONLY gestion.usuario
    ADD CONSTRAINT usuario_pk PRIMARY KEY (usuario_id);


--
-- TOC entry 2802 (class 2606 OID 24647)
-- Name: usuario usuario_un; Type: CONSTRAINT; Schema: gestion; Owner: -
--

ALTER TABLE ONLY gestion.usuario
    ADD CONSTRAINT usuario_un UNIQUE (email);


--
-- TOC entry 2812 (class 2606 OID 24595)
-- Name: material material_empresa_fk; Type: FK CONSTRAINT; Schema: gestion; Owner: -
--

ALTER TABLE ONLY gestion.material
    ADD CONSTRAINT material_empresa_fk FOREIGN KEY (empresa_id) REFERENCES gestion.empresa(empresa_id);


--
-- TOC entry 2813 (class 2606 OID 24605)
-- Name: material material_tipo_material_fk; Type: FK CONSTRAINT; Schema: gestion; Owner: -
--

ALTER TABLE ONLY gestion.material
    ADD CONSTRAINT material_tipo_material_fk FOREIGN KEY (tipo_material_id) REFERENCES gestion.tipo_material(tipo_material_id);


--
-- TOC entry 2814 (class 2606 OID 24610)
-- Name: material material_tipo_unidad_fk; Type: FK CONSTRAINT; Schema: gestion; Owner: -
--

ALTER TABLE ONLY gestion.material
    ADD CONSTRAINT material_tipo_unidad_fk FOREIGN KEY (tipo_unidad_id) REFERENCES gestion.tipo_unidad(tipo_unidad_id);


--
-- TOC entry 2811 (class 2606 OID 24590)
-- Name: producto producto_empresa_fk; Type: FK CONSTRAINT; Schema: gestion; Owner: -
--

ALTER TABLE ONLY gestion.producto
    ADD CONSTRAINT producto_empresa_fk FOREIGN KEY (empresa_id) REFERENCES gestion.empresa(empresa_id);


--
-- TOC entry 2816 (class 2606 OID 24620)
-- Name: producto_material producto_material_material_fk; Type: FK CONSTRAINT; Schema: gestion; Owner: -
--

ALTER TABLE ONLY gestion.producto_material
    ADD CONSTRAINT producto_material_material_fk FOREIGN KEY (material_id) REFERENCES gestion.material(material_id);


--
-- TOC entry 2815 (class 2606 OID 24615)
-- Name: producto_material producto_material_producto_fk; Type: FK CONSTRAINT; Schema: gestion; Owner: -
--

ALTER TABLE ONLY gestion.producto_material
    ADD CONSTRAINT producto_material_producto_fk FOREIGN KEY (producto_id) REFERENCES gestion.producto(producto_id);


--
-- TOC entry 2817 (class 2606 OID 24625)
-- Name: producto_material producto_material_tipo_unidad_fk; Type: FK CONSTRAINT; Schema: gestion; Owner: -
--

ALTER TABLE ONLY gestion.producto_material
    ADD CONSTRAINT producto_material_tipo_unidad_fk FOREIGN KEY (tipo_unidad_id) REFERENCES gestion.tipo_unidad(tipo_unidad_id);


--
-- TOC entry 2820 (class 2606 OID 24600)
-- Name: tipo_material tipo_material_empresa_fk; Type: FK CONSTRAINT; Schema: gestion; Owner: -
--

ALTER TABLE ONLY gestion.tipo_material
    ADD CONSTRAINT tipo_material_empresa_fk FOREIGN KEY (empresa_id) REFERENCES gestion.empresa(empresa_id);


--
-- TOC entry 2819 (class 2606 OID 24640)
-- Name: usuario usuario_empresa_fk; Type: FK CONSTRAINT; Schema: gestion; Owner: -
--

ALTER TABLE ONLY gestion.usuario
    ADD CONSTRAINT usuario_empresa_fk FOREIGN KEY (empresa_id) REFERENCES gestion.empresa(empresa_id);


--
-- TOC entry 2818 (class 2606 OID 24635)
-- Name: usuario usuario_tipo_usuario_fk; Type: FK CONSTRAINT; Schema: gestion; Owner: -
--

ALTER TABLE ONLY gestion.usuario
    ADD CONSTRAINT usuario_tipo_usuario_fk FOREIGN KEY (tipo_usuario_id) REFERENCES gestion.tipo_usuario(tipo_usuario_id);


-- Completed on 2019-01-07 22:35:49 VET

--
-- PostgreSQL database dump complete
--

