package GUI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

public class AnimationHandler {

    private MainScreen[] guis;
    private HashMap<String, Double[]>[] bakedAnimations;


    public AnimationHandler(MainScreen[] guis){
        this.guis = guis;
        bakedAnimations = new HashMap[guis.length];
        for (int i = 0; i < bakedAnimations.length; i++)
            bakedAnimations[i] = new HashMap<>();
    }

    private QuestionScreen[] getAnimated(){
        QuestionScreen[] animated = new QuestionScreen[guis.length];
        for (int i = 0; i < guis.length; i++)
            animated[i] = ((MainScreen.GlassScreen)SwingUtilities.getRootPane(guis[i]).getGlassPane()).questionScreen;
        return animated;
    }

    public void playOpeningAnimation(){
        int lastFrame = bakedAnimations[0].get("cardHeight").length - 1;
        QuestionScreen[] animated = getAnimated();
        new Timer(8, new ActionListener() {
            int i = 0;
            @Override
            public void actionPerformed(ActionEvent event) {
                System.out.println("playing");
                if (i == lastFrame)
                    cut(event);
                for (int j = 0; j < guis.length; j++){
                    double cardHeight = bakedAnimations[j].get("cardHeight")[i];
                    int background = bakedAnimations[j].get("backOpacity")[i].intValue();
                    animated[j].update(cardHeight,background);
                }
                i++;
            }
            private void cut(ActionEvent event){
                ((Timer)event.getSource()).stop();
            }
        }).start();
    }
    public void bakeOpeningAnimation(int gui){
        new Timer(0, new ActionListener() {
            private static final int MARGIN = 100;

            double cardHeight = 0;
            int backOpacity = 0;

            ArrayList<Double> cardHeights = new ArrayList<>();
            ArrayList<Double> backOpacities = new ArrayList<>();

            double cardDist;

            @Override
            public void actionPerformed(ActionEvent event) {
                System.out.println("Baking " + gui);
                cardHeights.add(cardHeight);
                backOpacities.add((double)backOpacity);

                cardDist = (guis[gui].getHeight()-MARGIN*2) - cardHeight;
                cardHeight += cardDist / 4;

                backOpacity += 10;

                if (Math.abs(cardDist) <= 5){
                    cut(event);
                }
            }

            private void cut(ActionEvent event){
                ((Timer)event.getSource()).stop();
                bakedAnimations[gui].put("cardHeight",cardHeights.toArray(new Double[0]));
                bakedAnimations[gui].put("backOpacity",backOpacities.toArray(new Double[0]));
            }
        }).start();
    }

    /*void playCountdownAnimation(){

    }
    void bakeCountdownAnimation(int sec){
        if (!firstTime.get())
            backOpacity.set(180);
        countdownThread = new Thread(() -> {
            countingDown.set(true);
            synchronized (COIN_ANIMATION_CYCLE_LOCK){
                coinAnimationCycle = 0;
            }
            int timeLeftMillis = sec * 1000;
            int delay = 10;

            while ((timeLeftMillis -= delay) > 0){
                double coinDist = (double)((timeLeftMillis % 1000) - 500) / 12000;
                synchronized (COIN_ANIMATION_CYCLE_LOCK){
                    coinAnimationCycle += coinDist;
                }
                countdown.set(timeLeftMillis/1000);

                if (timeLeftMillis % 1000 == 0){
                    synchronized (COIN_ANIMATION_CYCLE_LOCK){
                        coinAnimationCycle = 0;
                    }
                    if (timeLeftMillis < 6000){
                        backOpacity.addAndGet(10);
                    }
                }

                try{
                    Thread.sleep(delay-1);
                }catch(InterruptedException e){
                    System.out.println("CoinTimer interrupted by game host, the person");
                    countingDown.set(false);
                    return;
                }
            }
            countingDown.set(false);
        });
        countdownThread.start();
    }*/

    //Håller koll på varje GUI
    //Kör alla timers och delar ut värden till GUIna
    //Bakar en animation för varje GUI vid uppstart
    //När bakningarna inte är null kan de användas
}
