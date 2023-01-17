package silverassist.tradeshop.adminmenu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import silverassist.tradeshop.CustomConfig;
import silverassist.tradeshop.TradeShop;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static silverassist.tradeshop.Util.*;

public class ShopList {

    private static final JavaPlugin plugin = TradeShop.getInstance();

    private final List<String> fileNames;
    private final Player P;
    private int nowPage = 0;

    public ShopList(Player p){
        this.P = p;
        this.fileNames = getShopList();
        p.closeInventory();
        plugin.getServer().getPluginManager().registerEvents(new listener(),plugin);
    }

    public void open(int page){
        this.nowPage = page;
        Inventory inv = Bukkit.createInventory(P,54,PREFIX+"§a§lショップ一覧");
        for(int i = page * 45;i<Math.min((page+1)*45,fileNames.size());i++){
            String id = fileNames.get(i);
            ItemStack item = new ItemStack(CustomConfig.getYmlByID(id).getItemStack("item.9",new ItemStack(Material.PAPER)));
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§6§l"+id);
            item.setItemMeta(meta);
            inv.setItem(i%45,item);
        }
        for(int i = 45;i<54;i++)inv.setItem(i,GUI_BG);
        if(page>0)inv.setItem(45,createItem(Material.RED_STAINED_GLASS_PANE,"§c§l前へ"));
        if(page < (fileNames.size() -1)/45)inv.setItem(53,createItem(Material.LIME_STAINED_GLASS_PANE,"§a§l次へ"));
        P.openInventory(inv);
        this.unregisterCancel = false;
    }


    public static List<String> getShopList(){return getShopList("");}
    public static List<String> getShopList(String startRegex){
        Stream<Path> paths;
        try {
            paths = Files.list(Paths.get(plugin.getDataFolder() + "/data"));
        }catch (IOException e){
            System.err.println("[TradeShop]ShopDataフォルダの取得に失敗しました");
            e.printStackTrace();
            return null;
        }
        List<String> fileNames = new ArrayList<>();
        paths.forEach(e ->{
            String fileName = e.getFileName().toString();
            if(fileName.matches("^"+startRegex+".*\\.yml$")) fileNames.add(fileName.replaceAll("\\.yml$",""));
        });
        return fileNames;
    }

    private boolean unregisterCancel = false;
    private class listener implements Listener{
        @EventHandler
        public void onInventoryClose(InventoryCloseEvent e){
            if(!e.getPlayer().equals(P) || unregisterCancel)return;
            HandlerList.unregisterAll(this);
        }

        @EventHandler
        public void onInventoryClick(InventoryClickEvent e){
            if(!e.getWhoClicked().equals(P) || e.getCurrentItem()==null)return;
            e.setCancelled(true);
            if(!e.getClickedInventory().getType().equals(InventoryType.CHEST))return;
            int slot = e.getSlot();
            if(slot < 45)new ShopEdit(P, fileNames.get(Math.min(45*nowPage + slot,fileNames.size() -1))).open();
            else if(slot == 45 && e.getClickedInventory().getType().equals(Material.RED_STAINED_GLASS_PANE)){
                unregisterCancel = true;
                open(nowPage-1);
            }else if(slot == 53 && e.getClickedInventory().getType().equals(Material.LIME_STAINED_GLASS_PANE)){
                unregisterCancel = true;
                open(nowPage+1);
            }
        }


    }
}
