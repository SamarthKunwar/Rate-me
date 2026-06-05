
## Erzeugen eines Docker Image
## Image erhält den Namen mysql_image 
## Muss nur einmal ausgeführt werden

docker build -f Dockerfile -t swtp_rateme_image .


## Erzeugen eines Docker Container
## - Im Container werden automatisch SQL-Scripte ausgeführt
## - Es erfolgt ein Netzwerk- und Datei-Mapping
## Ausführen im Projektverzeichnis
## Muss nur einmal ausgeführt werden

== Starten ==

docker run --name swtpRateme -d -p3306:3306 swtp_rateme_image



## Nachdem der Container erzeugt wurde
## kann er immer wieder gestartet oder beendet werden

docker start swtpRateme 
docker stop swtpRateme 

