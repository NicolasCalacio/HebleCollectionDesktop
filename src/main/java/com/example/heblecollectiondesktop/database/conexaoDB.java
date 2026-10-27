package com.example.heblecollectiondesktop.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;


public class conexaoDB {

    private static final HikariDataSource DATA_SOURCE;
    private static final String ERRO_CONFIGURACAO;

    static {
        HikariDataSource ds = null;
        String erro = null;

        try {
            Properties prop = carregarConfiguracao();

            String host = obrigatoria(prop, "db.host");
            String porta = prop.getProperty("db.port", "3306").trim();
            String banco = obrigatoria(prop, "db.name");
            String usuario = obrigatoria(prop, "db.user");
            String senha = obrigatoria(prop, "db.password");
            String sslMode = prop.getProperty("db.sslMode", "REQUIRED").trim();

            HikariConfig cfg = new HikariConfig();
            cfg.setJdbcUrl(String.format("jdbc:mysql://%s:%s/%s?sslMode=%s&serverTimezone=UTC",
                    host, porta, banco, sslMode));
            cfg.setUsername(usuario);
            cfg.setPassword(senha);
            cfg.setPoolName("heble-pool");
            cfg.setMaximumPoolSize(5);
            cfg.setMinimumIdle(1);
            cfg.setConnectionTimeout(10_000);
            cfg.setInitializationFailTimeout(-1);

            ds = new HikariDataSource(cfg);

            final HikariDataSource paraFechar = ds;
            Runtime.getRuntime().addShutdownHook(new Thread(paraFechar::close));

        } catch (Exception ex) {
            erro = "As configurações de conexão do banco de dados não foram carregadas corretamente: " + ex.getMessage();
            System.err.println("Erro: " + erro);
        }

        DATA_SOURCE = ds;
        ERRO_CONFIGURACAO = erro;
    }

    public static Connection getConexao() throws SQLException {
        if (DATA_SOURCE == null) {
            throw new SQLException(ERRO_CONFIGURACAO);
        }
        return DATA_SOURCE.getConnection();
    }

    private static Properties carregarConfiguracao() throws IOException {
        Properties prop = new Properties();

        try (InputStream input = new FileInputStream("config.properties")) {
            prop.load(input);
            return prop;
        } catch (IOException ignorado) {

        }


        try (InputStream input = conexaoDB.class.getResourceAsStream("/config.properties")) {
            if (input == null) {
                throw new IOException("Arquivo 'config.properties' não encontrado (nem na pasta do programa, nem no classpath).");
            }
            prop.load(input);
            return prop;
        }
    }

    private static String obrigatoria(Properties prop, String chave) {
        String valor = prop.getProperty(chave);
        if (valor == null || valor.isBlank()) {
            throw new IllegalStateException("Falta a chave '" + chave + "' no config.properties");
        }
        return valor.trim();
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