package dev.flavius.onlyproxy;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class System
  extends JavaPlugin implements Listener {
  HashMap<Player, Boolean> canJoin = new HashMap<>();

  public void onEnable() {
    File folder = new File(getDataFolder().getPath());
    
    // New comment
    File config = new File(getDataFolder(), "config.yml");
    if (!config.exists()) {
      if (!folder.exists()) {
        folder.mkdir();
      }
      try {
        FileWriter writer = new FileWriter(config);
        BufferedWriter bWriter = new BufferedWriter(writer);
        bWriter.write("#Dev by Flavius");
        bWriter.newLine();
        bWriter.write("kickMsg:");
        bWriter.newLine();
        bWriter.write("- &cYour first message here");
        bWriter.newLine();
        bWriter.write("- &cYou can't connect from here");
        bWriter.newLine();
        bWriter.write("- &cPlease join ...");
        bWriter.newLine();
        bWriter.write("ip:");
        bWriter.newLine();
        bWriter.write("- 127.0.0.1");
        bWriter.newLine();
        bWriter.write("- 192.168.0.1");
        bWriter.newLine();
        bWriter.write("port:");
        bWriter.newLine();
        bWriter.write("- 25565");
        bWriter.newLine();
        bWriter.write("- 25577");
        bWriter.close();
        writer.close();
      } catch (IOException e) {
        e.printStackTrace();
      } 
      reloadConfig();
      saveDefaultConfig();
    } else {
      getConfig().options().header("Dev by Flavius");
      getConfig().options().copyHeader(true);
      String[] list = { "&cYour first message here", "&cYou can't connect from here", "&cPlease join ..." };
      getConfig().addDefault("kickMsg", list);
      List<String> ip = new ArrayList<>();
      ip.add("127.0.0.1");
      ip.add("192.168.0.1");
      getConfig().addDefault("ip", ip);
      List<String> port = new ArrayList<>();
      port.add("25575");
      port.add("25577");
      getConfig().addDefault("port", port);
      getConfig().options().copyDefaults(true);
      saveConfig();
    } 
    
    PluginManager pm = Bukkit.getServer().getPluginManager();
    pm.registerEvents(this, (Plugin)this);
  }
  
  @EventHandler(priority = EventPriority.MONITOR)
  public void join(PlayerLoginEvent e) {
    List<String> ipProxy = getConfig().getStringList("ip");
    List<String> port = getConfig().getStringList("port");
    Player p = e.getPlayer();
    this.canJoin.put(p, Boolean.valueOf(false));
    String ip = e.getRealAddress().getHostAddress();
    String pport = e.getHostname();
    for (String ipP : ipProxy) {
      if (ip.equals(ipP))
        for (String vp : port)
        { if (pport.endsWith(":" + vp))
            this.canJoin.put(p, Boolean.valueOf(true));  }  
    }  if (!((Boolean)this.canJoin.get(p)).booleanValue()) {
      List<String> kickMsg = getConfig().getStringList("kickMsg");
      String msg = "";
      e.setResult(PlayerLoginEvent.Result.KICK_OTHER);
      for (String aKickMsg : kickMsg) msg = msg + aKickMsg + "\n"; 
      msg = colorize(msg);
      e.disallow(e.getResult(), msg);
    } 
  }
  
  private String colorize(String input) {
    return ChatColor.translateAlternateColorCodes('&', input);
  }
  
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (cmd.getName().equalsIgnoreCase("0pjreload")) {
      if (sender instanceof Player) {
        final Player p = (Player)sender;
        if (p.hasPermission("0pj.reload")) {
          p.sendMessage("config...");
          Runnable t = (Runnable)(new BukkitRunnable()
            {
              public void run() {
                System.this.reloadConfig();
                System.this.saveDefaultConfig();
                p.sendMessage("complete !");
              }
            }).runTaskLater((Plugin)this, 10L);
        } 
      } else {
        getLogger().info("Reload config...");
        Runnable t = (Runnable)(new BukkitRunnable()
          {
            public void run() {
              System.this.reloadConfig();
              System.this.saveDefaultConfig();
              System.this.getLogger().info("Reload complete!");
            }
          }).runTaskLater((Plugin)this, 10L);
      } 
    }
    return super.onCommand(sender, cmd, label, args);
  }
}
