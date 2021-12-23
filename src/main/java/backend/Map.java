package backend;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class Map {
//    to be shown on chart

private int animalsAmount;
private int plantsAmount = 0;
private int[] genomDominant;
private double avgAnimalsEnergy;
private double avgAnimalLifeSpan;
private double avgAnimalChildrenAmount;

//    to be shown on chart end

    private final int width;
    private final int height;
    private final boolean hasBorders;
    private final double startEnergy;
    private final double moveEnergy;
    private final double jungleRatio;
    private double plantEnergy;
    private ConcurrentHashMap<Vector2d,TreeSet<Animal>> animals;
    private ConcurrentHashMap<Vector2d,Grass> grasses;
    private boolean isMapRunning;
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
    private Vector2d jungleLeftLowerCorner;
    private Vector2d jungleRightUpperCorner;
    private ArrayList<Vector2d> allPositions;
    private final ArrayList<Animal> listOfAllAnimals;

    public Map(int animalsAmount, int width, int height, double jungleRatio, boolean hasBorders, double startEnergy, double moveEnergy, double plantEnergy, boolean isMapRunning) {
        this.animalsAmount = animalsAmount;
        this.width = width;
        this.height = height;
        this.hasBorders = hasBorders;
        this.startEnergy = startEnergy;
        this.moveEnergy = moveEnergy;
        this.isMapRunning = isMapRunning;
        this.grasses = new ConcurrentHashMap<>();
        this.jungleRatio = jungleRatio;
        this.plantEnergy = plantEnergy;
        animals = new ConcurrentHashMap<>();
        listOfAllAnimals = new ArrayList<>();
        createJungleAndSavannaBoundries();
        createAllPositions();
        spawnAllAnimals();
    }
    public void createJungleAndSavannaBoundries() {
        double jungleArea = width * height / (1 + 1 / jungleRatio);
        int jungleWidth = (int) Math.sqrt(jungleArea);
        int jungleHeight = (int) Math.sqrt(jungleArea);
        while (jungleWidth > width) {
            jungleWidth--;
            jungleHeight++;
        }
        while (jungleHeight > height) {
            jungleWidth++;
            jungleHeight--;
        }
        int savannaHeight = (height - jungleHeight) / 2;
        int savannaWidth = (width - jungleWidth) / 2;
        this.jungleLeftLowerCorner = new Vector2d(savannaWidth, savannaHeight);
        this.jungleRightUpperCorner = new Vector2d(savannaWidth + jungleWidth, savannaHeight + jungleHeight);
    }

    public void createAllPositions() {
        this.allPositions = new ArrayList<>();
        for (int x = 0; x <= this.width; x++) {
            for (int y = 0; y <= this.height; y++) {
                this.allPositions.add(new Vector2d(x, y));
            }
        }
    }

    public void spawnAllAnimals() {
        Random rand = new Random();
        for (int i = 0; i < animalsAmount; i++) {
            Vector2d position = new Vector2d(rand.nextInt(width + 1), rand.nextInt(height + 1));
            int[] genes = new int[32];
            for (int j = 0; j < 32; j++) {
                genes[j] = rand.nextInt(8);
            }
            Animal newAnimal = new Animal(startEnergy, possibleMapDirections[rand.nextInt(8)], genes, moveEnergy, this, position);
            System.out.println(newAnimal);
            spawnAnimal(newAnimal);
        }
    }

    public void spawnAnimal(Animal animal) {
        if (animals.get(animal.getPosition()) == null) {
            TreeSet<Animal> newTreeSet = new TreeSet<>(new AnimalsComparator() {
                @Override
                public int compare(Animal o1, Animal o2) {
                    return super.compare(o1, o2);
                }
            });
            newTreeSet.add(animal);
            animals.put(animal.getPosition(), newTreeSet);
        } else {
            animals.get(animal.getPosition()).add(animal);
        }
        listOfAllAnimals.add(animal);
    }

    public void place(Animal animal) {
        if (animals.get(animal.getPosition()) == null) {
            TreeSet<Animal> newTreeSet = new TreeSet<>(new AnimalsComparator() {
                @Override
                public int compare(Animal o1, Animal o2) {
                    return super.compare(o1, o2);
                }
            });
            newTreeSet.add(animal);
            animals.put(animal.getPosition(), newTreeSet);
        } else {
            animals.get(animal.getPosition()).add(animal);
        }
    }

    public synchronized boolean isMapRunning() {
        return this.isMapRunning;
    }

    public synchronized void swapRunning() {
        this.isMapRunning = !this.isMapRunning;
    }

    public void removeAnimal() {
        this.animalsAmount--;
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


    public ArrayList<Animal> findAllStrongestAtPosition(Vector2d position) {
        ArrayList<Animal> strongestAnimals = new ArrayList<>();
        boolean first = true;
        if(animals.get(position) != null){
            for(Animal animal :animals.get(position)){
                if(first){
                    strongestAnimals.add(animal);
                    first = false;
                }
                if(animal.getEnergy() == strongestAnimals.get(0).getEnergy()){
                    strongestAnimals.add(animal);
                }
            }
            return strongestAnimals;
        }else{
            return null;
        }
    }

    public Vector2d feedAnimalsAtPosition(Vector2d position) {
        ArrayList<Animal> animalsToFeed = findAllStrongestAtPosition(position);
        if(animalsToFeed != null){
            double energyPart = plantEnergy/ animalsToFeed.size();
            for (Animal animal : animalsToFeed) {
                animal.setEnergy(animal.getEnergy() + energyPart);
            }
            return position;
        }
        return null;

    }

    public int[] generateGenes(Animal daddy, Animal mommy, Random rand) {
        double sumOfEnergy = mommy.getEnergy() + daddy.getEnergy();
        double energyPerOneGene = sumOfEnergy / 32;
        int[] childGenes = new int[32];
        boolean fromLeft = rand.nextInt(100) < 50;
        if (fromLeft) {
            for (int i = 0; i < 32; i++) {
                if ((i + 1) * energyPerOneGene <= mommy.getEnergy()) {
                    childGenes[i] = mommy.getGenes()[i];
                } else {
                    childGenes[i] = daddy.getGenes()[i];
                }
            }
        } else {
            for (int i = 0; i < 32; i++) {
                if ((i + 1) * energyPerOneGene <= daddy.getEnergy()) {
                    childGenes[i] = daddy.getGenes()[i];
                } else {
                    childGenes[i] = mommy.getGenes()[i];
                }
            }
        }
        return childGenes;
    }

    public void reproduceAnimalsAtPosition(Vector2d position) {
        Animal daddy = null;
        Animal mommy = null;
        if(animals.get(position) != null){

        Iterator<Animal> iterator = animals.get(position).iterator();
        int i = 0;
        while (iterator.hasNext()) {
            if (i % 2 == 0) {
                mommy = iterator.next();
            } else {
                daddy = iterator.next();
            }
            i++;
        }
        if (mommy != null && daddy != null && !mommy.equals(daddy)) {
            Random rand = new Random();
            int[] childGenes = generateGenes(mommy, daddy, rand);
            mommy.reproduce();
            daddy.reproduce();
            MapDirection childDirection = this.possibleMapDirections[rand.nextInt(8)];
            Animal child = new Animal(
                    mommy.getEnergy() * mommy.getReproducingCost() + daddy.getEnergy() * daddy.getReproducingCost(),
                    childDirection,
                    childGenes,
                    mommy.getMoveEnergy(),
                    mommy.getMap(),
                    mommy.getPosition()
            );
            spawnAnimal(child);
            animalsAmount++;
        }
        }
    }

    public void changePosition(Animal animal, Vector2d oldPosition) {
        TreeSet<Animal> oldTreeSet = animals.get(oldPosition);
        place(animal);
        for (Animal a : oldTreeSet) {
            if (a.equals(animal)) {
                oldTreeSet.remove(a);
                break;
            }
        }
    }

    public Animal getStrongestAtPosition(Vector2d position){
        if(findAllStrongestAtPosition(position) == null || findAllStrongestAtPosition(position).size() == 0){
            return null;
        }
        return findAllStrongestAtPosition(position).get(findAllStrongestAtPosition(position).size()-1);
    }

    public Object objectAt(Vector2d position){
        if(animals.get(position) != null) return getStrongestAtPosition(position);
        if(grasses.containsKey(position)) return new Grass();
        else return null;
    }

    public boolean isInJungle(Vector2d position) {
        return position.precedes(jungleLeftLowerCorner) && position.follows(jungleRightUpperCorner);
    }

    public void feedAnimals(){
        ArrayList<Vector2d> grassToRemove = new ArrayList<>();
        for (Vector2d vector2d : grasses.keySet()) {
            grassToRemove.add(feedAnimalsAtPosition(vector2d));
        }
        if(grassToRemove.size()>0){
            for(Vector2d g: grassToRemove){
                if(g != null){
                    grasses.remove(g);
                    plantsAmount--;
                }
            }
        }

    }

    public void reproduceAnimals(){
        for(Vector2d position: animals.keySet()){
            reproduceAnimalsAtPosition(position);
        }
    }

    public void addPlants() {
        boolean jungleGrassPlaced = false;
        boolean savannaGrassPlaced = false;
        int i = 0;
        while ((!jungleGrassPlaced || !savannaGrassPlaced) && i < width*height*2) {
            int x = ThreadLocalRandom.current().nextInt(0, width);
            int y = ThreadLocalRandom.current().nextInt(0, height);
            Vector2d grassProposition = new Vector2d(x, y);
            i++;
            if (grasses.get(grassProposition) == null && animals.get(grassProposition) == null) {
                if (isInJungle(grassProposition) && !jungleGrassPlaced) {
                    grasses.put(grassProposition, new Grass());
                    jungleGrassPlaced = true;
                    plantsAmount++;
                }
                else if (!isInJungle(grassProposition) && !savannaGrassPlaced) {
                    grasses.put(grassProposition, new Grass());
                    savannaGrassPlaced = true;
                    plantsAmount++;
                }
            }
        }
    }

    public void removeDeadAnimals() {
        ArrayList<Animal> animalsToRemove = new ArrayList<>();
        for (TreeSet<Animal> animalsAtPosition : animals.values()) {
            for (Animal animal : animalsAtPosition) {
                if (animal.isDead()) {
                    animalsToRemove.add(animal);
                }
            }
        }
        for(Animal animal: animalsToRemove){
            listOfAllAnimals.remove(animal);
            animals.get(animal.getPosition()).remove(animal);
            removeAnimal();
        }
    }

    public void moveAnimals() {
        Random rand = new Random();
        for (Animal animal : listOfAllAnimals) {
            animal.move(rand.nextInt(8));
        }
    }

}