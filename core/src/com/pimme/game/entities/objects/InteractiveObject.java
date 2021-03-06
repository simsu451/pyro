package com.pimme.game.entities.objects;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.pimme.game.PyroGame;
import com.pimme.game.screens.PlayScreen;
import com.pimme.game.tools.Manager;

public abstract class InteractiveObject
{
	protected World world;
	protected TiledMap map;
	protected Rectangle bounds;
	protected Body body;
	protected Fixture fixture;
	protected PlayScreen screen;
	protected Manager manager;
	protected InteractiveObject(PlayScreen screen, MapObject object) {
		this.screen = screen;
		this.world = screen.getWorld();
		this.map = screen.getMap();
		this.bounds = ((RectangleMapObject) object).getRectangle();
		this.manager = screen.getGame().getManager();

		defineObject();
	}

	private void defineObject() {
		BodyDef bdef = new BodyDef();
		FixtureDef fdef = new FixtureDef();
		PolygonShape shape = new PolygonShape();

		bdef.type = BodyType.StaticBody;
		bdef.position.set((bounds.getX() + bounds.getWidth() / 2) / PyroGame.PPM, (bounds.getY() + bounds.getHeight() / 2) / PyroGame.PPM);

		body = world.createBody(bdef);

		shape.setAsBox(bounds.getWidth() / 2 / PyroGame.PPM, bounds.getHeight() / 2 / PyroGame.PPM); // start at x and goes all directions
		fdef.shape = shape;
		fdef.isSensor = true;
		fixture = body.createFixture(fdef);
	}

	public abstract void onCollision();

	public void setCategoryFilter(short filterBit) {
		Filter filter = new Filter();
		filter.categoryBits = filterBit;
		fixture.setFilterData(filter);
	}

	public Cell getCell() {
		TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(1);
		float tileSize = layer.getTileWidth();
		return layer.getCell((int)(body.getPosition().x * PyroGame.PPM / tileSize),
				(int)(body.getPosition().y * PyroGame.PPM / tileSize));
	}

}
