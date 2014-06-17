package GA;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class MyGa {
	
	/**
	 * ��Ⱥ��ģ
	 */
	private int scale;
	
	/**
	 * ��������
	 */
	private int cityNum;
	
	/**
	 * �����б�
	 */
	private ArrayList<Integer> cityList;
	
	/**
	 * ������д���
	 */
	private int maxGen;
	
	/**
	 * ��ǰ���д���
	 */
	private int curGen;
	
	/**
	 * �������
	 */
	private double pc;
	
	/**
	 * �������
	 */
	private double pm;
	
	/**
	 * ��Ⱥ�и�����ۼƸ���
	 */
	private double[] pi;
	
	/**
	 *  ��ʼ��Ⱥ��������Ⱥ��������ʾ��Ⱥ��ģ��һ�д���һ�����壬��Ⱦɫ�壬�б�ʾȾɫ�����Ƭ��
	 */
	private int[][] oldPopulation;
	
	/**
	 * �µ���Ⱥ���Ӵ���Ⱥ
	 */
	private int[][] newPopulation;
	
	/**
	 * ��Ⱥ��Ӧ�ȣ���ʾ��Ⱥ�и����������Ӧ��
	 */
	private int[] fitness;
	
	/**
	 * �������ÿ�д���һ��Ⱦɫ��
	 */
	private double[][] distance;
	
	/**
	 * ��ѳ��ִ���
	 */
	private int bestGen;
	
	/**
	 * ��ѳ���
	 */
	private int bestLen;
	
	/**
	 * ���·��
	 */
	private int[] bestRoute;
	
	/**
	 * �����
	 */
	private Random random;

	/**
	 * 
	 * @param scale ��Ⱥ��ģ
	 * @param maxGen ���д���
	 * @param pc �������
	 * @param pm �������
	 */
	public MyGa(int scale, int maxGen, double pc, double pm){
		this.scale = scale;
		this.maxGen = maxGen;
		this.pc = pc;
		this.pm = pm;
	}
	
	/**
	 * ����һ��0-65535֮��������
	 * @return
	 */
	private int getRandomNum(){
		return this.random.nextInt(65535);
	}
	
	/**
	 * ��ʼ���㷨����file�м��������ļ�
	 * @param filename
	 * @throws IOException 
	 */
	public void init(String filename) throws IOException{
		ArrayList<Integer> x = new ArrayList<Integer>();
		ArrayList<Integer> y = new ArrayList<Integer>();
		this.cityList = new ArrayList<Integer>();
		
		//��ȡ����������Ϣ
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		String tmpStr = null;
		while((tmpStr = reader.readLine()) != null){
			String[] strArr = tmpStr.split(" ");
			this.cityList.add(Integer.parseInt(strArr[0]));//����Id
			x.add(Integer.parseInt(strArr[1]));//x ����
			y.add(Integer.parseInt(strArr[2]));//y ����
		}
		reader.close();
		
		this.cityNum = this.cityList.size();
		this.distance = new double[cityNum][cityNum];
		
		//ͨ��ŷʽ�������������
		for (int i = 0; i < cityNum - 1; i++) {
			distance[i][i] = 0; //�Խ���Ϊ0
			for (int j = i+1; j < cityNum; j++) {
				int xi = x.get(i);
				int yi = y.get(i);
				int xj = x.get(j);
				int yj = y.get(j);
				double rij = Math.sqrt(((xi - xj)*(xi - xj) + (yi - yj)*(yi - yj)));
				int tij = (int)Math.round(rij);
				if (tij < rij) {
					distance[i][j] = tij + 1;
					distance[j][i] = tij + 1;
				}else{
					distance[i][j] = tij;
					distance[j][i] = tij;
				}
			}
		}
		distance[cityNum - 1][cityNum - 1] = 0;//���һ�����еľ���Ϊ0��forѭ����û�г�ʼ��
		
		this.bestLen = Integer.MAX_VALUE;
		this.bestGen = 0;
		this.bestRoute = new int[cityNum];
		this.curGen = 0;
		
		this.newPopulation = new int[scale][cityNum];
		this.oldPopulation = new int[scale][cityNum];
		this.fitness = new int[scale];
		this.pi = new double[scale];
		
		this.random = new Random(System.currentTimeMillis());
	}
	
	/**
	 * ��ʼ����Ⱥ
	 */
	private void initGroup(){
		int i, j, k;
		for (k = 0; k < scale; k++) {
			for (i = 0; i < cityNum; ) {
				oldPopulation[k][i] = getRandomNum() % cityNum;
				//ȷ�����������Ⱦɫ����û���ظ��Ļ���
				for (j = 0; j < i; j++) {
					if (oldPopulation[k][i] == oldPopulation[k][j]) {
						break;
					}
				}
				if (i == j) {
					i++;
				}
			}
		}
	}
	
	/**
	 * ����Ⱦɫ��ľ���
	 * @param chromosome Ⱦɫ�壬��������ʼ����,����1,����2...����n
	 * @return the total distance of all chromosome's cities;
	 */
	private int evaluate(int[] chromosome){
		int len = 0;
		for(int i=1; i<cityNum; i++){
			int preCity = chromosome[i - 1];
			int curCity = chromosome[i];
			len += distance[preCity][curCity];
		}
		// ����n,��ʼ����
		len += distance[chromosome[cityNum - 1]][chromosome[0]];
//		System.out.println(" len:" + len);
		return len;
	}
	
	/**
	 * ������Ⱥ�и���������ۻ����ʣ�
	 * ǰ�����Ѿ�����������������Ӧ��fitness[max]��
	 * ��Ϊ����ѡ�����һ���֣�Pi[max]
	 */
	private void countRate(){
		double sumFitness = 0; 
		double[] tmpF = new double[scale];
		for (int i = 0; i < scale; i++) {
			//��������Ϊ����Խ�󣬸���Ӧ��ԽС
			tmpF[i]  = 10.0 / fitness[i];
			sumFitness += tmpF[i];
		}
		
		//�����ۼƸ���
		this.pi[0] = tmpF[0] / sumFitness;
		for (int i = 1; i < scale; i++) {
			pi[i] = (tmpF[i] / sumFitness) + pi[i - 1]; 
		}
	}
	
	/**
	 *  ��ѡĳ����Ⱥ����Ӧ����ߵĸ��壬ֱ�Ӹ��Ƶ��Ӵ��У�
	 *  ǰ�����Ѿ�����������������Ӧ��Fitness[max]
	 */
	private void selectBestGh(){
		int maxId = 0;
		int maxEvaluation = fitness[0];
		//��¼������С��cityId���ʶ�
		for (int i = 1; i < scale; i++) {
			if (maxEvaluation > fitness[i]) {
				maxEvaluation = fitness[i];
				maxId = i;
			}
		}
		
		//��¼��õ�Ⱦɫ����ִ���
		if (bestLen > maxEvaluation) {
			bestLen = maxEvaluation;
			bestGen = curGen;
			for (int i = 0; i < cityNum; i++) {
				bestRoute[i] = oldPopulation[maxId][i];
			}
		}
		
		// ��������Ⱥ����Ӧ����ߵ�Ⱦɫ��maxId���Ƶ�����Ⱥ�У����ڵ�һλ0
		this.copyGh(0, maxId);
	}
	
	/**
	 * ����Ⱦɫ�壬��oldPopulation���Ƶ�newPopulation
	 * @param curP ��Ⱦɫ������Ⱥ�е�λ��
	 * @param oldP �ɵ�Ⱦɫ������Ⱥ�е�λ��
	 */
	private void copyGh(int curP, int oldP){
		for (int i = 0; i < cityNum; i++) {
			newPopulation[curP][i] = oldPopulation[oldP][i];
		}
	}
	
	/**
	 * ����ѡ�������ѡ
	 */
	private void select(){
		int selectId = 0;
		double tmpRan;
//		System.out.print("selectId:");
		for (int i = 1; i < scale; i++) {
			tmpRan = (double)((getRandomNum() % 1000) / 1000.0);
			for (int j = 0; j < scale; j++) {
				selectId = j;
				if (tmpRan <= pi[j]) {
					break;
				}
			}
//			System.out.print(selectId+" ");
			copyGh(i, selectId);
		}
	}
	
	/**
	 * ���������������������
	 */
	public void evolution(){
		// ��ѡĳ����Ⱥ����Ӧ����ߵĸ���
		selectBestGh();
		// ����ѡ�������ѡscale-1����һ������
		select();
		
		double ran;
		for (int i = 0; i < scale; i = i+2) {
			ran = random.nextDouble();
			if (ran < this.pc) {
				//���С��pc������н���
				crossover(i, i+1);
			}else{
				//���ߣ����б���
				ran = random.nextDouble();
				if (ran < this.pm) {
					//����Ⱦɫ��i
					onVariation(i);
				}
				
				ran = random.nextDouble();
				if (ran < this.pm) {
					//����Ⱦɫ��i+1
					onVariation(i + 1);
				}
			}
		}
	}
	
	/**
	 * ���㽻��,��ͬȾɫ�彻�������ͬ�Ӵ�Ⱦɫ��
	 * @param k1 Ⱦɫ���� 1|234|56
	 * @param k2 Ⱦɫ���� 7|890|34
	 */
	private void crossover(int k1, int k2){
		//��ʱ������ڽ����Ⱦɫ��
		int[] gh1 = new int[cityNum];//Ⱦɫ��1
		int[] gh2 = new int[cityNum];//Ⱦɫ��2
		
		//������������λ��
		int pos1 = getRandomNum() % cityNum;
		int pos2 = getRandomNum() % cityNum;
		//ȷ��pos1��pos2����λ�ò�ͬ
		while(pos1 == pos2){
			pos2 = getRandomNum() % cityNum;
		}
		
		//ȷ��pos1С��pos2
		if (pos1 > pos2) {
			int tmpPos = pos1;
			pos1 = pos2;
			pos2 = tmpPos;
		}
		
		int i, j, k;
		//��¼��ǰ���ƽ���λ��
		int flag; 
		
		// ��Ⱦɫ��1�еĵ��������Ƶ�Ⱦɫ��2���ײ�
		for(i = 0, j = pos2; j < cityNum; i++, j++){
			gh2[i] = newPopulation[k1][j];
		}
		//Ⱦɫ��2ԭ����ʼλ��
		flag = i;
		
		//����ԴȾɫ��2��gh2����
		for(k = 0, j = flag; j < cityNum; k++){
			gh2[j] = newPopulation[k1][k];
			//���⽻����ͬһ��Ⱦɫ���д����ظ��Ļ���
			for (i = 0; i < flag; i++) {
				if (gh2[j] == gh2[i]) {
					break;
				}
			}
			//��Ⱦɫ���ز������ظ�����ʱ���Ÿ�����һ������
			if (i == flag) {
				j++;
			}
		}
		
		//������һ��Ⱦɫ��
		flag = pos1;
		for(k = 0, j = 0; k < cityNum; k++){
			gh1[j] = newPopulation[k1][k];
			//�ж�k2Ⱦɫ���0-pos1��λ���Ƿ��k1����ͬ
			for (i = 0; i < flag; i++) {
				if (newPopulation[k2][i] == gh1[j]) {
					break;
				}
			}
			if (i == flag) {
				j++;
			}
		}
		
		//����k1�ĵ�������
		flag = cityNum - pos1;
		for (i = 0, j = flag; j < cityNum; i++, j++) {
			gh1[j] = newPopulation[k2][i];
		}
		
		// ������ϷŻ���Ⱥ
		for (i = 0; i < cityNum; i++) {
			newPopulation[k1][i] = gh1[i];
			newPopulation[k2][i] = gh2[i];
		}
	}
	
	/**
	 * ��ζԻ���������
	 * �磺123456���153426������2��5�Ի���
	 * @param k Ⱦɫ����
	 */
	private void onVariation(int k){
		int ran1, ran2, tmp;
		//�Ի��������
		int count;
		
		count = getRandomNum() % cityNum;
		for (int i = 0; i < count; i++) {
			ran1 = getRandomNum() % cityNum;
			ran2 = getRandomNum() % cityNum;
			while(ran1 == ran2){
				ran2 = getRandomNum() % cityNum;
			}
			tmp = newPopulation[k][ran1];
			newPopulation[k][ran1] = newPopulation[k][ran2];
			newPopulation[k][ran2] = tmp;
		}
	}
	
	/**
	 * �������
	 */
	public void solve(){
		//��ʼ����Ⱥ
		initGroup();
		//�����ʼ�ʶ�
		for (int i = 0; i < scale; i++) {
			fitness[i] = this.evaluate(oldPopulation[i]);
		}
		// �����ʼ����Ⱥ�и���������ۻ����ʣ�pi[max]
		countRate();
		
		System.out.println("��ʼ��Ⱥ...");
		
		//��ʼ����
		for (curGen = 0; curGen < maxGen; curGen++) {
			evolution();
			// ������ȺnewGroup���Ƶ�����ȺoldGroup�У�׼����һ������
			for (int i = 0; i < scale; i++) {
				for (int j = 0; j < cityNum; j++) {
					oldPopulation[i][j] = newPopulation[i][j];
				}
			}
			
			//���㵱ǰ�����ʶ�
			for (int i = 0; i < scale; i++) {
				fitness[i] = this.evaluate(oldPopulation[i]);
			}
			
			// ���㵱ǰ��Ⱥ�и���������ۻ����ʣ�pi[max]
			countRate();
		}
		
		selectBestGh();
		
		System.out.println("��ѳ��ȳ��ִ�����");
		System.out.println(bestGen);
		System.out.println("��ѳ���");
		System.out.println(bestLen);
		System.out.println("���·����");
		for (int i = 0; i < cityNum; i++) {
			System.out.print(bestRoute[i] + ",");
		}
		
	}
	
	
	public static void main(String[] args) throws IOException{
		MyGa ga = new MyGa(6, 10, 0.8, 0.9);
		ga.init("./gadata/data2.txt");
		ga.solve();
	}
	
	
	
	
	
	
	
	
	

}
