package rosalila.taller.platformer;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "TallerPlatformer";
		cfg.useGL20 = false;
//		cfg.width = 480;
//		cfg.height = 320;
		cfg.width = 320;
		cfg.height = 480;
		
		new LwjglApplication(new TallerPlatformer(), cfg);
	}
}
