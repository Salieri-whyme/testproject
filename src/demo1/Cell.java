package demo1;

import java.awt.image.BufferedImage;
import java.util.Objects;

/*
属性：行，列每个小方格的图片
方法：左移一格、右移一格，下落一格
编写小方格实体类

*/
public class Cell {
    private int row;
    private  int col;
    private BufferedImage image;

    public Cell() {
    }
    public Cell(int row, int col, BufferedImage image) {
        this.row = row;
        this.col = col;
        this.image = image;
    }


    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Cell{" +
                "row=" + row +
                ", col=" + col +
                ", image=" + image +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cell cell = (Cell) o;
        return row == cell.row &&
                col == cell.col &&
                Objects.equals(image, cell.image);
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col, image);
    }
    //左移一格
    public void left(){
        col--;
    }
    //右移一格
    public void right(){
        col++;
    }
    //下落一格
    public void down(){
        row++;
    }
}
