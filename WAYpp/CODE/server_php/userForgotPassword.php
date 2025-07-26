<?php
header('Content-Type: application/json');
require_once 'db.php'; // Include il file di connessione al database

// Prendi dati POST e decodifica JSON
$input = json_decode(file_get_contents('php://input'), true);

if (!isset($input['email']) || !isset($input['password'])) { //Not null
    http_response_code(400);
    echo json_encode(['nome' => null, 'email' => null,'bio'=> null, 'psw'=> null]); // Risposta con dati mancanti
    exit;
} else {
    $emailInput = $input['email'];
    $passwordInput = $input['password'];

    try {
        // Cerca utente per email
        $stmt = $pdo->prepare("SELECT * FROM utenti WHERE email = :emailInput");
        $stmt->execute(['emailInput' => $emailInput]); //Sostituisce il placeholder con l'input
        $utente = $stmt->fetch(PDO::FETCH_ASSOC);

        if (!$utente) { // Se l'utente non esiste
            http_response_code(401);
            echo json_encode(['nome' => null, 'email' => null,'bio'=> null, 'psw'=> null]); // Risposta con dati mancanti
            exit;
        }

        $hashedPassword = password_hash($passwordInput, PASSWORD_DEFAULT);

        // Aggiorna l'utente
        $stmt = $pdo->prepare("UPDATE utenti SET password = :pwd WHERE id = :id");
        $stmt->execute([
            'pwd' => $hashedPassword,
            'id'  => $utente['id']
        ]);

        // OK
        echo json_encode([ 'nome' => $utente['nome'], 'email' => $utente['email'], 'psw' => $utente['password'], 'bio' => $utente['bio'] ]);
    } catch (Exception $e) {
        http_response_code(500);
        echo json_encode(['nome' => null, 'email' => null,'bio'=> null, 'psw'=> null]); // Risposta con dati mancanti
    }
}
