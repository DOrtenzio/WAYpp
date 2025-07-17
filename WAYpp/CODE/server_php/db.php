<?php
// db.php
$host = 'localhost';
$dbname = 'test2';
$user = 'test';
$pass = 'Francesco25@'; //Ovviamente l'utente sarà eliminato e le vere credenziali non le pubblicherò qui

try {
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8mb4", $user, $pass); //Oggetto PDO per la connessione al database
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION); // Imposta il modo di gestione degli errori
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode(['Errore Db' => true, 'message' => 'Errore di connessione DB']);
    exit;
}
