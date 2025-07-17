<?php
header('Content-Type: application/json');
require_once 'db.php';

// Prendi dati POST e decodifica JSON
$input = json_decode(file_get_contents('php://input'), true);

if ((!isset($input['p1']['email'])) || (!isset($input['p1']['nome'])) || (!isset($input['p1']['psw'])) || (!isset($input['p2']['nomeUnivoco'])) || (!isset($input['p2']['budget'])) || (!isset($input['p2']['itinerario'])) || (!isset($input['p2']['listaElementi']))) {
    http_response_code(400);
    echo json_encode(['confermaAzione' => false, 'parametro1' => null, 'parametro2' => null]);
    exit;
} else {
    $utente = [
        'email' => $input['p1']['email'],
        'nome' => $input['p1']['nome'],
        'psw' => $input['p1']['psw']
    ];
    $nuovoViaggio = [
        'nomeUnivoco' => $input['p2']['nomeUnivoco'],
        'budgetIniziale' => $input['budget']['budgetIniziale'],
        'budgetSpeso' => $input['budget']['budgetSpeso'],
        'tieniConto' => $input['budget']['tieniConto'],
        'listaBudget' => $input['budget']['lista'],
        'tappe' => $input['itinerario']['tappe'],
        'dateTappe' => $input['itinerario']['dateTappe'],
        'elementiTot' => $input['listaElementi']['elementiTot'],
        'elementiAcquistati' => $input['listaElementi']['elementiAcquistati']
    ];

    try {
        // Cerca utente per email e nome
        $stmt = $pdo->prepare("SELECT * FROM utenti WHERE email = :emailInput AND nome = :nomeInput");
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

        // Ricerca il viaggio per nome univoco
        $stmt = $pdo->prepare("SELECT * FROM viaggi WHERE user_id = :userId AND nomeUnivoco = :nomeUnivoco");
        $stmt->execute(['userId' => $utenteTrovato['id'], 'nomeUnivoco' => $nuovoViaggio['nomeUnivoco']]);
        $viaggioTrovato = $stmt->fetch(PDO::FETCH_ASSOC);

        //Aggiorna il viaggio
        if ($viaggioTrovato) {
            $stmt = $pdo->prepare("UPDATE viaggi SET budgetIniziale = :budgetIniziale, budgetSpeso = :budgetSpeso, tieniConto = :tieniConto, listaBudget = :listaBudget, tappe = :tappe, dateTappe = :dateTappe, elementiTot = :elementiTot, elementiAcquistati = :elementiAcquistati WHERE id = :viaggioId");
            $stmt->execute([
                'budgetIniziale' => $nuovoViaggio['budgetIniziale'],
                'budgetSpeso' => $nuovoViaggio['budgetSpeso'],
                'tieniConto' => $nuovoViaggio['tieniConto'],
                'listaBudget' => json_encode($nuovoViaggio['listaBudget']),
                'tappe' => json_encode($nuovoViaggio['tappe']),
                'dateTappe' => json_encode($nuovoViaggio['dateTappe']),
                'elementiTot' => json_encode($nuovoViaggio['elementiTot']),
                'elementiAcquistati' => json_encode($nuovoViaggio['elementiAcquistati'])
            ]);
            echo json_encode(['confermaAzione' => true, 'parametro1' => null, 'parametro2' => null]);
        } else
            echo json_encode(['confermaAzione' => false, 'parametro1' => null, 'parametro2' => null]);
        exit;

    } catch (Exception $e) {
        http_response_code(500);
        echo json_encode(['confermaAzione' => false, 'parametro1' => null, 'parametro2' => null]);
    }
}