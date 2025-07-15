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
  `user_id` int
);

CREATE TABLE `budget` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `viaggio_id` int,
  `budget_iniziale` double,
  `budget_speso` double
);

CREATE TABLE `rendicontazioni` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `budget_id` int,
  `motivazione` varchar(255),
  `cifra` double
);

CREATE TABLE `itinerario` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `viaggio_id` int
);

CREATE TABLE `tappe` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `nome_tappa` varchar(255),
  `data` date,
  `latitudine` double,
  `longitudine` double,
  `itinerario_id` int
);

CREATE TABLE `listaElementi` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `viaggio_id` int,
  `elementiTot` int,
  `elementiAcquistati` int
);

CREATE TABLE `elementi` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `nome` varchar(255),
  `descrizione` text,
  `luogoAcquisto` varchar(255),
  `isAcquistato` boolean,
  `lista_elementi_id` int
);

ALTER TABLE `viaggi` ADD FOREIGN KEY (`user_id`) REFERENCES `utenti` (`id`);

ALTER TABLE `budget` ADD FOREIGN KEY (`viaggio_id`) REFERENCES `viaggi` (`id`);

ALTER TABLE `rendicontazioni` ADD FOREIGN KEY (`budget_id`) REFERENCES `budget` (`id`);

ALTER TABLE `itinerario` ADD FOREIGN KEY (`viaggio_id`) REFERENCES `viaggi` (`id`);

ALTER TABLE `tappe` ADD FOREIGN KEY (`itinerario_id`) REFERENCES `itinerario` (`id`);

ALTER TABLE `listaElementi` ADD FOREIGN KEY (`viaggio_id`) REFERENCES `viaggi` (`id`);

ALTER TABLE `elementi` ADD FOREIGN KEY (`lista_elementi_id`) REFERENCES `listaElementi` (`id`);
