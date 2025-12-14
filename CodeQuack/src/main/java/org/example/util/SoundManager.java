package org.example.util;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;

public class SoundManager {

    public static void playQuack() {
        new Thread(() -> {
            try {
                InputStream audioSrc = SoundManager.class.getResourceAsStream("/quack.wav");

                if (audioSrc == null) {
                    System.err.println("Didn't find the quack :(");
                    return;
                }

                InputStream bufferedIn = new BufferedInputStream(audioSrc);
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);

                Clip clip = AudioSystem.getClip();
                clip.open(audioStream);

                clip.start();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}