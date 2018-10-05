/**
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
package net.aeronica.mods.mxtune.gui;

import net.aeronica.mods.mxtune.MXTuneMain;
import net.aeronica.mods.mxtune.groups.GROUPS;
import net.aeronica.mods.mxtune.inventory.IInstrument;
import net.aeronica.mods.mxtune.options.MusicOptionsUtil;
import net.aeronica.mods.mxtune.status.ClientCSDMonitor;
import net.aeronica.mods.mxtune.util.PlacedInstrumentUtil;
import net.aeronica.mods.mxtune.util.SheetMusicUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class GuiJamOverlay extends Gui
{
    private static final String INSTRUMENT_INVENTORY_EMPTY = "["+I18n.format("mxtune.instrumentInventory.empty")+"]";
    private static final ResourceLocation TEXTURE_STATUS = new ResourceLocation(MXTuneMain.MOD_ID, "textures/gui/status_widgets.png");
    static final int HOT_BAR_CLEARANCE = 40;
    private static final int WIDGET_WIDTH = 256;
    private static final int WIDGET_HEIGHT = 104;

    private static final int PLACARD_ICON_SIZE = 24;
    private static final int PLACARD_ICON_BASE_U_OFFSET = 54;
    private static final int PLACARD_ICON_BASE_V_OFFSET = 200;
    private static final int PLACARD_ICONS_PER_ROW = 8;

    private Minecraft mc;
    private FontRenderer fontRenderer;
    
    private static class GuiJamOverlayHolder {private static final GuiJamOverlay INSTANCE = new GuiJamOverlay();}
    public static GuiJamOverlay getInstance() {return GuiJamOverlayHolder.INSTANCE;}
    
    public GuiJamOverlay()
    {
        this.mc = Minecraft.getMinecraft();
        this.fontRenderer = this.mc.fontRenderer;
    }

    private static int hudTimer = 0;
    private static int count = 0;
    private static final int HUD_TIME = 5; /* seconds */
    private static void hudTimerReset() {hudTimer = HUD_TIME;}
    
    @SuppressWarnings("static-access")
    @SubscribeEvent
    public void onEvent(ClientTickEvent event)
    {
        if (event.side == Side.CLIENT && event.phase == TickEvent.Phase.END)
        {
            /* once per second second */
            if ((count++ % 20 == 0) && (hudTimer > 0)) {hudTimer--;}
            /* 4 times per second */
            if (count % 5 == 0) marqueePos++;
            /* once every 1/2 second */
            if (count % 10 == 0) ClientCSDMonitor.detectAndSend();
            count += this.partialTicks;
        }
    }
    
    private boolean canRenderHud(EntityPlayer playerIn)
    {        
        if (inGuiHudAdjust())
            return true;
        else return (!MusicOptionsUtil.isHudDisabled(playerIn) && (hudTimer > 0));
    }
    
    private boolean inGuiHudAdjust() {return (mc.currentScreen instanceof GuiHudAdjust);}
//    private static int lastWidth = 0;
//    private static int lastHeight = 0;
    private static boolean riding;
    private static ItemStack lastItemStack = ItemStack.EMPTY;
    private static ItemStack itemStack;
    private static float partialTicks;
    
    private static boolean isRidingFlag() {return riding;}
    private static void setRidingFlag(boolean flag) {riding = flag;}
    
    @SuppressWarnings("static-access")
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onRenderExperienceBar(RenderGameOverlayEvent.Post event)
    {

       if (mc.gameSettings.showDebugInfo) return;

       if (event.isCancelable() || event.getType() != ElementType.EXPERIENCE) { return; }
        EntityPlayerSP player = this.mc.player;
 
        int width = event.getResolution().getScaledWidth();
        int height = event.getResolution().getScaledHeight() - HOT_BAR_CLEARANCE;
        partialTicks = event.getPartialTicks();
//        if (hudData == null || inGuiHudAdjust() || lastWidth != width || lastHeight != height)
//        {
        HudData hudData = HudDataFactory.calcHudPositions((inGuiHudAdjust() ? MusicOptionsUtil.getAdjustPositionHud() : MusicOptionsUtil.getPositionHUD(player)), width, height);
//            lastWidth = width; lastHeight = height;
//        }

        ItemStack sheetMusic;
        if(PlacedInstrumentUtil.isRiding(player))
        {
            BlockPos pos = PlacedInstrumentUtil.getRiddenBlock(player);
            sheetMusic = SheetMusicUtil.getSheetMusic(pos, player, true);
            if (!isRidingFlag()) hudTimerReset();
            setRidingFlag(true);
        } else 
        {
            itemStack = player.getHeldItemMainhand();
            sheetMusic = SheetMusicUtil.getSheetMusic(itemStack);
            setRidingFlag(false);
        }
        
        if (inGuiHudAdjust() || ((!itemStack.isEmpty()) && (itemStack.getItem() instanceof IInstrument)) || isRidingFlag())
        {
            if ((lastItemStack.isEmpty() || (!itemStack.isEmpty() && !itemStack.equals(this.lastItemStack))) && !isRidingFlag()) {hudTimerReset(); lastItemStack = itemStack;}
            if (canRenderHud(player))
                renderHud(hudData, player, sheetMusic);
            else
                renderMini(hudData, player);
        } else lastItemStack = itemStack;
    }

    protected class GroupData
    {
        Integer memberID;
        String memberName;
        Integer memberNameWidth;
        Tuple<Integer, Integer> notePos;
        Integer playStatus;
           
        GroupData(Integer memberID, String memberName, Integer memberNameWidth, Tuple<Integer, Integer> notePos, Integer playStatus)
        {
            super();
            this.memberID = memberID;
            this.memberName = memberName;
            this.memberNameWidth = memberNameWidth;
            this.notePos = notePos;
            this.playStatus = playStatus;
        }
        
        public Integer getPlayStatus()
        {
            return playStatus;
        }
        public void setPlayStatus(Integer playStatus)
        {
            this.playStatus = playStatus;
        }
        public Tuple<Integer, Integer> getNotePos()
        {
            return notePos;
        }
        public void setNotePos(Tuple<Integer, Integer> notePos)
        {
            this.notePos = notePos;
        }
        String getMemberName()
        {
            return memberName;
        }
        public void setMemberName(String memberName)
        {
            this.memberName = memberName;
        }
        public Integer getMemberNameWidth()
        {
            return memberNameWidth;
        }
        public void setMemberNameWidth(Integer memberNameWidth)
        {
            this.memberNameWidth = memberNameWidth;
        }
    }
    
    private List<GroupData> processGroup(EntityPlayer playerIn)
    {
        ArrayList<GroupData> groupData = new ArrayList<GroupData>();
        int index = 0;
        Integer memberNameWidth;
        Tuple<Integer, Integer> notePos;
        Integer playStatus;
        Integer groupID;
        Integer memberID;
        String memberName;
        GroupData memberData;
        
        if (GROUPS.getClientGroups() != null || GROUPS.getClientMembers() != null)
        {
            groupID = GROUPS.getMembersGroupID(playerIn.getEntityId());
            
            /* Only draw if player is a member of a group */
            if (groupID != null)
            {
                /* Always add the leader at the HEAD of the list */
                memberID = GROUPS.getLeaderOfGroup(groupID);
                memberName = playerIn.getEntityWorld().getEntityByID(memberID).getDisplayName().getUnformattedText();
                memberNameWidth = fontRenderer.getStringWidth(memberName);
                playStatus = GROUPS.getIndex(memberID);     
                notePos = new Tuple<>(notePosMembers[index][0], notePosMembers[index][1]);
                memberData = new GroupData(memberID, memberName, memberNameWidth, notePos, playStatus);
                groupData.add(memberData);
                index++;

                /* Add the remaining members taking care to not add the leader a 2nd time. */
                Set<Integer> set = GROUPS.getClientMembers().keySet();
                for (Iterator<Integer> im = set.iterator(); im.hasNext();)
                {
                    memberID = im.next();
                    if (groupID.equals(GROUPS.getMembersGroupID(memberID)) && !memberID.equals(GROUPS.getLeaderOfGroup(groupID)))
                    {
                        memberName = playerIn.getEntityWorld().getEntityByID(memberID).getDisplayName().getUnformattedText();
                        memberNameWidth = fontRenderer.getStringWidth(memberName);
                        playStatus = GROUPS.getIndex(memberID);     
                        notePos = new Tuple<>(notePosMembers[index][0], notePosMembers[index][1]);
                        memberData = new GroupData(memberID, memberName, memberNameWidth, notePos, playStatus);
                        groupData.add(memberData);
                        index++;
                    }
                }
            }
        }
        return groupData;
    }
        
    @SuppressWarnings("unused")
    private void drawDebug(HudData hd, int maxWidth, int maxHeight)
    {
        int statusWidth, qX, qY;
        if (GROUPS.getClientPlayStatuses() != null && !GROUPS.getClientPlayStatuses().isEmpty())
        {
            String status = "Play Status:    " + GROUPS.getClientPlayStatuses().toString();
            statusWidth = fontRenderer.getStringWidth(status);
            qX = hd.quadX(maxWidth, 0, 4, statusWidth);
            qY = hd.quadY(maxHeight, 110, 4, 10);
            fontRenderer.drawStringWithShadow(status, qX, qY, 0xFFFFFF);
        }
        if (GROUPS.getPlayIDMembers() != null && !GROUPS.getPlayIDMembers().isEmpty())
        {
            String status = "PlayID Members: " + GROUPS.getPlayIDMembers().toString();
            statusWidth = fontRenderer.getStringWidth(status);
            qX = hd.quadX(maxWidth, 0, 4, statusWidth);
            qY = hd.quadY(maxHeight, 120, 4, 10);
            fontRenderer.drawStringWithShadow(status, qX, qY, 0xFFFFFF);
        }
        if (GROUPS.getActivePlayIDs() != null && !GROUPS.getActivePlayIDs().isEmpty())
        {
            String status = "ActivePlayIDs:  " + GROUPS.getActivePlayIDs().toString();
            statusWidth = fontRenderer.getStringWidth(status);
            qX = hd.quadX(maxWidth, 0, 4, statusWidth);
            qY = hd.quadY(maxHeight, 130, 4, 10);
            fontRenderer.drawStringWithShadow(status, qX, qY, 0xFFFFFF);
        }
        if (GROUPS.getActivePlayIDs() != null && !GROUPS.getActivePlayIDs().isEmpty())
        {
            String status = "GROUPS.index:   " + GROUPS.getIndex(mc.player.getEntityId());
            statusWidth = fontRenderer.getStringWidth(status);
            qX = hd.quadX(maxWidth, 0, 4, statusWidth);
            qY = hd.quadY(maxHeight, 140, 4, 10);
            fontRenderer.drawStringWithShadow(status, qX, qY, 0xFFFFFF);
        }
        if (GROUPS.getGroupsMembers() != null && !GROUPS.getGroupsMembers().isEmpty())
        {
            String status = "GroupsMembers:  " + GROUPS.getGroupsMembers();
            statusWidth = fontRenderer.getStringWidth(status);
            qX = hd.quadX(maxWidth, 0, 4, statusWidth);
            qY = hd.quadY(maxHeight, 150, 4, 10);
            fontRenderer.drawStringWithShadow(status, qX, qY, 0xFFFFFF);
        }
        if (GROUPS.getGroupsMembers() != null && !GROUPS.getGroupsMembers().isEmpty())
        {
            String status = String.format("Group Distance: %-1.2f", GROUPS.getGroupMembersScaledDistance(mc.player));
            statusWidth = fontRenderer.getStringWidth(status);
            qX = hd.quadX(maxWidth, 0, 4, statusWidth);
            qY = hd.quadY(maxHeight, 160, 4, 10);
            fontRenderer.drawStringWithShadow(status, qX, qY, 0xFFFFFF);
        }
    }

    private String getMusicTitle(ItemStack stackIn)
    {
        String sheetMusicTitle = getMusicTitleRaw(stackIn);
        if (sheetMusicTitle.isEmpty())
            return INSTRUMENT_INVENTORY_EMPTY;
        else
            return sheetMusicTitle;
    }
    
    private static String getMusicTitleRaw(ItemStack sheetMusic)
    {
        if (!sheetMusic.isEmpty() && sheetMusic.getTagCompound() != null)
        {
            NBTTagCompound contents = (NBTTagCompound) sheetMusic.getTagCompound().getTag("SheetMusic");
            if (contents != null)
            {
                return sheetMusic.getDisplayName();
            }
        }
        return "";
    }

    private static int TITLE_DISPLAY_WIDTH = 30;
    private static int marqueePos = 0;
    private static int getMarqueePos() {return marqueePos;}

    private String marquee(String text, int window)
    {
        int marqueePos = getMarqueePos();
        if(text.length() <= window) return text;
        StringBuilder sb = new StringBuilder(text).append("   ");
        int head = marqueePos % sb.length();
        int tail = (head+window) % sb.length();
        if (head < tail)
            return sb.substring(head, tail);
        else
            return sb.toString().substring(head, sb.length()) + (sb.substring(0,tail));
    }
    
    @SuppressWarnings("unused")
    private String spaces(int length)
    {
        StringBuilder outputBuffer = new StringBuilder(length);
        for (int i = 0; i < length; i++){
            outputBuffer.append(" ");
        }
        return outputBuffer.toString();
    }
    
    private void renderHud(HudData hd, EntityPlayer playerIn, ItemStack sheetMusic)
    {
        int maxWidth = 256;
        int maxHeight = 128;
        float hudScale = inGuiHudAdjust() ? MusicOptionsUtil.getAdjustSizeHud() : MusicOptionsUtil.getSizeHud(playerIn);
        
        String musicTitle = getMusicTitle(sheetMusic);
        
        GL11.glPushMatrix();
        GL11.glTranslatef(hd.getPosX(), hd.getPosY(), 0F);
        GL11.glScalef(hudScale, hudScale, hudScale);

        int iconX = hd.quadX(maxWidth, 0, 2, WIDGET_WIDTH);
        int iconY = hd.quadY(maxHeight, 0, 2, WIDGET_HEIGHT);

        setTexture(TEXTURE_STATUS);
        drawWidget(playerIn, iconX, iconY, musicTitle);
//        drawDebug(hd, maxWidth, maxHeight);
        GL11.glPopMatrix();
    }
    
    @SuppressWarnings("static-access")
    private void renderMini(HudData hd, EntityPlayer playerIn)
    {

        GL11.glPushMatrix();
        GL11.glTranslatef(hd.getPosX(), hd.getPosY(), 0F);

        /* draw the status icon */
        int iconX = hd.quadX(PLACARD_ICON_SIZE, 0, 2, PLACARD_ICON_SIZE);
        int iconY = hd.quadY(PLACARD_ICON_SIZE, 0, 2, PLACARD_ICON_SIZE);
        int index = GROUPS.getIndex(playerIn.getEntityId());
        setTexture(TEXTURE_STATUS);
        this.drawTexturedModalRect(iconX, iconY, PLACARD_ICON_BASE_U_OFFSET + index %
                PLACARD_ICONS_PER_ROW * PLACARD_ICON_SIZE, PLACARD_ICON_BASE_V_OFFSET + index /
                PLACARD_ICONS_PER_ROW * PLACARD_ICON_SIZE, PLACARD_ICON_SIZE, PLACARD_ICON_SIZE);

        /* draw the group member distance bar */
        double dist = GROUPS.getGroupMembersScaledDistance(playerIn);
        if (dist > 0D)
        {
            Color color = new Color(127,255,0);
            int colorRGB = color.HSBtoRGB((float)((1.0D-dist)*0.5D), 1F, 1F);
            drawRect(iconX+4, iconY+20, iconX+4 + (int)(15*dist), iconY+22, colorRGB + (208 << 24));
        }
//        drawDebug(hd, maxWidth, maxHeight);

        GL11.glPopMatrix();
    }
    
    /**
     * This is not exactly the ideal way to store positional data for the notation symbols.
     * TODO: Think about a note factory that calculates the note position and symbols
     * based on play status and rules of the staff. LOL, seems like alot of work for a
     * status widget.
     *
     */
    private static Integer[][] notePosMembers = { {45,23},{140,31},{45,39},{140,47},{45,55},{140,63},{45,71},{140,79} };
    /**
     * Maps play status to a note symbol on a texture.
     */
    public enum NOTATION
    {
        MEMBER_REST(0, 96, 104),
        MEMBER_QUEUED(1, 144, 104),
        MEMBER_PLAY(2, 0, 104),
        E3(3, 0,104),
        E4(4, 0,104),
        E5(5, 0,104),
        E6(6, 0,104),
        E7(7, 0,104),
        LEADER_REST(8, 96, 104),
        LEADER_QUEUED(9, 144, 104),
        LEADER_PLAY(10,0,104);

        public int getMetadata() {return this.meta;}

        public static NOTATION byMetadata(int metaIn)
        {
            int metaLocal = metaIn;
            if (metaLocal < 0 || metaLocal >= META_LOOKUP.length) {metaLocal = 0;}
            return META_LOOKUP[metaLocal];
        }
        public int getX() {return this.x;}
        public int getY() {return this.y;}
        
        private final int meta;
        private final int x, y;
        private static final NOTATION[] META_LOOKUP = new NOTATION[values().length];

        NOTATION(int metaIn, int xIn, int yIn) {this.meta = metaIn; this.x=xIn; y=yIn;}

        static {for (NOTATION value : values()) {META_LOOKUP[value.getMetadata()] = value;}}
    }
    
    private void drawWidget(EntityPlayer playerIn, int posX, int posY, String musicTitle)
    {
        NOTATION eNOTATION;
        /* draw the widget background */
        drawTexturedModalRect(posX, posY, 0, 0, WIDGET_WIDTH, WIDGET_HEIGHT);
        
        /* alto clef */
        drawTexturedModalRect(posX +14+4, posY +23, 0, 136, 28, 64);
        
        /* draw the notes/rests for members */
        List<GroupData> groupData = processGroup(playerIn);
 
        if (GROUPS.getClientGroups() != null || GROUPS.getClientMembers() != null)
        {
            /* Only draw if player is a member of a group */
            if (GROUPS.getMembersGroupID(playerIn.getEntityId()) != null)
            {
                for (GroupData gd: groupData)
                {
                    int status = GROUPS.getIndex(gd.memberID);
                    eNOTATION = NOTATION.byMetadata(status);
                    int nX = eNOTATION.getX();
                    int nY = eNOTATION.getY();
                    int x = posX + gd.notePos.getFirst();
                    int y = posY + gd.notePos.getSecond();
                    drawTexturedModalRect(x, y, nX, nY, 24, 16);
                }
            }
            else
            {
                /* draw whole note/rest for the solo player */
                int status = GROUPS.getIndex(playerIn.getEntityId());
                eNOTATION = NOTATION.byMetadata(status);
                int nX = eNOTATION.getX();
                int nY = eNOTATION.getY();
                int x = posX + notePosMembers[0][0];
                int y = posY + notePosMembers[0][1];
                drawTexturedModalRect(x, y, nX, nY, 24, 16);
            }
        }
        
        /* draw the names solo player/members */
        if (GROUPS.getClientGroups() != null || GROUPS.getClientMembers() != null)
        {
            /* Only draw if player is a member of a group */
            if (GROUPS.getMembersGroupID(playerIn.getEntityId()) != null)
            {
                int oddEven = 1;
                for (GroupData gd: groupData)
                {
                    int x = posX + gd.notePos.getFirst() +28;
                    int y = posY + gd.notePos.getSecond() + 4;
                    int textWidth = fontRenderer.getStringWidth(TextFormatting.BOLD +marquee(gd.getMemberName(), 10));
                    if (oddEven++ % 2 == 0) drawRect(x-2, y-2, x+textWidth+2, y+10, 0xC89558 + (208 << 24));
                    fontRenderer.drawString(TextFormatting.BOLD + marquee(gd.getMemberName(), 10), x, y, 0x543722);
                }
            }
            else
            {
                /* draw the name of the solo player */
                GROUPS.getIndex(playerIn.getEntityId());
                int x = posX + notePosMembers[0][0] + 28;
                int y = posY + notePosMembers[0][1] + 4;
                String name = (playerIn.getDisplayName().getUnformattedText());
                fontRenderer.drawString(TextFormatting.BOLD + marquee(name, 10), x, y, 0x543722);
            }
        }
        
        /* draw the music title */
        int musicTitleWidth = fontRenderer.getStringWidth(TextFormatting.BOLD + marquee(musicTitle, TITLE_DISPLAY_WIDTH));
        int musicTitlePosX = posX + (WIDGET_WIDTH/2 - musicTitleWidth/2);
        int musicTitlePosY = posY + 10;
        fontRenderer.drawString(TextFormatting.BOLD + marquee(musicTitle, TITLE_DISPLAY_WIDTH), musicTitlePosX, musicTitlePosY, 0x543722);
        
        /* draw the group member distance bar */
        double dist = GROUPS.getGroupMembersScaledDistance(playerIn);
        if (dist > 0D)
        {
            Color color = new Color(127,255,0);
            @SuppressWarnings("static-access")
            int colorRGB = color.HSBtoRGB((float)((1.0D-dist)*0.5D), 1F, 1F);
            drawRect(posX +237, posY +24 + 62 - (int)(62*dist), posX +240, posY +87, colorRGB + (144 << 24));
        }
    }
    
    public void setTexture(ResourceLocation texture) { this.mc.renderEngine.bindTexture(texture);}
    
    /**
     * Copied from the vanilla Gui class, but removed the GlStateManager blend enable/disable method calls.
     */
    public static void drawRect(int leftIn, int topIn, int rightIn, int bottomIn, int color)
    {
        int left = leftIn;
        int top = topIn;
        int right = rightIn;
        int bottom = bottomIn;
        
        if (left < right)
        {
            int i = left;
            left = right;
            right = i;
        }

        if (top < bottom)
        {
            int j = top;
            top = bottom;
            bottom = j;
        }

        float f3 = (float)(color >> 24 & 255) / 255.0F;
        float f = (float)(color >> 16 & 255) / 255.0F;
        float f1 = (float)(color >> 8 & 255) / 255.0F;
        float f2 = (float)(color & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color(f, f1, f2, f3);
        bufferBuilder.begin(7, DefaultVertexFormats.POSITION);
        bufferBuilder.pos((double)left, (double)bottom, 0.0D).endVertex();
        bufferBuilder.pos((double)right, (double)bottom, 0.0D).endVertex();
        bufferBuilder.pos((double)right, (double)top, 0.0D).endVertex();
        bufferBuilder.pos((double)left, (double)top, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
    }
}
