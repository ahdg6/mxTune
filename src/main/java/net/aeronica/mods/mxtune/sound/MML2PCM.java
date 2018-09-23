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
package net.aeronica.mods.mxtune.sound;

import net.aeronica.libs.mml.core.MMLLexer;
import net.aeronica.libs.mml.core.MMLParser;
import net.aeronica.libs.mml.core.MMLToMIDI;
import net.aeronica.libs.mml.core.MMLUtil;
import net.aeronica.mods.mxtune.groups.GROUPS;
import net.aeronica.mods.mxtune.sound.ClientAudio.Status;
import net.aeronica.mods.mxtune.util.ModLogger;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Patch;
import javax.sound.sampled.AudioInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class MML2PCM
{
    private byte[] mmlBuf = null;
    private InputStream is;
    private Map<Integer, String> playerMML = new HashMap<Integer, String>();
    private Map<Integer, Integer> playerChannel = new HashMap<Integer, Integer>();

    /**
     * Solo play format "<playerName|groupID>=mml@...;"
     * 
     * Jam play format inserts with a space between each player=MML sequence
     * "<playername1>=MML@...abcd; <playername2>=MML@...efgh; <playername2>=MML@...efgh;"
     * 
     * @param playID
     * @param musicText
     * @return false if errors
     */
    public boolean process(int playID, String musicText)
    {
        this.playerMML = GROUPS.deserializeIntStrMap(musicText);
        if (playerMML == null)
        {
            ModLogger.error("MML2PCM playerMML is null! Check for an issue with NBT, networking, threads. PlayID", playID, musicText);
            ModLogger.error("MML2PCM PlayID: %d", playID);
            ModLogger.error("MML2PCM MML: %s", musicText.substring(0, musicText.length() >= 60 ? 60 : musicText.length()));
            ClientAudio.setPlayIDAudioDataStatus(playID, Status.ERROR);
            return false;
        }
        /**
         * Map players to channels and append all the players MML
         */
        Set<Integer> keys = playerMML.keySet();
        Iterator<Integer> it = keys.iterator();
        Integer ch = 0;
        StringBuilder mml = new StringBuilder();
        while (it.hasNext())
        {
            Integer playerID = it.next();
            playerChannel.put(playerID, ch);
            ch++;
            mml.append(playerMML.get(playerID));
        }
        try
        {
            mmlBuf = mml.toString().getBytes("US-ASCII");
        } catch (UnsupportedEncodingException e)
        {
            ModLogger.error(e);
            ClientAudio.setPlayIDAudioDataStatus(playID, Status.ERROR);
            return false;
        }
        is = new java.io.ByteArrayInputStream(mmlBuf);

        /** ANTLR4 MML Parser BEGIN */
        MMLToMIDI mmlTrans = new MMLToMIDI();
        ANTLRInputStream input = null;

        try
        {
            input = new ANTLRInputStream(is);
        } catch (IOException e)
        {
            ModLogger.error(e);
            ClientAudio.setPlayIDAudioDataStatus(playID, Status.ERROR);
            return false;
        }
        MMLLexer lexer = new MMLLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        MMLParser parser = new MMLParser(tokens);
        // parser.removeErrorListeners();
        // parser.addErrorListener(new UnderlineListener());
        parser.setBuildParseTree(true);
        ParseTree tree = parser.band();

        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(mmlTrans, tree);
        /** ANTLR4 MML Parser END */

        for (int packedPreset: mmlTrans.getPackedPresets())
        {
            Patch patchPreset = MMLUtil.packetPreset2Patch(packedPreset);
            ModLogger.info("MML2PCM preset: bank: %d, program %d", patchPreset.getBank(), patchPreset.getProgram());       
        }
      
        try
        {
            Midi2WavRenderer mw = new Midi2WavRenderer();
            AudioInputStream ais = mw.createPCMStream(mmlTrans.getSequence(), ClientAudio.getAudioFormat(playID));
            ClientAudio.setPlayIDAudioStream(playID, ais);
        } catch (MidiUnavailableException | InvalidMidiDataException | IOException e)
        {
            ModLogger.error(e);
            ClientAudio.setPlayIDAudioDataStatus(playID, Status.ERROR);
            return false;
        }
        ClientAudio.setPlayIDAudioDataStatus(playID, Status.READY);
        return true;
    }

}
