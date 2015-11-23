Pour votre santé mentale, j'ai précompilé le programme dans l'éxécutable nommé devoir2.jar

UTILISATION:

java -jar devoir2.jar <path fichier>

Ex: java -jar devoir2.jar resources/program1.txt

COMPILATION:

Le projet utilise Gradle comme build manager. La tâche fatjar est configurée pour produire un exécutable bootstrapé

Ex: gradle fatJar

Le jar se trouvera ensuite dans build/libs