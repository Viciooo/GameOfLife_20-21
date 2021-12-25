package backend;

import com.example.gameoflife.App;
import com.example.gameoflife.ChartHandler;
import com.example.gameoflife.GridHandler;
import javafx.scene.chart.LineChart;

public class Simulation implements Runnable {
    private final Map map;
    private final GridHandler gridHandler;
    private final ChartHandler chartHandler;

    public Simulation(Map map, GridHandler gridHandler, ChartHandler chartHandler) {
        this.map = map;
        this.gridHandler = gridHandler;
        this.chartHandler = chartHandler;
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
                    Thread.sleep(300);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    }
}