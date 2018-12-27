/*
 * Aeronica's mxTune MOD
 * Copyright {2016} Paul Boese aka Aeronica
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.aeronica.mods.mxtune.config;

import com.google.common.collect.Maps;
import net.aeronica.mods.mxtune.Reference;
import net.aeronica.mods.mxtune.util.ModLogger;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.*;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Map;

public class ModConfig
{
    private ModConfig() { /* NOP */ }

    /** Client Configuration Settings */
    @Config(modid = Reference.MOD_ID, name = Reference.MOD_ID + "/" + Reference.MOD_ID + "_client", category="client")
    @LangKey("config.mxtune:ctgy.client")
    public static class ConfigClient
    {
        private ConfigClient() {/* NOP */}

        @Comment("Sound Channel Configuration")
        @LangKey("config.mxtune:soundChannelConfig")
        public static final Sound sound = new Sound();
        
        @Comment("Internet Resources")
        @LangKey("config.mxtune:internetResouces")
        public static final Links links = new Links();

        @Config.LangKey("config.mxtune.player_name_in_window_title")
        @Config.Comment("Player name in window title. Updates on logon and/or changing dimension.")
        public static PLAYER_NAME_IN_WINDOW_TITLE player_name_in_window_title = PLAYER_NAME_IN_WINDOW_TITLE.DISABLED;

        public static class Sound
        {
            @Name("Automatically configure sound channels")
            @LangKey("config.mxtune:autoConfigureChannels")
            public boolean autoConfigureChannels = true;
        }
        
        public static class Links
        {
            @Name("Site Links")
            @LangKey("config.mxtune:mmlLink")
            public String site = "https://mabibeats.com/";
        }

        public enum PLAYER_NAME_IN_WINDOW_TITLE
        {
            @Config.LangKey("config.mxtune.player_name_in_title.disabled")
            DISABLED("config.mxtune.player_name_in_title.disabled"),
            @Config.LangKey("config.mxtune.player_name_in_title.enabled")
            ENABLED("config.mxtune.player_name_in_title.enabled");

            private String translateKey;

            PLAYER_NAME_IN_WINDOW_TITLE(String translateKeyIn) {this.translateKey = translateKeyIn;}

            public String toString() {return this.translateKey;}
        }
    }
    
    @Config(modid= Reference.MOD_ID, name = Reference.MOD_ID + "/" + Reference.MOD_ID + "_general", category="general")
    @LangKey("config.mxtune:ctgy.general")
    public static class ConfigGeneral
    {
        private ConfigGeneral() {/* NOP */}

        @Comment("General Configuration")
        @LangKey("config.mxtune:generalConfig")
        public static final General general = new General();
        
        public static class General
        {
            @Name("Disable Welcome Status Message")
            @LangKey("config.mxtune:hideWelcomeStatusMessage")   
            public boolean hideWelcomeStatusMessage = false;
            
            @Name("Listener Range")
            @LangKey("config.mxtune:listenerRange")
            @RangeInt(min=10, max=64)
            public int listenerRange = 64;

            @Name("Group Play Abort Distance")
            @LangKey("config.mxtune:groupPlayAbortDistance")
            @RangeInt(min=10, max=24)    
            public int groupPlayAbortDistance = 10;
        }
    }
    
    @Config(modid = Reference.MOD_ID, name = Reference.MOD_ID + "/" + Reference.MOD_ID + "_recipes", type = Type.INSTANCE, category="recipe")
    @LangKey("config.mxtune:ctgy.recipes")
    public static class ConfigRecipes
    {
        private ConfigRecipes() {/* NOP */}

        @Name("Toggles")
        @Comment({"mxTune Recipes", "Requires a Server Restart if Changed!", "B:<name>=(true|false)"})
        @RequiresMcRestart
        public static Map<String, Boolean> recipeToggles;

        private static final String[] modItemRecipeNames = {
                "band_amp", "bass_drum",
                "cello", "chalumeau",
                "cymbels", "electric_guitar", "flute", "hand_chimes",
                "harp", "harpsichord", "harpsichord_coupled", "lute",
                "lyre", "mandolin", "music_paper", "orchestra_set",
                "piano", "recorder", "roncadora", "snare_drum",
                "spinet_piano", "standard_set", "trumpet", "tuba",
                "tuned_flute", "tuned_whistle", "ukulele",
                "violin", "whistle"
        };
        
        static
        {
            recipeToggles = Maps.newHashMap();
            for (String modItemRecipeName : modItemRecipeNames)
            {
                recipeToggles.put(modItemRecipeName, true);
            }
        }
    }
    
    public static float getListenerRange() {return (float)ConfigGeneral.general.listenerRange;}

    public static float getGroupPlayAbortDistance() {return (float)ConfigGeneral.general.groupPlayAbortDistance;}

    public static boolean hideWelcomeStatusMessage() {return ConfigGeneral.general.hideWelcomeStatusMessage;}

    public static boolean getAutoConfigureChannels() {return ConfigClient.sound.autoConfigureChannels;}

    public static String getMmlLink() {return ConfigClient.links.site;}

    /**
     * Will only allow this mods recipes to be disabled
     * @param stackIn stack to be tested
     * @return recipe state
     */
    public static boolean isRecipeEnabled(ItemStack stackIn)
    {
        // strip off "item." or "tile." and "instrument." to get the raw item name without domain and item base names
        String itemName = stackIn.getTranslationKey().replaceFirst("(item\\.|tile\\.)" + Reference.MOD_ID + "\\:", "");
        itemName = itemName.replaceFirst("instrument\\.", "");
        boolean enableState = !ConfigRecipes.recipeToggles.containsKey(itemName) || (ConfigRecipes.recipeToggles.get(itemName) && !itemName.contains(":"));
        ModLogger.debug("Recipe Enabled? %s %s", itemName, enableState);
        return enableState;
    }
    
    public static boolean isRecipeHidden(ItemStack stackIn)
    {
        return !isRecipeEnabled(stackIn);
    }
    
    @Mod.EventBusSubscriber
    public static class RegistrationHandler
    {
        private RegistrationHandler() {/* NOP */}

        @SubscribeEvent
        public static void onConfigChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event)
        {
            ModLogger.info("On ConfigChanged: %s", event.getModID());
            if(event.getModID().equals(Reference.MOD_ID))
            {
                ConfigManager.sync(Reference.MOD_ID, Config.Type.INSTANCE);
            }
        }
    }
}
