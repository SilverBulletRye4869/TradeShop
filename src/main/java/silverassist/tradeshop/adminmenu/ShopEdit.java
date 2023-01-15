package silverassist.tradeshop.adminmenu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import silverassist.tradeshop.CustomConfig;
import silverassist.tradeshop.TradeShop;

import java.util.Arrays;

import static silverassist.tradeshop.Util.*;

public class ShopEdit {
    private static final int[] ITEM_PLACE = {10,11,12,19,20,21,28,29,30,24};
    private static final JavaPlugin plugin = TradeShop.getInstance();

    private final Player P;
    private final String ID;
    private final YamlConfiguration YML;

    public ShopEdit(Player p, String id){
        this.P = p;
        this.ID = id;
        YML = CustomConfig.getYmlByID(id);
        p.closeInventory();
        plugin.getServer().getPluginManager().registerEvents(new listener(),plugin);

    }

    public void open(){
        Inventory inv = Bukkit.createInventory(this.P,54,PREFIX+"§d§lid: "+this.ID+"§a§lの編集画面");
        invFill(inv);
        for(int i = 0;i<10;i++)inv.setItem(ITEM_PLACE[i], YML.getItemStack("item."+i, new ItemStack(Material.AIR)));
        inv.setItem(37,createItem(Material.EXPERIENCE_BOTTLE,"§a§l必要経験値: "+YML.getInt("exp.need",0)));
        inv.setItem(38,createItem(Material.EXPERIENCE_BOTTLE,"§e§l消費経験値: "+YML.getInt("exp.use",0)));
        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                P.openInventory(inv);
            }
        },1);
    }


    private class listener implements Listener {
        @EventHandler
        public void onInventoryClose(InventoryCloseEvent e){
            if(!e.getPlayer().equals(P))return;
            Inventory inv = e.getInventory();
            for(int i = 0;i<10;i++)YML.set("item."+i,inv.getItem(ITEM_PLACE[i]));
            CustomConfig.saveYmlByID(ID);
            HandlerList.unregisterAll(this);
        }

        @EventHandler
        public void onInventoryClick(InventoryClickEvent e){
            if(!e.getWhoClicked().equals(P))return;
            if(e.getCurrentItem() == null || !e.getClickedInventory().getType().equals(InventoryType.CHEST))return;
            if(!Arrays.asList(ITEM_PLACE).contains(e.getSlot()))e.setCancelled(true);

            switch (e.getSlot()){
                case 37:
                    new SetNum(P,ID,"exp.need").open();
                    return;
                case 38:
                    new SetNum(P,ID,"exp.use").open();
                    return;
            }
        }
    }
}
