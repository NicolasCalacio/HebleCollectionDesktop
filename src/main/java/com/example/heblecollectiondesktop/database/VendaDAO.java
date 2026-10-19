package com.example.heblecollectiondesktop.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.example.heblecollectiondesktop.model.ItemVenda;
import com.example.heblecollectiondesktop.model.Venda;

public class VendaDAO {

    public boolean salvar(Venda venda) throws SQLException {
        String sqlVenda = "INSERT INTO vendas (consumidor_id, cupom_id, valor_subtotal, valor_frete, valor_desconto, valor_total, status_venda) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String sqlItem = "INSERT INTO itens_venda (venda_id, produto_id, quantidade, preco_unitario) VALUES (?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = conexaoDB.getConexao();
            conn.setAutoCommit(false);


            int vendaIdGenerado;
            try (PreparedStatement stmtVenda = conn.prepareStatement(sqlVenda, Statement.RETURN_GENERATED_KEYS)) {
                stmtVenda.setInt(1, venda.getConsumidorId());

                if (venda.getCupomId() != null) {
                    stmtVenda.setInt(2, venda.getCupomId());
                } else {
                    stmtVenda.setNull(2, Types.INTEGER);
                }

                stmtVenda.setBigDecimal(3, venda.getValorSubtotal());
                stmtVenda.setBigDecimal(4, venda.getValorFrete());
                stmtVenda.setBigDecimal(5, venda.getValorDesconto());
                stmtVenda.setBigDecimal(6, venda.getValorTotal());
                stmtVenda.setString(7, venda.getStatusVenda() != null ? venda.getStatusVenda() : "PENDENTE");

                stmtVenda.executeUpdate();

                try (ResultSet rs = stmtVenda.getGeneratedKeys()) {
                    if (rs.next()) {
                        vendaIdGenerado = rs.getInt(1);
                    } else {
                        throw new SQLException("Falha ao obter o ID da venda gerada.");
                    }
                }
            }

            try (PreparedStatement stmtItem = conn.prepareStatement(sqlItem)) {
                for (ItemVenda item : venda.getItens()) {
                    stmtItem.setInt(1, vendaIdGenerado);
                    stmtItem.setInt(2, item.getProdutoId());
                    stmtItem.setInt(3, item.getQuantidade());
                    stmtItem.setBigDecimal(4, item.getPrecoUnitario());
                    stmtItem.addBatch();
                }
                stmtItem.executeBatch();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public List<ItemVenda> listarItensPorVenda(int vendaId) throws SQLException {
        List<ItemVenda> itens = new ArrayList<>();
        String sql = "SELECT * FROM itens_venda WHERE venda_id = ?";

        try (Connection conn = conexaoDB.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, vendaId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ItemVenda item = new ItemVenda();
                    item.setIditem(rs.getInt("iditem"));
                    item.setVendaId(rs.getInt("venda_id"));
                    item.setProdutoId(rs.getInt("produto_id"));
                    item.setQuantidade(rs.getInt("quantidade"));
                    item.setPrecoUnitario(rs.getBigDecimal("preco_unitario"));
                    itens.add(item);
                }
            }
        }
        return itens;
    }

    public List<Venda> listarTodas() throws SQLException {
        List<Venda> vendas = new ArrayList<>();
        String sql = "SELECT * FROM vendas ORDER BY criado_em DESC";

        try (Connection conn = conexaoDB.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Venda v = new Venda();
                v.setIdvenda(rs.getInt("idvenda"));
                v.setConsumidorId(rs.getInt("consumidor_id"));
                v.setCupomId((Integer) rs.getObject("cupom_id"));
                v.setValorSubtotal(rs.getBigDecimal("valor_subtotal"));
                v.setValorFrete(rs.getBigDecimal("valor_frete"));
                v.setValorDesconto(rs.getBigDecimal("valor_desconto"));
                v.setValorTotal(rs.getBigDecimal("valor_total"));
                v.setStatusVenda(rs.getString("status_venda"));

                Timestamp timestamp = rs.getTimestamp("criado_em");
                if (timestamp != null) {
                    v.setCriadoEm(timestamp.toLocalDateTime());
                }

                vendas.add(v);
            }
        }
        return vendas;
    }
}