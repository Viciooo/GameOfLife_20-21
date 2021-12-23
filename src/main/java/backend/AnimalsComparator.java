package backend;

import java.util.Comparator;

public abstract class AnimalsComparator implements Comparator<Animal> {
    public int compare(Animal o1, Animal o2) {
        if (o1.equals(o2)) return 0;
        if (o1.getEnergy() == o2.getEnergy()) return 1;
        else return (int) (o1.getEnergy() - o2.getEnergy());
    }
}
