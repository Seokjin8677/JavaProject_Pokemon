package com.pokemon.model;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.pokemon.inventory.Equipment;
import com.pokemon.inventory.Inventory;
import com.pokemon.db.db;
import com.pokemon.inventory.Item;
import com.pokemon.util.AnimationSet;

import java.sql.SQLException;
import java.sql.Statement;

import static com.pokemon.db.db.con;
import static com.pokemon.db.db.rs;
import static com.pokemon.game.Settings.SCALE;
import static com.pokemon.game.Settings.TILE_SIZE;
import static com.pokemon.ui.LoginUi.playerID;

public class Player extends Rectangle {
    private AnimationSet<TextureRegion> animations;
    private DIRECTION facing;
    private PLAYER_STATE state;
    private final float playerSizeX = TILE_SIZE;
    private final float playerSizeY = TILE_SIZE*1.5f;

    public float getPlayerSizeX() {
        return playerSizeX * SCALE;
    }

    public float getPlayerSizeY() {
        return playerSizeY * SCALE;
    }

    private float ANIM_TIME = 0.6f;
    private float animTimer;

    // inventory and equips
    public Inventory inventory;
    public Equipment equips;

    private int LV;
    private int EXP;
    private int gold;
    private int RANK;
    private String skill[];
    private int skill_LV[];
    private int skill_EXP[];

    public Player(int x, int y, AnimationSet<TextureRegion> animations) {
        inventory = new Inventory(playerID);
        equips = new Equipment(playerID);
        skill = db.GET_SK(playerID);
        skill_LV = db.GET_SK_LV(playerID);
        skill_EXP = db.GET_SK_EXP(playerID);

        String sql = "SELECT U_LV,U_EXP,U_RANK,GOLD FROM USER WHERE U_ID ='"+playerID+"';";
        try {
            Statement stmt = con.createStatement();
            rs = stmt.executeQuery(sql);
            while(rs.next()) {
                this.LV = rs.getInt("U_LV");
                this.EXP = rs.getInt("U_EXP");
                this.RANK = rs.getInt("U_RANK");
                this.gold = rs.getInt("gold");
            }
        }catch(SQLException e){
            System.out.println("SQLException" + e);
            e.printStackTrace();
        }

        this.width = 32;
        this.height = 24;
        this.x = x;
        this.y = y;
        this.animations = animations;
        this.state = PLAYER_STATE.STANDING;
        this.facing = DIRECTION.SOUTH;
    }

    public void update(float delta) {
        if (state == PLAYER_STATE.WALKING) {
            animTimer += delta;
            if (animTimer > ANIM_TIME) {
                animTimer = 0f;
            }
        }
    }

    public enum PLAYER_STATE {
        WALKING,
        STANDING
    }

    public TextureRegion getSprites() {
        if (state == PLAYER_STATE.WALKING) {
            return animations.getWalking(facing).getKeyFrame(animTimer);
        }
        return animations.getStanding(facing);
    }

    public void equip(Item item) {
        //아이템 효과에 따라 스킬 레벨 증가
        for(int i=0;i<6;i++){
            if(skill[i].equals(item.getEffect())){
                skill_LV[i] +=db.GET_E_V(item.getProperty());
            }
        }
    }

    public void unequip(Item item) {
        for(int i=0;i<6;i++){
            if(skill[i].equals(item.getEffect())){
                skill_LV[i] -=db.GET_E_V(item.getProperty());
            }
        }
    }

    public void addGold(int g) {
       this.gold += g;
    }
    public int getGold(){
        return gold;
    }
    public int getLV(){
        return LV;
    }
    public int getRANK(){return RANK;}
    public int getEXP(){
        return EXP;
    }
    public int getMaxEXP(){
        return db.GET_MAX_EXP(LV);
    }

    public void finishMove() {
        state = PLAYER_STATE.STANDING;
    }

    public void setFacing(DIRECTION facing) {
        this.facing = facing;
    }

    public void setState(PLAYER_STATE state) {
        this.state = state;
    }
}
