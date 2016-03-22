package com.roaringcatgames.libgdxjam.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Created by barry on 3/13/16 @ 2:37 PM.
 */
public class ParticleEmitterComponent implements Component {

    public Array<TextureRegion> particleImages = new Array<>();
    public float elapsedTime = 0f;
    public float lastSpawnTime = 0f;
    public float spawnRate = 100f;
    public float duration = 1f;
    public float particleSpeed = 1f;
    public Vector2 particleMinMaxLifespans = new Vector2(1f, 1f);
    public Range angleRange = new Range(-45f, 45f);
    public boolean shouldFade = false;
    public boolean isLooping = false;

    public static ParticleEmitterComponent create(){
        return new ParticleEmitterComponent();
    }

    public ParticleEmitterComponent setParticleImages(Array<? extends TextureRegion> particleImages){
        this.particleImages.clear();
        this.particleImages.addAll(particleImages);
        return this;
    }

    public ParticleEmitterComponent setSpawnRate(float spawnRate){
        this.spawnRate = spawnRate;
        return this;
    }

    public ParticleEmitterComponent setParticleLifespans(float minLifespan, float maxLifespan){
        this.particleMinMaxLifespans.set(minLifespan, maxLifespan);
        return this;
    }

    public ParticleEmitterComponent setAngleRange(float min, float max){
        this.angleRange.setMin(min);
        this.angleRange.setMax(max);
        return this;
    }

    public ParticleEmitterComponent setShouldFade(boolean isFading){
        this.shouldFade = isFading;
        return this;
    }

    public ParticleEmitterComponent setShouldLoop(boolean shouldLoop){
        this.isLooping = shouldLoop;
        return this;
    }

    public ParticleEmitterComponent setDuration(float duration){
        this.duration = duration;
        return this;
    }

    public ParticleEmitterComponent setSpeed(float speed){
        this.particleSpeed = speed;
        return this;
    }
}
