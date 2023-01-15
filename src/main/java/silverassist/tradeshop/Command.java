package silverassist.tradeshop;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import silverassist.tradeshop.adminmenu.ShopEdit;
import silverassist.tradeshop.adminmenu.ShopList;
import silverassist.tradeshop.system.Setup;

import static silverassist.tradeshop.Util.sendPrefixMessage;

public class Command implements CommandExecutor {
    private final JavaPlugin plugin;
    private final Setup SHOP_SYSTEM;

    public Command(JavaPlugin plugin, Setup system){
        this.plugin = plugin;
        this.SHOP_SYSTEM = system;
        plugin.getCommand("tradeshop").setExecutor(this);
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
        if(args.length>2)id = args[1];

        switch (args[0]){
            case "open":
                if(id==null)return true;
                SHOP_SYSTEM.open(p,id);
                return true;

            case "create":
                if(id==null)return true;
                if(!CustomConfig.existYml(id)){
                    CustomConfig.createYmlByID(id);
                }
            case "edit":
                if(id==null)return true;
                if(!CustomConfig.existYml(id)){
                    sendPrefixMessage(p,"§c§lそのidのSHOPは存在しません");
                    return true;
                }
                new ShopEdit(p,id);
                return true;
            case "list":
                new ShopList(p);
                return true;
        }
        return false;
    }
}
