package bplus_tree;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class BPlusTree {
    private BPlusNode root;
    private static final int M = 5; // Степень (порядок) дерева

    static class BPlusNode {
        int[] keys = new int[M]; // Массив ключей
        BPlusNode[] children = new BPlusNode[M + 1]; // Массив содержущий ссылки на потомков
        int numKeys; // Кол-во ключей в узле
        boolean isLeaf;

        BPlusNode() {

        }

        BPlusNode(int key) {
            keys[0] = key;
            numKeys = 1;
            isLeaf = true;
        }

        BPlusNode(int key, BPlusNode leftChild, BPlusNode rightChild) {
            keys[0] = key;
            children[0] = leftChild;
            children[1] = rightChild;
            numKeys = 1;
            isLeaf = false;
        }
    }

    // Метод для вставки нового ключа в дерево
    public void insert(int key) {
        // Если корень равен null, создаем новый узел и делаем его корнем
        if (root == null) {
            root = new BPlusNode(key);
        } else {
            BPlusNode node = insert(root, key);
            if (node != null) {
                root = new BPlusNode(node.keys[0], root, node);
            }
        }
    }

    // реализует операцию вставки нового ключа в B+ дерево
    private BPlusNode insert(BPlusNode node, int key) {
        int i = 0;
        while (i < node.numKeys && node.keys[i] < key) {
            i++;
        }

        // Проверка на то, является ли нода листом или нет.
        if (node.isLeaf) {
            // Если лист - то производится вставка нового ключа в узел методом insertIntoLeaf().
            insertIntoLeaf(node, key, i);
        } else {
            // Если node является нелистовым узлом, то рекурсивно вызывается функция insert() для правильного потомка,
            // в который необходимо вставить новый ключ key.
            BPlusNode child = insert(node.children[i], key);
            if (child != null) {
                insertIntoNode(node, child.keys[0], child, i);
            }
        }
        if (node.numKeys == M) {
            return split(node);
        } else {
            return null;
        }
    }

    // Метод вставки нового ключа в листовой узел дерева
    private void insertIntoLeaf(BPlusNode leaf, int key, int index) {
        for (int i = leaf.numKeys - 1; i >= index; i--) {
            leaf.keys[i + 1] = leaf.keys[i];
        }
        leaf.keys[index] = key;
        leaf.numKeys++;
    }

    // Вставку нового ключа и соответствующего ему дочернего узла в узел B+ дерева.
    private void insertIntoNode(BPlusNode node, int key, BPlusNode child, int index) {
        for (int i = node.numKeys; i > index; i--) {
            node.keys[i] = node.keys[i - 1];
            node.children[i + 1] = node.children[i];
        }
        node.keys[index] = key;
        node.children[index + 1] = child;
        node.numKeys++;
    }

    // Разделение узла на два узла, чтобы поддерживать инвариант B-дерева, что размер каждого узла не превышает M
    private BPlusNode split(BPlusNode node) {
        // Создаем новый узел для правой половины ключей и дочерних узлов
        BPlusNode newRight = new BPlusNode();
        newRight.isLeaf = node.isLeaf;
        newRight.numKeys = M / 2;
        // Копируем правую половину ключей и дочерних узлов из текущего узла в новый
        for (int i = 0; i < newRight.numKeys; i++) {
            newRight.keys[i] = node.keys[M / 2 + i];
            newRight.children[i + 1] = node.children[M / 2 + i + 1];
        }
        // Обнуляем ключи и дочерние узлы в правой половине текущего узла
        for (int i = M / 2; i < M; i++) {
            node.keys[i] = 0;
            node.children[i + 1] = null;
        }
        node.numKeys = M / 2;
        // Если текущий узел является корнем, создаем новый корень и добавляем в него двух потомков
        if (node == root) {
            root = new BPlusNode();
            root.keys[0] = newRight.keys[0];
            root.children[0] = node;
            root.children[1] = newRight;
            root.numKeys = 1;
            return null;
        }

        return newRight;
    }

    // Удаления ключа из B-дерева
    public void delete(int key) {
        if (root == null) {
            return;
        }
        delete(root, key);
        if (root.numKeys == 0) {
            root = root.children[0];
        }
    }

    // Вспомогательный метод удаления ключа из B-дерева
    private void delete(BPlusNode node, int key) {
        int i = 0;
        while (i < node.numKeys && node.keys[i] < key) {
            i++;
        }
        if (node.isLeaf) {
            if (i < node.numKeys && node.keys[i] == key) {
                removeFromLeaf(node, i);
            }
        } else {
            if (i < node.numKeys && node.keys[i] == key) {
                removeFromInternal(node, i);
            } else {
                delete(node.children[i], key);
            }
        }
        // Если размер дочернего узла node.children[i] после удаления ключа key меньше половины M/2
        // (где M - максимальный размер узла), то вызываем метод fill, который пытается заполнить узел node.children[i] ключами из его соседних узлов.
        if (node.children[i] != null && node.children[i].numKeys < M / 2) {
            fill(node, i);
        }
    }

    // Удаление ключа из листового узла B-дерева
    private void removeFromLeaf(BPlusNode node, int index) {
        for (int i = index + 1; i < node.numKeys; i++) {
            node.keys[i - 1] = node.keys[i];
        }
        node.numKeys--;
    }

    // Удаление ключа из внутреннего узла дерева B+ дерева
    private void removeFromInternal(BPlusNode node, int index) {
        int key = node.keys[index];
        BPlusNode leftChild = node.children[index];
        BPlusNode rightChild = node.children[index + 1];
        if (leftChild.numKeys >= M / 2) {
            int predecessor = getPredecessor(leftChild);
            node.keys[index] = predecessor;
            delete(leftChild, predecessor);
        } else if (rightChild.numKeys >= M / 2) {
            int successor = getSuccessor(rightChild);
            node.keys[index] = successor;
            delete(rightChild, successor);
        } else {
            merge(node, index, key, leftChild, rightChild);
            delete(leftChild, key);
        }
    }

    // Метод для обхода B+ дерева сверху-вниз
    public void traverse() {
        traverse(root, 0);
    }

    // Рекурсивный метод для обхода B+ дерева сверху-вниз, начиная с указанного узла и уровня
    private void traverse(BPlusNode node, int level) {
        if (node != null) {
            // Выводим ключи и значения текущего узла с указанием уровня
            System.out.println("Level: " + level);
            for (int i = 0; i < node.keys.length; i++) {
                if (node.keys[i] != 0) {
                    System.out.println("Key: " + node.keys[i]);
                }
            }

            if (!node.isLeaf) {
                for (BPlusNode child : node.children) {
                    traverse(child, level + 1);
                }
            }
        }
    }


    // Возвращает ключ-предшественника в переданном узле дерева B+ дерева
    private int getPredecessor(BPlusNode node) {
        while (!node.isLeaf) {
            node = node.children[node.numKeys];
        }
        return node.keys[node.numKeys - 1];
    }

    // Возвращает ключ преемника для заданного узла в дереве B+
    private int getSuccessor(BPlusNode node) {
        while (!node.isLeaf) {
            node = node.children[0];
        }
        return node.keys[0];
    }

    // Заполняет Node, если его размер меньше M/2 (где M - максимальный размер узла)
    private void fill(BPlusNode node, int index) {
        BPlusNode leftSibling = index > 0 ? node.children[index - 1] : null;
        BPlusNode rightSibling = index < node.numKeys ? node.children[index + 1] : null;
        if (leftSibling != null && leftSibling.numKeys > M / 2) {
            // заполнение соседним ключом и/или дочерним узлом слева
            for (int i = node.numKeys; i > 0; i--) {
                node.keys[i] = node.keys[i - 1];
            }
            node.keys[0] = leftSibling.keys[leftSibling.numKeys - 1];
            if (!leftSibling.isLeaf) {
                for (int i = node.numKeys + 1; i > 0; i--) {
                    node.children[i] = node.children[i - 1];
                }
                node.children[0] = leftSibling.children[leftSibling.numKeys];
            }
            leftSibling.numKeys--;
            node.numKeys++;
        } else if (rightSibling != null && rightSibling.numKeys > M / 2) {
            // заполнение соседним ключом и/или дочерним узлом справа
            node.keys[node.numKeys] = rightSibling.keys[0];
            if (!rightSibling.isLeaf) {
                node.children[node.numKeys + 1] = rightSibling.children[0];
            }
            for (int i = 1; i < rightSibling.numKeys; i++) {
                rightSibling.keys[i - 1] = rightSibling.keys[i];
            }
            if (!rightSibling.isLeaf) {
                for (int i = 1; i <= rightSibling.numKeys; i++) {
                    rightSibling.children[i - 1] = rightSibling.children[i];
                }
            }
            rightSibling.numKeys--;
            node.numKeys++;
        } else {
            // объединение текущего узла с соседним
            if (leftSibling != null) {
                merge(node, index - 1, node.keys[index - 1], leftSibling, node.children[index]);
                index--;
            } else {
                merge(node, index, node.keys[index], node.children[index], rightSibling);
            }
        }
    }

    // Поиск по совпадению ключа в B+ дереве
    public int search(int key) {
        BPlusNode curr = root;
        while (!curr.isLeaf) {
            int i = 0;
            while (i < curr.numKeys && key >= curr.keys[i]) {
                i++;
            }
            curr = curr.children[i];
        }
        int i = 0;

        while (i < curr.numKeys && key > curr.keys[i]) {
            i++;
        }
        if (i < curr.numKeys && key == curr.keys[i]) {
            return curr.keys[curr.numKeys - 1];
        }
        return -1; // ненаход
    }

    // Слияние двух узлов B+ дерева
    // При слиянии двух узлов, все ключи и дочерние узлы из правого узла добавляются в левый узел
    private void merge(BPlusNode node, int index, int key, BPlusNode leftChild, BPlusNode rightChild) {
        leftChild.keys[leftChild.numKeys++] = key;
        for (int i = 0, j = leftChild.numKeys; i < rightChild.numKeys; i++, j++) {
            leftChild.keys[j] = rightChild.keys[i];
            leftChild.children[j] = rightChild.children[i];
            leftChild.numKeys++;
        }
        leftChild.children[leftChild.numKeys] = rightChild.children[rightChild.numKeys];
        for (int i = index + 1; i < node.numKeys; i++) {
            node.keys[i - 1] = node.keys[i];
            node.children[i] = node.children[i + 1];
        }
        node.numKeys--;
    }

    public void prettyPrint(BPlusNode node, String prefix, boolean isLeft) {
        if (node != null) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < node.numKeys; i++) {
                sb.append(node.keys[i] + " ");
            }
            System.out.println(prefix + (isLeft ? "|-- " : "\\-- ") + sb.toString().trim());
            if (!node.isLeaf) {
                for (int i = 0; i < node.numKeys + 1; i++) {
                    prettyPrint(node.children[i], prefix + (isLeft ? "|   " : "    "), i == 0);
                }
            }
        }
    }

    public BPlusNode getRoot() {
        return root;
    }
}
