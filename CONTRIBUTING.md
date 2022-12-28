# Contributing to Pickaria

The following document is only redacted in French as the main target are French players and developers.

## API du plugin

Les plugins se basent sur l'API de [Paper](https://papermc.io/) qui est un fork
de [Spigot](https://www.spigotmc.org/).  
Il est donc impératif d'avoir des connaissances sur a minima sur l'API de Spigot.  
L'équipe de Paper ne maintiens uniquement la dernière version du jeu, de ce fait, le plugin doit toujours être à jour
pour la dernière version.  
Compte tenu de l'API utilisée, les fonctionnalités suivantes doivent être utilisées lors du développement
du plugin :

- La bibliothèque [Adventure de Kyori](https://docs.adventure.kyori.net/) doit être utilisée pour la gestion des
  messages visible au joueur ;

## Kotlin

Les plugins pour Pickaria sont développés en [Kotlin](https://kotlinlang.org/). Il est donc impératif de contraire la
spécification du langage.  
Les fonctionnalités suivantes doivent être utilisées afin de tirer parti de Kotlin :

- Les fichiers de configurations doivent être implémentés
  avec [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization) ;
- Les coroutines de Kotlin sont utilisables grâce à la
  bibliothèque [MCCoroutines](https://github.com/Shynixn/MCCoroutine) ;
- Le traitement des dates se fait avec [kotlinx-datetime](https://github.com/Kotlin/kotlinx-datetime) plutôt que
  l'implémentation Java ;

### Nommage des variables

> Utiliser des variables plus courtes ne permet pas d'avoir un code plus rapide !

Écrivez des noms de variables et méthodes complets :

```kt
val delayBeforeNextPickup: Long = ...
fun getTimeBeforeNextPickup(): LocalDateTime {}
```

Les exceptions autorisées :

- Dans des boucles :
  ```kt
  for (i in 0..50) {
    for (j in 0..50) {}
  }
  ```
- Pour des positions :
  ```kt
  val x = 3
  val y = 5
  ```
- `Config` au lieu de `Configuration`

## Architecture MVC

Le plugin est utilisé l'architecture Model Vue Controller :

- __Model__ : contient les sources de données (fichiers de configuration, base de donnée, ...) ;
- __Controller__ : toute logique interne au plugin ;
- __Vue__ : ce que le joueur peut voir (menus, commandes, listeners) ;

Pour simplifier la désignation, un Model est le niveau 0, un Controller le niveau 1 et une Vue le niveau 2.

Globalement, un fichier ne doit jamais faire un appel à un fichier d'un niveau supérieur.
> Un fichier Model ne peut faire appel au contenu d'un Controller ou d'une Vue.  
> Un Controller ne peut pas faire appel à une Vue.

## Écriture de commande

Les commandes sont développées avec le framework [ACF](https://github.com/aikar/commands).  
En règle générale, les commandes ne doivent pas faire d'appel à la base de données.  
Elles doivent impérativement être documentées avec les annotations requises, exemple :

```kt
@CommandCompletion("@reward") // Défini quelle méthode utiliser pour l'auto complétion.
@CommandPermission("pickaria.command.reward") // Quelle permission doit avoir le joueur
@Description("Permet d'acheter une récompense.") // Ajoute une description à la commande
@Syntax("<reward-type> [amount]") // La syntaxe à utiliser pour la commande
fun onCommand() {
}
```

### Syntaxe des commandes

- `<reward-type>` : argument obligatoire
- `[amount]` : argument facultatif

## Git flow

Tout développement doit se faire sur une branche séparée, une fois le développement considéré comme terminé, une pull
request doit être ouverte vers la branche `dev`.

## Pour résumer

- Utiliser les implémentations en Kotlin si celle-ci existe pour un usage ;
- Toujours garder le plugin à jour ;
- Tirer parti des ajouts de l'API Paper ;