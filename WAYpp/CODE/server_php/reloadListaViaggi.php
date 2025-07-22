<?php
header('Content-Type: application/json');
require 'db.php';
require 'function.php';

// Prendi dati POST e decodifica JSON
$input = json_decode(file_get_contents('php://input'), true);

if ( !isset($input['p1']['email']) || !isset($input['p1']['nome']) || !isset($input['p1']['psw']) || !isset($input['p2']['list']) ) {
    http_response_code(400);
    echo json_encode(['confermaAzione' => false, 'parametro1' => null, 'parametro2' => null]);
    exit;
} else {
    $utente = [
        'email' => $input['p1']['email'],
        'nome' => $input['p1']['nome'],
        'psw' => $input['p1']['psw']
    ];
    $nuovaListaViaggi = $input['p2']['list'];

    try {
        // Cerca utente per email e nome
        $stmt = $pdo->prepare("SELECT * FROM utenti WHERE email = :emailInput AND nome = :nomeInput");
        $stmt->execute(['emailInput' => $utente['email'], 'nomeInput' => $utente['nome']]);
        $utenteTrovato = $stmt->fetch(PDO::FETCH_ASSOC);

        if (!$utenteTrovato) {
            http_response_code(401);
            echo json_encode(['confermaAzione' => false, 'parametro1' => 'no user', 'parametro2' => null]);
            exit;
        }

        // Verifica password
        if (!password_verify($utente['psw'], $utenteTrovato['password'])) {
            http_response_code(401);
            echo json_encode(['confermaAzione' => false, 'parametro1' => 'no psw', 'parametro2' => null]);
            exit;
        }

        // Elimina tutti i viaggi collegati all'utente
        $stmt = $pdo->prepare("DELETE FROM viaggi WHERE user_id = :userId");
        $stmt->execute(['userId' => $utenteTrovato['id']]);

        // Inserisci i nuovi viaggi
        $stmtViaggio = $pdo->prepare("INSERT INTO viaggi (user_id, nomeUnivoco, mezzoUsato, obiettivo) VALUES (:userId, :nomeUnivoco, :mezzoUsato, :obiettivo)");
        $userId = $utenteTrovato['id'];
        foreach ($nuovaListaViaggi as $viaggio) {
            if (!isset($viaggio['nomeUnivoco'])) continue;
            if (!isset($viaggio['mezzoUsato'])) continue;
            if (!isset($viaggio['obiettivo'])) continue;

            // 1. Inserisci viaggio
            $stmtViaggio->execute([
                'userId' => $utenteTrovato['id'],
                'nomeUnivoco' => $viaggio['nomeUnivoco'],
                'mezzoUsato'=>$viaggio['mezzoUsato'],
                'obiettivo'=>$viaggio['obiettivo']
            ]);
            $viaggioId = $pdo->lastInsertId();

            // 2. Inserisci budget
            if (isset($viaggio['budget'])) {
                $stmtBudget = $pdo->prepare("INSERT INTO budget (viaggio_id, budget_iniziale, budget_speso) VALUES (:viaggio_id, :iniziale, :speso)");
                $stmtBudget->execute([
                    'viaggio_id' => $viaggioId,
                    'iniziale' => $viaggio['budget']['budgetIniziale'] ?? 0,
                    'speso' => $viaggio['budget']['budgetSpeso'] ?? 0
                ]);
                $budgetId = $pdo->lastInsertId();

                // rendicontazioni (se presenti)
                if (!empty($viaggio['budget']['tieniConto'])) {
                    $stmtRendiconto = $pdo->prepare("INSERT INTO rendicontazioni (budget_id, motivazione, cifra) VALUES (:budget_id, :motivazione, :cifra)");
                    foreach ($viaggio['budget']['tieniConto'] as $r) {
                        $stmtRendiconto->execute([
                            'budget_id' => $budgetId,
                            'motivazione' => $r['motivazione'] ?? '',
                            'cifra' => $r['cifra'] ?? 0
                        ]);
                    }
                }
            }

            // 3. Inserisci itinerario e tappe
            if (isset($viaggio['itinerario'])) {
                $stmtItinerario = $pdo->prepare("INSERT INTO itinerario (viaggio_id, nome) VALUES (:viaggio_id, :nome)");
                $stmtItinerario->execute([
                    'viaggio_id' => $viaggioId,
                    'nome' => $viaggio['itinerario']['nome'] ?? ''
                ]);
                $itinerarioId = $pdo->lastInsertId();

                // Dichiara e prepara $stmtTappa qui, PRIMA del ciclo sulle tappe
                $stmtTappa = $pdo->prepare("INSERT INTO tappe (nome_tappa, data, latitudine, longitudine, itinerario_id) VALUES (:nome, :data, :lat, :lon, :it_id)");

                if (!empty($viaggio['itinerario']['tappe'])) {
                    foreach ($viaggio['itinerario']['tappe'] as $tappa) {
                        $dataTappa = null;
                        if (isset($tappa['data'])) {
                            if (is_array($tappa['data'])) {
                                if (count($tappa['data']) === 3 &&
                                    is_numeric($tappa['data'][0]) && // Anno
                                    is_numeric($tappa['data'][1]) && // Mese
                                    is_numeric($tappa['data'][2])) { // Giorno
                                    $dataTappa = sprintf('%04d-%02d-%02d', $tappa['data'][0], $tappa['data'][1], $tappa['data'][2]);
                                }
                            } elseif (is_string($tappa['data'])) {
                                $timestamp = strtotime($tappa['data']);
                                if ($timestamp !== false) {
                                    $dataTappa = date('Y-m-d', $timestamp);
                                }
                            }
                        }

                        // Esegui la query per la tappa
                        $stmtTappa->execute([
                            'nome' => $tappa['nome'] ?? 'Senza nome',
                            'data' => $dataTappa, // Usa la data formattata o null
                            'lat' => $tappa['latitudine'] ?? null,
                            'lon' => $tappa['longitudine'] ?? null,
                            'it_id' => $itinerarioId
                        ]);
                    }
                }
            }

            // 4. Inserisci lista elementi e elementi
            if (isset($viaggio['listaElementi'])) {
                $stmtLista = $pdo->prepare("INSERT INTO listaElementi (viaggio_id, elementiTot, elementiAcquistati) VALUES (:viaggio_id, :tot, :acq)");
                $stmtLista->execute([
                    'viaggio_id' => $viaggioId,
                    'tot' => $viaggio['listaElementi']['elementiTot'] ?? 0,
                    'acq' => $viaggio['listaElementi']['elementiAcquistati'] ?? 0
                ]);
                $listaId = $pdo->lastInsertId();

                if (!empty($viaggio['listaElementi']['list'])) {
                    $stmtElemento = $pdo->prepare("INSERT INTO elementi (nome, descrizione, luogoAcquisto, isAcquistato, lista_elementi_id) VALUES (:nome, :desc, :luogo, :acq, :lista_id)");
                    foreach ($viaggio['listaElementi']['list'] as $el) {
                        $stmtElemento->execute([
                            'nome' => $el['nome'] ?? '',
                            'desc' => $el['descrizione'] ?? '',
                            'luogo' => $el['luogoAcquisto'] ?? '',
                            'acq' => $el['isAcquistato'] ? 1 : 0,
                            'lista_id' => $listaId
                        ]);
                    }
                }
            }
        }

        // Recupera tutti i viaggi dell'utente
        $viaggi = getViaggiCompleti($pdo, $userId);
        header('Content-Type: application/json');
        echo json_encode(['confermaAzione' => true, 'parametro1' => $viaggi,'parametro2'=> null]);
        exit;


    } catch (Exception $e) {
        http_response_code(500);
        echo json_encode(['confermaAzione' => false, 'parametro1' => $e->getMessage(), 'parametro2' => null]);
    }
}