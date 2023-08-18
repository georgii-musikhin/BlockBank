package me.brokeski.blockbank.listeners;

import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import me.brokeski.blockbank.BlockBank;
import me.brokeski.blockbank.database.ATMQueries;
import me.brokeski.blockbank.database.Database;
import me.brokeski.blockbank.menu.MenuData;
import me.brokeski.blockbank.menu.atm.ATMMenu;
import me.brokeski.blockbank.models.ATM;
import me.brokeski.blockbank.utils.MessageUtils;
import me.brokeski.blockbank.utils.Serializer;
import me.kodysimpson.simpapi.exceptions.MenuManagerException;
import me.kodysimpson.simpapi.exceptions.MenuManagerNotSetupException;
import me.kodysimpson.simpapi.menu.MenuManager;
import me.kodysimpson.simpapi.menu.PlayerMenuUtility;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.sql.SQLException;

public class ATMListener implements Listener {

    @EventHandler
    public void openATM(PlayerInteractEvent e) throws MenuManagerNotSetupException, MenuManagerException, SQLException {

        if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock() != null && e.getClickedBlock().getType() == Material.ANVIL){

            //Get the ATM from the DB from its location
            ATM atm = ATMQueries.getATMFromLocation(e.getClickedBlock().getLocation());

            if (atm != null){
                e.setCancelled(true);
                PlayerMenuUtility playerMenuUtility = MenuManager.getPlayerMenuUtility(e.getPlayer());
                playerMenuUtility.setData(MenuData.ATM, atm);

                MenuManager.openMenu(ATMMenu.class, e.getPlayer());
            }
        }

    }

    @EventHandler
    public void placeATM(BlockPlaceEvent e) throws SQLException {

        if (ATM.isValidATM(e.getItemInHand())) {

            Block block = e.getBlockPlaced();

            //Create a new ATM from the Item and location of the placed block
            ATM atm = new ATM(e.getItemInHand(), block.getLocation());

            Database.getAtmDao().create(atm);

            e.getPlayer().sendMessage(MessageUtils.message("You have placed your ATM on the ground. Holy shift and right click to access it."));
        }
    }

    @EventHandler
    public void removeATM(BlockBreakEvent e) {

        if (e.getBlock().getType() == Material.ANVIL && Database.isATMLocation(e.getBlock().getLocation())) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(MessageUtils.message("You cannot break ATMs. Pick them up from their GUI. Change this message please."));
        }
    }

}
