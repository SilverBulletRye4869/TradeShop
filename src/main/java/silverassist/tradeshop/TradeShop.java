package silverassist.tradeshop;

import org.bukkit.plugin.java.JavaPlugin;
import silverassist.tradeshop.system.Setup;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class TradeShop extends JavaPlugin {

    private static JavaPlugin plugin = null;
    private static Setup SHOP_SYSTEM =null;

    @Override
    public void onEnable() {
        plugin =this;
        this.saveDefaultConfig();
        try {
            Files.createDirectories(Paths.get(this.getDataFolder()+"/data"));
        } catch (IOException e) {
            System.err.println("[TradeShop]Dataフォルダの作成に失敗しました");
            getServer().getPluginManager().disablePlugin(this);
        }
        SHOP_SYSTEM = new Setup(this);
        new Command(this,SHOP_SYSTEM);

    }

    public static JavaPlugin getInstance(){return plugin;}
    public static Setup getShopSystem(){return SHOP_SYSTEM;}

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
