package fr.bricoloc.stocks_service.repository;

import fr.bricoloc.stocks_service.model.ToolStock;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ToolStockRepository extends JpaRepository<ToolStock, Long> {

    // On redéfinit le findById pour y ajouter un verrou PESSIMISTE
    // Ça veut dire : "Si je lis cette ligne, personne d'autre n'a le droit de la
    // modifier tant que je n'ai pas fini !"
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM ToolStock t WHERE t.id = :id")
    Optional<ToolStock> findByIdWithLock(@Param("id") Long id);
}