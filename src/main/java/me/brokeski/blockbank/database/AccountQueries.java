package me.brokeski.blockbank.database;

import com.j256.ormlite.stmt.QueryBuilder;
import me.brokeski.blockbank.models.ATM;
import me.brokeski.blockbank.models.AccountTier;
import me.brokeski.blockbank.models.CheckingAccount;
import me.brokeski.blockbank.models.SavingsAccount;
import me.brokeski.blockbank.utils.Serializer;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AccountQueries {

    public static CheckingAccount getAccountForPlayer(Player p){
        CheckingAccount checkingAccount = null;
        try{
            checkingAccount = Database.getCheckingsDao().queryForId(p.getUniqueId());
            if (checkingAccount == null){
                //Create an account for the player
                checkingAccount = new CheckingAccount();
                checkingAccount.setBalance(0.0);
                checkingAccount.setOwner(p.getUniqueId());
                Database.getCheckingsDao().create(checkingAccount);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return checkingAccount;
    }

    public static boolean hasSavingsAccount(Player p){

        List<SavingsAccount> accounts = new ArrayList<>();
        try {
            accounts = Database.getSavingsDao().queryForEq("owner", p.getUniqueId());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return !accounts.isEmpty();
    }

    public static SavingsAccount getSavingsAccount(Player p) throws SQLException {

        QueryBuilder<SavingsAccount, Integer> queryBuilder = Database.getSavingsDao().queryBuilder();
        queryBuilder.where().eq("owner", p.getUniqueId());

        return Database.getSavingsDao().queryForFirst(queryBuilder.prepare());
    }

    /**
     * @param p The player
     * @param nextTier The tier to upgrade to
     * @return if it worked or not
     */
    public static boolean upgradeSavingsAccount(Player p, AccountTier nextTier) throws SQLException {
        SavingsAccount account = getSavingsAccount(p);
        if (account == null){
            return false;
        }else{
            account.setTier(nextTier);
            Database.getSavingsDao().update(account);
            return true;
        }
    }

}
