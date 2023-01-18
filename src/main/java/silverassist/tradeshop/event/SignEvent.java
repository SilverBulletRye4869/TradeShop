package silverassist.tradeshop.event;

import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import silverassist.tradeshop.CustomConfig;
import silverassist.tradeshop.system.Setup;

import static silverassist.tradeshop.Util.PREFIX;

public class SignEvent implements Listener {
    private final JavaPlugin plugin;
    private final Setup SHOP_SYSTEM;
    public SignEvent(JavaPlugin plugin, Setup system){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this,plugin);
        this.SHOP_SYSTEM = system;
    }

    @EventHandler
    public void onSignEdit(SignChangeEvent e){
        String[] lines = e.getLines();
        if(!e.getPlayer().isOp() || !lines[0].equals("tradeshop"))return;
        if(!CustomConfig.existYml(lines[3]))return;
        for(int i = 1;i < 3;i++)e.setLine(i,lines[i].replace("&","§"));
        e.setLine(0,PREFIX);
    }

    @EventHandler
    public void onSignClick(PlayerInteractEvent e){
        if(!e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getClickedBlock() == null)return;
        BlockState bs = e.getClickedBlock().getState();
        if(!(bs instanceof Sign))return;
        Sign sign = (Sign) bs;
        String id = sign.getLine(3);
        if(!sign.getLine(0).equals(PREFIX) || !CustomConfig.existYml(id))return;
        SHOP_SYSTEM.open(e.getPlayer(),id);
    }
}
