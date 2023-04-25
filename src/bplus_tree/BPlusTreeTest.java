package bplus_tree;

public class BPlusTreeTest {
    public static void main(String[] args) {
        BPlusTree bPlusTree = new BPlusTree();

        for (int i = 0; i < 20; i++) {
            int key = (int) (Math.random() * 181);
            bPlusTree.insert(key);
        }


        System.out.println("B+ tree:");
        bPlusTree.prettyPrint(bPlusTree.getRoot(), "", true);

        System.out.println("\nОбход B+ дерева сверху-вниз");
        bPlusTree.traverse();
    }
}
