package com.pokemon.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ScreenUtils;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.pokemon.game.Pokemon;
import com.pokemon.ui.AbstractUi;
import com.pokemon.ui.LoginUi;
import com.pokemon.util.SkinGenerator;

import java.util.HashMap;
import java.util.Stack;

import java.util.HashMap;

public class MainMenuScreen implements Screen {
    final Pokemon game;
    OrthographicCamera camera;
    private Texture logoImage;
    private Stack<AbstractUi> uiStack;
    public MainMenuScreen(Pokemon game) {
        VisUI.load(VisUI.SkinScale.X1);
        this.game = game;
        uiStack = new Stack<>();
        logoImage = new Texture(Gdx.files.internal("logo.png"));
        camera = new OrthographicCamera();
        camera.setToOrtho(false,800,480);

        uiStack.add(new LoginUi(this,game));
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0,0,0,1);
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.batch.draw(logoImage, 144, 280,512,192);
        game.batch.end();

        for (AbstractUi abstractUi : uiStack) {
            abstractUi.update();
        }
    }

    public void gameStart() {
        game.setScreen(new GameScreen(game));

        //game.setScreen(new BattleScreen(game)); // 배틀스크린 테스트용
        dispose();
    }

    @Override
    public void resize(int width, int height) {
//        testUi.resize(width, height);
        for (AbstractUi abstractUi : uiStack) {
            abstractUi.resize(width, height);
        }
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
        VisUI.dispose();
        for (AbstractUi abstractUi : uiStack) {
            abstractUi.dispose();
        }
        logoImage.dispose();
    }

    public void pushScreen(AbstractUi ui) {
        uiStack.add(ui);
    }

    public void popScreen() {
        AbstractUi popped = uiStack.pop();
        popped.dispose();
        Gdx.input.setInputProcessor(uiStack.peek().getStage());
    }

}
