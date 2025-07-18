<?php
function getViaggiCompleti($pdo, $userId) {
    $stmt = $pdo->prepare("SELECT * FROM viaggi WHERE user_id = :userId");
    $stmt->execute(['userId' => $userId]);
    $viaggiBase = $stmt->fetchAll(PDO::FETCH_ASSOC);

    $viaggiCompleti = [];

    foreach ($viaggiBase as $v) {
        $viaggioId = $v['id'];

        // Recupera budget
        $stmt = $pdo->prepare("SELECT * FROM budget WHERE viaggio_id = :vid");
        $stmt->execute(['vid' => $viaggioId]);
        $budget = $stmt->fetch(PDO::FETCH_ASSOC);

        $rendicontazioni = [];
        if ($budget) {
            $stmtR = $pdo->prepare("SELECT motivazione, cifra FROM rendicontazioni WHERE budget_id = :bid");
            $stmtR->execute(['bid' => $budget['id']]);
            $rendicontazioni = $stmtR->fetchAll(PDO::FETCH_ASSOC);
        }

        // Recupera itinerario
        $stmt = $pdo->prepare("SELECT * FROM itinerario WHERE viaggio_id = :vid");
        $stmt->execute(['vid' => $viaggioId]);
        $itinerario = $stmt->fetch(PDO::FETCH_ASSOC);

        $tappe = [];
        $dateTappe = [];
        if ($itinerario) {
            $stmtT = $pdo->prepare("SELECT * FROM tappe WHERE itinerario_id = :iid");
            $stmtT->execute(['iid' => $itinerario['id']]);
            $tappeData = $stmtT->fetchAll(PDO::FETCH_ASSOC);
            foreach ($tappeData as $t) {
                $tappe[] = [
                    'nome' => $t['nome_tappa'],
                    'latitudine' => $t['latitudine'],
                    'longitudine' => $t['longitudine']
                ];
                $dateTappe[] = $t['data'];
            }
        }

        // Recupera listaElementi
        $stmt = $pdo->prepare("SELECT * FROM listaElementi WHERE viaggio_id = :vid");
        $stmt->execute(['vid' => $viaggioId]);
        $lista = $stmt->fetch(PDO::FETCH_ASSOC);

        $elementi = [];
        if ($lista) {
            $stmtE = $pdo->prepare("SELECT * FROM elementi WHERE lista_elementi_id = :lid");
            $stmtE->execute(['lid' => $lista['id']]);
            $elementi = $stmtE->fetchAll(PDO::FETCH_ASSOC);
        }

        // Componi il viaggio completo
        $viaggiCompleti[] = [
            'nomeUnivoco' => $v['nomeUnivoco'],
            'budget' => $budget ? [
                'budgetIniziale' => $budget['budget_iniziale'],
                'budgetSpeso' => $budget['budget_speso'],
                'tieniConto' => $rendicontazioni,
                'lista' => "" // eventualmente ricostruibile
            ] : null,
            'itinerario' => $itinerario ? [
                'nome' => $itinerario['nome'] ?? '',
                'tappe' => $tappe,
                'dateTappe' => $dateTappe
            ] : null,
            'listaElementi' => $lista ? [
                'list' => $elementi,
                'elementiTot' => $lista['elementiTot'],
                'elementiAcquistati' => $lista['elementiAcquistati']
            ] : null
        ];
    }

    return $viaggiCompleti;
}
