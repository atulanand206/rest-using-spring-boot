CREATE TABLE IF NOT EXISTS users
(
  id            UUID         NOT NULL,
  name          VARCHAR(100) NOT NULL,
  phone         VARCHAR(15)  NOT NULL,
  email         VARCHAR(50)  NOT NULL,
  administrator BOOLEAN      NOT NULL,
  CONSTRAINT users_pkey PRIMARY KEY (id)
);