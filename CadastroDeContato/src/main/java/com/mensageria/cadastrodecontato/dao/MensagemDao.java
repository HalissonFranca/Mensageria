package com.mensageria.cadastrodecontato.dao;

import com.mensageria.cadastrodecontato.cinfig.ConnectionFactory;
import com.mensageria.cadastrodecontato.model.Mensage;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MensagemDao implements IMensagemDAO{
    @Override
    public Mensage create(Mensage menssage) {
        String query = "INSERT INTO mensageria " + "(nome, email, telefone) "+
                "VALUES (?,?,?)";
        Mensage m = new Mensage();
        try (Connection con = ConnectionFactory.getConnection()){
            PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, menssage.getNome());
            ps.setString(2, menssage.getEmail());
            ps.setString(3, menssage.getTelefone());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()){

                       m.setId(rs.getLong(1));
                       m.setNome(rs.getString(2));
                        m.setEmail(rs.getString(3));
                        m.setTelefone(rs.getString(4));
            }
        } catch (SQLException e){
            throw new RuntimeException(e);
        }

        return m;
    }

    @Override
    public Optional<Mensage> read(Long id) {
        Mensage m = new Mensage();
        String query = "SELECT * FROM mensageria WHERE id = ?";
        try (Connection con = ConnectionFactory.getConnection()){
            PreparedStatement ps = con.prepareStatement(query);
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next());
            m.setId(rs.getLong("id"));
            m.setNome(rs.getString("nome"));
            m.setEmail(rs.getString("email"));
            m.setTelefone(rs.getString("telefone"));

        } catch (SQLException e){
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(m);
    }

    @Override
    public Mensage delete(Long id) {
        Mensage m = null;
        String query = "DELETE FROM mensageria WHERE id = ? RETURNING id, nome, email, telefone";

        try (Connection con = ConnectionFactory.getConnection()) {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setLong(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // Se o registro for excluído, m será preenchido com os dados do registro
                m = new Mensage();
                m.setId(rs.getLong("id"));
                m.setNome(rs.getString("nome"));
                m.setEmail(rs.getString("email"));
                m.setTelefone(rs.getString("telefone"));
            } else {
                // Se não encontrar nenhum registro com o ID especificado
                throw new SQLException("Registro não encontrado para o ID: " + id);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return m;
    }

    @Override
    public List<Mensage> readAll() {
        List<Mensage> mensagens = new ArrayList<>();
        String query = "SELECT * FROM mensageria";

        try (Connection con = ConnectionFactory.getConnection()) {
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Mensage msg = new Mensage();
                msg.setId(rs.getLong("id"));
                msg.setNome(rs.getString("nome"));
                msg.setEmail(rs.getString("email"));
                msg.setTelefone(rs.getString("telefone"));
                mensagens.add(msg);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao recuperar os registros", e);
        }

        return mensagens;
    }


}
