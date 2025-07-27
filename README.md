# WAYpp - Il Tuo Compagno di Viaggio Intelligente

[![GitHub license](https://img.shields.io/badge/license-MIT-blue.svg)](https://github.com/DOrtenzio/WAYpp/blob/main/LICENSE)
[![GitHub contributors](https://img.shields.io/github/contributors/DOrtenzio/WAYpp)](https://github.com/DOrtenzio/WAYpp/graphs/contributors)
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
    <img width="1127" height="913" alt="image" src="https://github.com/user-attachments/assets/01058e91-8e8c-4fdf-83fd-65265785fcfe" />
* **Gestione Viaggi:** Una sezione dedicata per visualizzare, aggiungere, modificare ed eliminare i tuoi viaggi personali.
    <img width="451" height="366" alt="image" src="https://github.com/user-attachments/assets/ac7c1607-6449-40b1-b060-e09bb0d24b6f" />
    <img width="451" height="366" alt="image" src="https://github.com/user-attachments/assets/2a0d82c6-eea9-4f43-9660-81066631b294" />
* **Dettaglio Viaggio:** Ogni viaggio è suddiviso in tre sezioni fondamentali:
    * **💰 Budget:** Tieni sotto controllo le tue spese con un resoconto dettagliato per rimanere sempre nel budget.
    * **🎒 Elementi:** Prepara la valigia senza dimenticare nulla! **SALVINO** ti suggerisce gli elementi essenziali in base al tipo di viaggio e a ciò che hai già aggiunto.
    * **📍 Itinerario:** Organizza le tue tappe con mappe interattive, collegamenti ad app di navigazione, calendari per la tabella di marcia e **consigli personalizzati di SALVINO** su cosa vedere e fare in ogni luogo, basandosi sempre sui tuoi gusti.
   <img width="451" height="366" alt="image" src="https://github.com/user-attachments/assets/61714a01-acb6-4f7a-a7a0-4242f9ed88bb" />
   <img width="451" height="366" alt="image" src="https://github.com/user-attachments/assets/435fd021-586b-45da-b4ee-c138f57c37a2" />
   <img width="451" height="366" alt="image" src="https://github.com/user-attachments/assets/d573b65f-ab57-4263-957f-1b592c7fdcf3" />

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
    * **Database:** Importa lo schema del database MySQL fornito nella cartella `WAYpp/CODE/db/` (o dove si trova il tuo schema grafico). Assicurati che il database sia accessibile da XAMPP.
        <img width="1225" height="577" alt="image" src="https://github.com/user-attachments/assets/1349c4e0-e846-40de-816c-5cf6b5da64a3" />
    * **Server PHP:** Configura il server PHP all'interno del tuo ambiente XAMPP, posizionando i file del backend nella directory `htdocs/api` (Creala se non esiste) di XAMPP o configurando un virtual host.
      Seguendo i file indicati nel percorso `WAYpp/CODE/server_php/`.
    * **Avvia Apache e MySQL tramite XAMPP.**

      <img width="700" height="400" alt="image" src="https://github.com/user-attachments/assets/ff5942a1-44fe-4115-a7ba-b6c09064ceff" />

3.  **Configurazione delle API e delle Variabili d'Ambiente:**
    * Crea un file `.env` nella directory principale del progetto, copiandolo dal file `.env.example` fornito.
        ```bash
        cp .env.example .env
        ```
    * Apri il file `.env` e inserisci le tue chiavi API per GraphHopper, Maileroo, Geocoding, Groq, ect.

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
    > Hey non complicarti la vita usa un IDE 🫡

---

## 🤝 Contributi

Siamo entusiasti di ricevere contributi, suggerimenti e miglioramenti al progetto! WAYpp è un software libero e la collaborazione è fondamentale per la sua crescita.
Sia che tu voglia correggere un bug, aggiungere una nuova funzionalità o migliorare l'interfaccia utente, ogni aiuto è ben accetto.

Nonostante il continuo diminuire dell'uso di JavaFX nell'ambito dello sviluppo di nuove applicazioni (cosa che ci spinge a essere sempre attenti alle nuove tendenze, ma non ci impedisce di apprezzarne la robustezza e le capacità per applicazioni desktop dedicate), crediamo che la sua integrazione in WAYpp sia solida e funzionale.

Ringraziamo sentitamente i seguenti contributori:

* **gutroch**
* **Diego D'Ortenzio**
* E non ultimo, il nostro amico peloso **Rocky** 🐶!

  <img width="400" height="450" alt="image" src="https://github.com/user-attachments/assets/c156f147-857e-4885-8d1d-37c88e759e42" />

---

## 📸 Galleria Foto

Ecco degli screenshot dell'app in azione!

<img width="451" height="366" alt="image" src="https://github.com/user-attachments/assets/8c4ad1a5-4491-45d9-90f8-244bce1ba0a2" />
<img width="451" height="366" alt="image" src="https://github.com/user-attachments/assets/249a309e-02a1-4e0a-bb96-62b23abd9a37" />
<img width="451" height="366" alt="image" src="https://github.com/user-attachments/assets/ccca518e-78b8-4f4d-912e-a4d3ac250556" />
<img width="451" height="366" alt="image" src="https://github.com/user-attachments/assets/461dc9a0-5c34-4b01-9f30-5325cbc2fa9f" />
<img width="451" height="366" alt="image" src="https://github.com/user-attachments/assets/285da58c-24fd-4ac0-b1ca-685cf95eff56" />
<img width="451" height="366" alt="image" src="https://github.com/user-attachments/assets/ccf368e5-2d0c-4911-87b1-640b655e2aab" />
<img width="451" height="366" alt="image" src="https://github.com/user-attachments/assets/61714a01-acb6-4f7a-a7a0-4242f9ed88bb" />
<img width="451" height="366" alt="image" src="https://github.com/user-attachments/assets/435fd021-586b-45da-b4ee-c138f57c37a2" />
<img width="451" height="366" alt="image" src="https://github.com/user-attachments/assets/b2590730-e883-4447-871e-4fb6899663e0" />
<img width="451" height="366" alt="image" src="https://github.com/user-attachments/assets/d573b65f-ab57-4263-957f-1b592c7fdcf3" />
<img width="451" height="366" alt="image" src="https://github.com/user-attachments/assets/8dde0dba-b1fb-4848-8e02-e30880c169ee" />
<img width="451" height="366" alt="image" src="https://github.com/user-attachments/assets/fadc6f5b-e5fa-4ed9-a96a-84399fc1be36" />
<img width="451" height="366" alt="image" src="https://github.com/user-attachments/assets/4ad5f47d-5dee-43bf-9ac1-d0ab346da7af" />
<img width="451" height="366" alt="image" src="https://github.com/user-attachments/assets/1b549e60-05cd-40d3-9fa4-a6ecd3f9ace5" />



---

## 📜 Licenza

Questo progetto è rilasciato sotto licenza [MIT License](https://github.com/DOrtenzio/WAYpp/blob/main/LICENSE).

---
