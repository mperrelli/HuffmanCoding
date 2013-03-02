//////////////////////////////////////////////
//  Matthew Perrelli        Huffman Coding  //
//											//
//  Compresses a .txt file          		//
//											//
//////////////////////////////////////////////
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.PriorityQueue;
import java.util.Scanner;

public class main {

	static ArrayList<node> nodes = new ArrayList<node>();
	static Hashtable<Character, Integer> frequencies = new Hashtable<Character, Integer>();
	static Hashtable<Character, String> bitencode = new Hashtable<Character, String>();
	static String datafile = "";
	static Scanner scanner = new Scanner (System.in);
	static String path = "/Users/Matt/Downloads/";
	static StringBuffer buf = new StringBuffer(8);
	
	public static void main(String[] args) {
		//System.out.print("Enter the file to compress: ");
		datafile = scanner.next();
		reader();
		//printfreqs();
		buildNodes();
		node root = buildTree(nodes);
		printTree(root);
		buildBitTable(root, "");
		System.out.print(bitCount());
		encode();
		if(buf.length() > 0){
			emptyBuf();
		}
	}
	
	// Prints whatever remains in the temporary buffer.
	private static void emptyBuf() {
		
		while(buf.length() < 8){
			buf.append("0");
		}
		String temp = buf.toString();
		int decimal = Integer.parseInt(temp, 2);
		char output = (char) decimal;
		System.out.print(output);
		buf.delete(0, 8);
		
	}

	// Takes a file and compresses its contents using previously determined 
	// frequencies.
	private static void encode() {
		try{
			FileReader in = new FileReader(path + datafile);
			BufferedReader br = new BufferedReader(in);
			
			int character = 0;
			while(br.ready()){
				character = br.read();
				char temp = (char) character;
				String bits = bitencode.get(temp);
				for(int i = 0; i < bits.length(); i++){
					processBit(bits.charAt(i));
				}
				
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	// Processes the bits that are being read in by the encode function
	// 8 bits -> decimal -> character
	private static void processBit(char n) {
		buf.append(n);
		if(buf.length() == 8){
			String temp = buf.toString();
			int decimal = Integer.parseInt(temp, 2);
			char output = (char) decimal;
			System.out.write(output);
			buf = new StringBuffer(8);
		}
		
	}

	// Counts the amount of bits in the uncompressed file
	private static String bitCount() {
		int count = 0;
		StringBuffer buf2 = new StringBuffer(10);
		try{
			FileReader in = new FileReader(path + datafile);
			BufferedReader br = new BufferedReader(in);
			int character = 0;
			while(br.ready()){
				character = br.read();
				char temp = (char) character;
				count += bitencode.get(temp).length();
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		String check = Integer.toString(count);
		if(check.length() < 10){
			int zeros = 10 - check.length();
			for(int i = 0; i < zeros; i++){
				buf2.append("0");
			}
		}
		buf2.append(check);
		String output = buf2.toString();
		return output;
	}

	// Builds the bit table based our search tree
	private static void buildBitTable(node n, String i) {
		if(n.left != null){
			buildBitTable(n.left, i + "0");
		}
		else if(n.left == null && n.right == null){
			bitencode.put(n.character, i);
			//System.out.println("char '" + n.character + "' = " + i + " " + i.length());
		}
		
		if(n.right != null){
			buildBitTable(n.right, i + "1");
		}
		
	}

	// Build the tree by first sending our nodes into the priorityQueue
	// Then using the queue we construct our tree from the ground up.
	public static node buildTree(ArrayList<node> n){

		PriorityQueue<node> pq = new PriorityQueue<node>(n);

		while(pq.size() > 1){
			node left = pq.poll();
			node right = pq.poll();
			node parent = new node('\0', left.frequency + right.frequency, left, right);
			pq.add(parent);
		}

		return pq.poll();

	}

	// For each entry in out frequency table we construct a node.
	private static void buildNodes() {
		for(Character c : frequencies.keySet()){
			nodes.add(new node(c, frequencies.get(c), null, null));
		}
		
	}

	// Function to print the frequency table.
	@SuppressWarnings("unused")
	private static void printfreqs() {
		for(Character c : frequencies.keySet()){
			System.out.println("Character '" + c + "' " + frequencies.get(c));
		}
		
	}

	// Reads in the initial file so we can process each char
	private static void reader() {
		try{
			FileReader in = new FileReader(path + datafile);
			BufferedReader br = new BufferedReader(in);
			int character = 0;
			while(br.ready()){
				character = br.read();
				processChar((char) character);
				
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		
	}

	// Place each character into the frequency table
	// OR update the table to increase the already existing frequency
	private static void processChar(char letter) {
		char curr = letter;
		if(frequencies.containsKey(curr)){
			int freq = frequencies.get(curr);
			frequencies.put(curr, freq + 1);
		}
		else{
			frequencies.put(curr, 1);
		}
	}
	
	// Prints the tree
	static void printTree(node n) {
	    if (!n.isParent()){
	    	int value = (int)n.character;
	        System.out.print("(" + value + ")");
	    	
	    }
	    else {
	    	System.out.print("(");
	        printTree(n.left);
	        printTree(n.right);
	        System.out.print(")");
	    }
	}

}

// Node class
class node implements Comparable<node> {
	
	char character;
	int frequency;
	node left;
	node right;
	
	public node(char c, int f, node left, node right){
		this.character = c;
		this.frequency = f;
		this.left = left;
		this.right = right;
		
	}
	
	public boolean isParent(){
		if(character == '\0'){
			return true;
		}
		else{
			return false;
		}
	}
	
	public void setLeftChild(node o){
		this.left = o;
	}
	
	public void setRightChild(node o){
		this.right = o;
	}

	@Override
	public int compareTo(node o) {
		final int BEFORE = -1;
	    final int EQUAL = 0;
	    final int AFTER = 1;
	    
	    if (this.frequency < o.frequency) return BEFORE;
	    if (this.frequency > o.frequency) return AFTER;
	    
		return EQUAL;
	}

}

