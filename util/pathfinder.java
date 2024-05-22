package util;

/**
 * The pathfinder class represents a node in a pathfinding algorithm.
 * It stores information about the node's position, parent node, and various costs.
 */
public class pathfinder {

   private pathfinder parent;
   private int r, c;
   private double f, costFromStart, distanceToEnd;

   /**
    * Constructs a pathfinder object with the given row and column.
    *
    * @param row The row position of the node.
    * @param col The column position of the node.
    */
   public pathfinder(int row, int col) {
      r = row;
      c = col;
      costFromStart = 0;
      distanceToEnd = 0;
      f = 0;
   }

   /**
    * Constructs a pathfinder object with the given parent node, row, and column.
    *
    * @param p   The parent node.
    * @param row The row position of the node.
    * @param col The column position of the node.
    */
   public pathfinder(pathfinder p, int row, int col) {
      r = row;
      c = col;
      parent = p;
      costFromStart = parent.getCostFromStart() + distanceTo(parent.getR(), parent.getC());
   }

   /**
    * Calculates the distance from this node to the specified row and column.
    *
    * @param row The row position of the target node.
    * @param col The column position of the target node.
    * @return The distance between this node and the target node.
    */
   private double distanceTo(int row, int col) {
      return Math.sqrt(Math.pow(row - r, 2) + Math.pow(col - c, 2));
   }

   /**
    * Retrieves the cost from the start node to this node.
    *
    * @return The cost from the start node to this node.
    */
   private double getCostFromStart() {
      return costFromStart;
   }

   /**
    * Retrieves the row position of this node.
    *
    * @return The row position of this node.
    */
   public int getR() {
      return r;
   }

   /**
    * Retrieves the column position of this node.
    *
    * @return The column position of this node.
    */
   public int getC() {
      return c;
   }

   /**
    * Sets the distance to the end node based on the specified target row and column.
    *
    * @param targetR The row position of the target node.
    * @param targetC The column position of the target node.
    */
   public void setDistanceToEnd(int targetR, int targetC) {
      distanceToEnd = targetR - r + targetC - c;
   }

   /**
    * Calculates and sets the total cost (f) of reaching this node.
    */
   public void findF() {
      f = costFromStart + distanceToEnd;
   }

   /**
    * Retrieves the total cost (f) of reaching this node.
    *
    * @return The total cost of reaching this node.
    */
   public double getF() {
      return f;
   }

   /**
    * Retrieves the parent node of this node.
    *
    * @return The parent node of this node.
    */
   public pathfinder getParent() {
      return parent;
   }

   /**
    * Checks if this node has a parent node.
    *
    * @return True if this node has a parent node, otherwise false.
    */
   public boolean hasParent() {
      return parent != null;
   }

   /**
    * Returns a string representation of this node.
    *
    * @return A string representation of this node.
    */
   @Override
   public String toString() {
      return "(" + r + "," + c + ")";
   }
}
