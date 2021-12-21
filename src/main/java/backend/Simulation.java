package backend;

public class Simulation {
    Map map;

    public Simulation(Map map) {
        this.map = map;
    }

    public void run(){
        map.spawnAllAnimals();
        while (true){
            map.removeDeadAnimals();
            map.moveAnimals();
            map.feedAnimals();
            map.reproduceAnimals();
            map.addPlants();
        }
    }
}
