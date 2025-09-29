package com.furieau.apps;

import java.util.*;
import java.io.*;

public class ConsoleSnakeGame {
    // 游戏区域大小
    private static final int WIDTH = 20;
    private static final int HEIGHT = 15;
    
    // 游戏元素符号
    private static final char SNAKE_BODY = '■';
    private static final char SNAKE_HEAD = '●';
    private static final char FOOD = '★';
    private static final char EMPTY = '　';
    
    // 方向常量
    private static final int UP = 0;
    private static final int RIGHT = 1;
    private static final int DOWN = 2;
    private static final int LEFT = 3;
    
    // 游戏状态
    private boolean gameOver = false;
    private boolean paused = false;
    private int score = 0;
    
    // 蛇和食物
    private LinkedList<Point> snake;
    private Point food;
    private int direction;
    
    // 随机数生成器
    private Random random;
    
    // 输入读取
    private BufferedReader reader;
    
    // 点类，表示坐标
    private static class Point {
        int x, y;
        
        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Point point = (Point) obj;
            return x == point.x && y == point.y;
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }
    
    public ConsoleSnakeGame() {
        random = new Random();
        reader = new BufferedReader(new InputStreamReader(System.in));
        initializeGame();
    }
    
    // 初始化游戏
    private void initializeGame() {
        // 初始化蛇，长度为3，位于屏幕中央
        snake = new LinkedList<>();
        int startX = WIDTH / 2;
        int startY = HEIGHT / 2;
        snake.add(new Point(startX, startY));
        snake.add(new Point(startX - 1, startY));
        snake.add(new Point(startX - 2, startY));
        
        // 初始方向向右
        direction = RIGHT;
        
        // 生成第一个食物
        generateFood();
        
        // 重置游戏状态
        gameOver = false;
        paused = false;
        score = 0;
    }
    
    // 生成食物
    private void generateFood() {
        while (true) {
            int x = random.nextInt(WIDTH);
            int y = random.nextInt(HEIGHT);
            food = new Point(x, y);
            
            // 确保食物不会生成在蛇身上
            if (!snake.contains(food)) {
                break;
            }
        }
    }
    
    // 绘制游戏界面
    private void draw() {
        // 清屏 - 使用ANSI转义序列
        System.out.print("\033[H\033[2J");
        System.out.flush();
        
        // 绘制上边界
        System.out.print("┌");
        for (int i = 0; i < WIDTH; i++) {
            System.out.print("─");
        }
        System.out.println("┐");
        
        // 绘制游戏区域
        for (int y = 0; y < HEIGHT; y++) {
            System.out.print("│");
            for (int x = 0; x < WIDTH; x++) {
                Point current = new Point(x, y);
                if (snake.getFirst().equals(current)) {
                    System.out.print(SNAKE_HEAD);
                } else if (snake.contains(current)) {
                    System.out.print(SNAKE_BODY);
                } else if (food.equals(current)) {
                    System.out.print(FOOD);
                } else {
                    System.out.print(EMPTY);
                }
            }
            System.out.println("│");
        }
        
        // 绘制下边界
        System.out.print("└");
        for (int i = 0; i < WIDTH; i++) {
            System.out.print("─");
        }
        System.out.println("┘");
        
        // 显示分数和提示
        System.out.println("分数: " + score);
        System.out.println("使用 W A S D 控制方向，P 暂停，R 重新开始，Q 退出");
        if (paused) {
            System.out.println("游戏已暂停，按 P 继续");
        }
        if (gameOver) {
            System.out.println("游戏结束！按 R 重新开始");
        }
    }
    
    // 处理输入 - Android兼容版本
    private void processInput() {
        try {
            if (reader.ready()) {
                int key = reader.read();
                
                // 忽略回车键
                if (key == 10) return;
                
                switch (key) {
                    case 'w':
                    case 'W':
                        if (direction != DOWN) direction = UP;
                        break;
                    case 's':
                    case 'S':
                        if (direction != UP) direction = DOWN;
                        break;
                    case 'a':
                    case 'A':
                        if (direction != RIGHT) direction = LEFT;
                        break;
                    case 'd':
                    case 'D':
                        if (direction != LEFT) direction = RIGHT;
                        break;
                    case 'p':
                    case 'P':
                        paused = !paused;
                        break;
                    case 'r':
                    case 'R':
                        if (gameOver) initializeGame();
                        break;
                    case 'q':
                    case 'Q':
                        System.exit(0);
                        break;
                }
            }
        } catch (IOException e) {
            // 忽略输入异常
        }
    }
    
    // 更新游戏状态
    private void update() {
        if (paused || gameOver) return;
        
        // 计算新的蛇头位置
        Point head = snake.getFirst();
        Point newHead;
        
        switch (direction) {
            case UP:
                newHead = new Point(head.x, head.y - 1);
                break;
            case DOWN:
                newHead = new Point(head.x, head.y + 1);
                break;
            case LEFT:
                newHead = new Point(head.x - 1, head.y);
                break;
            case RIGHT:
                newHead = new Point(head.x + 1, head.y);
                break;
            default:
                newHead = head;
        }
        
        // 检查是否撞墙
        if (newHead.x < 0 || newHead.x >= WIDTH || newHead.y < 0 || newHead.y >= HEIGHT) {
            gameOver = true;
            return;
        }
        
        // 检查是否撞到自己
        if (snake.contains(newHead)) {
            gameOver = true;
            return;
        }
        
        // 移动蛇
        snake.addFirst(newHead);
        
        // 检查是否吃到食物
        if (newHead.equals(food)) {
            score += 10;
            generateFood();
        } else {
            // 如果没有吃到食物，移除蛇尾
            snake.removeLast();
        }
    }
    
    // 运行游戏
    public void run() {
        System.out.println("贪吃蛇游戏开始！");
        System.out.println("使用 W A S D 控制方向，P 暂停，R 重新开始，Q 退出");
        
        // 游戏主循环
        while (true) {
            draw();
            processInput();
            update();
            
            try {
                // 控制游戏速度
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    
    public static void main(String[] args) {
        ConsoleSnakeGame game = new ConsoleSnakeGame();
        game.run();
    }
}