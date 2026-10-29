package br.com.soulup.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Fabrica de conexoes com o Oracle.
 * As credenciais vem SOMENTE das variaveis de ambiente DB_URL, DB_USER e DB_PASSWORD,
 * assim a senha nunca fica salva no codigo nem no GitHub.
 */
public class ConnectionFactory {

    private ConnectionFactory() {
        // classe utilitaria: nao precisa ser instanciada
    }

    public static Connection getConnection() throws SQLException {
        String url = lerVariavel("DB_URL");
        String usuario = lerVariavel("DB_USER");
        String senha = lerVariavel("DB_PASSWORD");
        return DriverManager.getConnection(url, usuario, senha);
    }

    private static String lerVariavel(String nome) {
        String valor = System.getenv(nome);
        if (valor == null || valor.isBlank()) {
            throw new IllegalStateException("Variável de ambiente " + nome + " não configurada");
        }
        return valor;
    }
}
