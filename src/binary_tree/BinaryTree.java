package binary_tree;

public class BinaryTree {
    private Node root;

    public BinaryTree() {
        root = null;
    }

    //Метод для вставки нового узла в дерево
    public void insert(int key) {
        root = insertRecursive(root, key);
    }

    //Вспомогательный рекурсивный метод для вставки нового узла
    private Node insertRecursive(Node root, int key) {
        if (root == null) {
            root = new Node(key);
            return root;
        }

        if (key < root.key) {
            root.left = insertRecursive(root.left, key);
        } else if (key > root.key) {
            root.right = insertRecursive(root.right, key);
        }

        return root;
    }

    //Метод для поиска узла по ключу в дереве
    public Node search(int key) {
        return searchRecursive(root, key);
    }

    //Вспомогательный рекурсивный метод для поиска узла по ключу
    private Node searchRecursive(Node root, int key) {
        if (root == null) {
            return null;
        }

        if (key == root.key) {
            return root;
        } else if (key < root.key) {
            return searchRecursive(root.left, key);
        } else {
            return searchRecursive(root.right, key);
        }
    }

    // Метод для удаления узла по ключу
    public void delete(int key) {
        root = deleteRecursive(root, key);
    }

    //Вспомогательный рекурсивный метод для удаления узла по ключу
    private Node deleteRecursive(Node root, int key) {
        if (root == null) {
            System.out.println("Данное значение отсутствует в дереве");
            return null;
        }

        if (key < root.key) {
            root.left = deleteRecursive(root.left, key);
        } else if (key > root.key) {
            root.right = deleteRecursive(root.right, key);
        } else {
            // Узел с ключом для удаления найден
            // Узел с одним или без детей
            if (root.left == null) {
                return root.right;
            } else if (root.right == null) {
                return root.left;
            }

            // Узел с двумя детьми
            root.key = minValue(root.right);
            root.right = deleteRecursive(root.right, root.key);
        }
        return root;
    }

    // Вспомогательный метод для поиска минимального значения в дереве
    private int minValue(Node node) {
        int minv = node.key;
        while (node.left != null) {
            minv = node.left.key;
            node = node.left;
        }
        return minv;
    }

    // Метод возвращающий строку содержащию ключи узлов дерева, после обхода сверху-вниз
    public String traverseTopDown() {
        StringBuilder sb = new StringBuilder();
        traverseTopDownRec(root, sb);
        return sb.toString();
    }

    // Вспомогательный рекурсивный метод для обхода дерева сверху-вниз
    private void traverseTopDownRec(Node root, StringBuilder sb) {
        if (root != null) {
            // Выводим значение корня
            sb.append(root.key).append(" ");

            // Рекурсивно обходим левое поддерево
            traverseTopDownRec(root.left, sb);

            // Рекурсивно обходим правое поддерево
            traverseTopDownRec(root.right, sb);
        }
    }

    //Метод для вывода дерева в консоль
    public void prettyPrint() {
        prettyPrint(root, "", true);
    }

    private void prettyPrint(Node node, String prefix, boolean isLeft) {
        if (node != null) {
            System.out.println(prefix + (isLeft ? "|-- " : "\\-- ") + node.key);
            prettyPrint(node.left, prefix + (isLeft ? "|   " : "    "), true);
            prettyPrint(node.right, prefix + (isLeft ? "|   " : "    "), false);
        }
    }
}

