package backend;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Map {

    private int animalsAmount;
    private final int width;
    private final int height;
    private final boolean hasBorders;
    private final double startEnergy;
    private final double moveEnergy;
    private final double jungleRatio;
    private double plantEnergy;
    private HashMap<Vector2d, TreeSet<Animal>> animals;
    private HashMap<Vector2d, Grass> grasses;
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

    public Map(int animalsAmount, int width, int height, double jungleRatio, boolean hasBorders, double startEnergy, double moveEnergy,double plantEnergy) {
        this.animalsAmount = animalsAmount;
        this.width = width;
        this.height = height;
        this.hasBorders = hasBorders;
        this.startEnergy = startEnergy;
        this.moveEnergy = moveEnergy;
        this.grasses = new HashMap<>();
        this.jungleRatio = jungleRatio;
        this.plantEnergy = plantEnergy;
        animals = new HashMap<Vector2d,TreeSet<Animal>>();
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
            TreeSet<Animal> newTreeSet = new TreeSet<>((o1, o2) -> {
                if (o1.equals(o2)) return 0;
                if (o1.getEnergy() == o2.getEnergy()) return 1;
                else return (int) (o1.getEnergy() - o2.getEnergy());
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
            TreeSet<Animal> newTreeSet = new TreeSet<>((o1, o2) -> {
                if (o1.equals(o2)) return 0;
                if (o1.getEnergy() == o2.getEnergy()) return 1;
                else return (int) (o1.getEnergy() - o2.getEnergy());
            });
            newTreeSet.add(animal);
            animals.put(animal.getPosition(), newTreeSet);
        } else {
            animals.get(animal.getPosition()).add(animal);
        }
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
        if(animals.get(position) != null){
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
        }else{
            return null;
        }
    }

    public void feedAnimalsAtPosition(Vector2d position) {
        ArrayList<Animal> animalsToFeed = findAllStrongestAtPosition(position);
        if(animalsToFeed != null){
            double energyPart = grasses.get(position).getPlantEnergy() / animalsToFeed.size();
            grasses.remove(position);
            for (Animal animal : animalsToFeed) {
                animal.setEnergy(animal.getEnergy() + energyPart);
            }
        }

    }

    public int[] generateGenes(Animal mommy, Animal daddy, Random rand) {
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
        for(Vector2d position: grasses.keySet()){
            feedAnimalsAtPosition(position);
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
        while (!jungleGrassPlaced && !savannaGrassPlaced) {
            int x = ThreadLocalRandom.current().nextInt(0, width);
            int y = ThreadLocalRandom.current().nextInt(0, height);
            Vector2d grassProposition = new Vector2d(x, y);
            if (!grasses.containsKey(grassProposition) && !animals.containsKey(grassProposition)) {
                if (isInJungle(grassProposition) && !jungleGrassPlaced) {
                    grasses.put(grassProposition, new Grass());
                    jungleGrassPlaced = true;
                }
                if (!isInJungle(grassProposition) && !savannaGrassPlaced) {
                    grasses.put(grassProposition, new Grass());
                    savannaGrassPlaced = true;
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
            removeAnimal();
        }
        for(Animal animal: animalsToRemove){
            listOfAllAnimals.remove(animal);
            animals.get(animal.getPosition()).remove(animal);
        }
    }

    public void moveAnimals() {
        Random rand = new Random();
        for (Animal animal : listOfAllAnimals) {
            animal.move(rand.nextInt(8));
        }
    }

}