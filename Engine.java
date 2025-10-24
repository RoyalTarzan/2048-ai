import java.util.Arrays;

public class Engine {
    int[][] board=new int[][]{
            {0,0,0,0},
            {0,0,0,0},
            {0,0,0,0},
            {0,0,0,0}};
    int points=0;

    public int[][] getBoard() {
        return board;
    }

    public void setBoard(int[][] board){
        this.board=board;
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
                    }else if (board[i][j+1]==board[i][j]){
                        board[i][j+1]=board[i][j]*2;
                        board[i][j]=0;
                        if ( j>0&&board[i][j-1]!=0){
                            board[i][j]=board[i][j-1];
                            board[i][j-1]=0;
                        }
                        points+=board[i][j+1];
                        System.out.println(Arrays.deepToString(board));
                    }else {
                        System.out.println(Arrays.deepToString(board));
                    }
                }   catch (ArrayIndexOutOfBoundsException e){
                    board[i][j]=board[i][j];
                    System.out.println(Arrays.deepToString(board));
                }
            }
        }
        System.out.println(Arrays.deepToString(board)+"\npoints="+points);
        setBoard(board);
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
                    }else if (board[i][j-1]==board[i][j]){
                        board[i][j-1]=board[i][j]*2;
                        board[i][j]=0;
                        if ( j<3&&board[i][j+1]!=0){
                            board[i][j]=board[i][j+1];
                            board[i][j+1]=0;
                        }
                        points+=board[i][j-1];
                        System.out.println(Arrays.deepToString(board));
                    }else {
                        System.out.println(Arrays.deepToString(board));
                    }
                }   catch (ArrayIndexOutOfBoundsException e){
                    board[i][j]=board[i][j];
                    System.out.println(Arrays.deepToString(board));
                }
            }
        }
        System.out.println(Arrays.deepToString(board)+"\npoints="+points);
        setBoard(board);
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
                    }else if (board[i+1][j]==board[i][j]){
                        board[i+1][j]=board[i][j]*2;
                        board[i][j]=0;
                        if ( i>0&&board[i-1][j]!=0){
                            board[i][j]=board[i-1][j];
                            board[i-1][j]=0;
                        }
                        points+=board[i+1][j];
                        System.out.println(Arrays.deepToString(board));
                    }else {
                        System.out.println(Arrays.deepToString(board));
                    }
                }   catch (ArrayIndexOutOfBoundsException e){
                    board[i][j]=board[i][j];
                    System.out.println(Arrays.deepToString(board));
                }
            }
        }
        System.out.println(Arrays.deepToString(board)+"\npoints="+points);
        setBoard(board);
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
                    }else if (board[i-1][j]==board[i][j]){
                        board[i-1][j]=board[i][j]*2;
                        board[i][j]=0;
                        if ( i<3&&board[i+1][j]!=0){
                            board[i][j]=board[i+1][j];
                            board[i+1][j]=0;
                        }
                        points+=board[i-1][j];
                        System.out.println(Arrays.deepToString(board));
                    }else {
                        System.out.println(Arrays.deepToString(board));
                    }
                }   catch (ArrayIndexOutOfBoundsException e){
                    board[i][j]=board[i][j];
                    System.out.println(Arrays.deepToString(board));
                }
            }
        }
        System.out.println(Arrays.deepToString(board)+"\npoints="+points);
        setBoard(board);
    }
}
