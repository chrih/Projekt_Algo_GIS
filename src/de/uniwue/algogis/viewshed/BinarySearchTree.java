package de.uniwue.algogis.viewshed;

public class BinarySearchTree {

    // root of the tree
    private BinaryNode root;

    // default constructor 
    public BinarySearchTree() {
        root = null;
    }

    // insert element  
    public void insertElement(Comparable x) {
        root = insert(x, root);
    }

    // remove element
    public void removeElement(Comparable x) {
        root = remove(x, root);
    }

    // find element
    public Comparable findElement(Comparable x) {
        return getElementAt(find(x, root));
    }
    
    // empty tree
    public void emptyTree() {
        root = null;
    }

    // check if tree is empty
    public boolean isEmpty() {
        return root == null;
    }

    public void printTree() {
        if (isEmpty()) {
            System.out.println("Empty tree");
        } else {
            printTree(root);
        }
    }

    // get element at node t
    private Comparable getElementAt(BinaryNode t) {
        return t == null ? null : t.element;
    }

    // insert node into tree
    private BinaryNode insert(Comparable x, BinaryNode t) {
        // no node existing
        if (t == null) {
            t = new BinaryNode(x, null, null);
            // go node to the left
        } else if (x.compareTo(t.element) < 0) {
            t.left = insert(x, t.left);
            // go node to the right
        } else if (x.compareTo(t.element) > 0) {
            t.right = insert(x, t.right);
        } else {
        }
        return t;
    }

    // remove node from tree
    private BinaryNode remove(Comparable x, BinaryNode t) {
        if (t == null) {
            return t;
        }
        if (x.compareTo(t.element) < 0) {
            t.left = remove(x, t.left);
        } else if (x.compareTo(t.element) > 0) {
            t.right = remove(x, t.right);
        } else if (t.left != null && t.right != null) {
            t.element = findMin(t.right).element;
            t.right = remove(t.element, t.right);
        } else {
            t = (t.left != null) ? t.left : t.right;
        }
        return t;
    }

    // find minimum from node t on
    private BinaryNode findMin(BinaryNode t) {
        if (t == null) {
            return null;
        } else if (t.left == null) {
            return t;
        }
        return findMin(t.left);
    }

    // find maximum from node t
    private BinaryNode findMax(BinaryNode t) {
        if (t != null) {
            while (t.right != null) {
                t = t.right;
            }
        }

        return t;
    }

    // find element 
    private BinaryNode find(Comparable x, BinaryNode t) {
        if (t == null) {
            return null;
        }
        if (x.compareTo(t.element) < 0) {
            return find(x, t.left);
        } else if (x.compareTo(t.element) > 0) {
            return find(x, t.right);
        } else {
            return t;
        }
    }

    // print 
    private void printTree(BinaryNode t) {
        if (t != null) {
            printTree(t.left);
            System.out.println(t.element);
            printTree(t.right);
        }
    }
}
