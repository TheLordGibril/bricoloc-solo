package fr.bricoloc.stocks_service.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "tool_stocks")
public class ToolStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tool_name", nullable = false)
    private String toolName;

    @Column(name = "total_quantity", nullable = false)
    private Integer totalQuantity;

    @Column(name = "available_quantity", nullable = false)
    private Integer availableQuantity;
}