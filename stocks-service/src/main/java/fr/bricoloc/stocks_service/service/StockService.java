package fr.bricoloc.stocks_service.service;

import fr.bricoloc.stocks_service.model.ToolStock;
import fr.bricoloc.stocks_service.repository.ToolStockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StockService {

    @Autowired
    private ToolStockRepository stockRepository;

    // Le @Transactional est CRITIQUE. Il garantit que tout se fait, ou rien ne se
    // fait (ACID)
    @Transactional
    public String reserveTool(Long toolId, int quantityToReserve) {

        // 1. On va chercher l'outil. S'il n'existe pas, on arrête tout.
        ToolStock tool = stockRepository.findByIdWithLock(toolId)
                .orElseThrow(() -> new RuntimeException("Erreur : Outil introuvable (ID: " + toolId + ")"));

        // 2. Vérification de la disponibilité
        if (tool.getAvailableQuantity() >= quantityToReserve) {

            // 3. Si ok, on diminue le stock disponible
            tool.setAvailableQuantity(tool.getAvailableQuantity() - quantityToReserve);
            stockRepository.save(tool);

            return "Succès : Réservation confirmée pour " + quantityToReserve + "x " + tool.getToolName()
                    + ". Nouveau stock : " + tool.getAvailableQuantity();
        } else {
            // 4. Si pas ok, on refuse. C'est l'anti-surbooking !
            return "Échec : Impossible de réserver " + quantityToReserve + "x " + tool.getToolName()
                    + ". Stock insuffisant (" + tool.getAvailableQuantity() + " restants).";
        }
    }
}