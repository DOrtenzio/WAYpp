<?php
// db.php
$host = 'localhost';
$dbname = 'tuo_database';
$user = 'tuo_utente';
$pass = 'tua_password';

try {
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8mb4", $user, $pass); //Oggetto PDO per la connessione al database
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION); // Imposta il modo di gestione degli errori
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode(['success' => false, 'message' => 'Errore di connessione DB']);
    exit;
}
