package com.furieau.apps.MazeAdventureGame;

import java.util.Random;
import java.util.Scanner;

/**
 * 位置类 - 表示游戏中的坐标
 */
class Position {
    private int x;
    private int y;
    
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public int getX() { return x; }
    public int getY() { return y; }
    
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    
    public boolean equals(Position other) {
        return this.x == other.x && this.y == other.y;
    }
    
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}

/**
 * 玩家类 - 表示游戏玩家
 */
class Player {
    private Position position;
    private int health;
    private int score;
    private final int maxHealth = 100;
    
    public Player(int startX, int startY) {
        this.position = new Position(startX, startY);
        this.health = maxHealth;
        this.score = 0;
    }
    
    public Position getPosition() { return position; }
    public int getHealth() { return health; }
    public int getScore() { return score; }
    
    public void move(int dx, int dy) {
        position.setX(position.getX() + dx);
        position.setY(position.getY() + dy);
    }
    
    public void takeDamage(int damage) {
        health = Math.max(0, health - damage);
    }
    
    public void heal(int amount) {
        health = Math.min(maxHealth, health + amount);
    }
    
    public void addScore(int points) {
        score += points;
    }
    
    public boolean isAlive() {
        return health > 0;
    }
    
    public String getStatus() {
        return "生命值: " + health + "/" + maxHealth + " | 分数: " + score;
    }
}

/**
 * 游戏物品接口
 */
interface GameItem {
    void interact(Player player);
    char getSymbol();
    String getDescription();
}

/**
 * 宝藏类
 */
class Treasure implements GameItem {
    private int value;
    
    public Treasure(int value) {
        this.value = value;
    }
    
    @Override
    public void interact(Player player) {
        player.addScore(value);
        System.out.println("✨ 找到了宝藏！获得 " + value + " 分！");
    }
    
    @Override
    public char getSymbol() {
        return 'T';
    }
    
    @Override
    public String getDescription() {
        return "宝藏 - 获得分数";
    }
}

/**
 * 陷阱类
 */
class Trap implements GameItem {
    private int damage;
    
    public Trap(int damage) {
        this.damage = damage;
    }
    
    @Override
    public void interact(Player player) {
        player.takeDamage(damage);
        System.out.println("💥 踩到陷阱！受到 " + damage + " 点伤害！");
    }
    
    @Override
    public char getSymbol() {
        return 'X';
    }
    
    @Override
    public String getDescription() {
        return "陷阱 - 造成伤害";
    }
}

/**
 * 治疗药水类
 */
class HealthPotion implements GameItem {
    private int healAmount;
    
    public HealthPotion(int healAmount) {
        this.healAmount = healAmount;
    }
    
    @Override
    public void interact(Player player) {
        player.heal(healAmount);
        System.out.println("❤️ 喝下治疗药水！恢复 " + healAmount + " 点生命值！");
    }
    
    @Override
    public char getSymbol() {
        return 'H';
    }
    
    @Override
    public String getDescription() {
        return "治疗药水 - 恢复生命值";
    }
}

/**
 * 迷宫地图类
 */
class Maze {
    private char[][] grid;
    private int width;
    private int height;
    private GameItem[][] items;
    private Position exit;
    
    public Maze(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new char[height][width];
        this.items = new GameItem[height][width];
        initializeMaze();
        placeItems();
        placeExit();
    }
    
    private void initializeMaze() {
        // 初始化迷宫，' '代表可走路径，'#'代表墙
        Random rand = new Random();
        
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                // 边界是墙
                if (i == 0 || i == height - 1 || j == 0 || j == width - 1) {
                    grid[i][j] = '#';
                } else {
                    // 随机生成一些墙
                    if (rand.nextDouble() < 0.2) {
                        grid[i][j] = '#';
                    } else {
                        grid[i][j] = ' ';
                    }
                }
            }
        }
        
        // 确保起点和终点区域是空的
        grid[1][1] = ' ';  // 起点
        grid[height-2][width-2] = ' ';  // 终点区域
    }
    
    private void placeItems() {
        Random rand = new Random();
        
        for (int i = 1; i < height - 1; i++) {
            for (int j = 1; j < width - 1; j++) {
                if (grid[i][j] == ' ') {
                    double chance = rand.nextDouble();
                    if (chance < 0.1) { // 10%几率生成宝藏
                        items[i][j] = new Treasure(rand.nextInt(50) + 25);
                    } else if (chance < 0.2) { // 10%几率生成陷阱
                        items[i][j] = new Trap(rand.nextInt(20) + 10);
                    } else if (chance < 0.25) { // 5%几率生成治疗药水
                        items[i][j] = new HealthPotion(rand.nextInt(30) + 20);
                    }
                }
            }
        }
    }
    
    private void placeExit() {
        exit = new Position(width - 2, height - 2);
        grid[exit.getY()][exit.getX()] = 'E';
    }
    
    public boolean isValidMove(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height && grid[y][x] != '#';
    }
    
    public boolean isExit(int x, int y) {
        return x == exit.getX() && y == exit.getY();
    }
    
    public GameItem getItemAt(int x, int y) {
        return items[y][x];
    }
    
    public void removeItemAt(int x, int y) {
        items[y][x] = null;
    }
    
    public void display(Player player) {
        System.out.println("\n迷宫地图:");
        System.out.println("══════════════════════════════════");
        
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Position playerPos = player.getPosition();
                if (playerPos.getX() == j && playerPos.getY() == i) {
                    System.out.print('P'); // 玩家位置
                } else if (items[i][j] != null && grid[i][j] != 'E') {
                    System.out.print(items[i][j].getSymbol()); // 物品
                } else {
                    System.out.print(grid[i][j]); // 地图元素
                }
                System.out.print(' ');
            }
            System.out.println();
        }
        
        System.out.println("══════════════════════════════════");
        System.out.println("图例: P=玩家, #=墙, T=宝藏, X=陷阱, H=药水, E=出口");
        System.out.println("══════════════════════════════════");
    }
    
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}

/**
 * 游戏控制类
 */
class GameController {
    private Player player;
    private Maze maze;
    private Scanner scanner;
    private boolean gameRunning;
    
    public GameController() {
        this.scanner = new Scanner(System.in);
        initializeGame();
    }
    
    private void initializeGame() {
        System.out.println("🎮 欢迎来到迷宫探险游戏！");
        System.out.println("══════════════════════════════════");
        
        maze = new Maze(15, 10);
        player = new Player(1, 1);
        gameRunning = true;
        
        displayInstructions();
    }
    
    private void displayInstructions() {
        System.out.println("\n游戏说明:");
        System.out.println("• 使用 W(上), A(左), S(下), D(右) 移动");
        System.out.println("• 找到宝藏(T)获得分数");
        System.out.println("• 避开陷阱(X)以免受伤");
        System.out.println("• 收集治疗药水(H)恢复生命");
        System.out.println("• 找到出口(E)赢得游戏");
        System.out.println("• 输入 Q 退出游戏");
        System.out.println("══════════════════════════════════");
    }
    
    public void startGame() {
        while (gameRunning && player.isAlive()) {
            maze.display(player);
            System.out.println(player.getStatus());
            System.out.println("位置: " + player.getPosition());
            
            if (checkWinCondition()) {
                break;
            }
            
            processPlayerInput();
            System.out.println();
        }
        
        endGame();
    }
    
    private boolean checkWinCondition() {
        Position pos = player.getPosition();
        if (maze.isExit(pos.getX(), pos.getY())) {
            System.out.println("\n🎉 恭喜！你成功逃出迷宫！");
            System.out.println("最终分数: " + player.getScore());
            return true;
        }
        return false;
    }
    
    private void processPlayerInput() {
        System.out.print("请输入移动方向 (W/A/S/D): ");
        String input = scanner.nextLine().trim().toUpperCase();
        
        int dx = 0, dy = 0;
        
        switch (input) {
            case "W": dy = -1; break;
            case "S": dy = 1; break;
            case "A": dx = -1; break;
            case "D": dx = 1; break;
            case "Q": 
                gameRunning = false;
                System.out.println("游戏结束！");
                return;
            default:
                System.out.println("无效输入！请使用 W, A, S, D 移动");
                return;
        }
        
        movePlayer(dx, dy);
    }
    
    private void movePlayer(int dx, int dy) {
        Position currentPos = player.getPosition();
        int newX = currentPos.getX() + dx;
        int newY = currentPos.getY() + dy;
        
        if (maze.isValidMove(newX, newY)) {
            player.move(dx, dy);
            checkForItems(newX, newY);
        } else {
            System.out.println("无法往这个方向移动！");
        }
    }
    
    private void checkForItems(int x, int y) {
        GameItem item = maze.getItemAt(x, y);
        if (item != null) {
            item.interact(player);
            maze.removeItemAt(x, y);
        }
    }
    
    private void endGame() {
        if (!player.isAlive()) {
            System.out.println("\n💀 游戏结束！你的生命值已归零！");
            System.out.println("最终分数: " + player.getScore());
        }
        
        scanner.close();
        System.out.println("\n感谢游玩！");
    }
}

/**
 * 游戏主类
 */
public class MazeAdventureGame {
    public static void main(String[] args) {
        GameController game = new GameController();
        game.startGame();
    }
}