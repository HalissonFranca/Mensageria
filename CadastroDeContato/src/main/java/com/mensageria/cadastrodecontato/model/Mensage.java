package com.mensageria.cadastrodecontato.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Mensage {
    Long id;
    String nome;
    String email;
    String telefone;
}
