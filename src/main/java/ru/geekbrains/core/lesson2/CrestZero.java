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
    private int AL_IGNORE;

    public CrestZero(int fieldSize, int winCount) {
        field = new String[fieldSize][fieldSize];
        WIN_COUNT = winCount < fieldSize ? winCount : fieldSize;
        AL_IGNORE = fieldSize - WIN_COUNT;

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
        boolean canBlock = smartTurn(0,0);
        System.out.println(canBlock);
        if(!canBlock){
            int x;
            int y;
            do{
                x = random.nextInt(field.length);
                y = random.nextInt(field.length);
            } while (!isCellEmpty(y, x));
            field[y][x] = DOT_AI;
        }

        return checkWin(DOT_AI);
    }

    /**
     * Метод умного хода AL
     * @param delta_y
     * @param delta_x
     * @return isTurn
     */
    private boolean smartTurn(int delta_y, int delta_x){
        int[] posX = searchPreWinXOpponents(DOT_HUMAN, DOT_AI, delta_y);
        if(posX != null){
            field[posX[0]][posX[1]] = DOT_AI;
            return true;
        }
        int[] posY = searchPreWinYOpponents(DOT_HUMAN, DOT_AI, delta_x);
        if(posY != null){
            field[posY[0]][posY[1]] = DOT_AI;
            return true;
        }
        int[] posDia1 = searchPreWinDiagonal1Opponents(DOT_HUMAN, DOT_AI, delta_x);
        if(posDia1 != null){
            field[posDia1[0]][posDia1[1]] = DOT_AI;
            return true;
        }
        int[] posDia2 = searchPreWinDiagonal2Opponents(DOT_HUMAN, DOT_AI, delta_x);
        if(posDia2 != null){
            field[posDia2[0]][posDia2[1]] = DOT_AI;
            return true;
        }

        if(delta_y < field.length){
            smartTurn(++delta_y, delta_x);
        }
        if(delta_x < field.length){
            smartTurn(delta_y, ++delta_x);
        }

        return false;
    }

    /**
     * Поиск возможной победы по X
     * @param dotOpponent
     * @param youDot
     * @param delta_y
     * @return position
     */
    private int[] searchPreWinXOpponents(String dotOpponent, String youDot, int delta_y){
        for (int y = delta_y; y < field.length; y++) {
            int countDotOp = 0;
            int countDotYou = 0;
            int[] pos = null;

            for (int x = 0; x < field.length; x++) {
                if(field[y][x].equals(dotOpponent)){
                    countDotOp++;

                } else if (field[y][x].equals(youDot)){
                    countDotYou++;

                } else if (isCellEmpty(y, x)){
                    pos = new int[]{y, x};

                }
                if((countDotOp >= WIN_COUNT - 1) && (countDotYou < AL_IGNORE)) return pos;
            }
        }

        return null;
    }

    /**
     * Поиск победы по диагонале с лева на право
     * @param dotOpponent
     * @param youDot
     * @param delta_x
     * @return position
     */
    private int[] searchPreWinDiagonal1Opponents(String dotOpponent, String youDot, int delta_x){
        for (int y = 0; y < field.length; y++) {
            for (int x = delta_x; x < field.length; x++) {
                int countDotOp = 0;
                int countDotYou = 0;
                int[] pos = null;

                for (int index = 0; (y + index < field.length) & (x + index < field.length); index++) {
                    if(field[y + index][x + index].equals(dotOpponent)){
                        countDotOp++;

                    } else if(field[y + index][x + index].equals(youDot)){
                        countDotYou++;

                    } else if (isCellEmpty(y + index, x + index)){
                        pos = new int[]{y + index, x + index};

                    }
                    if((countDotOp >= WIN_COUNT - 1) && (countDotYou < AL_IGNORE)) return pos;
                }
            }
        }

        return null;
    }

    /**
     * Поиск возможной победы по Y
     * @param dotOpponent
     * @param youDot
     * @param delta_x
     * @return position
     */
    private int[] searchPreWinYOpponents(String dotOpponent, String youDot, int delta_x){
        for (int x = delta_x; x < field.length; x++) {
            int countDotOp = 0;
            int countDotYou = 0;
            int[] pos = null;

            for (int y = 0; y < field.length; y++) {
                if(field[y][x].equals(dotOpponent)){
                    countDotOp++;
                    //int[] temp = searchPreWinDiagonal2Opponents(y, x, dotOpponent, youDot);
                    //if(temp != null) return temp;
                } else if (field[y][x].equals(youDot)){
                    countDotYou++;
                } else if (isCellEmpty(y, x)){
                    pos = new int[]{y, x};
                }
                if((countDotOp >= WIN_COUNT - 1) && (countDotYou < AL_IGNORE)) return pos;
            }

        }

        return null;
    }

    /**
     * Поиск победы по диагонале с права на лево
     * @param dotOpponent
     * @param youDot
     * @param delta_x
     * @return position
     */
    private int[] searchPreWinDiagonal2Opponents(String dotOpponent, String youDot, int delta_x){
        for (int y = 0; y < field.length; y++) {
            for (int x = delta_x; x < field.length; x++) {
                int countDotOp = 0;
                int countDotYou = 0;
                int[] pos = null;

                for (int index = 0; (y + index < field.length) & (x - index >= 0); index++) {
                    if(field[y + index][x - index].equals(dotOpponent)){
                        countDotOp++;
                    } else if(field[y + index][x - index].equals(youDot)){
                        countDotYou++;
                    } else if (isCellEmpty(y + index, x - index)){
                        pos = new int[]{y + index, x - index};
                    }
                    if((countDotOp >= WIN_COUNT - 1) && (countDotYou < AL_IGNORE)) return pos;
                }
            }
        }

        return null;
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
        return checkWinX(dot, WIN_COUNT, 0) | checkWinY(dot, WIN_COUNT, 0);
    }

    /**
     * Проверяет выйрушную комбинацию по оси X и диагонали с лево на право
     * @param dot
     * @return checkValue
     */
    private boolean checkWinX(String dot, int win_count, int delta_y){
        for (int y = delta_y; y < field.length; y++){
            int countDot = 0;

            for (int x = 0; x < field[0].length; x++){

                if(field[y][x].equals(dot)){
                    countDot++;
                } else {
                    countDot = 0;
                }

                if(countDot >= win_count) return true;

                if(checkDiagonal1Win(x, y, dot, win_count)) return true;
            }
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
    private boolean checkDiagonal1Win(int x, int y, String dot, int win_count){
        int i = 0;
        try {
            while (field[y + i][x + i].equals(dot)){
                if(i >= win_count - 1){
                    return true;
                }
                i++;
            }
        } catch (ArrayIndexOutOfBoundsException e){
            return false;
        }

        return false;
    }

    /**
     * Проверяет выйрушную комбинацию по оси Y и диагонали с право на лево
     * @param dot
     * @return checkValue
     */
    private boolean checkWinY(String dot, int win_count, int delta_x){
        for (int x = delta_x; x < field[0].length; x++){
            int countDot = 0;

            for (int y = 0; y < field.length; y++){
                if(field[y][x].equals(dot)){
                    countDot++;
                } else {
                    countDot = 0;
                }

                if(countDot >= win_count) return true;

                if(checkDiagonal2Win(x, y, dot, win_count)) return true;
            }
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
    private boolean checkDiagonal2Win(int x, int y, String dot, int win_count){
        int i = 0;
        try {
            while (field[y + i][x - i].equals(dot)){
                if(i >= win_count - 1){
                    return true;
                    //1 - по диагонале
                }
                i++;
            }
        } catch (ArrayIndexOutOfBoundsException e){
            return false;
        }

        return false;
    }
    //WIN/////////////////////////////////////////////////////////////////////////////////////////////////////
}
