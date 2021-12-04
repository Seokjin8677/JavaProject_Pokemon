package com.pokemon.controller;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.maps.MapLayer;
import com.pokemon.battle.Battle;
import com.pokemon.db.db;
import com.pokemon.game.Pokemon;
import com.pokemon.model.Player;
import com.pokemon.screen.BattleScreen;
import com.pokemon.screen.GameScreen;
import com.pokemon.ui.*;
import com.pokemon.battle.event.BattleEvent;
import com.pokemon.battle.event.TextEvent;

import java.util.Queue;
import java.util.Stack;


public class BattleScreenController extends InputAdapter {

    public enum STATE {
        USE_NEXT_POKEMON,    // Text displayed when Pokemon faints
        SELECT_ACTION,        // Moves, Items, Pokemon, Run
        DEACTIVATED        // Do nothing, display nothing
    }

    private Player player;
    private STATE state;

    private Queue<BattleEvent> queue;

    private Battle battle;

    private DialogueBox dialogue;
    private OptionBox optionBox;
    public static MoveSelectBox moveSelect;

    private Stack<AbstractUi> uiStack;
    private Pokemon game;
    private BattleScreen battleScreen;

    public void setDialogue(boolean visible){
        dialogue.setVisible(visible);
    }

    public BattleScreenController(Pokemon game, BattleScreen battleScreen, Battle battle, Queue<BattleEvent> queue, DialogueBox dialogue, MoveSelectBox options, OptionBox optionBox, Stack<AbstractUi> uiStack, Player player) {
        this.game = game;
        this.battleScreen = battleScreen;
        this.battle = battle;
        this.queue = queue;
        this.dialogue = dialogue;
        moveSelect = options;
        this.optionBox = optionBox;
        this.uiStack = uiStack;
        this.player = player;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (this.state == STATE.DEACTIVATED) {
            return false;
        }
        if (this.state == STATE.USE_NEXT_POKEMON && optionBox.isVisible()) {
            if (keycode == Keys.UP) {
                optionBox.moveUp();
            } else if (keycode == Keys.DOWN) {
                optionBox.moveDown();
            } else if (keycode == Keys.X) {
                if (optionBox.getIndex() == 0) { // YES selected
                    for (int i = 0; i < battle.getPTrainer().getTeamSize(); i++) {
                        if (!battle.getPTrainer().getPokemon(i).isFainted()) {
                            battle.chooseNewPokemon(battle.getPTrainer().getPokemon(i));
                            optionBox.setVisible(false);
                            this.state = STATE.DEACTIVATED;
                            break;
                        }
                    }
                } else if (optionBox.getIndex() == 1) { // NO selected
                    battle.attemptRun();
                    optionBox.setVisible(false);
                    this.state = STATE.DEACTIVATED;
                }
            }
        }
  /*      if (battle.getO_P().isCapture()) {
            endTurn();
        }*/
        if (moveSelect.isVisible()) {
            if (keycode == Keys.X) {
                int selection = moveSelect.getSelection();
                /* 해당 스킬이 null이 아니고 Current SK CNT가 1이상일때만 동작*/
                if (battle.getP_P().getSkill()[selection] == null || battle.getP_P().getCurrent_SK_CNT()[selection] <= 0) {
                    queue.add(new TextEvent("사용할 수 없습니다.", 0.5f));
                } else {
                    battle.progress(moveSelect.getSelection());
                    endTurn();
                }
            } else if (keycode == Keys.UP) {
                moveSelect.moveUp();
                return true;
            } else if (keycode == Keys.DOWN) {
                moveSelect.moveDown();
                return true;
            } else if (keycode == Keys.LEFT) {
                moveSelect.moveLeft();
                return true;
            } else if (keycode == Keys.RIGHT) {
                moveSelect.moveRight();
                return true;
            } else if (keycode == Keys.F9) {
                battle.selectItem();
                uiStack.add(new useItemUi(this, battle, battleScreen, game, player));
            }
        }
        return false;
    }

    public STATE getState() {
        return state;
    }

    public void setState(STATE state) {
        this.state = state;
    }

    public void update(float delta) {
        if (isDisplayingNextDialogue() && dialogue.isFinished() && !optionBox.isVisible()) {
            optionBox.clearChoices();
            optionBox.addOption("YES");
            optionBox.addOption("NO");
            optionBox.setVisible(true);
        }

    }


    /*
     * Displays the UI for a new turn
     */

    public void restartTurn() {
        this.state = STATE.SELECT_ACTION;
        dialogue.setVisible(false);
        for (int i = 0; i <= 3; i++) {
            String label = "------";
            String skill = db.GET_PM_SK_NAME(battle.getP_P().getSkill()[i]);
            int max = battle.getP_P().getSK_CNT()[i];
            int cur = battle.getP_P().getCurrent_SK_CNT()[i];
            if (skill != null) {
                label = skill + " " + cur + "/" + max;
            }
            moveSelect.setLabel(i, label);
        }
        moveSelect.setVisible(true);
    }

    /*
     * Displays UI for selecting a new Pokemon
     */

    public void displayNextDialogue() {
        this.state = STATE.USE_NEXT_POKEMON;
        dialogue.setVisible(true);
        dialogue.animateText("다음 포켓몬을 내보내시겠습니까?");
    }

    public boolean isDisplayingNextDialogue() {
        return this.state == STATE.USE_NEXT_POKEMON;
    }

    public void endTurn() {
        moveSelect.setVisible(false);
        this.state = STATE.DEACTIVATED;
    }
}

