package de.uniwue.algogis.viewshed;

class BinaryNode {

    Comparable element;
    BinaryNode left;
    BinaryNode right;

    BinaryNode(Comparable compElement) {
        this(compElement, null, null);
    }

    BinaryNode(Comparable compElement, BinaryNode leftNode, BinaryNode rightNode) {
        this.element = compElement;
        this.left = leftNode;
        this.right = rightNode;
    }

}
