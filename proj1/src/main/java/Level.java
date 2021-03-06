import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Level implements Cloneable {
    private ArrayList<ArrayList<String>> level;
    private String name;
    private int width;
    private int height;
    private ArrayList<Piece> pieces;

    private boolean finish;

    public boolean isFinish() {
        return finish;
    }

    public void finish() {
        this.finish = true;
    }

    enum Direction {
        NULL, UP, DOWN, LEFT, RIGHT
    }

    public static Direction changeDirection(Direction dir) {
        switch (dir) {
            case NULL:
            case RIGHT:
                return Direction.UP;
            case UP:
                return Direction.DOWN;
            case DOWN:
                return Direction.LEFT;
            case LEFT:
                return Direction.RIGHT;
        }
        return Direction.NULL;
    }

    Level() { // contrutor do nivel 0 (vazio)
        level = new ArrayList<>();
        name = "level0";
        width = 0;
        height = 0;
        pieces = new ArrayList<>();
        finish = true;
    }

    Level(String name) {
        this.name = name;
        finish = false;
        level = new ArrayList<>();

        // open file
        try {
            File myObj = new File(name);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                String[] cells = data.split(" ");
                ArrayList<String> line = new ArrayList<>(Arrays.asList(cells));
                level.add(line);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred loading level " + name + ".");
            e.printStackTrace();
            return;
        }

        this.height = level.size();
        this.width = level.get(0).size();

        // get pieces
        pieces = new ArrayList<>();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                String numSteps = level.get(i).get(j);
                if (numSteps.matches("-?\\d+")) {
                    Piece newPiece = new Piece(j + 1, i + 1, Integer.parseInt(numSteps));
                    pieces.add(newPiece);
                }
            }
        }
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        Level l = (Level) super.clone();

        ArrayList<ArrayList<String>> levelClone = new ArrayList<>();

        for (ArrayList<String> strings : this.level) {
            Iterator<String> iterator = strings.iterator();
            ArrayList<String> temp = new ArrayList<>();
            while (iterator.hasNext()) {
                temp.add(iterator.next());
            }
            levelClone.add(temp);
        }

        l.level = levelClone;

        ArrayList<Piece> piecesClone = new ArrayList<>();
        for (Piece piece : this.pieces) {
            piecesClone.add((Piece) piece.clone());
        }

        l.pieces = piecesClone;

        return l;

    }

    public ArrayList<String> getLine(int i) {
        return level.get(i);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public ArrayList<Piece> getAllPieces() {
        return pieces;
    }

    public int getNumPieces() {
        return pieces.size();
    }

    private void setHouse(Position pos, String data) {
        level.get(pos.getY() - 1).set(pos.getX() - 1, data);
    }

    public void selectPiece() {
        while(true) {
            Position pos = Printer.selectPiece();
            for (Piece piece : pieces) {
                if (piece.getPos().equals(pos)) {
                    selectDirection(pos);
                    return;
                }
            }

            System.out.println("Error: Invalid position!");
            System.out.println();
        }
    }

    public void selectDirection(Position pos) {
        switch (Printer.selectDirection()) {
            case 1:
                expandPiece(pos, Direction.UP);
                break;
            case 2:
                expandPiece(pos, Direction.DOWN);
                break;
            case 3:
                expandPiece(pos, Direction.LEFT);
                break;
            case 4:
                expandPiece(pos, Direction.RIGHT);
                break;
            default:
                break;
        }
    }

    public int expandPiece(Position pos, Direction dir) {
        int numberCellsExpanded = 0;
        Piece currPiece;
        ArrayList<Piece> tempPieces = new ArrayList<>(pieces);
        for (Piece piece : tempPieces) {
            if (piece.getPos().equals(pos)) {
                currPiece = piece;

                switch (dir) {
                    case UP:
                        numberCellsExpanded = expandPieceUP(currPiece);
                        break;
                    case DOWN:
                        numberCellsExpanded = expandPieceDOWN(currPiece);
                        break;
                    case LEFT:
                        numberCellsExpanded = expandPieceLEFT(currPiece);
                        break;
                    case RIGHT:
                        numberCellsExpanded = expandPieceRIGHT(currPiece);
                        break;
                    default:
                        break;
                }
                tempPieces.remove(piece);
                break;
            }
        }
        pieces = new ArrayList<>(tempPieces);
        return numberCellsExpanded;

    }

    private int expandPieceUP(Piece piece) {
        // coloca a casa da peca a #
        Position pos = new Position(piece.getPos().getX(), piece.getPos().getY());
        setHouse(pos, "#");
        int numberCellsExpanded = 0;

        // comaca a expansao
        pos = new Position(pos.getX(), pos.getY() - 1);

        for (int i = 0; i < piece.getNumSteps(); i++) {

            if (pos.getY() > 0) {// testa se esta dentro do tabuleiro
                if (level.get(pos.getY() - 1).get(pos.getX() - 1).equals(".")) { // testa se esta vazio
                    setHouse(pos, "#");
                    pos = new Position(pos.getX(), pos.getY() - 1);
                    numberCellsExpanded++;
                } else if (level.get(pos.getY() - 1).get(pos.getX() - 1).equals("W")) { // testa se acabou o jogo
                    setHouse(pos, "#");
                    numberCellsExpanded++;
                    finish = true;
                    return numberCellsExpanded;
                } else { // posicao ocupada
                    i--;
                    pos = new Position(pos.getX(), pos.getY() - 1);
                    numberCellsExpanded++;
                }
            }
        }

        return numberCellsExpanded;
    }

    private int expandPieceDOWN(Piece piece) {
        // coloca a casa da peca a #
        Position pos = new Position(piece.getPos().getX(), piece.getPos().getY());
        setHouse(pos, "#");
        int numberCellsExpanded = 0;

        // comaca a expansao
        pos = new Position(pos.getX(), pos.getY() + 1);

        for (int i = 0; i < piece.getNumSteps(); i++) {

            if (pos.getY() < height) {// testa se esta dentro do tabuleiro
                if (level.get(pos.getY() - 1).get(pos.getX() - 1).equals(".")) { // testa se esta vazio
                    setHouse(pos, "#");
                    pos = new Position(pos.getX(), pos.getY() + 1);
                    numberCellsExpanded++;
                } else if (level.get(pos.getY() - 1).get(pos.getX() - 1).equals("W")) { // testa se acabou o jogo
                    setHouse(pos, "#");
                    numberCellsExpanded++;
                    finish = true;
                    return numberCellsExpanded;
                } else { // posicao ocupada
                    i--;
                    pos = new Position(pos.getX(), pos.getY() + 1);
                    numberCellsExpanded++;
                }
            }
        }
        return numberCellsExpanded;
    }

    private int expandPieceRIGHT(Piece piece) {
        // coloca a casa da peca a #
        Position pos = new Position(piece.getPos().getX(), piece.getPos().getY());
        setHouse(pos, "#");
        int numberCellsExpanded = 0;

        // comaca a expansao
        pos = new Position(pos.getX() + 1, pos.getY());

        for (int i = 0; i < piece.getNumSteps(); i++) {

            if (pos.getX() < width) {// testa se esta dentro do tabuleiro
                if (level.get(pos.getY() - 1).get(pos.getX() - 1).equals(".")) { // testa se esta vazio
                    setHouse(pos, "#");
                    pos = new Position(pos.getX() + 1, pos.getY());
                    numberCellsExpanded++;
                } else if (level.get(pos.getY() - 1).get(pos.getX() - 1).equals("W")) { // testa se acabou o jogo
                    setHouse(pos, "#");
                    numberCellsExpanded++;
                    finish = true;
                    return numberCellsExpanded;
                } else { // posicao ocupada
                    i--;
                    pos = new Position(pos.getX() + 1, pos.getY());
                    numberCellsExpanded++;
                }
            }
        }
        return numberCellsExpanded;
    }

    private int expandPieceLEFT(Piece piece) {
        // coloca a casa da peca a #
        Position pos = new Position(piece.getPos().getX(), piece.getPos().getY());
        setHouse(pos, "#");
        int numberCellsExpanded = 0;

        // comaca a expansao
        pos = new Position(pos.getX() - 1, pos.getY());

        for (int i = 0; i < piece.getNumSteps(); i++) {

            if (pos.getX() > 0) {// testa se esta dentro do tabuleiro
                if (level.get(pos.getY() - 1).get(pos.getX() - 1).equals(".")) { // testa se esta vazio
                    setHouse(pos, "#");
                    numberCellsExpanded++;
                    pos = new Position(pos.getX() - 1, pos.getY());
                } else if (level.get(pos.getY() - 1).get(pos.getX() - 1).equals("W")) { // testa se acabou o jogo
                    setHouse(pos, "#");
                    numberCellsExpanded++;
                    finish = true;
                    return numberCellsExpanded;
                } else { // posicao ocupada
                    i--;
                    pos = new Position(pos.getX() - 1, pos.getY());
                    numberCellsExpanded++;
                }
            }
        }
        return numberCellsExpanded;
    }

    public void reset() {
        this.finish = false;
        level = new ArrayList<>();
        // open file
        try {
            File myObj = new File(name);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                String[] cells = data.split(" ");
                ArrayList<String> line = new ArrayList<>(Arrays.asList(cells));
                level.add(line);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred loading level " + name + ".");
            e.printStackTrace();
            return;
        }

        // get pieces
        pieces = new ArrayList<>();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                String numSteps = level.get(i).get(j);
                if (numSteps.matches("-?\\d+")) {
                    Piece newPiece = new Piece(j + 1, i + 1, Integer.parseInt(numSteps));
                    pieces.add(newPiece);
                }
            }
        }
    }
}
