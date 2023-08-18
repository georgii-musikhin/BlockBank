package me.brokeski.blockbank.commands;

import me.kodysimpson.simpapi.colors.ColorTranslator;
import me.kodysimpson.simpapi.command.SubCommand;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.api.trait.Trait;
// import net.citizensnpcs.api.trait.LookClose;
// import net.citizensnpcs.api.trait.SkinTrait;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.List;

public class CreateTellerCommand extends SubCommand {

    @Override
    public String getName() {
        return "spawnteller";
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public String getDescription() {
        return "Spawns a teller at your current location";
    }

    @Override
    public String getSyntax() {
        return "dwdwdwdwdw";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {

        String skinName = "Banker";

        if (sender instanceof Player p){
            NPCRegistry registry = CitizensAPI.getNPCRegistry();
            NPC npc = registry.createNPC(EntityType.PLAYER, ColorTranslator.translateColorCodes("&e&lBank Teller"));
            npc.setUseMinecraftAI(true);
            npc.spawn(p.getLocation());
            // npc.addTrait(LookClose.class);
            // npc.addTrait(Skin.class).toString().setSkinName(skinName, true);

        }else{
            sender.sendMessage("You must be a player to run this command.");
        }

    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return null;
    }

    private static class SkinTrait {
    }
}