package rosalila.taller.platformer;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

public class MenuButtonListener extends InputListener
{
	int level;
	public MenuButtonListener(int level) {
		this.level=level;
	}
	
	@Override
	public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
		if(TallerPlatformer.screen=="menu")
		{
			TallerPlatformer.initLevel(level);
			TallerPlatformer.current_level=level;
			TallerPlatformer.screen="game";
		}
		
		return true;
	}
}
