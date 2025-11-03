package game;

import java.util.Arrays;
import java.util.Random;

public class Engine {
    int[][] board=new int[][]{
            {0,0,0,0},
            {0,0,0,0},
            {0,0,0,0},
            {0,0,0,0}};
    boolean[][] combined=new boolean[][]{
            {false,false,false,false},
            {false,false,false,false},
            {false,false,false,false},
            {false,false,false,false}};
    int points=0;

    public int[][] getBoard() {
        return board;
    }

    public void setBoard(int[][] board){
        this.board=board;
    }

    public int getPoints() {
        return points;
    }

    public void moveRight(){
        System.out.println(Arrays.deepToString(board));
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                try{
                    if(board[i][j+1]==0){
                        board[i][j+1]=board[i][j];
                        board[i][j]=0;
                        if ( j>0&&board[i][j-1]!=0){
                            board[i][j]=board[i][j-1];
                            board[i][j-1]=0;
                        }
                        System.out.println(Arrays.deepToString(board));
                    }else if (board[i][j+1]==board[i][j]&&!combined[i][j]){
                        board[i][j+1]=board[i][j]*2;
                        board[i][j]=0;
                        if ( j>0&&board[i][j-1]!=0){
                            board[i][j]=board[i][j-1];
                            board[i][j-1]=0;
                        }
                        System.out.println(Arrays.deepToString(board));
                        combined[i][j+1]=true;
                        points+=board[i][j+1];
                    }
                }   catch (ArrayIndexOutOfBoundsException e){
                    board[i][j]=board[i][j];
                    System.out.println(Arrays.deepToString(board));
                }
            }
        }
        System.out.println(Arrays.deepToString(board)+"\npoints="+points);
        random2or4();
    }

    public void moveLeft() {
        System.out.println(Arrays.deepToString(board));
        for (int i = 0; i < 4; i++) {
            for (int j =3; j > -1; j--) {
                try{
                    if(board[i][j-1]==0){
                        board[i][j-1]=board[i][j];
                        board[i][j]=0;
                        if ( j<3&&board[i][j+1]!=0){
                            board[i][j]=board[i][j+1];
                            board[i][j+1]=0;
                        }
                        System.out.println(Arrays.deepToString(board));
                    }else if (board[i][j-1]==board[i][j]&&!combined[i][j]){
                        board[i][j-1]=board[i][j]*2;
                        board[i][j]=0;
                        if ( j<3&&board[i][j+1]!=0){
                            board[i][j]=board[i][j+1];
                            board[i][j+1]=0;
                        }
                        System.out.println(Arrays.deepToString(board));
                        combined[i][j-1]=true;
                        points+=board[i][j-1];
                    }
                }   catch (ArrayIndexOutOfBoundsException e){
                    board[i][j]=board[i][j];
                    System.out.println(Arrays.deepToString(board));
                }
            }
        }
        combined=new boolean[][]{
                {false,false,false,false},
                {false,false,false,false},
                {false,false,false,false},
                {false,false,false,false}};
        System.out.println(Arrays.deepToString(board)+"\npoints="+points);
        random2or4();
    }

    public void moveDown() {
        System.out.println(Arrays.deepToString(board));
        for (int i = 0; i < 4; i++) {
            for (int j =0; j < 4; j++) {
                try{
                    if(board[i+1][j]==0){
                        board[i+1][j]=board[i][j];
                        board[i][j]=0;
                        if ( i>0&&board[i-1][j]!=0){
                            board[i][j]=board[i-1][j];
                            board[i-1][j]=0;
                        }
                        System.out.println(Arrays.deepToString(board));
                    }else if (board[i+1][j]==board[i][j]&&!combined[i][j]){
                        board[i+1][j]=board[i][j]*2;
                        board[i][j]=0;
                        if ( i>0&&board[i-1][j]!=0){
                            board[i][j]=board[i-1][j];
                            board[i-1][j]=0;
                        }
                        System.out.println(Arrays.deepToString(board));
                        combined[i+1][j]=true;
                        points+=board[i+1][j];
                    }
                }   catch (ArrayIndexOutOfBoundsException e){
                    board[i][j]=board[i][j];
                    System.out.println(Arrays.deepToString(board));
                }
            }
        }
        combined=new boolean[][]{
                {false,false,false,false},
                {false,false,false,false},
                {false,false,false,false},
                {false,false,false,false}};
        System.out.println(Arrays.deepToString(board)+"\npoints="+points);
        random2or4();
    }

    public void moveUp() {
        System.out.println(Arrays.deepToString(board));
        for (int i = 3; i > -1; i--) {
            for (int j =0; j < 4; j++) {
                try{
                    if(board[i-1][j]==0){
                        board[i-1][j]=board[i][j];
                        board[i][j]=0;
                        if ( i<3&&board[i+1][j]!=0){
                            board[i][j]=board[i+1][j];
                            board[i+1][j]=0;
                        }
                        System.out.println(Arrays.deepToString(board));
                    }else if (board[i-1][j]==board[i][j]&&!combined[i][j]){
                        board[i-1][j]=board[i][j]*2;
                        board[i][j]=0;
                        if ( i<3&&board[i+1][j]!=0){
                            board[i][j]=board[i+1][j];
                            board[i+1][j]=0;
                        }
                        System.out.println(Arrays.deepToString(board));
                        combined[i-1][j]=true;
                        points+=board[i-1][j];
                    }
                }   catch (ArrayIndexOutOfBoundsException e){
                    board[i][j]=board[i][j];
                    System.out.println(Arrays.deepToString(board));
                }
            }
        }
        combined=new boolean[][]{
                {false,false,false,false},
                {false,false,false,false},
                {false,false,false,false},
                {false,false,false,false}};
        System.out.println(Arrays.deepToString(board)+"\npoints="+points);
        random2or4();
    }

    public boolean lose(){
        if (lowest()==0){
            return false;
        }
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                try{
                    if (board[i][j]!=board[i][j+1]||board[i][j+1]!=0){
                        return true;
                    }
                }catch (ArrayIndexOutOfBoundsException e){
                    if (board[i][j]!=board[i][j-1]||board[i][j-1]!=0){
                        return true;
                    }
                }
                try {
                    if (board[i][j]!=board[i+1][j]||board[i+1][j]!=0){
                        return true;
                    }
                }catch (ArrayIndexOutOfBoundsException e){
                    if (board[i][j]!=board[i-1][j]||board[i-1][j]!=0){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void random2or4(){
        int[][] oldBoard=board;
        Random random=new Random();
        if (lowest()==0){
            if (random.nextInt(0,10)<9){
                for (int i = 0; i < 4; i++) {
                    for (int j = 0; j < 4; j++) {
                        if (random.nextInt(0,16)==1&&board[i][j]==0){
                            board[i][j]=2;
                            return;
                        }
                    }
                }
            }else {
                for (int i = 0; i < 4; i++) {
                    for (int j = 0; j < 4; j++) {
                        if (random.nextInt(0,16)==1&&board[i][j]==0){
                            board[i][j]=4;
                            return;
                        }
                    }
                }
            }
            if (oldBoard==board){
                random2or4();
            }
        }
    }

    public void reset(){
        board=new int[][]{{0,0,0,0},{0,0,0,0},{0,0,0,0},{0,0,0,0}};
        points=0;
    }

    private int lowest(){
        int lowest=Integer.MAX_VALUE;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (board[i][j]<lowest){
                    lowest=board[i][j];
                }
            }
        }
        return lowest;
    }
}
