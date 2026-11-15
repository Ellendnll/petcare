package com.petcare.pet;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "pets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome do pet é obrigatório")
    private String nome;

    private String especie; 
    
    private String raca;

    private LocalDate nascimento;

    @Column(name = "usuario_id")
    private Long usuarioId;
}