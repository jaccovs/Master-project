package org.exquisite.diagnosis.sfl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ObservationMatrix {

	Map<String, Integer> cells;
	Map<String, BigInteger[]> compressedInfo = null;
	boolean[] error;
	Map<Integer, String> integerToCellMap = null;
	boolean[][] obs;
	
	
	public Map<String, Integer> getCells() {
		return cells;
	}

	public boolean[] getErrorVector() {
		return error;
	}

	public boolean[][] getObservationMatrix() {
		return obs;
	}


	/**
	 * @param obs
	 * @param cells
	 * @param error
	 * @throws ObservationMatrixCreationException
	 */
	public ObservationMatrix(boolean[][] obs, Map<String, Integer> cells,
			boolean[] error) throws ObservationMatrixCreationException {
		if (obs.length != cells.size())
			throw new ObservationMatrixCreationException(
					"The length of the observation matrix does not fit to the cells map!");

		for (int i = 0; i < obs.length; i++) {
			if (obs[i].length != error.length)
				throw new ObservationMatrixCreationException(
						"The length of the observation matrix entries do not fit to the error vector!");
		}

		this.obs = obs;
		this.cells = cells;
		computeIntegerToCellMap();
		this.error = error;
		computeCompressedInfo();
	}

	/**
	 * @param positiveCones
	 *            List of cones from positive output cells
	 * @param negativeCones
	 *            List of cones from negative output cells
	 * @param range
	 *            Set of cells that should be considered in this observation
	 *            matrix
	 * @param similarityCoefficients
	 */
	public ObservationMatrix(List<Set<String>> positiveCones,
			List<Set<String>> negativeCones, Set<String> range) {
		int index = 0;
		cells = new HashMap<String, Integer>();
		for (String cell : range) {
			cells.put(cell, index++);
		}

		error = new boolean[positiveCones.size() + negativeCones.size()];
		for (int i = positiveCones.size(); i < error.length; i++) {
			error[i] = true;
		}

		obs = new boolean[cells.size()][];
		for (int i = 0; i < cells.size(); i++) {
			obs[i] = new boolean[error.length];
		}

		int counter = 0;
		for (Set<String> cone : positiveCones) {
			for (String cell : cone) {
				if (cells.containsKey(cell)) {
					obs[cells.get(cell)][counter] = true;
				}

			}
			counter++;
		}
		for (Set<String> cone : negativeCones) {
			for (String cell : cone) {
				if (cells.containsKey(cell)) {
					obs[cells.get(cell)][counter] = true;
				}
			}
			counter++;
		}

		computeCompressedInfo();
		computeIntegerToCellMap();
	}

	protected void computeCompressedInfo() { // [a11, a10, a01, a00]
		compressedInfo = new HashMap<String, BigInteger[]>();

		for (String cell : cells.keySet()) {
			BigInteger failedInvolved = BigInteger.ZERO;
			BigInteger passedInvolved = BigInteger.ZERO;
			BigInteger failedNotInvolved = BigInteger.ZERO;
			BigInteger passedNotInvolved = BigInteger.ZERO;
			
		
			for (int i = 0; i < error.length; i++) {
				if (obs[cells.get(cell)][i] == true && error[i] == true)
					failedInvolved=failedInvolved.add(BigInteger.ONE);
				else if (obs[cells.get(cell)][i] == true && error[i] == false)
					passedInvolved=passedInvolved.add(BigInteger.ONE);
				else if (obs[cells.get(cell)][i] == false && error[i] == true)
					failedNotInvolved=failedNotInvolved.add(BigInteger.ONE);
				else
					passedNotInvolved=passedNotInvolved.add(BigInteger.ONE);
			}
			compressedInfo.put(cell, new BigInteger[] { failedInvolved,
					passedInvolved, failedNotInvolved, passedNotInvolved });

		}

	}

	private void computeIntegerToCellMap() {
		if (integerToCellMap == null) {
			integerToCellMap = new HashMap<Integer, String>();
			for (String cell : cells.keySet()) {
				integerToCellMap.put(cells.get(cell), cell);
			}
		}
	}

	private String fill(String str, int fillSize) {
		StringBuilder strB = new StringBuilder();
		while ((fillSize - str.length()) / 2 > strB.length())
			strB.append(" ");
		strB.append(str);
		while (strB.length() < fillSize)
			strB.append(" ");
		return strB.toString();
	}

	/**
	 * 
	 * @param sc
	 *            The coefficient that should be used
	 * @return The ranking number for each cell (starting with 1, cells with the
	 *         same number have both the lower ranking number)
	 */
	public Map<String, Integer> getCoefficientRanking(SimilarityCoefficient sc) {
		Map<String, BigDecimal> coefficientValues = getCoefficientValues(sc);
		Collection<BigDecimal> unsorted = coefficientValues.values();
		List<BigDecimal> sorted = asSortedList(unsorted);

		Map<String, Integer> ranking = new HashMap<String, Integer>();
		
		for(String cell : cells.keySet()){
			ranking.put(cell, sorted.size()-sorted.lastIndexOf(coefficientValues.get(cell)));
		}
		
		return ranking;
	}
		
	
	public static
	<T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
	  List<T> list = new ArrayList<T>(c);
	  java.util.Collections.sort(list);
	  return list;
	}
	/**
	 * 
	 * @param sc
	 *            The coefficient that should be used
	 * @return The computed coefficient for each cell
	 */
	public Map<String, BigDecimal> getCoefficientValues(SimilarityCoefficient sc) {
		Map<String, BigDecimal> coefficients = new HashMap<String, BigDecimal>();
		for (String cell : cells.keySet()) {
			BigInteger a11 = compressedInfo.get(cell)[0];
			BigInteger a10 = compressedInfo.get(cell)[1];
			BigInteger a01 = compressedInfo.get(cell)[2];
			BigInteger a00 = compressedInfo.get(cell)[3];

			coefficients.put(cell,
					sc.getSimilarityCoefficient(a11, a10, a01, a00));
		}
		return coefficients;
	}

	public Map<String, Integer> getMapping() {
		return cells;
	}

	private int getSizeOfLongestCell() {
		int length = 0;
		for (String cell : cells.keySet()) {
			if (cell.length() > length)
				length = cell.length();
		}
		return length+1;
	}

	@Override
	public String toString() {
		StringBuilder strB = new StringBuilder();

		int length = getSizeOfLongestCell();

//		strB.append("OBSERVATION MATRIX");
//		strB.append(System.lineSeparator());
		for (Integer i = 0; i < cells.size(); i++) {
			String cell = integerToCellMap.get(i);
			strB.append(fill(cell, length));
		}
		strB.append("| ERROR");
		strB.append(System.lineSeparator());
		for (int i = 0; i < (cells.size()) * length; i++) {
			strB.append("-");
		}
		strB.append("-------");
		strB.append(System.lineSeparator());

		for (Integer j = 0; j < error.length; j++) {
			for (int i = 0; i < cells.size(); i++) {
				strB.append(fill(obs[i][j] ? "\u2022" : " ", length));
			}
			strB.append("|  ");
			strB.append(error[j] ? "\u2022" : " ");
			strB.append(System.lineSeparator());
		}
		strB.append(System.lineSeparator());

		return strB.toString();
	}
	
	
	public String toCSV() {
		StringBuilder strB = new StringBuilder();

//		strB.append("OBSERVATION MATRIX");
//		strB.append(System.lineSeparator());
		for (Integer i = 0; i < cells.size(); i++) {
			String cell = integerToCellMap.get(i);
			strB.append(cell);
			strB.append(";");
		}
		strB.append("ERROR");
		strB.append(System.lineSeparator());
		
		for (Integer j = 0; j < error.length; j++) {
			for (int i = 0; i < cells.size(); i++) {
				strB.append(obs[i][j] ? "1" : "0");
				strB.append(";");
			}
			strB.append(error[j] ? "1" : "0");
			strB.append(System.lineSeparator());
		}
		return strB.toString();
	}
	
	@Override
	public boolean equals(Object obj){
		if(! (obj instanceof ObservationMatrix))
			return false;
		ObservationMatrix obs2 = (ObservationMatrix) obj;
		if(obs2.cells.size() != cells.size())
			return false;
		if(obs2.error.length != error.length)
			return false;
		if(obs2.obs.length != obs.length)
			return false;
		for(int i=0; i<obs.length;i++){
			if(obs2.obs[i].length != obs[i].length){
				return false;
			}
		}
		
		for(String coord :cells.keySet()){
			if(!obs2.cells.containsKey(coord))
				return false;
		}
		
		for(String coord: compressedInfo.keySet()){
			BigInteger[] compressed1 = compressedInfo.get(coord);
			BigInteger[] compressed2 = obs2.compressedInfo.get(coord);
			for(int i=0;i<compressed1.length;i++){
				if(compressed1[i].compareTo(compressed2[i])!=0)
					return false;
			}
		}
		
		return true;
	}
}
