package me.brokeski.blockbank.listeners;

import me.brokeski.blockbank.database.AccountQueries;
import me.brokeski.blockbank.database.Database;
import me.brokeski.blockbank.models.CheckingAccount;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.SQLException;

public class JoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) throws SQLException {

        //Make sure every player that joins has their own checking account
        AccountQueries.getAccountForPlayer(e.getPlayer());

    }

}