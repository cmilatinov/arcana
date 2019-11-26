import com.arcana.audio.AudioLibrary;
import com.arcana.audio.AudioPlayer;
import com.arcana.audio.AudioSample;

public class Test {
	
	public static void main(String[] args){
		AudioLibrary.setMasterVolume(0.5f);
		AudioSample sample = AudioLibrary.createSound("./sound/swag.wav");
		sample.setVolume(0.7f);
		AudioSample sample2 = AudioLibrary.createSound("./sound/swag2.wav");
		AudioPlayer p = AudioLibrary.playSound(sample);
		AudioPlayer p2 = AudioLibrary.playSound(sample2);
		try {
			Thread.sleep(10000);
			p.stopPlayback();
			Thread.sleep(1000);
			p2.stopPlayback();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
