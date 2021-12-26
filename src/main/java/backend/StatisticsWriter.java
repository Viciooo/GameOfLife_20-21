package backend;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class StatisticsWriter {
    private final Map map;
    private final ArrayList<Integer> animalAmountHistory = new ArrayList<>();
    private final ArrayList<Integer> grassAmountHistory = new ArrayList<>();
    private final ArrayList<Double> avgEnergyHistory = new ArrayList<>();
    private final ArrayList<Double> avgLifeSpanHistory = new ArrayList<>();
    private final ArrayList<Double> avgChildrenAmountHistory = new ArrayList<>();

    private long sumOfAnimals = 0;
    private long sumOfBushes = 0;
    private double sumOfEnergy = 0;
    private double sumOfLifeSpan = 0;
    private double sumOfChildren = 0;

    public StatisticsWriter(Map map) {
        this.map = map;
    }

    public void writeStatistics() throws IOException {
        FileWriter fileWriter;

        this.animalAmountHistory.add(map.getAnimalsAmount());
        this.grassAmountHistory.add(map.getPlantsAmount());
        this.avgEnergyHistory.add(map.getAvgAnimalsEnergy());
        this.avgLifeSpanHistory.add(map.getAvgAnimalLifeSpan());
        this.avgChildrenAmountHistory.add(map.getAvgAnimalChildrenAmount());

        this.sumOfAnimals += map.getAnimalsAmount();
        this.sumOfBushes += map.getPlantsAmount();
        this.sumOfEnergy += map.getAvgAnimalsEnergy();
        this.sumOfLifeSpan += map.getAvgAnimalLifeSpan();
        this.sumOfChildren += map.getAvgAnimalChildrenAmount();

        int numberOfEpoch = map.getEpochNumber();
        if (map.getHasBorders()) {
            fileWriter = new FileWriter("src/main/resources/mapWithBorders.csv");
        } else {
            fileWriter = new FileWriter("src/main/resources/noBordersMap.csv");
        }
        fileWriter.append("Epoch").append(",");
        fileWriter.append("Animal amount").append(",");
        fileWriter.append("Grass amount").append(",");
        fileWriter.append("Average energy").append(",");
        fileWriter.append("Average life span").append(",");
        fileWriter.append("Average children amount").append("\n");
        for (int i = 0; i < numberOfEpoch; i++) {
            fileWriter.append(Integer.toString(i)).append(",");
            fileWriter.append(this.animalAmountHistory.get(i).toString()).append(",");
            fileWriter.append(this.grassAmountHistory.get(i).toString()).append(",");
            fileWriter.append(this.avgEnergyHistory.get(i).toString()).append(",");
            fileWriter.append(this.avgLifeSpanHistory.get(i).toString()).append(",");
            fileWriter.append(this.avgChildrenAmountHistory.get(i).toString()).append("\n");
        }

        fileWriter.append("On average").append(",");
        fileWriter.append(String.valueOf(this.sumOfAnimals / (double) numberOfEpoch)).append(",");
        fileWriter.append(String.valueOf(this.sumOfBushes / (double) numberOfEpoch)).append(",");
        fileWriter.append(String.valueOf(this.sumOfEnergy / (double) numberOfEpoch)).append(",");
        fileWriter.append(String.valueOf(this.sumOfLifeSpan / (double) numberOfEpoch)).append(",");
        fileWriter.append(String.valueOf(this.sumOfChildren / (double) numberOfEpoch)).append("\n");
        fileWriter.close();
    }
}
