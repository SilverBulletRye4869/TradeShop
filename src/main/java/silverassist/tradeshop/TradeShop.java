package silverassist.tradeshop;

import org.bukkit.plugin.java.JavaPlugin;
import silverassist.tradeshop.system.Setup;

public final class TradeShop extends JavaPlugin {

    private static JavaPlugin plugin = null;
    private static Setup SHOP_SYSTEM =null;

    @Override
    public void onEnable() {
        plugin =this;
        // Plugin startup logic
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
