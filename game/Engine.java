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

    public Engine(){
        random2or4();
    }

    public int[][] getBoard() {
        return board;
    }

    /*public void setBoard(int[][] board){
        this.board=board;
    }*/

    public int getPoints() {
        return points;
    }

    public void moveRight(){}

    public void moveLeft() {}

    public void moveDown() {}

    public void moveUp() {}

    public boolean move(int up,int left){
        int iDis;
        int iStart;
        int iDir;
        int jDis;
        int jStart;
        int jDir;
        if (1==up){
            iStart=3;
            iDis=-1;
            iDir=-1;
        }else if(up==-1){
            iStart=0;
            iDis=1;
            iDir=1;
        }else {
            iStart=0;
            iDis=0;
            iDir=1;
        }
        if (left==1){
            jStart=3;
            jDis=-1;
            jDir=-1;
        }else if(left==-1){
            jStart=0;
            jDis=1;
            jDir=1;
        }else {
            jStart=0;
            jDis=0;
            jDir=1;
        }
        int[][] oldBoard=board.clone();
        System.out.println("Old board: "+ Arrays.deepToString(oldBoard));
        for (int i = 0; i <4; i++) {
            int iLoc=iStart+(i*iDir);
            for (int j =0; j < 4; j++) {
                int jLoc=jStart+(j*jDir);
                try{
                    if(board[iLoc+iDis][jLoc+jDis]==0){
                        board[iLoc+iDis][jLoc+jDis]=board[iLoc][jLoc];
                        board[iLoc][jLoc]=0;
                        if ( iLoc>0&&board[iLoc-iDis][jLoc-jDis]!=0){
                            board[iLoc][jLoc]=board[iLoc-iDis][jLoc-jDis];
                            board[iLoc-iDis][jLoc-jDis]=0;
                        }
                    }else if (board[iLoc+iDis][jLoc+jDis]==board[iLoc][jLoc]&&!combined[iLoc][jLoc]){
                        board[iLoc+iDis][jLoc+jDis]=board[iLoc][jLoc]*2;
                        board[iLoc][jLoc]=0;
                        if ( iLoc>0&&board[iLoc-iDis][jLoc-jDis]!=0){
                            board[iLoc][jLoc]=board[iLoc-iDis][jLoc-jDis];
                            board[iLoc-iDis][jLoc-jDis]=0;
                        }
                        combined[iLoc+iDis][jLoc+jDis]=true;
                        points+=board[iLoc+iDis][jLoc+jDis];
                    }
                }   catch (ArrayIndexOutOfBoundsException _){
                }
            }
        }
        System.out.println("New board: "+ Arrays.deepToString(board));
        boolean added2Or4=false;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j]==oldBoard[i][j]){continue;}
                random2or4();
                System.out.println("Added random 2 or 4");
                added2Or4=true;
                break;
            }
            if (added2Or4){break;}
        }
        combined=new boolean[][]{
                {false,false,false,false},
                {false,false,false,false},
                {false,false,false,false},
                {false,false,false,false}};
        return added2Or4;
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
                try{
                    if (board[i][j]!=board[i][j-1]||board[i][j-1]!=0){
                        return true;
                    }
                }catch (ArrayIndexOutOfBoundsException ignored){}
                try {
                    if (board[i][j] != board[i + 1][j]){
                        return true;
                    }
                }catch (ArrayIndexOutOfBoundsException e){
                    if (board[i][j]!=board[i-1][j]||board[i-1][j]!=0){
                        return true;
                    }
                }
                try {
                    if (board[i][j]!=board[i-1][j]||board[i-1][j]!=0){
                        return true;
                    }
                }catch (ArrayIndexOutOfBoundsException ignored){}
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
        random2or4();
    }

    private int lowest(){
        int lowest=board[0][0];
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
