package backend;

import java.util.HashMap;
import java.util.TreeSet;

public class Map {
    private int animalsAmount;
    private final int width;
    private final int height;
    private final double jungleRatio;
    private final boolean hasBorders;
    private TreeSet<Animal> animals;
    private HashMap<Vector2d,Grass> grasses;

    public Map(int animalsAmount, int width, int height, double jungleRatio, boolean hasBorders) {
        this.animalsAmount = animalsAmount;
        this.width = width;
        this.height = height;
        this.jungleRatio = jungleRatio;
        this.hasBorders = hasBorders;
        this.grasses = new HashMap<>();
        int jungleWidth = (int) Math.floor(width - this.jungleRatio * width);
        int jungleHeight = (int) Math.floor(height - this.jungleRatio * height);
        int savannaWidth = (width - jungleWidth)/2;
        int savannaHeight = (height - jungleWidth)/2;
        Vector2d grassPosition = new Vector2d(savannaWidth,savannaHeight);

        for (int x = savannaWidth;x<=jungleWidth;x++){
            for (int y = savannaHeight;y<=jungleHeight;y++){
                grasses.put(grassPosition,new Grass());
                grassPosition.add(new Vector2d(0,1));
            }
            grassPosition.add(new Vector2d(1,0));
        }

        this.animals = new TreeSet<>((a1, a2) -> {
            if (a1.equals(a2)){
                return 0;
            }
            if (a1.getEnergy()< a2.getEnergy()){
                return -1;
            }
            return 1;
        });
    }

    public void place(Animal animal){
        animals.add(animal);
        grasses.remove(animal.getPosition());
    }

    public void addAnimal(int animalsAmount) {
        this.animalsAmount++;
    }
    public void removeAnimal(int animalsAmount) {
        this.animalsAmount--;
    }

    public int getAnimalsAmount() {
        return animalsAmount;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean getHasBorders() {
        return hasBorders;
    }

//    TODO
//    public Animal getStrongest(){
//    }
//    TODO
//    public void feedAnimals(){
//
//    }
//    TODO
//    public void reproduce(Animal a1,Animal a2){
//
//    }
//    TODO
//    public void addPlants(){
//
//    }

}
