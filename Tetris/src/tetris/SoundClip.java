package tetris;

/**
 * Class SoundClip
 * @authors Brendan Jones, arrangements by: luisfelipesv y melytc
 *
 * Luis Felipe Salazar A00817158 Melissa Janet Treviño A00816715
 *
 * 1/MAR/16
 * @version 2.0
 *
 * The {@code SoundClip} class is responsible the use of audio files.
 */
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioSystem;
import java.io.IOException;
import java.net.URL;

public class SoundClip {

    // Input stream with a specified audio format and length.
    private AudioInputStream aiSample;
    
    // Clip object with the audio. 
    private Clip cClip;
    
    // Boolean to work with loops in the audio.
    private boolean bLooping = false;
    
    // Intefer with the number of repeats done of the audio.
    private int iRepeat = 0;
    
    // String with the name of the audio file.
    private String sFilename = "";
    
    // Long number with the actual time of reproduction of the audio.
    private long lClipTime = 0;

    /**
     * Default constructor of class SoundClip.
     */
    public SoundClip() {
        try {
            // Creates the Buffer with the sound.
            cClip = AudioSystem.getClip();
        } catch (LineUnavailableException excLine) {
        }
    }

    /**
     * Constructor with parameters. Calls 'load' function to load the sound
     * file.
     *
     * @param sFilename is the <code>String</code> with the name of the file.
     */
    public SoundClip(String sFilename) {
        // Calls the default contructor.
        this();

        // Loads the sound file.
        load(sFilename);
    }

    /**
     * Access method that returns a type Clip object.
     *
     * @return clip is a <code>Clip object</code>.
     */
    public Clip getClip() {
        return cClip;
    }

    /**
     * Modifier method used to modify if the sound will be repeated.
     *
     * @param bLooping is a <code>boolean</code> value.
     */
    public void setLooping(boolean bLooping) {
        this.bLooping = bLooping;
    }

    /**
     * Access method that returns a boolean to know if the sound is repeated.
     *
     * @return bLooping is a <code>boolean</code> value.
     */
    public boolean getLooping() {
        return bLooping;
    }

    /**
     * Modifier method used to define the number of repetitions.
     *
     * @param iRepeat is an <code>integer</code> that represents the number of
     * repetitions.
     */
    public void setRepeat(int iRepeat) {
        this.iRepeat = iRepeat;
    }

    /**
     * Access method that returns the number of repetitions.
     *
     * @return iRepeat is an <code>integer</code> value that represents the
     * number of repetitions.
     */
    public int getRepeat() {
        return iRepeat;
    }

    /**
     * Modifier method that assigns the name of the file.
     *
     * @param sFilename is a <code>String</code> with file name.
     */
    public void setFilename(String sFilename) {
        this.sFilename = sFilename;
    }

    /**
     * Access method that returns the name of the file.
     *
     * @return sFilename is a <code>String</code> with file name.
     */
    public String getFilename() {
        return sFilename;
    }

    /**
     * Method that verifies if the audio file is loaded.
     *
     * @return aiSample is a<code>sample object</code>.
     */
    public boolean isLoaded() {
        return (boolean) (aiSample != null);
    }

    /**
     * Access method that returns the URL of the file.
     *
     * @param sFilename is a <code>String</code> with the file name.
     */
    private URL getURL(String sFilename) {
        URL url = null;

        try {
            url = this.getClass().getResource(sFilename);
        } catch (Exception exc) {
        }

        return url;
    }

    /**
     * Method that loads the audio file.
     *
     * @param sAudiofile is a <code>String</code> with the file name of the
     * audio.
     *
     * @return a <code>boolean</code> value.
     */
    public boolean load(String sAudiofile) {
        try {
            setFilename(sAudiofile);
            aiSample = AudioSystem.getAudioInputStream(getURL(sFilename));
            cClip.open(aiSample);
            return true;

        } catch (IOException e) {
            return false;
        } catch (UnsupportedAudioFileException e) {
            return false;
        } catch (LineUnavailableException e) {
            return false;
        }
    }

    /**
     * Method that reproduces the sound.
     */
    public void play() {
        // Return with no actions if the sound is not loaded.
        if (!isLoaded()) {
            return;
        }
        // Starts playing from the start of the file.
        cClip.setFramePosition(0);

        // Play with the looping option.
        if (bLooping) {
            cClip.loop(Clip.LOOP_CONTINUOUSLY);
        } else {
            cClip.loop(iRepeat);
        }
    }

    /**
     * Method that stops the audio.
     */
    public void stop() {
        cClip.stop();
        
        // Set the clip time in 0, so it starts again if needed.
        lClipTime = 0;
    }

    /**
     * Method that pauses the audio.
     */
    public void pause() {
        // Get the actual position and save it for later.
        lClipTime = cClip.getMicrosecondPosition();
        cClip.stop();

    }

    /**
     * Method that plays again the audio from where it left.
     */
    public void unpause() {
        // Play from where it left the last time.
        cClip.setMicrosecondPosition(lClipTime);

        // Play with the looping option.
        if (bLooping) {
            cClip.loop(Clip.LOOP_CONTINUOUSLY);
        } else {
            cClip.loop(iRepeat);
        }
    }
}
