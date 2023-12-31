package me.brokeski.blockbank.menu.menusteller.savings;

import me.brokeski.blockbank.configs.models.AccountTierConfig;
import me.brokeski.blockbank.database.Database;
import me.brokeski.blockbank.BlockBank;
import me.brokeski.blockbank.menu.MenuData;
import me.brokeski.blockbank.menu.menusteller.savings.SavingsTierSelectionMenu;
import me.brokeski.blockbank.models.AccountTier;
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
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.Date;

public class ConfirmOpenAccountMenu extends Menu {

    private final AccountTierConfig tierConfig;
    private final Economy economy = BlockBank.getEconomy();

    public ConfirmOpenAccountMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
        tierConfig = BlockBank.getPlugin().getAccountConfig().getSavingsAccountTiers().get(playerMenuUtility.getData(MenuData.CONFIRM_TIER, AccountTier.class));
    }

    @Override
    public String getMenuName() {
        return "Confirm: Open Savings Account?";
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


        if (e.getCurrentItem().getType() == Material.BELL){

            try {
                openAccount(playerMenuUtility.getData(MenuData.CONFIRM_TIER, AccountTier.class), playerMenuUtility.getOwner());
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            playerMenuUtility.getOwner().closeInventory();

        }else if (e.getCurrentItem().getType() == Material.BARRIER){

            //go back
            MenuManager.openMenu(SavingsTierSelectionMenu.class, playerMenuUtility.getOwner());

        }

    }

    public void openAccount(AccountTier tier, Player player) throws SQLException {

        double fee = tierConfig.getCost();

        if (economy.getBalance(player) >= fee){

            EconomyResponse response = economy.withdrawPlayer(player, fee);

            if (response.transactionSuccess()){

                SavingsAccount account = new SavingsAccount();
                account.setOwner(player.getUniqueId());
                account.setTier(tier);
                account.setBalance(0);
                account.setLastChecked(new Date());
                account.setLastUpdated(new Date());

                Database.getSavingsDao().create(account);

                player.sendMessage(MessageUtils.message("You have successfully opened a " + tierConfig.getDisplayName() + " Savings Account."));

            }else{
                player.sendMessage(MessageUtils.message("Transaction Error. Try again later."));
            }

        }else{
            player.sendMessage(MessageUtils.message("You don't have the funds to open an account."));
        }
    }

    @Override
    public void setMenuItems() {

        ItemStack yes = makeItem(Material.BELL, ColorTranslator.translateColorCodes("&#54d13f&lYes"), ColorTranslator.translateColorCodes("&7Open for &a$" + tierConfig.getCost()));
        ItemStack no = makeItem(Material.BARRIER, ColorTranslator.translateColorCodes("&4&lNo"), ColorTranslator.translateColorCodes("&7Go back"));
        ItemStack tierItem = makeItem(tierConfig.getItem(), ColorTranslator.translateColorCodes(tierConfig.getDisplayName()),
                "&7Price: &a$" + tierConfig.getCost(),
                "&7Interest Rate: &a" + tierConfig.getInterestRate() + "%");

        inventory.setItem(0, no);
        inventory.setItem(4, tierItem);
        inventory.setItem(8, yes);

        setFillerGlass();
    }
}