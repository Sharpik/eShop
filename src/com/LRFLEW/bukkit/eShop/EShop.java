package com.LRFLEW.bukkit.eShop;

import java.util.HashMap;

import org.bukkit.material.MaterialData;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import java.util.logging.Logger;

import com.LRFLEW.register.payment.Methods;

public class EShop extends JavaPlugin {
	
	public final HashMap<MaterialData, Pricing> costs = new HashMap<MaterialData, Pricing>();
	public final HashMap<String, MaterialData> iLocal = new HashMap<String, MaterialData>();
	
	private final Prefs prefs = new Prefs();
	private final Perms perms = new Perms();
        
        private static EShop instance;
        static final Logger log = Logger.getLogger("Minecraft");

	@Override
	public void onDisable()
        {
		costs.clear();
		iLocal.clear();
		prefs.clear();

        // NOTE: All registered events are automatically unregistered when a plugin is disabled
		PluginDescriptionFile pdfFile = this.getDescription();
		System.out.println( pdfFile.getName() + " says Goodbye!" );
	}

	@Override
	public void onEnable()
        {
            PluginManager pm = this.getServer().getPluginManager();
		if (!Methods.hasMethod()) pm.disablePlugin(this);
		else
                {
                        instance = this;
                        Pricing.fillSettings(getServer().getLogger(), getDataFolder(), costs);
			Pricing.fillILocal(getServer().getLogger(), getDataFolder(), iLocal);
			prefs.load(getServer().getLogger(), getDataFolder());
			perms.setYeti(prefs);
			
			// set our CommandExecuter for our command
                        //Com kommand = new Com(costs, iLocal, prefs, perms);
                        //getCommand("shop").setExecutor(kommand);
                        
                        //log.info("Debug: Registruju příkaz.");
                        getCommand("shop").setExecutor(new Com(costs, iLocal, prefs, perms));
                        //log.info("Debug: Příkaz registrován.");
			
			PluginDescriptionFile pdfFile = getDescription();
			System.out.println( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );
		}
	}
        
        public static EShop getInstance()
        {
            return instance;
        }

}
