CREATE TABLE language (
  id              BIGINT        NOT NULL PRIMARY KEY,
  cd              CHAR(2)       NOT NULL,
  description     VARCHAR(50)
);

CREATE TABLE author (
  id              BIGINT        NOT NULL PRIMARY KEY,
  first_name      VARCHAR(50),
  last_name       VARCHAR(50)   NOT NULL,
  date_of_birth   DATE,
  year_of_birth   BIGINT,
  distinguished   BOOL
);

CREATE TABLE book (
  id              BIGINT        NOT NULL PRIMARY KEY,
  author_id       BIGINT        NOT NULL,
  title           VARCHAR(400)  NOT NULL,
  published_in    BIGINT        NOT NULL,
  language_id     BIGINT        NOT NULL,

  CONSTRAINT fk_book_author     FOREIGN KEY (author_id)   REFERENCES author(id),
  CONSTRAINT fk_book_language   FOREIGN KEY (language_id) REFERENCES language(id)
);

CREATE TABLE book_store (
  name            VARCHAR(400)  NOT NULL UNIQUE
);

CREATE TABLE book_to_book_store (
  name            VARCHAR(400)  NOT NULL,
  book_id         INTEGER       NOT NULL,
  stock           INTEGER,

  PRIMARY KEY(name, book_id),
  CONSTRAINT fk_b2bs_book_store FOREIGN KEY (name)        REFERENCES book_store (name) ON DELETE CASCADE,
  CONSTRAINT fk_b2bs_book       FOREIGN KEY (book_id)     REFERENCES book (id)         ON DELETE CASCADE
);