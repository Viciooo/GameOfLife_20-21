package backend;

import com.example.gameoflife.GridHandler;

public class Simulation implements Runnable {
    private final Map map;
    private final GridHandler gridHandler;

    public Simulation(Map map, GridHandler gridHandler) {
        this.map = map;
        this.gridHandler = gridHandler;
    }

    @Override
    public void run() {
        int i = 0;
        while (true) {
            map.removeDeadAnimals();
            map.moveAnimals();
            map.feedAnimals();
            map.reproduceAnimals();
            map.addPlants();
            System.out.println(i);
            gridHandler.refreshMap();
            i++;
            try {
                Thread.sleep(3);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}