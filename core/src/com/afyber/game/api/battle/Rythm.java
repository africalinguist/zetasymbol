package com.afyber.game.api.battle;

import com.afyber.game.ZetaSymbol;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.ArrayList;

public class Rythm {

    public boolean playing;
    public boolean previewing;

    public float beatsPerSec;
    public float secsPerBeat;

    public float posInSong;
    public int currentMeasureNum;
    public int currentBeatNum;
    public float deltaFromBeat;
    private int hitStatus;

    public int timeSigTop;

    public boolean usesEighths;
    // first list is measures
    // second list is beats
    // 0: no note
    // 1: "A" note
    // 2: "B" note
    // 3: "C" note
    public int[][] noteData;

    public int intrumentType = 0;

    Texture spriteSheet;
    TextureRegion note;
    Animation<TextureRegion> hitNoteAnim;
    float hitNoteAnimTime;
    Animation<TextureRegion> missNoteAnim;
    float missNoteAnimTime;

    // this is the only use of a list in the entire project and it's just because I want to be able to use add()
    // (aka because I'm lazy)
    // and I might even remove it for consistency
    public ArrayList<HitRating> ratings;

    public Rythm() {
        setupTextures();
    }

    public void draw(SpriteBatch batch) {
        for (int i = 0; i < 4; i++) {
            int dataToDraw = noteData[currentMeasureNum + (currentBeatNum + i >= 4 ? 1 : 0)][(currentBeatNum + i) % timeSigTop];
            if (dataToDraw > 0) {
                if (i == 0 && deltaFromBeat >= 0) {
                    if (hitStatus == 1 || previewing) {
                        batch.draw(hitNoteAnim.getKeyFrame(hitNoteAnimTime), 25f + (dataToDraw - 1) * 50, 60);
                        hitNoteAnimTime += Gdx.graphics.getDeltaTime();
                    }
                    else {
                        batch.draw(missNoteAnim.getKeyFrame(missNoteAnimTime), 25f + (dataToDraw - 1) * 50, 60);
                        missNoteAnimTime += Gdx.graphics.getDeltaTime();
                    }
                }
                else {
                    batch.draw(note, 28f + (dataToDraw - 1) * 50, 60 + ((i - deltaFromBeat) * 30f));
                }
            }
        }
    }

    public void update(float delta) {
        if (playing) {
            posInSong += delta;
            int currentBeat = currentBeatNum;
            currentBeatNum = Math.round((posInSong + ZetaSymbol.calibration) / secsPerBeat) % timeSigTop;
            currentMeasureNum = (int) Math.floor(posInSong / (secsPerBeat * timeSigTop));
            // the closer to 0 this is the closer to on-beat you are
            // hopefully finally stinking fixed this, now it should be a range from -0.5 to 0.5
            deltaFromBeat = ((posInSong + (secsPerBeat / 2) + ZetaSymbol.calibration) % secsPerBeat - (secsPerBeat / 2)) / secsPerBeat;

            if (currentBeat != currentBeatNum) {
                hitStatus = 0;
                hitNoteAnimTime = 0;
                missNoteAnimTime = 0;
            }

            if ((ZetaSymbol.input.isKeyDown(4) || ZetaSymbol.input.isKeyDown(5) || ZetaSymbol.input.isKeyDown(6)) && !previewing) {
                attemptHit();
            }
        }
        //System.out.println("beat: " + currentBeatNum + " delta: " + deltaFromBeat);
    }

    public void attemptHit() {
        if (hitStatus == 0) {
            hitStatus = 1;
            if (ZetaSymbol.input.isKeyDown(4) && !ZetaSymbol.input.isKeyDown(5) && !ZetaSymbol.input.isKeyDown(6)) {
                if (noteData[currentMeasureNum][currentBeatNum] == 1 && (intrumentType == 0 || intrumentType == 1)) {
                    gradeHit();
                }
            }
            else if (ZetaSymbol.input.isKeyDown(5) && !ZetaSymbol.input.isKeyDown(4) && !ZetaSymbol.input.isKeyDown(6)) {
                if (noteData[currentMeasureNum][currentBeatNum] == 2 && intrumentType == 1) {
                    gradeHit();
                }
                if (noteData[currentMeasureNum][currentBeatNum] == 3 && intrumentType == 0) {
                    gradeHit();
                }
            }
            else if (ZetaSymbol.input.isKeyDown(6) && !ZetaSymbol.input.isKeyDown(4) && !ZetaSymbol.input.isKeyDown(5)) {
                if (noteData[currentMeasureNum][currentBeatNum] == 3 && (intrumentType == 0 || intrumentType == 1)) {
                    gradeHit();
                }
            }
            else {
                System.out.println("Wrong note.");
            }
        }
    }

    private void gradeHit() {
        float gradeAbs = Math.abs(deltaFromBeat);

        if (gradeAbs < 0.03f) {
            System.out.println("Perfect!");
            ratings.add(HitRating.PERFECT);
        }
        else if (gradeAbs < 0.06f) {
            System.out.println("Excellent!");
            ratings.add(HitRating.EXCELLENT);
        }
        else if (gradeAbs < 0.1f) {
            System.out.println("Good!");
            ratings.add(HitRating.GOOD);
        }
        else if (gradeAbs < 0.16f) {
            System.out.println("Alright.");
            ratings.add(HitRating.ALRIGHT);
        }
        else {
            System.out.println("Miss.");
            ratings.add(HitRating.MISS);
            hitStatus = 2;
        }
        System.out.println(deltaFromBeat);
    }

    public void play() {
        posInSong = 0;
        ratings.clear();
        playing = true;
    }

    private void setupTextures() {
        spriteSheet = new Texture(Gdx.files.internal("battle spritesheet.png"));

        note = new TextureRegion(spriteSheet, 0, 0, 5, 5);

        TextureRegion[] hitFrames = new TextureRegion[]{
                new TextureRegion(spriteSheet, 0, 5, 11, 5),
                new TextureRegion(spriteSheet, 11, 5, 11, 5),
                new TextureRegion(spriteSheet, 22, 5, 11, 5),
                new TextureRegion(spriteSheet, 33, 5, 11, 5),
                new TextureRegion(spriteSheet, 44, 5, 11, 5)
        };
        hitNoteAnim = new Animation<>(0.075f, hitFrames);

        TextureRegion[] missFrames = new TextureRegion[]{
                new TextureRegion(spriteSheet, 0, 10, 11, 5),
                new TextureRegion(spriteSheet, 11, 10, 11, 5),
                new TextureRegion(spriteSheet, 22, 10, 11, 5),
                new TextureRegion(spriteSheet, 33, 10, 11, 5)
        };
        missNoteAnim = new Animation<>(0.085f, missFrames);
    }

    public void dispose() {
        spriteSheet.dispose();
    }
}
