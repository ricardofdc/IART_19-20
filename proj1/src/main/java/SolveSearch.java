import java.util.*;

public class SolveSearch {
    Level level;
    private boolean debug;

    SolveSearch(Level level) {
        this.level = level;
        this.debug = true;
    }


    public void debugMode(boolean value){
        this.debug = value;
    }

    

    public NewNode breadthFirstSearch() {
        Queue<NewNode> nodesWaiting = new LinkedList<>(); // queue for FIFO (First In First Out)
        NewNode root = new NewNode(level);
        Level.Direction direction = Level.Direction.NULL;
        nodesWaiting.add(root);

        if (level.isFinish())
            return root;

        while (true) {
            try {
                NewNode dad = nodesWaiting.remove();

                ArrayList<Piece> pieces = dad.getState().getAllPieces();

                for (Piece piece : pieces) { // percorre todas as pecas
                    for (int j = 0; j < 4; j++) { // percorre as 4 direcoes possiveis
                        NewNode node = new NewNode(dad, piece, direction = Level.changeDirection(direction));
                        if (this.debug)
                            Printer.nodeInfo(node);

                        if (node.getState().isFinish()) {
                            level.finish();
                            return node;
                        }
                        nodesWaiting.add(node);
                    }
                }

                if (this.debug)
                    Printer.printNodesQueue(nodesWaiting);
            } catch (NoSuchElementException e) { // a fila chegou ao fim
                return new NewNodeNull(level);
            }
        }
    }

    public NewNode depthFirstSearch() {
        Stack<NewNode> nodesWaiting = new Stack<>(); // stack for LIFO (Last In First Out)
        NewNode root = new NewNode(level);
        Level.Direction direction = Level.Direction.NULL;
        nodesWaiting.push(root);

        if (level.isFinish())
            return root;

        while (true) {
            try {
                NewNode dad = nodesWaiting.pop();

                ArrayList<Piece> pieces = dad.getState().getAllPieces();

                for (Piece piece : pieces) { // percorre todas as pecas
                    for (int j = 0; j < 4; j++) { // percorre as 4 direcoes possiveis
                        NewNode node = new NewNode(dad, piece, direction = Level.changeDirection(direction));
                        if (this.debug)
                            Printer.nodeInfo(node);
                        if (node.getState().isFinish()) {
                            level.finish();
                            return node;
                        }
                        nodesWaiting.push(node);
                    }
                }

                if (this.debug)
                    Printer.printNodesStack(nodesWaiting);
            } catch (EmptyStackException e) { // a fila chegou ao fim
                return new NewNodeNull(level);
            }
        }

    }

    public NewNode iterativeDeepeningSearch() {
        Stack<NewNode> nodesWaiting; // stack for LIFO (Last In First Out)
        NewNode root = new NewNode(level); // o root é sempre o mesmo
        int maxDepthCounter = 1;
        int currDephtCounter;

        while (true) { // profundidade iterativa
            nodesWaiting = new Stack<>();
            Level.Direction direction = Level.Direction.NULL;
            nodesWaiting.push(root);

            if (level.isFinish())
                return root;

            while (true) {
                try {
                    NewNode dad = nodesWaiting.pop();
                    ArrayList<Piece> pieces = dad.getState().getAllPieces();
                    currDephtCounter = dad.getDepth() + 1;

                    for (Piece piece : pieces) { // percorre todas as pecas
                        for (int j = 0; j < 4; j++) { // percorre as 4 direcoes possiveis
                            NewNode node = new NewNode(dad, piece, direction = Level.changeDirection(direction));
                            if (this.debug)
                                Printer.nodeInfo(node);
                            if (node.getState().isFinish()) { // solucao encontrada
                                level.finish();
                                return node;
                            }
                            if (currDephtCounter < maxDepthCounter) // apenas adiciona o nó a stack se nao estiver no
                                                                    // limite de profundidade
                                nodesWaiting.push(node);
                        }
                    }
                    if (this.debug)
                        Printer.printNodesStack(nodesWaiting);

                } catch (EmptyStackException e) { // a stack chegou ao fim
                    break;
                }
            }

            maxDepthCounter++; // aumenta a profundidade da proxima tentativa

            if (maxDepthCounter > level.getNumPieces()) // nao existe solucao
                return new NewNodeNull(level);

        }

    }

    public NewNode greedySearch() {
        PriorityQueue<NewNode> nodesWaiting = new PriorityQueue<>(NewNode.priorityComparator); //used to sort by distance and depth
        NewNode root = new NewNode(level);
        Level.Direction direction = Level.Direction.NULL;
        nodesWaiting.add(root);

        if (level.isFinish())
            return root;

        while (true) {

            NewNode dad = nodesWaiting.poll();

            if(dad == null) //fim da fila de prioridade
                return new NewNodeNull(level);

            ArrayList<Piece> pieces = dad.getState().getAllPieces();

            for (Piece piece : pieces) { // percorre todas as pecas
                for (int j = 0; j < 4; j++) { // percorre as 4 direcoes possiveis
                    NewNode node = new NewNode(dad, piece, direction = Level.changeDirection(direction), 0);
                    if (this.debug)
                        Printer.nodeInfo(node);
                    if (node.getState().isFinish()) {
                        level.finish();
                        return node;
                    }
                    if(node.getState().getAllPieces().size()>0) //se nao tiver mais peças para expandir nao vale a pena adicionar à fila
                        nodesWaiting.add(node);
                }
            }
            if (this.debug)
                Printer.printNodesQueue(nodesWaiting);

        }
    }

    public NewNode AStarSearch(int factor) {
        
        
        PriorityQueue<NewNode> nodesWaiting = new PriorityQueue<>(NewNode.priorityComparator);
        NewNode root = new NewNode(level);
        Level.Direction direction = Level.Direction.NULL;
        nodesWaiting.add(root);

        
        if (level.isFinish())
            return root;

        while (true) {
            NewNode dad = nodesWaiting.poll();

            if(dad == null) //fim da fila de prioridade
                return new NewNodeNull(level);

            ArrayList<Piece> pieces = dad.getState().getAllPieces();

            for (Piece piece : pieces) { // percorre todas as pecas
                for (int j = 0; j < 4; j++) { // percorre as 4 direcoes possiveis
                    NewNode node = new NewNode(dad, piece, direction = Level.changeDirection(direction),factor);
                    if (this.debug)
                        Printer.nodeInfo(node);
                    if (node.getState().isFinish()) {
                        level.finish();
                        return node;
                    }
                    nodesWaiting.add(node);
                }
            }
            if (this.debug)
                Printer.printNodesQueue(nodesWaiting);
        }
    }
}
