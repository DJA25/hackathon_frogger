import java.awt.*;

public class Player {

    float x,  y;
    int r,  c;
    int w, h;
    public Rectangle rec;
    public boolean right;
    public boolean left;
    public boolean up;
    public boolean down;


    public Player(int r, int c, int w, int h) {
        this.r = r;
        this.c = c;
        this.w = w;
        this.h = h;
        rec = new Rectangle(r, c, w, h);
    }

    public void move() {
        if(left) r--;
        if(right)r++;
        if(up)c++;
        if(down)c--;

        rec = new Rectangle(r, c, w, h);
    }



}
