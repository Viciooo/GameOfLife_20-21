package backend;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Map {

    private int animalsAmount;
    private final int width;
    private final int height;
    private final boolean hasBorders;
    private HashMap<Vector2d,TreeSet<Animal>> animals;
    private final HashMap<Vector2d, Grass> grasses;
    private final MapDirection[] possibleMapDirections = {
            MapDirection.N,
            MapDirection.NE,
            MapDirection.E,
            MapDirection.SE,
            MapDirection.S,
            MapDirection.SW,
            MapDirection.W,
            MapDirection.NW
    };
    private  double jungleRatio;
    private Vector2d jungleLeftLowerCorner;
    private Vector2d jungleRightUpperCorner;
    private ArrayList<Vector2d> allPositions;

    public Map(int animalsAmount, int width, int height, double jungleRatio, boolean hasBorders, HashMap<Vector2d, TreeSet<Animal>> animals) {
        this.animalsAmount = animalsAmount;
        this.width = width;
        this.height = height;
        this.hasBorders = hasBorders;
        this.animals = animals;
        this.grasses = new HashMap<>();
        createJungleAndSavannaBoundries();
        createAllPositions();
        addGrassToJungle();
    }

    public void createAllPositions(){
        for(int x = 0;x <= this.width;x++){
            for(int y = 0;y <= this.height;y++){
                allPositions.add(new Vector2d(x,y));
            }
        }
    }

    public void addGrassToJungle(){
        for(Vector2d position: allPositions){
            if(isInJungle(position)) grasses.put(position,new Grass());
        }
    }

    public void createJungleAndSavannaBoundries(){
        double jungleArea = width*height / (1 + 1/jungleRatio);
        int jungleWidth = (int) Math.sqrt(jungleArea);
        int jungleHeight = (int) Math.sqrt(jungleArea);
        while(jungleWidth > width){
            jungleWidth--;
            jungleHeight++;
        }
        while(jungleHeight > height){
            jungleWidth++;
            jungleHeight--;
        }
        int savannaHeight = (height - jungleHeight) / 2;
        int savannaWidth = (width - jungleWidth) / 2;
        jungleLeftLowerCorner = new Vector2d(savannaWidth, savannaHeight);
        jungleRightUpperCorner = new Vector2d(savannaWidth + jungleWidth, savannaHeight + jungleHeight);
    }

    public void place(Animal animal) {
        if(animals.get(animal.getPosition()) == null){
            TreeSet<Animal> newTreeSet = new TreeSet<>((o1, o2) -> {
                if(o1.equals(o2)) return 0;
                if(o1.getEnergy() == o2.getEnergy()) return 1;
                else return (int) (o1.getEnergy() - o2.getEnergy());
            });
            newTreeSet.add(animal);
            animals.put(animal.getPosition(),newTreeSet);
        }else{
            animals.get(animal.getPosition()).add(animal);
        }
        grasses.remove(animal.getPosition());
        this.animalsAmount++;
    }

    public void removeAnimal() {
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

    public ArrayList<Animal> findStrongestAtPosition(Vector2d position){
        ArrayList<Animal> strongestAnimals = new ArrayList<>();
        Iterator<Animal> iterator = animals.get(position).iterator();
        int i = 0;
        while (iterator.hasNext()) {
            strongestAnimals.add(iterator.next());
            if (i != 0 && strongestAnimals.get(i).getEnergy() != strongestAnimals.get(i - 1).getEnergy()) {
                Animal strongest = strongestAnimals.get(i);
                strongestAnimals = new ArrayList<>();
                strongestAnimals.add(strongest);
                i = 0;
            }
            i++;
        }
        return strongestAnimals;
    }

    public void feedAnimalsAtPosition(Vector2d position) {
            if (grasses.get(position) != null) {
                ArrayList<Animal> animalsToFeed = findStrongestAtPosition(position);
                double energyPart = grasses.get(position).getPlantEnergy()/animalsToFeed.size();
                grasses.remove(position);
                for(Animal animal: animalsToFeed){
                    animal.setEnergy(animal.getEnergy() + energyPart);
                }
            }
    }

    public int[] generateGenes(Animal mommy,Animal daddy,Random rand){
        double sumOfEnergy = mommy.getEnergy() + daddy.getEnergy();
        double energyPerOneGene = sumOfEnergy/32;
        int[] childGenes = new int[32];
        boolean fromLeft = rand.nextInt(100) < 50;
        if(fromLeft){
            for(int i = 0;i < 32;i++){
                if((i+1)*energyPerOneGene <= mommy.getEnergy()){
                    childGenes[i] = mommy.getGenes()[i];
                }else{
                    childGenes[i] = daddy.getGenes()[i];
                }
            }
        }else{
            for(int i = 0;i < 32;i++){
                if((i+1)*energyPerOneGene <= daddy.getEnergy()){
                    childGenes[i] = daddy.getGenes()[i];
                }else{
                    childGenes[i] = mommy.getGenes()[i];
                }
            }
        }
        return childGenes;
    }

    public void reproduceAnimalsAtPosition(Vector2d position) {
        Animal daddy = null;
        Animal mommy = null;
        Iterator<Animal> iterator = animals.get(position).iterator();
        int i = 0;
        while (iterator.hasNext()) {
            if(i % 2 == 0){
                mommy = iterator.next();
            }else{
                daddy = iterator.next();
            }
            i++;
        }
        if(mommy != null && daddy != null && !mommy.equals(daddy)){
            Random rand = new Random();
            int[] childGenes = generateGenes(mommy,daddy,rand);
            mommy.reproduce();
            daddy.reproduce();
            MapDirection childDirection = this.possibleMapDirections[rand.nextInt(8)];
            Animal child = new Animal(
                    mommy.getEnergy()*mommy.getReproducingCost()+daddy.getEnergy()*daddy.getReproducingCost(),
                    childDirection,
                    childGenes,
                    mommy.getMoveEnergy(),
                    mommy.getMap(),
                    mommy.getPosition()
                    );
            place(child);
        }
    }


    public boolean isInJungle(Vector2d position){
        return position.precedes(jungleLeftLowerCorner) && position.follows(jungleRightUpperCorner);
    }

    public void addPlants(){
        boolean jungleGrassPlaced = false;
        boolean savannaGrassPlaced = false;
        while(!jungleGrassPlaced && !savannaGrassPlaced){
            int x = ThreadLocalRandom.current().nextInt(0, width);
            int y = ThreadLocalRandom.current().nextInt(0, height);
            Vector2d grassProposition = new Vector2d(x,y);
            if(!grasses.containsKey(grassProposition) && !animals.containsKey(grassProposition)){
                if(isInJungle(grassProposition) && !jungleGrassPlaced){
                    grasses.put(grassProposition,new Grass());
                    jungleGrassPlaced = true;
                }
                if(!isInJungle(grassProposition) && !savannaGrassPlaced){
                    grasses.put(grassProposition,new Grass());
                    savannaGrassPlaced = true;
                }
            }
        }
    }

    public void clearMap(){
        for(TreeSet<Animal> animalsAtPosition: animals.values()){
            animalsAtPosition.removeIf(Animal::isDead);
            removeAnimal();
        }
    }

    public void changePosition(Animal animal,Vector2d oldPosition){
        TreeSet<Animal> oldTreeSet = animals.get(oldPosition);
//        TODO can couse errors because we place it and then remove from the treeSet (?)
        place(animal);
        for(Animal a : oldTreeSet){
            if(a == animal) {
                oldTreeSet.remove(a);
                break;
            }
        }
    }

}
