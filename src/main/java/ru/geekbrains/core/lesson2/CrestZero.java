package ru.geekbrains.core.lesson2;

import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class CrestZero {
    private final String DOT_HUMAN = "\u001B[31mX\u001B[0m";
    private final String DOT_AI = "\u001B[34m0\u001B[0m";
    private final String DOT_EMPTY = "*";
    private final Scanner scanner = new Scanner(System.in);
    private final Random random = new Random();
    public String[][] field;
    private int WIN_COUNT;
    private int RESISTANCE;

    public CrestZero(int fieldSize, int winCount, int resistance) {
        field = new String[fieldSize][fieldSize];
        WIN_COUNT = winCount < fieldSize ? winCount : fieldSize;
        RESISTANCE = resistance < WIN_COUNT - 2 ? resistance : WIN_COUNT - 2;
        for (int y = 0; y < fieldSize; y++){
            for (int x = 0; x < fieldSize; x++){
                field[y][x] = DOT_EMPTY;
            }
        }
    }

    /**
     * Распечатать игровое поле
     */
    public void printField(){
        System.out.print("+");
        for (int i = 0; i < field[0].length; i++){
            System.out.print("-" + (i + 1));
        }
        System.out.println("-");

        for (int y = 0; y < field.length; y++){
            System.out.print(y + 1 + "|");
            for (int x = 0; x < field[0].length; x++){
                System.out.print(field[y][x] + "|");
            }
            System.out.println();
        }

        for (int i = 0; i < field[0].length * 2 + 2; i++){
            System.out.print("-");
        }
        System.out.println();
    }

    /**
     * Проверка, является ли ячейка игрового поля пустой
     * @param x координата
     * @param y координата
     * @return результат проверки
     */
    private boolean isCellEmpty(int x, int y){
        return field[x][y] == DOT_EMPTY;
    }

    /**
     * Проверка валидности координат хода
     * @param x координата
     * @param y координата
     * @return результат проверки
     */
    private boolean isCellValid(int x, int y){
        return x >= 0 && x < field[0].length && y >= 0 && y < field.length;
    }

    //GAME/////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Метод запускающий игру
     */
    public void game(){
        printField();
        boolean continueGame = true;
        while (continueGame){
            String state = step();
            if(state.equals("далее")){
                continueGame = true;
            } else {
                continueGame = false;
                System.out.println(state);
            }
        }
    }

    /**
     * Метод одного хода в игре состоящего их хода игрока и Al
     * @return state
     */
    public String step(){
        String state = "далее";

        boolean draw = checkDraw();
        if(draw){
            state = "Ничья";
        }

        boolean winHu = humanTurn();
        if(winHu){
            state = "Вы победили";
        }

        boolean winAL = aiTurn();
        if(winAL && !winHu){
            state = "Вы проиграли";
        }

        printField();

        return state;
    }

    /**
     * Ход игрока (человека)
     */
    private boolean humanTurn(){
        int x;
        int y;
        do {
            System.out.print("Введите координаты хода X(от 1 до " + field[0].length + ") и Y(от 1 до "+ field.length + ") через пробел: ");
            x = scanner.nextInt() - 1;
            y = scanner.nextInt() - 1;
        } while (!isCellValid(y, x) || !isCellEmpty(y, x));
        field[y][x] = DOT_HUMAN;

        return checkWin(DOT_HUMAN);
    }

    /**
     * Ход игрока (компьютера)
     */
    private boolean aiTurn(){
        boolean canBlock = smartTurn2(0,0, RESISTANCE);
        if(!canBlock){
            int x;
            int y;
            do{
                x = random.nextInt(field[0].length);
                y = random.nextInt(field.length);
            } while (!isCellEmpty(y, x));
            field[y][x] = DOT_AI;
        }

        return checkWin(DOT_AI);
    }

    private boolean smartTurn2(int delta_y, int delta_x, int resistance){
        if (resistance >= 0){
            int pre_win_count = WIN_COUNT - resistance;
            int[] preWinX = checkWinX(DOT_HUMAN, pre_win_count, delta_y);

            if(preWinX != null) {
                int indexY;
                int indexX;
                if(preWinX[2] == 0){
                    indexY = preWinX[0];
                    indexX = preWinX[1] - pre_win_count;
                    if(isCellValid(indexY, indexX) && isCellEmpty(indexY, indexX)){
                        field[indexY][indexX] = DOT_AI;
                        return true;
                    }
                    indexX = preWinX[1] + 1;
                    if(isCellValid(indexY, indexX) && isCellEmpty(indexY, indexX)){
                        field[indexY][indexX] = DOT_AI;
                        return true;
                    }
                } else {
                    indexY = preWinX[0] - pre_win_count;
                    indexX = preWinX[1] - pre_win_count;
                    if(isCellValid(indexY, indexX) && isCellEmpty(indexY, indexX)){
                        field[indexY][indexX] = DOT_AI;
                        return true;
                    }
                    indexY = preWinX[0] + 1;
                    indexX = preWinX[1] + 1;
                    if(isCellValid(indexY, indexX) && isCellEmpty(indexY, indexX)){
                        field[indexY][indexX] = DOT_AI;
                        return true;
                    }
                }
            }

            int[] preWinY = checkWinY(DOT_HUMAN, pre_win_count, delta_x);
            if(preWinY != null) {
                int indexY;
                int indexX;
                if(preWinY[2] == 0){
                    indexY = preWinY[0] - pre_win_count;
                    indexX = preWinY[1];
                    if(isCellValid(indexY, indexX) && isCellEmpty(indexY, indexX)){
                        field[indexY][indexX] = DOT_AI;
                        return true;
                    }
                    indexY = preWinY[0] + 1;
                    if(isCellValid(indexY, indexX) && isCellEmpty(indexY, indexX)){
                        field[indexY][indexX] = DOT_AI;
                        return true;
                    }
                } else {
                    indexY = preWinY[0] - pre_win_count;
                    indexX = preWinY[1] + pre_win_count;
                    if(isCellValid(indexY, indexX) && isCellEmpty(indexY, indexX)){
                        field[indexY][indexX] = DOT_AI;
                        return true;
                    }
                    indexY = preWinY[0] + 1;
                    indexX = preWinY[1] - 1;
                    if(isCellValid(indexY, indexX) && isCellEmpty(indexY, indexX)){
                        field[indexY][indexX] = DOT_AI;
                        return true;
                    }
                }
            }

            if(delta_y < field.length){
                smartTurn2(++delta_y, delta_x, resistance);
            }
            if(delta_x < field[0].length){
                smartTurn2(delta_y, ++delta_x, resistance);
            }

            smartTurn2(0, 0, --resistance);
        }

        return false;
    }
    //GAME/////////////////////////////////////////////////////////////////////////////////////////////////////

    //WIN/////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Проверяет пришли ли игроки к ничье
     * @return checkValue
     */
    public boolean checkDraw(){
        boolean isDraw = true;
        for (int y = 0; y < field.length; y++){
            for (int x = 0; x < field[0].length; x++){
                isDraw &= (field[y][x] != DOT_EMPTY);
            }
        }

        return isDraw;
    }

    /**
     * Проверяет выйгрышные комбинации на поле
     * @param dot
     * @return checkValue
     */
    public boolean checkWin(String dot){
        return checkWinX(dot, WIN_COUNT, 0) != null | checkWinY(dot, WIN_COUNT, 0) != null;
    }

    /**
     * Проверяет выйрушную комбинацию по оси X и диагонали с лево на право
     * @param dot
     * @return checkValue
     */
    private int[] checkWinX(String dot, int win_count, int delta_y){
        int[] pos = null;

        for (int y = delta_y; y < field.length; y++){
            int countDot = 0;

            for (int x = 0; x < field[0].length; x++){

                if(field[y][x].equals(dot)){
                    countDot++;
                    pos = new int[]{y, x, 0};
                    //0 - по оси
                } else {
                    countDot = 0;
                    pos = null;
                }

                if(countDot >= win_count) return pos;

                pos = checkDiagonal1Win(x, y, dot, win_count);
                if(pos != null) return pos;
            }
        }

        return pos;
    }

    /**
     * Проверяет выйрушную комбинацию по диагонали с лево на право
     * @param x
     * @param y
     * @param dot
     * @return checkValue
     */
    private int[] checkDiagonal1Win(int x, int y, String dot, int win_count){
        int i = 0;
        try {
            while (field[y + i][x + i].equals(dot)){
                if(i >= win_count - 1){
                    return new int[]{y + i, x + i, 1};
                    //1 - по диагонале
                }
                i++;
            }
        } catch (ArrayIndexOutOfBoundsException e){
            return null;
        }

        return null;
    }

    /**
     * Проверяет выйрушную комбинацию по оси Y и диагонали с право на лево
     * @param dot
     * @return checkValue
     */
    private int[] checkWinY(String dot, int win_count, int delta_x){
        int[] pos = null;

        for (int x = delta_x; x < field[0].length; x++){
            int countDot = 0;

            for (int y = 0; y < field.length; y++){
                if(field[y][x].equals(dot)){
                    countDot++;
                    pos = new int[]{y, x, 0};
                    //0 - по оси
                } else {
                    countDot = 0;
                    pos = null;
                }

                if(countDot >= win_count) return pos;

                pos = checkDiagonal2Win(x, y, dot, win_count);
                if(pos != null ) return pos;
            }
        }

        return pos;
    }

    /**
     * Проверяет выйрушную комбинацию по диагонали с право на лево
     * @param x
     * @param y
     * @param dot
     * @return checkValue
     */
    private int[] checkDiagonal2Win(int x, int y, String dot, int win_count){
        int i = 0;
        try {
            while (field[y + i][x - i].equals(dot)){
                if(i >= win_count - 1){
                    return new int[]{y + i, x - i, 1};
                    //1 - по диагонале
                }
                i++;
            }
        } catch (ArrayIndexOutOfBoundsException e){
            return null;
        }

        return null;
    }
    //WIN/////////////////////////////////////////////////////////////////////////////////////////////////////
}
