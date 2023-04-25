package binary_tree;

public class Node {
    int key;
    Node left;
    Node right;

    public Node(int key) {
        this.key = key;
        left = null;
        right = null;
    }

    @Override
    public String toString() {
        return "Node{" +
                "key=" + key +
                ", left=" + left +
                ", right=" + right +
                '}';
    }
}
