package nju.aor.common;

public class Vertex {
    public int id;
    public long coord_x;
    public long coord_y;
    public double demand;

    public Vertex(int _id, long _x, long _y,double demand) {
        this.id = _id;
        this.coord_x = _x;
        this.coord_y = _y;
        this.demand=demand;
    }

    @Override
    public String toString() {
        return id + "," + coord_x + "," + coord_y+','+demand;
    }
}
