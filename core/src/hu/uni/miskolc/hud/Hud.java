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
    private int timeLeft;
    private int money;

    private Label waveLabel;
    private Label timeLeftLabel;
    private Label moneyLabel;

    public Hud(SpriteBatch batch)   {
        wave = 1;
        timeLeft = 60;
        money = 100;

        viewPort = new FitViewport(ZombieGame.WIDTH, ZombieGame.HEIGHT, new OrthographicCamera());
        stage = new Stage(viewPort, batch);

        Table table = new Table();
        table.top();
        table.setFillParent(true);

        waveLabel = new Label(String.format("%01d", wave), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        timeLeftLabel = new Label(String.format("%02d", timeLeft), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        moneyLabel = new Label(String.format("%03d", money), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        waveLabel.setFontScale(2.0f);
        timeLeftLabel.setFontScale(2.0f);
        moneyLabel.setFontScale(2.0f);

        table.add(waveLabel).expandX();
        table.add(timeLeftLabel).expandX();
        table.add(moneyLabel).expandX();

        stage.addActor(table);
    }

    public void update(float delta)    {
        moneyLabel.setText(money+"");
        timeLeftLabel.setText(timeLeft+"");
        waveLabel.setText(wave+"");
    }

    public int getWave() {
        return wave;
    }

    public void setWave(int wave) {
        this.wave = wave;
    }

    public int getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(int timeLeft) {
        this.timeLeft = timeLeft;
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
