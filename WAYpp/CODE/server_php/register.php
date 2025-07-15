<?php
header('Content-Type: application/json');
require_once 'db.php'; // Include il file di connessione al database

// Prendi dati POST e decodifica JSON
$input = json_decode(file_get_contents('php://input'), true);

if (!isset($input['email']) || !isset($input['nome']) || !isset($input['psw'])) { //Not null
    http_response_code(400);
    echo json_encode(['nome' => null, 'email' => null,'bio'=> null, 'psw'=> null]); // Risposta con dati mancanti
    exit;
} else {
    $emailInput = $input['email'];
    $nomeInput = $input['nome'];
    $passwordInput = $input['psw'];
    $bioInput = 'Sto Usando WAYpp'; // Valore predefinito per la bio


    try {
        // Controlla se l'utente esiste già
        $stmt = $pdo->prepare("SELECT email FROM utenti WHERE email = :emailInput");
        $stmt->execute(['emailInput' => $emailInput]);
        if ($stmt->fetch()) {
            http_response_code(409); // Conflitto: l'utente esiste già
            echo json_encode(['nome' => null, 'email' => null,'bio'=> null, 'psw'=> null]);
            exit;
        }

        // Hash della password
        $hashedPassword = password_hash($passwordInput, PASSWORD_DEFAULT);

        // Inserisci il nuovo utente
        $stmt = $pdo->prepare("INSERT INTO utenti (nome, email, password, bio) VALUES (:nome, :email, :password, :bio)");
        $stmt->execute([
            'nome' => $nomeInput,
            'email' => $emailInput,
            'password' => $hashedPassword,
            'bio' => $bioInput
        ]);

        echo json_encode([
            'user' => ['nome' => $nomeInput, 'email' => null,'bio'=> null, 'psw'=> null]
        ]);
    } catch (Exception $e) {
        http_response_code(500); // Errore interno del server
        echo json_encode(['nome' => null, 'email' => null,'bio'=> null, 'psw'=> null]); // Risposta con dati mancanti
    }
}
