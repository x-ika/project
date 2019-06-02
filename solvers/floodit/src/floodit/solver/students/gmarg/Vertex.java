package floodit.solver.students.gmarg;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class Vertex {
	private int id, count, color, depth, heurist, distance;
	private boolean visited;
	private HashSet<Vertex> neighbours;
	private String path;
	private String idPath;
	
	public Vertex(){
		
	}
	
	public Vertex(int id, int color){
		this.setId(id);
		this.setColor(color);
		this.setCount(1);
		this.setVisited(false);
		this.setDepth(-1);
		this.setNeighbours(new HashSet<>());
		this.setHeurist(0);
		this.setDistance(0);
		this.setPath("");
		this.setIdPath("");
	}

	public void setPath(String string) {
		this.path = string;
		
	}

	public void addPath(String string){
		this.path = this.path + string;
	}
	
	public String getPath(){
		return this.path;
	}
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
	public void incCount(){
		this.count++;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}
	
	public void incDepth(){
		this.depth++;
	}
	
	public void decDepth(){
		this.depth--;
	}

	public int getHeurist() {
		return heurist;
	}

	public void setHeurist(int heurist) {
		this.heurist = heurist;
	}

	public void incHeurist(){
		this.heurist++;
	}
	
	public boolean isVisited() {
		return visited;
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}

	public void changeVisited(){
		this.visited = !this.visited;
	}
	
	public HashSet<Vertex> getNeighbours() {
		return neighbours;
	}

	public void removeNeigh(Vertex vrt){
		this.neighbours.remove(vrt);
	}
	
	public void setNeighbours(HashSet<Vertex> neighbours) {
		this.neighbours = neighbours;
	}
	
	public void addNeighbour(Vertex vrt){
		this.neighbours.add(vrt);
	}
	
	public void addNeighbours(HashSet<Vertex> neighs){
		this.neighbours.addAll(neighs);
	}
	

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}
	 

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vertex other = (Vertex) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}
	
	public void incDistance(){
		this.distance++;
	}
	
public Vertex getCopy(Vertex copyFrom, Vertex copyTo, HashMap<Vertex,Vertex> created){
		
		copyTo.setId(copyFrom.getId());
		copyTo.setColor(copyFrom.getColor());
		copyTo.setCount(copyFrom.getCount());
		copyTo.setDepth(copyFrom.getDepth());
		copyTo.setDistance(copyFrom.getDistance());
		copyTo.setHeurist(copyFrom.getHeurist());
		copyTo.setVisited(copyFrom.isVisited());
		copyTo.setPath(copyFrom.getPath());
		copyTo.setIdPath(copyFrom.getIdPath());
		
		created.put(copyFrom, copyTo);
	
		HashSet<Vertex> copyToNeighs = new HashSet<>();
		HashSet<Vertex> copyFromNeighs = new HashSet<>();
		copyFromNeighs.addAll(copyFrom.getNeighbours());
		
		Iterator<Vertex> it = copyFromNeighs.iterator();
		
		
		while (it.hasNext()){
			Vertex curr = it.next();
			
			
			Vertex tmp = created.get(curr);
			
			if (tmp == null){
				tmp = new Vertex();
				tmp = getCopy(curr,tmp,created);
			}
			
			
			copyToNeighs.add(tmp);
		}
		
		copyTo.setNeighbours(copyToNeighs);
	
		return copyTo;
	}

	public void removeNeighs(HashSet<Vertex> sameColor) {
		this.neighbours.removeAll(sameColor);
		
	}

	public String getIdPath() {
		return idPath;
	}

	public void setIdPath(String idPath) {
		this.idPath = idPath;
	}
	
	public void addIdPath(String path){
		this.idPath = this.idPath + path;
	}
}
