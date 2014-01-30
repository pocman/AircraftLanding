AircraftLanding
===============

Projet avant vente

Le générateur d'instances fonctionne sur 4 modes : aéroport par défaut, petite, moyenne ou grande taille

**Instance par défaut** 
=====================
C'est une instance spécifiée dans le cahier des charges du projet.
Elle contient les pistes de capacités suivantes : <code>{6,5,3,2,7,6,4,1,1}</code>
Elle contient aussi 125 avions de capacité <code>{1,2,3}</code> déterminée par une loi normale
Les avions sont répartis de manière réaliste tout au long de la journée

**Pour les autres instances**
=====================
Le nombre de pistes, leur capacité et le nombre d'avions qui circulent chaque jour dépend du choix de taille de l'instance
Le générateur utilise également un entier comme argument qui permet de générer différentes petites,
moyennes et grandes instances (utilisation d'un random qui fait varier les instances dans une 
certaine mesure).

**L'interface de lancement**
=====================
**Contrainte cumulative multiple**
--------------------
L'interface console de lancement permet de spécifier si on veut ou non utiliser la _contrainte cumulative multiple_ dévelloppée par Arnault Letort. Par défaut, cette option est désactivée. Son utilisation dans le contexte actuel n'est pas justifié car chaque avion ne consomme finalement qu'un ressource sur une piste.

Elle sera cepandent très importante pour la suite du projet. En effet, dans le cadre de la modélisation de l'alimentation des avions
en kerozene ou de l'assignement des portes, chaque avion consommera plusieurs ressources. Cette contrainte permet aussi de prendre en compte
des notions de coloration des avions. Ainsi, il sera possible de spécifier des contraintes de placement entre deux avions (comme dans le cadre des avions de frets ou de tourisme).

Cette nouvelle contrainte est issue de _notre lien fort_ avec le monde de la recherche. Nous pensons quelle sera un _élément clé_ permettant
la scalabilité de notre logiciel à mesure que l'on assure la prise en compte d'un ensemble plus varié de contraintes métier..

**Contrainte de précédence**
--------------------
Dans le cadre du projet, nous avons cherché à optimisé le nombre de violation de contraintes de précédence entre les avions.
Cette contrainte souple est très couteuse à mettre en place. Dans le cadre des instances petites et moyennes, elle est parfaitement adaptée.
Il est cependant possible de la désactiver afin d'améliorer les performances sur les grandes instances.

**Le time-out**
--------------------
Comme vous pouvez le voir en console, dans un premier temps afin de vérifier une relative validité de l'instance en entrée de notre modèle, 
nous utiliserons notre logiciel avec une version dégénéré de l'instance (une seule piste avec la somme des capacité de toutes les pistes et sans contrainte de précédence).
La résolution de cette instance dite 'dummy' permet rejeter rapidement les instances trop surchargées.

Dans le cas des instances à la limite haute de la surcharge, il n'est malheureusement pas encore possible de démontrer la non existance d'une solution dans tous les cas avec un temps de calcul résonnable.
Nous proposons la mise en place d'un time-out afin de mettre fin à la recherche

**L'interface de sortie**
=====================
Nous proposons 3 interfaces de sorties.

* Une interface dans la console qui permet d'observer rapidement les niveaux de charge par point d'interêt pour chaque piste.
* Une interface au format <code>.csv</code> qui permet d'interfacer notre solver avec un autre produit déjà existant
* Une interface sous la forme d'un fichier <code>.xls</code> qui permet d'observer la charge sur chaque piste, mais aussi le statut de chaque avion ainsi que les heures d'atterrissages et de décollages.

**Axes d'amélioration**.
=====================

* Après un certain temps, il sera nécessaire de changer de stratégie afin de non plus chercher de solutions mais de prouver la non-existence de solution

* Pour le moment, nous utilisons des temps en minutes. Cela pose problème dans le cas où le nombre d'avions est supérieur à 300.
En effet, nous ne pouvons plus assurer qu'il existe une et une unique opération par minute sur l'ensemble de l'aéroport via un alldifferent sur les dates.
Pour échapper à ce problème, il faudra modéliser une action par pas de temps par piste et non sur l'ensemble de l'aéroport.
Nous proposons aussi la possibilité de passer en Seconde comme pas de temps.

* La contrainte de précédence semble être optimisable.

* L'utilisation de toutes les pistes n'est pas assurée, cela pourra s'améliorer avec une heuristique plus poussée.


