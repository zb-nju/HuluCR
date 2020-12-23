package nju.java315.client.game;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.MenuItem;
import com.almasb.fxgl.app.scene.FXGLDefaultMenu;
import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.app.scene.SceneFactory;
import com.almasb.fxgl.app.scene.SimpleGameMenu;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.ui.UI;
import com.almasb.fxgl.ui.UIController;

import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import nju.java315.client.game.components.PlayerComponent;
import nju.java315.client.game.event.PutEvent;
import nju.java315.client.game.type.MonsterType;
import nju.java315.client.game.type.CursorEventType;

import static com.almasb.fxgl.dsl.FXGL.*;
import static nju.java315.client.game.Config.*;

import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;
import java.util.EnumSet;
import java.util.Map;
import java.util.Random;

interface randomMonster{
    MonsterType getRandomMonster();
}

public class HuluCRApp extends GameApplication {

    private HuluCRController uiController;

    private static Random rand = new Random(47);

    //闭包，获得随机的卡片
    static randomMonster randomMonster = () -> MonsterType.class.getEnumConstants()[rand.nextInt(MonsterType.class.getEnumConstants().length)];

    List<Entity> cards = new ArrayList<>();

    int cardX = 50;
    int[] cardY = {240, 325, 410, 495, 600};

    //以下变量用于选卡操作
    int currentCard = -1;
    Point2D lastCursorPoint = null;

    //鼠标操作类型
    CursorEventType cursorEventType = CursorEventType.UNKNOW;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(WIDTH);
        settings.setHeight(HEIGHT);
        settings.setTitle("葫芦娃战争");
        settings.setVersion("0.0.1");
        settings.setProfilingEnabled(false);
        settings.setMainMenuEnabled(true);
        settings.setGameMenuEnabled(true);
        //settings.setEnabledMenuItems(EnumSet.of(MenuItem.EXTRA));

        settings.setSceneFactory(new SceneFactory() {
            @Override
            public FXGLMenu newMainMenu() {

                return new FXGLDefaultMenu(MenuType.MAIN_MENU);
                //return new HuluCRMenu(MenuType.MAIN_MENU);
            }
        });
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }


    @Override
    protected void initInput() {
        Input input = getInput();

        UserAction putCard = new UserAction("put"){
            @Override
            protected void onActionBegin(){
                Point2D cursorPoint = getInput().getMousePositionUI();
                System.out.println("put:" + cursorPoint.toString());
                dealWithCursorBegin(cursorPoint);
            }

            @Override
            protected void onAction() {
                Point2D cursorPoint = getInput().getMousePositionUI();
                dealWithCursor(cursorPoint);
            }

            @Override
            protected void onActionEnd() {
                Point2D cursorPoint = getInput().getMousePositionUI();
                dealWithCursorEnd(cursorPoint);
            }
        };
        input.addAction(putCard, MouseButton.PRIMARY);
    }
    private Entity player;
	// private PlayerComponent playerComponent;

    // 初始化事件监听和处理
    @Override
    protected void onPreInit() {
        // 事件相关
        onEvent(PutEvent.ANY, this::onMonsterPut);
    }

    // 建立映射表
    @Override
    protected void initGameVars(Map<String, Object> vars) {

        vars.put("upTowerLives", CHILD_TOWER_LIVES);
        vars.put("downTowerLives", CHILD_TOWER_LIVES);
        vars.put("mainTowerLives", MAIN_TOWER_LIVES);
        vars.put("waterMeter", WATER_INIT_COUNT);
        vars.put("min", 0);
        vars.put("sec", 0);
    }

    // 初始化游戏元素
    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(new HuluCRFactory());

        //圣水自动增加
        getGameTimer().runAtInterval(()->{
            if(getd("waterMeter") < WATER_MAX_COUNT){
                inc("waterMeter", 1 / WATER_UP_STEP);
                if(getd("waterMeter") > WATER_MAX_COUNT)
                    set("waterMeter", WATER_MAX_COUNT);
            }
        }, Duration.seconds(WATER_UP_TIME / WATER_UP_STEP));

        for(int i = 0;i < 4; i++){
            MonsterType temp = randomMonster.getRandomMonster();

            cards.add(
                spawn("Card", new SpawnData(cardX, cardY[i]).put("type", temp))
            );
        }

        //计时器
        getGameTimer().runAtInterval(()->{
            inc("sec", 1);
            if(geti("sec")>=60){
                inc("min", 1);
                set("sec", 0);
            }
            String minValue, secValue;
            if(geti("min") < 10)
                minValue = "0" + String.valueOf(geti("min"));
            else
                minValue = String.valueOf(geti("min"));

            if(geti("sec") < 10)
                secValue = "0" + String.valueOf(geti("sec"));
            else
                secValue = String.valueOf(geti("sec"));

            uiController.getTimeLabel().setText(minValue + ":" + secValue);
        }, Duration.seconds(1));

        spawn("Background");

        spawnPlayer();
    }

    // 初始化物理环境
    @Override
    protected void initPhysics() {

    }

    // 初始化ui
    @Override
    protected void initUI() {
        uiController = new HuluCRController(getGameScene());

        UI ui = getAssetLoader().loadUI(Asset.FXML_MAIN_UI, uiController);

        uiController.getWaterMeter().currentValueProperty().bind(getdp("waterMeter"));

        getGameScene().addUI(ui);
    }

    private boolean runningFirstTime = true;
    private boolean gameLoading = false;
    @Override
    protected void onUpdate(double tpf) {
        super.onUpdate(tpf);
        if(runningFirstTime) {
            getDialogService().showInputBox("Please input your room id", answer -> {
                System.out.println("room id: "+ answer);
                // send room id to server
                runOnce(this::stopLoading, Duration.seconds(2.0));

                runningFirstTime = false;
                gameLoading = true;
            });
        }
        else if (gameLoading) {
            //System.out.println("loading");
        }
        else{

        }

    }

    //确定正在进行哪种操作
    public void dealWithCursorBegin(Point2D cursorPoint){
        int x = (int)cursorPoint.getX(), y = (int)cursorPoint.getY();
        if(x >= cardX && x <= cardX + 73){
            for(int i = 0;i < 4;i++){
                if(y >= cardY[i] && y <= cardY[i] + 73){
                    currentCard = i;
                    lastCursorPoint = cursorPoint;
                    cursorEventType = CursorEventType.PUT_CARD;
                    break;
                }
            }
        }
    }

    public void dealWithCursor(Point2D cursorPoint){
        switch(cursorEventType){
            case PLAYER_READY:
                break;
            case PUT_CARD:
                if(currentCard != -1){
                    double dx = (int)(cursorPoint.getX() - lastCursorPoint.getX());
                    double dy = (int)(cursorPoint.getY() - lastCursorPoint.getY());

                    cards.get(currentCard).translate(dx, dy);
                    lastCursorPoint = cursorPoint;
                }
                break;
            case UNKNOW:
                break;
            default:
                break;
        }
    }

    public void dealWithCursorEnd(Point2D cursorPoint){
        switch(cursorEventType){
            case PLAYER_READY:
                break;
            case PUT_CARD:
                if(currentCard != -1 && isSuitableForPutCard(cursorPoint)){
                    MonsterType type = (MonsterType) cards.get(currentCard).getType();

                    if((int)getd("waterMeter") >= type.getCost()){
                        inc("waterMeter", (double)(-1 * type.getCost()));

                        Entity card = cards.remove(currentCard);

                        MonsterType temp = randomMonster.getRandomMonster();
                        cards.add(
                            spawn("Card", new SpawnData(cardX, cardY[4]).put("type", temp))
                        );
                        
                        // 产生放置事件
                        getGameWorld().removeEntity(card);
                        getEventBus().fireEvent(new PutEvent(PutEvent.ANY, "LargeHulu", cursorPoint));
                        

                    }
                    
                }
                runOnce(this::updateCardPosition, Duration.millis(200));
                
                break;
            case UNKNOW:
                break;
            default:
                break;
        }
        cursorPoint = null;
        currentCard = -1;
        cursorEventType = CursorEventType.UNKNOW;
    }

    public void updateCardPosition(){
        for(int i = 0;i < 4;i++){
            animationBuilder()
                .interpolator(Interpolators.EXPONENTIAL.EASE_IN())
                .duration(Duration.millis(200))
                .translate(cards.get(i))
                .from(cards.get(i).getPosition())
                .to(new Point2D(cardX, cardY[i]))
                .buildAndPlay();
        }
    }

    public boolean isSuitableForPutCard(Point2D cursorPoint){
        int x = (int)cursorPoint.getX(), y = (int)cursorPoint.getY();
        if(x >= 235 && x <= 545 && y >= 50 && y <= 550)
            return true;
        else if(x >= 210 && x<= 235 && y >= 215 && y <= 385)
            return true;
        return false;
    }

    private void spawnPlayer() {
        player = spawn("Player", 15, 50);
        // playerComponent = player.getComponent(PlayerComponent.class);
    }

    private void stopLoading(){
        gameLoading = false;
    }

    private void onMonsterPut(PutEvent event){
        System.out.println("monster");
        spawn(event.getMonsterName(), new SpawnData(event.getPoint()).put("hp", 100));

        // 向server发送放置消息
        spawn("Fireball", event.getPoint());
    }
}
