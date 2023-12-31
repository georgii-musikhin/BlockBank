package me.brokeski.blockbank;

import me.brokeski.blockbank.commands.CreateTellerCommand;
import me.brokeski.blockbank.configs.ATMConfig;
import me.brokeski.blockbank.configs.AccountConfig;
import me.brokeski.blockbank.configs.MessageConfig;
import me.brokeski.blockbank.database.Database;
import me.brokeski.blockbank.listeners.ATMListener;
import me.brokeski.blockbank.listeners.BankTellerListener;
import me.brokeski.blockbank.tasks.InterestTask;
import me.kodysimpson.simpapi.command.CommandManager;
import me.kodysimpson.simpapi.config.ConfigManager;
import me.kodysimpson.simpapi.menu.MenuManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class BlockBank extends JavaPlugin {

    private static final Logger log = Logger.getLogger("Minecraft");
    private static Economy econ = null;
    private static BlockBank plugin;

    private static String url;

    //Config
    private MessageConfig messageConfig;
    private ATMConfig atmConfig;
    private AccountConfig accountConfig;

    @Override
    public void onEnable() {

        if(getServer().getPluginManager().getPlugin("Citizens") == null || getServer().getPluginManager().getPlugin("Citizens").isEnabled() == false) {
            getLogger().log(Level.SEVERE, "Citizens 2.0 not found or not enabled");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (!setupEconomy() ) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        plugin = this;

        //Setup Config
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        //Create database tables if not already generated
        url = "jdbc:h2:" + getDataFolder().getAbsolutePath() + "/data/littybank";
        Database.initializeDatabase();

        getServer().getPluginManager().registerEvents(new BankTellerListener(), this);
        getServer().getPluginManager().registerEvents(new ATMListener(), this);

        MenuManager.setup(getServer(), this);

        //Configuration
        messageConfig = ConfigManager.loadConfig(this, MessageConfig.class);
        atmConfig = ConfigManager.loadConfig(this, ATMConfig.class);
        accountConfig = ConfigManager.loadConfig(this, AccountConfig.class);

        try {
            CommandManager.createCoreCommand(this, "litty", "litty bank core", "/litty", null, Arrays.asList("pickle"),  CreateTellerCommand.class);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        // 60 seconds for testing
        new InterestTask().runTaskTimerAsynchronously(this, 20, accountConfig.getCompoundingPeriodSeconds() * 20);
    }

    @Override
    public void onDisable() {

        try {
            Database.getConnectionSource().close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ConfigManager.saveConfig(this, messageConfig);
        ConfigManager.saveConfig(this, atmConfig);
        ConfigManager.saveConfig(this, accountConfig);

        log.info(String.format("[%s] Disabled Version %s", getDescription().getName(), getDescription().getVersion()));
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public static BlockBank getPlugin() {
        return plugin;
    }

    public static Economy getEconomy() {
        return econ;
    }

    public MessageConfig getMessageConfig() {
        return messageConfig;
    }

    public ATMConfig getAtmConfig() {
        return atmConfig;
    }

    public AccountConfig getAccountConfig() {
        return accountConfig;
    }
}