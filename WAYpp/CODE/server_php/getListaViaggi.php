<?php
header('Content-Type: application/json');
require_once 'db.php'; // Include il file di connessione al database

// Prendi dati POST e decodifica JSON
$input = json_decode(file_get_contents('php://input'), true);

if (!isset($input['email']) || !isset($input['nome']) || !isset($input['psw'])) { //Not null
    http_response_code(400);
    echo json_encode(['confermaAzione' => false, 'parametro1' => null,'parametro2'=> null]);
    exit;
} else {
    $utente => [
        'email' => $input['email'],
        'nome' => $input['nome'],
        'psw' => $input['psw']
    ];
    try {
        // Hash della password
        $hashedPassword = password_hash($utente['psw'], PASSWORD_DEFAULT);
        
        // Cerca utente per email
        $stmt = $pdo->prepare("SELECT id, nome, email, password FROM utenti WHERE email = :emailInput AND nome = :nomeInput");
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

        // Recupera la lista viaggi collegata all'utente
        $stmt = $pdo->prepare("SELECT id, nomeUnivoco FROM viaggi WHERE user_id = :userId");
        $stmt->execute(['userId' => $utenteTrovato['id']]);
        $listaViaggi = $stmt->fetchAll(PDO::FETCH_ASSOC);

        // Restituisci la lista come JSON
        echo json_encode(['confermaAzione' => true, 'parametro1' => $listaViaggi,'parametro2'=> null]);
        exit;

    } catch (Exception $e) {
        http_response_code(500);
        echo json_encode(['confermaAzione' => false, 'parametro1' => null,'parametro2'=> null]);
    }
}
