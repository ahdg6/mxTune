/**
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
package net.aeronica.mods.mxtune.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockInstrument2H extends BlockHorizontal implements IPlacedInstrument
{

    protected BlockInstrument2H(Material material, String blockName)
    {
        super(material);
        setBlockName(this, blockName);
        this.setSoundType(SoundType.WOOD);
        setHardness(0.2F);
        disableStats();
    }
    /**
     * Set the registry name of {@code item} to {@code itemName} and the
     * Unlocalized name to the full registry name.<br>
     * 
     * @param block
     *            The block
     * @param blockName
     *            The block's name
     */
    public static void setBlockName(Block block, String blockName)
    {
        block.setRegistryName(blockName);
        block.setUnlocalizedName(block.getRegistryName().toString());
    }
    @Override
    public TileInstrument getTE(World worldIn, BlockPos pos)
    {
        return null;
    }
    
}