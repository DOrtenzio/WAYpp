<?php
header('Content-Type: application/json');
require_once 'db.php'; // Include il file di connessione al database

// Prendi dati POST e decodifica JSON
$input = json_decode(file_get_contents('php://input'), true);

if (!isset($input['nuovo']['email']) || !isset($input['nuovo']['nome']) || !isset($input['nuovo']['psw']) || !isset($input['nuovo']['bio']) || !isset($input['vecchio']['email']) || !isset($input['vecchio']['nome']) || !isset($input['vecchio']['psw']) || !isset($input['vecchio']['bio'])) { //Not null
    http_response_code(400);
    echo json_encode(['confermaAzione' => false, 'parametro1' => null,'parametro2'=> null]);
    exit;
} else {
    $nuovo => [
        'email' => $input['nuovo']['email'],
        'nome' => $input['nuovo']['nome'],
        'psw' => $input['nuovo']['psw'],
        'bio' => $input['nuovo']['bio']
    ];
    $vecchio => [
        'email' => $input['vecchio']['email'],
        'nome' => $input['vecchio']['nome'],
        'psw' => $input['vecchio']['psw'],
        'bio' => $input['vecchio']['bio']
    ];
    try {
        // Hash della nuova password
        $hashedPassword = password_hash($nuovo['psw'], PASSWORD_DEFAULT);

        // Aggiorna l'utente
        $stmt = $pdo->prepare("UPDATE utenti SET nome = :nome, email = :email, password = :password, bio = :bio WHERE email = :oldEmail");
        $stmt->execute([
            'nome' => $nuovo['nome'],
            'email' => $nuovo['email'],
            'password' => $hashedPassword,
            'bio' => $nuovo['bio'],
            'oldEmail' => $vecchio['email']
        ]);

        echo json_encode([ 'confermaAzione' => true, 'parametro1' => null,'parametro2'=> null]);
    } catch (Exception $e) {
        http_response_code(500);
        echo json_encode(['confermaAzione' => false, 'parametro1' => null, 'parametro2' => null]);
    }
}
