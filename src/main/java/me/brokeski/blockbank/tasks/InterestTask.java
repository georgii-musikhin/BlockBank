package me.brokeski.blockbank.tasks;

import me.brokeski.blockbank.BlockBank;
import me.brokeski.blockbank.database.Database;
import me.brokeski.blockbank.models.SavingsAccount;
import me.brokeski.blockbank.utils.AccountUtils;
import me.brokeski.blockbank.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;
import java.util.List;

public class InterestTask extends BukkitRunnable {
    @Override
    public void run() {

        List<SavingsAccount> accounts = null;
        try {
            accounts = Database.getSavingsDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (accounts != null){
            for (SavingsAccount account : accounts){

                double interestRate = AccountUtils.getAccountInterest(account.getTier());
                double currentAmount = account.getBalance();
                double gained = currentAmount * interestRate;

                account.setBalance(currentAmount + gained);

                //update the account with new interest
                try {
                    Database.getSavingsDao().update(account);

                    //Message the player if they are online
                    if (BlockBank.getPlugin().getAccountConfig().isAlertInterestGained()){
                        OfflinePlayer player = Bukkit.getOfflinePlayer(account.getOwner());
                        if (player.isOnline()){
                            MessageUtils.message(player.getPlayer(), "Your " + account.getTier() + " savings account has just earned $" + AccountUtils.getPrettyBalance(gained) + ".");
                        }
                    }
                } catch (SQLException e){
                    e.printStackTrace();
                }
            }
        }
    }
}



