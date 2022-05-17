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
public class Tetris extends JPanel {
    //正在下落的方块
    private Tetromino currentOne=Tetromino.randomOne();
    //将要下落的方块
    private Tetromino nextOne=Tetromino.randomOne();
    //游戏的主要区域
    private  Cell[][] wall =new Cell[18][9];
    //声明每个单元格边长为34像素
    private static final int CELL_SIZE = 34;
    private int time=500;



    //声明游戏分数池
    int[] scores_pool = {0,100,250,500,1000};
    //声明当前游戏获得的分数
    private int totalScore = 0;
    //声明当前已消除的行数
    private int totalLine = 0;

    //声明游戏的三种状态，分别是游戏中，暂停，游戏结束
    public static final int PLAYING = 0;
    public static final int PAUSE = 1;
    public static final int GAMEOVER = 2;
    //声明变量存放当前游戏状态的值
    private int game_state;
    //声明一个数组，来显示游戏的状态
    String[] show_state = {"Press P[pause]","Press C[move]","Press S[replay]"};

//载入方块图片
    public static BufferedImage I;
    public static BufferedImage J;
    public static BufferedImage L;
    public static BufferedImage O;
    public static BufferedImage S;
    public static BufferedImage T;
    public static BufferedImage Z;
    public static BufferedImage backImage;



    static {
        try {
            I= ImageIO.read(new File("image/I.png"));
            J= ImageIO.read(new File("image/J.png"));
            L= ImageIO.read(new File("image/L.png"));
            O= ImageIO.read(new File("image/O.png"));
            S= ImageIO.read(new File("image/S.png"));
            T= ImageIO.read(new File("image/T.png"));
            Z= ImageIO.read(new File("image/Z.png"));
            backImage= ImageIO.read(new File("image/background.png"));
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
        g.setColor(Color.darkGray);
        g.drawImage(backImage,0,0,null);
        //平移坐标轴
        g.translate(4,6);
        //绘制游戏主区域
        paintWall(g);
        //绘制正在下落的四方格
        paintCurrentOne(g);
        //绘制将要下落的四方格
        paintNextOne(g);
        //绘制游戏得分
        paintScore(g);
        //绘制游戏当前的状态
        paintState(g);
    }


    public void start(){
        game_state = PLAYING;

        KeyListener l = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int code = e.getKeyCode();
                switch (code){

                    case KeyEvent.VK_DOWN:
                        if(game_state==0){
                        sortDropAction();}
                        break;
                    case KeyEvent.VK_LEFT:

                        moveLeftAction();
                        break;
                    case KeyEvent.VK_RIGHT:

                        moveRightAction();
                        break;
                    case KeyEvent.VK_UP:

                        rotateRightAction();
                        break;
                    case KeyEvent.VK_SPACE:
                        if(game_state==0){
                        hanDropAction();}
                        break;
                    case KeyEvent.VK_N:
                        setTime(500);
                        break;
                    case KeyEvent.VK_H:
                        setTime(250);
                        break;
                    case KeyEvent.VK_M:
                        setTime(100);
                        break;
                    case KeyEvent.VK_E:
                        setTime(750);
                        break;
                    case KeyEvent.VK_P:
                        if (game_state == PLAYING) {
                            game_state = PAUSE;
                        }
                        break;
                    case KeyEvent.VK_C:
                        if(game_state == PAUSE){
                            game_state = PLAYING;
                        }
                        break;
                    case KeyEvent.VK_S:
                        game_state = PLAYING;
                        wall = new Cell[18][9];
                        currentOne = Tetromino.randomOne();
                        nextOne = Tetromino.randomOne();
                        totalScore = 0;
                        totalLine = 0;
                        break;
                }
            }
        };

        //将俄罗斯方块窗口设置为焦点
        this.addKeyListener(l);
        this.requestFocus();

        while(true){
            //判断当前游戏状态是进行中，每time/1000秒下落一次
            if(game_state == PLAYING){
                try {
                    Thread.sleep(getTime());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //判断能否下落
                if(canDrop()){
                    currentOne.softDrop();
                }else{
                    //嵌入到墙中
                    landToWall();
                    //判断能否消行
                    destroyLine();
                    //判断游戏是否结束
                    if(isGameOver()){
                        game_state = GAMEOVER;
                    }else{
                        currentOne = nextOne;
                        nextOne = Tetromino.randomOne();
                    }
                }
            }
            repaint();
        }
    }

    //创建顺时针旋转
    public void rotateRightAction(){
        currentOne.rotateRight();
        //判断是否越界或重合
        if(outOfBounds() || coincide()){
            currentOne.rotateLeft();
        }
    }

    //瞬间下落
    public void  hanDropAction(){
        while(true){
            //判断四方格能否下落
            if(canDrop()){
                currentOne.softDrop();
            }else{
                break;
            }
        }
        //嵌入到墙中
        landToWall();
        //判断能否消行
        destroyLine();
        //判断游戏是否结束
        if(isGameOver()){
            game_state = GAMEOVER;
        }else{
            //继续生成新的四方格
            currentOne = nextOne;
            nextOne = Tetromino.randomOne();
        }
    }

    //按键一次，四方格下落一格
    public void sortDropAction(){
        //判断能否下落
        if(canDrop()){
            //当前四方格下落一格
            currentOne.softDrop();
        }else{
            //将四方格嵌入墙中
            landToWall();
            //判断能否消行
            destroyLine();
            //判断游戏是否结束
            if(isGameOver()){
                game_state = GAMEOVER;
            }else{
                //游戏未结束时，生成新的四方格
                currentOne = nextOne;
                nextOne = Tetromino.randomOne();
            }
        }
    }

    private void landToWall() {
        Cell[] cells = currentOne.cells;
        for (Cell cell : cells) {
            int row = cell.getRow();
            int col = cell.getCol();
            wall[row][col] = cell;
        }
    }

    //判断四方格能否下落
    public boolean canDrop(){
        Cell[] cells = currentOne.cells;
        for (Cell cell : cells) {
            int row = cell.getRow();
            int col = cell.getCol();
            //判断是否到达底部
            if(row == wall.length-1){
                return false;
            }else if(wall[row+1][col] != null){
                return false;
            }
        }
        return true;
    }

    //创建消行方法
    public void destroyLine(){
        //声明变量，统计当前消除的行数
        int line = 0;
        Cell[] cells = currentOne.cells;
        for (Cell cell : cells) {
            int row = cell.getRow();
            //判断当前行是否已满
            if(isFullLine(row)){
                line++;
                for (int i = row; i >0; i--) {
                    System.arraycopy(wall[i-1],0,wall[i],0,wall[0].length);
                }
                wall[0] = new Cell[9];
            }
        }

        //在分数池中获取分数，累加到总分中
        if (time==500){
        totalScore += 1*scores_pool[line];}
        if (time==100){
            totalScore += 5*scores_pool[line];}
        if (time==250){
            totalScore += 2.5*scores_pool[line];}
        if (time==750){
            totalScore += 0.5*scores_pool[line];}
        //统计消除的总行数
        totalLine += line;
    }

    //判断当前行是否已满
    public boolean isFullLine(int row){
        Cell[] cells = wall[row];
        for (Cell cell : cells) {
            if(cell == null){
                return false;
            }
        }
        return true;
    }

    //判断游戏是否结束
    public boolean isGameOver(){
        Cell[] cells = nextOne.cells;
        for (Cell cell : cells) {
            int row = cell.getRow();
            int col = cell.getCol();
            if(wall[row][col] != null){
                return true;
            }
        }
        return false;
    }

    private void paintState(Graphics g) {
        g.setColor(Color.WHITE);
        if(game_state == PLAYING){
            g.drawString(show_state[PLAYING],350,560);
        }else if(game_state == PAUSE){
            g.drawString(show_state[PAUSE],350,560);
        }else if(game_state == GAMEOVER){
            g.drawString(show_state[GAMEOVER],350,560);
            g.setColor(Color.RED);
            g.setFont(new Font(Font.SANS_SERIF,Font.BOLD,46));
            g.drawString("GAMEOVER!",10,300);
        }
    }

    private void paintScore(Graphics g) {
        g.setFont(new Font(Font.SANS_SERIF,Font.BOLD,24));
        g.setColor(Color.WHITE);
        g.drawString("Score:" + totalScore,350,177);
        g.drawString("Lines:" + totalLine,350,310);
        g.drawString("Press E:0.75x ",350,400);
        g.drawString("Press N:1x " ,350,435);
        g.drawString("Press H:2.5x " ,350,470);
        g.drawString("Press M:5x " ,350,505);
    }

    private void paintNextOne(Graphics g) {
        Cell[] cells = nextOne.cells;
        for (Cell cell : cells) {
            int x = cell.getCol() * CELL_SIZE + 260;
            int y = cell.getRow() * CELL_SIZE + 17;
            g.drawImage(cell.getImage(),x,y,null);
        }
    }

    private void paintCurrentOne(Graphics g) {
        Cell[] cells = currentOne.cells;
        for (Cell cell : cells) {
            int x = cell.getCol() * CELL_SIZE;
            int y = cell.getRow() * CELL_SIZE;
            g.drawImage(cell.getImage(),x,y,null);
        }
    }

    private void paintWall(Graphics g) {
        for (int i = 0; i < wall.length; i++) {
            for (int j = 0; j < wall[i].length; j++) {
                int x = j * CELL_SIZE;
                int y = i * CELL_SIZE;
                Cell cell = wall[i][j];
                //判断当前单元格是否有小方块，如果没有则绘制矩形，否则将小方块嵌入到墙中
                if(cell == null){
                    g.drawRect(x,y,CELL_SIZE,CELL_SIZE);
                }else{
                    g.drawImage(cell.getImage(),x,y,null);
                }
            }
        }
    }

    //判断游戏是否出界
    public boolean outOfBounds(){
        Cell[] cells = currentOne.cells;
        for (Cell cell : cells) {
            int col = cell.getCol();
            int row = cell.getRow();
            if(row<0 || row>(wall.length-1) || col<0 || col>(wall[0].length-1)){
                return true;
            }
        }
        return false;
    }
    //判断方块是否重合
    public boolean coincide(){
        Cell[] cells = currentOne.cells;
        for (Cell cell : cells) {
            int col = cell.getCol();
            int row = cell.getRow();
            if(wall[row][col] != null){
                return true;
            }
        }
        return false;
    }
    //按键一次，四方格左移一次
    public void moveLeftAction(){
        currentOne.moveleft();
        //判断是否越界，或者四方格是否重合
        if(outOfBounds() || coincide()){
            currentOne.moveright();
        }
    }
    //按键一次，四方格右移一次
    public void moveRightAction(){
        currentOne.moveright();
        //判断是否越界，或者四方格是否重合
        if(outOfBounds() || coincide()){
            currentOne.moveleft();
        }
    }

    //游戏场景设置以及main函数
    public static void main(String args[]){
        //创建一个窗口对象
        JFrame frame  = new JFrame("俄罗斯方块");
        //创建游戏界面，也就是面板
        Begin begin=new Begin();
        Tetris panel = new Tetris();
        //将面板嵌入到窗口
        frame.add(begin);
        //可见设置
        frame.setVisible(true);
        //窗口尺寸
        frame.setSize(560,670);
        begin.begin();
        while (begin.getTime()==0) {
            begin.begin();
           }
        begin.repaint();
        frame.add(panel);
        //窗口居中
        frame.setLocationRelativeTo(null);
        //播放音乐
        String filepath = "music/bgm.wav";
        musicStuff musicObject = new musicStuff();
        musicObject.playMusic(filepath);
        //窗口关闭程序终止

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //游戏主要逻辑封装在方法中
        panel.start();


    }
}
