package spamdetector.detector.students.kurid;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

public class Source {
	private String[] spams;
	private String[] useful;
	private String[] rules;
	public String[] getSpams(){
		return spams;
	}
	public String[] getUseful() {
		return useful;
	}
	public String[] getRules() {
		return rules;
	}
	public Source(){
		try {
			Scanner sc = new Scanner(new FileReader("test1.txt"));
			sc.useDelimiter("]");
			String spam = sc.next();
			spam = clean(spam);
			useful = spam.split(",");
			
			String us = sc.next();
			us = clean(us);
			spams = us.split(",");
			
			String rl = sc.next();
			rl = clean(rl);
			rules = rl.split(",");
			
			sc.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private String clean(String s){
		for(int i = 0; i < s.length(); i++){
			if(s.charAt(i) == '[')
				return s.substring(i+1);
		}
		return s;
	}
}
