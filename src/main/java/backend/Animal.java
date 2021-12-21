package backend;

import java.util.Arrays;
import java.util.Objects;

public class Animal{
    private int startEnergy;
    private int energy; //na początku to jest startEnergy
    private MapDirection direction;
    private int[] genes;
    private final double minEnergyNeededToReproduce; //only example
    private final double reproducingCost = 0.25;
    private final int moveEnergy;
    private final Map map;
    private Vector2d position;

    public Vector2d getPosition() {
        return position;
    }

    public int getStartEnergy() {
        return startEnergy;
    }

    public int getEnergy() {
        return energy;
    }

    public MapDirection getDirection() {
        return direction;
    }

    public int[] getGenes() {
        return genes;
    }

    public double getMinEnergyNeededToReproduce() {
        return minEnergyNeededToReproduce;
    }

    public double getReproducingCost() {
        return reproducingCost;
    }

    public int getMoveEnergy() {
        return moveEnergy;
    }

    public Map getMap() {
        return map;
    }

    public void setPosition(Vector2d position) {
        this.position = position;
    }

    public void setStartEnergy(int startEnergy) {
        this.startEnergy = startEnergy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public void setDirection(MapDirection direction) {
        this.direction = direction;
    }

    public void setGenes(int[] genes) {
        this.genes = genes;
    }

    public Animal(int startEnergy, MapDirection direction, int[] genes, int moveEnergy, Map map, int x, int y) {
        this.startEnergy = startEnergy;
        this.energy = startEnergy;
        this.direction = direction;
        this.genes = genes;
        Arrays.sort(this.genes);
        this.minEnergyNeededToReproduce = 0.5*startEnergy;
        this.moveEnergy = moveEnergy;
        this.map = map;
        this.position = new Vector2d(x,y);
    }

    public void dayPasses(){
        this.energy -= moveEnergy;
    }

    public void reproduce(){
        this.energy -= reproducingCost*energy;
    }

    public void move(int theMove){
        int x1 = 0;
        int y1 = 0;
        int x2 = this.map.getWidth();
        int y2 = this.map.getHeight();
        Vector2d lowerLeftCorner = new Vector2d(x1,y1);
        Vector2d upperRightCorner = new Vector2d(x2,y2);
        boolean mapCheck = map.getHasBorders();
        switch (theMove){
            case 0 -> {
                Vector2d other = getPosition().add((Objects.requireNonNull(this.getDirection().toUnitVector())));
                if(mapCheck){
                    if (other.getX() < x1) {
                        this.setPosition(this.position.add(new Vector2d(x2, 0)));
                    } else if (other.getY() < y1) {
                        this.setPosition(this.position.add(new Vector2d(0, y2)));
                    } else if (other.getX() > x2) {
                        this.setPosition(this.position.subtract(new Vector2d(x2, 0)));
                    } else if (other.getY() > y2) {
                        this.setPosition(this.position.subtract(new Vector2d(0, y2)));
                    } else {
                        this.setPosition(this.position.add(Objects.requireNonNull(getDirection().toUnitVector())));
                    }
                }
                else{
                    if(other.follows(upperRightCorner) && other.precedes(lowerLeftCorner)){
                        this.setPosition(other);
                    }
                }
            }
            case 1 -> {
                this.setDirection(this.direction.next());
            }
            case 2 -> {
                this.setDirection(this.direction.next());
                this.setDirection(this.direction.next());
            }
            case 3 -> {
                this.setDirection(this.direction.next());
                this.setDirection(this.direction.next());
                this.setDirection(this.direction.next());
            }
            case 4 -> {
                Vector2d other = getPosition().subtract(Objects.requireNonNull(this.getDirection().toUnitVector()));
                if(mapCheck){
                    if (other.getX() < x1) {
                        this.setPosition(this.position.add(new Vector2d(x2, 0)));
                    } else if (other.getY() < y1) {
                        this.setPosition(this.position.add(new Vector2d(0, y2)));
                    } else if (other.getX() > x2) {
                        this.setPosition(this.position.subtract(new Vector2d(x2, 0)));
                    } else if (other.getY() > y2) {
                        this.setPosition(this.position.subtract(new Vector2d(0, y2)));
                    } else {
                        this.setPosition(this.position.add(Objects.requireNonNull(getDirection().toUnitVector())));
                    }
                }
                else{
                    if(other.follows(upperRightCorner) && other.precedes(lowerLeftCorner)){
                        this.setPosition(other);
                    }
                }
            }
            case 5 ->{
                this.setDirection(this.direction.previous());
                this.setDirection(this.direction.previous());
                this.setDirection(this.direction.previous());
            }
            case 6 ->{
                this.setDirection(this.direction.previous());
                this.setDirection(this.direction.previous());
            }
            case 7 ->{
                this.setDirection(this.direction.previous());
            }
        }
    }

}
