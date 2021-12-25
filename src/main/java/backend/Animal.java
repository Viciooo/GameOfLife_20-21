package backend;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

public class Animal {
    private double energy;
    private MapDirection direction;
    private final ArrayList<Integer> genes;
    private final double minEnergyNeededToReproduce;
    private final double reproducingCost = 0.25;
    private final double moveEnergy;
    private final Map map;
    private final int epochOfBirth;
    private Vector2d position;
    private int childrenAmount = 0;
    private boolean isTracked;

    public int getEpochOfBirth() {
        return epochOfBirth;
    }

    public int getChildrenAmount() {
        return childrenAmount;
    }

    public Vector2d getPosition() {
        return position;
    }

    public boolean isDead() {
        return energy <= 0;
    }

    public double getEnergy() {
        return energy;
    }

    public MapDirection getDirection() {
        return direction;
    }

    public ArrayList<Integer> getGenes() {
        return genes;
    }

    public double getMinEnergyNeededToReproduce() {
        return minEnergyNeededToReproduce;
    }

    public double getReproducingCost() {
        return reproducingCost;
    }

    public double getMoveEnergy() {
        return moveEnergy;
    }

    public Map getMap() {
        return map;
    }

    public void setPosition(Vector2d position) {
        this.position = position;
    }

    public void setEnergy(double energy) {
        this.energy = energy;
    }

    public void setDirection(MapDirection direction) {
        this.direction = direction;
    }

    public Animal(double startEnergy, MapDirection direction, ArrayList<Integer> genes, double moveEnergy, Map map, Vector2d position) {
        this.energy = startEnergy;
        this.direction = direction;
        this.genes = genes;
        this.genes.sort(Comparator.naturalOrder());
        this.minEnergyNeededToReproduce = 0.5 * startEnergy;
        this.moveEnergy = moveEnergy;
        this.map = map;
        this.position = position;
        this.epochOfBirth = this.map.getEpochNumber();
        this.isTracked = false;
    }

    public synchronized void swapIsTracked(){
        this.isTracked = !isTracked;
    }

    public synchronized boolean isTracked() {
        return isTracked;
    }

    public void dayPasses() {
        this.energy -= moveEnergy;
    }

    public void reproduce() {
        this.energy -= reproducingCost * energy;
        this.childrenAmount++;
    }

    public void move(int theMove) {
        int x1 = 0;
        int y1 = 0;
        int x2 = this.map.getWidth();
        int y2 = this.map.getHeight();
        Vector2d lowerLeftCorner = new Vector2d(x1, y1);
        Vector2d upperRightCorner = new Vector2d(x2, y2);
        Vector2d oldPosition = this.position;
        boolean mapCheck = map.getHasBorders();
        switch (theMove) {
            case 0 -> {
                Vector2d other = getPosition().add((Objects.requireNonNull(this.getDirection().toUnitVector())));
                if (!mapCheck) {

                    if(other.getX() < x1 && other.getY() < y1){
                        this.setPosition(this.position.add(new Vector2d(x2, 0)));
                        this.setPosition(this.position.add(new Vector2d(0, y2)));
                    }

                    else if(other.getX() < x1 && other.getY() > y2){
                        this.setPosition(this.position.add(new Vector2d(x2, 0)));
                        this.setPosition(this.position.subtract(new Vector2d(0, y2)));
                    }

                    else if(other.getX() > x2 && other.getY() < y1){
                        this.setPosition(this.position.subtract(new Vector2d(x2, 0)));
                        this.setPosition(this.position.add(new Vector2d(0, y2)));
                    }

                    else if(other.getX() > x2 && other.getY() > y2){
                        this.setPosition(this.position.subtract(new Vector2d(x2, 0)));
                        this.setPosition(this.position.subtract(new Vector2d(0, y2)));
                    }

                    else if (other.getX() < x1) {
                        this.setPosition(this.position.add(new Vector2d(x2, 0)));
                    }

                    else if (other.getY() < y1) {
                        this.setPosition(this.position.add(new Vector2d(0, y2)));
                    }

                    else if (other.getX() > x2) {
                        this.setPosition(this.position.subtract(new Vector2d(x2, 0)));
                    }

                    else if (other.getY() > y2) {
                        this.setPosition(this.position.subtract(new Vector2d(0, y2)));
                    }

                    else {
                        this.setPosition(this.position.add(Objects.requireNonNull(getDirection().toUnitVector())));
                    }

                    map.changePosition(this, oldPosition);
                }

                else {
                    if (other.follows(upperRightCorner) && other.precedes(lowerLeftCorner)) {
                        this.setPosition(other);
                        map.changePosition(this, oldPosition);

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
                if (!mapCheck) {

                    if(other.getX() < x1 && other.getY() < y1){
                        this.setPosition(this.position.add(new Vector2d(x2, 0)));
                        this.setPosition(this.position.add(new Vector2d(0, y2)));
                    }

                    else if(other.getX() < x1 && other.getY() > y2){
                        this.setPosition(this.position.add(new Vector2d(x2, 0)));
                        this.setPosition(this.position.subtract(new Vector2d(0, y2)));
                    }

                    else if(other.getX() > x2 && other.getY() < y1){
                        this.setPosition(this.position.subtract(new Vector2d(x2, 0)));
                        this.setPosition(this.position.add(new Vector2d(0, y2)));
                    }

                    else if(other.getX() > x2 && other.getY() > y2){
                        this.setPosition(this.position.subtract(new Vector2d(x2, 0)));
                        this.setPosition(this.position.subtract(new Vector2d(0, y2)));
                    }

                    else if (other.getX() < x1) {
                        this.setPosition(this.position.add(new Vector2d(x2, 0)));
                    }

                    else if (other.getY() < y1) {
                        this.setPosition(this.position.add(new Vector2d(0, y2)));
                    }

                    else if (other.getX() > x2) {
                        this.setPosition(this.position.subtract(new Vector2d(x2, 0)));
                    }

                    else if (other.getY() > y2) {
                        this.setPosition(this.position.subtract(new Vector2d(0, y2)));
                    }

                    else {
                        this.setPosition(this.position.add(Objects.requireNonNull(getDirection().toUnitVector())));
                    }

                    map.changePosition(this, oldPosition);
                }

                else {

                    if (other.follows(upperRightCorner) && other.precedes(lowerLeftCorner)) {
                        this.setPosition(other);
                        map.changePosition(this, oldPosition);

                    }
                }
            }
            case 5 -> {
                this.setDirection(this.direction.previous());
                this.setDirection(this.direction.previous());
                this.setDirection(this.direction.previous());
            }
            case 6 -> {
                this.setDirection(this.direction.previous());
                this.setDirection(this.direction.previous());
            }
            case 7 -> {
                this.setDirection(this.direction.previous());
            }
        }
        dayPasses();
    }

    @Override
    public String toString() {
        return "src/main/resources/hedgehog_img.jpg";
    }
}
