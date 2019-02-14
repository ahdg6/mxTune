package net.aeronica.mods.mxtune.util;


import net.aeronica.mods.mxtune.config.ModConfig;
import net.aeronica.mods.mxtune.network.PacketDispatcher;
import net.aeronica.mods.mxtune.network.client.AudiblePingPlayerMessage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.SoundEvent;

public class Util
{

    private Util() { /* NOP */ }

    /**
     * Forge really likes annotation magic. This makes static analysis tools shut up.
     * <p></p>
     * This method was copied from https://github.com/JamiesWhiteShirt/clothesline
     * https://github.com/JamiesWhiteShirt/clothesline/blob/master/src/main/java/com/jamieswhiteshirt/clothesline/common/Util.java
     *
     * Copyright 2018 Erlend Åmdal
     *
     * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
     * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
     * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and
     * to permit persons to whom the Software is furnished to do so, subject to the following conditions:
     *
     * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
     * Software.
     *
     * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO TH
     * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
     * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
     * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
     */
    @SuppressWarnings("ConstantConditions")
    public static <T> T nonNullInjected()
    {
        return null;
    }

    public static void audiblePingPlayer(EntityPlayer entityPlayer, SoundEvent soundEvent)
    {
        PacketDispatcher.sendTo(new AudiblePingPlayerMessage(soundEvent), (EntityPlayerMP) entityPlayer);
    }

    public static Boolean inDev()
    {
        return (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment") || ModConfig.moreDebugMessages();
    }
}
