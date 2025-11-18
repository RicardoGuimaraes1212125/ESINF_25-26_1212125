package utils;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author DEI-ESINF
 * @param <E>
 */
public class AVL<E extends Comparable<E>> extends BST<E> {

    private int balanceFactor(Node<E> node) {
        if (node == null) return 0;
        return height(node.getLeft()) - height(node.getRight());
    }

    private Node<E> rightRotation(Node<E> node) {
        Node<E> x = node.getLeft();
        Node<E> temp = x.getRight();

        x.setRight(node);
        node.setLeft(temp);

        return x;
    }

    private Node<E> leftRotation(Node<E> node) {
        Node<E> y = node.getRight();
        Node<E> temp = y.getLeft();

        y.setLeft(node);
        node.setRight(temp);

        return y;
    }

    private Node<E> twoRotations(Node<E> node) {
        if (balanceFactor(node) > 0) {               // left-heavy → Left-Right
            node.setLeft(leftRotation(node.getLeft()));
            return rightRotation(node);
        } else {                                     // right-heavy → Right-Left
            node.setRight(rightRotation(node.getRight()));
            return leftRotation(node);
        }
    }

    private Node<E> balanceNode(Node<E> node) {
        int balance = balanceFactor(node);

        // Left-heavy
        if (balance > 1) {
            if (balanceFactor(node.getLeft()) >= 0) {
                node = rightRotation(node); // single rotation
            } else {
                node = twoRotations(node);
            }
        }
        // Right-heavy
        else if (balance < -1) {
            if (balanceFactor(node.getRight()) <= 0) {
                node = leftRotation(node); // single rotation
            } else {
                node = twoRotations(node);
            }
        }

        return node;
    }

    @Override
    public void insert(E element) {
        if (element == null)
            throw new IllegalArgumentException("Element cannot be null");
        root = insert(element, root);
    }

    private Node<E> insert(E element, Node<E> node) {
        if (node == null)
            return new Node<>(element, null, null);

        int cmp = element.compareTo(node.getElement());

        if (cmp < 0)
            node.setLeft(insert(element, node.getLeft()));
        else if (cmp > 0)
            node.setRight(insert(element, node.getRight()));
        else {
            // elemento já existe → opcionalmente atualiza o valor
            node.setElement(element);
            return node;
        }   // ← ESTA CHAVETA FALTAVA!!

        // balanceamento após inserção
        return balanceNode(node);   // ← ESTE RETURN ESTAVA FORA DO BLOCO
    }

    @Override
    public void remove(E element) {
        root = remove(element, root());
    }

    private Node<E> remove(E element, Node<E> node) {
        if (node == null) {
            return null;
        }
        if (element.compareTo(node.getElement()) == 0) { // node is the Node to be removed

            if (node.getLeft() == null && node.getRight() == null) { // node is a leaf
                return null;
            }
            if (node.getLeft() == null) {   // only right child
                return node.getRight();
            }
            if (node.getRight() == null) {  // only left child
                return node.getLeft();
            }

            // has two child trees
            E smallElem = smallestElement(node.getRight());
            node.setElement(smallElem);
            node.setRight(remove(smallElem, node.getRight()));
            node = balanceNode(node);
        }
        else if (element.compareTo(node.getElement()) < 0) {
            node.setLeft(remove(element, node.getLeft()));
            node = balanceNode(node);
        }
        else {
            node.setRight(remove(element, node.getRight()));
            node = balanceNode(node);
        }
        return node;
    }

    public boolean equals(Object otherObj) {

        if (this == otherObj)
            return true;

        if (otherObj == null || this.getClass() != otherObj.getClass())
            return false;

        AVL<E> second = (AVL<E>) otherObj;
        return equals(root, second.root);
    }

    public boolean equals(Node<E> root1, Node<E> root2) {
        if (root1 == null && root2 == null)
            return true;
        else if (root1 != null && root2 != null) {
            if (root1.getElement().compareTo(root2.getElement()) == 0) {
                return equals(root1.getLeft(), root2.getLeft())
                        && equals(root1.getRight(), root2.getRight());
            } else
                return false;
        }
        else return false;
    }

}