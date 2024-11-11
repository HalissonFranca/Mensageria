package com.mensageria.cadastrodecontato.dao;

import com.mensageria.cadastrodecontato.model.Mensage;

import java.util.List;
import java.util.Optional;

public interface IMensagemDAO {
    Mensage create(Mensage menssage);
    Optional<Mensage> read(Long id);
    Mensage delete(Long id);
    List<Mensage> readAll();
}
