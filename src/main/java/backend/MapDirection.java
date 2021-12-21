package backend;

public enum MapDirection {
    N,
    NE,
    E,
    SE,
    S,
    SW,
    W,
    NW;

    public MapDirection next() {
        switch (this) {
            case N -> {
                return NE;
            }
            case NE -> {
                return E;
            }
            case E -> {
                return SE;
            }
            case SE -> {
                return S;
            }
            case S -> {
                return SW;
            }
            case SW -> {
                return W;
            }
            case W -> {
                return NW;
            }
            default -> {
                return null;
            }
        }
    }

    public MapDirection previous() {
        switch (this) {
            case N -> {
                return NW;
            }
            case NW -> {
                return W;
            }
            case W -> {
                return SW;
            }
            case SW -> {
                return S;
            }
            case S -> {
                return SE;
            }
            case SE -> {
                return E;
            }
            case E -> {
                return NE;
            }
            default -> {
                return null;
            }
        }
    }

    public Vector2d toUnitVector() {
        switch (this) {
            case N -> {
                return new Vector2d(0, 1);
            }
            case NE -> {
                return new Vector2d(1, 1);
            }
            case NW -> {
                return new Vector2d(-1, 1);
            }
            case S -> {
                return new Vector2d(0, -1);
            }
            case SE -> {
                return new Vector2d(1, -1);
            }
            case SW -> {
                return new Vector2d(-1, -1);
            }
            case E -> {
                return new Vector2d(1, 0);
            }
            case W -> {
                return new Vector2d(-1, 0);
            }
            default -> {
                return null;
            }
        }
    }
}

