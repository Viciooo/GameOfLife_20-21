package backend;

import com.example.gameoflife.App;
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
        while (true) {
//            System.out.println(this.map.isMapRunning());
            if (this.map.isMapRunning()) {
            try {
                    map.removeDeadAnimals();
                    map.moveAnimals();
                    map.feedAnimals();
                    map.reproduceAnimals();
                    map.addPlants();
                    gridHandler.refreshMap();
                    Thread.sleep(300);
                } catch(Throwable e){
                    e.printStackTrace();
                }
            }
        }
    }
}