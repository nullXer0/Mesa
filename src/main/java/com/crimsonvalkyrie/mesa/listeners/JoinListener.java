package com.crimsonvalkyrie.mesa.listeners;

import com.crimsonvalkyrie.mesa.Mesa;
import com.crimsonvalkyrie.mesa.misc.Database;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.UUID;
import java.util.logging.Level;

public class JoinListener implements Listener
{

	private static final String QUERY_STATEMENT = "SELECT * FROM " + Database.DAYS_PLAYED + " WHERE uuid = ?;";
	private static final String EXIST_STATEMENT = "SELECT EXISTS(SELECT * FROM " + Database.DAYS_PLAYED + " WHERE uuid = ?);";
	private static final String INSERT_STATEMENT = "INSERT INTO " + Database.DAYS_PLAYED + "(uuid, total_days, last_played, current_streak, longest_streak) VALUES(?, ?, ?);";
	private static final String UPDATE_STATEMENT = "UPDATE " + Database.DAYS_PLAYED + " SET last_played = ?, total_days = ?, current_streak = ?, longest_streak = ? WHERE player = ?";

	@EventHandler(priority = EventPriority.MONITOR)
	public void onJoin(PlayerJoinEvent event)
	{
		UUID uuid = event.getPlayer().getUniqueId();
		boolean newPlayer = true;

		//TODO: correct indexes, and implement streaks
		try
		{
			Connection conn = Mesa.getDatabase().getConnection(1);

			PreparedStatement existsStatement = conn.prepareStatement(EXIST_STATEMENT);
			existsStatement.setString(1, uuid.toString());

			ResultSet existsResult = existsStatement.executeQuery();
			if(existsResult.next())
			{
				newPlayer = !existsResult.getBoolean(1);
			}

			if(newPlayer)
			{
				PreparedStatement insertStatement = conn.prepareStatement(INSERT_STATEMENT);
				insertStatement.setString(1, uuid.toString());
				insertStatement.setDate(2, Date.valueOf(LocalDate.now()));
				insertStatement.setInt(3, 1);
				insertStatement.execute();
			}
			else
			{
				int daysPlayed;
				Date last_played;

				PreparedStatement queryStatement = conn.prepareStatement(QUERY_STATEMENT);
				queryStatement.setString(1, uuid.toString());
				ResultSet queryResult = queryStatement.executeQuery();
				queryResult.next();
				last_played = queryResult.getDate("last_played");
				daysPlayed = queryResult.getInt("total_days");

				if(last_played.compareTo(Date.valueOf(LocalDate.now())) != 0)
				{
					PreparedStatement updateStatement = conn.prepareStatement(UPDATE_STATEMENT);
					updateStatement.setInt(1, daysPlayed + 1);
					updateStatement.setDate(2, Date.valueOf(LocalDate.now()));
					updateStatement.setString(3, uuid.toString());
					updateStatement.execute();
				}
			}
		}
		catch(Exception e)
		{
			Mesa.getPlugin().getLogger().log(Level.SEVERE, "Error when updating login stats", e);
		}
	}
}
