package silverassist.tradeshop.system;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.plugin.java.JavaPlugin;
import silverassist.tradeshop.CustomConfig;
import silverassist.tradeshop.Util;

import java.util.HashMap;
import java.util.Set;

public class Setup {
    private final JavaPlugin plugin;
    private final HashMap<String, Buy> SHOP_DATA = new HashMap<>();
    private final HashMap<Player, String> OPENING = new HashMap<>();

    public Setup(JavaPlugin plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(new listener(),plugin);
    }

    public boolean open(Player p, String id){
        if(!SHOP_DATA.containsKey(id)){
            if(!this.reloadShop(id))return false;
        }
        p.closeInventory();
        boolean result = SHOP_DATA.get(id).open(p);
        if(!result){
            Util.sendPrefixMessage(p,"§c§lこのSHOPは準備中です");
            return false;
        }
        OPENING.put(p,id);
        return true;
    }

    public boolean reloadShop(String id){
        if(!CustomConfig.existYml(id))return false;
        SHOP_DATA.put(id,new Buy(this,id));
        return true;
    }

    public boolean deleteShop(String id){
        if(!SHOP_DATA.containsKey(id))return false;
        SHOP_DATA.remove(id);
        return true;
    }

    public Set<String> getLoadedShopSet(){
        return SHOP_DATA.keySet();
    }

    private class listener implements Listener {
        @EventHandler
        public void onInventoryClose(InventoryCloseEvent e){
            if(!OPENING.containsKey(e.getPlayer()))return;
            OPENING.remove(e.getPlayer());
        }

        @EventHandler
        public void onInventoryClick(InventoryClickEvent e){
            Player p =(Player)e.getWhoClicked();
            if(!OPENING.containsKey(p) || e.getCurrentItem() == null)return;
            e.setCancelled(true);
            if(e.getClickedInventory().getType().equals(InventoryType.CHEST) && e.getSlot() == 24) SHOP_DATA.get(OPENING.get(p)).payment(p);
        }
    }
}
