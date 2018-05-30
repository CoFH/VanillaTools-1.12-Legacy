package cofh.vanillatools;

import cofh.CoFHCore;
import cofh.core.init.CoreEnchantments;
import cofh.core.init.CoreProps;
import cofh.core.util.ConfigHandler;
import cofh.vanillatools.proxy.Proxy;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod (modid = VanillaTools.MOD_ID, name = VanillaTools.MOD_NAME, version = VanillaTools.VERSION, dependencies = VanillaTools.DEPENDENCIES, updateJSON = VanillaTools.UPDATE_URL, certificateFingerprint = "8a6abf2cb9e141b866580d369ba6548732eff25f")
public class VanillaTools {

	public static final String MOD_ID = "vanillatools";
	public static final String MOD_NAME = "CoFH: Vanilla+ Tools";

	public static final String VERSION = "1.0.0";
	public static final String VERSION_MAX = "1.1.0";
	public static final String VERSION_GROUP = "required-after:" + MOD_ID + "@[" + VERSION + "," + VERSION_MAX + ");";
	public static final String UPDATE_URL = "https://raw.github.com/cofh/version/master/" + MOD_ID + "_update.json";

	public static final String DEPENDENCIES = CoFHCore.VERSION_GROUP;

	@Instance (MOD_ID)
	public static VanillaTools instance;

	@SidedProxy (clientSide = "cofh.vanillatools.proxy.ProxyClient", serverSide = "cofh.vanillatools.proxy.Proxy")
	public static Proxy proxy;

	public static final Logger LOG = LogManager.getLogger(MOD_ID);
	public static final ConfigHandler CONFIG = new ConfigHandler(VERSION);

	public VanillaTools() {

		super();
	}

	/* INIT */
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {

		CONFIG.setConfiguration(new Configuration(new File(CoreProps.configDir, "/cofh/vanillaplus/tools.cfg"), true));

		Equipment.preInit();
		CoreEnchantments.register();

		proxy.preInit(event);
	}

	@EventHandler
	public void initialize(FMLInitializationEvent event) {

		proxy.initialize(event);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {

		proxy.postInit(event);
	}

	@EventHandler
	public void loadComplete(FMLLoadCompleteEvent event) {

		CONFIG.cleanUp(false, true);

		LOG.info(MOD_NAME + ": Load Complete.");
	}

}
