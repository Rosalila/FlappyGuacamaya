package rosalila.taller.platformer;

import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class BestScoreAnimation extends Image
{
	int width,height;
	public BestScoreAnimation() {
		super(new AnimationDrawable(4, 1, 256, 128, 0,"best_score.png", 0.12f));
		this.width=256;
		this.height=128;
		setPosition(TallerPlatformer.w/2-width/2, TallerPlatformer.h-height);
		((AnimationDrawable) this.getDrawable()).animateRow(0,true);
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
