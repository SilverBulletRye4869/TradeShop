package silverassist.tradeshop;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Util {
    public static final String PREFIX = "§b§l[§e§lTradeShop§b§l]";
    public static final ItemStack GUI_BG = createItem(Material.LIGHT_BLUE_STAINED_GLASS_PANE,"§r");

    public static ItemStack createItem(Material m,String name){return createItem(m,name,null,0,null);}
    public static ItemStack createItem(Material m, String name, List<String> lore){return createItem(m,name,lore,0,null);}
    public static ItemStack createItem(Material m, String name, List<String> lore, HashMap<Enchantment,Integer> ench){return createItem(m,name,lore,0,ench);}
    public static ItemStack createItem(Material m, String name, List<String> lore, int model){return createItem(m,name,lore,model,null);}
    public static ItemStack createItem(Material m, String name, List<String> lore, int model,HashMap<Enchantment,Integer> ench){
        ItemStack item = new ItemStack(m);
        ItemMeta itemMeta = item.getItemMeta();
        if(itemMeta!=null){
            itemMeta.setDisplayName(name);
            if(lore!=null)itemMeta.setLore(lore);
            itemMeta.setCustomModelData(model);
            item.setItemMeta(itemMeta);
        }
        if(ench!=null)item.addEnchantments(ench);
        return item;
    }

    public static void invFill(Inventory inv){invFill(inv,GUI_BG,false);}
    public static void invFill(Inventory inv,ItemStack item){invFill(inv,item,false);}
    public static void invFill(Inventory inv,ItemStack item,boolean isAppend){
        int size = inv.getSize();
        for(int i = 0;i<size;i++){
            if(isAppend && inv.getItem(i).getType() != Material.AIR)continue;
            inv.setItem(i,item);
        }
    }

    public static void sendPrefixMessage(Player p, String msg){
        p.sendMessage(PREFIX+"§r"+msg);
    }


    private static ItemStack rightArrowBanner = null;
    public static ItemStack getRightArrowBanner(){
        if(rightArrowBanner == null)setRightArrowBanner();
        return rightArrowBanner;
    }
    private static void setRightArrowBanner(){
        ItemStack item = new ItemStack(Material.YELLOW_BANNER);
        BannerMeta bannerMeta = (BannerMeta) item.getItemMeta();
        List<Pattern> patterns = new ArrayList<>();
        patterns.add(new Pattern(DyeColor.LIGHT_BLUE, PatternType.BORDER));
        patterns.add(new Pattern(DyeColor.LIGHT_BLUE, PatternType.HALF_VERTICAL));
        patterns.add(new Pattern(DyeColor.YELLOW,PatternType.STRIPE_MIDDLE));
        patterns.add(new Pattern(DyeColor.LIGHT_BLUE,PatternType.STRIPE_TOP));
        patterns.add(new Pattern(DyeColor.LIGHT_BLUE,PatternType.STRIPE_BOTTOM));
        patterns.add(new Pattern(DyeColor.LIGHT_BLUE,PatternType.CURLY_BORDER));
        bannerMeta.setPatterns(patterns);
        item.setItemMeta(bannerMeta);
        rightArrowBanner = item;
    }
}
