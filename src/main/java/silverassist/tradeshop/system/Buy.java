package silverassist.tradeshop.system;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import silverassist.tradeshop.CustomConfig;

import static silverassist.tradeshop.Util.*;

public class Buy {
    private static final int[] ITEM_PLACE = {10,11,12,19,20,21,28,29,30,24};
    private static final int[] LIME_PLACE = {14,15,16,23,25,32,33,34};

    private final ItemStack[] ITEM = new ItemStack[10];
    private final int[] EXP_LV = new int[2]; //{必要経験値レベル,消費経験値レベル}
    private final Setup SHOP_SYSTEM;
    private final String SHOP_NAME;

    public Buy(Setup system,String id){
        this.SHOP_SYSTEM = system;
        this.SHOP_NAME = id;
        YamlConfiguration y = CustomConfig.getYmlByID(id);
        for(int i = 0;i<10;i++) ITEM[i] = y.getItemStack("item."+i,new ItemStack(Material.AIR));
        this.EXP_LV[0] = y.getInt("exp.need",0);
        this.EXP_LV[1] = y.getInt("exp.use",0);
    }

    public boolean open(Player p){
        if(ITEM[9].getType().equals(Material.AIR))return false;
        Inventory inv = Bukkit.createInventory(p,54,PREFIX+"§c§l"+this.SHOP_NAME);

        invFill(inv);
        for(int i = 0;i < this.LIME_PLACE.length;i++)inv.setItem(this.LIME_PLACE[i],createItem(Material.LIME_STAINED_GLASS_PANE,"§r"));

        for(int i = 0;i < this.ITEM_PLACE.length/* = 10 */;i++)inv.setItem(this.ITEM_PLACE[i], this.ITEM[i]);
        if(this.EXP_LV[0]>0)inv.setItem(37,createItem(Material.EXPERIENCE_BOTTLE,"§a§l必要経験値: "+this.EXP_LV[0]));
        if(this.EXP_LV[0]>0)inv.setItem(38,createItem(Material.EXPERIENCE_BOTTLE,"§e§l消費経験値: "+this.EXP_LV[1]));

        p.openInventory(inv);
        return true;
    }

    public boolean payment(Player p){
        if(p.getLevel() < EXP_LV[0]){
            sendPrefixMessage(p,"§c§l必要経験値レベルが足りません");
            return false;
        }
        if(p.getLevel() < EXP_LV[1]){
            sendPrefixMessage(p,"§c§lあなたのレベルが消費経験値レベルを下回っています");
            return false;
        }
        for(int i = 0;i<9;i++){
            ItemStack item = this.ITEM[i];
            if(p.getInventory().containsAtLeast(item,item.getAmount()))p.getInventory().removeItem(item);
            else{
                for(int j = 0;j<i;j++)p.getInventory().addItem(this.ITEM[j]);
                sendPrefixMessage(p,"§c§l取引に必要なアイテムが不足しています");
                return false;
            }
        }
        p.setLevel(p.getLevel()- EXP_LV[1]);
        p.getInventory().addItem(this.ITEM[9]);
        sendPrefixMessage(p,"§a§lトレード成功！");
        return true;
    }
}
