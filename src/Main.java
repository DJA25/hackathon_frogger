import org.json.simple.parser.ParseException;

import java.lang.reflect.Array;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Scanner;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.awt.*;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class Main implements Runnable, KeyListener {

    public JFrame frame;
    static boolean lost;
    public Canvas canvas;
    public JPanel panel;

    public Image Gameoverpic;


    public BufferStrategy bufferStrategy;

    final int WIDTH = 1200;
    final int HEIGHT = 800;
    static  Player player = new Player(7, 1, 40, 40);
    public Image playerImg = Toolkit.getDefaultToolkit().getImage("pixil-frame-0.png");

    public double scroll;
    public double scrollSpeed = 0.035;

    public static int curCol = 0;

    static int[] road = new int[10000];
    static boolean[][] tree = new boolean[10000][16];
    static Car[][] cars = new Car[10000][2];
    final static int ROAD = 1;
    final static int GRASS = 0;

    public static String code = "zion";

    public String parkName;

    public NationalPark park;
    public ArrayList treeIMG = new ArrayList();
    public ArrayList carIMG = new ArrayList();
    public ArrayList backgroundIMG = new ArrayList();

    public static void main(String[] args) {
        road[0] = GRASS;
        road[1] = GRASS;
        genAll();
        Main ex = new Main();
        new Thread(ex).start();
    }

    public static void genAll() {
        try {
            ArrayList allParks = parkAPI.getParkList();
            for (Object o:allParks){
                NationalPark thisPark = (NationalPark) o;
                System.out.println(thisPark.parkName+" -- park code: "+thisPark.parkCode);
                System.out.println("—————————"+thisPark.description);
            }
        }catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        System.out.print("Which national park would you like to play in? Type the 4 letter park code. If you're not sure, try 'zion': ");
        Scanner scanner = new Scanner(System.in);
        code = scanner.nextLine();
        if(code.length()!=4){
            code = "zion";
        }

        int max = 3;
        int min = 1;
        int index = 2;
        int add = 1;
        while(index<road.length) {
            int random = (int) (Math.random()*max);
            for(int i = index; i<random+index && i<road.length; i++) {
                road[i] = ROAD;
                boolean left = Math.random()<0.5;
                int btwn = 200+ (int)(Math.random()*250);
                int rand =(int)(Math.random()*800);
                int width = 100 + (int) (Math.random()*100);
                double speed = Math.random()*0.175+0.1;
                if(left) speed*=-1;
                Car car = new Car(rand,i,width,50,left, speed);
                Car car2 = new Car(rand-btwn-width,i,width,50,left, speed);

                cars[i][0] = car;
                cars[i][1] = car2;

            }
            index+=random;
            if(index>=road.length) break;
            road[index] = GRASS;
            for(int i = 0; i<16; i++) {
                if(Math.random()<(3.0/16)) tree[index][i] = true;
            }
            index++;
            if(add*(20+add*5)<index) max++;
            if(max>8) max=8;
        }

    }

    public Main() {

//        setupGraphics();
        setUpGraphics();
        canvas.addKeyListener(this);
        Gameoverpic= Toolkit.getDefaultToolkit().getImage("YouLost.png");
    }

    public void keyPressed(KeyEvent event) {
        char key = event.getKeyChar();
        int keyCode = event.getKeyCode();
        System.out.println("Key Pressed: " + key + "  Code: " + keyCode);
        if(key == 'a' || keyCode == 37) {
            if(!tree[player.c][player.r-1])player.r--;
        }
        else if(key == 'd' || keyCode == 39) {
            if(!tree[player.c][player.r+1])player.r++;
        }
        else if(key == 'w' || key == ' ' || keyCode == 38) {
            if(!tree[player.c+1][player.r]) {
                player.c++;
                scrollSpeed = 0.06;
            }
        }
        else if(key == 's' || keyCode == 40){
            if(!tree[player.c-1][player.r])player.c--;
        }
        if(player.r<0)player.r=0;
        else if(player.r>15) player.r=15;




    }

    public void keyReleased(KeyEvent event) {
        char key = event.getKeyChar();
        int keyCode = event.getKeyCode();

        if(key == 'w' || keyCode == 38) {
            scrollSpeed = 0.035;
        }



    }

    public void keyTyped(KeyEvent event) {
        char key = event.getKeyChar();
        int keyCode = event.getKeyCode();

    }

    public void run() {
        Graphics2D g = (Graphics2D) bufferStrategy.getDrawGraphics();
        g.setFont(new Font("Arial", Font.BOLD, 50));
        g.drawString("Loading...", 500,400);
        g.dispose();
        bufferStrategy.show();

        try {
            park = parkAPI.getParkInfo(code);
            parkName = park.parkName;
            treeIMG.add(pixelate("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRLkuT8CnLIAHzeb2is0LLtcvFBvrFbFpRzgRwkpXGH&s",25));
//            for(Object o: park.plantImages) {
//                treeIMG.add(pixelate((String) o));
//            }
            for(Object o: park.animalImages) {
                carIMG.add(pixelate((String) o,1));
            }
            for(Object o: park.backgroundImages) {
                backgroundIMG.add(pixelate((String) o,1));
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        while (true) {
            scroll+=scrollSpeed;
            render();
            int min = (player.r+4)*50+5;
            int max = min + player.w;
            Car c1 = cars[player.c][0];
            Car c2 = cars[player.c][1];
//            if(player.c);
        }
    }
    static int score = 0;
    public void setUpGraphics() {
        frame = new JFrame("Application Template");   //Create the program window or frame.  Names it.

        panel = (JPanel) frame.getContentPane();  //sets up a JPanel which is what goes in the frame
        panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));  //sizes the JPanel
        panel.setLayout(null);   //set the layout

        // creates a canvas which is a blank rectangular area of the screen onto which the application can draw
        // and trap input events (Mouse and Keyboard events)
        canvas = new Canvas();
        canvas.setBounds(0, 0, WIDTH, HEIGHT);
        canvas.setIgnoreRepaint(true);

        panel.add(canvas);  // adds the canvas to the panel.

        // frame operations
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  //makes the frame close and exit nicely
        frame.pack();  //adjusts the frame and its contents so the sizes are at their default or larger
        frame.setResizable(false);   //makes it so the frame cannot be resized
        frame.setVisible(true);      //IMPORTANT!!!  if the frame is not set to visible it will not appear on the screen!

        // sets up things so the screen displays images nicely.
        canvas.createBufferStrategy(2);
        bufferStrategy = canvas.getBufferStrategy();
        canvas.requestFocus();

        System.out.println("DONE graphic setup");

    }

    public void render() {
        Graphics2D g = (Graphics2D) bufferStrategy.getDrawGraphics();
        g.clearRect(0, 0, WIDTH, HEIGHT);

        int minCol = (int)scroll/50;
        int maxCol = minCol+16;
//        for(int i = Math.max(0, minCol-16); i<minCol; i++) {
//            if(road[i] != ROAD) continue;
//            int mult = 1;
//            cars[i][0].x+=cars[i][0].speed;
//            cars[i][1].x+=cars[i][0].speed;
//            if(cars[i][1].x>1000+cars[i][1].w && !cars[i][1].left) cars[i][1].x=200-cars[i][1].w-20;
//            if(cars[i][0].x>1000+cars[i][0].w && !cars[i][0].left) cars[i][0].x=200-cars[i][0].w-20;
//            if(cars[i][0].x<200-cars[i][0].w && cars[i][0].left) cars[i][0].x=1000+cars[i][0].w+20;
//            if(cars[i][0].x<200-cars[i][1].w && cars[i][0].left) cars[i][1].x=1000+cars[i][1].w+20;
//        }
        for(int i = minCol; i<=maxCol; i++) {
            if((15-i)*50+scroll<-100) continue;
            Rectangle c1, c2;
            if(road[i]==GRASS) {
                g.setColor(new Color(66, 91, 61));
                g.fillRect(200, (15-i)*50+(int)scroll, WIDTH-400, 50);
                for(int j = 0; j<16; j++) {
                    if(tree[i][j]) {
                        g.drawImage((Image)treeIMG.get(0),(200+j*50)+10, ((15-i)*50)+10+(int)scroll,30,30,null);
                    }
                }
            }

            else if (road[i]==ROAD) {
//                System.out.println(i);
                cars[i][0].x+=cars[i][0].speed;
                cars[i][1].x+=cars[i][0].speed;
                if(cars[i][1].x>1000+cars[i][1].w && !cars[i][1].left) cars[i][1].x=-cars[i][1].w-20;
                if(cars[i][0].x>1000+cars[i][0].w && !cars[i][0].left) cars[i][0].x=-cars[i][0].w-20;
                if(cars[i][0].x<-cars[i][0].w && cars[i][0].left) cars[i][0].x=800+cars[i][0].w+20;
                if(cars[i][0].x<-cars[i][1].w && cars[i][0].left) cars[i][1].x=800+cars[i][1].w+20;


                g.setColor(Color.DARK_GRAY);
                g.fillRect(200, (15-i)*50+(int)scroll, WIDTH-400, 50);

                g.setColor(Color.blue);
                Image placeholder = (Image)carIMG.get((int)(carIMG.size()*cars[i][0].randomSeed));
                cars[i][0].w = (int)(40*((placeholder.getWidth(null)*1.0)/(placeholder.getHeight(null)*1.0)));

                g.drawImage(placeholder,(int)(200+cars[i][0].x), (15-i)*50+(int)scroll+5, cars[i][0].w, 40,null);
//
//                g.drawRect((int)(200+cars[i][0].x), (15-i)*50+(int)scroll+5, cars[i][0].w, 40);

                placeholder = (Image)carIMG.get((int)(carIMG.size()*cars[i][1].randomSeed));
                cars[i][1].w = (int)(40*((placeholder.getWidth(null)*1.0)/(placeholder.getHeight(null)*1.0)));
                g.drawImage(placeholder,(int)(200+cars[i][1].x), (15-i)*50+(int)scroll+5, cars[i][1].w, 40,null);
//
//                g.drawRect((int)(200+cars[i][1].x), (15-i)*50+(int)scroll+5, cars[i][1].w, 40);

                c1 = new Rectangle((int)(200+cars[i][0].x), (15-i)*50+(int)scroll+5, cars[i][0].w, 40);
                c2 = new Rectangle((int)(200+cars[i][1].x), (15-i)*50+(int)scroll+5, cars[i][1].w, 40);
                Rectangle pp = new Rectangle(((player.r+4)*50)+5, ((15-player.c)*50)+5+(int)scroll, player.w, player.h);
                if(c1!=null && pp.intersects(c1)) lost = true;
                if(c2!=null && pp.intersects(c2)) lost = true;
//                                    g.drawImage((200+j*50)+10, ((15-i)*50)+10+(int)scroll,30,30,null);
            }
        }

        g.drawImage((Image)backgroundIMG.get(0),0,0,200,HEIGHT,null);
        g.drawImage((Image)backgroundIMG.get(backgroundIMG.size()-1),WIDTH - 200,0,200,HEIGHT,null);

        g.setColor(Color.RED);
        g.drawImage(playerImg, ((player.r+4)*50)+5, ((15-player.c)*50)+5+(int)scroll, player.w, player.h, null);

        g.setColor(Color.BLACK);
        g.drawRoundRect(20,20,400,80,10,10);
        g.setColor(Color.WHITE);
        g.fillRoundRect(20,20,400,80,10,10);
        g.setColor(Color.BLACK);
        g.drawString("Score: " + player.c*100,25,51);
        g.drawString(parkName,25,91);



        if(((15-player.c)*50)+5+(int)scroll>HEIGHT+50) lost = true;

        scroll+=((player.c-minCol)*0.011);

//        g.setColor(Color.BLACK);
//        g.drawRoundRect(500,30,200,100,10,10);
//        g.fillRoundRect(500,30,200,100,10,10);
//        g.drawString("Score: " + (player.c*100),505,35);



///*
        if(lost==true) {
            if(score == 0) score = player.c*100;
            g.clearRect(0,0,WIDTH, HEIGHT);
            g.drawImage((Image)backgroundIMG.get(0),0,0,WIDTH,HEIGHT,null);
            g.setColor(Color.WHITE);
            g.fillRect(250, 300,700,150);
            g.setFont(new Font("Arial", Font.BOLD, 50));
            g.setColor(Color.RED);
            g.drawString("You Lose :(", 300, 400);
//            g.drawImage(Gameoverpic,0,0, WIDTH,HEIGHT, null);
            g.drawString("Score: " + score, 600,400);
        }

// */


        g.dispose();
        bufferStrategy.show();

    }

    public Image pixelate(String input, int size) { //filename = poutput of
        try {
            // the line that reads the image file
            // How big should the pixelations be?
            final int PIX_SIZE = size;
            // Read the file as an Image
            BufferedImage img = ImageIO.read(new URL(input));
            // Get the raster data (array of pixels)
            Raster src = img.getData();
            // Create an identically-sized output raster
            WritableRaster dest = src.createCompatibleWritableRaster();

            // Loop through every PIX_SIZE pixels, in both x and y directions
            for (int y = 0; y < src.getHeight(); y += PIX_SIZE) {
                for (int x = 0; x < src.getWidth(); x += PIX_SIZE) {

                    // Copy the pixel
                    double[] pixel = new double[3];
                    pixel = src.getPixel(x, y, pixel);

                    // "Paste" the pixel onto the surrounding PIX_SIZE by PIX_SIZE neighbors
                    // Also make sure that our loop never goes outside the bounds of the image
                    for (int yd = y; (yd < y + PIX_SIZE) && (yd < dest.getHeight()); yd++) {
                        for (int xd = x; (xd < x + PIX_SIZE) && (xd < dest.getWidth()); xd++) {
                            dest.setPixel(xd, yd, pixel);
                        }
                    }
                }
            }
            // Save the raster back to the Image
            ((BufferedImage) img).setData(dest);

            // Write the new file
            //ImageIO.write((RenderedImage) img, "jpg", new File("bee.jpg"));
            return img;
            // work with the image here ...

        } catch (IOException e) {
            // log the exception
            // re-throw if desired
            return null;
        }

    }

}


