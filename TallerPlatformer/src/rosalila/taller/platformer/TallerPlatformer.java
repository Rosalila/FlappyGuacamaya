package rosalila.taller.platformer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.tests.utils.GdxTest;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class TallerPlatformer extends GdxTest {
	/**
	 * The player character, has state and state time, 
	 */
	static class Koala
	{
		static float WIDTH;
		static float HEIGHT;
		static float MAX_VELOCITY = 10f;
		static float JUMP_VELOCITY = 30f;
		static float DAMPING = 0.87f;

		enum State {
			Standing,
			Walking,
			Jumping
		}

		final Vector2 position = new Vector2();
		final Vector2 velocity = new Vector2();
		State state = State.Walking;
		float stateTime = 0;
		boolean facesRight = true;
		boolean grounded = false;
	}

	private TiledMap map;
	private OrthogonalTiledMapRenderer renderer;
	private OrthographicCamera camera;
//	private Texture koalaTexture;
//	private Animation stand;
//	private Animation walk;
//	private Animation jump;
	private Animation fly;
	private Koala koala;
	BitmapFont font;
	Label score_label;
	Stage stage;
	Sprite intro;
	static int score; 
	SpriteBatch batch;
	
	String screen="intro";
	private Pool<Rectangle> rectPool = new Pool<Rectangle>() {
		@Override
		protected Rectangle newObject () {
			return new Rectangle();
		}
	};
	private Array<Rectangle> tiles = new Array<Rectangle>();

	private static final float GRAVITY = -2.5f;

	@Override
	public void create ()
	{
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		score = 0;
//		// load the koala frames, split them, and assign them to Animations
//		koalaTexture = new Texture("koalio.png");
//		koalaTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
//		//koalaTexture.setFilter(minFilter, magFilter)
//		TextureRegion[] regions = TextureRegion.split(koalaTexture, 18, 26)[0];
//		stand = new Animation(0, regions[0]);
//		jump = new Animation(0, regions[1]);
//		walk = new Animation(0.15f, regions[2], regions[3], regions[4]);
//		walk.setPlayMode(Animation.LOOP_PINGPONG);
		
		TextureRegion g01 = new TextureRegion(new Texture("guacamaya/01.png"));
		TextureRegion g02 = new TextureRegion(new Texture("guacamaya/02.png"));
		TextureRegion g03 = new TextureRegion(new Texture("guacamaya/03.png"));
		TextureRegion g04 = new TextureRegion(new Texture("guacamaya/04.png"));
		TextureRegion g05 = new TextureRegion(new Texture("guacamaya/05.png"));
		
		fly = new Animation(0.15f, g01, g02, g03, g04, g05);
		fly.setPlayMode(Animation.LOOP_PINGPONG);

		// figure out the width and height of the koala for collision
		// detection and rendering by converting a koala frames pixel
		// size into world units (1 unit == 16 pixels)
		Koala.WIDTH = 1 / 16f * g01.getRegionWidth();
		Koala.HEIGHT = 1 / 16f * g01.getRegionHeight();
//		Koala.HEIGHT = 1 / 16f * regions[0].getRegionWidth();
//		Koala.WIDTH = 1 / 16f * regions[0].getRegionHeight();
		
		Texture intro_texture=new Texture("intro.png");
		intro_texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		intro = new Sprite(new TextureRegion(intro_texture,320,480));
		intro.setSize(w, h);
		
		//Text
		font = new BitmapFont(Gdx.files.internal("data/default.fnt"),false);
		Skin uiSkin;
		uiSkin = new Skin();
		uiSkin.add("default", new BitmapFont());
		//Label style
		LabelStyle label_syle = new LabelStyle();
		label_syle.font = font;
		label_syle.fontColor = Color.WHITE;
		uiSkin.add("default", label_syle);
		score_label = new Label("Score: "+score,uiSkin);
		
		stage = new Stage();
		stage.addActor(score_label);
		
		initLevel(1);
		
		batch = new SpriteBatch();
		
		// create the Koala we want to move around the world
		koala = new Koala();
		koala.position.set(20, 15);
		
		Music oggMusic = Gdx.audio.newMusic(Gdx.files.internal("music.ogg"));
		oggMusic.play();
	}
	
	void initLevel(int level)
	{
		if(map!=null)
		{
			map.dispose();
		}
		if(renderer!=null)
		{
			renderer.dispose();
		}
//		if(camera!=null)
//		{
//			camera.
//		}
		// load the map, set the unit scale to 1/16 (1 unit == 16 pixels)
		map = new TmxMapLoader().load("level"+level+".tmx");
		renderer = new OrthogonalTiledMapRenderer(map, 1 / 16f);

		// create an orthographic camera, shows us 30x20 units of the world
		camera = new OrthographicCamera();
//		camera.setToOrtho(false, 30, 20);
		camera.setToOrtho(false, 20, 30);
		camera.update();
	}

	@Override
	public void render () {
		// clear the screen
		Gdx.gl.glClearColor(0.7f, 0.7f, 1.0f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		// get the delta time
		float deltaTime = Gdx.graphics.getDeltaTime();
		
		if(screen=="game")
		{
			renderGame(deltaTime);
		}
		
		if(screen=="intro")
		{
			logicIntro();
			renderIntro();
		}
		
		stage.draw();
	}
	
	void renderIntro()
	{
		batch.begin();
		intro.draw(batch);
		batch.end();
	}
	
	void logicIntro()
	{
		if((Gdx.input.isKeyPressed(Keys.SPACE) || isTouched(0.0f, 1))) {
			screen="game";
			score=0;
			score_label.setText("Score: "+score);
		}
	}
	
	void gameOver()
	{
		screen="intro";
		koala.position.set(20, 15);
		initLevel(1);
	}
	
	void renderGame(float deltaTime)
	{
		// update the koala (process input, collision detection, position update)
		updateKoala(deltaTime);

		// let the camera follow the koala, x-axis only
		camera.position.x = koala.position.x+9f;
		camera.update();

		// set the tile map rendere view based on what the
		// camera sees and render the map
		renderer.setView(camera);
		renderer.render();

		// render the koala
		renderKoala(deltaTime);	
	}

	private Vector2 tmp = new Vector2();
	private void updateKoala(float deltaTime) {
		if(deltaTime == 0) return;
		koala.stateTime += deltaTime;	

		// check input and apply to velocity & state
		if((Gdx.input.isKeyPressed(Keys.SPACE) || isTouched(0.0f, 1)) /*&& koala.grounded*/) {
			koala.velocity.y = Koala.JUMP_VELOCITY;
			koala.state = Koala.State.Jumping;
			koala.grounded = false;
			screen="game";
		}

//		if(false || Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.A) || isTouched(0, 0.25f)) {
//			koala.velocity.x = -Koala.MAX_VELOCITY;
//			if(koala.grounded) koala.state = Koala.State.Walking;
//			koala.facesRight = false;
//		}

//		if(true || Gdx.input.isKeyPressed(Keys.RIGHT) || Gdx.input.isKeyPressed(Keys.D) || isTouched(0.25f, 0.5f)) {
			koala.velocity.x = Koala.MAX_VELOCITY;
			if(koala.grounded) koala.state = Koala.State.Walking;
			koala.facesRight = true;
//		}

		// apply gravity if we are falling
		koala.velocity.add(0, GRAVITY);

		// clamp the velocity to the maximum, x-axis only
		if(Math.abs(koala.velocity.x) > Koala.MAX_VELOCITY) {
			koala.velocity.x = Math.signum(koala.velocity.x) * Koala.MAX_VELOCITY;
		}

		// clamp the velocity to 0 if it's < 1, and set the state to standign
		if(Math.abs(koala.velocity.x) < 1) {
			koala.velocity.x = 0;
			if(koala.grounded) koala.state = Koala.State.Standing;
		}

		// multiply by delta time so we know how far we go
		// in this frame
		koala.velocity.scl(deltaTime);

		// perform collision detection & response, on each axis, separately
		// if the koala is moving right, check the tiles to the right of it's
		// right bounding box edge, otherwise check the ones to the left
		Rectangle koalaRect = rectPool.obtain();
		koalaRect.set(koala.position.x, koala.position.y, Koala.WIDTH, Koala.HEIGHT);
		int startX, startY, endX, endY;
		if(koala.velocity.x > 0) {
			startX = endX = (int)(koala.position.x + Koala.WIDTH + koala.velocity.x);
		} else {
			startX = endX = (int)(koala.position.x + koala.velocity.x);
		}
		startY = (int)(koala.position.y);
		endY = (int)(koala.position.y + Koala.HEIGHT);
		getTiles(startX, startY, endX, endY, tiles,1);
		koalaRect.x += koala.velocity.x;
		for(Rectangle tile: tiles) {
			if(koalaRect.overlaps(tile)) {
				koala.velocity.x = 0;
				gameOver();
				break;
			}
		}
		koalaRect.x = koala.position.x;

		// if the koala is moving upwards, check the tiles to the top of it's
		// top bounding box edge, otherwise check the ones to the bottom
		if(koala.velocity.y > 0) {
			startY = endY = (int)(koala.position.y + Koala.HEIGHT + koala.velocity.y);
		} else {
			startY = endY = (int)(koala.position.y + koala.velocity.y);
		}
		startX = (int)(koala.position.x);
		endX = (int)(koala.position.x + Koala.WIDTH);
		getTiles(startX, startY, endX, endY, tiles,1);
		koalaRect.y += koala.velocity.y;
		for(Rectangle tile: tiles) {
			if(koalaRect.overlaps(tile)) {
				// we actually reset the koala y-position here
				// so it is just below/above the tile we collided with
				// this removes bouncing :)
				if(koala.velocity.y > 0) {
					koala.position.y = tile.y - Koala.HEIGHT;
					// we hit a block jumping upwards, let's destroy it!
					TiledMapTileLayer layer = (TiledMapTileLayer)map.getLayers().get(1);
//					layer.setCell((int)tile.x, (int)tile.y, null);
				} else {
					koala.position.y = tile.y + tile.height;
					// if we hit the ground, mark us as grounded so we can jump
					koala.grounded = true;
				}
				koala.velocity.y = 0;
				break;
			}
		}
		//Inicio cambio
		getTiles(startX, startY, endX, endY, tiles,2);
		for(Rectangle tile: tiles) {
			if(koalaRect.overlaps(tile)) {
				TiledMapTileLayer layer = (TiledMapTileLayer)map.getLayers().get(2);
				layer.setCell((int)tile.x, (int)tile.y, null);
				score++;
				score_label.setText("Score: "+score);
			}
		}
		//fin cambio
		rectPool.free(koalaRect);

		// unscale the velocity by the inverse delta time and set 
		// the latest position
		koala.position.add(koala.velocity);
		koala.velocity.scl(1/deltaTime);

		// Apply damping to the velocity on the x-axis so we don't
		// walk infinitely once a key was pressed
		koala.velocity.x *= Koala.DAMPING;
		
		
		//No salirse
		if(koala.position.y<0)
			koala.position.y=0;
		if(koala.position.y>29)
			koala.position.y=29;
	}

	private boolean isTouched(float startX, float endX) {
		// check if any finge is touch the area between startX and endX
		// startX/endX are given between 0 (left edge of the screen) and 1 (right edge of the screen)
		for(int i = 0; i < 2; i++) {
			float x = Gdx.input.getX() / (float)Gdx.graphics.getWidth();
			if(Gdx.input.isTouched(i) && (x >= startX && x <= endX)) {
				return true;
			}
		}
		return false;
	}

	private void getTiles(int startX, int startY, int endX, int endY, Array<Rectangle> tiles, int num_layer) {
		TiledMapTileLayer layer = (TiledMapTileLayer)map.getLayers().get(num_layer);
		rectPool.freeAll(tiles);
		tiles.clear();
		for(int y = startY; y <= endY; y++) {
			for(int x = startX; x <= endX; x++) {
				Cell cell = layer.getCell(x, y);
				if(cell != null) {
					Rectangle rect = rectPool.obtain();
					rect.set(x, y, 1, 1);
					tiles.add(rect);
				}
			}
		}
	}

	private void renderKoala(float deltaTime) {
		// based on the koala state, get the animation frame
		TextureRegion frame = null;
		frame = fly.getKeyFrame(koala.stateTime);
//		switch(koala.state) {
//			case Standing: frame = stand.getKeyFrame(koala.stateTime); break;
//			case Walking: frame = walk.getKeyFrame(koala.stateTime); break;
//			case Jumping: frame = jump.getKeyFrame(koala.stateTime); break; 
//		}

		// draw the koala, depending on the current velocity
		// on the x-axis, draw the koala facing either right
		// or left
		Batch batch = renderer.getSpriteBatch();
		batch.begin();
		if(koala.facesRight) {
			batch.draw(frame, koala.position.x, koala.position.y,0,0, Koala.WIDTH, Koala.HEIGHT,1,1,koala.velocity.y);
		} else {
			batch.draw(frame, koala.position.x + Koala.WIDTH, koala.position.y, -Koala.WIDTH, Koala.HEIGHT);
		}
		batch.end();
	}

	@Override
	public void dispose () {
	}
}
