# ==========================================
# ÉTAPE 1 : La compilation (Le Builder)
# ==========================================
# On utilise une image lourde qui contient Maven et Java 21 LTS
FROM maven:3.9-eclipse-temurin-25 AS builder

# On crée un dossier de travail
WORKDIR /build

# On copie tout le code source de ton PC vers le conteneur
COPY pom.xml .
COPY src ./src

# On demande à Docker de compiler le projet lui-même !
RUN mvn clean package -DskipTests

# ==========================================
# ÉTAPE 2 : L'exécution (La Production)
# ==========================================
# On repart d'une image Java 21 toute propre et ultra-légère
FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

# On récupère UNIQUEMENT le fichier .jar généré à l'étape 1
COPY --from=builder /build/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]