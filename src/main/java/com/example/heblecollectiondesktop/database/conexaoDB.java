package com.example.heblecollectiondesktop.database;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class conexaoDB {

    private static String host;
    private static String porta;
    private static String banco;
    private static String usuario;
    private static String senha;

    static {
        Properties prop = new Properties();
        try (InputStream input = new FileInputStream("config.properties")) {
            prop.load(input);
            host = prop.getProperty("db.host");
            porta = prop.getProperty("db.port");
            banco = prop.getProperty("db.name");
            usuario = prop.getProperty("db.user");
            senha = prop.getProperty("db.password");
        } catch (IOException ex) {
            System.err.println("Erro: Não foi possível carregar o arquivo 'config.properties'. Verifique se ele está na raiz do projeto.");
        }
    }

    public static Connection getConexao() throws SQLException {
        if (host == null || senha == null) {
            throw new SQLException("As configurações de conexão do banco de dados não foram carregadas corretamente!");
        }

        String url = String.format(
                "jdbc:mysql://%s:%s/%s?useSSL=false&serverTimezone=UTC",
                host, porta, banco
        );

        return DriverManager.getConnection(url, usuario, senha);
    }

    public static void main(String[] args) {
        try (Connection conexao = getConexao()) {
            if (conexao != null) {
                System.out.println("Conexão com o banco de dados realizada com sucesso!");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao conectar ao banco de dados: " + e.getMessage());
            e.printStackTrace();
        }
    }
}