# SORRY DON'T LOOK UNDER HERE WE'RE WORKING

# WAYpp - Il Tuo Compagno di Viaggio Intelligente

[![GitHub license](https://img.shields.io/badge/license-MIT-blue.svg)](https://github.com/your-repo/your-project/blob/main/LICENSE)
[![GitHub contributors](https://img.shields.io/github/contributors/your-repo/your-project)](https://github.com/your-repo/your-project/graphs/contributors)
[![JavaFX](https://img.shields.io/badge/UI-JavaFX-brightgreen.svg)](https://openjfx.io/)
[![PHP](https://img.shields.io/badge/Backend-PHP-blueviolet.svg)](https://www.php.net/)
[![MySQL](https://img.shields.io/badge/Database-MySQL-orange.svg)](https://www.mysql.com/)

---

## 🗺️ Panoramica del Progetto

**WAYpp** è un'applicazione desktop non innovativa progettata per **semplificare la pianificazione e la programmazione dei tuoi viaggi**. Sfruttando la potenza del **cloud**, offre un'esperienza utente fluida e integrata, combinando un'interfaccia intuitiva con funzionalità intelligenti per aiutarti in ogni fase del tuo viaggio. Che tu stia sognando un'avventura esotica o organizzando una breve gita, WAYpp è qui per trasformare le tue idee in realtà.

L'intera architettura si basa su un'infrastruttura client-server:
* **Frontend:** Realizzato con **JavaFX**, offre un'interfaccia utente moderna e responsiva.
* **Backend:** Costituito da un **server PHP** che espone diverse API RESTful per gestire la logica di business.
* **Database:** Un database **MySQL relazionale** progettato ad hoc per garantire l'integrità e l'efficienza dei dati.

---

## ✨ Caratteristiche Principali

WAYpp è ricco di funzionalità pensate per il viaggiatore moderno:

* **Autenticazione Completa:** Gestione di login, registrazione, recupero password e modifica delle informazioni utente.
* **Esplora Viaggi:** Scopri viaggi standard o lasciati ispirare da **viaggi esotici generati dall'IA (SALVINO)**, personalizzati in base ai tuoi gusti. Puoi scegliere, modificare e fare tuoi questi itinerari.
    * **TODO: Inserisci uno screenshot della sezione "Esplora Viaggi" qui.**
* **Gestione Viaggi:** Una sezione dedicata per visualizzare, aggiungere, modificare ed eliminare i tuoi viaggi personali.
    * **TODO: Inserisci uno screenshot della sezione "Gestione Viaggi" qui.**
* **Dettaglio Viaggio:** Ogni viaggio è suddiviso in tre sezioni fondamentali:
    * **💰 Budget:** Tieni sotto controllo le tue spese con un resoconto dettagliato per rimanere sempre nel budget.
    * **🎒 Elementi:** Prepara la valigia senza dimenticare nulla! **SALVINO** ti suggerisce gli elementi essenziali in base al tipo di viaggio e a ciò che hai già aggiunto.
    * **📍 Itinerario:** Organizza le tue tappe con mappe interattive, collegamenti ad app di navigazione, calendari per la tabella di marcia e **consigli personalizzati di SALVINO** su cosa vedere e fare in ogni luogo, basandosi sempre sui tuoi gusti.
    * **TODO: Inserisci uno screenshot della sezione "Dettaglio Viaggio" (magari mostrando Budget, Elementi o Itinerario) qui.**

---

## ⚙️ Tecnologie Utilizzate

WAYpp sfrutta un mix potente di tecnologie e API esterne per offrire la sua ricchezza di funzionalità:

**Core Technologies:**

* **JavaFX:** Per l'interfaccia utente desktop.
* **PHP:** Per il server backend e le API.
* **MySQL:** Per la gestione del database relazionale.
* **Maven:** Gestione delle dipendenze per il progetto Java.
* **Java 11+:** Per lo sviluppo dell'applicazione desktop, con particolare enfasi sulle `HttpRequest` per le chiamate API esterne.

**API Esterne (e loro potenziale miglioramento):**

* **GraphHopper API:** Per calcolare percorsi mappati precisi e ottimizzati.
* **Maileroo API:** Utilizzata per l'invio di email, come quelle per il reset della password.
* **Geocoding API:** Per la conversione di indirizzi in coordinate geografiche e viceversa.
* **Groq API:** Per l'integrazione dell'IA generativa (SALVINO) che assiste concretamente nell'organizzazione del viaggio, fornendo suggerimenti e creando itinerari.
* **Unsplash API:** Per l'integrazione di immagini accattivanti nella sezione esplora.

**Nota:** Attualmente, alcune di queste API sono utilizzate con **tier gratuiti**, il che potrebbe comportare piccoli ritardi (ad esempio, un'attesa di 1 secondo dopo una geolocalizzazione). Se si desidera rimuovere queste limitazioni e migliorare le prestazioni (ad esempio, incrementando l'intelligenza dell'IA con API come quelle di OpenAI o Google Gemini), è possibile aggiornare i piani delle API. La flessibilità del codice, basata su `HttpRequest` di Java 11, permette di **modificare facilmente le API** con alternative a proprio piacere.

---

## 🚀 Installazione e Avvio

Per far funzionare WAYpp sul tuo sistema, segui questi passaggi:

1.  **Requisiti:**
    * **Java Development Kit (JDK) 11 o superiore** installato.
    * **Apache Maven** installato.
    * **XAMPP** (o un ambiente equivalente con Apache e MySQL) installato e configurato.

2.  **Configurazione del Backend (PHP & MySQL):**
    * Clona questo repository:
        ```bash
        git clone [https://github.com/your-repo/WAYpp.git](https://github.com/your-repo/WAYpp.git)
        cd WAYpp
        ```
    * **Database:** Importa lo schema del database MySQL fornito nella cartella `database/` (o dove si trova il tuo schema grafico). Assicurati che il database sia accessibile da XAMPP.
        * **TODO: Indica il percorso esatto dello schema del DB, es. `docs/db_schema.sql`**
    * **Server PHP:** Configura il server PHP all'interno del tuo ambiente XAMPP, posizionando i file del backend nella directory `htdocs` di XAMPP o configurando un virtual host.
        * **TODO: Indica il percorso esatto dei file del backend PHP nel tuo progetto, es. `backend/php/`**
    * **Avvia Apache e MySQL tramite XAMPP.**

3.  **Configurazione delle API e delle Variabili d'Ambiente:**
    * Crea un file `.env` nella directory principale del progetto, copiandolo dal file `.env.example` fornito.
        ```bash
        cp .env.example .env
        ```
    * Apri il file `.env` e inserisci le tue chiavi API per GraphHopper, Maileroo, Geocoding, Groq, ecc.
        * **TODO: Specificare la posizione del file `.env.example` se non è nella root, es. `src/main/resources/.env.example`**

4.  **Compilazione ed Esecuzione del Frontend (JavaFX):**
    * Naviga nella directory principale del progetto (dove si trova il `pom.xml` di Maven).
    * Compila il progetto Maven:
        ```bash
        mvn clean install
        ```
    * Esegui l'applicazione JavaFX:
        ```bash
        mvn javafx:run
        ```

---

## 🤝 Contributi

Siamo entusiasti di ricevere contributi, suggerimenti e miglioramenti al progetto! WAYpp è un software libero e la collaborazione è fondamentale per la sua crescita.
Sia che tu voglia correggere un bug, aggiungere una nuova funzionalità o migliorare l'interfaccia utente, ogni aiuto è ben accetto.

Nonostante il continuo diminuire dell'uso di JavaFX nell'ambito dello sviluppo di nuove applicazioni (cosa che ci spinge a essere sempre attenti alle nuove tendenze, ma non ci impedisce di apprezzarne la robustezza e le capacità per applicazioni desktop dedicate), crediamo che la sua integrazione in WAYpp sia solida e funzionale.

Ringraziamo sentitamente i seguenti contributori:

* **gutroch**
* **Diego D'Ortensio**
* E non ultimo, il nostro amico peloso **Rocky**, la mascotte 🐶!
    * **TODO: Inserisci una foto di Rocky qui!**

---

## 📸 Galleria Foto

Qui puoi inserire gli screenshot più accattivanti della tua applicazione in azione!

* **TODO: Inserisci qui uno screenshot generale dell'interfaccia principale dell'applicazione.**
* **TODO: Inserisci qui uno screenshot di una sezione specifica che vuoi mettere in evidenza (es. la sezione "Itinerario" con la mappa).**
* **TODO: Inserisci qui un altro screenshot che mostri una funzionalità chiave.**

---

## 📜 Licenza

Questo progetto è rilasciato sotto licenza [MIT License](https://opensource.org/licenses/MIT).

---