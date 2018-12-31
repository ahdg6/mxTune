/*
 * Aeronica's mxTune MOD
 * Copyright {2016} Paul Boese a.k.a. Aeronica
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
package net.aeronica.mods.mxtune.blocks;

import net.aeronica.mods.mxtune.items.ItemInstrument;
import net.aeronica.mods.mxtune.util.ModLogger;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static net.aeronica.mods.mxtune.util.SheetMusicUtil.*;

public interface IMusicPlayer
{
    default String getMML(World worldIn, BlockPos blockPos)
    {
        StringBuilder buildMML = new StringBuilder();
        TileEntity tileEntity = worldIn.getTileEntity(blockPos);

        if (tileEntity instanceof TileInstrument)
        {
            TileInstrument tileInstrument = (TileInstrument) tileEntity;
            try
            {
                for (int slot = 0; slot < tileInstrument.getInventory().getSlots(); slot++)
                {
                    ItemStack stackInSlot = tileInstrument.getInventory().getStackInSlot(slot);
                    if (!stackInSlot.isEmpty() && stackInSlot.getItem() instanceof ItemInstrument)
                    {
                        ItemInstrument instrument = (ItemInstrument) stackInSlot.getItem();
                        int patch = instrument.getPatch(stackInSlot.getMetadata());
                        ItemStack sheetMusic = getSheetMusic(stackInSlot);
                        if (!sheetMusic.isEmpty() && sheetMusic.getTagCompound() != null)
                        {
                            NBTTagCompound contents = (NBTTagCompound) sheetMusic.getTagCompound().getTag(KEY_SHEET_MUSIC);
                            if (contents.hasKey(KEY_MML))
                            {
                                String mml = contents.getString(KEY_MML);
                                mml = mml.replace("MML@", "MML@I" + patch);
                                buildMML.append(slot).append("=").append(mml).append("|");
                            }
                        }
                    }
                }
            } catch (Exception e)
            {
                ModLogger.error(e);
            }
         }
        return buildMML.toString();
    }
}
