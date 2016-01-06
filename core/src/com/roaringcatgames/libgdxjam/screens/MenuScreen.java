package com.roaringcatgames.libgdxjam.screens;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.roaringcatgames.kitten2d.ashley.systems.*;
import com.roaringcatgames.libgdxjam.App;
import com.roaringcatgames.libgdxjam.systems.CleanUpSystem;
import com.roaringcatgames.libgdxjam.systems.FiringSystem;
import com.roaringcatgames.libgdxjam.systems.PlayerSystem;
import com.roaringcatgames.libgdxjam.systems.RemainInBoundsSystem;

/**
 * Created by barry on 12/22/15 @ 5:51 PM.
 */
public class MenuScreen extends LazyInitScreen implements InputProcessor {

    private IScreenDispatcher dispatcher;
    private SpriteBatch batch;
    private PooledEngine engine;
    private OrthographicCamera cam;
    private Vector3 touchPoint;
    private Viewport viewport;

    private Entity ball;

    public MenuScreen(SpriteBatch batch, IScreenDispatcher dispatcher) {
        super();
        this.batch = batch;
        this.dispatcher = dispatcher;
        this.touchPoint = new Vector3();
    }


    @Override
    protected void init() {
        engine = new PooledEngine();

        RenderingSystem renderingSystem = new RenderingSystem(batch, App.PPM);
        cam = renderingSystem.getCamera();
        viewport = new ExtendViewport(20f, 30f, 40f, 60f, cam);// FitViewport(20f, 30f, cam);
        viewport.apply();
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(cam.viewportWidth/2f, cam.viewportHeight/2f, 0);

        Vector3 playerPosition = new Vector3(
                cam.position.x,
                5f,
                0f);

        Gdx.app.log("Menu Screen", "Cam Pos: " + cam.position.x + " | " +
                cam.position.y + " Cam W/H: " + cam.viewportWidth + "/" + cam.viewportHeight);


        //AshleyExtensions Systems
        engine.addSystem(new MovementSystem());
        engine.addSystem(new RotationSystem());
        engine.addSystem(new BoundsSystem());
        engine.addSystem(new AnimationSystem());

        //Custom Systems
        Vector2 minBounds = new Vector2(0f, 0f);
        Vector2 maxBounds = new Vector2(cam.viewportWidth, cam.viewportHeight);
        engine.addSystem(new PlayerSystem(playerPosition, 1f, cam));
        engine.addSystem(new FiringSystem());
        engine.addSystem(new CleanUpSystem(minBounds, maxBounds));
        engine.addSystem(new RemainInBoundsSystem(minBounds, maxBounds));
        //Extension Systems
        engine.addSystem(renderingSystem);
        //engine.addSystem(new GravitySystem(new Vector2(0f, -9.8f)));
        engine.addSystem(new DebugSystem(renderingSystem.getCamera(), Color.CYAN, Color.PINK, Input.Keys.TAB));
        App.game.multiplexer.addProcessor(this);
    }

    /**************************
     * Screen Adapter Methods
     **************************/
    @Override
    protected void update(float deltaChange) {



        engine.update(Math.min(deltaChange, App.MAX_DELTA_TICK));

//        if(ball.getComponent(TransformComponent.class).position.y <= 1f){
//            ball.getComponent(VelocityComponent.class).setSpeed(0f, 20f);
//        }
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        if(viewport != null) {
            viewport.update(width, height);
            cam.position.set(cam.viewportWidth / 2, cam.viewportHeight / 2, 0);
        }
    }
    /**************************
     * Input Processor Methods
     **************************/

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }


}
