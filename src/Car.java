import java.util.Random;

public class Car {
    float x, y;
    int w, h;
    boolean left;
    double speed;
    double randomSeed;


    public Car(int x, int y, int w, int h, boolean left, double speed) {
        Random rand = new Random();
        this.randomSeed = rand.nextFloat();
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.left = left;
        this.speed = speed;
    }
}
