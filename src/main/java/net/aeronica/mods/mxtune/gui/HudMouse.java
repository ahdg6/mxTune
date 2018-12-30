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
package net.aeronica.mods.mxtune.gui;


public class HudMouse
{
    private int hudPos;
    private int mouseX;
    private int mouseY;

    public HudMouse() { /* NOP */ }

    public int getHudPos()
    {
        return hudPos;
    }

    public void setHudPos(int hudPos)
    {
        this.hudPos = hudPos;
    }

    public int getMouseX()
    {
        return mouseX;
    }

    public void setMouseX(int mouseX)
    {
        this.mouseX = mouseX;
    }

    public int getMouseY()
    {
        return mouseY;
    }

    public void setMouseY(int mouseY)
    {
        this.mouseY = mouseY;
    }
}

