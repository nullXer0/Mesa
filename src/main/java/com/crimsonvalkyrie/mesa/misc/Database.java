package com.crimsonvalkyrie.mesa.misc;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.mariadb.jdbc.MariaDbDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class Database
{
	public static final String PREFIX = "mesa_";
	public static final String DAYS_PLAYED = PREFIX + "days_played";

	private static MariaDbDataSource dbDataSource;

	private static final Connection[] CONNECTIONS = new Connection[10];

	public Database(Plugin plugin) throws SQLException
	{
		ConfigurationSection dbSection = plugin.getConfig().getConfigurationSection("database");

		dbDataSource = new MariaDbDataSource();
		dbDataSource.setServerName(dbSection.getString("host"));
		dbDataSource.setPortNumber(dbSection.getInt("port"));
		dbDataSource.setDatabaseName(dbSection.getString("database"));
		dbDataSource.setUser(dbSection.getString("user"));
		dbDataSource.setPassword(dbSection.getString("password"));

		createConnections();

		createTables();
	}

	private void createConnections() throws SQLException
	{
		for(int i = 0; i < CONNECTIONS.length; i++)
		{
			CONNECTIONS[i] = dbDataSource.getConnection();
		}
	}

	public Connection getConnection(int index) throws SQLException
	{
		Connection connection = CONNECTIONS[index];

		if(connection.isClosed())
		{
			connection = dbDataSource.getConnection();
			CONNECTIONS[index] = connection;
		}

		return connection;
	}

	private void createTables() throws SQLException
	{
		Connection conn = getConnection(0);

		//TODO: add monthly/weekly streaks
		conn.prepareStatement("CREATE TABLE IF NOT EXISTS " + Database.DAYS_PLAYED + "(uuid char(36) NOT NULL UNIQUE, last_played DATE, total_days INT, current_streak INT, longest_streak INT, PRIMARY KEY(uuid));").execute();
	}
}
