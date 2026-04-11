package fr.bricoloc.stocks_service.config;

import fr.bricoloc.stocks_service.model.ToolStock;
import fr.bricoloc.stocks_service.repository.ToolStockRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(ToolStockRepository repository) {
        return args -> {
            // On vérifie si la base est vide pour ne pas la remplir en double à chaque
            // redémarrage
            if (repository.count() == 0) {
                ToolStock perceuse = new ToolStock();
                perceuse.setToolName("Perceuse sans fil Bosch");
                perceuse.setTotalQuantity(5);
                perceuse.setAvailableQuantity(5); // Il y a 5 perceuses en rayon

                repository.save(perceuse);
                System.out.println("======> Fausse donnée injectée : Perceuse Bosch (Stock : 5) <======");
            }
        };
    }
}