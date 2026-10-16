package com.example.heblecollectiondesktop.database;

import com.example.heblecollectiondesktop.model.Consumidores;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ConsumidoresDAO {

    public List<Consumidores> listar() {
        String sql = "SELECT * FROM consumidores";
        List<Consumidores> lista = new ArrayList<>();

        try (Connection conn = conexaoDB.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Consumidores consumidor = new Consumidores(
                        rs.getInt("IDconsumidor"),
                        rs.getString("nomconsumidor"),
                        rs.getString("cpfconsumidor"),
                        rs.getString("senhaconsumidor"),
                        rs.getString("emailconsumidor"),
                        rs.getString("celularconsumidor"),
                        rs.getString("cepconsumidor"),
                        rs.getString("enderecoconsumidor"),
                        rs.getString("dataNascconsumidor"),
                        rs.getString("statusconsumidor")
                );
                lista.add(consumidor);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao listar consumidores: " + e.getMessage());
        }

        return lista;
    }

    public List<Consumidores> pesquisarPorNome(String nome) {
        String sql = "SELECT * FROM consumidores WHERE nomconsumidor LIKE ?";
        List<Consumidores> lista = new ArrayList<>();

        try (Connection conn = conexaoDB.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + nome + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Consumidores consumidor = new Consumidores(
                            rs.getInt("IDconsumidor"),
                            rs.getString("nomconsumidor"),
                            rs.getString("cpfconsumidor"),
                            rs.getString("senhaconsumidor"),
                            rs.getString("emailconsumidor"),
                            rs.getString("celularconsumidor"),
                            rs.getString("cepconsumidor"),
                            rs.getString("enderecoconsumidor"),
                            rs.getString("dataNascconsumidor"),
                            rs.getString("statusconsumidor")
                    );
                    lista.add(consumidor);
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao pesquisar consumidores: " + e.getMessage());
        }

        return lista;
    }

    public Consumidores buscarPorId(int idConsumidor) {
        String sql = "SELECT * FROM consumidores WHERE IDconsumidor = ?";
        Consumidores consumidor = null;

        try (Connection conn = conexaoDB.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idConsumidor);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    consumidor = new Consumidores(
                            rs.getInt("IDconsumidor"),
                            rs.getString("nomconsumidor"),
                            rs.getString("cpfconsumidor"),
                            rs.getString("senhaconsumidor"),
                            rs.getString("emailconsumidor"),
                            rs.getString("celularconsumidor"),
                            rs.getString("cepconsumidor"),
                            rs.getString("enderecoconsumidor"),
                            rs.getString("dataNascconsumidor"),
                            rs.getString("statusconsumidor")
                    );
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar consumidor por ID: " + e.getMessage());
        }

        return consumidor;
    }

    public void atualizar(Consumidores consumidor) {
        String sql = "UPDATE consumidores SET nomconsumidor = ?, cpfconsumidor = ?, senhaconsumidor = ?, emailconsumidor = ?, celularconsumidor = ?, cepconsumidor = ?, enderecoconsumidor = ?, dataNascconsumidor = ?, statusconsumidor = ? WHERE IDconsumidor = ?";

        try (Connection conn = conexaoDB.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, consumidor.getNomconsumidor());
            stmt.setString(2, consumidor.getCpfconsumidor());
            stmt.setString(3, consumidor.getSenhaconsumidor());
            stmt.setString(4, consumidor.getEmailconsumidor());
            stmt.setString(5, consumidor.getCelularconsumidor());
            stmt.setString(6, consumidor.getCepconsumidor());
            stmt.setString(7, consumidor.getEnderecoconsumidor());
            stmt.setString(8, consumidor.getDataNascconsumidor());
            stmt.setString(9, consumidor.getStatusconsumidor());
            stmt.setInt(10, consumidor.getIDconsumidor());

            stmt.executeUpdate();
            System.out.println("Consumidor atualizado com sucesso!");

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar consumidor: " + e.getMessage());
        }
    }

    public boolean atualizarStatusConsumidor(int idConsumidor, String novoStatus) {
        String sql = "UPDATE consumidores SET statusconsumidor = ? WHERE IDconsumidor = ?";

        try (Connection conn = conexaoDB.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, novoStatus);
            stmt.setInt(2, idConsumidor);

            int linhasAfetadas = stmt.executeUpdate();
            System.out.println("Status do consumidor atualizado com sucesso!");
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar o status do consumidor: " + e.getMessage());
            return false;
        }
    }

    public boolean deletar(int idConsumidor) {
        String sql = "DELETE FROM consumidores WHERE IDconsumidor = ?";

        try (Connection conn = conexaoDB.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idConsumidor);
            stmt.executeUpdate();
            System.out.println("Consumidor excluído com sucesso!");

        } catch (SQLException e) {
            System.err.println("Erro ao excluir consumidor: " + e.getMessage());
        }

        return false;
    }
}