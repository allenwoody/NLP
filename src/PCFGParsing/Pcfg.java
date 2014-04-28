package PCFGParsing;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;

public class Pcfg {

	private HashMap<String, Double> guideMap = null;//���������
	private ArrayList<String> wordsList = null;//�ȴ������Ĵ�
	private ArrayList<String> nonTermList = null;//���ն������
	
	/**
	 *ÿһ�������ڵĵ÷֣�keyΪ0,1,NP����ʽ,�ֱ����i�У�j�У�NP����
	 */
	private HashMap<String, Double> scoreMap = null;

	public Pcfg(){
		guideMap = new HashMap<String, Double>();
		wordsList = new ArrayList<String>();
		nonTermList = new ArrayList<String>();
		scoreMap = new HashMap<String, Double>();
		this.init();
	}
	
	private void init(){
		wordsList.add("fish");
		wordsList.add("people");
		wordsList.add("fish");
		wordsList.add("tanks");
	}
	
	public String loadGuide(String filePath){
		guideMap.clear();
		nonTermList.clear();
		String resultStr = "";
		try{
			BufferedReader reader = new BufferedReader(new FileReader(filePath));
			String lineStr = null;
			while((lineStr = reader.readLine()) != null){
				resultStr += lineStr + "\r\n";
				String[] lineArr = lineStr.split(",");
				if(lineArr.length < 2)	continue;
				guideMap.put(lineArr[0], Double.parseDouble(lineArr[1]));
				nonTermList.add(lineArr[0]);

			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return resultStr;
	}

	/**
	 * Ԥ�����
	 */
	public void preprocess(){
		for(int i=0; i<wordsList.size();i++){
			for(String A : nonTermList){
				String key = A + "->" + wordsList.get(i);
				if(guideMap.containsKey(key)){
					double score = guideMap.get(key);
					String skey = i + "," + (i+1) + "," + A;
					scoreMap.put(skey,score);
				}
			}
		}
	}


	public static void main(String[] args){
		Pcfg model = new Pcfg();
		String nonTermStr = model.loadGuide("pcfg_nonterm_guide.txt");
		System.out.println(nonTermStr);
		model.preprocess();
	}

}
