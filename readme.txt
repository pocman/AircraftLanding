AircraftLanding
===============

Projet avant vente

Le generateur d'instances fonctionne sur 4 modes : aeroport par defaut, petite, moyenne ou grande taille

**Lancement du projet** 
=====================
Le lancement du logiciel s'effectue via le main de AircraftLanding.
L'Ihm la plus riche est pr�sente dans le fichier aeroport.xls
Vous pouvez trouver le repo github du projet : https://github.com/pocman/AircraftLanding.git

**Instance par defaut** 
=====================
C'est une instance specifiee dans le cahier des charges du projet.
Elle contient les pistes de capacites suivantes : <code>{6,5,3,2,7,6,4,1,1}</code>
Elle contient aussi 125 avions de capacite <code>{1,2,3}</code> determinee par une loi normale
Les avions sont repartis de manière realiste tout au long de la journee

**Pour les autres instances**
=====================
Le nombre de pistes, leur capacite et le nombre d'avions qui circulent chaque jour depend du choix de taille de l'instance
Le generateur utilise egalement un entier comme argument qui permet de generer differentes petites,
moyennes et grandes instances (utilisation d'un random qui fait varier les instances dans une 
certaine mesure).

**L'interface de lancement**
=====================
**Contrainte cumulative multiple**
--------------------
L'interface console de lancement permet de specifier si on veut ou non utiliser la _contrainte cumulative multiple_ develloppee par Arnault Letort. Par defaut, cette option est desactivee. Son utilisation dans le contexte actuel n'est pas justifie car chaque avion ne consomme finalement qu'un ressource sur une piste.

Elle sera cepandent très importante pour la suite du projet. En effet, dans le cadre de la modelisation de l'alimentation des avions
en kerozene ou de l'assignement des portes, chaque avion consommera plusieurs ressources. Cette contrainte permet aussi de prendre en compte
des notions de coloration des avions. Ainsi, il sera possible de specifier des contraintes de placement entre deux avions (comme dans le cadre des avions de frets ou de tourisme).

Cette nouvelle contrainte est issue de _notre lien fort_ avec le monde de la recherche. Nous pensons quelle sera un _element cle_ permettant
la scalabilite de notre logiciel à mesure que l'on assure la prise en compte d'un ensemble plus varie de contraintes metier..

**Contrainte de precedence**
--------------------
Dans le cadre du projet, nous avons cherche à optimise le nombre de violation de contraintes de precedence entre les avions.
Cette contrainte souple est très couteuse à mettre en place. Dans le cadre des instances petites et moyennes, elle est parfaitement adaptee.
Il est cependant possible de la desactiver afin d'ameliorer les performances sur les grandes instances.

**Le time-out**
--------------------
Comme vous pouvez le voir en console, dans un premier temps afin de verifier une relative validite de l'instance en entree de notre modèle, 
nous utiliserons notre logiciel avec une version degenere de l'instance (une seule piste avec la somme des capacite de toutes les pistes et sans contrainte de precedence).
La resolution de cette instance dite 'dummy' permet rejeter rapidement les instances trop surchargees.

Dans le cas des instances à la limite haute de la surcharge, il n'est malheureusement pas encore possible de demontrer la non existance d'une solution dans tous les cas avec un temps de calcul resonnable.
Nous proposons la mise en place d'un time-out afin de mettre fin à la recherche

**L'interface de sortie**
=====================
Nous proposons 3 interfaces de sorties.

* Une interface dans la console qui permet d'observer rapidement les niveaux de charge par point d'interêt pour chaque piste.
* Une interface au format <code>.csv</code> qui permet d'interfacer notre solver avec un autre produit dejà existant
* Une interface sous la forme d'un fichier <code>.xls</code> qui permet d'observer la charge sur chaque piste, mais aussi le statut de chaque avion ainsi que les heures d'atterrissages et de decollages.

**Axes d'amelioration**.
=====================

* Après un certain temps, il sera necessaire de changer de strategie afin de non plus chercher de solutions mais de prouver la non-existence de solution

* Pour le moment, nous utilisons des temps en minutes. Cela pose problème dans le cas où le nombre d'avions est superieur à 300.
En effet, nous ne pouvons plus assurer qu'il existe une et une unique operation par minute sur l'ensemble de l'aeroport via un alldifferent sur les dates.
Pour echapper à ce problème, il faudra modeliser une action par pas de temps par piste et non sur l'ensemble de l'aeroport.
Nous proposons aussi la possibilite de passer en Seconde comme pas de temps.

* La contrainte de precedence semble être optimisable.

* L'utilisation de toutes les pistes n'est pas assuree, cela pourra s'ameliorer avec une heuristique plus poussee.


