# SKILL-11 : Consultation (RAAM Section 11)

## Vue d'ensemble

La section 11 du RAAM (Référentiel d'Évaluation de l'Accessibilité des Applications Mobiles) traite de la consultation du contenu, qui inclut le contrôle du temps, des éléments en mouvement, et les actions qui requièrent une précision ou des mesures de récupération en cas d'erreur.

### Zone de touche minimum [AA] (Critère 11.5)

C'est l'une des règles d'accessibilité mobile les plus fréquemment violées. Les interfaces tactiles requièrent que les cibles (boutons, liens, éléments interactifs) soient suffisamment grandes pour être activées facilement par des utilisateurs ayant des problèmes de motricité ou de tremblement, mais aussi pour le confort de tous.

- **iOS** : La zone de touche minimale recommandée par Apple (HIG) est de **44x44 pt**.
- **Android** : La zone de touche minimale recommandée (Material Design) est de **48x48 dp**.

**Attention :** Il est possible d'avoir une icône visuelle plus petite (ex: 24x24), tant que le conteneur interactif autour (avec un padding transparent) respecte la taille minimale requise.

### Timeouts : Session expirante et compte à rebours [A] (Critère 11.1)

Lorsqu'une application utilise des limites de temps (pour des raisons de sécurité ou de session), l'utilisateur doit être averti de l'expiration imminente et avoir la possibilité de prolonger le temps imparti.

- L'alerte doit s'afficher suffisamment tôt (ex: 2 minutes avant expiration).
- L'annonce doit être vocalisée par le lecteur d'écran.
- La méthode d'extension doit être simple (ex: bouton "Prolonger").

### Erreurs récupérables et Actions irréversibles [AA] (Critère 11.6)

Pour les formulaires qui requièrent un engagement financier, légal, ou des modifications/suppressions de données importantes :
- Demander une confirmation explicite avant d'exécuter l'action irréversible.
- Ne jamais vider les champs valides lorsqu'une erreur de saisie se produit.
- Fournir des mécanismes de récupération si une erreur est effectuée.

---

## Checklist de Consultation

### Niveau [A] - Simplifié
- [ ] **[A] 11.1.1** Si une session expire, l'utilisateur est-il averti avant l'expiration ?
- [ ] **[A] 11.1.2** L'utilisateur peut-il prolonger la session expirante au moins 10 fois sans perte de données ?
- [ ] **[A] 11.1.3** Les carrousels automatiques peuvent-ils être mis en pause (bouton pause ou activation tactile) ?
- [ ] **[A] 11.1.4** Les animations déclenchées automatiquement durent-elles moins de 5 secondes ou peuvent-elles être stoppées ?
- [ ] **[A] 11.1.5** Les contenus qui s'actualisent automatiquement (live-feed) peuvent-ils être mis en pause ?
- [ ] **[A] 11.2.1** Les vidéos d'arrière-plan ne se lancent pas automatiquement ou peuvent être arrêtées ?
- [ ] **[A] 11.2.2** Les éléments en mouvement ne capturent pas le focus du lecteur d'écran de manière bloquante ?
- [ ] **[A] 11.3.1** Aucun composant de l'interface ne clignote plus de 3 fois par seconde (risque de crise d'épilepsie) ?
- [ ] **[A] 11.3.2** Si une alerte critique clignote, l'utilisateur a-t-il le contrôle pour stopper le clignotement ?
- [ ] **[A] 11.4.1** Les pages principales sont lisibles sans styles (le mode contraste élevé natif de l'OS fonctionne correctement) ?

### Niveau [AA] - Complet
- [ ] **[AA] 11.5.1** Les boutons iconiques isolés sur iOS ont-ils une zone de touche d'au moins 44x44 pt ?
- [ ] **[AA] 11.5.2** Les boutons iconiques isolés sur Android ont-ils une zone de touche d'au moins 48x48 dp ?
- [ ] **[AA] 11.5.3** Les liens interactifs dans un texte (inline) disposent-ils d'un espacement suffisant pour ne pas cliquer à côté ?
- [ ] **[AA] 11.5.4** Les éléments de liste interactive mesurent au moins la hauteur de touche minimale exigée ?
- [ ] **[AA] 11.6.1** Toute suppression de compte nécessite-t-elle une validation via une pop-up ou un écran de confirmation ?
- [ ] **[AA] 11.6.2** Lors d'un achat ou transfert d'argent, l'utilisateur a-t-il la possibilité de vérifier et modifier les données avant l'envoi final ?
- [ ] **[AA] 11.6.3** Les formulaires longs retiennent-ils les données valides lorsqu'une validation d'erreur échoue côté client ou serveur ?
- [ ] **[AA] 11.6.4** En cas d'erreur de saisie, le champ concerné affiche-t-il clairement l'attente (ex: format requis) sans effacer la saisie utilisateur ?
- [ ] **[AA] 11.7.1** Les sessions avec délai d'inactivité permettent-elles à l'utilisateur de désactiver complètement le timeout dans ses réglages ?
- [ ] **[AA] 11.8.1** Le texte reste lisible si l'utilisateur désactive les polices personnalisées (Dynamic Type / font scaling forcé) ?
