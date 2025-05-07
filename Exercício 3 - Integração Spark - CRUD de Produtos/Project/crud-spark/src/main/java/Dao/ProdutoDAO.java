package Dao;

import Modelo.Produto;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;

public class ProdutoDAO extends DAO {

    // Construtor da classe ProdutoDAO
    public ProdutoDAO() {
        super();
        conectar();
    }

    // Método para encerrar a conexão com o banco de dados
    public void finalize() {
        close();
    }

    // Métodos de interação com o banco de dados (Criar, Editar, Excluir)
    
    public boolean inserirProduto(Produto produto) {
        boolean status = false;
        
        try {
            Statement st = getConexao().createStatement();
            st.executeUpdate("INSERT INTO produtos (nome, preco, quantidade) " +
                    "VALUES ('" + produto.getNome() + "', " + produto.getPreco() + 
                    ", " + produto.getQuantidade() + ");");
            st.close();
            status = true;
        } catch (SQLException u) {
            throw new RuntimeException(u);
        }
        
        return status;
    }

    public boolean atualizarProduto(Produto produto) {
        boolean status = false;
        
        try {
            Statement st = getConexao().createStatement();
            String sql = "UPDATE produtos SET nome = '" + produto.getNome() + "', preco = " + produto.getPreco() + 
                    ", quantidade = " + produto.getQuantidade() + " WHERE id = " + produto.getId();
            st.executeUpdate(sql);
            st.close();
            status = true;
        } catch (SQLException u) {
            throw new RuntimeException(u);
        }
        
        return status;
    }

    public boolean excluirProduto(int id) {
        boolean status = false;
        
        try {
            Statement st = getConexao().createStatement();
            st.executeUpdate("DELETE FROM produtos WHERE id = " + id);
            st.close();
            status = true;
        } catch (SQLException u) {
            throw new RuntimeException(u);
        }
        
        return status;
    }

    // Métodos de leitura do banco de dados (Ler todos, ler um)
    
    public Produto getProduto(int id) {
        Produto produto = null;
        
        try {
            Statement st = getConexao().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            String sql = "SELECT * FROM produtos WHERE id = " + id;
            ResultSet rs = st.executeQuery(sql);
            
            if (rs.next()) {
                produto = new Produto(
                        rs.getInt("id"), 
                        rs.getString("nome"), 
                        rs.getDouble("preco"), 
                        rs.getInt("quantidade")
                );
            }
            st.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        
        return produto;
    }

    public List<Produto> getProdutos(String orderBy) {
        List<Produto> produtos = new ArrayList<>();
        
        try {
            Statement st = getConexao().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            String sql = "SELECT * FROM produtos" + ((orderBy.trim().length() == 0) ? "" : (" ORDER BY " + orderBy));
            ResultSet rs = st.executeQuery(sql);
            
            while (rs.next()) {
                Produto produto = new Produto(
                        rs.getInt("id"), 
                        rs.getString("nome"), 
                        rs.getDouble("preco"), 
                        rs.getInt("quantidade")
                );
                produtos.add(produto);
            }
            st.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        
        return produtos;
    }
}
