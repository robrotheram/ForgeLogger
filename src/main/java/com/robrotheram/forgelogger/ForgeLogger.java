package com.robrotheram.forgelogger;

import com.robrotheram.forgelogger.utils.CoordinatesChunk;
import com.robrotheram.forgelogger.utils.Database;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.config.Property;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Mod(
        modid   = ForgeLogger.MODID,
        name    = ForgeLogger.MODID,
        version = ForgeLogger.VERSION,

        acceptableRemoteVersions = "*",
        acceptableSaveVersions   = ""
)
public class ForgeLogger {
    /**
     * Frozen at 1.0.0 to prevent misleading world save error
     */
    public static final String VERSION = "0.1";
    public static final String MODID = "ForgeLogger";
    static final String NAME = "ForgeLogger";
    static final List ALIASES = Arrays.asList("FL", "ForgeLogger");
    private static final Logger LOG = LogManager.getFormatterLogger(ForgeLogger.MODID);

    public static ForgeLogger INSTANCE = null;
    /**
     * Shortcut reference to vanilla server instance
     */
    public static MinecraftServer SERVER = null;

    //Define your configuration object
    private static Configuration config = null;


    @Mod.EventHandler
    @SideOnly(Side.CLIENT)
    public void clientPreInit(FMLPreInitializationEvent event) {
        LOG.error("This mod is intended only for use on servers");
        LOG.error("Please consider removing this mod from your installation");
    }

    @Mod.EventHandler
    @SideOnly(Side.SERVER)
    public void preInit(FMLPreInitializationEvent event) {
        LOG.info("Beginning pre-initialization");
        Configuration config=new Configuration(event.getSuggestedConfigurationFile());
        config.load();
        config.setCategoryComment(Configuration.CATEGORY_GENERAL,"Input here all your whitelists per bag. \nIf empty, bag won't be added to the world.\nUse modid:* to whitelist the whole mod.\nFor example:\nminecraft:wool/2 will add Magenta wool to the whitelist. \nminecraft:wool will add every wool type. \nminecraft:wool/0+1+2 will add damage value 0,1 and 2.\nAdd multiple items by using a comma between items.");
        String black = config.get(Configuration.CATEGORY_GENERAL, "Whitelist Items/Blocks for black bag", "").getString();
        String red=config.get(Configuration.CATEGORY_GENERAL,"Whitelist Items/Blocks for red bag","").getString();
        String green=config.get(Configuration.CATEGORY_GENERAL,"Whitelist Items/Blocks for green bag","").getString();
        String brown=config.get(Configuration.CATEGORY_GENERAL,"Whitelist Items/Blocks for brown bag","").getString();
        String blue=config.get(Configuration.CATEGORY_GENERAL,"Whitelist Items/Blocks for blue bag","").getString();
        String purple=config.get(Configuration.CATEGORY_GENERAL,"Whitelist Items/Blocks for purple bag","").getString();
        String cyan=config.get(Configuration.CATEGORY_GENERAL,"Whitelist Items/Blocks for cyan bag","").getString();
        String silver=config.get(Configuration.CATEGORY_GENERAL,"Whitelist Items/Blocks for silver bag","").getString();
        String gray=config.get(Configuration.CATEGORY_GENERAL,"Whitelist Items/Blocks for gray bag","").getString();
        String pink=config.get(Configuration.CATEGORY_GENERAL,"Whitelist Items/Blocks for pink bag","").getString();
        String lime=config.get(Configuration.CATEGORY_GENERAL,"Whitelist Items/Blocks for lime bag","").getString();
        String yellow=config.get(Configuration.CATEGORY_GENERAL,"Whitelist Items/Blocks for yellow bag","").getString();
        String lightBlue=config.get(Configuration.CATEGORY_GENERAL,"Whitelist Items/Blocks for lightBlue bag","").getString();
        String magenta=config.get(Configuration.CATEGORY_GENERAL,"Whitelist Items/Blocks for magenta bag","").getString();
        String orange=config.get(Configuration.CATEGORY_GENERAL,"Whitelist Items/Blocks for orange bag","").getString();
        String white=config.get(Configuration.CATEGORY_GENERAL,"Whitelist Items/Blocks for white bag","").getString();
        config.save();


    }


    @Mod.EventHandler
    @SideOnly(Side.SERVER)
    public void serverStart(FMLServerStartingEvent event) {
        if (INSTANCE == null) INSTANCE = this;
        if (SERVER == null) SERVER = event.getServer();
        Database dao = new Database();

        try {
            dao.readDataBase();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    /**
     * load the configuration values from the configuration file
     */
    public static void syncFromFile()
    {
        syncConfig(true, true);
    }

    private static void syncConfig(boolean loadConfigFromFile, boolean readFieldsFromConfig)
    {

        if (loadConfigFromFile) {
            config.load();
        }

        final int MY_INT_MIN_VALUE = 3;
        final int MY_INT_MAX_VALUE = 12;
        final int MY_INT_DEFAULT_VALUE = 10;
        Property propMyInt = config.get(CATEGORY_NAME_GENERAL, "myInteger", MY_INT_DEFAULT_VALUE,
                "Configuration integer (myInteger)", MY_INT_MIN_VALUE, MY_INT_MAX_VALUE);
        propMyInt.setLanguageKey("gui.mbe70_configuration.myInteger");

        // boolean
        final boolean MY_BOOL_DEFAULT_VALUE = true;
        Property propMyBool = config.get(CATEGORY_NAME_GENERAL, "myBoolean", MY_BOOL_DEFAULT_VALUE);
        propMyBool.comment = "Configuration boolean (myBoolean)";
        propMyBool.setLanguageKey("gui.mbe70_configuration.myBoolean").setRequiresMcRestart(true);

        // double
        final double MY_DOUBLE_MIN_VALUE = 0.0;
        final double MY_DOUBLE_MAX_VALUE = 1.0;
        final double MY_DOUBLE_DEFAULT_VALUE = 0.80;
        Property propMyDouble = config.get(CATEGORY_NAME_GENERAL, "myDouble",
                MY_DOUBLE_DEFAULT_VALUE, "Configuration double (myDouble)",
                MY_DOUBLE_MIN_VALUE, MY_DOUBLE_MAX_VALUE);
        propMyDouble.setLanguageKey("gui.mbe70_configuration.myDouble");

        // string
        final String MY_STRING_DEFAULT_VALUE = "default";
        Property propMyString = config.get(CATEGORY_NAME_GENERAL, "myString", MY_STRING_DEFAULT_VALUE);
        propMyString.comment = "Configuration string (myString)";
        propMyString.setLanguageKey("gui.mbe70_configuration.myString").setRequiresWorldRestart(true);

        if (readFieldsFromConfig) {
            //If getInt cannot get an integer value from the config file value of myInteger (e.g. corrupted file)
            // it will set it to the default value passed to the function
            myInteger = propMyInt.getInt(MY_INT_DEFAULT_VALUE);
            if (myInteger > MY_INT_MAX_VALUE || myInteger < MY_INT_MIN_VALUE) {
                myInteger = MY_INT_DEFAULT_VALUE;
            }
            myBoolean = propMyBool.getBoolean(MY_BOOL_DEFAULT_VALUE); //can also use a literal (see integer example) if desired
            myDouble = propMyDouble.getDouble(MY_DOUBLE_DEFAULT_VALUE);
            if (myDouble > MY_DOUBLE_MAX_VALUE || myDouble < MY_DOUBLE_MIN_VALUE) {
                myDouble = MY_DOUBLE_DEFAULT_VALUE;
            }
            myString = propMyString.getString();

        }

        // ---- step 4 - write the class's variables back into the config properties and save to disk -------------------

        //  This is done even for a loadFromFile==true, because some of the properties may have been assigned default
        //    values if the file was empty or corrupt.

        propMyInt.set(myInteger);
        propMyBool.set(myBoolean);
        propMyDouble.set(myDouble);
        propMyString.set(myString);

        if (config.hasChanged()) {
            config.save();
        }
    }


    public ArrayList<CoordinatesChunk> getLoadedChunks(int dimension){
        HashSet<CoordinatesChunk> chunkStatus = new HashSet<CoordinatesChunk>();
        WorldServer world = DimensionManager.getWorld(dimension);
        if (world != null)
        {
            for (ChunkCoordIntPair coord : world.getPersistentChunks().keySet()){
                chunkStatus.add(new CoordinatesChunk(dimension, coord, (byte)1));
            }

            for (Object o : ((ChunkProviderServer)world.getChunkProvider()).loadedChunks){
                Chunk chunk = (Chunk)o;

                chunkStatus.add(new CoordinatesChunk(dimension, chunk.getChunkCoordIntPair(), (byte)0));
            }
        }

        return new ArrayList<CoordinatesChunk>(chunkStatus);
    }


    //Declare all configuration fields used by the mod here
    public static int myInteger;
    public static boolean myBoolean;
    public static double myDouble;
    public static int[] myIntList;
    public static String myString;
    public static String myColour;

    public static final String CATEGORY_NAME_GENERAL = "category_general";
    public static final String CATEGORY_NAME_OTHER = "category_other";
}

