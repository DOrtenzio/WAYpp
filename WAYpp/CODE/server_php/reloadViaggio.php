<?php
header('Content-Type: application/json');
require_once 'db.php';
require 'function.php';

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
        'budgetIniziale' => $input['p2']['budget']['budgetIniziale'],
        'budgetSpeso' => $input['p2']['budget']['budgetSpeso'],
        'tieniConto' => $input['p2']['budget']['tieniConto'],
        'tappe' => $input['p2']['itinerario']['tappe'],
        'elementiTot' => $input['p2']['listaElementi']['elementiTot'],
        'elementiAcquistati' => $input['p2']['listaElementi']['elementiAcquistati']
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
        $userId = $utenteTrovato['id'];
        $stmt = $pdo->prepare("SELECT * FROM viaggi WHERE user_id = :userId AND nomeUnivoco = :nomeUnivoco");
        $stmt->execute(['userId' => $utenteTrovato['id'], 'nomeUnivoco' => $nuovoViaggio['nomeUnivoco']]);
        $viaggioTrovato = $stmt->fetch(PDO::FETCH_ASSOC);

        //Aggiorna il viaggio
        if ($viaggioTrovato) {
           // Dopo aver verificato l'utente e trovato il viaggio
           $viaggioId = $viaggioTrovato['id'];

           // 1. Aggiorna budget
           $stmt = $pdo->prepare("SELECT id FROM budget WHERE viaggio_id = :id");
           $stmt->execute(['id' => $viaggioId]);
           $budget = $stmt->fetch(PDO::FETCH_ASSOC);

           if ($budget) {
               $stmt = $pdo->prepare("UPDATE budget SET budget_iniziale = :iniziale, budget_speso = :speso WHERE id = :id");
               $stmt->execute([
                   'iniziale' => $input['p2']['budget']['budgetIniziale'],
                   'speso' => $input['p2']['budget']['budgetSpeso'],
                   'id' => $budget['id']
               ]);

               // Elimina vecchie rendicontazioni
               $pdo->prepare("DELETE FROM rendicontazioni WHERE budget_id = :id")->execute(['id' => $budget['id']]);

               // Inserisci nuove rendicontazioni
               $stmtR = $pdo->prepare("INSERT INTO rendicontazioni (budget_id, motivazione, cifra) VALUES (:budget_id, :motivazione, :cifra)");
               foreach ($input['p2']['budget']['tieniConto'] as $r) {
                   $stmtR->execute([
                       'budget_id' => $budget['id'],
                       'motivazione' => $r['motivazione'] ?? '',
                       'cifra' => $r['cifra'] ?? 0
                   ]);
               }
           }

           // 2. Aggiorna itinerario
           $stmt = $pdo->prepare("SELECT id FROM itinerario WHERE viaggio_id = :id");
           $stmt->execute(['id' => $viaggioId]);
           $itinerario = $stmt->fetch(PDO::FETCH_ASSOC);

           if ($itinerario) {
               // Cancella vecchie tappe
               $pdo->prepare("DELETE FROM tappe WHERE itinerario_id = :id")->execute(['id' => $itinerario['id']]);

               // AGGIORNA il nome dell'itinerario
               $stmtUpdateIt = $pdo->prepare("UPDATE itinerario SET nome = :nome WHERE id = :id");
               $stmtUpdateIt->execute([
                   'nome' => $input['p2']['itinerario']['nome'] ?? '',
                   'id' => $itinerario['id']
               ]);

               // Inserisci nuove tappe
               $stmtT = $pdo->prepare("INSERT INTO tappe (nome_tappa, data, latitudine, longitudine, itinerario_id) VALUES (:nome, :data, :lat, :lon, :it_id)");
               foreach ($input['p2']['itinerario']['tappe'] as $tappa) {
                   $dataTappa = null;
                   if (isset($tappa['data'])) {
                       if (is_array($tappa['data'])) {
                           // Qui gestiamo il formato [anno, mese, giorno]
                           if (count($tappa['data']) === 3 &&
                               is_numeric($tappa['data'][0]) && // Anno
                               is_numeric($tappa['data'][1]) && // Mese
                               is_numeric($tappa['data'][2])) { // Giorno
                               $dataTappa = sprintf('%04d-%02d-%02d', $tappa['data'][0], $tappa['data'][1], $tappa['data'][2]);
                           }
                       } elseif (is_string($tappa['data'])) {
                          // Se per qualche motivo arriva già come stringa (es. "2024-07-21")
                          $timestamp = strtotime($tappa['data']);
                          if ($timestamp !== false) {
                              $dataTappa = date('Y-m-d', $timestamp);
                          }
                       }
                   }

                   $stmtT->execute([
                       'nome' => $tappa['nome'] ?? '',
                       'data' => $dataTappa, // Usa la data formattata o null
                       'lat' => $tappa['latitudine'] ?? null,
                       'lon' => $tappa['longitudine'] ?? null,
                       'it_id' => $itinerario['id']
                   ]);
               }
           }

           // 3. Aggiorna listaElementi
           $stmt = $pdo->prepare("SELECT id FROM listaElementi WHERE viaggio_id = :id");
           $stmt->execute(['id' => $viaggioId]);
           $lista = $stmt->fetch(PDO::FETCH_ASSOC);

           if ($lista) {
               $stmt = $pdo->prepare("UPDATE listaElementi SET elementiTot = :tot, elementiAcquistati = :acq WHERE id = :id");
               $stmt->execute([
                   'tot' => $input['p2']['listaElementi']['elementiTot'],
                   'acq' => $input['p2']['listaElementi']['elementiAcquistati'],
                   'id' => $lista['id']
               ]);

               $pdo->prepare("DELETE FROM elementi WHERE lista_elementi_id = :id")->execute(['id' => $lista['id']]);

               $stmtEl = $pdo->prepare("INSERT INTO elementi (nome, descrizione, luogoAcquisto, isAcquistato, quantita, lista_elementi_id) VALUES (:nome, :desc, :luogo, :acq, :quant, :lista_id)");
               foreach ($input['p2']['listaElementi']['list'] as $el) {
                   $stmtEl->execute([
                       'nome' => $el['nome'] ?? '',
                       'desc' => $el['descrizione'] ?? '',
                       'luogo' => $el['luogoAcquisto'] ?? '',
                       'acq' => $el['isAcquistato'] ? 1 : 0,
                       'quant' => $el['quantita'] ?? 0,
                       'lista_id' => $lista['id']
                   ]);
               }
           }

           $viaggi = getViaggiCompleti($pdo, $userId);
           header('Content-Type: application/json');
           echo json_encode(['confermaAzione' => true, 'parametro1' => $viaggi,'parametro2'=> null]);
           exit;


        } else
            echo json_encode(['confermaAzione' => false, 'parametro1' => null, 'parametro2' => null]);
        exit;

    } catch (Exception $e) {
        http_response_code(500);
        echo json_encode(['confermaAzione' => false, 'parametro1' => null, 'parametro2' => null]);
    }
}