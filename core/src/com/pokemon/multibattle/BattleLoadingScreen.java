package com.pokemon.multibattle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ScreenUtils;
import com.pokemon.game.Pokemon;
import com.pokemon.util.GifDecoder;

public class BattleLoadingScreen implements Screen {
    final Pokemon game;
    Animation<TextureRegion> clock;
    private Texture background;
    private float elapsed;
    private SpriteBatch batch;
    private BattleClient bc;
    int a;

    public BattleLoadingScreen(Pokemon game) {
        bc = new BattleClient(game);
        this.game = game;
        batch = new SpriteBatch();
        background = new Texture(Gdx.files.internal("C:\\loding.png"));
        clock = GifDecoder.loadGIFAnimation(Animation.PlayMode.LOOP, Gdx.files.internal("C:\\loding4.gif").read());
    }

    @Override
    public void show() {

    }
    public void startLoading(){
        game.setScreen(new Loading(game));
        dispose();
    }

    public void select(){
       game.setScreen(new PokemonSelectScreen(game,bc));
        dispose();
    }
    @Override
    public void render(float delta) {
        elapsed += Gdx.graphics.getDeltaTime();
        ScreenUtils.clear(0, 0, 0, 1);
        batch.begin();
        batch.draw(background,0,0);
        batch.draw(clock.getKeyFrame(elapsed), 140f, 50f);
        if(bc.getA() == 50){
            select();
        }
        batch.end();

    }

    @Override
    public void resize(int width, int height) {

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
