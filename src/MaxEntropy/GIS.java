package MaxEntropy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * ����ص�GIS�㷨
 * @author wangjiewen
 *
 */
public class GIS {

	/**
	 * ѵ�������ļ��� N
	 */
	private ArrayList<String[]> trainList = null;
	
	private HashSet<String> labelSet = null;
	
	/**
	 * �����ļ��� n
	 */
	private HashMap<String, Integer> featuresMap = null;
	
	/**
	 * ������id�±��ӳ��
	 */
	private HashMap<String, Integer> indexMap = null;
	
	/**
	 * �������
	 */
	private double[] ep_ = null;
	
	/**
	 * ��������
	 */
	private double[] ep = null;
	
	/**
	 * �������ճ��� ��
	 */
	private double[] lambda = null;
	
	/**
	 * ��һ�ε������������ճ���
	 */
	private double[] lambdaOld = null;
	
	/**
	 * �ж������Ľ���
	 */
	private double epsilon = 0.01;
	
	/**
	 * GIS �㷨������������
	 */
	private double maxIter = 1000;
	
	/**
	 * ����������
	 */
	private int N = 0;
	
	/**
	 * GIS��ϵ��
	 */
	private int C = 0;
	
	
	
	public GIS(){
		trainList = new ArrayList<String[]>();
		labelSet = new HashSet<String>();
		featuresMap = new HashMap<String, Integer>();
		indexMap = new HashMap<String, Integer>();
		
		//��ʼ������
		loadData();
		initParam();
	}
	
	private void loadData(){
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File("MaxEntropy/train.txt")));
			String buff = null;
			while((buff = reader.readLine()) != null){
				String[] fields = buff.split(" ");
				String label = fields[0];
				labelSet.add(label);
				
				for (int i = 1; i < fields.length; i++) {
					String key = genKey(label, fields[i]);
					if(!featuresMap.containsKey(key)){
						featuresMap.put(key, 1);
					}else{
						int count = featuresMap.get(key) + 1;
						featuresMap.put(key, count);
					}
				}
				trainList.add(fields);
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void initParam(){
		this.N = trainList.size();
		
		for (String[] fields : trainList) {
			int len = fields.length - 1; //��һ����label
			if(len > this.C){
				this.C = len;
			}
		}
		
		//���㾭����� ep_
		Iterator<String> iter = featuresMap.keySet().iterator();
		int featLen = featuresMap.size();
		this.ep_ = new double[featLen];
		for (int i = 0; i < featLen; i++) {
			String key = iter.next();
			int count = this.featuresMap.get(key);
			double prob = (double)count / (double)this.N;
			
			this.ep_[i] = prob;
			indexMap.put(key, i);
		}
		
		//��ʼ�� ��
		this.lambda = new double[featLen];
		this.lambdaOld = lambda.clone();
	}
	
	private static String genKey(String label, String field){
		return label + "-" + field;
	}
	
	/**
	 * ����Z����� exp(�� ��i*fi(a,b))
	 * @param features ��������
	 * @param label ����ı�ǩ
	 * @return
	 */
	private double zFunc(String[] features, String label){
		double sum = 0.0;
		for (int i = 0; i < features.length; i++) {
			String feat = features[i];
			String key = genKey(label, feat);
			if (this.featuresMap.containsKey(key)) {//����fi(a,b) == 1
				int index = this.indexMap.get(key);
				sum += this.lambda[index];
			}
		}
		sum = Math.exp(sum);
		return sum;
	}
	
	/**
	 * ���� p`(b|a) = (1/Z)*exp(�� ��i*fi(a,b))
	 * @param features
	 * @param label
	 * @return
	 */
	private double pFunc(String[] features, String label){
		double prob = 0.0;
		
		//�� z = ��exp(�� ��i*fi(a,b))
		double Z = 0.0;
		for(String l : this.labelSet){
			Z += zFunc(features, l);
		}
		
		prob = (1.0/Z) * zFunc(features, label);
		return prob;
	}
	
	/**
	 * ������������ Ep
	 * @return
	 */
	private double[] calcEp(){
		double[] ep = new double[this.featuresMap.size()];
		
		for (String[] record : this.trainList) {
			//��һ����label
			String[] features = Arrays.copyOfRange(record, 1, record.length);
			
			for (String label : this.labelSet) {
				double prob = pFunc(features, label);
				
				for (String feature : features) {
					String key = genKey(label, feature);
					if (this.featuresMap.containsKey(key)) {
						int index = this.indexMap.get(key);
						// �� p(a) * p(b|a) * f(a,b), p(a) = 1/N
						ep[index] += (1.0/N) * prob;
					}
				}
			}
		}
		
		return ep;
	}
	
	/**
	 * �ж��Ƿ�����GIS����������
	 * @param lambda
	 * @param lambdaOld
	 * @return
	 */
	private boolean isConvergent(double[] lambda, double[] lambdaOld){
		int len = lambda.length;
		for(int i = 0; i < len; i++){
			if(Math.abs(lambda[i] - lambdaOld[i]) >= this.epsilon){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * ����ѵ��
	 */
	public void train(){
		for (int k = 0; k < this.maxIter; k++) {
			System.out.println("iter -->" + k);
			if (k >= 621) {
				System.out.println("----");
			}
			this.ep = this.calcEp();
			this.lambdaOld = this.lambda.clone();
			
			int len = this.lambda.length;
			for (int i = 0; i < len; i++) {
				//update lambda
				this.lambda[i] += (1.0 / C) * Math.log(this.ep_[i] / this.ep[i]);
			}
			
			//�ж��Ƿ�����
			if (this.isConvergent(this.lambda, this.lambdaOld)) {
				break;
			}
		}
	}
	
	
	public void predict(String[] features){
		for (String label : this.labelSet) {
			double prob = this.pFunc(features, label);
			System.out.println(label + " " + prob);
		}
	}
	
	
	
	public static void main(String[] args){
		GIS entropy = new GIS();
		entropy.train();
		System.out.println();
		System.out.println("---------------");
		entropy.predict(new String[]{"Sunny","Happy", "Dry"});
		
		System.out.println("---------------");
		entropy.predict(new String[]{"Cloudy","Sad", "Humid"});
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
}
