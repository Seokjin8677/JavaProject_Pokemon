package com.pokemon.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pokemon.battle.Battle;
import com.pokemon.controller.GameController;
import com.pokemon.controller.PlayerController;
import com.pokemon.game.Pokemon;
import com.pokemon.game.Settings;
import com.pokemon.model.PK;
import com.pokemon.model.Player;
import com.pokemon.ui.*;
import com.pokemon.util.SkinGenerator;

import static com.pokemon.ui.LoginUi.playerID;

public class BattleScreen implements Screen {
    final Pokemon game;
    private AssetManager assetManager;
    private OrthographicCamera camera;

    private PlayerController playerController;
    private BattleRenderer battleRenderer;
    private GameController gameController;
    public static int playerNum=1;

    /* View */
    private Viewport gameViewport;

    /* Model */
    private Battle battle;

    /* UI */
    private Skin skin;
    private Stage uiStage;
    private Table dialogueRoot;
    private DialogueBox dialogueBox;
    private OptionBox optionBox;

    private Table moveSelectRoot;
    private MoveSelectBox moveSelectBox;

    private Table statusBoxRoot;
    //private DetailedStatusBox playerStatus;
    private StatusBox playerStatus;
    private StatusBox opponentStatus;

    public BattleScreen(Pokemon game) {
        this.game = game;
        gameViewport = new ScreenViewport();
        camera = new OrthographicCamera();
        camera.setToOrtho(false,800,480);
        this.battle = new Battle(false);

        assetManager= new AssetManager();
        assetManager.load("ui/uipack.atlas", TextureAtlas.class);
        assetManager.load("font/han/gul.fnt", BitmapFont.class);
        assetManager.finishLoading();

        skin = SkinGenerator.generateSkin(assetManager);

        battleRenderer = new BattleRenderer(game,battle,camera);
        initUI();
    }

    private void initUI() {
        /* ROOT UI STAGE */
        uiStage = new Stage(new ScreenViewport());
        uiStage.getViewport().update(
                (int)(Gdx.graphics.getWidth()/Settings.SCALE),
                (int)(Gdx.graphics.getHeight()/Settings.SCALE),
                true);
        uiStage.setDebugAll(false);

        /* STATUS BOXES */
        statusBoxRoot = new Table();
        statusBoxRoot.setFillParent(true);
        uiStage.addActor(statusBoxRoot);

        //playerStatus = new DetailedStatusBox(skin);
        playerStatus = new StatusBox(skin);
        playerStatus.setText(battle.getP_P().getName());
        playerStatus.setLV("LV"+battle.getP_P().getLV());
        playerStatus.setHpText(battle.getP_P().getCurrentHP() + "/" + battle.getP_P().getStat()[2]);

        opponentStatus = new StatusBox(skin);
        opponentStatus.setText(battle.getO_P().getName());
        opponentStatus.setLV("LV"+battle.getO_P().getLV());
        opponentStatus.setHpText(battle.getO_P().getCurrentHP() + "/" + battle.getO_P().getStat()[2]);

        statusBoxRoot.add(playerStatus).expand().align(Align.left);
        statusBoxRoot.add(opponentStatus).expand().align(Align.right);

        /* MOVE SELECTION BOX */
        moveSelectRoot = new Table();
        moveSelectRoot.setFillParent(true);
        uiStage.addActor(moveSelectRoot);

        moveSelectBox = new MoveSelectBox(skin);
        moveSelectBox.setVisible(false);

        moveSelectRoot.add(moveSelectBox).expand().align(Align.bottom);

        /* OPTION BOX */
        dialogueRoot = new Table();
        dialogueRoot.setFillParent(true);
        uiStage.addActor(dialogueRoot);

        optionBox = new OptionBox(skin);
        optionBox.setVisible(false);

        /* DIALOGUE BOX */
        dialogueBox = new DialogueBox(skin);
        dialogueBox.setVisible(false);

        Table dialogTable = new Table();
        dialogTable.add(optionBox).expand().align(Align.right).space(8f).row();
        dialogTable.add(dialogueBox).expand().align(Align.bottom).space(8f);

        dialogueRoot.add(dialogTable).expand().align(Align.bottom);
    }

    @Override
    public void show() {

    }
    float elapsed;
    @Override
    public void render(float delta) {
        gameViewport.apply();
        camera.position.x =  Gdx.graphics.getWidth()/2;
        camera.position.y = Gdx.graphics.getHeight()/2;
        game.batch.setProjectionMatrix(camera.combined);
        elapsed += Gdx.graphics.getDeltaTime();
        game.batch.begin();
        battleRenderer.render(game.batch,elapsed);
        game.batch.end();
        camera.update();
        uiStage.draw();
    }

    @Override
    public void resize(int width, int height) {
        game.batch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
        game.batch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
        uiStage.getViewport().update(
                (int)(Gdx.graphics.getWidth()/Settings.SCALE),
                (int)(Gdx.graphics.getHeight()/Settings.SCALE),
                true);
        gameViewport.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
