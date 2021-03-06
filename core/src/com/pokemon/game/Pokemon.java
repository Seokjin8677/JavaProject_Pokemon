package com.pokemon.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.ScreenUtils;
import com.pokemon.controller.PlayerController;
import com.pokemon.model.Player;
import com.pokemon.multibattle.BattleClient;
import com.pokemon.screen.BattleScreen;
import com.pokemon.screen.GameScreen;

import com.pokemon.screen.MainMenuScreen;
//import com.pokemon.ui.inventory.InventoryUI;

import java.util.HashMap;

public class Pokemon extends Game {
	public SpriteBatch batch;
	public BitmapFont font;
    private HashMap<String, String> accounts = new HashMap<>(); // 임시 로그인 기능용
	private String str;
	private Music battleMusic;
	private int i;
	private int j;
	private boolean onoff;
	public boolean isOnoff() {
		return onoff;
	}

	public void setOnoff(boolean onoff) {
		this.onoff = onoff;
	}

	public int getJ() {
		return j;
	}
	public void setJ(int j) {
		this.j = j;
	}
	public int getI() {
		return i;
	}

	public void setI(int i) {
		this.i = i;
	}

	public String getSendMessage() {
		return sendMessage;
	}

	public void setSendMessage(String sendMessage) {
		this.sendMessage = sendMessage;
	}

	private String sendMessage;
	private String recieveMessage;

	public String getSelectedPokemon(int n) {
		return selectedPokemon[n];
	}

	public void setSelectedPokemon(String[] selectedPokemon) {
		this.selectedPokemon = selectedPokemon;
	}

	private String[] selectedPokemon;

	public void setrecieveMessage(String recieveMessage) {
		this.recieveMessage = recieveMessage;
	}

	public String getrecieveMessage() {
		return recieveMessage;
	}

	public String getStr() {
		return str;
	}

	public void setStr(String str) {
		this.str = str;
	}
	@Override
	public void create () {
		batch = new SpriteBatch();
		font = new BitmapFont();
		//this.setScreen(new window(this));
		this.setScreen(new MainMenuScreen(this));
		//this.setScreen(new BattleScreen(this));
		battleMusic = Gdx.audio.newMusic(Gdx.files.internal("music/battleMusic.ogg"));
		battleMusic.setVolume(0.05f);
		battleMusic.setLooping(true);

	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		font.dispose();
	}

	// 임시 로그인 기능용
	public void createAccount(String id,String password) {
		accounts.put(id, password);
	}

	// 임시 로그인 기능용
	public boolean loginValidate(String id,String password) {
		try {
			if (accounts.get(id).equals(password)) {
				return true;
			}
		} catch (NullPointerException ignored) {}

		return false;
	}

	public Music getBattleMusic() {
		return battleMusic;
	}
}
