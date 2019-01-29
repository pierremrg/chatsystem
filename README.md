# ChatSystem

Ce répertoire GitHub contient tout le code source écrit pour créer une application de clavardage dans le cadre de l'UF POO.

Il contient l'ensemble des fichiers JAVA (dossier `src`), des ressources (dossier `res`) et des données pour tester le serveur de présence. Le dossier `jars` contient, lui, les librairies utilisées lors de la réalisation du projet.

Le fichier `chatsystem.jar` correspond à l'application prête à être utilisée.

Un **manuel utilisateur** décrit en détails le fonctionnement du logiciel ; toutefois, un rappel des procédures d'utilisation est présenté ci-dessous.

# Extrait du manuel utilisateur : 4. Procédures d’utilisation

Cette partie du manuel indique comment installer, configurer et utiliser le chat.

## Installation et configuration préalable

1. **Création du dossier où installer le chat**

Le logiciel de chat est un fichier `.jar` (`ChatSystem.jar`). Copiez ce fichier à l'intérieur du dossier d'installation. Placez également le fichier `settings.ini` à l'intérieur de ce dossier.
Un dossier `data` sera créé lors de la première utilisation du logiciel.
_Remarque : il n'est pas nécessaire que l'utilisateur le crée par lui-même._

Si nous choisissons d'appeler notre dossier d'installation `ChatSystem`, voici l'arborescence que nous aurons (pour rappel, le dossier `data` n'est pas obligatoire pour démarrer l'application) :
```
ChatSystem/
	|— data/
		|— groups.bin
		|— messages.bin
		|— user.bin
	|— ChatSystem.jar
	|— settings.ini
```

---
2. **Configuration du fichier `settings.ini`**

Le fichier `settings.ini` permet de paramétrer l'utilisation de l'application. Voici la structure de ce fichier :
```ini
[general]
use_server=1
theme=light

[server]
ip=192.168.0.35
port=8080
timeout=3000
update_interval=1000

[udp]
port=5000
```

Le paramètre `use_server` de la section `[general]` permet de sélectionner le **mode UDP** ou le **mode serveur**.
- En mettant la valeur `1`, le **mode serveur** sera utilisé ;
- Tout autre valeur indiquée permettra d'utiliser le **mode UDP**.

Le paramètre `theme` de cette même section permet de sélectionner le thème utilisé dans l'application. Deux thèmes sont disponibles : un thème clair (par défaut, `light`) et un thème sombre (`dark`).

La section `[udp]` permet de configurer l'utilisation du service UDP. Vous pouvez ainsi sélectionner le port à utiliser à l'aide du paramètre `port`.
_Remarque : le port utilisé doit être identique sur chaque machine du réseau voulant communiquer entre-elles._

La section `[server]` est décrite en détails ci-dessous.

---
3. **Configuration et utilisation d'un serveur de présence (exemple de configuration avec Tomcat)**

_Cette section indique comment configurer et utiliser un serveur de présence. Ce manuel indique la marche à suivre pour utiliser un serveur **Tomcat**._
_La variable `$CATALINA_HOME` correspond au répertoire d'installation de **Tomcat**._

#### Installation des données du serveur

Pour utiliser le **mode serveur**, une machine du réseau doit être choisie pour faire office de serveur. Un moteur de servlets (ou conteneur servlets) doit être installé au préalable sur cette machine.

Le dossier `ChatSystem_Tomcat` fourni contient les données nécessaires pour faire fonctionner l'application avec le moteur de servlets libre **Tomcat**. Voici son contenu :

```
chatsystem/
	|— WEB-INF/
		|— classes/
			|— ChatSystem/
				|— User.class
			|— ChatSystemServer/
				|— ChatServer$ServerResponse.class
				|— ChatServer.class
		|— lib/
			|— gson-2.6.2.jar
		|— src/
			|— ChatSystem/
				|— User.java
			|— ChatSystemServer/
				|— ChatServer.java
		|— web.xml
```
Le répertoire `chatsystem` doit être copié entièrement dans le dossier `webapps` du dossier d'installation de Tomcat.

- Le sous-dossier `classes` contient les dossiers et fichiers compilés nécessaires au bon fonctionnement de la servlet ;
- Le sous-dossier `lib` contient les librairies externes utilisées ;
- Le sous-dossier `src` contient le code source de la servlet **(*)**. 
- Enfin, le fichier `web.xml` contient les informations de configuration de la servlet.

Si vous utilisez Tomcat, vous ne devriez pas avoir à faire plus de configurations.

**(*)** : Il n'est pas utile de recompiler la servlet pour qu'elle fonctionne ; toutefois, il est possible de le faire avec les commandes suivantes :
```bash
cd "$CATALINA_HOME/webapps/chatsystem/WEB-INF/src"
javac
	-cp "$CATALINA_HOME/lib/servlet-api.jar:$CATALINA_HOME/webapps/chatsystem/WEB-INF/lib/*"
	-d ../classes/
	ChatSystem/User.java ChatSystemServer/*
```
_Remarque : sous Windows, remplacez `:` par `;` de l'option `-cp` pour effectuer la compilation._

#### Configuration du serveur

Si vous avez utilisé la valeur `1` pour le paramètre `use_server`, vous devez configurer l'application pour utiliser correctement le serveur à l'aide des paramètres dans la section `[server]`.
Cette section contient 4 paramètres :
- `ip` : il s'agit de l'adresse IP de la machine sur laquelle est déployé le serveur. Dans le cas où une machine faisant office de serveur souhaiterait également utiliser le logiciel de chat, utilisez la valeur `localhost` ;
- `port` : c'est le port sur lequel le serveur est déployé. Par défaut, dans Tomcat, il s'agit du port `8080` ;
- `timeout` : c'est le temps maximal que peut prendre la connexion au serveur, en millisecondes (ms). Passé ce délai, la connexion échouera ;
- `update_interval` : il s'agit de la durée écoulée entre deux requêtes au serveur, en millisecondes (ms).

#### Lancement et test du serveur

Une fois le serveur installé et correctement configuré, vous pouvez le lancer. Utilisez pour cela la commande :
```bash
sudo $CATALINA_HOME/bin/startup.sh
```
Une fois le serveur lancé, vous pouvez tester son bon fonctionnement. Pour cela, ouvrez un navigateur internet et allez à l'adresse suivante (dans cet exemple, le port `8080` est utilisé) :
- http://localhost:8080/chatsystem/ChatServer?test depuis la machine déployant le serveur ;

ou bien

- http://X.X.X.X:8080/chatsystem/ChatServer?test (où `X.X.X.X` est l'adresse IP de la machine supportant le serveur) depuis tout autre machine souhaitant utiliser l'application.

Dans les deux cas, vous devriez avoir comme réponse l'élément _json_ suivant :
```json
{"code":0,"dataFormat":"json","data":"Le serveur fonctionnement correctement."}
```

Par la suite, pour arrêter le serveur, utilisez la commande :
```bash
sudo $CATALINA_HOME/bin/shutdown.sh
```

#### Sources d'erreurs possibles

Si après avoir configuré et démarré le serveur, vous ne parvenez pas à obtenir le résultat correct du test, vérifiez les points suivants :
- Assurez-vous d'avoir copié correctement tout le dossier `chatsystem` dans le dossier `webapps` de **Tomcat** ;
- Vérifiez que les configurations de **Tomcat** et du chat sont les mêmes ;
- Si une erreur `404` survient, c'est que les fichiers `.class` sont manquants où que le descripteur de la servlet (`web.xml`) contient des données incorrectes ;
- Si une erreur `500` survient (et que tous les fichiers `.class` ont été correctement copiés), il est probable que le fichier `gson-2.6.2.jar` soit manquant dans le dossier `$CATALINA_HOME/webapps/chatsystem/WEB-INF/lib/` ;
- Si le test fonctionne sur une machine mais pas sur une autre, vérifiez les paramètres du pare-feu, qui pourrait bloquer certaines connexions (notamment sur le port `8080`).


## Utilisation du chat

Cette section détaille l'utilisation du chat, après installation et configuration.

#### Utilisation classique du chat

Pour démarrer le chat et discuter avec d'autre utilisateurs :

- **Lancement de l'application :**

	1. Si utilisation du **mode serveur**, lancez le serveur sur une machine ;
	2. Configurez l'application en modifiant le fichier de configuration `.ini` (cf. *"Installation et configuration préalable"* --- il n'est pas nécessaire de le modifier à chaque connexion) ;
	3. Lancez l’application ;

- **Création du compte utilisateur :**

	4. Lors la première utilisation, créez un nouvel utilisateur (en indiquant un nom d'utilisateur et un mot de passe) ;

- **Connexion au chat :**

	5. Sur la fenêtre de connexion, choisissez l’adresse IP du réseau souhaité (ignorez cette option si vous n'êtes connecté qu'à un seul réseau) ;

- **Utilisation du chat :**

	6. Connectez vous ;
	7. Vous pouvez maintenant parler aux autres utilisateurs connectés en les sélectionnant dans la liste des utilisateurs connectés.

#### Modification des informations d'utilisateur

Pour changer de nom d’utilisateur et/ou de mot de passe :

1. Cliquez sur le bouton "Editer mon profil" lorsque vous êtes connecté ;
2. Vous avez ensuite la possibilité de modifier les informations vous concernant.
