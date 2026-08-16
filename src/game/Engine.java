package src.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static src.util.ListFunctions.getRandom;

public class Engine {
    int points=0;
    public int size;
    int[][] board;

    public Engine(int size){
        this.board=new int[size][size];
        random2or4();
        this.size=size;
        random2or4();
    }

    public int[][] getBoard() {
        return board;
    }

    public int getPoints() {
        return points;
    }

    public boolean moveRight(){
        int[][] oldBoard=new int[size][];
        for (int i = 0; i < size; i++) {
            oldBoard[i]=board[i].clone();
        }
        for (int i=0;i< board.length;i++){
            int[] row=new int[board[i].length];
            for (int j=0;j<board[i].length;j++){
                row[j]=board[i][board[i].length-1-j];
            }
            row=slideLeft(row);
            for (int j=0;j<row.length;j++){
                board[i][board[i].length-1-j]=row[j];
            }
        }
        if (Arrays.deepEquals(oldBoard, board)){
            return false;
        }
        random2or4();
        return true;
    }

    public boolean moveLeft(){
        int[][] oldBoard=new int[size][];
        for (int i = 0; i < size; i++) {
            oldBoard[i]=board[i].clone();
        }
        for (int i=0;i< board.length;i++){
            board[i]=slideLeft(board[i]);
        }
        if (Arrays.deepEquals(oldBoard, board)){
            return false;
        }
        random2or4();
        return true;
    }

    public int[] slideLeft(int[] row) {
        int[] result=new int[row.length];
        int pos=0;
        for (int val:row){
            if (val!=0){result[pos++]=val;}
        }
        row=result;
        for (int i=0;i< row.length-1;i++){
            if (row[i]==row[i+1]){
                row[i]*=2;
                points += row[i];
                row[i+1]=0;
            }
        }
        pos=0;
        result=new int[row.length];
        for (int val:row){
            if (val!=0){result[pos++]=val;}
        }
        return result;
    }

    public boolean moveDown() {
        int[][] oldBoard=new int[size][];
        for (int i = 0; i < size; i++) {
            oldBoard[i]=board[i].clone();
        }
        for (int i = 0; i < board[0].length; i++) {
            int[] result=new int[board.length];
            for (int j = 0; j < board.length; j++) {
                result[j]=board[board.length-1-j][i];
            }
            result=slideLeft(result);
            for (int j = 0; j < board.length; j++) {
                board[j][i]=result[result.length-j-1];
            }
        }
        if (Arrays.deepEquals(oldBoard, board)){
            return false;
        }
        random2or4();
        return true;
    }

    public boolean moveUp() {
        int[][] oldBoard=new int[size][];
        for (int i = 0; i < board.length; i++) {
            oldBoard[i]=board[i].clone();
        }
        for (int i = 0; i < board[0].length; i++) {
            int[] result=new int[board.length];
            for (int j = 0; j < board.length; j++) {
                result[j]=board[j][i];
            }
            result=slideLeft(result);
            for (int j = 0; j < board.length; j++) {
                board[j][i]=result[j];
            }
        }
        if (Arrays.deepEquals(oldBoard, board)){
            return false;
        }
        random2or4();
        return true;
    }

    public boolean lose(){
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j]==0||
                        j<size-1&&board[i][j]==board[i][j+1]||
                        i<size-1&&board[i][j]==board[i+1][j]){
                    return false;
                }
            }
        }
        return true;
    }

    public void random2or4(){
        Random random=new Random();
        List<int[]> emptyCells=new ArrayList<>();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j]==0){
                    emptyCells.add(new int[]{i,j});
                }
            }
        }
        if (emptyCells.isEmpty()){return;}
        int[] cell=getRandom(emptyCells);
        board[cell[0]][cell[1]]=random.nextInt(10)<9?2:4;
    }

    public void reset(){
        board=new int[this.size][this.size];
        points=0;
        random2or4();
        random2or4();
    }
}
