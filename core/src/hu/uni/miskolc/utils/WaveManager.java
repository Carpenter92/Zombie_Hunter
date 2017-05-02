package hu.uni.miskolc.utils;

import com.badlogic.gdx.utils.Array;

import java.util.Random;

public class WaveManager {

    private static final int WAVE_DIFFICULTY = 800;
    private static final float DIFFICULTY_MODIFIER = 1.3f;
    private static final int BOSS_WAVES = 5;

    private static final int MAX_ZOMBIES = 25;
    private static final int MAX_STANDARD_ZOMBIES = 10;
    private static final int MAX_MUMMY_ZOMBIES = 10;
    private static final int MAX_ARMORED_ZOMBIES = 10;
    private static final int MAX_FAST_ZOMBIES = 10;
    private static final int MAX_HEAVY_ZOMBIES = 5;

    private static final int MIN_ZOMBIES = 7;
    private static final int MIN_STANDARD_ZOMBIES = 4;
    private static final int MIN_MUMMY_ZOMBIES = 1;
    private static final int MIN_ARMORED_ZOMBIES = 1;
    private static final int MIN_FAST_ZOMBIES = 0;
    private static final int MIN_HEAVY_ZOMBIES = 0;

    private int wave;
    private int playerMoney;
    private Array<ZombieTypes> zombies;

    public WaveManager(int wave, int playerMoney) {
        this.wave = wave;
        this.playerMoney = playerMoney;
        zombies = new Array<ZombieTypes>();
    }

    private void generateWave() {
        Random random = new Random(System.currentTimeMillis());
        int playerMoneyStacked = playerMoney / 200;
        double actualWaveDifficulty = (Math.pow(DIFFICULTY_MODIFIER, wave)
                * WAVE_DIFFICULTY) + playerMoneyStacked * 100;
        int currentWaveDifficulty = 0;

        int standardZombies = random.nextInt(MAX_STANDARD_ZOMBIES -
                MIN_STANDARD_ZOMBIES) + MIN_STANDARD_ZOMBIES;
        int mummyZombies = random.nextInt(MAX_MUMMY_ZOMBIES -
                MIN_MUMMY_ZOMBIES) + MIN_MUMMY_ZOMBIES;
        int armoredZombies = random.nextInt(MAX_ARMORED_ZOMBIES -
                MIN_ARMORED_ZOMBIES) + MIN_ARMORED_ZOMBIES;
        int fastZombies = random.nextInt(MAX_FAST_ZOMBIES -
                MIN_FAST_ZOMBIES) + MIN_FAST_ZOMBIES;
        int heavyZombies = random.nextInt(MAX_HEAVY_ZOMBIES -
                MIN_HEAVY_ZOMBIES) + MIN_HEAVY_ZOMBIES;
        System.out.println(standardZombies + " " + mummyZombies + " " + armoredZombies + " " + heavyZombies);
        System.out.println(actualWaveDifficulty);
        while (currentWaveDifficulty < actualWaveDifficulty && standardZombies > 0) {
            zombies.add(ZombieTypes.STANDARD);
            standardZombies--;
            currentWaveDifficulty += 100;
        }
        while (currentWaveDifficulty < actualWaveDifficulty && mummyZombies > 0) {
            zombies.add(ZombieTypes.MUMMY);
            mummyZombies--;
            currentWaveDifficulty += 120;
        }
        while (currentWaveDifficulty < actualWaveDifficulty && armoredZombies > 0) {
            zombies.add(ZombieTypes.ARMORED);
            armoredZombies--;
            currentWaveDifficulty += 150;
        }
        while (currentWaveDifficulty < actualWaveDifficulty && heavyZombies > 0) {
            zombies.add(ZombieTypes.ARMORED);
            heavyZombies--;
            currentWaveDifficulty += 300;
        }
        System.out.println(currentWaveDifficulty);
        System.out.println(zombies);
    }

    public Array<ZombieTypes> getWave() {
        zombies.clear();
        wave++;
        generateWave();
        zombies.reverse();
        return zombies;
    }
}
