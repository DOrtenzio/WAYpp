<?php
<?php
header('Content-Type: application/json');
require_once 'db.php';

// Prendi dati POST e decodifica JSON
$input = json_decode(file_get_contents('php://input'), true);

if (
    !isset($input['utente']['email']) ||
    !isset($input['utente']['nome']) ||
    !isset($input['utente']['psw']) ||
    !isset($input['listaViaggi']['list']) ||
    !is_array($input['listaViaggi']['list'])
) {
    http_response_code(400);
    echo json_encode(['confermaAzione' => false, 'parametro1' => null, 'parametro2' => null]);
    exit;
} else {
    $utente = [
        'email' => $input['utente']['email'],
        'nome' => $input['utente']['nome'],
        'psw' => $input['utente']['psw']
    ];
    $nuovaListaViaggi = $input['listaViaggi']['list'];

    try {
        // Cerca utente per email e nome
        $stmt = $pdo->prepare("SELECT id, nome, email, password FROM utenti WHERE email = :emailInput AND nome = :nomeInput");
        $stmt->execute(['emailInput' => $utente['email'], 'nomeInput' => $utente['nome']]);
        $utenteTrovato = $stmt->fetch(PDO::FETCH_ASSOC);

        if (!$utenteTrovato) {
            http_response_code(401);
            echo json_encode(['confermaAzione' => false, 'parametro1' => null, 'parametro2' => null]);
            exit;
        }

        // Verifica password
        if (!password_verify($utente['psw'], $utenteTrovato['password'])) {
            http_response_code(401);
            echo json_encode(['confermaAzione' => false, 'parametro1' => null, 'parametro2' => null]);
            exit;
        }

        // Elimina tutti i viaggi collegati all'utente
        $stmt = $pdo->prepare("DELETE FROM viaggi WHERE user_id = :userId");
        $stmt->execute(['userId' => $utenteTrovato['id']]);

        // Inserisci i nuovi viaggi
        $stmt = $pdo->prepare("INSERT INTO viaggi (user_id, nomeUnivoco) VALUES (:userId, :nomeUnivoco)");
        foreach ($nuovaListaViaggi as $viaggio) {
            if (isset($viaggio['nomeUnivoco'])) {
                $stmt->execute([
                    'userId' => $utenteTrovato['id'],
                    'nomeUnivoco' => $viaggio['nomeUnivoco']
                ]);
            }
        }

        // Recupera la nuova lista viaggi aggiornata
        $stmt = $pdo->prepare("SELECT id, nomeUnivoco FROM viaggi WHERE user_id = :userId");
        $stmt->execute(['userId' => $utenteTrovato['id']]);
        $listaViaggi = $stmt->fetchAll(PDO::FETCH_ASSOC);

        // Restituisci la lista come JSON
        echo json_encode(['confermaAzione' => true, 'parametro1' => $listaViaggi, 'parametro2' => null]);
        exit;

    } catch (Exception $e) {
        http_response_code(500);
        echo json_encode(['confermaAzione' => false, 'parametro1' => null, 'parametro2' => null]);
    }
}