package fr.bricoloc.stocks_service.controller;

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
}