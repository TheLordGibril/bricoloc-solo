package fr.bricoloc.stocks_service.controller;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.List;
import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stocks")
public class StockController {

    @Autowired
    private fr.bricoloc.stocks_service.service.StockService stockService;
    @Autowired
    private fr.bricoloc.stocks_service.repository.ToolStockRepository stockRepository;

    @GetMapping("/ping")
    public String ping() {
        return "Le microservice de gestion des stocks BricoLoc est OPÉRATIONNEL !";
    }

    // Un POST est utilisé quand on modifie de la donnée (ici, on réserve)
    @PostMapping("/reserve/{toolId}/{quantity}")
    public String reserve(@PathVariable Long toolId, @PathVariable int quantity) {
        try {
            return stockService.reserveTool(toolId, quantity);
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @PostMapping("/admin/run-etl-migration")
    public String runEtlMigration() {
        // On simule le script ETL (Extract, Transform, Load)
        if (stockRepository.count() <= 1) { // Si la base est presque vide

            fr.bricoloc.stocks_service.model.ToolStock scie = new fr.bricoloc.stocks_service.model.ToolStock();
            scie.setToolName("Scie circulaire Makita (Migrée depuis Oracle)");
            scie.setTotalQuantity(12);
            scie.setAvailableQuantity(12);

            fr.bricoloc.stocks_service.model.ToolStock ponceuse = new fr.bricoloc.stocks_service.model.ToolStock();
            ponceuse.setToolName("Ponceuse Bosch (Migrée depuis Oracle)");
            ponceuse.setTotalQuantity(8);
            ponceuse.setAvailableQuantity(8);

            stockRepository.save(scie);
            stockRepository.save(ponceuse);

            return "✅ [ETL SUCCESS] 2 références extraites d'Oracle, transformées et chargées dans PostgreSQL.";
        }
        return "⚠️ Migration déjà effectuée.";
    }

    @PostMapping("/admin/stress-test-surbooking")
    public String stressTest() throws InterruptedException {
        // On va simuler 10 clients qui cliquent EXACTEMENT en même temps
        int numberOfClients = 10;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfClients);
        List<Callable<String>> tasks = new ArrayList<>();

        // On prépare les 10 requêtes (chacun veut réserver 1 perceuse ID 1)
        for (int i = 0; i < numberOfClients; i++) {
            final int clientId = i + 1;
            tasks.add(() -> {
                try {
                    return "Client " + clientId + " -> " + stockService.reserveTool(1L, 1);
                } catch (Exception e) {
                    return "Client " + clientId + " -> Erreur : " + e.getMessage();
                }
            });
        }

        // 3... 2... 1... GO ! On lance les 10 en parallèle
        List<Future<String>> results = executor.invokeAll(tasks);
        executor.shutdown();

        // On récolte les résultats pour les afficher
        StringBuilder report = new StringBuilder();
        report.append("🔥 CRASH TEST : 10 clients tentent d'acheter la perceuse ID 1 simultanément...\n\n");

        for (Future<String> result : results) {
            try {
                report.append(result.get()).append("\n");
            } catch (Exception e) {
                report.append("Erreur d'exécution\n");
            }
        }

        return report.toString();
    }
}