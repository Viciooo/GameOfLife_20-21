package backend;

import com.example.gameoflife.*;


public class Simulation implements Runnable {
    private final Map map;
    private final GridHandler gridHandler;
    private final ChartHandler chartHandler;
    private final GenomeDominantHandler genomeDominantHandler;
    private final TrackedAnimalHandler trackedAnimalHandler;

    public Simulation(
            Map map,
            GridHandler gridHandler,
            ChartHandler chartHandler,
            GenomeDominantHandler genomeDominantHandler,
            TrackedAnimalHandler trackedAnimalHandler) {

        this.trackedAnimalHandler = trackedAnimalHandler;
        this.map = map;
        this.gridHandler = gridHandler;
        this.chartHandler = chartHandler;
        this.genomeDominantHandler = genomeDominantHandler;
    }

    @Override
    public void run() {
        while (true) {
            if (this.map.isMapRunning()) {
                try {
                    if (gridHandler.getTrackedAnimal() != null) {
                        trackedAnimalHandler.refreshLabel(gridHandler.getTrackedAnimal());
                    }
                    map.incrementEpochNumber();
                    map.removeDeadAnimals();
                    map.moveAnimals();
                    map.feedAnimals();
                    map.reproduceAnimals();
                    map.addPlants();
                    gridHandler.refreshMap();
                    chartHandler.refreshChart();
                    genomeDominantHandler.refreshLabel();
                    Thread.sleep(300);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    }
}