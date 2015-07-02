package de.uniwue.algogis.viewshed;

class BinaryNode {

    Comparable element;
    BinaryNode left;
    BinaryNode right;

    BinaryNode(Comparable theElement) {
        this(theElement, null, null);
    }

    BinaryNode(Comparable theElement, BinaryNode lt, BinaryNode rt) {
        element = theElement;
        left = lt;
        right = rt;
    }

}
