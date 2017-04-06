package hu.uni.miskolc.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import hu.uni.miskolc.ZombieGame;

public class Hud implements Disposable {

    private Stage stage;
    private Viewport viewPort;

    private int wave;
    private int livesLeft;
    private int money;

    private Label waveLabel;
    private Label livesLeftLabel;
    private Label moneyLabel;

    public Hud(SpriteBatch batch)   {
        wave = 1;
        livesLeft = 10;
        money = 100;

        viewPort = new FitViewport(ZombieGame.WIDTH, ZombieGame.HEIGHT, new OrthographicCamera());
        stage = new Stage(viewPort, batch);

        Table table = new Table();
        table.top();
        table.setFillParent(true);

        waveLabel = new Label(String.format("%01d", wave), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        livesLeftLabel = new Label(String.format("%02d", livesLeft), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        moneyLabel = new Label(String.format("%03d", money), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        waveLabel.setFontScale(2.0f);
        livesLeftLabel.setFontScale(2.0f);
        moneyLabel.setFontScale(2.0f);

        table.add(waveLabel).expandX();
        table.add(livesLeftLabel).expandX();
        table.add(moneyLabel).expandX();

        stage.addActor(table);
    }

    public void update(float delta)    {
        moneyLabel.setText(money+"");
        livesLeftLabel.setText(livesLeft + "");
        waveLabel.setText(wave+"");
    }

    public int getWave() {
        return wave;
    }

    public void setWave(int wave) {
        this.wave = wave;
    }

    public int getLivesLeft() {
        return livesLeft;
    }

    public void decreaseLives() {
        livesLeft--;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public Stage getStage() {
        return stage;
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
