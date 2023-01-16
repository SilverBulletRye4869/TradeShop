package silverassist.tradeshop;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import silverassist.tradeshop.adminmenu.ShopEdit;
import silverassist.tradeshop.adminmenu.ShopList;
import silverassist.tradeshop.system.Setup;

import java.util.List;

import static silverassist.tradeshop.Util.sendPrefixMessage;

public class Command implements CommandExecutor {
    private final Setup SHOP_SYSTEM;

    public Command(JavaPlugin plugin, Setup system){
        this.SHOP_SYSTEM = system;
        PluginCommand command = plugin.getCommand("tradeshop");
        command.setExecutor(this);
        command.setTabCompleter(new Tab());
    }
    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if(!(sender instanceof Player))return true;
        Player p = (Player) sender;

        if(args.length<1){
            //help
            return true;
        }
        String id = null;
        if(args.length>1)id = args[1];

        switch (args[0]){
            case "open":
                if(id==null)return true;
                SHOP_SYSTEM.open(p,id);
                return true;

            case "create":
                if(id==null)return true;
                if(!CustomConfig.existYml(id)) CustomConfig.createYmlByID(id);
            case "edit":
                if(id==null)return true;
                if(!CustomConfig.existYml(id)){
                    sendPrefixMessage(p,"§c§lそのidのSHOPは存在しません");
                    return true;
                }
                new ShopEdit(p,id).open();
                return true;
            case "list":
                new ShopList(p).open(0);
                return true;

            case "reload":
                if(id==null){
                    //configのreload
                    return true;
                }
                boolean result = SHOP_SYSTEM.reloadShop(id);
                if(result)sendPrefixMessage(p,"§a§lSHOPをrelaodしました");
                else sendPrefixMessage(p,"§c§lSHOPが存在しません");
                return true;
            case "reloadall":
                SHOP_SYSTEM.getLoadedShopSet().forEach(SHOP_SYSTEM::reloadShop);
                sendPrefixMessage(p,"§a全てのshopをreloadしました");
                return true;
            case "delete":
                if(id==null)return true;
                CustomConfig.deleteYmlByID(id);
                SHOP_SYSTEM.deleteShop(id);
                sendPrefixMessage(p,"§c§lSHOP『"+id+"』を正常に削除しました。");

                return true;
        }
        return false;
    }

    private class Tab implements TabCompleter{

        @Override
        public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String alias, String[] args) {
            switch (args.length){
                case 1:
                    return List.of("open","create","edit","list","reload","reloadall","delete");
                case 2:
                    switch (args[0]){
                        case "open":
                        case "edit":
                        case "reload":
                            return ShopList.getShopList(args[1]);
                    }
            }
            return null;
        }
    }
}
