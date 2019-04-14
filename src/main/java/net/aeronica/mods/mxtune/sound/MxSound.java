/*
 * Aeronica's mxTune MOD
 * Copyright 2019, Paul Boese a.k.a. Aeronica
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

/*
 * Aeronica's mxTune MOD
 * Copyright 2018, Paul Boese a.k.a. Aeronica
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package net.aeronica.mods.mxtune.sound;

import net.aeronica.mods.mxtune.config.ModConfig;
import net.aeronica.mods.mxtune.managers.GroupHelper;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.client.audio.SoundEventAccessor;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;

public abstract class MxSound extends MovingSound
{

    private Integer playID;
    private SoundEventAccessor soundEventAccessor;

    /**
     * Implements ISound<br></br>
     * For musical machines carried or used in the world
     */
    MxSound(Integer playID)
    {
        super(ModSoundEvents.PCM_PROXY, SoundCategory.MASTER);
        this.playID = playID;
        this.sound = new PCMSound();
        this.volume = getModVolume();
        this.pitch = 1F;
        this.repeat = false;
        this.repeatDelay = 0;
        this.donePlaying = false;
        Vec3d pos = GroupHelper.getMedianPos(playID);
        this.xPosF = (float) pos.x;
        this.yPosF = (float) pos.y;
        this.zPosF = (float) pos.z;
        this.attenuationType = AttenuationType.LINEAR;
        this.soundEventAccessor = new SoundEventAccessor(this.sound.getSoundLocation(), "mxtune.subtitle.pcm-proxy");
    }

    /** This is used as the key for our PlaySoundEvent handler */
    MxSound()
    {
        super(ModSoundEvents.PCM_PROXY, SoundCategory.MASTER);
    }
    
    @Override
    public SoundEventAccessor createAccessor(SoundHandler handler)
    {
        return this.soundEventAccessor;
    }

    @Override
    public void update()
    {
        if (this.playID != null && ClientAudio.hasPlayID(playID))
        {
            Vec3d pos = GroupHelper.getMedianPos(playID);
            this.xPosF = (float) pos.x;
            this.yPosF = (float) pos.y;
            this.zPosF = (float) pos.z; 
        }
        else
        {
            this.setDonePlaying();
        }
        this.volume = getModVolume();
    }
    
    private void setDonePlaying()
    {
        this.repeat = false;
        this.donePlaying = true;
        this.repeatDelay = 0;
    }

    private float getModVolume() { return ModConfig.getClientPlayerVolume(); }

    public float getVolume()
    {
        return volume;
    }
}
