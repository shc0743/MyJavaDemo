package com.furieau.apps;

import java.io.IOException;
import java.util.Scanner;

public class TicTacToe {
    private static final int BOARD_SIZE = 3;
    private static char[][] board = new char[BOARD_SIZE][BOARD_SIZE];
    private static int cursorX = 0;
    private static int cursorY = 0;
    private static char currentPlayer = 'X';
    private static boolean gameOver = false;
    private static char winner = ' ';

    public static void main(String[] args) {
        initializeBoard();
        printInstructions();
        
        Scanner scanner = new Scanner(System.in);
        
        while (!gameOver) {
            printBoard();
            System.out.println("当前玩家: " + currentPlayer);
            System.out.println("使用方向键移动光标，空格键落子，Q退出");
            
            try {
                int input = getInput();
                
                switch (input) {
                    case 65: // 上箭头
                        moveCursor(0, -1);
                        break;
                    case 66: // 下箭头
                        moveCursor(0, 1);
                        break;
                    case 67: // 右箭头
                        moveCursor(1, 0);
                        break;
                    case 68: // 左箭头
                        moveCursor(-1, 0);
                        break;
                    case 32: // 空格
                        makeMove();
                        break;
                    case 81: // Q
                    case 113: // q
                        System.out.println("游戏结束！");
                        return;
                    default:
                        System.out.println("无效输入！使用方向键移动，空格落子");
                }
                
                checkGameStatus();
                
            } catch (IOException e) {
                System.out.println("输入错误，请重试");
            }
        }
        
        printBoard();
        if (winner != ' ') {
            System.out.println("恭喜！玩家 " + winner + " 获胜！");
        } else {
            System.out.println("平局！");
        }
        scanner.close();
    }
    
    private static void initializeBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j] = ' ';
            }
        }
    }
    
    private static void printInstructions() {
        System.out.println("=== 三子棋游戏 ===");
        System.out.println("控制方式:");
        System.out.println("↑↓←→ 方向键 - 移动光标");
        System.out.println("空格键 - 落子");
        System.out.println("Q键 - 退出游戏");
        System.out.println("==================");
    }
    
    private static void printBoard() {
        clearConsole();
        
        System.out.println("\n   0   1   2");
        System.out.println("  ┌───┬───┬───┐");
        
        for (int i = 0; i < BOARD_SIZE; i++) {
            System.out.print(i + " │");
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (i == cursorY && j == cursorX) {
                    // 光标位置，用[]突出显示
                    System.out.print("[" + board[i][j] + "]│");
                } else {
                    System.out.print(" " + board[i][j] + " │");
                }
            }
            
            if (i < BOARD_SIZE - 1) {
                System.out.println("\n  ├───┼───┼───┤");
            }
        }
        
        System.out.println("\n  └───┴───┴───┘");
    }
    
    private static void clearConsole() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            // 如果清屏失败，至少打印一些空行
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }
    
    private static int getInput() throws IOException {
        // 这个方法需要读取单个字符输入
        // 注意：在有些终端中可能需要调整
        try {
            int input = System.in.read();
            // 清除输入缓冲区
            while (System.in.available() > 0) {
                System.in.read();
            }
            return input;
        } catch (IOException e) {
            throw new IOException("读取输入失败");
        }
    }
    
    private static void moveCursor(int deltaX, int deltaY) {
        int newX = cursorX + deltaX;
        int newY = cursorY + deltaY;
        
        if (newX >= 0 && newX < BOARD_SIZE && newY >= 0 && newY < BOARD_SIZE) {
            cursorX = newX;
            cursorY = newY;
        }
    }
    
    private static void makeMove() {
        if (board[cursorY][cursorX] == ' ') {
            board[cursorY][cursorX] = currentPlayer;
            switchPlayer();
        } else {
            System.out.println("该位置已有棋子！");
            try {
                Thread.sleep(1000); // 暂停1秒显示消息
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    private static void switchPlayer() {
        currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
    }
    
    private static void checkGameStatus() {
        // 检查行
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (board[i][0] != ' ' && board[i][0] == board[i][1] && board[i][1] == board[i][2]) {
                gameOver = true;
                winner = board[i][0];
                return;
            }
        }
        
        // 检查列
        for (int j = 0; j < BOARD_SIZE; j++) {
            if (board[0][j] != ' ' && board[0][j] == board[1][j] && board[1][j] == board[2][j]) {
                gameOver = true;
                winner = board[0][j];
                return;
            }
        }
        
        // 检查对角线
        if (board[0][0] != ' ' && board[0][0] == board[1][1] && board[1][1] == board[2][2]) {
            gameOver = true;
            winner = board[0][0];
            return;
        }
        
        if (board[0][2] != ' ' && board[0][2] == board[1][1] && board[1][1] == board[2][0]) {
            gameOver = true;
            winner = board[0][2];
            return;
        }
        
        // 检查平局
        boolean isFull = true;
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] == ' ') {
                    isFull = false;
                    break;
                }
            }
        }
        
        if (isFull) {
            gameOver = true;
        }
    }
    
    // 备用方案：如果方向键不工作，使用这个版本
    public static void alternativeVersion() {
        Scanner scanner = new Scanner(System.in);
        initializeBoard();
        printInstructions();
        
        System.out.println("使用 WASD 移动，空格落子");
        
        while (!gameOver) {
            printBoard();
            System.out.println("当前玩家: " + currentPlayer);
            System.out.print("输入命令 (WASD移动, 空格落子, Q退出): ");
            String input = scanner.nextLine().toLowerCase();
            
            switch (input) {
                case "w":
                    moveCursor(0, -1);
                    break;
                case "s":
                    moveCursor(0, 1);
                    break;
                case "a":
                    moveCursor(-1, 0);
                    break;
                case "d":
                    moveCursor(1, 0);
                    break;
                case " ":
                    makeMove();
                    break;
                case "q":
                    System.out.println("游戏结束！");
                    return;
                default:
                    System.out.println("无效输入！使用WASD移动，空格落子");
            }
            
            checkGameStatus();
        }
        
        printBoard();
        if (winner != ' ') {
            System.out.println("恭喜！玩家 " + winner + " 获胜！");
        } else {
            System.out.println("平局！");
        }
        scanner.close();
    }
}