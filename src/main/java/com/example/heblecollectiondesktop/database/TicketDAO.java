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
        String sql = "SELECT * FROM tickets ORDER BY criado_em DESC";
        List<Ticket> lista = new ArrayList<>();

        try (Connection conexao = conexaoDB.getConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

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
        String sql = "UPDATE tickets SET status = ? WHERE id = ?";

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
        String sql = "DELETE FROM tickets WHERE id = ?";

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

    public boolean Adicionar(Ticket ticket) {
        String sql = """
            INSERT INTO tickets 
            (assunto, descricao, status, funcionario_id) 
            VALUES (?, ?, ?, ?)
            """;

        try (Connection conexao = conexaoDB.getConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, ticket.getAssunto());
            stmt.setString(2, ticket.getDescricao());

            stmt.setString(3, ticket.getStatus() != null ? ticket.getStatus() : "pendente");

            if (ticket.getFuncionarioId() > 0) {
                stmt.setInt(4, ticket.getFuncionarioId());
            } else {
                stmt.setNull(4, java.sql.Types.INTEGER);
            }

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao adicionar ticket: " + e.getMessage(), e);
        }
    }
}