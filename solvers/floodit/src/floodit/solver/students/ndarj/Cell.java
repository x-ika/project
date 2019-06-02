package floodit.solver.students.ndarj;

import java.util.ArrayList;

public class Cell {
    
    private int row, col, initialColor;
    private static ArrayList<ArrayList<Cell>> allCells = new ArrayList<>();
    public Cell(int row, int col, int color) {
	this.row = row;
	this.col = col;
	this.initialColor = color;

	save();
    }

    private void save() {
	while (allCells.size() <= this.row) 
		allCells.add(new ArrayList<>());
	ArrayList<Cell> list = allCells.get(this.row);
	while (list.size() <= this.col)
		list.add(null);
	list.set(this.col, this);
    }

    public static Cell getCell(int row, int col) {
	if (row < 0 || col < 0) 
	    return null;
	if (row >= allCells.size()) 
	    return null;
	if (col >= allCells.get(row).size())
	    return null;
	return allCells.get(row).get(col);
    }

    public int getInitialColor() {
	return this.initialColor;
    }

    public int getRow() {
	return this.row;
    }

    public int getCol() {
	return this.col;
    }

    private Node parentNode = null;
    public void setParentNode(Node node) {
	if(parentNode != null) {
	    System.out.println("setting parentNode twice");
	}
	parentNode = node;
    }
    
    public Node getParentNode() {
	return parentNode;
    }
    

    public boolean equals(Object obj) {
	if (this == obj) {
	    return true;
	}
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass())
	    return false;
	Cell other = (Cell) obj;
	if (col != other.col)
	    return false;
	if (row != other.row)
	    return false;
	return true;
    }


    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + col;
	result = prime * result + row;
	return result;
    }
    
}
