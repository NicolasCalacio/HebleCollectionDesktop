package com.example.heblecollectiondesktop.database;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.example.heblecollectiondesktop.model.Cupom;

public class CupomDAO {

    private static final String COLUNAS = "id, codigo, desconto_percentual, valor_minimo, ativo, validade";

    public List<Cupom> listarTodos() throws SQLException {
        List<Cupom> lista = new ArrayList<>();
        String sql = "SELECT " + COLUNAS + " FROM cupons ORDER BY id DESC";

        try (Connection conexao = conexaoDB.getConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearResultSetParaCupom(rs));
            }
        }
        return lista;
    }

    public Cupom buscarPorCodigo(String codigo) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM cupons WHERE codigo = ?";

        try (Connection conexao = conexaoDB.getConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, codigo);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSetParaCupom(rs);
                }
            }
        }
        return null;
    }

    public Cupom buscarPorId(int id) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM cupons WHERE id = ?";

        try (Connection conexao = conexaoDB.getConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSetParaCupom(rs);
                }
            }
        }
        return null;
    }

    public boolean salvar(Cupom cupom) throws SQLException {
        String sql = "INSERT INTO cupons (codigo, desconto_percentual, valor_minimo, ativo, validade) VALUES (?, ?, ?, ?, ?)";

        try (Connection conexao = conexaoDB.getConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            preencherCampos(stmt, cupom);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean atualizar(Cupom cupom) throws SQLException {
        String sql = "UPDATE cupons SET codigo = ?, desconto_percentual = ?, valor_minimo = ?, ativo = ?, validade = ? WHERE id = ?";

        try (Connection conexao = conexaoDB.getConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            preencherCampos(stmt, cupom);
            stmt.setInt(6, cupom.getId());

            return stmt.executeUpdate() > 0;
        }
    }

    public boolean deletar(int id) throws SQLException {
        String sql = "DELETE FROM cupons WHERE id = ?";

        try (Connection conexao = conexaoDB.getConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean deletar(Cupom cupom) throws SQLException {
        if (cupom == null) return false;
        return deletar(cupom.getId());
    }

    public boolean excluir(int id) throws SQLException {
        return deletar(id);
    }

    private void preencherCampos(PreparedStatement stmt, Cupom cupom) throws SQLException {
        if (cupom.getValidade() == null) {
            throw new SQLException("A validade do cupom é obrigatória.");
        }

        stmt.setString(1, cupom.getCodigo());
        stmt.setBigDecimal(2, duasCasas(cupom.getDescontoPercentual()));
        stmt.setBigDecimal(3, duasCasas(cupom.getValorMinimo()));
        stmt.setBoolean(4, cupom.isAtivo());
        stmt.setDate(5, new java.sql.Date(cupom.getValidade().getTime()));
    }

    private static BigDecimal duasCasas(double valor) {
        return BigDecimal.valueOf(valor).setScale(2, RoundingMode.HALF_UP);
    }

    private Cupom mapearResultSetParaCupom(ResultSet rs) throws SQLException {
        return new Cupom(
                rs.getInt("id"),
                rs.getString("codigo"),
                rs.getDouble("desconto_percentual"),
                rs.getDouble("valor_minimo"),
                rs.getBoolean("ativo"),
                rs.getDate("validade")
        );
    }
}