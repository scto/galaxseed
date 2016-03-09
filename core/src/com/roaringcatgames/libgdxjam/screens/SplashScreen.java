package com.roaringcatgames.libgdxjam.screens;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.roaringcatgames.kitten2d.ashley.components.*;
import com.roaringcatgames.kitten2d.ashley.systems.*;
import com.roaringcatgames.libgdxjam.App;
import com.roaringcatgames.libgdxjam.Assets;
import com.roaringcatgames.libgdxjam.systems.BackgroundSystem;
import com.roaringcatgames.libgdxjam.systems.ScreenWrapSystem;
import com.roaringcatgames.libgdxjam.values.Z;

/**
 * Created by barry on 12/22/15 @ 7:27 PM.
 */
public class SplashScreen extends LazyInitScreen {
    SpriteBatch batch;
    private IScreenDispatcher dispatcher;
    private PooledEngine engine;

    private float minSplashSuggestions = 6f;
    private float elapsedTime = 0f;
    private OrthographicCamera cam;
    private Viewport viewport;

    public SplashScreen(SpriteBatch batch, IScreenDispatcher dispatcher){
        this.batch = batch;
        this.dispatcher = dispatcher;
    }

    @Override
    protected void init() {

        engine = new PooledEngine();
        RenderingSystem render = new RenderingSystem(batch, App.PPM);
        cam = render.getCamera();
        viewport = new FitViewport(20f, 30f, cam);
        viewport.apply();
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(cam.viewportWidth/2f, cam.viewportHeight/2f, 0);

        engine.addSystem(new AnimationSystem());
        engine.addSystem(new BoundsSystem());
        engine.addSystem(render);
        Vector2 minBounds = new Vector2(0f, 0f);
        Vector2 maxBounds = new Vector2(cam.viewportWidth, cam.viewportHeight);
        engine.addSystem(new ScreenWrapSystem(minBounds, maxBounds, App.PPM));
        engine.addSystem(new BackgroundSystem(minBounds, maxBounds, false));
        engine.addSystem(new MovementSystem());
        engine.addSystem(new RotationSystem());

        Entity title = engine.createEntity();
        title.add(TransformComponent.create()
                .setPosition(App.W / 2f, 15f, Z.gameOver)
                .setScale(1f, 1f));
        title.add(TextureComponent.create()
                .setRegion(Assets.getSplashTitle()));
        engine.addEntity(title);

        Entity loading = engine.createEntity();
        loading.add(VelocityComponent.create()
            .setSpeed(-2.5f, 0f));
        loading.add(RotationComponent.create()
            .setRotationSpeed(45f));
        loading.add(TransformComponent.create()
                .setPosition(16f, 8f, 0f)
                .setScale(0.5f, 0.5f));
        loading.add(TextureComponent.create());
        loading.add(StateComponent.create()
                .set("DEFAULT")
                .setLooping(true));
        loading.add(AnimationComponent.create()
                .addAnimation("DEFAULT", new Animation(1f / 30f, Assets.getLoadingFrames(), Animation.PlayMode.LOOP_PINGPONG)));

        engine.addEntity(loading);
    }

    @Override
    protected void update(float delta) {
        elapsedTime += delta;

        if(Assets.am.update() && elapsedTime >= minSplashSuggestions){
            Gdx.app.log("Splash Screen", "Assets are Loaded!");
            dispatcher.endCurrentScreen();
        }else {
            engine.update(delta);
        }
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        if(viewport != null) {
            viewport.update(width, height);
        }
    }
}
