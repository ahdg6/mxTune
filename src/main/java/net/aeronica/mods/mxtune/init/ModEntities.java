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
package net.aeronica.mods.mxtune.init;

import net.aeronica.mods.mxtune.MXTune;
import net.aeronica.mods.mxtune.Reference;
import net.aeronica.mods.mxtune.entity.EntitySittableBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public enum ModEntities
{
    ;
    protected static int entityID = 0;
    
    private static int getEntityID() {return entityID++;}

    public static void init()
    {
        registerModEntity(EntitySittableBlock.class, "mountableblock", 80, 1, false);
    }
    
    private static void registerModEntity(Class<? extends Entity> entityClass, String name, int trackingRange, int updateFrequency, boolean sendsVelocityUpdates)
    {
        EntityRegistry.registerModEntity(new ResourceLocation(Reference.MOD_ID, name), entityClass, name, getEntityID(), MXTune.instance, trackingRange, updateFrequency, sendsVelocityUpdates);
    }
}
