package fgtXray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraftforge.common.ConfigCategory;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;
import net.minecraftforge.oredict.OreDictionary;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import fgtXray.OreInfo;
import fgtXray.client.DefaultConfig;
import fgtXray.client.ConfigHandler;
import fgtXray.client.OresSearch;

@Mod(modid="FgtXray", name="Fgt X-Ray", version="0.0.1")
@NetworkMod(clientSideRequired=true)
public class FgtXRay {
	public static int localPlyX, localPlyY, localPlyZ; // For internal use in the ClientTick thread.
	public static boolean drawOres = false; // Off by default
	public static boolean skipGenericBlocks = true; // See ClientTick.run() thread. Skip common blocks in overworld/nether/end.
	
	public static String[] distStrings = new String[]{ "8", "16", "32", "48", "64", "80", "128", "256" }; // Strings for use in the GUI
	public static int[] distNumbers = new int[]{ 8, 16, 32, 48, 64, 80, 128, 256 }; // Radius +/- around the player to search. So 8 is 8 on left and right of player plus under the player. So 17x17 area. 
	public static int distIndex = 0; // Index for the distNumers array. Default search distance.
	
	public static Configuration config = null;
	
	public static Map<String, OreInfo> oredictOres = new HashMap<String, OreInfo>();
		/* Ores to check through the ore dictionary and add each instance found to the searchList. 
		 * put( "oreType", new OreInfo(...) ) oreType is the ore dictionary string id. Press Print OreDict and check console to see list.
		 * OreInfo( String "Gui Name", // The name to be displayed in the GUI.
		 *     int id, int meta, // Leave these at 0. The OresSearch will set them through the ore dictionary.
		 *     int color, // 0x RED GREEN BLUE (0xRRGGBB)
		 *     bool enabled) // Should the be on by default. Its then set internally by GuiSettings.
		 * Open DefaultConfig.java for more info.
		 */
	
	public static List<OreInfo> customOres = new ArrayList<OreInfo>();
		/* List of custom id:meta to add.
		 * OreInfo( String "Gui Name", // Displayed in the GUI.
		 *     int id, int meta, // Set these to whatever the id:meta is for your block.
		 *     int color, // color 0xRRGGBB
		 *     bool enabled) // On by default? 
		 */
	
	// The instance of your mod that Forge uses.
	@Instance(value = "FgtXray")
	public static FgtXRay instance;
	
	// Says where the client and server 'proxy' code is loaded.
	@SidedProxy(clientSide="fgtXray.client.ClientProxy", serverSide="fgtXray.ServerProxy")
	public static ServerProxy proxy;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		Configuration config = new Configuration( event.getSuggestedConfigurationFile() );
		config.load();
		
		if( config.getCategoryNames().isEmpty() ){
			System.out.println("[Fgt XRay] Config file not found. Creating now.");
			DefaultConfig.create( config );
			config.save();
		}
		
		ConfigHandler.setup( event ); // Read the config file and setup environment.
	}

	@EventHandler
	public void load(FMLInitializationEvent event) {
		proxy.proxyInit();
		//OreDictionary.registerOre("oreGold", Block.oreGold); // Testing Duplicate OreDict bug.
		if (OresSearch.searchList.isEmpty()){ // Populate the OresSearch.searchList
			OresSearch.get();
		}
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
	}
}
