package utils;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

/*
 * @author DEI-ESINF
 * @param <E>
 */

public class TREE<E extends Comparable<E>> extends BST<E>{
 
    
     /*
     * @param element A valid element within the tree
     * @return the path to a given element in the tree
     */
    public List<E> path(E elem) {
        List<E> path = new ArrayList<>();
        Node<E> current = root();
        while (current != null) {
            path.add(current.getElement());
            int cmp = elem.compareTo(current.getElement());
            if (cmp == 0) break;
            current = (cmp < 0) ? current.getLeft() : current.getRight();
        }
        return path;
    }

     /*
     * @return the set of the leaf node elements of the tree
     */
    public Set<E> leafs(){
        Set<E> leaves = new TreeSet<>();
        collectLeaves(root(), leaves);
        return leaves;
    }

    private void collectLeaves(Node<E> node, Set<E> leaves) {
        if (node == null) return;
        if (node.getLeft() == null && node.getRight() == null)
            leaves.add(node.getElement());
        else {
            collectLeaves(node.getLeft(), leaves);
            collectLeaves(node.getRight(), leaves);
        }
    }
    
    /*
    * @return an array with the minimum and the maximum values of the tree
    */
    public E[] range() {
        if (isEmpty()) return null;
        E min = smallestElement();
        E max = largestElement(root());
        E[] arr = (E[]) new Comparable[]{min, max};
        return arr;
    }

    private E largestElement(Node<E> node) {
        if (node == null) return null;
        if (node.getRight() == null) return node.getElement();
        return largestElement(node.getRight());
    }
    
    /*
    *  @return the set of elements belonging to the diameter of the BST
    */
    public Set<E> diameter(){
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    /*
    *  @return the previous element of the tree for a given element
    */
    public E findPredecessor (E element){
         Node<E> current = root();
        E predecessor = null;
        while (current != null) {
            int cmp = element.compareTo(current.getElement());
            if (cmp > 0) {
                predecessor = current.getElement();
                current = current.getRight();
            } else {
                current = current.getLeft();
            }
        }
        return predecessor;
    } 
    
    /*
    * – verify if the current and tree BST are identical.
    */
    public boolean identical(BST<E> tree){
        throw new UnsupportedOperationException("Not supported yet.");
    } 
        
    /*
    * – remove all elements in the current BST that are outside the range [low, high]
    */
    public void truncate(E low, E high){
        root = truncate(root(), low, high);
    }  

    private Node<E> truncate(Node<E> node, E low, E high) {
        if (node == null) return null;

        if (node.getElement().compareTo(low) < 0)
            return truncate(node.getRight(), low, high);
        if (node.getElement().compareTo(high) > 0)
            return truncate(node.getLeft(), low, high);

        node.setLeft(truncate(node.getLeft(), low, high));
        node.setRight(truncate(node.getRight(), low, high));
        return node;
    }
            
    /*
     *– return true if BST<E> tree is a sub tree of the BST<E>. 
     */        
    public boolean isSubTree(BST<E> tree){
        throw new UnsupportedOperationException("Not supported yet.");
    } 
    
    
    public boolean isSymmetric(){
        throw new UnsupportedOperationException("Not supported yet.");
    } 
    
    
    public TREE<E> minimumSubtree (Set<E> elems ) {
        throw new UnsupportedOperationException("Not supported yet.");
    } 
    
}
