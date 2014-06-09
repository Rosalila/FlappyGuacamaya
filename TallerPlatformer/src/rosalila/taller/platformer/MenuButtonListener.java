package rosalila.taller.platformer;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class MenuButtonListener extends InputListener
{
	int level;
	Image button_img;
	static int selected_level;
	public MenuButtonListener(int level,Image button_img) {
		this.level=level;
		this.button_img=button_img;
	}
	
	@Override
	public boolean touchDown (InputEvent event, float x, float y, int pointer, int button)
	{
		super.touchDown(event, x, y, pointer, button);
		if(TallerPlatformer.screen=="menu")
		{
			MenuButtonListener.selected_level = level;
			TallerPlatformer.select_sound.play();
			button_img.setColor(1f, 1f, 1f, 0.5f);
		}
		return true;
	}
	
	@Override
	public void touchUp (InputEvent event, float x, float y, int pointer, int button)
	{
		super.touchUp(event, x, y, pointer, button);
		if(TallerPlatformer.screen=="menu")
		{
			if(MenuButtonListener.selected_level == level)
			{
				button_img.setColor(1f, 1f, 1f, 1f);
				TallerPlatformer.initLevel(level);
				TallerPlatformer.current_level=level;
				TallerPlatformer.screen="game";
			}else
			{
				button_img.setColor(1f, 1f, 1f, 1f);
			}
		}
	}
}
