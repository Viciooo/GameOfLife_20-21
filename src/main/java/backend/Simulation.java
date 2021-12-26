package backend;

import com.example.gameoflife.*;


public class Simulation implements Runnable {
    private final Map map;
    private final GridHandler gridHandler;
    private final ChartHandler chartHandler;
    private final StatisticsWriter statisticsWriter;
    private final GenomeDominantHandler genomeDominantHandler;
    private final TrackedAnimalHandler trackedAnimalHandler;
    private boolean simulationEnded;

    public Simulation(
            Map map,
            GridHandler gridHandler,
            ChartHandler chartHandler,
            GenomeDominantHandler genomeDominantHandler,
            TrackedAnimalHandler trackedAnimalHandler,
            StatisticsWriter statisticsWriter) {
        this.statisticsWriter = statisticsWriter;
        this.trackedAnimalHandler = trackedAnimalHandler;
        this.map = map;
        this.gridHandler = gridHandler;
        this.chartHandler = chartHandler;
        this.genomeDominantHandler = genomeDominantHandler;
        this.simulationEnded = false;
    }

    @Override
    public void run() {
        while (true) {
            if (this.map.isMapRunning() && !simulationEnded) {
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
                    statisticsWriter.writeStatistics();
                    Thread.sleep(200);
                    if(map.getAnimalsAmount() == 0){
                        simulationEnded = true;
                        map.swapRunning();
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    }
}