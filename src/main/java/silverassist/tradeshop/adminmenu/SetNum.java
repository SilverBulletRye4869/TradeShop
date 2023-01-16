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

import java.util.List;

import static silverassist.tradeshop.Util.*;

public class SetNum {

    private static final List<Integer> numKeyPos = List.of(38,10,11,12,19,20,21,28,29,30);
    private static final JavaPlugin plugin = TradeShop.getInstance();


    private final Player P;
    private final String PATH;
    private final String ID;
    private final YamlConfiguration DATA;
    private final int MIN_NUM;
    private int nowNum;


    public SetNum(Player p,String shopID, String path){this(p,shopID,path,0);}
    public SetNum(Player p,String shopID, String path, int minNum) {
        this.P = p;
        p.closeInventory();
        this.PATH = path;
        this.ID = shopID;
        this.MIN_NUM = minNum;
        DATA= CustomConfig.getYmlByID(shopID);
        nowNum = DATA.getInt(path);
        plugin.getServer().getPluginManager().registerEvents(new listener(), plugin);
    }

    public void open(){
        Inventory inv = Bukkit.createInventory(P,54, PREFIX+"§r"+PATH+"の設定");
        invFill(inv);
        for(int i = 0;i<numKeyPos.size();i++){
            ItemStack item = createItem(Material.PAPER, "§6§l" + i);
            item.setAmount(Math.max(1,i));
            inv.setItem(numKeyPos.get(i),item);
        }
        inv.setItem(15,createItem(nowNum == 0 ? Material.PAPER : Material.MAP,"§6§l"+nowNum));
        inv.setItem(41,createItem(Material.RED_STAINED_GLASS_PANE,"§c§lリセット"));
        inv.setItem(43,createItem(Material.LIME_STAINED_GLASS_PANE,"§a§l確定"));
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
            if(!P.equals(e.getPlayer()))return;
            HandlerList.unregisterAll(this);
            new ShopEdit(P,ID).open();
        }
        @EventHandler
        public void onInventoryClick(InventoryClickEvent e){
            if(!P.equals(e.getWhoClicked()) ||e.getCurrentItem() == null|| !e.getInventory().getType().equals(InventoryType.CHEST))return;
            e.setCancelled(true);
            int slot = e.getSlot();
            switch (slot){
                case 41:
                    e.getClickedInventory().setItem(15,createItem(Material.PAPER,"§6§l0"));
                    nowNum = 0;
                    return;
                case 43:
                    if(nowNum<MIN_NUM){
                        sendPrefixMessage((Player)e.getWhoClicked(),"§c§l値は最低でも、『"+MIN_NUM+"』にする必要があります");
                        return;
                    }
                    DATA.set(PATH,nowNum);
                    P.closeInventory();
                    break;
                default:
                    if(nowNum>9999999 || !numKeyPos.contains(slot))return;
                    int numKey = numKeyPos.indexOf(slot);
                    nowNum = nowNum * 10 + numKey;
                    e.getClickedInventory().setItem(15,createItem(nowNum == 0 ? Material.PAPER : Material.MAP,"§6§l"+nowNum));
                    return;
            }
            CustomConfig.saveYmlByID(ID);
            //DATA.save(CustomConfig.getYmlFileByID(GACHA_ID));
        }
    }
}
