package dev.tfduque.textO.textTools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

	public static String status = "Não conectou...";

	public static java.sql.Connection getConnection() {

		Connection connection = null;

		try {

			String driverName = "com.mysql.cj.jdbc.Driver";

			Class.forName(driverName);

			String serverName = "localhost";

			String mydatabase = "texto";

			String url = "jdbc:mysql://" + serverName + "/" + mydatabase+"?useTimezone=true&serverTimezone=UTC";

			String username = "root"; // nome de um usuário de seu BD

			String password = ""; // sua senha de acesso

			connection = DriverManager.getConnection(url, username, password);

			// Testa sua conexão//

			if (connection != null) {

				status = ("STATUS--->Connected!");

			} else {

				status = ("STATUS--->No connection available.");

			}

			return connection;

		} catch (ClassNotFoundException e) {

			System.out.println("No database driver found.");

			return null;

		} catch (SQLException e) {

			System.out.println("Could not connect to the database.");
			e.printStackTrace();

			return null;

		}

	}

	public static String getStatus() {
		return status;
	}

	public static boolean closeConnection() {

		try {

			DatabaseConnection.getConnection().close();

			return true;

		} catch (SQLException e) {

			return false;

		}

	}

	public static java.sql.Connection restartConnection() {

		closeConnection();

		return DatabaseConnection.getConnection();

	}
	
	public static java.sql.Connection getQuinhentasPerguntasConnection() {

		Connection connection = null;

		try {

			String driverName = "com.mysql.cj.jdbc.Driver";

			Class.forName(driverName);

			String serverName = "localhost";

			String mydatabase = "quinhentas_perguntas";

			String url = "jdbc:mysql://" + serverName + "/" + mydatabase+"?useTimezone=true&serverTimezone=UTC";

			String username = "root"; // nome de um usuário de seu BD

			String password = ""; // sua senha de acesso

			connection = DriverManager.getConnection(url, username, password);

			// Testa sua conexão//

			if (connection != null) {

				status = ("STATUS--->Connected!");

			} else {

				status = ("STATUS--->No connection available.");

			}

			return connection;

		} catch (ClassNotFoundException e) {

			System.out.println("No database driver found.");

			return null;

		} catch (SQLException e) {

			System.out.println("Could not connect to the database.");
			e.printStackTrace();

			return null;

		}

	}

}
