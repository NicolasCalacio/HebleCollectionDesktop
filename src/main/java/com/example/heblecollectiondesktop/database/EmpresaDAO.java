package com.example.heblecollectiondesktop.database;

import com.example.heblecollectiondesktop.model.Empresa;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmpresaDAO {

    public List<Empresa> listarPendentes() {
        String sql = "SELECT * FROM empresa WHERE aprovado = 0 ORDER BY id DESC";
        List<Empresa> empresas = new ArrayList<>();

        try (Connection conn = conexaoDB.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Empresa e = new Empresa(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("cnpj"),
                        rs.getString("estilo"),
                        rs.getBoolean("aprovado")
                );
                empresas.add(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar empresas pendentes: " + e.getMessage(), e);
        }

        return empresas;
    }

    public List<Empresa> listarAprovadas() {
        String sql = "SELECT * FROM empresa WHERE aprovado = 1 ORDER BY nome ASC";
        List<Empresa> empresas = new ArrayList<>();

        try (Connection conn = conexaoDB.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Empresa e = new Empresa(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("cnpj"),
                        rs.getString("estilo"),
                        rs.getBoolean("aprovado")
                );
                empresas.add(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar empresas aprovadas: " + e.getMessage(), e);
        }

        return empresas;
    }

    public List<Empresa> listarTodas() {
        String sql = "SELECT * FROM empresa ORDER BY nome ASC";
        List<Empresa> empresas = new ArrayList<>();

        try (Connection conn = conexaoDB.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Empresa e = new Empresa(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("cnpj"),
                        rs.getString("estilo"),
                        rs.getBoolean("aprovado")
                );
                empresas.add(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar todas as empresas: " + e.getMessage(), e);
        }

        return empresas;
    }

    public boolean atualizar(Empresa empresa) {
        String sql = "UPDATE empresa SET nome = ?, cnpj = ?, estilo = ? WHERE id = ?";

        try (Connection conn = conexaoDB.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, empresa.getNome());
            stmt.setString(2, empresa.getCnpj());
            stmt.setString(3, empresa.getEstilo());
            stmt.setInt(4, empresa.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar dados da empresa: " + e.getMessage(), e);
        }
    }

    public boolean atualizarCampos(int id, String nome, String cnpj, String estilo) {
        String sql = "UPDATE empresa SET nome = ?, cnpj = ?, estilo = ? WHERE id = ?";

        try (Connection conn = conexaoDB.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nome);
            stmt.setString(2, cnpj);
            stmt.setString(3, estilo);
            stmt.setInt(4, id);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar campos da empresa: " + e.getMessage(), e);
        }
    }

    public boolean atualizarStatusAprovacao(int id, boolean aprovado) {
        String sql = "UPDATE empresa SET aprovado = ? WHERE id = ?";

        try (Connection conn = conexaoDB.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, aprovado);
            stmt.setInt(2, id);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar status da empresa: " + e.getMessage(), e);
        }
    }

    public Empresa buscarPorId(int id) {
        String sql = "SELECT * FROM empresa WHERE id = ?";

        try (Connection conn = conexaoDB.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Empresa(
                            rs.getInt("id"),
                            rs.getString("nome"),
                            rs.getString("cnpj"),
                            rs.getString("estilo"),
                            rs.getBoolean("aprovado")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar empresa por ID: " + e.getMessage(), e);
        }

        return null;
    }

    public boolean deletar(int idEmpresa) {
        String sql = "DELETE FROM empresa WHERE id = ?";
        try (Connection conn = conexaoDB.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idEmpresa);
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao deletar empresa: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}