package rosalila.taller.platformer;

import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class BestScoreAnimation extends Image
{
	public BestScoreAnimation() {
		super(new AnimationDrawable(4, 1, 256, 128, 0,"best_score.png", 0.10f));
		((AnimationDrawable) this.getDrawable()).animateRow(0,true);
	}
	
	@Override
	public void act(float delta)
	{
//		if(isVisible())
//		{
			((AnimationDrawable) this.getDrawable()).act(delta);
			super.act(delta);
//		}
	}
}
