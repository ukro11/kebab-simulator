package kebab_simulator.model.sound;

public class SoundConstants {

    public final SoundSource SOUND_BACKGROUND;
    public final SoundSource[] SOUND_FRYING = new SoundSource[2];
    public final SoundSource[] SOUND_CUTTING = new SoundSource[2];
    public final SoundSource SOUND_PICKUP;

    public SoundConstants() {
        this.SOUND_BACKGROUND = new SoundSource("background", "background.wav");

        this.SOUND_FRYING[0] = new SoundSource("frying", "frying.wav");
        this.SOUND_FRYING[1] = new SoundSource("frying", "frying.wav");

        this.SOUND_CUTTING[0] = new SoundSource("cutting", "cutting.wav");
        this.SOUND_CUTTING[1] = new SoundSource("cutting", "cutting.wav");

        this.SOUND_PICKUP = new SoundSource("pick-up", "plop.wav");
        this.SOUND_PICKUP.setVolume(0.8);
    }
}
