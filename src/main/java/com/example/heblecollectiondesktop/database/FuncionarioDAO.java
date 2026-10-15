package com.example.heblecollectiondesktop.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.heblecollectiondesktop.model.Cargo;
import com.example.heblecollectiondesktop.model.Funcionario;
import com.example.heblecollectiondesktop.model.Gerente;

public class FuncionarioDAO {

    public Funcionario autenticar(String matricula, String senha) throws SQLException {
        String sql = "SELECT * FROM funcionarios WHERE matricula = ? AND senha = ? LIMIT 1";

        try (Connection conexao = conexaoDB.getConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, matricula);
            stmt.setString(2, senha);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("idfuncionarios");
                    String mat = rs.getString("matricula");
                    String pass = rs.getString("senha");
                    String cargoBanco = rs.getString("cargo");

                    Cargo cargoEnum = Cargo.FUNCIONARIO;
                    if (cargoBanco != null && !cargoBanco.isBlank()) {
                        try {
                            cargoEnum = Cargo.valueOf(cargoBanco.trim().toUpperCase());
                        } catch (IllegalArgumentException e) {
                            System.err.println("Aviso: Cargo desconhecido no banco ('" + cargoBanco + "'). Assumindo FUNCIONARIO.");
                        }
                    }

                    if (cargoEnum == Cargo.GERENTE) {
                        return new Gerente(id, mat, pass);
                    } else {
                        return new Funcionario(id, mat, pass, cargoEnum);
                    }
                }
            }
        }
        return null;
    }

    public List<Funcionario> listarTodos() throws SQLException {
        List<Funcionario> lista = new ArrayList<>();
        String sql = "SELECT * FROM funcionarios ORDER BY idfuncionarios DESC";

        try (Connection conexao = conexaoDB.getConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("idfuncionarios");
                String mat = rs.getString("matricula");
                String pass = rs.getString("senha");
                String cargoBanco = rs.getString("cargo");

                Cargo cargoEnum = Cargo.FUNCIONARIO;
                if (cargoBanco != null && !cargoBanco.isBlank()) {
                    try {
                        cargoEnum = Cargo.valueOf(cargoBanco.trim().toUpperCase());
                    } catch (IllegalArgumentException e) {
                        System.err.println("Aviso: Cargo desconhecido no banco ('" + cargoBanco + "'). Assumindo FUNCIONARIO.");
                    }
                }

                if (cargoEnum == Cargo.GERENTE) {
                    lista.add(new Gerente(id, mat, pass));
                } else {
                    lista.add(new Funcionario(id, mat, pass, cargoEnum));
                }
            }
        }
        return lista;
    }

    public void salvar(Funcionario funcionario) throws SQLException {
        String sql = "INSERT INTO funcionarios (matricula, senha, cargo) VALUES (?, ?, ?)";

        try (Connection conexao = conexaoDB.getConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, funcionario.getMatricula());
            stmt.setString(2, funcionario.getSenha());
            stmt.setString(3, funcionario.getCargo().name());

            stmt.executeUpdate();
        }
    }

    public boolean atualizar(Funcionario funcionario) {
        String sql = "UPDATE funcionarios SET matricula = ?, senha = ?, cargo = ? WHERE idfuncionarios = ?";

        try (Connection conn = conexaoDB.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, funcionario.getMatricula());
            stmt.setString(2, funcionario.getSenha());
            stmt.setString(3, funcionario.getCargo().name());
            stmt.setInt(4, funcionario.getId());

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao executar UPDATE no banco de dados: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deletar(int idFuncionarioDeletar) throws SQLException {
        String sqlDelete = "DELETE FROM funcionarios WHERE idfuncionarios = ?";

        try (Connection conn = conexaoDB.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sqlDelete)) {

            stmt.setInt(1, idFuncionarioDeletar);
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        }
    }

    public boolean deletar(int idFuncionarioDeletar, String matriculaModerador, String motivo, String observacao) throws SQLException {
        String sqlSelect = "SELECT matricula FROM funcionarios WHERE idfuncionarios = ?";
        String sqlDelete = "DELETE FROM funcionarios WHERE idfuncionarios = ?";
        String sqlLog = "INSERT INTO logs_moderacao (funcionario_id, acao, alvo_afetado, detalhes, data_acao) VALUES (?, ?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = conexaoDB.getConexao();
            conn.setAutoCommit(false);

            String alvoAfetado = "ID: " + idFuncionarioDeletar;
            try (PreparedStatement stmtSelect = conn.prepareStatement(sqlSelect)) {
                stmtSelect.setInt(1, idFuncionarioDeletar);
                try (ResultSet rs = stmtSelect.executeQuery()) {
                    if (rs.next()) {
                        alvoAfetado = rs.getString("matricula");
                    }
                }
            }

            try (PreparedStatement stmtDelete = conn.prepareStatement(sqlDelete)) {
                stmtDelete.setInt(1, idFuncionarioDeletar);
                stmtDelete.executeUpdate();
            }

            try (PreparedStatement stmtLog = conn.prepareStatement(sqlLog)) {
                stmtLog.setString(1, matriculaModerador);
                stmtLog.setString(2, "EXCLUSAO_FUNCIONARIO");
                stmtLog.setString(3, alvoAfetado);
                stmtLog.setString(4, "Motivo: " + motivo + (observacao.isBlank() ? "" : " | Obs: " + observacao));
                stmtLog.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
                stmtLog.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }
}