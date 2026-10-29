-- =====================================================================
-- SoulUp - Gamificacao Sustentavel
-- Script de criacao das tabelas (Oracle) + dados de exemplo
-- Sprint 4 - Computational Thinking Using Python - FIAP 2026
--
-- Como usar: abra no SQL Developer conectado ao Oracle da FIAP e
-- execute como script (F5). Na primeira execucao os DROP TABLE vao
-- mostrar erro ORA-00942 (tabela nao existe) - isso e esperado.
-- =====================================================================

-- Remove as tabelas (filha primeiro) para o script poder ser executado de novo
DROP TABLE SOULUP_REGISTRO_ACAO CASCADE CONSTRAINTS;
DROP TABLE SOULUP_ACAO CASCADE CONSTRAINTS;
DROP TABLE SOULUP_USUARIO CASCADE CONSTRAINTS;

-- ---------------------------------------------------------------------
-- Usuarios participantes da gamificacao
-- ---------------------------------------------------------------------
CREATE TABLE SOULUP_USUARIO (
    id_usuario   NUMBER GENERATED ALWAYS AS IDENTITY,
    nome         VARCHAR2(80)  NOT NULL,
    email        VARCHAR2(120) NOT NULL,
    dt_cadastro  DATE DEFAULT SYSDATE NOT NULL,
    CONSTRAINT pk_soulup_usuario PRIMARY KEY (id_usuario),
    CONSTRAINT uk_soulup_usuario_nome UNIQUE (nome),
    CONSTRAINT uk_soulup_usuario_email UNIQUE (email)
);

-- ---------------------------------------------------------------------
-- Catalogo de acoes sustentaveis (era o dicionario "acoes" da Sprint 3)
-- ---------------------------------------------------------------------
CREATE TABLE SOULUP_ACAO (
    id_acao    NUMBER GENERATED ALWAYS AS IDENTITY,
    nome       VARCHAR2(60) NOT NULL,
    pontos     NUMBER(3)    NOT NULL,
    categoria  VARCHAR2(20) NOT NULL,
    CONSTRAINT pk_soulup_acao PRIMARY KEY (id_acao),
    CONSTRAINT uk_soulup_acao_nome UNIQUE (nome),
    CONSTRAINT ck_soulup_acao_pontos CHECK (pontos BETWEEN 1 AND 100),
    CONSTRAINT ck_soulup_acao_categoria CHECK (categoria IN (
        'MOBILIDADE', 'RESIDUOS', 'ENERGIA', 'AGUA',
        'CONSUMO', 'NATUREZA', 'ENGAJAMENTO'))
);

-- ---------------------------------------------------------------------
-- Acoes realizadas por cada usuario (era a lista "acoes" do usuario)
-- A UNIQUE impede registrar a mesma acao duas vezes para o mesmo usuario
-- ---------------------------------------------------------------------
CREATE TABLE SOULUP_REGISTRO_ACAO (
    id_registro  NUMBER GENERATED ALWAYS AS IDENTITY,
    id_usuario   NUMBER NOT NULL,
    id_acao      NUMBER NOT NULL,
    dt_registro  DATE DEFAULT SYSDATE NOT NULL,
    CONSTRAINT pk_soulup_registro PRIMARY KEY (id_registro),
    CONSTRAINT fk_soulup_registro_usuario FOREIGN KEY (id_usuario)
        REFERENCES SOULUP_USUARIO (id_usuario),
    CONSTRAINT fk_soulup_registro_acao FOREIGN KEY (id_acao)
        REFERENCES SOULUP_ACAO (id_acao),
    CONSTRAINT uk_soulup_registro UNIQUE (id_usuario, id_acao)
);

-- ---------------------------------------------------------------------
-- Dados de exemplo: catalogo (mesmos pontos da Sprint 3)
-- ---------------------------------------------------------------------
INSERT INTO SOULUP_ACAO (nome, pontos, categoria) VALUES ('Usar transporte publico', 15, 'MOBILIDADE');
INSERT INTO SOULUP_ACAO (nome, pontos, categoria) VALUES ('Reciclar lixo domestico', 10, 'RESIDUOS');
INSERT INTO SOULUP_ACAO (nome, pontos, categoria) VALUES ('Economizar energia em casa', 12, 'ENERGIA');
INSERT INTO SOULUP_ACAO (nome, pontos, categoria) VALUES ('Usar bicicleta no lugar do carro', 18, 'MOBILIDADE');
INSERT INTO SOULUP_ACAO (nome, pontos, categoria) VALUES ('Plantar uma arvore', 20, 'NATUREZA');
INSERT INTO SOULUP_ACAO (nome, pontos, categoria) VALUES ('Reduzir consumo de agua', 8, 'AGUA');
INSERT INTO SOULUP_ACAO (nome, pontos, categoria) VALUES ('Comprar produto sustentavel', 10, 'CONSUMO');
INSERT INTO SOULUP_ACAO (nome, pontos, categoria) VALUES ('Evitar plastico descartavel', 12, 'RESIDUOS');
INSERT INTO SOULUP_ACAO (nome, pontos, categoria) VALUES ('Consumir conteudo SoulUp', 5, 'ENGAJAMENTO');
INSERT INTO SOULUP_ACAO (nome, pontos, categoria) VALUES ('Compartilhar missao sustentavel', 7, 'ENGAJAMENTO');

-- ---------------------------------------------------------------------
-- Dados de exemplo: usuarios
-- ---------------------------------------------------------------------
INSERT INTO SOULUP_USUARIO (nome, email) VALUES ('Ana Silva', 'ana@email.com');
INSERT INTO SOULUP_USUARIO (nome, email) VALUES ('Bruno Costa', 'bruno@email.com');
INSERT INTO SOULUP_USUARIO (nome, email) VALUES ('Carla Souza', 'carla@email.com');
INSERT INTO SOULUP_USUARIO (nome, email) VALUES ('Diego Lima', 'diego@email.com');
INSERT INTO SOULUP_USUARIO (nome, email) VALUES ('Elisa Rocha', 'elisa@email.com');

-- ---------------------------------------------------------------------
-- Dados de exemplo: registros (busca os IDs pelo e-mail e pelo nome da acao)
-- ---------------------------------------------------------------------
INSERT INTO SOULUP_REGISTRO_ACAO (id_usuario, id_acao)
    SELECT u.id_usuario, a.id_acao FROM SOULUP_USUARIO u, SOULUP_ACAO a
    WHERE u.email = 'ana@email.com' AND a.nome IN (
        'Usar transporte publico', 'Plantar uma arvore', 'Usar bicicleta no lugar do carro',
        'Reciclar lixo domestico', 'Economizar energia em casa', 'Evitar plastico descartavel');

INSERT INTO SOULUP_REGISTRO_ACAO (id_usuario, id_acao)
    SELECT u.id_usuario, a.id_acao FROM SOULUP_USUARIO u, SOULUP_ACAO a
    WHERE u.email = 'bruno@email.com' AND a.nome IN (
        'Reciclar lixo domestico', 'Reduzir consumo de agua', 'Plantar uma arvore',
        'Usar transporte publico');

INSERT INTO SOULUP_REGISTRO_ACAO (id_usuario, id_acao)
    SELECT u.id_usuario, a.id_acao FROM SOULUP_USUARIO u, SOULUP_ACAO a
    WHERE u.email = 'carla@email.com' AND a.nome IN (
        'Consumir conteudo SoulUp', 'Compartilhar missao sustentavel', 'Reduzir consumo de agua');

INSERT INTO SOULUP_REGISTRO_ACAO (id_usuario, id_acao)
    SELECT u.id_usuario, a.id_acao FROM SOULUP_USUARIO u, SOULUP_ACAO a
    WHERE u.email = 'diego@email.com' AND a.nome IN ('Usar bicicleta no lugar do carro');

-- Elisa fica sem registros (nivel Iniciante) para demonstrar o filtro por nivel

COMMIT;

-- Conferencia rapida: ranking com JOIN + GROUP BY
SELECT u.nome, NVL(SUM(a.pontos), 0) AS pontos
  FROM SOULUP_USUARIO u
  LEFT JOIN SOULUP_REGISTRO_ACAO r ON r.id_usuario = u.id_usuario
  LEFT JOIN SOULUP_ACAO a ON a.id_acao = r.id_acao
 GROUP BY u.nome
 ORDER BY pontos DESC;
