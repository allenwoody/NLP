package test;

import java.rmi.Naming;

public class IndexServer1 {
	public static void main(String[] args){
		
		try {
			
//			String url = "rmi://192.168.233.15:1099/";//rmi://222.201.101.15:1099/
//			RMIClientService clientService = (RMIClientService)Naming.lookup(url + "RMIClientService");
//			String msg = clientService.getYourName();
//			System.out.println(msg);
			
//			String url = "rmi://192.168.233.15:1099/";//rmi://222.201.101.15:1099/
			String url = "rmi://222.201.101.15:1099/";//rmi://222.201.101.15:1099/
			RMIService serverService = (RMIService)Naming.lookup(url + "RMIService");
			//String msg = serverService.echo("20111003632 ������");
			byte[] msg = serverService.getMessage("20111003632 ������");
			System.out.println(new String(msg,"utf-8"));
			String msg1 = "20111003632 ������";
			String msg2 = "����һѧ�ڵ�ѧϰ���Ҿ��û���������������������˲��ٵ�֪ʶ���ر���java�ı�̡���socket,mysql,rmi�����˺�������˽⣬��ʦ���ν��úܺã������ʵ��Ļ���Ҳ�ܶ࣬ϣ���Ժ��л�����ѡлԺ���Ŀ�";
			String msg3 = serverService.putMessage(msg1, msg2.getBytes());
			System.out.println(msg3);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
