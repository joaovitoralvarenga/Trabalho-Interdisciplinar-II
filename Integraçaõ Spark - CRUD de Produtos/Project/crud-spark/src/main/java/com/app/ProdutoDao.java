package com.app;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDao {
    private Connection connection;

    public ProdutoDao() {
        try {
            this.connection = Database.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void salvarProduto(Produto produto) throws SQLException {
        String sql = "INSERT INTO produtos (nome, preco, quantidade) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, produto.getNome());
            stmt.setBigDecimal(2, produto.getPreco());
            stmt.setInt(3, produto.getQuantidade());
            stmt.executeUpdate();
        }
    }

    public List<Produto> listarProdutos() throws SQLException {
        String sql = "SELECT * FROM produtos";
        List<Produto> produtos = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Produto produto = new Produto(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getBigDecimal("preco"),
                    rs.getInt("quantidade")
                );
                produtos.add(produto);
            }
        }
        return produtos;
    }

    public Produto buscarProdutoPorId(int id) throws SQLException {
        String sql = "SELECT * FROM produtos WHERE id = ?";
        Produto produto = null;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    produto = new Produto(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getBigDecimal("preco"),
                        rs.getInt("quantidade")
                    );
                }
            }
        }
        return produto;
    }

    public void atualizarProduto(Produto produto) throws SQLException {
        String sql = "UPDATE produtos SET nome = ?, preco = ?, quantidade = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, produto.getNome());
            stmt.setBigDecimal(2, produto.getPreco());
            stmt.setInt(3, produto.getQuantidade());
            stmt.setInt(4, produto.getId());
            stmt.executeUpdate();
        }
    }

    public void deletarProduto(int id) throws SQLException {
        String sql = "DELETE FROM produtos WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}
