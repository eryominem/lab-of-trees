package binary_tree;

public class BinaryTreeTest {
    public static void main(String[] args) {
        //генерация ключей
        int[] values = new int[20];
        for (int i = 0; i < values.length; i++) {
            values[i] = (int) (Math.random() * 181); //диапазон генерации ключей [0; 180]]
        }

        //построение дерева
        BinaryTree bt = new BinaryTree();
        for (Integer val : values) {
            bt.insert(val);
        }

        //вывод получившегося дерева в консоль
        bt.prettyPrint();

        //поиск ключа по совпадению
        Node foundNode = bt.search(5);
        if (foundNode == null) {
            System.out.println("Узел не найден.");
        } else {
            System.out.println(foundNode.toString());
        }

        //удаление узла из дерева по ключу
        bt.delete(121);


        //обход дерева сверху-вниз
        String traversedTree = bt.traverseTopDown();
        System.out.println(traversedTree);
    }
}

