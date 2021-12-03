package com.pokemon.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.ScreenUtils;
import com.pokemon.controller.GameController;
import com.pokemon.controller.PlayerController;
import com.pokemon.db.db;
import com.pokemon.game.Pokemon;
import com.pokemon.model.Player;
import com.pokemon.ui.AbstractUi;
//import com.pokemon.ui.inventory.InventoryRenderer;
//import com.pokemon.ui.inventory.InventoryUI;
import com.pokemon.ui.SkillListUi;
import com.pokemon.ui.inventory.InventoryUi;
import com.pokemon.transition.*;
import com.pokemon.ui.AbstractUi;
import com.pokemon.util.Action;
import com.pokemon.util.AnimationSet;
import com.pokemon.util.SkinGenerator;
import com.pokemon.world.World;
import com.pokemon.world.Home;
import com.pokemon.world.MainWorld;
import com.pokemon.ui.pokemonBox.myPokemonUI;
import com.pokemon.world.World;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;

import java.util.Stack;

import static com.pokemon.game.Settings.SCALED_TILE_SIZE;

public class GameScreen implements Screen {
    final Pokemon game;
    private static World world;
    private Skin skin;

    private static TweenManager tweenManager;
    private static AssetManager assetManager;
    private ShaderProgram transitionShader;
    private OrthographicCamera camera;
    public Player player;
    private PlayerController playerController;
    private WorldRenderer worldRenderer;
    private GameController gameController;
    // private InventoryRenderer inventoryRenderer;
    private Stack<AbstractUi> uiStack;
    Stage stage;
    private boolean invenCheck=false;
    private boolean skillCheck=false;
    private TransitionScreen transitionScreen;
    private boolean isTransition;
    private boolean pokemonBoxCheck;

    public GameScreen(Pokemon game) {
        this.game = game;
        assetManager = new AssetManager();
        assetManager.load("players/players.atlas", TextureAtlas.class);
        assetManager.load("transitions/white.png", Texture.class);
        for (int i = 0; i < 13; i++) {
            assetManager.load("transitions/transition_"+i+".png", Texture.class);
        }
        assetManager.load("texture/texture.atlas", TextureAtlas.class);
        assetManager.finishLoading();

        tweenManager = new TweenManager();
        Tween.registerAccessor(BattleBlinkTransition.class, new BattleBlinkTransitionAccessor());

        transitionShader = new ShaderProgram(
                Gdx.files.internal("transitions/vertexshader.txt"),
                Gdx.files.internal("transitions/fragmentshader.txt"));
        if (!transitionShader.isCompiled()) {
            System.out.println(transitionShader.getLog());
        }
        isTransition = false;


        TextureAtlas playerTexture = assetManager.get("players/players.atlas", TextureAtlas.class);
        //TextureAtlas Texture = assetManager.get("texture/texture.atlas", TextureAtlas.class);

        skin = SkinGenerator.generateSkin(assetManager);

        AnimationSet<TextureRegion> animations = new AnimationSet<>(
                new Animation<TextureRegion>(0.3f / 2f, playerTexture.findRegions("dawn_walk_north"), Animation.PlayMode.LOOP_PINGPONG),
                new Animation<TextureRegion>(0.3f / 2f, playerTexture.findRegions("dawn_walk_south"), Animation.PlayMode.LOOP_PINGPONG),
                new Animation<TextureRegion>(0.3f / 2f, playerTexture.findRegions("dawn_walk_east"), Animation.PlayMode.LOOP_PINGPONG),
                new Animation<TextureRegion>(0.3f / 2f, playerTexture.findRegions("dawn_walk_west"), Animation.PlayMode.LOOP_PINGPONG),
                playerTexture.findRegion("dawn_stand_north"),
                playerTexture.findRegion("dawn_stand_south"),
                playerTexture.findRegion("dawn_stand_east"),
                playerTexture.findRegion("dawn_stand_west")
        );

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
        player = new Player(2*SCALED_TILE_SIZE, 3*SCALED_TILE_SIZE, animations);
        world = new Home(player,game,this);
        worldRenderer = new WorldRenderer(player);
        playerController = new PlayerController(player);
        gameController = new GameController(game);
        transitionScreen = new TransitionScreen(game,this);
        uiStack = new Stack<>();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        camera.position.x = player.x;
        camera.position.y = player.y;
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        worldRenderer.render(game.batch);
        game.batch.end();
        gameController.update();
        player.update(delta);
        world.update();

        if (uiStack.isEmpty() && !isTransition) {
            playerController.update();
        } else {
            for (AbstractUi abstractUi : uiStack) {
                abstractUi.update();
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                popUi();
            }
        }

        // 맵 페이드 아웃
        if (Gdx.input.isKeyPressed(Input.Keys.F1)) {
            transitionScreen.startTransition(
                    new FadeOutTransition(0.8f, Color.BLACK, getTweenManager(), getAssetManager()),
                    new FadeInTransition(0.8f,  Color.BLACK, getTweenManager(), getAssetManager()),
                    new Action() {
                        @Override
                        public void action() {
                            player.setX(13);
                            player.setY(6);
                        }
                    });
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.F3)){
            pokemonBoxCheck = (!pokemonBoxCheck);
            if(pokemonBoxCheck) {
                this.pushUi(new myPokemonUI(this, game,player));
            }else {
                AbstractUi popped = uiStack.pop();
                popped.dispose();
            }
        }

//        // 전투 페이드 아웃
//        if (Gdx.input.isKeyPressed(Input.Keys.F2)) {
//            transitionScreen.startTransition(
//                    this,
//                    this,
//                    new BattleBlinkTransition(4f, 4 , Color.GRAY, getTransitionShader(), getTweenManager(), getAssetManager()),
//                    new BattleTransition(1F,  10, true, getTransitionShader(), getTweenManager(), getAssetManager())
//            );
////                    new Action() {
////                        @Override
////                        public void action() {
////                           // game.setScreen(new BattleScreen(game));
////                        }
////                    });
//        }
        this.update(delta);
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    public void update(float delta){

        if (Gdx.input.isKeyJustPressed(Input.Keys.F9)){

            if (uiStack.isEmpty()) {
                pushUi(new InventoryUi(this, game, player));
            } else {
                popUi();
            }

//            invenCheck = (!invenCheck);
//            if(invenCheck) {
//                //아이템 추가
//                db.UPDATE("ITEM_01",1);
//                player.inventory.addItem("ITEM_01",1);
//                this.pushUi(new InventoryUi(this, game,player));
//            }else {
//                AbstractUi popped = uiStack.pop();
//                popped.dispose();
//            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.K)){
            if (uiStack.isEmpty()) {
                pushUi(new SkillListUi(this, game,player));
            } else {
                popUi();
            }

//            skillCheck = (!skillCheck);
//            if(skillCheck) {
//                this.pushUi(new SkillListUi(this, game,player));
//            }else {
//                AbstractUi popped = uiStack.pop();
//                popped.dispose();
//            }
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.B)){
            game.setScreen(new BattleScreen(game,player));
            dispose();
        }
    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        assetManager.dispose();
    }

    public static World getWorld() {
        return world;
    }

    public static void setWorld(World world) {
        GameScreen.world = world;
    }

    public static AssetManager getAssetManager() {
        return assetManager;
    }

    public static TweenManager getTweenManager() {
        return tweenManager;
    }
    public ShaderProgram getTransitionShader() {
        return transitionShader;
    }

    public TransitionScreen getTransitionScreen() {
        return transitionScreen;
    }

    public void setTransition(boolean transition) {
        isTransition = transition;
    }

    public Stack<AbstractUi> getUiStack() {
        return uiStack;
    }

    public void pushUi(AbstractUi ui) {
        uiStack.add(ui);
    }
    public void popUi() {
        if (!uiStack.isEmpty()) {
            AbstractUi popped = uiStack.pop();
            popped.dispose();
            //Gdx.input.setInputProcessor(uiStack.peek().getStage());
        }
    }
}