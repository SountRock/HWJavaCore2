package ru.geekbrains.core.lesson2;

import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;

public class CrestZero {
    private final char DOT_HUMAN = 'X';
    private final char DOT_AI = '0';
    private final char DOT_EMPTY = '*';
    private final Scanner scanner = new Scanner(System.in);
    private final Random random = new Random();
    public char[][] field;
    private int WIN_COUNT;

    public CrestZero(int fieldSizeX, int fieldSizeY, int winCount) {
        field = new char[fieldSizeY][fieldSizeX];
        WIN_COUNT = winCount;

        for (int y = 0; y < fieldSizeY; y++){
            for (int x = 0; x < fieldSizeX; x++){
                field[y][x] = DOT_EMPTY;
            }
        }
    }

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
        if(winAL){
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
        int x;
        int y;
        do{
            x = random.nextInt(field[0].length);
            y = random.nextInt(field.length);
        }
        while (!isCellEmpty(y, x));
        field[y][x] = DOT_AI;

        return checkWin(DOT_AI);
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
    public boolean checkWin(char dot){
        return checkWinX(dot) | checkWinY(dot);
    }

    /**
     * Проверяет выйрушную комбинацию по оси X и диагонали с лево на право
     * @param dot
     * @return checkValue
     */
    private boolean checkWinX(char dot){
        for (int y = 0; y < field.length; y++){
            int countDotX = 0;

            for (int x = 0; x < field[0].length; x++){
                if(field[y][x] == dot){
                    countDotX++;
                }

               if(checkDiagonal1Win(x, y, dot)) return true;
            }

            if(countDotX >= WIN_COUNT) return true;
        }

        return false;
    }

    /**
     * Проверяет выйрушную комбинацию по диагонали с лево на право
     * @param x
     * @param y
     * @param dot
     * @return checkValue
     */
    private boolean checkDiagonal1Win(int x, int y, char dot){
        int xPos = x;
        int yPos = y;
        boolean countDotDiagonal = true;
        for (int i = 0; (i < WIN_COUNT) && countDotDiagonal; i++) {
            try {
                if(field[yPos + i][xPos + i] != dot){
                    countDotDiagonal = false;
                }
            } catch (ArrayIndexOutOfBoundsException e){
                countDotDiagonal = false;
            }
        }

        return countDotDiagonal;
    }

    /**
     * Проверяет выйрушную комбинацию по оси Y и диагонали с право на лево
     * @param dot
     * @return checkValue
     */
    private boolean checkWinY(char dot){
        for (int x = 0; x < field[0].length; x++){
            int countDot = 0;

            for (int y = 0; y < field.length; y++){
                if(field[y][x] == dot){
                    countDot++;
                }

                if(checkDiagonal2Win(x, y, dot)) return true;
            }

            if(countDot >= WIN_COUNT) return true;;
        }

        return false;
    }

    /**
     * Проверяет выйрушную комбинацию по диагонали с право на лево
     * @param x
     * @param y
     * @param dot
     * @return checkValue
     */
    private boolean checkDiagonal2Win(int x, int y, char dot){
        int xPos = x;
        int yPos = y;
        boolean countDotDiagonal = true;
        for (int i = 0; (i < WIN_COUNT) && countDotDiagonal; i++) {
            try {
                if(field[yPos + i][xPos - i] != dot){
                    countDotDiagonal = false;
                }
            } catch (ArrayIndexOutOfBoundsException e){
                countDotDiagonal = false;
            }
        }

        return countDotDiagonal;
    }

}
