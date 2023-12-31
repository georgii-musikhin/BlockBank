package me.brokeski.blockbank.menu.menusteller.savings;

import me.brokeski.blockbank.BlockBank;
import me.brokeski.blockbank.database.Database;
import me.brokeski.blockbank.menu.MenuData;
import me.brokeski.blockbank.menu.menusteller.savings.AccountOptionsMenu;
import me.brokeski.blockbank.models.SavingsAccount;
import me.brokeski.blockbank.utils.MessageUtils;
import me.kodysimpson.simpapi.colors.ColorTranslator;
import me.kodysimpson.simpapi.exceptions.MenuManagerException;
import me.kodysimpson.simpapi.exceptions.MenuManagerNotSetupException;
import me.kodysimpson.simpapi.menu.Menu;
import me.kodysimpson.simpapi.menu.MenuManager;
import me.kodysimpson.simpapi.menu.PlayerMenuUtility;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;

public class ConfirmDeleteMenu extends Menu {

    private final Economy economy;
    private SavingsAccount account;

    public ConfirmDeleteMenu(PlayerMenuUtility playerMenuUtility) throws SQLException {
        super(playerMenuUtility);
        economy = BlockBank.getEconomy();
        account = Database.getSavingsDao().queryForId(playerMenuUtility.getData(MenuData.ACCOUNT_ID, Integer.class));
    }

    @Override
    public String getMenuName() {
        return "Confirm: Delete Account";
    }

    @Override
    public int getSlots() {
        return 9;
    }

    @Override
    public boolean cancelAllClicks() {
        return true;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) throws MenuManagerNotSetupException, MenuManagerException {

        switch (e.getCurrentItem().getType()){
            case BELL -> {
                try {
                    deleteAccount();
                    p.closeInventory();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            case BARRIER -> MenuManager.openMenu(AccountOptionsMenu.class, p);
        }

    }

    @Override
    public void setMenuItems() {
        ItemStack yes = makeItem(Material.BELL, ColorTranslator.translateColorCodes("&#54d13f&lYes"), ColorTranslator.translateColorCodes("&7Delete the account"));
        ItemStack no = makeItem(Material.BARRIER, ColorTranslator.translateColorCodes("&4&lNo"), ColorTranslator.translateColorCodes("&7Go back"));

        inventory.setItem(0, no);
        inventory.setItem(8, yes);

        setFillerGlass();
    }

    public void deleteAccount() throws SQLException {

        EconomyResponse response = economy.depositPlayer(p, account.getBalance());

        if (response.transactionSuccess()){

            Database.getSavingsDao().delete(account);

            p.sendMessage(MessageUtils.message("Your account has been successfully deleted and $" + account.getBalance() + " has been added to your balance.\n" +
                    "Will add a confirmation menu later"));

        }else{

            p.sendMessage(MessageUtils.message("Transaction Error. Try again later."));

        }
    }

}