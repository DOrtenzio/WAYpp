CREATE DATABASE IF NOT EXISTS test2;
USE test2;

CREATE TABLE `utenti` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `nome` varchar(255),
  `email` varchar(255) UNIQUE,
  `bio` text,
  `password` varchar(255)
);

CREATE TABLE `viaggi` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `nomeUnivoco` varchar(255) UNIQUE,
  `user_id` int,
  FOREIGN KEY (user_id) REFERENCES utenti(id) ON DELETE CASCADE
);

CREATE TABLE `budget` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `viaggio_id` int,
  `budget_iniziale` double,
  `budget_speso` double,
  FOREIGN KEY (viaggio_id) REFERENCES viaggi(id) ON DELETE CASCADE
);

CREATE TABLE `rendicontazioni` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `budget_id` int,
  `motivazione` varchar(255),
  `cifra` double,
  FOREIGN KEY (budget_id) REFERENCES budget(id) ON DELETE CASCADE
);

CREATE TABLE `itinerario` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `viaggio_id` int,
  `nome` varchar(255),
  FOREIGN KEY (viaggio_id) REFERENCES viaggi(id) ON DELETE CASCADE
);

CREATE TABLE `tappe` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `nome_tappa` varchar(255),
  `data` date,
  `latitudine` double,
  `longitudine` double,
  `itinerario_id` int,
  FOREIGN KEY (itinerario_id) REFERENCES itinerario(id) ON DELETE CASCADE
);

CREATE TABLE `listaElementi` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `viaggio_id` int,
  `elementiTot` int,
  `elementiAcquistati` int,
  FOREIGN KEY (viaggio_id) REFERENCES viaggi(id) ON DELETE CASCADE
);

CREATE TABLE `elementi` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `nome` varchar(255),
  `descrizione` text,
  `luogoAcquisto` varchar(255),
  `isAcquistato` boolean,
  `lista_elementi_id` int,
  FOREIGN KEY (lista_elementi_id) REFERENCES listaElementi(id) ON DELETE CASCADE
);
