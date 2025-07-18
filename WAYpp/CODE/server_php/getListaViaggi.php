<?php
header('Content-Type: application/json');
require_once 'db.php'; // Include il file di connessione al database
require 'function.php';

// Prendi dati POST e decodifica JSON
$input = json_decode(file_get_contents('php://input'), true);

if (!isset($input['email']) || !isset($input['nome']) || !isset($input['psw'])) { //Not null
    http_response_code(400);
    echo json_encode(['confermaAzione' => false, 'parametro1' => null,'parametro2'=> null]);
    exit;
} else {
    $utente = [
        'email' => $input['email'],
        'nome' => $input['nome'],
        'psw' => $input['psw']
    ];
    try {
        // Hash della password
        $hashedPassword = password_hash($utente['psw'], PASSWORD_DEFAULT);
        
        // Cerca utente per email
        $stmt = $pdo->prepare("SELECT * FROM utenti WHERE email = :emailInput AND nome = :nomeInput");
        $stmt->execute(['emailInput' => $utente['email'], 'nomeInput' => $utente['nome']]);
        $utenteTrovato = $stmt->fetch(PDO::FETCH_ASSOC);

        if (!$utenteTrovato) {
            http_response_code(401);
            echo json_encode(['confermaAzione' => false, 'parametro1' => null,'parametro2'=> null]);
            exit;
        }

        // Verifica password
        if (!password_verify($utente['psw'], $utenteTrovato['password'])) {
            http_response_code(401);
            echo json_encode(['confermaAzione' => false, 'parametro1' => null,'parametro2'=> null]);
            exit;
        }

        $userId = $utenteTrovato['id']; // <-- qui definisci userId

        // Restituisci la lista come JSON
        $viaggi = getViaggiCompleti($pdo, $userId);
        header('Content-Type: application/json');
        echo json_encode(['confermaAzione' => true, 'parametro1' => $viaggi,'parametro2'=> null]);
        exit;


    } catch (Exception $e) {
        http_response_code(500);
        echo json_encode(['confermaAzione' => false, 'parametro1' => null,'parametro2'=> null]);
    }
}
