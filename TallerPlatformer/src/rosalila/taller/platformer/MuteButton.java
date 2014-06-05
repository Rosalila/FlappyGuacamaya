package rosalila.taller.platformer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class MuteButton extends Image
{
	boolean is_playing;
	public MuteButton()
	{
		super(new AnimationDrawable(1, 2, 64, 64, 0,"sound_sprite.png", 0.10f));
		((AnimationDrawable) this.getDrawable()).animateRow(0,true);
		this.setY(480-64);
		
		addListener(new ClickListener()
		{
			@Override
			public void touchUp (InputEvent event, float x, float y, int pointer, int button)
			{
				super.touchUp(event, y, y, button, button);
				if(is_playing)
				{
					((AnimationDrawable) getDrawable()).animateRow(1,true);
					TallerPlatformer.game_music.stop();
				}
				else
				{
					((AnimationDrawable) getDrawable()).animateRow(0,true);
					TallerPlatformer.game_music.play();
				}
				is_playing=!is_playing;
			}
		});
		is_playing=true;
	}
	
	@Override
	public void act(float delta)
	{
		if(isVisible())
		{
			((AnimationDrawable) this.getDrawable()).act(delta);
			super.act(delta);
		}
	}
}
