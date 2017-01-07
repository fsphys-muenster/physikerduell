# physikerduell
Source code for the software used for the “Physikerduell” (“Physicists’ Feud”) which takes place every year during our Summer Festival.

## Regeln des Physikerduells
Siehe Ordner `regelwerk`.

## Zum Ändern des Fragenkatalogs
Die Fragen in einer regelkonformen (bezogen auf den CSV-Interpreter) Datei aufbereiten.
Format der Dateien: Im Prinzip eine zweispaltige CSV-Datei (s. auch frühere Kataloge).
Die erste Zeile enthält immer nur die Frage in der ersten Spalte, während alle darauffolgenden
Zeilen Antworten enthalten: In der ersten Spalte den Antworttext, in der zweiten die Punktzahl.
Um eine neue Frage anzufangen, wird eine Leerzeile eingefügt (die trotzdem ein Komma enthält,
da es ja eine CSV-Datei ist!). Am leichtesten lassen sich solche Dateien erstellen, indem man
die Tabelle mit den Fragen, Antworten und Punkten wie beschrieben in LibreOffice bzw. Excel
erstellt und dann als CSV-Datei exportiert.

Ist die Datei `fragen.csv` im gleichen Ordner wie das Programm (`.jar`-Datei) enthalten,
lädt das Programm die Fragen aus dieser Datei. Alternativ kann im Programm auch mit
dem Button „Fragenkatalog auswählen“ eine Datei geladen werden.

## Buzzer-Modus
Der Buzzer kann über eine (emulierte) Tastenkombination ausgelöst werden. Bisher implementiert ist die Kombination: <kbd>Ctrl</kbd>&nbsp;<kbd>Shift</kbd>&nbsp;<kbd>B</kbd>.

## Zum Ändern von Sounds und Splashscreens:
1. Die `.jar`-Datei in einem Programm öffnen → z.&nbsp;B. [7-Zip](http://www.7-zip.de/)
2. Bei `Physikerduell.jar/de/uni_muenster/physikerduell` die entsprechenden Dateien (`.png`, `.mp3`, …) ändern

## TODO
- Jedes Jahr: Neuer Fragenkatalog
- Ein neues Design → Animationen etc.
