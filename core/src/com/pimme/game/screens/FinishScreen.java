package com.pimme.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pimme.game.PyroGame;
import com.pimme.game.tools.Highscore;
import com.pimme.game.tools.Manager;
import com.pimme.game.tools.Manager.Level;
import com.pimme.game.tools.Utils;

public class FinishScreen implements Screen
{
	private PyroGame game;
	private Manager manager;
	private Viewport viewPort;
	private Stage stage;
	private Table table;

	private TextButton playAgainButton;
	private TextButton menuButton;
	private TextButton exitButton;
	private TextButton nextButton;

	public FinishScreen(final PyroGame game, int score) {
		this.game = game;
		manager = game.getManager();

		viewPort = new FitViewport(PyroGame.V_WIDTH, PyroGame.V_HEIGHT, new OrthographicCamera());
		stage = new Stage(viewPort, game.batch);
		Gdx.input.setInputProcessor(stage);

		table = new Table();
		table.setFillParent(true);
		stage.addActor(table);
		if (manager.getCompletedLevels() != null && manager.getCompletedLevels().size == 4) {// All level finished
			score = manager.getTotalHighscore();
		}

		initButtons();
		setLevelSpecific();
		if (score < 0) score = 0;
		table.add(menuButton).colspan(3).row();
		table.add(exitButton).colspan(3).row();
		table.add(new Label("Highscores:      ", Utils.skin));
		table.add(new Label("Score:   ", Utils.skin));
		table.add(new Label(Integer.toString(score), Utils.skin.get("green", LabelStyle.class))).left().row();
		boolean highLighted = false;
		for (int i = 0; i < Highscore.MAX_SCORES; i ++) {
			if (score == manager.getHighScore(i) && score > 0 && !highLighted) {
				table.add(new Label(Integer.toString(i + 1) + ".         " + Integer.toString(manager.getHighScore(i)), Utils.skin.get("green", LabelStyle.class))).row();
				highLighted = true; // Only allow 1 score to get highlighted
			}
			else table.add(new Label(Integer.toString(i + 1) + ".         " + Integer.toString(manager.getHighScore(i)), Utils.skin)).row();
		}
	}

	private void setLevelSpecific() {
		Label finishLabel;
		if (manager.getCompletedLevels() == null) { // select level play
			finishLabel = new Label("Wow. You made it!", Utils.skin);
			table.add(finishLabel).colspan(3).row();
			table.add(playAgainButton).colspan(3).row();
		}
		else if (manager.getCompletedLevels().size != 4) { // story mode
			finishLabel = new Label(Integer.toString((manager.getCompletedLevels().size)) + " / " + 4, Utils.skin);
			table.add(finishLabel).colspan(3).row();
			table.add(nextButton).colspan(3).row();
		} else {
			finishLabel = new Label("Rockstar", Utils.skin); // All level finished
			table.add(finishLabel).colspan(3).row();
		}
		finishLabel.setFontScale(1.5f);
	}

	private void initButtons() {
		playAgainButton = new TextButton("Play again", Utils.skin);
		menuButton = new TextButton("Menu", Utils.skin);
		exitButton = new TextButton("Exit", Utils.skin);
		nextButton = new TextButton("Next level", Utils.skin);

		playAgainButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent e, float x, float y) {
				game.setScreen(new PlayScreen(game));
				dispose();
			}
			@Override public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
				playAgainButton.setStyle(Utils.skin.get("hover", TextButtonStyle.class));
			}
			@Override public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
				playAgainButton.setStyle(Utils.skin.get("default", TextButtonStyle.class));
			}
		});

		menuButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent e, float x, float y) {
				game.setScreen(new MenuScreen(game));
				dispose();
			}
			@Override public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
				menuButton.setStyle(Utils.skin.get("hover", TextButtonStyle.class));
			}
			@Override public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
				menuButton.setStyle(Utils.skin.get("default", TextButtonStyle.class));
			}
		});

		exitButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent e, float x, float y) {
				Gdx.app.exit();
			}
			@Override public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
				exitButton.setStyle(Utils.skin.get("hover", TextButtonStyle.class));
			}
			@Override public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
				exitButton.setStyle(Utils.skin.get("default", TextButtonStyle.class));
			}
		});
		nextButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent e, float x, float y) {
				switch (manager.getCompletedLevels().size) {
					case 1:
						manager.setCurrentLevel(Level.LEVEL2);
						break;
					case 2:
						manager.setCurrentLevel(Level.LEVEL3);
						break;
					case 3:
						manager.setCurrentLevel(Level.BOUNCE);
						break;
					default:
						nextButton = null;
						break;
				}
				game.setScreen(new PlayScreen(game));
			}
			@Override public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
				nextButton.setStyle(Utils.skin.get("hover", TextButtonStyle.class));
			}
			@Override public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
				nextButton.setStyle(Utils.skin.get("default", TextButtonStyle.class));
			}
		});
	}

	@Override public void show() {

	}

	@Override public void render(final float delta) {
		Gdx.gl.glClearColor(0,0,0,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act();
		stage.draw();
	}

	@Override public void resize(final int width, final int height) {
		viewPort.update(width, height, true);
	}

	@Override public void pause() {

	}

	@Override public void resume() {

	}

	@Override public void hide() {

	}

	@Override public void dispose() {
		stage.dispose();
	}
}
