import java.util.*;
import java.math.*;

class Chess {
  public static void main(String[] args) {
    Scanner in = new Scanner(System.in);
    System.out.print("Numbers of players: ");
    int numberOfPlayers = in.nextInt();
    char board[][] = new char[3][3];
    for (int i = 0; i < 3; i++) {
      board[i] = new char[3];
      for (int j = 0; j < 3; j++) {
        board[i][j] = '-';
      }
    }


    boolean invokeComputer = numberOfPlayers == 1;

    Random random = new Random();

    int turns = 0;

    if (invokeComputer) {
      System.out.print("Enter 1 if you want the computer to go first, 0 otherwise: ");
      turns = in.nextInt();
    }

    Chess.printBoard(board);

    int whoWon = 0;

    while (!Chess.isBoardFull(board)) {
      int response = Chess.getInput(turns, board, in, invokeComputer);
      int _i = response / 3;
      int _j = response % 3;

      board[_i][_j] = (turns % 2 + "").charAt(0);

      Chess.printBoard(board);

      turns++;

      whoWon = Chess.whoWon(board);
      if (whoWon != 0) break;
    }

    // whoWon = Chess.whoWon(board);

    if (whoWon == 0) {
      System.out.println("It was a tie!");
    } else if (whoWon == 1) {
      System.out.println("Player 1 won!");
    } else if (whoWon == 2) {
      if (invokeComputer) {
        System.out.println("Computer won!");
      } else {
        System.out.println("Player 2 won!");
      }
    }
  }

  public static int getInt (char c) {
    return Integer.parseInt(c + "");
  }

  public static void printBoard (char board[][]) {
    System.out.println("");
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        System.out.print(board[i][j] + "\t");
      }
      System.out.println("");
    }
    System.out.println("");
  }

  public static int getInput (int turn, char board[][], Scanner in, boolean computer) {
    int _turn = turn;
    turn = turn % 2;
    turn++;

    if (turn == 2 && computer) {
      int res = Chess.getComputerInput(_turn, board);
      System.out.println("Computer (Symbol 1): " + res);
      return --res;
    }
    System.out.print("Player " + turn + " (Symbol " + (turn == 1 ? '0' : '1') + "): ");
    int response = in.nextInt();

    if (!Chess.isInputValid(board, response)) return Chess.getInput(_turn, board, in, computer);

    return --response;
  }

  public static boolean isInputValid (char board[][], int response) {

    if (response < 1 || response > 9) return false;

    response--;
    int i = response / 3;
    int j = response % 3;

    if (board[i][j] != '-') return false;

    return true;

  }

  public static boolean isBoardFull (char board[][]) {
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        if (board[i][j] == '-') return false;
      }
    }
    return true;
  }

  public static int whoWon (char board[][]) {
    if (didWin(board, '0')) return 1;
    else if (didWin(board, '1')) return 2;
    else return 0;
  }

  public static boolean didWin (char board[][], char c) {
    
    // Check rows
    for (int i = 0; i < 3; i++) {
      if (board[i][0] == c && board[i][1] == c && board[i][2] == c) return true;
    }

    // Check columns
    for (int i = 0; i < 3; i++) {
      if (board[0][i] == c && board[1][i] == c && board[2][i] == c) return true;
    }

    // Check diagonals
    if (board[0][0] == c && board[1][1] == c && board[2][2] == c) return true;
    if (board[0][2] == c && board[1][1] == c && board[2][0] == c) return true;

    return false;
  }

  public static int getComputerInput (int turns, char board[][]) {
    // return Chess.getComputerInputRandomly(turns, board);
    return Chess.getComputerInputIntelligently(turns, board);
  }

  public static int getComputerInputRandomly (int turns, char board[][]) {
    Random random = new Random();
    int i = random.nextInt(9);

    // System.out.println("Returning input randomly!");

    if (Chess.isInputValid(board, i - 1)) return --i;
    else return Chess.getComputerInputRandomly(turns, board);
  }

  public static int getComputerInputIntelligently(int turns, char board[][]) {
    int response = 0;

    // Try to win
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        char copy[][] = Chess.getBoardCopy(board);
        response = (i * 3) + j + 1;
        if (Chess.isInputValid(copy, response)) {
          int _i = (response - 1) / 3;
          int _j = (response - 1) % 3;
          // System.out.println("Response_w: " + response);
          copy[_i][_j] = '1';
          if (Chess.didWin(copy, '1')) {
            // System.out.println("Winning..");
            return response;
          }
        }
      }
    }

    // Try to block if there are two or more than winnable paths
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        char copy[][] = Chess.getBoardCopy(board);
        response = (i * 3) + j + 1;
        if (Chess.isInputValid(copy, response)) {
          int winnable = Chess.countWinnable(board, '0');
          if (winnable == 2) {
            // System.out.println("There are " + winnable + " winnable paths.");
            return response;
          }
        }
      }
    }

    // Try to block a single winnable path
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        char copy[][] = Chess.getBoardCopy(board);
        response = (i * 3) + j + 1;
        if (Chess.isInputValid(copy, response)) {
          int _i = (response - 1) / 3;
          int _j = (response - 1) % 3;
          // System.out.println("Response_b: " + response);
          copy[_i][_j] = '0';
          if (Chess.didWin(copy, '0')) {
            // System.out.println("Blocking single winnable path!"); 
            return response;
          }
        }
      }
    }

    // Try to acquire middle
    if (Chess.isInputValid(board, 5)) return 5;

    // Try to acquire corners
    ArrayList<Integer> cornerList = new ArrayList<Integer>();
    Random random = new Random();
    while (cornerList.size() != 4) {
      int r = random.nextInt(4);
      if (!cornerList.contains(r)) {
        cornerList.add(r);
      }
    }
    for (int i = 0; i < cornerList.size(); i++) {
      int corner = cornerList.get(i);
      switch (corner) {
        case 0:
          if (Chess.isInputValid(board, 1)) {
            if (board[2][2] != '0') return 1;
          }
          break;
        case 1:
          if (Chess.isInputValid(board, 3)) {
            if (board[2][0] != '0') return 3;
          }
          break;
        case 2:
          if (Chess.isInputValid(board, 7)) {
            if (board[0][2] != '0') return 7;
          }
          break;
        case 3:
          if (Chess.isInputValid(board, 9)) {
            if (board[0][0] != '0') return 9;
          }
          break;
        default: break;
      }
    }

    return Chess.getComputerInputRandomly(turns, board);
  }

  public static char[][] getBoardCopy(char board[][]) {
    char copy[][] = new char[3][3];
    for (int i = 0; i < 3; i++) {
      copy[i] = new char[3];
      for (int j = 0; j < 3; j++) {
        copy[i][j] = board[i][j];
      }
    }
    return copy;
  }

  public static int countWinnable (char board[][], char c) {
    int winnable = 0;
    int response = 0;
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        char copy[][] = Chess.getBoardCopy(board);
        response = (i * 3) + j + 1;
        if (Chess.isInputValid(copy, response)) {
          int _i = (response - 1) / 3;
          int _j = (response - 1) % 3;
          copy[_i][_j] = c;
          if (Chess.didWin(copy, c)) winnable++;
        }
      }
    }
    return winnable;
  }
}