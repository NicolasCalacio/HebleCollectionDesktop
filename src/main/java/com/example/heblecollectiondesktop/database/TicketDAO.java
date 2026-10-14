package com.example.heblecollectiondesktop.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.example.heblecollectiondesktop.model.Ticket;

public class TicketDAO {

    public List<Ticket> listarTodos() {
        String sql = "SELECT * FROM login_schema.tickets ORDER BY criado_em DESC";
        List<Ticket> lista = new ArrayList<>();

        try (Connection conexao = conexaoDB.getConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                // Lê diretamente o texto do status da base de dados
                String status = rs.getString("status");

                Ticket t = new Ticket(
                        rs.getInt("id"),
                        rs.getString("assunto"),
                        rs.getString("descricao"),
                        status,
                        rs.getInt("funcionario_id")
                );
                lista.add(t);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar todos os tickets: " + e.getMessage(), e);
        }

        return lista;
    }

    public boolean atualizarStatus(int idTicket, String novoStatus) {
        String sql = "UPDATE login_schema.tickets SET status = ? WHERE id = ?";

        try (Connection conexao = conexaoDB.getConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, novoStatus);
            stmt.setInt(2, idTicket);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar status do ticket: " + e.getMessage(), e);
        }
    }

    public boolean deletar(int idTicket) {
        String sql = "DELETE FROM login_schema.tickets WHERE id = ?";

        try (Connection conexao = conexaoDB.getConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, idTicket);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao deletar ticket: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}