Planificateur de tâches multi plateforme.
Capable de planifier une alerte ou le lancement d'un programme ou l'ouverture 
d'un fichier de tout type.
Pour le lancement d'un programme sous :
Windows :
	Le lancement d'un programme/l'ouverture d'un fichier se base sur la commande start, le programme utilise donc l'application par défaut du système assignée à l'extension du fichier pour ouvrir ce dernier.
Sous Mac :
	Idem avec la commande "open -a".
Linux :
	La commande start n'ayant pas d'équivalent, l'application se contente de lancer la commande renseignée dans le path, pour lancer un programme, ajoutez la commande nécessaire au début du path (e.g. : java -jar monPath.jar)

Possibilité de choisir la méthode de déclenchement de la tâche : Au démarrage ou à une heure donnée.
La tâche peut être unique ou répétée tous les jours.
Un nombre de répétitions suivant la première exécution de 
la tâche peut également être renseigné, avec un délai au choix entre chacune des répétitions.

Langues disponibles :
{Français,  Anglais}
Pour ajouter une langue, rajouter dans src/texts un fichier nommé "SIGLE_DE_LA_LANGUE".properties avec toutes les clés renseignées et ajouter ce sigle dans l'énumération Language.java

Raccourcis claviers disponibles, possibilités d'undo/redo les actions effectuées, d'annuler toutes les tâches planifiées ou 1 par 1, de replanifier toutes les tâches.

Onglet disponible afin de suivre l'évolution des tâches actuellement planifiées et le temps/nombre de repétitions restants.

L'application peut-être minimisée dans la barre des notifications dès le démarrage et à la fermeture aux choix par l'utilisateur.

Détails du projet:
	Projet NetBeans; 
	Java 8; 
	JavaFX pour les graphismes; 
	Swing pour TrayIcon; 
	org.json pour la persistance des tâches;

@author Johan Gousset