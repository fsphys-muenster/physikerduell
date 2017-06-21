# physikerduell
Source code for the software used for the “Physikerduell” (“Physicists’ Feud”) which takes place every year during our Summer Festival.

## Regeln des Physikerduells
Siehe Ordner [`docs/regelwerk`](docs/regelwerk).

## Zum Ändern des Fragenkatalogs
Die Fragen in einer regelkonformen (bezogen auf den CSV-Interpreter) Datei aufbereiten.
Format der Dateien: Im Prinzip eine zweispaltige CSV-Datei (s. auch frühere Kataloge).
Die erste Zeile enthält immer nur die Frage in der ersten Spalte, während alle darauffolgenden
Zeilen Antworten enthalten: In der ersten Spalte den Antworttext, in der zweiten die Punktzahl.
Um eine neue Frage anzufangen, wird eine Leerzeile eingefügt (die trotzdem ein Komma enthält,
da es ja eine CSV-Datei ist!). Jede Frage muss (mindestens) 6 Antworten haben!
Am leichtesten lassen sich solche Dateien erstellen, indem man die Tabelle mit den Fragen,
Antworten und Punkten wie beschrieben in LibreOffice bzw. Excel erstellt und dann als CSV-Datei
exportiert.

Hinweis: Die erste Frage in der Fragen-Datei wird vom Programm als Beispiel-/Test-Frage
interpretiert und kann nicht im Spiel verwendet werden! Die „echten“ Fragen sollten also
ab der zweiten Frage in der Datei beginnen.

Ist die Datei `fragen.csv` im gleichen Ordner wie das Programm (`.jar`-Datei) enthalten,
lädt das Programm die Fragen aus dieser Datei. Alternativ kann im Programm auch mit
dem Button „Fragenkatalog auswählen“ eine Datei geladen werden.

## Buzzer-Modus
Der Buzzer kann über eine (emulierte) Tastenkombination ausgelöst werden. Bisher implementiert
ist die Kombination:<br> <kbd>Ctrl</kbd><kbd>Shift</kbd><kbd>B</kbd>.
Um den Buzzer mittels Tastenkombination auszulösen, muss das `ControlPanel`-Fenster im
Vordergrund (fokussiert) sein.

## Zum Ändern von Sounds und Splashscreens:
1. Die `.jar`-Datei in einem Programm öffnen → z. B. [7-Zip](http://www.7-zip.de/)
2. In der Datei die entsprechenden Dateien (`.png`, `.mp3`, …) ändern
