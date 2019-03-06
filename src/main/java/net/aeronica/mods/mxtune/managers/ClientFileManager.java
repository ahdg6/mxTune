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

package net.aeronica.mods.mxtune.managers;

import net.aeronica.mods.mxtune.Reference;

import java.util.UUID;

public class ClientFileManager
{
    // TODO: Client side server cache folders must be per server (integrated or dedicated)
    // This must be done to avoid naming conflicts.
    private ClientFileManager() { /* NOP*/ }

    static boolean songAvailable(UUID uuidPlayList)
    {
        return !Reference.EMPTY_UUID.equals(uuidPlayList);
    }
}