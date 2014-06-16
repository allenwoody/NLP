package GA;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class CityGa {
	
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
	private ArrayList<City> cityList;
	
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
	private double[] fitness;
	
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
	private double bestLen;
	
	/**
	 * ���·��
	 */
	private int[] bestRoute;
	
	/**
	 * �����
	 */
	private Random random;
	
	/**
	 * �Ƶ������
	 */
	private HotelHelper hotelHelper;
	
	/**
	 * ��������������
	 */
	private double upDay = 3;
	/**
	 * ��������������
	 */
	private double downDay = 2.0;

	/**
	 * 
	 * @param scale ��Ⱥ��ģ
	 * @param maxGen ���д���
	 * @param pc �������
	 * @param pm �������
	 */
	public CityGa(int scale, int maxGen, double pc, double pm){
		this.scale = scale;
		this.maxGen = maxGen;
		this.pc = pc;
		this.pm = pm;
		
		this.hotelHelper = new HotelHelper("./gadata/hotel.txt");
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
		this.cityList = new ArrayList<City>();
		
		//��ȡ������Ϣ
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		String tmpStr = null;
		while((tmpStr = reader.readLine()) != null ){
			String[] arr = tmpStr.split("\\s");
			City city = new City();
			city.setSid(arr[0]);
			city.setDays(Double.parseDouble(arr[1]));
			city.setPrice(Double.parseDouble(arr[2]));
			city.setLng(Double.parseDouble(arr[3]));
			city.setLat(Double.parseDouble(arr[4]));
			city.setViewCount(Integer.parseInt(arr[5]));
			cityList.add(city);
		}
		reader.close();
		
		this.cityNum = this.cityList.size();
		
		this.bestLen = Integer.MAX_VALUE;
		this.bestGen = 0;
		this.bestRoute = new int[cityNum];
		this.curGen = 0;
		
		this.newPopulation = new int[scale][cityNum];
		this.oldPopulation = new int[scale][cityNum];
		this.fitness = new double[scale];
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
	 * ���ۺ��������ڼ����ʶ�
	 * @param chromosome Ⱦɫ�壬����������1,����2...����n
	 * @return the total distance of all chromosome's cities;
	 */
	private double evaluate(int[] chromosome){
		double ticketPrice = 0;//��Ʊ
		double hotness = 0;//�ȶ�
		//�Ƶ굱ǰȾɫ���Ӧ�ľƵ���Ϣ
		ArrayList<Hotel> hotels = new ArrayList<Hotel>();
		for (int i = 0; i < chromosome.length; i++) {
			if (chromosome[i] == 1) {
				City city = cityList.get(i);
				ticketPrice +=  city.getPrice();
				hotness += (double)city.getViewCount();
				//��øþ���ľƵ���Ϣ
				Hotel hotel = hotelHelper.getHotel(city.getLng(), city.getLat());
				if (hotel != null) {
					hotels.add(hotel);
				}
			}
		}
		
		Collections.sort(hotels);
		double hotelPrice = 0.0;
		/* �жϾƵ�ĸ����Ƿ������Ҫ��ס������
		 * �������������ס����������۸�
		 * ���С����������оƵ�ļ۸�ʣ�������Ͱ�����ͼ۸����
		 */
		int len = Math.min(hotels.size(), (int)upDay);
		if (len != 0) {
			for (int i = 0; i < len; i++) {
				hotelPrice += hotels.get(i).getPrice();
			}
			int span = (int)(upDay - hotels.size());
			for (int i = 0; i < span; i++) {
				hotelPrice += hotels.get(i).getPrice();
			}
		}
		
		double price = hotelPrice + ticketPrice;
		double fitness = (10000.0 / (price + 10.0)) * 0.6 + Math.pow(hotness, 1.0/3.0) * 0.4;
		return fitness;
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
		double maxEvaluation = fitness[0];
		//��¼�ʶ�����cityId���ʶ�
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
		CityGa ga = new CityGa(30, 1000, 0.8, 0.9);
		ga.init("./gadata/data2.txt");
		ga.solve();
	}
	
	
	
	
	
	
	
	
	

}
