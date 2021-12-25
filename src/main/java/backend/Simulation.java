package backend;

import com.example.gameoflife.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.TreeSet;
import java.util.concurrent.ThreadLocalRandom;

public class Simulation implements Runnable {
    private final Map map;
    private final GridHandler gridHandler;
    private final ChartHandler chartHandler;
    private final GenomeDominantHandler genomeDominantHandler;
    private final TrackedAnimalHandler trackedAnimalHandler;
    private boolean hardBlocked;

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
        this.hardBlocked = false;
    }

//    public synchronized void feedAnimals() {
//        ArrayList<Vector2d> grassToRemove = new ArrayList<>();
//        for (Vector2d vector2d : map.getGrasses().keySet()) {
//            grassToRemove.add(map.feedAnimalsAtPosition(vector2d));
//        }
//        if (grassToRemove.size() > 0) {
//            for (Vector2d g : grassToRemove) {
//                if (g != null) {
//                    map.getGrasses().remove(g);
//                    map.setPlantsAmount(map.getPlantsAmount() - 1);
//                }
//            }
//        }
//
//    }
//
//    public synchronized void reproduceAnimals() {
//        for (Vector2d position : map.getAnimals().keySet()) {
//            map.reproduceAnimalsAtPosition(position);
//        }
//    }
//
//    public synchronized void addPlants() {
//        boolean jungleGrassPlaced = false;
//        boolean savannaGrassPlaced = false;
//        int i = 0;
//        while ((!jungleGrassPlaced || !savannaGrassPlaced) && i < map.getWidth() * map.getHeight() * 10) {
//            int x = ThreadLocalRandom.current().nextInt(0, map.getWidth() + 1);
//            int y = ThreadLocalRandom.current().nextInt(0, map.getHeight() + 1);
//            Vector2d grassProposition = new Vector2d(x, y);
//            i++;
//            if (map.getGrasses().get(grassProposition) == null && map.getAnimals().get(grassProposition) == null) {
//                if (map.isInJungle(grassProposition) && !jungleGrassPlaced) {
//                    map.getGrasses().put(grassProposition, new Grass());
//                    jungleGrassPlaced = true;
//                    map.setPlantsAmount(map.getPlantsAmount() + 1);
//                } else if (!map.isInJungle(grassProposition) && !savannaGrassPlaced) {
//                    map.getGrasses().put(grassProposition, new Grass());
//                    savannaGrassPlaced = true;
//                    map.setPlantsAmount(map.getPlantsAmount() + 1);
//                }
//            }
//        }
//    }
//
//    public synchronized void removeDeadAnimals() {
//        ArrayList<Animal> animalsToRemove = new ArrayList<>();
//        for (TreeSet<Animal> animalsAtPosition : map.getAnimals().values()) {
//            for (Animal animal : animalsAtPosition) {
//                if (animal.isDead()) {
//                    animalsToRemove.add(animal);
//                }
//            }
//        }
//        for (Animal animal : animalsToRemove) {
//            map.addAgeOfDeadAnimalTo_avgAnimalLifeSpan(animal);
//            map.getListOfAllAnimals().remove(animal);
//            map.getAnimals().get(animal.getPosition()).remove(animal);
//            map.deathOfAnimalUpdateFor_avgAnimalChildrenAmount(animal);
//        }
//    }
//
//    public synchronized void moveAnimals() {
//        Random rand = new Random();
//        System.out.println(map.getListOfAllAnimals().toString());
//        for (Animal animal : map.getListOfAllAnimals()) {
//            animal.move(animal.getGenes().get(rand.nextInt(32)));
//        }
//    }

    @Override
    public void run() {
        while (true) {
            if (this.map.isMapRunning() && !hardBlocked) {
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
                    System.out.println(map.getAnimalsAmount()+" animals and "+map.getPlantsAmount() + " plants");
                    System.out.println(map.getListOfAllAnimals().size()+" really animals and "+map.getGrasses().size() + " really plants");
//                    if(map.getAnimalsAmount() == 0) {
//                        map.swapRunning();
//                        hardBlocked = true;
//                    }
                    Thread.sleep(300);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    }
}