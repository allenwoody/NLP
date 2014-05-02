package PCFGParsing;

public class Triple {
	
	private int begin = 0;
	private int end = 0;
	private int splitPos = 0;
	private String B = null;
	private String C = null;
	/**
	 * ��ʾ�Ƿ�ɷ��ѵĽڵ�
	 */
	private boolean isSplited = false;
	/**
	 * ��ʾ�Ƿ�Ϊ�ն˴�
	 */
	private boolean isTerm = false;

	/**
	 * ��ȴ��ڵ���2�Ŀ��Է��ѵĽڵ�
	 * @param begin
	 * @param end
	 * @param split ���ѵ��λ��
	 * @param B
	 * @param C
	 */
	public Triple(int begin, int end, int split, String B, String C){
		this.begin = begin;
		this.end = end;
		this.splitPos = split;
		this.B = B;
		this.C = C;
		this.isSplited = true;
	}
	
	/**
	 * ���ɷ��ѵĽڵ�
	 * @param begin
	 * @param end
	 * @param B
	 * @param isTerm �Ƿ�Ϊ�ն˴ʣ�Ĭ��Ϊfalse
	 */
	public Triple(int begin, int end, String B, boolean isTerm){
		this.begin = begin;
		this.end = end;
		this.B = B;
		this.isSplited = false;
		this.isTerm = isTerm;
	}
	
	public int getSplitPos() {
		return splitPos;
	}

	public void setSplitPos(int splitPos) {
		this.splitPos = splitPos;
	}

	public String getB() {
		return B;
	}

	public void setB(String b) {
		B = b;
	}

	public String getC() {
		return C;
	}

	public void setC(String c) {
		C = c;
	}

	public boolean isSplited() {
		return isSplited;
	}

	public void setSplited(boolean isSplited) {
		this.isSplited = isSplited;
	}

	public boolean isTerm() {
		return isTerm;
	}

	public void setTerm(boolean isTerm) {
		this.isTerm = isTerm;
	}
	
	public int getBegin() {
		return begin;
	}

	public void setBegin(int begin) {
		this.begin = begin;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

}
