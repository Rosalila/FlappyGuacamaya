package rosalila.taller.platformer;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class MenuButtonListener extends InputListener
{
	int level;
	Image button_img;
	public MenuButtonListener(int level,Image button_img) {
		this.level=level;
		this.button_img=button_img;
	}
	
	@Override
	public boolean touchDown (InputEvent event, float x, float y, int pointer, int button)
	{
		if(TallerPlatformer.screen=="menu")
		{
			button_img.setColor(1f, 1f, 1f, 0.5f);
		}
		return true;
	}
	
	@Override
	public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
		if(TallerPlatformer.screen=="menu")
		{
			button_img.setColor(1f, 1f, 1f, 1f);
			TallerPlatformer.initLevel(level);
			TallerPlatformer.current_level=level;
			TallerPlatformer.screen="game";
		}
	}
}
