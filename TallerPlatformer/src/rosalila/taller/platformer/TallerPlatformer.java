package rosalila.taller.platformer;

import java.util.ArrayList;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import swarm.AndroidFunctionsInterface;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
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
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
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
		static float MAX_VELOCITY = 5f;
		static float JUMP_VELOCITY = 17f;
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
	
	final static float TILE_SIZE=64;

	private static TiledMap map;
	private static OrthogonalTiledMapRenderer renderer;
	private static OrthographicCamera camera;
//	private Texture koalaTexture;
//	private Animation stand;
//	private Animation walk;
//	private Animation jump;
	private Animation fly;
	Skin uiSkin;
	static private Koala koala;
	BitmapFont font;
	Label total_score_label;
	static Label score_label;
	Stage stage_game;
	Sprite intro;
	static int score=0;
	static int current_level=1;
	SpriteBatch batch;
	Sprite game_bg;
	Sprite menu_bg;
	Stage stage_menu;
	Stage stage_intro;
	Image continue_game_over;
	BestScoreAnimation best_score_animation;
	boolean touch_up_flag=true;
	boolean game_over=false;
	boolean tap_flag=false;
	static Sound coin_sound;
	static Sound jump_sound;
	static Sound hit_sound;
	static Sound select_sound;
	boolean key_up=true;
	LabelStyle label_syle;
	float color_animation=0;
	static boolean color_animation_activated=false;
	Image swarm_button;
	static float w,h;
	static Image game_intro;

	MuteButton mute_button;
	public static Music game_music;
	
	static String screen="intro";
	
	private final AndroidFunctionsInterface androidFunctions;
	
	ArrayList<Label>score_labels;
	
	Preferences prefs;
	private Pool<Rectangle> rectPool = new Pool<Rectangle>() {
		@Override
		protected Rectangle newObject () {
			return new Rectangle();
		}
	};
	private Array<Rectangle> tiles = new Array<Rectangle>();

	private static final float GRAVITY = -2f;

	
	public TallerPlatformer(AndroidFunctionsInterface desktopFunctions)
	{
		this.androidFunctions = desktopFunctions;
	}
	
	@Override
	public void create ()
	{
		androidFunctions.SwarmPreload();
		
		w = Gdx.graphics.getWidth();
		h = Gdx.graphics.getHeight();
		
		score = 0;
		current_level = 1;
		
		score_labels=new ArrayList<Label>();
		
		initPrefs();
		
		TextureRegion g01 = new TextureRegion(new Texture("guacamaya/01.png"));
		TextureRegion g02 = new TextureRegion(new Texture("guacamaya/02.png"));
		TextureRegion g03 = new TextureRegion(new Texture("guacamaya/03.png"));
		TextureRegion g04 = new TextureRegion(new Texture("guacamaya/04.png"));
		TextureRegion g05 = new TextureRegion(new Texture("guacamaya/05.png"));
		
		fly = new Animation(0.10f, g01, g02, g03, g04, g05);
		fly.setPlayMode(Animation.LOOP);

		// figure out the width and height of the koala for collision
		// detection and rendering by converting a koala frames pixel
		// size into world units (1 unit == 16 pixels)
		Koala.WIDTH = 1 / TILE_SIZE * g01.getRegionWidth();
		Koala.HEIGHT = 1 / TILE_SIZE * g01.getRegionHeight();
//		Koala.HEIGHT = 1 / 16f * regions[0].getRegionWidth();
//		Koala.WIDTH = 1 / 16f * regions[0].getRegionHeight();
		
		Texture intro_texture=new Texture("intro.png");
		intro_texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		intro = new Sprite(new TextureRegion(intro_texture,320,480));
		intro.setSize(w, h);
		
		//Text
		font = new BitmapFont(Gdx.files.internal("font/Fugaz.fnt"),false);
		uiSkin = new Skin();
		uiSkin.add("default", new BitmapFont());
		//Label style
		label_syle = new LabelStyle();
		label_syle.font = font;
		label_syle.fontColor = Color.WHITE;
		uiSkin.add("default", label_syle);
		score_label = new Label("Puntos: "+score,uiSkin);
		score_label.setColor(Color.BLACK);
		
		stage_game = new Stage();
		stage_game.addActor(score_label);
		
		continue_game_over = new Image(new Texture("continue.png"));
		continue_game_over.setY(continue_game_over.getY()+20);
		continue_game_over.setVisible(false);
		continue_game_over.addListener(new ClickListener(){
			@Override
			public void touchUp (InputEvent event, float x, float y, int pointer, int button)
			{
				super.touchUp(event, y, y, button, button);
				select_sound.play();
				if(getScore(current_level)<score)
				{
					color_animation_activated=true;
					setScore(current_level, score);
				}
				updateScores();
				continue_game_over.setVisible(false);
				best_score_animation.setVisible(false);
				game_over=false;
				screen="menu";
				Gdx.input.setInputProcessor(stage_menu);
			}
		});
		continue_game_over.setPosition(320/2-continue_game_over.getWidth()/2, 480/2-continue_game_over.getHeight()/2);
		stage_game.addActor(continue_game_over);
		
		best_score_animation = new BestScoreAnimation();
		best_score_animation.setVisible(false);
		stage_game.addActor(best_score_animation);
		
		game_intro = new Image(new Texture("game_intro.png"));
		game_intro.setVisible(false);
		stage_game.addActor(game_intro);
		
		Texture game_bg_texture=new Texture("game_bg.png");
		game_bg_texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		game_bg = new Sprite(new TextureRegion(game_bg_texture,320,480));
		game_bg.setSize(w, h);
		
		Texture menu_bg_texture=new Texture("menu_bg.png");
		menu_bg_texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		menu_bg = new Sprite(new TextureRegion(menu_bg_texture,320,480));
		menu_bg.setSize(w, h);
		
		stage_menu = new Stage();
		Gdx.input.setInputProcessor(stage_menu);
		
		int level_temp=1;
		int offset_x=20;
		int offset_y=10;
		int spacing_x=110;
		int spacing_y=90;
		int row_position_temp=spacing_y*3+50;
		for(int y=0;y<4;y++)
		{
			for(int x=0;x<3;x++)
			{
				Image button=getLevelButton(level_temp);
				button.setPosition(x*spacing_x+offset_x, row_position_temp+offset_y);
				stage_menu.addActor(button);
				
				Label label=new Label("",uiSkin);
				label.setColor(Color.BLACK);
				label.setPosition(x*spacing_x+offset_x, row_position_temp-10+offset_y);
				//label.setFontScale(0.7f);
				stage_menu.addActor(label);
				score_labels.add(label);
				
				level_temp++;
			}
			row_position_temp-=spacing_y;
		}
		total_score_label=new Label("", uiSkin);
		total_score_label.setY(total_score_label.getY()+15);
		stage_menu.addActor(total_score_label);
		
		swarm_button = new Image(new Texture (Gdx.files.internal("swarm.png")));
		swarm_button.addListener(new ClickListener(){
			@Override
			public void touchUp (InputEvent event, float x, float y, int pointer, int button)
			{
				super.touchUp(event, 0, 0, pointer, button);
				if(TallerPlatformer.screen.equals("menu"))
				{
					System.out.print("Boton de swarm presionado.");
					if(!androidFunctions.IsSwarmInitiated())
						androidFunctions.SwarmInitiate();
					
					androidFunctions.ShowLeaderboardSwarm();
//					while(!androidFunctions.IsSwarmInitiated());
				}
				
			}
		});
		
		stage_menu.addActor(swarm_button);
		
		stage_intro = new Stage();
		String pro_tips[] = {"El puntaje brilla cuando alcancanzas un nuevo score."
				,"Puedes habilitar Swarm para competir con tu puntaje en l]nea."
				,"Mientras tengas presionada la pantalla, la guacamaya seguir$ volando hacia arriba."
				};
		//$=á /=é ]=í @=ó %=ú
		Label pro_tip_label = new Label("Tip: "+pro_tips[(int)(Math.random()*1000)%pro_tips.length], uiSkin);
		pro_tip_label.setWidth(300);
		pro_tip_label.setWrap(true);
		pro_tip_label.setX(10);
		pro_tip_label.setY(pro_tip_label.getHeight()+10);
		pro_tip_label.setColor(Color.BLACK);
		stage_intro.addActor(pro_tip_label);
		
		batch = new SpriteBatch();
		
		// create the Koala we want to move around the world
		koala = new Koala();
		koala.position.set(0, 7);
		koala.velocity.y=0;
		
		game_music = Gdx.audio.newMusic(Gdx.files.internal("music.ogg"));
		game_music.play();
		game_music.setLooping(true);
		updateScores();
		
		coin_sound = Gdx.audio.newSound(Gdx.files.internal("sfx/coin.wav"));
		jump_sound = Gdx.audio.newSound(Gdx.files.internal("sfx/jump.wav"));
		hit_sound = Gdx.audio.newSound(Gdx.files.internal("sfx/hit.wav"));
		select_sound = Gdx.audio.newSound(Gdx.files.internal("sfx/select.wav"));
		
		mute_button = new MuteButton();
//		stage_game.addActor(mute_button);
		stage_menu.addActor(mute_button);
	}
	
	Image getLevelButton(int level)
	{
		Image button = new Image(new Texture("menu/button"+level+".png"));
		button.addListener(new MenuButtonListener(level,button));
		return button;
	}
	
	static void initLevel(int level)
	{
		game_intro.setVisible(true);
		color_animation_activated=false;
		score_label.setColor(Color.BLACK);
		score=0;
		score_label.setText("Puntos: "+score);
		
		koala.position.set(0, 7);
		koala.velocity.y=0;
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
		renderer = new OrthogonalTiledMapRenderer(map, 1 / TILE_SIZE);

		// create an orthographic camera, shows us 30x20 units of the world
		camera = new OrthographicCamera();
//		camera.setToOrtho(false, 30, 20);
		camera.setToOrtho(false, 10, 15);
		camera.update();
	}

	@Override
	public void render () {
		// clear the screen
		Gdx.gl.glClearColor(0.7f, 0.7f, 1.0f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		if(!androidFunctions.IsSwarmInitiated())
			swarm_button.setColor(0.7f,0.7f,0.7f,1f);
		else
			swarm_button.setColor(1f,1f,1f,1f);

		// get the delta time
		float deltaTime = Gdx.graphics.getDeltaTime();
		

		
		if(screen=="game")
		{
			//Render the bg
			batch.begin();
			game_bg.draw(batch);
			batch.end();
			
			renderGame(deltaTime);
			stage_game.draw();
			stage_game.act();
			
			///!!!!
			if(color_animation_activated)
			{
				score_label.setColor(color_animation,0,0,1);
				color_animation+=0.03;
				if(color_animation>1)
					color_animation=0;
			}else
			{
				total_score_label.setColor(Color.BLACK);
			}
		}
		
		if(screen=="intro")
		{
			logicIntro();
			renderIntro();
		}
		
		if(screen=="menu")
		{
			//Render the bg
			batch.begin();
			menu_bg.draw(batch);
			batch.end();
			
			stage_menu.draw();
			stage_menu.act();
			
			///!!!!
			if(color_animation_activated)
			{
				total_score_label.setColor(color_animation,0,0,1);
				color_animation+=0.03;
				if(color_animation>1)
					color_animation=0;
			}else
			{
				total_score_label.setColor(Color.BLACK);
			}
		}
	}
	
	void renderIntro()
	{
		batch.begin();
		intro.draw(batch);
		batch.end();
		stage_intro.draw();
	}
	
	void logicIntro()
	{		
		if((Gdx.input.isKeyPressed(Keys.SPACE) || isTouched(0.0f, 1))&& touch_up_flag)
		{
			touch_up_flag=false;
		}
		
		if( !isTouched(0.0f, 1))
		{
			if(!touch_up_flag)
				screen="menu";
			touch_up_flag=true;
		}
	}
	
	void gameOver()
	{
		if(!game_over)
		{
			hit_sound.play();
			Gdx.input.setInputProcessor(stage_game);
		}
			
		tap_flag=false;
		game_over=true;
		continue_game_over.setVisible(true);
		if(getScore(current_level)<score)
		{
			best_score_animation.setVisible(true);
		}
	}
	
	void renderGame(float deltaTime)
	{
		// update the koala (process input, collision detection, position update)
		updateKoala(deltaTime);

		// let the camera follow the koala, x-axis only
		camera.position.x = koala.position.x+4f;
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
		if(((Gdx.input.isKeyPressed(Keys.SPACE) || isTouched(0.0f, 1)) && !game_over) /*&& koala.grounded*/)
		{
			game_intro.setVisible(false);
			if(key_up)
				jump_sound.play();
			koala.velocity.y = Koala.JUMP_VELOCITY;
			koala.state = Koala.State.Jumping;
			koala.grounded = false;
			screen="game";
			key_up=false;
		}else
		{
			key_up=true;
		}
		
		koala.stateTime += deltaTime;
		
		if(isTouched(0.0f, 1))
		{
			tap_flag=true;
		}
		
		if(!game_over && tap_flag)
		{
			// check input and apply to velocity & state
			if((Gdx.input.isKeyPressed(Keys.SPACE) || isTouched(0.0f, 1)) /*&& koala.grounded*/) {
				koala.velocity.y = Koala.JUMP_VELOCITY;
				koala.state = Koala.State.Jumping;
				koala.grounded = false;
				screen="game";
			}

			koala.velocity.x = Koala.MAX_VELOCITY;
			if(koala.grounded) koala.state = Koala.State.Walking;
			koala.facesRight = true;

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
//		if(koala.velocity.x > 0) {
//			startX = endX = (int)(koala.position.x + Koala.WIDTH + koala.velocity.x);
//		} else {
//			startX = endX = (int)(koala.position.x + koala.velocity.x);
//		}
//		startY = (int)(koala.position.y);
//		endY = (int)(koala.position.y + Koala.HEIGHT);
		startX = (int)(koala.position.x + Koala.WIDTH/4);
		endX = (int)(koala.position.x + Koala.WIDTH - Koala.WIDTH/4);
		
		startY = (int)(koala.position.y + Koala.HEIGHT/4);
		endY = (int)(koala.position.y + Koala.HEIGHT - Koala.HEIGHT/4);
		
		getTiles(startX, startY, endX, endY, tiles,1);
//		koalaRect.x += koala.velocity.x;
		for(Rectangle tile: tiles) {
			if(koalaRect.overlaps(tile)) {
				koala.velocity.x = 0;
				gameOver();
				break;
			}
		}
//		koalaRect.x = koala.position.x;

		//Inicio cambio
		startX = (int)(koala.position.x + koala.velocity.x);
		endX = (int)(koala.position.x + Koala.WIDTH + koala.velocity.x);
		
		startY = (int)(koala.position.y + koala.velocity.y);
		endY = (int)(koala.position.y + Koala.HEIGHT + koala.velocity.y);
		
		getTiles(startX, startY, endX, endY, tiles,2);
		for(Rectangle tile: tiles) {
			if(koalaRect.overlaps(tile)) {
				coin_sound.play();
				TiledMapTileLayer layer = (TiledMapTileLayer)map.getLayers().get(2);
				layer.setCell((int)tile.x, (int)tile.y, null);
				score++;
				score_label.setText("Puntos: "+score);
				if(getScore(current_level)<score)
				{
					color_animation_activated=true;
				}
			}
		}
		//fin cambio
		rectPool.free(koalaRect);

		// unscale the velocity by the inverse delta time and set 
		// the latest position
		if(!game_over && tap_flag)
			koala.position.add(koala.velocity);
		koala.velocity.scl(1/deltaTime);

		// Apply damping to the velocity on the x-axis so we don't
		// walk infinitely once a key was pressed
		koala.velocity.x *= Koala.DAMPING;
		
		
		//No salirse
		if(koala.position.y<=1)
			gameOver();
		if(koala.position.y>13)
			koala.position.y=13;
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

		// draw the koala, depending on the current velocity
		// on the x-axis, draw the koala facing either right
		// or left
		Batch batch = renderer.getSpriteBatch();
		batch.begin();
		if(koala.facesRight) {
			batch.draw(frame, koala.position.x, koala.position.y,Koala.WIDTH/2, Koala.HEIGHT/2, Koala.WIDTH, Koala.HEIGHT,1,1,koala.velocity.y);
		} else {
			batch.draw(frame, koala.position.x + Koala.WIDTH, koala.position.y, -Koala.WIDTH, Koala.HEIGHT);
		}
		batch.end();
	}

	@Override
	public void dispose () {
	}
	
	public void resize(int width, int height) {
	    // TODO Auto-generated method stub
		this.w=width;
		this.h=height;
		stage_game.setViewport(320, 480, true);
		stage_menu.setViewport(320, 480, true);
		stage_intro.setViewport(320, 480, true);
//	    swarm_button.setPosition(this.w-swarm_button.getWidth()*screen_scale, 0);
		mute_button.setPosition(320-mute_button.getWidth(), 480-mute_button.getHeight());
		swarm_button.setPosition(320-swarm_button.getWidth(), 0);
    	continue_game_over.setPosition(320/2-continue_game_over.getWidth()/2, 480/2-continue_game_over.getHeight()/2);
    	best_score_animation.setPosition(320/2-best_score_animation.width/2, 480-best_score_animation.height);
    	game_intro.setPosition(320/2-game_intro.getWidth()/2, 480-game_intro.getHeight());
	    System.out.println(this.w);
	}
	
	void setScore(int level, int score)
	{
		prefs.putString(""+level, androidFunctions.encript(score,level));
		prefs.flush();
	}
	
	int getScore(int level)
	{
//		return 0;
		String level_str = ""+level;
		return androidFunctions.decript(prefs.getString(level_str, "0"),level);
	}
	
	void initPrefs()
	{
		prefs = Gdx.app.getPreferences("scores");
	}
	
	void updateScores()
	{
		int total_score=0;
		int i=1;
		for(Label l: score_labels)
		{
			int score_temp=getScore(i);
			l.setText(""+score_temp+"pts");
			i++;
			total_score+=score_temp;
		}
		total_score_label.setText("Puntaje total: "+total_score);
		if(androidFunctions.IsSwarmInitiated())
			androidFunctions.SubmitScore(total_score);
	}
}
