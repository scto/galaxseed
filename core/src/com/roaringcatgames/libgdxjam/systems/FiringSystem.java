package com.roaringcatgames.libgdxjam.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.utils.Array;
import com.roaringcatgames.kitten2d.ashley.components.*;
import com.roaringcatgames.libgdxjam.Assets;
import com.roaringcatgames.libgdxjam.components.*;
import com.roaringcatgames.libgdxjam.values.Damage;
import com.roaringcatgames.libgdxjam.values.Rates;
import com.roaringcatgames.libgdxjam.values.Volume;
import com.roaringcatgames.libgdxjam.values.Z;

/**
 * Created by barry on 1/3/16 @ 1:14 AM.
 */
public class FiringSystem extends IteratingSystem {

    private float lastFireTime = 0f;
    private float timeElapsed = 0f;
    private float bulletSpeed = 20f;
    private Music firingMusic;
    private ComponentMapper<TransformComponent> tm;
    private ComponentMapper<GunComponent> mm;
    private ComponentMapper<StateComponent> sm;
    private ComponentMapper<AnimationComponent> am;

    Array<Entity> muzzles = new Array<>();
    Entity player;

    public FiringSystem(){
        super(Family.one(PlayerComponent.class, GunComponent.class).get());
        tm = ComponentMapper.getFor(TransformComponent.class);
        mm = ComponentMapper.getFor(GunComponent.class);
        sm = ComponentMapper.getFor(StateComponent.class);
        am = ComponentMapper.getFor(AnimationComponent.class);
        firingMusic = Assets.getFiringMusic();
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if(player != null) {
            StateComponent sc = player.getComponent(StateComponent.class);
            if(!firingMusic.isPlaying()){
                firingMusic.setVolume(Volume.FIRING_MUSIC);
                firingMusic.setLooping(true);
                firingMusic.play();
            }

            if(sc.get() != "DEFAULT") {
                timeElapsed += deltaTime;

                if (timeElapsed - lastFireTime >= Rates.timeBetweenShots) {

                    lastFireTime = timeElapsed;

                    for(Entity m:muzzles){
                        StateComponent mState = sm.get(m);
                        mState.set("FIRING");
                        FollowerComponent follower = m.getComponent(FollowerComponent.class);
                        generateBullet(follower.offset.x, follower.offset.y, 0f, bulletSpeed);
                    }
                }
            }else{
                firingMusic.stop();
                for(Entity m:muzzles){
                    StateComponent mState = sm.get(m);
                    if(mState.get() != "DEFAULT") {
                        mState.set("DEFAULT");
                        m.getComponent(TextureComponent.class).setRegion(null);
                    }
                }
            }
        }

        muzzles.clear();
        player = null;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

        if(mm.has(entity)){
            muzzles.add(entity);
        }else {
            if (player != null) {
                throw new IllegalStateException("Cannot handle Two Players right now!");
            }
            player = entity;
        }
    }


    private void generateBullet(float xOffset, float yOffset, float xVel, float yVel){
        PooledEngine engine = (PooledEngine)getEngine();
        TransformComponent playerPos = tm.get(player);
        //Generate Bullets here
        Entity bullet = engine.createEntity();
        bullet.add(WhenOffScreenComponent.create(engine));
        bullet.add(KinematicComponent.create(engine));
        bullet.add(TransformComponent.create(engine)
                .setPosition(playerPos.position.x + xOffset, playerPos.position.y + yOffset, Z.seed)
                .setScale(0.5f, 0.5f));
        bullet.add(CircleBoundsComponent.create(engine)
            .setCircle(playerPos.position.x + xOffset, playerPos.position.y + yOffset, 0.125f)
            .setOffset(0f, 0.25f));
        bullet.add(TextureComponent.create(engine));
        bullet.add(DamageComponent.create(engine)
            .setDPS(Damage.seed));
        bullet.add(AnimationComponent.create(engine)
                .addAnimation("DEFAULT", new Animation(1f / 6f, Assets.getBulletFrames(), Animation.PlayMode.NORMAL))
                .addAnimation("FLYING", new Animation(1f / 6f, Assets.getBulletFlyingFrames(), Animation.PlayMode.NORMAL)));
        bullet.add(StateComponent.create(engine)
                .set("DEFAULT")
                .setLooping(false));
        bullet.add(BulletComponent.create((PooledEngine)getEngine()));
        bullet.add(VelocityComponent.create(engine)
                .setSpeed(xVel, yVel));
        getEngine().addEntity(bullet);
    }
}
