package com.pokemon.inventory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.pokemon.util.SkinGenerator;

import java.sql.SQLException;
import java.sql.Statement;

import static com.pokemon.db.db.con;
import static com.pokemon.db.db.rs;

/**
 * An Item is held by an inventory slot and can be one of:
 * - potion (restores current hp)
 * - equip (several categories of equips)
 * - misc (some other useless thing)
 *
 * @author Ming Li
 */
public class Item {
    // id
    public String name;
    // name displayed on tooltip
    public String labelName;
    // for rendering onto tooltip
    private String info;
    private String property;
    private String effect;
    private int sell;

    // type of item
    /**
     * 0 - 몬스터볼
     * 1 - 재료
     * 2 - 머리
     * 3 - 장신구
     * 4 - 옷
     * 5 - 신발
     * 6 - 무기
     * 7 - 소비
     */
    public final static String[] TYPE= {"U_HEAD", "U_ACC","U_CLOTHES","U_SHOES", "U_WEAPON"};
    private int type;
    private String key;



    //인벤토리 인덱스
    private int index;
    //제작 인덱스
    private int cIndex;
    //아이템 갯수
    private int cnt;
    //장착 유무
    private boolean equipped = false;
    //제작창
    private boolean crafting = false;

   //이미지
    public Image actor;
    public Label count;


    public Item(String key) {
        AssetManager assetManager = new AssetManager();
        //assetManager.load("pokemon/Raichu.png",Texture.class);
        assetManager.load("texture/돌.png",Texture.class);
        assetManager.load("texture/풀.png",Texture.class);
        assetManager.load("texture/몬스터볼.png",Texture.class);
        assetManager.load("texture/수퍼볼.png",Texture.class);
        assetManager.load("texture/나무곡괭이.png",Texture.class);
        assetManager.load("texture/나무괭이.png",Texture.class);
        assetManager.load("texture/상처약.png",Texture.class);
        assetManager.load("texture/좋은상처약.png",Texture.class);
        assetManager.load("texture/고급상처약.png",Texture.class);
        assetManager.load("texture/풀회복약.png",Texture.class);
        assetManager.load("texture/PP에이더.png",Texture.class);
        assetManager.load("texture/PP맥스.png",Texture.class);
        assetManager.finishLoading();

        Skin skin = SkinGenerator.generateSkin(assetManager);

        String sql = "SELECT ITEM_NAME, ITEM_INFO,ITEM_PROPERTY, ITEM_TYPE FROM ITEM WHERE ITEM_ID ='"+key+"';";
        this.key = key;//ITEM_ID
        try {
            Statement stmt = con.createStatement();
            rs = stmt.executeQuery(sql);
            while(rs.next()) {
                this.name = rs.getString("ITEM_NAME"); //아이템 명
                this.info = rs.getString("ITEM_INFO"); //아이템 설명
                this.property = rs.getString("ITEM_PROPERTY"); //아이템 속성 ex) 나무, 노멀, 등 //아이템 효과 ex)채광, 제작, 공격력 증가등의 스킬기록 // 아이템 속성에 따라 증가시킴
                this.type = rs.getInt("ITEM_TYPE"); //아이템 종류 ex) 장비, 재료, 포켓몬볼
                actor = new Image(assetManager.get("texture/"+name+".png", Texture.class));
                count = new Label("",skin);
            }
        }catch(SQLException e){
            System.out.println("SQLException" + e);
            e.printStackTrace();
        }
    }

    public String getEffect(){
        return effect;
    }
    public String getKey(){
            return key;
    }
    public String getProperty(){
        return property;
    }
    public String getInfo(){
        return info;
    }
    public int getType(){
        return type;
    }
    public String getName(){return name;}
    //인벤토리 인덱스
    public int getIndex(){
        return index;
    }
    public void setIndex(int index){
       this.index = index;
    }
    //제작 인덱스
    public int getCIndex(){
        return cIndex;
    }
    public void setCIndex(int cIndex){
        this.cIndex = cIndex;
    }
    public int getSell(){return sell;}
    public boolean getEquipped(){
        return equipped;
    }
    public void setEquipped(boolean equipped){
        this.equipped = equipped;
    }

    public boolean getCrafting(){
        return crafting;
    }
    public void setCrafting(boolean crafting){
        this.crafting = crafting;
    }

    public void setCNT(int cnt){this.cnt = cnt;}
    public void setCurrentCNT(){this.count.setText(getCNT());}
    public int getCurrentCNT(){
        switch (count.getText().length){
            case 1:
                return 1;
            case 2:
                return 8;
            case 3:
                return 14;
            case 4:
                return 20;
            default: return 0;
        }
    }
    public int getCNT(){return cnt;}

}
