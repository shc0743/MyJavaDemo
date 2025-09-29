package com.furieau.apps;

import java.util.Scanner;
import java.util.Random;
import java.util.HashMap;

public class TextAdventureGame {
    private static Scanner scanner = new Scanner(System.in);
    private static Random random = new Random();
    
    // 玩家属性
    private static int playerHealth = 100;
    private static int playerAttack = 15;
    private static int playerDefense = 5;
    private static int playerGold = 0;
    private static int playerLevel = 1;
    private static int experience = 0;
    private static HashMap<String, Integer> inventory = new HashMap<>();
    
    // 游戏地图
    private static String[][] map = {
        {"森林", "河流", "山洞"},
        {"村庄", "平原", "沼泽"},
        {"山脉", "沙漠", "城堡"}
    };
    private static int playerX = 1;
    private static int playerY = 1;
    
    public static void main(String[] args) {
        System.out.println("=== 文字冒险游戏 ===");
        System.out.println("欢迎来到冒险世界！");
        System.out.println("输入 'help' 查看可用命令");
        
        // 初始化物品
        inventory.put("治疗药水", 2);
        inventory.put("剑", 1);
        
        boolean playing = true;
        
        while (playing && playerHealth > 0) {
            displayLocation();
            System.out.print("\n你要做什么？> ");
            String input = scanner.nextLine().toLowerCase();
            
            switch (input) {
                case "help":
                    showHelp();
                    break;
                case "move":
                    movePlayer();
                    break;
                case "map":
                    showMap();
                    break;
                case "status":
                    showStatus();
                    break;
                case "inventory":
                    showInventory();
                    break;
                case "explore":
                    explore();
                    break;
                case "use":
                    useItem();
                    break;
                case "quit":
                    playing = false;
                    break;
                default:
                    System.out.println("未知命令，输入 'help' 查看可用命令");
            }
            
            // 随机事件
            if (random.nextInt(100) < 25) { // 25% 几率遇到事件
                randomEvent();
            }
            
            // 检查升级
            checkLevelUp();
        }
        
        if (playerHealth <= 0) {
            System.out.println("\n游戏结束！你被击败了...");
        } else {
            System.out.println("\n感谢游玩！再见！");
        }
    }
    
    private static void displayLocation() {
        System.out.println("\n=================================");
        System.out.println("当前位置: " + map[playerY][playerX]);
        System.out.println("坐标: (" + playerX + ", " + playerY + ")");
        System.out.println("=================================");
    }
    
    private static void showHelp() {
        System.out.println("\n可用命令:");
        System.out.println("help    - 显示帮助信息");
        System.out.println("move    - 移动角色");
        System.out.println("map     - 显示地图");
        System.out.println("status  - 显示角色状态");
        System.out.println("inventory - 查看背包");
        System.out.println("explore - 探索当前区域");
        System.out.println("use     - 使用物品");
        System.out.println("quit    - 退出游戏");
    }
    
    private static void movePlayer() {
        System.out.print("移动方向 (n-北, s-南, e-东, w-西): ");
        String direction = scanner.nextLine().toLowerCase();
        
        switch (direction) {
            case "n": // 北
                if (playerY > 0) playerY--;
                else System.out.println("无法向北移动！");
                break;
            case "s": // 南
                if (playerY < 2) playerY++;
                else System.out.println("无法向南移动！");
                break;
            case "e": // 东
                if (playerX < 2) playerX++;
                else System.out.println("无法向东移动！");
                break;
            case "w": // 西
                if (playerX > 0) playerX--;
                else System.out.println("无法向西移动！");
                break;
            default:
                System.out.println("无效方向！");
                return;
        }
        System.out.println("你移动到了: " + map[playerY][playerX]);
    }
    
    private static void showMap() {
        System.out.println("\n=== 世界地图 ===");
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                if (x == playerX && y == playerY) {
                    System.out.print("[你] ");
                } else {
                    System.out.print("[" + map[y][x] + "] ");
                }
            }
            System.out.println();
        }
    }
    
    private static void showStatus() {
        System.out.println("\n=== 角色状态 ===");
        System.out.println("生命值: " + playerHealth + "/100");
        System.out.println("等级: " + playerLevel);
        System.out.println("经验: " + experience + "/" + (playerLevel * 50));
        System.out.println("攻击力: " + playerAttack);
        System.out.println("防御力: " + playerDefense);
        System.out.println("金币: " + playerGold);
    }
    
    private static void showInventory() {
        System.out.println("\n=== 背包 ===");
        if (inventory.isEmpty()) {
            System.out.println("背包空空如也...");
        } else {
            for (String item : inventory.keySet()) {
                System.out.println(item + ": " + inventory.get(item));
            }
        }
    }
    
    private static void explore() {
        System.out.println("\n你在 " + map[playerY][playerX] + " 中探索...");
        
        int event = random.nextInt(100);
        
        if (event < 40) { // 40% 几率找到物品
            findItem();
        } else if (event < 70) { // 30% 几率遇到怪物
            encounterMonster();
        } else if (event < 85) { // 15% 几率找到金币
            findGold();
        } else { // 15% 几率无事发生
            System.out.println("你探索了一番，但没有发现什么特别的东西。");
        }
    }
    
    private static void findItem() {
        String[] items = {"治疗药水", "力量药水", "防御药水", "魔法剑"};
        String foundItem = items[random.nextInt(items.length)];
        
        inventory.put(foundItem, inventory.getOrDefault(foundItem, 0) + 1);
        System.out.println("你找到了: " + foundItem + "!");
    }
    
    private static void findGold() {
        int gold = random.nextInt(50) + 10;
        playerGold += gold;
        System.out.println("你找到了 " + gold + " 枚金币!");
    }
    
    private static void encounterMonster() {
        String[] monsters = {"哥布林", "骷髅", "巨蜘蛛", "狼人"};
        String monster = monsters[random.nextInt(monsters.length)];
        int monsterHealth = random.nextInt(30) + 20;
        int monsterAttack = random.nextInt(10) + 5;
        
        System.out.println("你遇到了 " + monster + "!");
        System.out.println("怪物生命值: " + monsterHealth);
        
        while (monsterHealth > 0 && playerHealth > 0) {
            System.out.println("\n你的生命值: " + playerHealth);
            System.out.println(monster + " 的生命值: " + monsterHealth);
            System.out.print("你要做什么？(a-攻击, r-逃跑): ");
            String action = scanner.nextLine().toLowerCase();
            
            if (action.equals("a")) {
                // 玩家攻击
                int damage = playerAttack + random.nextInt(10);
                monsterHealth -= damage;
                System.out.println("你对 " + monster + " 造成了 " + damage + " 点伤害!");
                
                if (monsterHealth > 0) {
                    // 怪物反击
                    int monsterDamage = Math.max(0, monsterAttack - playerDefense + random.nextInt(5));
                    playerHealth -= monsterDamage;
                    System.out.println(monster + " 对你造成了 " + monsterDamage + " 点伤害!");
                }
            } else if (action.equals("r")) {
                if (random.nextInt(100) < 50) { // 50% 逃跑成功率
                    System.out.println("你成功逃跑了!");
                    return;
                } else {
                    System.out.println("逃跑失败!");
                    int monsterDamage = Math.max(0, monsterAttack - playerDefense + random.nextInt(5));
                    playerHealth -= monsterDamage;
                    System.out.println(monster + " 对你造成了 " + monsterDamage + " 点伤害!");
                }
            }
        }
        
        if (monsterHealth <= 0) {
            int expGained = random.nextInt(20) + 10;
            int goldGained = random.nextInt(30) + 5;
            experience += expGained;
            playerGold += goldGained;
            System.out.println("你击败了 " + monster + "!");
            System.out.println("获得 " + expGained + " 经验值!");
            System.out.println("获得 " + goldGained + " 金币!");
        }
    }
    
    private static void useItem() {
        showInventory();
        System.out.print("使用哪个物品？> ");
        String item = scanner.nextLine();
        
        if (inventory.containsKey(item) && inventory.get(item) > 0) {
            switch (item) {
                case "治疗药水":
                    playerHealth = Math.min(100, playerHealth + 30);
                    System.out.println("使用治疗药水，恢复30点生命值!");
                    break;
                case "力量药水":
                    playerAttack += 5;
                    System.out.println("使用力量药水，攻击力增加5点!");
                    break;
                case "防御药水":
                    playerDefense += 3;
                    System.out.println("使用防御药水，防御力增加3点!");
                    break;
                default:
                    System.out.println("这个物品无法直接使用!");
                    return;
            }
            inventory.put(item, inventory.get(item) - 1);
            if (inventory.get(item) == 0) {
                inventory.remove(item);
            }
        } else {
            System.out.println("没有这个物品或数量不足!");
        }
    }
    
    private static void randomEvent() {
        int event = random.nextInt(100);
        
        if (event < 20) { // 20% 几率遇到好事
            System.out.println("\n*** 随机事件: 你发现了一个宝箱！ ***");
            int gold = random.nextInt(100) + 50;
            playerGold += gold;
            System.out.println("获得 " + gold + " 金币!");
        } else if (event < 35) { // 15% 几率遇到坏事
            System.out.println("\n*** 随机事件: 你踩到了陷阱！ ***");
            int damage = random.nextInt(20) + 5;
            playerHealth -= damage;
            System.out.println("受到 " + damage + " 点伤害!");
        }
    }
    
    private static void checkLevelUp() {
        int requiredExp = playerLevel * 50;
        if (experience >= requiredExp) {
            playerLevel++;
            experience = 0;
            playerAttack += 3;
            playerDefense += 2;
            playerHealth = 100; // 升级恢复生命值
            System.out.println("\n*** 恭喜！你升级到 " + playerLevel + " 级！ ***");
            System.out.println("攻击力 +3, 防御力 +2, 生命值恢复!");
        }
    }
}