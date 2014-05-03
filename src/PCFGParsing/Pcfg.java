package PCFGParsing;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;

import javax.swing.tree.DefaultMutableTreeNode;

public class Pcfg {

	private HashMap<String, Double> guideMap = null;//���������
	private ArrayList<String> wordsList = null;//�ȴ������Ĵ�
	private ArrayList<String> nonTermList = null;//���ն������
	
	/**
	 *ÿһ�������ڵĵ÷֣�keyΪ0,1,NP����ʽ,�ֱ����i�У�j�У�NP����
	 */
	private HashMap<String, Double> scoreMap = null;
	/**
	 * ����·����map, keyΪ0,1,NP����ʽ,�ֱ����i�У�j�У�NP����
	 */
	private HashMap<String, Triple> backMap = null;

	public Pcfg(){
		guideMap = new HashMap<String, Double>();
		wordsList = new ArrayList<String>();
		nonTermList = new ArrayList<String>();
		scoreMap = new HashMap<String, Double>();
		backMap = new HashMap<String, Triple>();
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

				String[] guideArr = lineArr[0].split("->");
				if(guideArr.length < 1)	continue;
				if(!nonTermList.contains(guideArr[0])){
					nonTermList.add(guideArr[0]);
//					System.out.println(guideArr[0]);
				}

			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return resultStr;
	}

	/**
	 * Ԥ�����
	 */
	public void preHandle(){
		for(int i=0; i<wordsList.size();i++){

			//handle the terminal words
			for(String A : nonTermList){
				String key = A + "->" + wordsList.get(i);
				if(guideMap.containsKey(key)){
					double score = guideMap.get(key);
					String skey = i + "," + (i+1) + "," + A;
					scoreMap.put(skey,score);
					backMap.put(skey, new Triple(i, i+1, wordsList.get(i), false));
				}
			}

			//handle the unaries
			boolean added = true;
			while(added){
				added = false;
				for(String A : nonTermList){
					for(String B : nonTermList){
						String keyB = i + "," + (i+1) + "," + B;
						if(scoreMap.containsKey(keyB) && guideMap.containsKey(A + "->" + B)){
							double prob = guideMap.get(A + "->" + B) * scoreMap.get(keyB);
							String keyA = i + "," + (i+1) + "," + A;
							//��������ɵ�prob����ԭ���ı��ʽ�ĸ��ʣ��򱣴�����
							double probA = scoreMap.containsKey(keyA) ? scoreMap.get(keyA) : 0;
							if (prob > probA) {
								scoreMap.put(keyA, prob);
								backMap.put(keyA, new Triple(i, i+1, B, true));
								added = true;
							}
						}
					}
				}
			}
		}
		System.out.println();
	}
	
	/**
	 * ��ʽ�����
	 */
	public void doHandle(){
		int wordsLen = wordsList.size();
		//���ÿ�ȣ��磺0-2 1-3 2-4; 0-3 1-4; 0-4����ʽ
		for(int span=2; span<=wordsLen; span++){
			for(int begin=0; begin <= wordsLen-span; begin++){
				int end = begin + span;
				for(int split = begin+1; split <= end-1; split++){
					calOneSquare(begin, end, split);
				}

				//--------handle unaries---------------------
				boolean added = true;
				while(added){
					added = false;
					for(String A : nonTermList){
						for(String B : nonTermList){
							String keyGuide = A + "->" + B;
							double probGuide = guideMap.containsKey(keyGuide) ? guideMap.get(keyGuide) : 0;
							String keyB = begin + "," + end + "," + B;
							double probB = scoreMap.containsKey(keyB) ? scoreMap.get(keyB) : 0;
							String keyA = begin + "," + end + "," + A;
							double probA = scoreMap.containsKey(keyA) ? scoreMap.get(keyA) : 0;
							
							//�������ĸ���
							double prob = probGuide*probB;
							if(prob > probA){
								scoreMap.put(keyA, prob);
								backMap.put(keyA, new Triple(begin, end, B, false));
								added = true;
							}
						}
					}
				}
			}
		}
		System.out.println("end of doHandle");
	}
	
	/**
	 * ����ÿһ������
	 */
	private void calOneSquare(int begin,int end, int split){
		for(String A : nonTermList){
			for(String B : nonTermList){
				for(String C : nonTermList){
					String keyA = begin + "," + end + "," + A;
					String keyB = begin + "," + split + "," + B;
					String keyC = split + "," + end + "," + C;
					double probA = scoreMap.containsKey(keyA) ? scoreMap.get(keyA) : 0;
					double probB = scoreMap.containsKey(keyB) ? scoreMap.get(keyB) : 0;
					double probC = scoreMap.containsKey(keyC) ? scoreMap.get(keyC) : 0;
					
					//��������ɵĹ���ĸ��ʣ����������ĸ���
					String key = A + "->" + B + " " + C;
					double probGuide = guideMap.containsKey(key) ? guideMap.get(key) : 0;
					double prob = probGuide*probB*probC;
					if(prob > probA){
						scoreMap.put(keyA, prob);
						backMap.put(keyA, new Triple(begin, end, split, B, C));
					}
				}
			}
		}
	}
	
	/**
	 * ��ӡ������
	 */
	public void display(){
		int len = wordsList.size();
		for(String word : nonTermList){
			String key = 0 + "," + len + "," + word;
			if(scoreMap.containsKey(key)){
				double prob = scoreMap.get(key);
				System.out.println(key + "---" + prob);
			}else{
				System.out.println(key + "---û��ֵ");
			}
		}
		
		System.out.println("--------------------------------------");
		String[] arr = scoreMap.keySet().toArray(new String[]{});
		Arrays.sort(arr);
		for (String key : arr) {
			double prob = scoreMap.get(key);
			System.out.println(key + "----" + prob);
		}
		
		System.out.println("--------------------------------------");
		String[] arrback = backMap.keySet().toArray(new String[]{});
		Arrays.sort(arrback);
		for (String key : arrback) {
			Triple triple = backMap.get(key);
			if (triple.isSplited()) {
				System.out.println(key + "--" + triple.getB() + "--" + triple.getC() + "--" + triple.getSplitPos());
			}else{
				System.out.println(key + "--" + triple.getB());
			}
		}
	}
	
	public DefaultMutableTreeNode output(){
		System.out.println("----------start of output----------------------------");
		LinkedList<Triple> backQueue = new LinkedList<>();
		LinkedList<DefaultMutableTreeNode> treeQueue = new LinkedList<>();
		Triple headTriple = backMap.get("0,4,S");//backMap��ͷָ��
		Triple pTriple = headTriple;//����backMapʱ��ָ��
		//����ͼ��ͷָ��
		DefaultMutableTreeNode headNode = new DefaultMutableTreeNode("S");
		//��������ͼ��ָ��
		DefaultMutableTreeNode pNode = headNode;
		backQueue.offer(headTriple);
		treeQueue.offer(headNode);
		while(backQueue.size() != 0){
			pTriple = backQueue.poll();
			pNode = treeQueue.poll();
			if(pTriple != null && pNode != null){//����Ƿ��ն˴�
				int begin = pTriple.getBegin();
				int end = pTriple.getEnd();
				if(pTriple.isSplited()){//������Է���
					int split = pTriple.getSplitPos();
					String keyB = begin + "," + split + "," + pTriple.getB();
					String keyC = split + "," + end + "," + pTriple.getC();
					Triple bTriple = backMap.get(keyB);
					Triple cTriple = backMap.get(keyC);
					backQueue.offer(bTriple);
					backQueue.offer(cTriple);
					DefaultMutableTreeNode bNode = new DefaultMutableTreeNode(pTriple.getB());
					DefaultMutableTreeNode cNode = new DefaultMutableTreeNode(pTriple.getC());
					treeQueue.offer(bNode);
					treeQueue.offer(cNode);
					pNode.add(bNode);
					pNode.add(cNode);
					
					System.out.println(begin + "," + end + "--" + pTriple.getB() + "--" + pTriple.getC() + "--" + pTriple.getSplitPos());
				}else{//���ɷ��ѵĴ�
					String keyB = begin + "," + end + "," + pTriple.getB();
					Triple bTriple = backMap.get(keyB);
					backQueue.offer(bTriple);
					DefaultMutableTreeNode bNode = new DefaultMutableTreeNode(pTriple.getB());
					treeQueue.offer(bNode);
					pNode.add(bNode);
					
					System.out.println(begin + "," + end + "--" + pTriple.getB() + "--" + pTriple.getSplitPos());
				}

			}else{//������ն˴�
				System.out.println("�ն˴���");
			}
		}

		System.out.println("end of output");
		return headNode;
	}


	public static void main(String[] args){
		Pcfg model = new Pcfg();
		String nonTermStr = model.loadGuide("pcfg_nonterm_guide.txt");
		System.out.println(nonTermStr);
		model.preHandle();
		model.doHandle();
//		model.display();
		model.output();
	}

}
