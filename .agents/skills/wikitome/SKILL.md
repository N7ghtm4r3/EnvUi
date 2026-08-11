---
name: wikitome
description: Crea in chat una scheda breve, chiara e autonoma su una specifica API, capacità tecnica o funzionalità da implementare, per esempio "come implementare remember". Usa questa skill quando l'utente vuole capire rapidamente che cosa fa un'API o un concetto, come progettarlo in modo indipendente da framework e linguaggi, i casi d'uso, quando usarlo o evitarlo e un piccolo esempio concettuale. Su richiesta esplicita, valuta anche uno snippet o scenario condiviso dall'utente rispetto all'API trattata.
---

# WikiToMe

## Obiettivo

Produrre una voce wiki tecnica, compatta e consultabile a colpo d'occhio. Restare agnostici rispetto a linguaggi, framework, vendor e librerie, salvo richiesta esplicita dell'utente.

## Procedura

1. Identificare l'API, la capacità o il comportamento richiesto e il problema che risolve.
2. Se il nome è ambiguo, dichiarare in una riga l'interpretazione adottata. Non inventare firme, endpoint o comportamenti proprietari.
3. Descrivere il contratto concettuale: input, output, stato, persistenza, errori e vincoli rilevanti. Omettere le voci non applicabili.
4. Spiegare l'implementazione tramite componenti e flusso dei dati, usando interfacce astratte e pseudocodice neutrale.
5. Evidenziare i casi d'uso, quando scegliere la soluzione e quando evitarla.
6. Concludere con un esempio minimo che mostri il percorso principale e, se importante, un errore o caso limite.
7. Aggiungere una valutazione contestuale solo quando l'utente chiede esplicitamente di valutare uno snippet o scenario che ha condiviso.

## Formato della risposta

Usare questo ordine, adattando o unendo le sezioni quando serve. Preferire definizioni, elenchi brevi e tabelle compatte alla prosa continua.

### `<nome API o funzionalità>`

> Definizione in 1–2 frasi e problema risolto.

#### Contratto

Mostrare firma concettuale, input, risultato, stato ed errori principali. Usare una tabella solo se chiarisce più campi.

#### Funzionamento

Elencare da 3 a 6 passaggi concettuali, includendo solo i componenti necessari.

#### Utilizzo

Raccogliere in punti distinti: casi d'uso, quando usarla e quando evitarla.

#### Esempio

Mostrare un esempio minimo e completo del percorso principale.

#### Valutazione dello snippet o scenario (opzionale)

Includere questa parte solo su richiesta esplicita e basarla sul materiale fornito dall'utente. Valutare l'uso rispetto al contratto, al ciclo di vita e ai casi d'uso dell'API descritta, non come code review generale.

- Dire subito se l'approccio è adatto, parzialmente adatto o non adatto, spiegandone il motivo.
- Individuare al massimo tre aspetti rilevanti: correttezza, ciclo di vita, chiavi o dipendenze, stato, errori, prestazioni oppure alternativa più appropriata.
- Proporre una correzione minima quando serve; preservare linguaggio, framework e stile dello snippet originale.
- Usare un tono naturale e diretto. Non imporre la struttura wiki a questa sezione se una breve spiegazione discorsiva risulta più chiara.
- Dichiarare eventuali ipotesi quando lo scenario non contiene abbastanza contesto. Non inventare requisiti mancanti.

## Regole di qualità

- Puntare a 150–300 parole, salvo richiesta diversa.
- Adottare uno stile wiki: neutrale, dichiarativo, denso di informazioni e senza introduzioni o conclusioni conversazionali.
- Evitare paragrafi lunghi, ripetizioni, transizioni narrative e formule come "in altre parole".
- Spiegare prima il comportamento osservabile e poi i dettagli interni.
- Usare nomi astratti come `Store`, `Clock`, `Serializer` o `Policy` invece di prodotti specifici.
- Racchiudere nomi di API, funzioni, parametri, tipi, chiavi e valori letterali tra backtick inline.
- Racchiudere ogni esempio multilinea in un blocco Markdown fenced e specificare sempre il linguaggio corretto, per esempio `json`, `http`, `sql`, `python` o `javascript`.
- Usare il tag `text` esclusivamente per pseudocodice realmente indipendente dal linguaggio. Non dichiarare un linguaggio diverso da quello mostrato soltanto per ottenere colorazione sintattica.
- Separare il contratto pubblico dalle possibili strategie di implementazione.
- Segnalare le decisioni importanti: durata dei dati, invalidazione, concorrenza, sicurezza, privacy, idempotenza e gestione degli errori, ma solo se pertinenti.
- Non trasformare la risposta in un tutorial completo, una comparazione di framework o documentazione esaustiva.
- Non presentare pseudocodice come codice pronto per la produzione.
- Se l'utente indica un'API concreta o fornisce documentazione, rispettarne la terminologia e distinguere i fatti documentati dalle raccomandazioni progettuali.
- Non aggiungere automaticamente una valutazione né chiedere uno snippet quando l'utente vuole soltanto la scheda dell'API.

## Esempio di stile

Per una richiesta come "Come posso implementare remember?", interpretare `remember` come una capacità che conserva un valore tra richieste solo se il contesto non indica un'API specifica. Riassumere il contratto `remember(key, producer, policy) -> value`, descrivere lettura, scadenza, calcolo, salvataggio e invalidazione, quindi mostrare pseudocodice conciso in un blocco `text` e indicare quando una cache o una sessione non sono appropriate.
