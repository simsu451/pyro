package com.pimme.game.entities;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;

public class FlyPowerup extends InteractiveObject
{
    public FlyPowerup(final World world, final TiledMap map, final Rectangle bounds)
    {
	super(world, map, bounds);
	fixture.setUserData(this);
    }

    @Override public void onCollision() {

    }
}
