# Die Buzzer und [MaKey MaKey](http://makeymakey.com)
Die Buzzer bestehen jeweils aus einem physischen Knopf, der an den MaKey MaKey
der Fachschaft angeschlossen ist. Bei Drücken des Buzzer-Knopfs wird ein
Stromkreis geschlossen, sodass der MaKey MaKey eine bestimmte Tastenkombination
an den per USB-Kabel angeschlossenen Computer sendet. Zum Zusammenbauen der
funktionsfähigen Buzzer müssen also zwei Schritte durchgeführt werden:
1. Der MaKey MaKey muss so programmiert werden, dass er die richtige
   Tastenkombination an das Physikerduell-Programm schickt. Hierfür muss die
   Tastenbelegung (d. h. welche Eingabe am MaKey MaKey für welche Taste auf
   der Tastatur steht) angepasst werden.
   *(Falls das früher schon einmal, z. B. beim letzten Sommerfest, erledigt
   wurde, muss dieser Schritt natürlich nicht wiederholt werden.)*
2. Die Buzzer-Knöpfe müssen per Kabel an den richtigen Stellen mit dem MaKey
   MaKey verbunden werden.

## Programmieren des MaKey MaKey
Für Schritt 1 gibt es
[einen Hinweis im MaKey MaKey-FAQ](http://makeymakey.com/faq/#h.isayfkigsqoz).
Leider ist unser MaKey MaKey älter als v1.2, sodass wir die „einfache“
Methode der Konfiguration über den Browser nicht nutzen können. (Allerdings
scheint man [auf diese Weise Tasten wie <kbd>Strg</kbd> oder <kbd>Shift</kbd>
ohnehin nicht belegen zu
können](http://www.makeymakey.com/forums/index.php?topic=15814.0).)
Stattdessen muss der [MaKey MaKey über das Arduino IDE umprogrammiert
werden](https://learn.sparkfun.com/tutorials/makey-makey-advanced-guide).
Dazu kann man einfach den Schritten in der verlinkten Anleitung folgen.

**Hinweise:**
- Die Anleitung funktioniert, auch wenn auf unserem MaKey MaKey nicht das
  SparkFun-Logo aufgedruckt ist.
- Unter Linux muss man die notwendigen Nutzerrechte haben, um auf den
  MaKey MaKey (oder allgemein Arduino-Geräte) via serieller Schnittstelle
  zugreifen zu können. Unter Ubuntu erreicht man das z. B. mit dem Befehl

      sudo usermod -a -G dialout "$USER"

  (erfordert Administratorrechte).
  [Weitere Informationen in der Arduino-Dokumentation.](https://www.arduino.cc/en/Guide/Linux#toc6)
- Unter Linux muss man, falls
  [ModemManager](https://www.freedesktop.org/wiki/Software/ModemManager/)
  installiert ist, entweder das Skript
  [`disable_modemmanager.sh`](disable_modemmanager.sh) ausführen oder
  ModemManager deinstallieren (leider erfordert beides Administrator-Rechte).
  Ansonsten kann sich das Arduino IDE nicht mit dem MaKey MaKey verbinden.
- Falls beim Upload im Arduino IDE der Fehler

  >     avrdude: ser_open(): can't open device "/dev/ttyACM0": Device or resource busy

  oder

  >     processing.app.debug.RunnerException
  >     	at cc.arduino.packages.uploaders.SerialUploader.uploadUsingPreferences(SerialUploader.java:160)
  >     	at cc.arduino.UploaderUtils.upload(UploaderUtils.java:78)
  >     	at processing.app.SketchController.upload(SketchController.java:713)
  >     	at processing.app.SketchController.exportApplet(SketchController.java:686)
  >     	at processing.app.Editor$DefaultExportHandler.run(Editor.java:2149)
  >     	at java.lang.Thread.run(Thread.java:745)
  >     Caused by: processing.app.SerialException: Error touching serial port '/dev/ttyACM0'.
  >     	at processing.app.Serial.touchForCDCReset(Serial.java:99)
  >     	at cc.arduino.packages.uploaders.SerialUploader.uploadUsingPreferences(SerialUploader.java:144)
  >     	... 5 more
  >     Caused by: jssc.SerialPortException: Port name - /dev/ttyACM0; Method name - openPort(); Exception type - Port busy.
  >     	at jssc.SerialPort.openPort(SerialPort.java:164)
  >     	at processing.app.Serial.touchForCDCReset(Serial.java:93)
  >     	... 6 more

  auftritt, liegt das wahrscheinlich an den fehlenden Rechten für die serielle
  Schnittstelle oder an ModemManager. Dann sollte wie oben beschrieben
  vorgegangen werden.

Der Arduino-Quelltext ist unter [`docs/buzzer/makey_makey/`](makey_makey/) oder
[auf GitHub bei SparkFun](https://github.com/sparkfun/MaKeyMaKey/tree/master/firmware/Arduino/makey_makey/)
zu finden. In der Datei [`settings.h`](makey_makey/settings.h) kann man
anpassen, welcher Eingang auf dem MaKey MaKey für welche Taste auf der Tastatur
stehen soll. Wenn nach einem Klick auf den Upload-Knopf im Arduino IDE keine
Fehler auftreten, ist die Programmierung abgeschlossen.

## Verbinden von Buzzer und MaKey MaKey
Derzeit ist in [`settings.h`](makey_makey/settings.h) Folgendes eingestellt:

| Pin | Standardbelegung | Neue Belegung                                   |
| --- | ---------------- | ----------------------------------------------- |
| A4  | Maus ↓           | `KEY_LEFT_CTRL` (linke <kbd>Strg</kbd>-Taste)   |
| A3  | Maus ←           | `KEY_LEFT_SHIFT` (linke <kbd>Shift</kbd>-Taste) |
| A2  | Maus →           | `'2'`                                           |
| A1  | Mausklick links  | `'1'`                                           |
| D4  | `'a'`            | `KEY_LEFT_CTRL` (linke <kbd>Strg</kbd>-Taste)   |
| D3  | `'s'`            | `KEY_LEFT_SHIFT` (linke <kbd>Shift</kbd>-Taste) |
| D2  | `'d'`            | `'2'`                                           |
| D1  | `'f'`            | `'1'`                                           |
| D0  | `'g'`            | `'b'`                                           |

Ansonsten wurden alle Eingänge des MaKey MaKey auf ihren ursprünglichen Werten
belassen. Die Buzzer müssen nun dementsprechend so mit dem MaKey MaKey
verbunden werden, dass ein Druck auf Buzzer 1 die Kombination
<kbd>Strg</kbd><kbd>Shift</kbd><kbd>1</kbd> (für Team 1) und auf Buzzer 2 die
Kombination <kbd>Strg</kbd><kbd>Shift</kbd><kbd>2</kbd> (für Team 2) auslöst.

