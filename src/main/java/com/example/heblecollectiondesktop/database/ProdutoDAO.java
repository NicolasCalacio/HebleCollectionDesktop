package com.example.heblecollectiondesktop.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.example.heblecollectiondesktop.model.Produto;

public class ProdutoDAO {

    public List<Produto> listarTodos() {
        String sql = "SELECT p.*, COALESCE(e.nome, 'Não vinculada') AS nome_empresa " +
                "FROM produtos p " +
                "LEFT JOIN empresa e ON p.empresa_id = e.id " +
                "ORDER BY p.id DESC";
        List<Produto> lista = new ArrayList<>();

        try (Connection conexao = conexaoDB.getConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Produto p = new Produto(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("descricao"),
                        rs.getDouble("preco"),
                        rs.getInt("quantidade_estoque"),
                        rs.getString("status"),
                        rs.getString("nome_empresa"),
                        rs.getBoolean("ativo")
                );
                lista.add(p);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar produtos: " + e.getMessage(), e);
        }

        return lista;
    }

    public List<Produto> listarPendentes() {
        String sql = "SELECT p.*, COALESCE(e.nome, 'Não vinculada') AS nome_empresa " +
                "FROM produtos p " +
                "LEFT JOIN empresa e ON p.empresa_id = e.id " +
                "WHERE p.status = 'PENDENTE' OR p.status IS NULL " +
                "ORDER BY p.id DESC";
        List<Produto> lista = new ArrayList<>();

        try (Connection conexao = conexaoDB.getConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Produto p = new Produto(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("descricao"),
                        rs.getDouble("preco"),
                        rs.getInt("quantidade_estoque"),
                        rs.getString("status"),
                        rs.getString("nome_empresa"),
                        rs.getBoolean("ativo")
                );
                lista.add(p);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar produtos pendentes: " + e.getMessage(), e);
        }

        return lista;
    }

    public boolean atualizarProduto(Produto produto) {
        String sql = "UPDATE produtos SET nome = ?, descricao = ?, preco = ?, quantidade_estoque = ?, status = ?, ativo = ? WHERE id = ?";

        try (Connection conexao = conexaoDB.getConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, produto.getNome());
            stmt.setString(2, produto.getDescricao());
            stmt.setDouble(3, produto.getPreco());
            stmt.setInt(4, produto.getQuantidadeEstoque());
            stmt.setString(5, produto.getStatus());
            stmt.setBoolean(6, produto.isAtivo());
            stmt.setInt(7, produto.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar produto: " + e.getMessage(), e);
        }
    }

    public boolean atualizarStatus(int idProduto, String novoStatus) {
        String sql = "UPDATE produtos SET status = ? WHERE id = ?";

        try (Connection conexao = conexaoDB.getConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, novoStatus);
            stmt.setInt(2, idProduto);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar status do produto: " + e.getMessage(), e);
        }
    }

    public boolean deletar(int idProduto) {
        String sql = "UPDATE produtos SET ativo = false WHERE id = ?";

        try (Connection conexao = conexaoDB.getConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, idProduto);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar produto: " + e.getMessage(), e);
        }
    }

    public List<Produto> buscarPorTermo(String termo) {
        List<Produto> lista = new ArrayList<>();
        String sql = "SELECT p.*, COALESCE(e.nome, 'Não vinculada') AS nome_empresa " +
                "FROM produtos p " +
                "LEFT JOIN empresa e ON p.empresa_id = e.id " +
                "WHERE p.ativo = 1 AND (p.nome LIKE ? OR p.descricao LIKE ? OR e.nome LIKE ?) " +
                "ORDER BY p.id DESC";

        try (Connection conn = conexaoDB.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String filtro = "%" + termo + "%";
            stmt.setString(1, filtro);
            stmt.setString(2, filtro);
            stmt.setString(3, filtro);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Produto produto = new Produto(
                            rs.getInt("id"),
                            rs.getString("nome"),
                            rs.getString("descricao"),
                            rs.getDouble("preco"),
                            rs.getInt("quantidade_estoque"),
                            rs.getString("status"),
                            rs.getString("nome_empresa"),
                            rs.getBoolean("ativo") // <--- Adicionado aqui
                    );
                    lista.add(produto);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}
