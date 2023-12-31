package me.brokeski.blockbank.listeners;

import me.brokeski.blockbank.BlockBank;
import me.brokeski.blockbank.menu.menusteller.TellerMenu;
import me.brokeski.blockbank.models.BankNote;
import me.brokeski.blockbank.utils.MessageUtils;
import me.kodysimpson.simpapi.colors.ColorTranslator;
import me.kodysimpson.simpapi.exceptions.MenuManagerException;
import me.kodysimpson.simpapi.exceptions.MenuManagerNotSetupException;
import me.kodysimpson.simpapi.menu.MenuManager;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class BankTellerListener implements Listener {

    @EventHandler
    public void onEntityInteract(NPCRightClickEvent e) throws MenuManagerNotSetupException, MenuManagerException {

        if (e.getNPC().getName().equalsIgnoreCase(ColorTranslator.translateColorCodes("&e&lBank Teller"))) {
            // add NPC is bank teller check here
            if (BankNote.isPlayerInConversation(e.getClicker())) return;

            ItemStack itemInHand = e.getClicker().getInventory().getItemInMainHand();

            if (itemInHand.hasItemMeta() && BankNote.isBankNote(itemInHand)) {
                redeemBankNote(e.getClicker(), new BankNote(itemInHand).getValue(), itemInHand, e.getClicker().isSneaking());
            }else{
                new TellerMenu(MenuManager.getPlayerMenuUtility(e.getClicker())).open();
            }
        }

    }

    public void redeemBankNote(Player player, double value, ItemStack item, boolean sneaking) {

        Economy economy = BlockBank.getEconomy();

        if (sneaking) {
            List<Integer> slots = new ArrayList<>();
            value = 0;
            for (int i=0; i<36; ++i) {
                ItemStack itemStack = player.getInventory().getItem(i);
                if (itemStack!=null && BankNote.isBankNote(itemStack)) {
                    slots.add(i); value += new BankNote(itemStack).getValue();
                }
            }

            EconomyResponse response = economy.depositPlayer(player, value);

            if (response.transactionSuccess()){

                slots.forEach(i -> player.getInventory().setItem(i, new ItemStack(Material.AIR)));

                player.sendMessage(MessageUtils.message("You have successfully redeemed $" + value));

            }else{

                player.sendMessage(MessageUtils.message("Transaction Error. Try again later."));

            }
            return;
        }

        EconomyResponse response = economy.depositPlayer(player, value);

        if (response.transactionSuccess()){

            if (item.getAmount() > 1){
                player.getInventory().setItemInMainHand(BankNote.createBankNote(value));
                for (int i = 0; i < item.getAmount() - 2; i++){
                    player.getInventory().addItem(BankNote.createBankNote(value));
                }
            }else{
                player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
            }

            player.sendMessage(MessageUtils.message("You have successfully redeemed $" + value));

        }else{

            player.sendMessage(MessageUtils.message("Transaction Error. Try again later."));

        }

    }

}