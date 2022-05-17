package demo1;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.management.PlatformLoggingMXBean;
import java.util.concurrent.Callable;

//编写俄罗斯方块主类
public class Begin extends JPanel {
    //正在下落的方块
    private Tetromino currentOne=Tetromino.randomOne();
    //将要下落的方块
    private Tetromino nextOne=Tetromino.randomOne();
    //游戏的主要区域
    private  Cell[][] wall =new Cell[18][9];
    //声明每个单元格边长为34像素
    private static final int CELL_SIZE = 34;
    private int time=0;




    //载入方块图片
    public static BufferedImage backImage;
    public static BufferedImage backImage3;


    static {
        try {

            backImage= ImageIO.read(new File("image/background2.png"));
            backImage3= ImageIO.read(new File("image/background.png"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getTime() {
        return time;
    }
    public void setTime(int t) {
        this.time = t;
    }

    @Override
    public void paint(Graphics g) {
        if (time==0){
            g.setColor(Color.darkGray);
            g.drawImage(backImage,0,0,null);
            //平移坐标轴
            g.translate(0,0);
            paintScore(g);}
        else{}


    }


    public void begin(){

        KeyListener l = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int code = e.getKeyCode();
                switch (code){


                    case KeyEvent.VK_G:
                        setTime(1);
                        break;
                }
            }
        };

        //将俄罗斯方块窗口设置为焦点
        this.addKeyListener(l);
        this.requestFocus();


    }

    private void paintScore(Graphics g) {
        g.setFont(new Font(Font.SANS_SERIF,Font.BOLD,40));
        g.setColor(Color.WHITE);
        g.drawString("Press button G to begin ",200,300);
        g.setFont(new Font(Font.SANS_SERIF,Font.BOLD,40));
        g.drawString("Contact information" ,200,400);
        g.drawString("QQ 736018696" ,200,450);
    }




}
