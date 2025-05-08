package Dao;

import java.sql.*;

public class DAO {
    private Connection conexao;
    
    public DAO() {
        conexao = null;
    }
    
    public boolean conectar() {
        String driverName = "org.postgresql.Driver";                    
        String serverName = "localhost";  // Endereço do servidor PostgreSQL
        String mydatabase = "produtos";  // Nome do banco de dados
        int porta = 5432;  // Porta do PostgreSQL
        String url = "jdbc:postgresql://" + serverName + ":" + porta + "/" + mydatabase;  // URL de conexão
        String username = "postgres";  // Nome de usuário do PostgreSQL
        String password = "1234";  // Senha do banco de dados
        boolean status = false;

        try {
            Class.forName(driverName);  // Carrega o driver JDBC do PostgreSQL
            conexao = DriverManager.getConnection(url, username, password);  // Estabelece a conexão
            status = (conexao != null);  // Verifica se a conexão foi bem-sucedida
            System.out.println("Conexão efetuada com o banco 'Produtos'!");
        } catch (ClassNotFoundException e) { 
            System.err.println("Conexão NÃO efetuada com o PostgreSQL -- Driver não encontrado -- " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Conexão NÃO efetuada com o PostgreSQL -- " + e.getMessage());
        }

        return status;
    }
    
    public boolean close() {
        boolean status = false;
        
        try {
            if (conexao != null && !conexao.isClosed()) {
                conexao.close();  // Fecha a conexão com o banco
                status = true;
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return status;
    }
    
    public Connection getConexao() {
        return conexao;
    }
}
