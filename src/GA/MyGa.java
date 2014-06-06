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
				double rij = Math.sqrt(((xi - xj)*(xi - xj) + (yi - yj)*(yi - yj)) / 10.0);
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
	
	private int evaluate(int[] chromosome){
		return 0;
	}
	
	/**
	 * ����һ��0-65535֮��������
	 * @return
	 */
	private int getRandomNum(){
		return this.random.nextInt(65535);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
