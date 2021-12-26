package backend;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class Map {
    private int animalsAmount;
    private int plantsAmount;
    private ArrayList<Integer> genomeDominant;
    private int genomeDominantCnt;
    private double avgAnimalLifeSpan;
    private int deadAnimalsCnt;
    private double avgAnimalChildrenAmount;
    private ConcurrentHashMap<ArrayList<Integer>, Integer> genomeMap;
    private final int width;
    private final int height;
    private final boolean hasBorders;
    private final double startEnergy;
    private final double moveEnergy;
    private final double jungleRatio;
    private final double plantEnergy;
    private ConcurrentHashMap<Vector2d, ArrayList<Animal>> animals;
    private final ConcurrentHashMap<Vector2d, Grass> grasses;
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
    private final ArrayList<Animal> listOfAllAnimals;
    private final boolean magicVariant;
    private int magicTricksLeft = 3;

    public synchronized boolean isMagicVariant() {
        return magicVariant;
    }

    public synchronized int getMagicTricksLeft() {
        return magicTricksLeft;
    }

    public Map(int animalsAmount, int width, int height, double jungleRatio, boolean hasBorders, double startEnergy, double moveEnergy, double plantEnergy, boolean isMapRunning, boolean magicVariant) throws Exception {
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
        this.genomeMap = new ConcurrentHashMap<>();
        this.genomeDominant = new ArrayList<>();
        this.genomeDominantCnt = 0;
        this.deadAnimalsCnt = 0;
        this.plantsAmount = 0;
        this.avgAnimalLifeSpan = 0;
        this.avgAnimalChildrenAmount = 0;
        this.magicVariant = magicVariant;

        createJungleAndSavannaBoundaries();
        spawnAllAnimals();
    }

    public void sortAnimalList(ArrayList<Animal> animalList){
        animalList.sort(new AnimalsComparator() {
            @Override
            public int compare(Animal o1, Animal o2) {
                return super.compare(o1, o2);
            }
        });
    }

    public void updateGenomeMap(Animal animal) {
        if (genomeMap.get(animal.getGenes()) != null) {
            int value = genomeMap.get(animal.getGenes());
            value++;
            genomeMap.replace(animal.getGenes(), value);
        } else {
            genomeMap.put(animal.getGenes(), 1);
        }
    }

    public void doTheMagic(){
        Random rand = new Random();
        ArrayList<Animal> animalsToAdd = new ArrayList<>();
        for(Animal animal: listOfAllAnimals){
            Vector2d newPosition = null;
            while(newPosition == null){
                Vector2d positionProposition = new Vector2d(rand.nextInt(width+1), rand.nextInt(height+1));
                if(objectAt(positionProposition) == null){
                    newPosition = positionProposition;
                }else if(objectAt(positionProposition) instanceof Grass){
                    grasses.remove(positionProposition);
                    plantsAmount--;
                    newPosition = positionProposition;
//                  grass can not be where animal is, so I remove it
                }
            }
            Animal newAnimal = new Animal(startEnergy,animal.getDirection(),animal.getGenes(),moveEnergy,this,newPosition);
            animalsToAdd.add(newAnimal);
        }
        for(Animal animal:animalsToAdd){
            spawnAnimal(animal);
        }
        magicTricksLeft--;
        magicUpdateFor_avgAnimalChildrenAmount();
    }

    public void addAgeOfDeadAnimalTo_avgAnimalLifeSpan(Animal animal) {
        avgAnimalLifeSpan *= deadAnimalsCnt;
        avgAnimalLifeSpan += this.epochNumber - animal.getEpochOfBirth();
        deadAnimalsCnt++;
        avgAnimalLifeSpan /= deadAnimalsCnt;
    }

    public void birthUpdateFor_avgAnimalChildrenAmount() {
        avgAnimalChildrenAmount *= animalsAmount;
        avgAnimalChildrenAmount += 2;
        animalsAmount++;
        avgAnimalChildrenAmount /= animalsAmount;
    }

    public void magicUpdateFor_avgAnimalChildrenAmount() {
        avgAnimalChildrenAmount *= animalsAmount;
        animalsAmount += 5;
        avgAnimalChildrenAmount /= animalsAmount;
    }

    public void deathOfAnimalUpdateFor_avgAnimalChildrenAmount(Animal animal) {
        avgAnimalChildrenAmount *= animalsAmount;
        avgAnimalChildrenAmount -= animal.getChildrenAmount();
        animalsAmount--;
        avgAnimalChildrenAmount /= animalsAmount;
    }

    public synchronized int getAnimalsAmount() {
        return animalsAmount;
    }

    public int getPlantsAmount() {
        return plantsAmount;
    }

    public ArrayList<Integer> getGenomeDominant() {
        return genomeDominant;
    }

    public double getAvgAnimalsEnergy() {
        double avgAnimalsEnergy = 0;
        for (Animal animal : listOfAllAnimals) {
            avgAnimalsEnergy += animal.getEnergy();
        }
        avgAnimalsEnergy /= listOfAllAnimals.size();
        return avgAnimalsEnergy;
    }

    public double getAvgAnimalLifeSpan() {
        return avgAnimalLifeSpan;
    }

    public double getAvgAnimalChildrenAmount() {
        return avgAnimalChildrenAmount;
    }

    private int epochNumber = 0;

    public int getEpochNumber() {
        return this.epochNumber;
    }

    public void incrementEpochNumber() {
        this.epochNumber++;
    }

    //chart functions end

    //    map creation functions
    public void createJungleAndSavannaBoundaries() {
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

    public void spawnAllAnimals(){
        Random rand = new Random();
        for (int i = 0; i < animalsAmount; i++) {
            Vector2d position = new Vector2d(rand.nextInt(width + 1), rand.nextInt(height + 1));
            ArrayList<Integer> genes = new ArrayList<>();
            for (int j = 0; j < 32; j++) {
                genes.add(rand.nextInt(8));
            }
            Animal newAnimal = new Animal(startEnergy, possibleMapDirections[rand.nextInt(8)], genes, moveEnergy, this, position);
            spawnAnimal(newAnimal);
        }
    }

    public void spawnAnimal(Animal animal) {
        if (animals.get(animal.getPosition()) == null) {
            ArrayList<Animal> newList = new ArrayList<>();
            newList.add(animal);
            animals.put(animal.getPosition(), newList);
        } else {
            animals.get(animal.getPosition()).add(animal);
        }
        listOfAllAnimals.add(animal);
        updateGenomeMap(animal);
        if (genomeMap.get(animal.getGenes()) > genomeDominantCnt) {
            genomeDominantCnt = genomeMap.get(animal.getGenes());
            genomeDominant = animal.getGenes();
        }
    }
//    map creation functions


    public void place(Animal animal) {
        if (animals.get(animal.getPosition()) == null) {
            ArrayList<Animal> newList = new ArrayList<>();
            newList.add(animal);
            animals.put(animal.getPosition(), newList);
        } else {
            animals.get(animal.getPosition()).add(animal);
        }
    }

    public ArrayList<Animal> findAllStrongestAtPosition(Vector2d position) {
        ArrayList<Animal> strongestAnimals = new ArrayList<>();
        boolean first = true;
        if (animals.get(position) != null) {
            for (Animal animal : animals.get(position)) {
                if (first) {
                    strongestAnimals.add(animal);
                    first = false;
                }
                if (animal.getEnergy() == strongestAnimals.get(0).getEnergy()) {
                    strongestAnimals.add(animal);
                }
            }
            return strongestAnimals;
        } else {
            return null;
        }
    }

    public Vector2d feedAnimalsAtPosition(Vector2d position) {
        ArrayList<Animal> animalsToFeed = findAllStrongestAtPosition(position);
        if (animalsToFeed != null) {
            double energyPart = plantEnergy / animalsToFeed.size();
            for (Animal animal : animalsToFeed) {
                animal.setEnergy(animal.getEnergy() + energyPart);
            }
            return position;
        }
        return null;

    }

    public ArrayList<Integer> generateGenes(Animal daddy, Animal mommy, Random rand) {
        double sumOfEnergy = mommy.getEnergy() + daddy.getEnergy();
        double energyPerOneGene = sumOfEnergy / 32;
        ArrayList<Integer> childGenes = new ArrayList<>();
        boolean fromLeft = rand.nextInt(100) < 50;
        if (fromLeft) {
            for (int i = 0; i < 32; i++) {
                if ((i + 1) * energyPerOneGene <= mommy.getEnergy()) {
                    childGenes.add(mommy.getGenes().get(i));
                } else {
                    childGenes.add(daddy.getGenes().get(i));
                }
            }
        } else {
            for (int i = 0; i < 32; i++) {
                if ((i + 1) * energyPerOneGene <= daddy.getEnergy()) {
                    childGenes.add(daddy.getGenes().get(i));
                } else {
                    childGenes.add(mommy.getGenes().get(i));
                }
            }
        }
        return childGenes;
    }

    public void reproduceAnimalsAtPosition(Vector2d position) {
        Animal daddy = null;
        Animal mommy = null;
        if (animals.get(position) != null) {

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
            if (mommy != null && daddy != null && !mommy.equals(daddy) && mommy.getEnergy() >= mommy.getMinEnergyNeededToReproduce()) {
                Random rand = new Random();
                ArrayList<Integer> childGenes = generateGenes(mommy, daddy, rand);
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
                birthUpdateFor_avgAnimalChildrenAmount();
            }
        }
    }

    public void changePosition(Animal animal, Vector2d oldPosition) {
        place(animal);
        for (Animal a : animals.get(oldPosition)) {
            if (a.equals(animal)) {
                animals.get(oldPosition).remove(a);
                break;
            }
        }
    }

    public Animal getStrongestAtPosition(Vector2d position) {
        if (findAllStrongestAtPosition(position) == null || findAllStrongestAtPosition(position).size() == 0) {
            return null;
        }
        return findAllStrongestAtPosition(position).get(0);
    }

    public Object objectAt(Vector2d position) {
        if (animals.get(position) != null) return getStrongestAtPosition(position);
        if (grasses.containsKey(position)) return new Grass();
        else return null;
    }

    public boolean isInJungle(Vector2d position) {
        return position.precedes(jungleLeftLowerCorner) && position.follows(jungleRightUpperCorner);
    }


    public void setPlantsAmount(int plantsAmount) {
        this.plantsAmount = plantsAmount;
    }


    public ConcurrentHashMap<Vector2d, ArrayList<Animal>> getAnimals() {
        return animals;
    }

    public ConcurrentHashMap<Vector2d, Grass> getGrasses() {
        return grasses;
    }

    public synchronized boolean isMapRunning() {
        return this.isMapRunning;
    }

    public synchronized void swapRunning() {
        this.isMapRunning = !this.isMapRunning;
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


    @Override
    public String toString() {
        return !hasBorders ? "No borders map" : "Map with borders";
    }

    public ArrayList<Animal> getListOfAllAnimals() {
        return listOfAllAnimals;
    }

    public void feedAnimals() {
        ArrayList<Vector2d> grassToRemove = new ArrayList<>();
        for (Vector2d vector2d : getGrasses().keySet()) {
            grassToRemove.add(feedAnimalsAtPosition(vector2d));
        }
        if (grassToRemove.size() > 0) {
            for (Vector2d g : grassToRemove) {
                if (g != null) {
                    getGrasses().remove(g);
                    setPlantsAmount(getPlantsAmount() - 1);
                }
            }
        }

    }

    public void reproduceAnimals() {
        for (Vector2d position : getAnimals().keySet()) {
            reproduceAnimalsAtPosition(position);
        }
    }

    public void addPlants() {
        boolean jungleGrassPlaced = false;
        boolean savannaGrassPlaced = false;
        int i = 0;
        while ((!jungleGrassPlaced || !savannaGrassPlaced) && i < getWidth() * getHeight() * 10) {
            int x = ThreadLocalRandom.current().nextInt(0, getWidth() + 1);
            int y = ThreadLocalRandom.current().nextInt(0, getHeight() + 1);
            Vector2d grassProposition = new Vector2d(x, y);
            i++;
            if (getGrasses().get(grassProposition) == null && getAnimals().get(grassProposition) == null) {
                if (isInJungle(grassProposition) && !jungleGrassPlaced) {
                    getGrasses().put(grassProposition, new Grass());
                    jungleGrassPlaced = true;
                    setPlantsAmount(getPlantsAmount() + 1);
                } else if (!isInJungle(grassProposition) && !savannaGrassPlaced) {
                    getGrasses().put(grassProposition, new Grass());
                    savannaGrassPlaced = true;
                    setPlantsAmount(getPlantsAmount() + 1);
                }
            }
        }
    }

    public void removeDeadAnimals() {
        ArrayList<Animal> animalsToRemove = new ArrayList<>();
        for (ArrayList<Animal> animalsAtPosition : getAnimals().values()) {
            sortAnimalList(animalsAtPosition);
            for (Animal animal : animalsAtPosition) {
                if (animal.isDead()) {
                    animalsToRemove.add(animal);
                }
            }
        }
        for (Animal animal : animalsToRemove) {
            addAgeOfDeadAnimalTo_avgAnimalLifeSpan(animal);
            getListOfAllAnimals().remove(animal);
            getAnimals().get(animal.getPosition()).remove(animal);
            deathOfAnimalUpdateFor_avgAnimalChildrenAmount(animal);
        }
    }

    public void moveAnimals() {
        Random rand = new Random();
        for (Animal animal : getListOfAllAnimals()) {
            animal.move(animal.getGenes().get(rand.nextInt(32)));
        }
    }

}