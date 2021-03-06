package nju.java315.client.game;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.ExpireCleanComponent;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.entity.state.StateComponent;
import com.almasb.fxgl.particle.ParticleComponent;
import com.almasb.fxgl.particle.ParticleEmitter;
import com.almasb.fxgl.particle.ParticleEmitters;
import com.almasb.fxgl.pathfinding.CellMoveComponent;
import com.almasb.fxgl.pathfinding.astar.AStarMoveComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Point2D;
import javafx.scene.effect.BlendMode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import nju.java315.client.game.components.AttackTargetComponent;
import nju.java315.client.game.components.DamageComponent;
import nju.java315.client.game.components.HealthCompoent;
import nju.java315.client.game.components.IdentityComponent;
import nju.java315.client.game.components.SensorComponent;
import nju.java315.client.game.components.ai.MovableMonsterAIComponent;
import nju.java315.client.game.components.ai.UnmovableMonsterAIComponent;
import nju.java315.client.game.components.attack.ArrowAttack;
import nju.java315.client.game.components.attack.FireBallAttack;
import nju.java315.client.game.components.attack.VenomBallAttack;
import nju.java315.client.game.components.attack.WaterBallAttack;
import nju.java315.client.game.type.AttackMethodType;
import nju.java315.client.game.type.EntityType;
import nju.java315.client.game.type.MonsterType;

import static com.almasb.fxgl.dsl.FXGL.*;

import com.almasb.fxgl.core.util.LazyValue;

public class HuluCRFactory implements EntityFactory {

    @Spawns("Block")
    public Entity newBlock(SpawnData data) {
        int width = data.get("width");
        int height = data.get("height");
        Rectangle rect = new Rectangle(width,height,Color.TRANSPARENT);

        return entityBuilder(data)
                .type(HuluCRType.BLOCK)
                .with(new CollidableComponent(true))
                .viewWithBBox(rect)
                .zIndex(-10)
                .build();
    }

    @Spawns("Background")
    public Entity newBackground(SpawnData data) {
        return entityBuilder().at(-10, -10)
                // bigger than game size to account for camera shake
                .view(texture("background/background.png", Config.WIDTH + 20, Config.HEIGHT + 20)).zIndex(-500).build();
    }


    @Spawns("Fireball")
    public Entity newFireball(SpawnData data) {

        SimpleBooleanProperty flag = new SimpleBooleanProperty();
        flag.set(true);

        ParticleEmitter emitter = ParticleEmitters.newFireEmitter();

        emitter.startColorProperty().bind(
                Bindings.when(flag)
                        .then(Color.RED)
                        .otherwise(Color.RED)
        );

        emitter.endColorProperty().bind(
                Bindings.when(flag)
                        .then(Color.LIGHTPINK)
                        .otherwise(Color.LIGHTBLUE)
        );

        emitter.setBlendMode(BlendMode.SRC_OVER);
        emitter.setSize(2, 3);
        emitter.setEmissionRate(1);
        //Point2D direction = new Point2D(1,0);
        Point2D direction = data.get("direction");
        //int move_speed = data.get("speed");
        return FXGL.entityBuilder(data)
                    .type(AttackMethodType.FIREBALL)
                    .bbox(new HitBox(BoundingShape.circle(5)))
                    .with(new CollidableComponent(true))
                    .with(new ParticleComponent(emitter))
                    .with(new ProjectileComponent(direction, 200))
                    .with(new AttackTargetComponent(data.get("target")))
                    //.with(new ExpireCleanComponent(Duration.seconds(0.8)))
                    .with(new DamageComponent(10))
                    .build();

    }

    @Spawns("Waterball")
    public Entity newWaterball(SpawnData data) {

        SimpleBooleanProperty flag = new SimpleBooleanProperty();
        flag.set(true);

        ParticleEmitter emitter = ParticleEmitters.newFireEmitter();

        emitter.startColorProperty().bind(
                Bindings.when(flag)
                        .then(Color.BLUE)
                        .otherwise(Color.BLUE)
        );

        emitter.endColorProperty().bind(
                Bindings.when(flag)
                        .then(Color.LIGHTBLUE)
                        .otherwise(Color.LIGHTBLUE)
        );

        emitter.setBlendMode(BlendMode.SRC_OVER);
        emitter.setSize(2, 3);
        emitter.setEmissionRate(1);
        //Point2D direction = new Point2D(1,0);
        Point2D direction = data.get("direction");
        return FXGL.entityBuilder(data)
                    .type(AttackMethodType.FIREBALL)
                    .bbox(new HitBox(BoundingShape.circle(5)))
                    .with(new CollidableComponent(true))
                    .with(new ParticleComponent(emitter))
                    .with(new ProjectileComponent(direction, 200))
                    .with(new AttackTargetComponent(data.get("target")))
                    //.with(new ExpireCleanComponent(Duration.seconds(0.8)))
                    .with(new DamageComponent(10))
                    .build();

    }

    @Spawns("Venomball")
    public Entity newVenomball(SpawnData data) {

        SimpleBooleanProperty flag = new SimpleBooleanProperty();
        flag.set(true);

        ParticleEmitter emitter = ParticleEmitters.newFireEmitter();

        emitter.startColorProperty().bind(
                Bindings.when(flag)
                        .then(Color.BLACK)
                        .otherwise(Color.BLACK)
        );

        emitter.endColorProperty().bind(
                Bindings.when(flag)
                        .then(Color.GRAY)
                        .otherwise(Color.GRAY)
        );

        emitter.setBlendMode(BlendMode.SRC_OVER);
        emitter.setSize(2, 3);
        emitter.setEmissionRate(1);
        //Point2D direction = new Point2D(1,0);
        Point2D direction = data.get("direction");
        return FXGL.entityBuilder(data)
                    .type(AttackMethodType.FIREBALL)
                    .bbox(new HitBox(BoundingShape.circle(5)))
                    .with(new CollidableComponent(true))
                    .with(new ParticleComponent(emitter))
                    .with(new ProjectileComponent(direction, 200))
                    .with(new AttackTargetComponent(data.get("target")))
                    //.with(new ExpireCleanComponent(Duration.seconds(0.8)))
                    .with(new DamageComponent(10))
                    .build();

    }

    @Spawns("Arrow")
    public Entity newArrow(SpawnData data) {
        Point2D direction = data.get("direction");

        return entityBuilder(data)
                .viewWithBBox("Arrow.png")
                .type(AttackMethodType.FIREBALL)
                .with(new CollidableComponent(true))
                // .with(new ParticleComponent(emitter))
                .with(new ProjectileComponent(direction, 200))
                .with(new AttackTargetComponent(data.get("target")))
                //.with(new ExpireCleanComponent(Duration.seconds(0.8)))
                .with(new DamageComponent(10))
                .build();
    }

    @Spawns("Fist")
    public Entity newFist(SpawnData data) {

        Point2D direction = data.get("direction");
        //int move_speed = data.get("speed");
        int move_speed = 400;
        return FXGL.entityBuilder(data)
                    .type(AttackMethodType.FIREBALL)
                    .bbox(new HitBox(BoundingShape.circle(5)))
                    .with(new CollidableComponent(true))
                    .with(new ProjectileComponent(direction, move_speed))
                    .with(new ExpireCleanComponent(Duration.seconds(0.8)))
                    .with(new DamageComponent(100))
                    .build();
    }

    @Spawns("Card")
    public Entity newCard(SpawnData data){
        MonsterType type = data.get("type");
        return FXGL.entityBuilder(data)
                    .type(type)
                    .viewWithBBox(type.getCardUrl())
                    .build();
    }

    @Spawns("ReadyButton")
    public Entity newReadyButton(SpawnData data){
        return FXGL.entityBuilder(data)
                    .viewWithBBox(Config.Asset.READY_BUTTON_URL)
                    .type(EntityType.READY_BUTTON)
                    .build();
    }

    @Spawns("ReadyTitle")
    public Entity newReadyTitle(SpawnData data){
        return FXGL.entityBuilder(data)
                    .viewWithBBox(Config.Asset.READY_TITLE_URL)
                    .type(EntityType.READY_TITLE)
                    .build();
    }

    @Spawns("StartTitle")
    public Entity newStartTitle(SpawnData data){
        return FXGL.entityBuilder(data)
                    .viewWithBBox(Config.Asset.START_TITLE_URL)
                    .type(EntityType.START_TITLE)
                    .build();
    }


    @Spawns("LargeHulu")
    public Entity newLargeHulu(SpawnData data) {
        boolean flag = data.get("flag");
        Entity e = FXGL.entityBuilder()
                    .type(MonsterType.LARGE_HULU)
                    .viewWithBBox(MonsterType.LARGE_HULU.getRightUrl())
                    .with(new HealthCompoent(MonsterType.LARGE_HULU.getHp()))
                    .with(new CollidableComponent(true))
                    .with(new FireBallAttack())
                    .with(new IdentityComponent(flag))
                    .with(new CellMoveComponent(Config.CELL_WIDTH, Config.CELL_HEIGHT, MonsterType.LARGE_HULU.getSpeed()))
                    .with(new AStarMoveComponent(new LazyValue<>(() -> geto("grid"))))
                    .with(new SensorComponent(MonsterType.LARGE_HULU.getSensor()))
                    .with(new StateComponent())
                    .with(new MovableMonsterAIComponent())
                    .build();

        e.setPosition(data.getX() - e.getWidth()/2, data.getY() - e.getY() - e.getHeight()/2);
        return e;
    }

    @Spawns("FireHulu")
    public Entity newFireHulu(SpawnData data) {
        boolean flag = data.get("flag");
        Entity e = FXGL.entityBuilder()
                    .type(MonsterType.FIRE_HULU)
                    .viewWithBBox(MonsterType.FIRE_HULU.getRightUrl())
                    .with(new HealthCompoent(MonsterType.FIRE_HULU.getHp()))
                    .with(new CollidableComponent(true))
                    .with(new FireBallAttack())
                    .with(new IdentityComponent(flag))
                    .with(new CellMoveComponent(Config.CELL_WIDTH, Config.CELL_HEIGHT, MonsterType.FIRE_HULU.getSpeed()))
                    .with(new AStarMoveComponent(new LazyValue<>(() -> geto("grid"))))
                    .with(new SensorComponent(150.0))
                    .with(new StateComponent())
                    .with(new MovableMonsterAIComponent())
                    .build();

        e.setPosition(data.getX() - e.getWidth()/2, data.getY() - e.getY() - e.getHeight()/2);
        return e;
    }

    @Spawns("EyeHulu")
    public Entity newEyeHulu(SpawnData data) {
        boolean flag = data.get("flag");
        Entity e = FXGL.entityBuilder()
                    .type(MonsterType.EYE_HULU)
                    .viewWithBBox(MonsterType.EYE_HULU.getRightUrl())
                    .with(new HealthCompoent(MonsterType.EYE_HULU.getHp()))
                    .with(new CollidableComponent(true))
                    .with(new WaterBallAttack())
                    .with(new IdentityComponent(flag))
                    .with(new CellMoveComponent(Config.CELL_WIDTH, Config.CELL_HEIGHT, MonsterType.EYE_HULU.getSpeed()))
                    .with(new AStarMoveComponent(new LazyValue<>(() -> geto("grid"))))
                    .with(new SensorComponent(150.0))
                    .with(new StateComponent())
                    .with(new MovableMonsterAIComponent())
                    .build();

        e.setPosition(data.getX() - e.getWidth()/2, data.getY() - e.getY() - e.getHeight()/2);
        return e;
    }

    @Spawns("WaterHulu")
    public Entity newWaterHulu(SpawnData data) {
        boolean flag = data.get("flag");
        Entity e = FXGL.entityBuilder()
                    .type(MonsterType.WATER_HULU)
                    .viewWithBBox(MonsterType.WATER_HULU.getRightUrl())
                    .with(new HealthCompoent(MonsterType.WATER_HULU.getHp()))
                    .with(new CollidableComponent(true))
                    .with(new WaterBallAttack())
                    .with(new IdentityComponent(flag))
                    .with(new CellMoveComponent(Config.CELL_WIDTH, Config.CELL_HEIGHT, MonsterType.WATER_HULU.getSpeed()))
                    .with(new AStarMoveComponent(new LazyValue<>(() -> geto("grid"))))
                    .with(new SensorComponent(150.0))
                    .with(new StateComponent())
                    .with(new MovableMonsterAIComponent())
                    .build();

        e.setPosition(data.getX() - e.getWidth()/2, data.getY() - e.getY() - e.getHeight()/2);
        return e;
    }

    @Spawns("IronHulu")
    public Entity newIronHulu(SpawnData data) {
        boolean flag = data.get("flag");
        Entity e = FXGL.entityBuilder()
                    .type(MonsterType.IRON_HULU)
                    .viewWithBBox(MonsterType.IRON_HULU.getRightUrl())
                    .with(new HealthCompoent(MonsterType.IRON_HULU.getHp()))
                    .with(new CollidableComponent(true))
                    .with(new ArrowAttack())
                    .with(new IdentityComponent(flag))
                    .with(new CellMoveComponent(Config.CELL_WIDTH, Config.CELL_HEIGHT, MonsterType.IRON_HULU.getSpeed()))
                    .with(new AStarMoveComponent(new LazyValue<>(() -> geto("grid"))))
                    .with(new SensorComponent(150.0))
                    .with(new StateComponent())
                    .with(new MovableMonsterAIComponent())
                    .build();

        e.setPosition(data.getX() - e.getWidth()/2, data.getY() - e.getY() - e.getHeight()/2);
        return e;
    }

    @Spawns("StealthHulu")
    public Entity newStealthHulu(SpawnData data) {
        boolean flag = data.get("flag");
        Entity e = FXGL.entityBuilder()
                    .type(MonsterType.STEALTH_HULU)
                    .viewWithBBox(MonsterType.STEALTH_HULU.getRightUrl())
                    .with(new HealthCompoent(MonsterType.STEALTH_HULU.getHp()))
                    .with(new CollidableComponent(true))
                    .with(new ArrowAttack())
                    .with(new IdentityComponent(flag))
                    .with(new CellMoveComponent(Config.CELL_WIDTH, Config.CELL_HEIGHT, MonsterType.STEALTH_HULU.getSpeed()))
                    .with(new AStarMoveComponent(new LazyValue<>(() -> geto("grid"))))
                    .with(new SensorComponent(150.0))
                    .with(new StateComponent())
                    .with(new MovableMonsterAIComponent())
                    .build();

        e.setPosition(data.getX() - e.getWidth()/2, data.getY() - e.getY() - e.getHeight()/2);
        return e;
    }

    @Spawns("HuluHulu")
    public Entity newHuluHulu(SpawnData data) {
        boolean flag = data.get("flag");
        Entity e = FXGL.entityBuilder()
                    .type(MonsterType.HULU_HULU)
                    .viewWithBBox(MonsterType.HULU_HULU.getRightUrl())
                    .with(new HealthCompoent(MonsterType.HULU_HULU.getHp()))
                    .with(new CollidableComponent(true))
                    .with(new VenomBallAttack())
                    .with(new IdentityComponent(flag))
                    .with(new CellMoveComponent(Config.CELL_WIDTH, Config.CELL_HEIGHT, MonsterType.HULU_HULU.getSpeed()))
                    .with(new AStarMoveComponent(new LazyValue<>(() -> geto("grid"))))
                    .with(new SensorComponent(150.0))
                    .with(new StateComponent())
                    .with(new MovableMonsterAIComponent())
                    .build();

        e.setPosition(data.getX() - e.getWidth()/2, data.getY() - e.getY() - e.getHeight()/2);
        return e;
    }

    @Spawns("NTR")
    public Entity newNTR(SpawnData data) {
        boolean flag = data.get("flag");
        Entity e = FXGL.entityBuilder()
                    .type(MonsterType.NTR)
                    .viewWithBBox(MonsterType.NTR.getRightUrl())
                    .with(new HealthCompoent(MonsterType.NTR.getHp()))
                    .with(new CollidableComponent(true))
                    .with(new FireBallAttack())
                    .with(new IdentityComponent(flag))
                    .with(new CellMoveComponent(Config.CELL_WIDTH, Config.CELL_HEIGHT, MonsterType.NTR.getSpeed()))
                    .with(new AStarMoveComponent(new LazyValue<>(() -> geto("grid"))))
                    .with(new SensorComponent(150.0))
                    .with(new StateComponent())
                    .with(new MovableMonsterAIComponent())
                    .build();

        e.setPosition(data.getX() - e.getWidth()/2, data.getY() - e.getY() - e.getHeight()/2);
        return e;
    }

    @Spawns("Snake")
    public Entity newSnake(SpawnData data) {
        boolean flag = data.get("flag");
        Entity e = FXGL.entityBuilder()
                    .type(MonsterType.SNAKE)
                    .viewWithBBox(MonsterType.SNAKE.getRightUrl())
                    .with(new HealthCompoent(MonsterType.SNAKE.getHp()))
                    .with(new CollidableComponent(true))
                    .with(new VenomBallAttack())
                    .with(new IdentityComponent(flag))
                    .with(new CellMoveComponent(Config.CELL_WIDTH, Config.CELL_HEIGHT, MonsterType.SNAKE.getSpeed()))
                    .with(new AStarMoveComponent(new LazyValue<>(() -> geto("grid"))))
                    .with(new SensorComponent(150.0))
                    .with(new StateComponent())
                    .with(new MovableMonsterAIComponent())
                    .build();

        e.setPosition(data.getX() - e.getWidth()/2, data.getY() - e.getY() - e.getHeight()/2);
        return e;
    }

    // @Spawns("Centipede")
    // public Entity newCentipede(SpawnData data) {
    //     boolean flag = data.get("flag");
    //     Entity e = FXGL.entityBuilder()
    //                 .type(MonsterType.CENTIPEDE)
    //                 .viewWithBBox(MonsterType.CENTIPEDE.getRightUrl())
    //                 .with(new HealthCompoent(MonsterType.CENTIPEDE.getHp()))
    //                 .with(new CollidableComponent(true))
    //                 .with(new ArrowBallAttack())
    //                 .with(new IdentityComponent(flag))
    //                 .with(new CellMoveComponent(Config.CELL_WIDTH, Config.CELL_HEIGHT, MonsterType.CENTIPEDE.getSpeed()))
    //                 .with(new AStarMoveComponent(new LazyValue<>(() -> geto("grid"))))
    //                 .with(new SensorComponent(150.0))
    //                 .with(new StateComponent())
    //                 .with(new MovableMonsterAIComponent())
    //                 .build();

    //     e.setPosition(data.getX() - e.getWidth()/2, data.getY() - e.getY() - e.getHeight()/2);
    //     return e;
    // }

    // @Spawns("Spider")
    // public Entity newSpider(SpawnData data) {
    //     boolean flag = data.get("flag");
    //     Entity e = FXGL.entityBuilder()
    //                 .type(MonsterType.SPIDER)
    //                 .viewWithBBox(MonsterType.SPIDER.getRightUrl())
    //                 .with(new HealthCompoent(MonsterType.SPIDER.getHp()))
    //                 .with(new CollidableComponent(true))
    //                 .with(new VenomBallAttack())
    //                 .with(new IdentityComponent(flag))
    //                 .with(new CellMoveComponent(Config.CELL_WIDTH, Config.CELL_HEIGHT, MonsterType.SPIDER.getSpeed()))
    //                 .with(new AStarMoveComponent(new LazyValue<>(() -> geto("grid"))))
    //                 .with(new SensorComponent(150.0))
    //                 .with(new StateComponent())
    //                 .with(new MovableMonsterAIComponent())
    //                 .build();

    //     e.setPosition(data.getX() - e.getWidth()/2, data.getY() - e.getY() - e.getHeight()/2);
    //     return e;
    // }

    // @Spawns("Toad")
    // public Entity newToad(SpawnData data) {
    //     boolean flag = data.get("flag");
    //     Entity e = FXGL.entityBuilder()
    //                 .type(MonsterType.TOAD)
    //                 .viewWithBBox(MonsterType.TOAD.getRightUrl())
    //                 .with(new HealthCompoent(MonsterType.TOAD.getHp()))
    //                 .with(new CollidableComponent(true))
    //                 .with(new VenomBallAttack())
    //                 .with(new IdentityComponent(flag))
    //                 .with(new CellMoveComponent(Config.CELL_WIDTH, Config.CELL_HEIGHT, MonsterType.TOAD.getSpeed()))
    //                 .with(new AStarMoveComponent(new LazyValue<>(() -> geto("grid"))))
    //                 .with(new SensorComponent(150.0))
    //                 .with(new StateComponent())
    //                 .with(new MovableMonsterAIComponent())
    //                 .build();

    //     e.setPosition(data.getX() - e.getWidth()/2, data.getY() - e.getY() - e.getHeight()/2);
    //     return e;
    // }

    // @Spawns("Wasp")
    // public Entity newWasp(SpawnData data) {
    //     boolean flag = data.get("flag");
    //     Entity e = FXGL.entityBuilder()
    //                 .type(MonsterType.WASP)
    //                 .viewWithBBox(MonsterType.WASP.getRightUrl())
    //                 .with(new HealthCompoent(MonsterType.WASP.getHp()))
    //                 .with(new CollidableComponent(true))
    //                 .with(new ArrowBallAttack())
    //                 .with(new IdentityComponent(flag))
    //                 .with(new CellMoveComponent(Config.CELL_WIDTH, Config.CELL_HEIGHT, MonsterType.WASP.getSpeed()))
    //                 .with(new AStarMoveComponent(new LazyValue<>(() -> geto("grid"))))
    //                 .with(new SensorComponent(150.0))
    //                 .with(new StateComponent())
    //                 .with(new MovableMonsterAIComponent())
    //                 .build();

    //     e.setPosition(data.getX() - e.getWidth()/2, data.getY() - e.getY() - e.getHeight()/2);
    //     return e;
    // }

    // @Spawns("WildBoar")
    // public Entity newWildBoar(SpawnData data) {
    //     boolean flag = data.get("flag");
    //     Entity e = FXGL.entityBuilder()
    //                 .type(MonsterType.WILD_BOAR)
    //                 .viewWithBBox(MonsterType.WILD_BOAR.getRightUrl())
    //                 .with(new HealthCompoent(MonsterType.WILD_BOAR.getHp()))
    //                 .with(new CollidableComponent(true))
    //                 .with(new WaterBallAttack())
    //                 .with(new IdentityComponent(flag))
    //                 .with(new CellMoveComponent(Config.CELL_WIDTH, Config.CELL_HEIGHT, MonsterType.WILD_BOAR.getSpeed()))
    //                 .with(new AStarMoveComponent(new LazyValue<>(() -> geto("grid"))))
    //                 .with(new SensorComponent(150.0))
    //                 .with(new StateComponent())
    //                 .with(new MovableMonsterAIComponent())
    //                 .build();

    //     e.setPosition(data.getX() - e.getWidth()/2, data.getY() - e.getY() - e.getHeight()/2);
    //     return e;
    // }

    @Spawns("FakeMonster")
    public Entity newFakeMonster(SpawnData data) {

        Entity monster = FXGL.entityBuilder()
                    .type(HuluCRType.MONSTER)
                    .viewWithBBox(((MonsterType)data.get("type")).getRightUrl())
                    .build();

        monster.setPosition(data.getX() - monster.getWidth()/2, data.getY() - monster.getY() - monster.getHeight()/2);
        return monster;
    }

    @Spawns("Tower")
    public Entity newTower(SpawnData data) {
        boolean flag = data.get("flag");
        return entityBuilder(data)
                .type(MonsterType.GRANDFATHER)
                .viewWithBBox(MonsterType.GRANDFATHER.getRightUrl())
                .with(new CollidableComponent(true))
                .with(new FireBallAttack())
                .with(new HealthCompoent(MonsterType.GRANDFATHER.getHp()))
                .with(new IdentityComponent(flag))
                .with(new SensorComponent(MonsterType.GRANDFATHER.getSensor()))
                .with(new StateComponent())
                .with(new UnmovableMonsterAIComponent())
                .scale(1.5, 1.5)
                .build();
    }

    @Spawns("hpBar")
    public Entity newHpBar(SpawnData data) {
        return FXGL.entityBuilder(data)
                .view(new Rectangle(60, 10, Color.RED))
                .view(new Rectangle(60, 10, Color.GREEN))
                .zIndex(10)
                .build();
    }
}
