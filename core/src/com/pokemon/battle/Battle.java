package com.pokemon.battle;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.pokemon.model.PK;

import java.sql.SQLException;
import java.sql.Statement;

import static com.pokemon.db.db.con;
import static com.pokemon.db.db.rs;
import static com.pokemon.screen.BattleScreen.playerNum;
import static com.pokemon.ui.LoginUi.playerID;

public class Battle {
    public enum STATE {
        READY_TO_PROGRESS,
        SELECT_NEW_POKEMON,
        RAN,
        WIN,
        LOSE,
        ;
    }

    private STATE state;

    private PK player;
    private PK opponent;

    private Texture P_T;
    private Texture O_T;

    private AssetManager assetManager;

    public static String OppoID;

    private String[] oppoKey;
    private String wildKey;


    public Battle(boolean multi) {
        assetManager = new AssetManager();
        assetManager.load("battle/battlepack.atlas", TextureAtlas.class);
        assetManager.load("pokemon/bulbasaur.png", Texture.class);
        assetManager.load("pokemon/slowpoke.png", Texture.class);
        assetManager.finishLoading();

        this.P_T = assetManager.get("pokemon/bulbasaur.png", Texture.class);
        this.O_T = assetManager.get("pokemon/slowpoke.png", Texture.class);

        String[] userKey = {playerID, String.valueOf(playerNum)};
        this.player = new PK(userKey, P_T); //유저 포켓몬 가져오기
        if(!multi) {
            String sql = "SELECT PM_ID FROM MAP_INFO WHERE LIVE = 'MAP01' ORDER BY RAND() LIMIT 1;"; //MAP_INFO 테이블에서 해당 맵의 랜덤 포켓몬 한개 가져오기
            String PM_ID = null;
            try {
                Statement stmt = con.createStatement();
                rs = stmt.executeQuery(sql);
                while(rs.next()) {
                    PM_ID = rs.getString("PM_ID");
                }
            }catch(SQLException e){};
            wildKey = PM_ID;
           //this.opponent = new PK(wildKey, O_T); //야생 포켓몬
           this.opponent = new PK("PM_02", O_T); //야생 포켓몬
        }
        else {
            oppoKey = new String[]{OppoID, String.valueOf(playerNum)};
            this.opponent = new PK(oppoKey, O_T); //상대 포켓몬
        }
    }

    public PK getP_P() {
        return player;
    }


    public PK getO_P() {
        return opponent;
    }
}