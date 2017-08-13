/**
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
package net.aeronica.mods.mxtune.crafting;

import java.util.function.BooleanSupplier;

import com.google.gson.JsonObject;

import net.aeronica.mods.mxtune.util.ModLogger;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;


public class RecipeEnabledFactory implements IConditionFactory
{

    @Override
    public BooleanSupplier parse(JsonContext context, JsonObject json)
    {
        String value = JsonUtils.getString(json , "item");
        ModLogger.info("*** *** modid: %s, jsonObj: %s, value: %s", context.getModId(), json.toString(), value);
        return () -> Boolean.TRUE;
    }

}
