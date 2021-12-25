package backend;

import com.example.gameoflife.App;
import com.example.gameoflife.ChartHandler;
import com.example.gameoflife.GenomeDominantHandler;
import com.example.gameoflife.GridHandler;
import javafx.scene.chart.LineChart;

public class Simulation implements Runnable {
    private final Map map;
    private final GridHandler gridHandler;
    private final ChartHandler chartHandler;
    private final GenomeDominantHandler genomeDominantHandler;

    public Simulation(Map map, GridHandler gridHandler, ChartHandler chartHandler, GenomeDominantHandler genomeDominantHandler) {
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
                    if(this.map.getAnimalsAmount() == 0){
                        this.map.swapRunning();
                        map.incrementEpochNumber();
                        map.removeDeadAnimals();
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    }
}