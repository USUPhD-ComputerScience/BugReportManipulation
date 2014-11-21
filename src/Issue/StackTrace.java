package Issue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

import Util.Util;

public class StackTrace {
	private List<String> mListCalls = null;
	public List<String> getmListCalls() {
		return mListCalls;
	}

	public void setmListCalls(List<String> mListCalls) {
		this.mListCalls = mListCalls;
	}

	private String mException;
	public String getmException() {
		return mException;
	}

	public void setmException(String mException) {
		this.mException = mException;
	}

	public String mMessage = null;
	public static final double mThreshold = 0.9;
	private static final double c_Coefficent = 0.1; // c is a coefficient for
													// the
													// distance to the
													// top
													// frame
	private static final double o_Coefficent = 0.1; // is a coefficient for the
													// alignment offset

	public StackTrace(String st, String ex, String mes) {
		mListCalls = Util.splitCalls(st);
		// System.out.println("==========StackTrace=======");
		/*
		 * for (int i = 0; i < mListCalls.size(); i++) { String[] call =
		 * mListCalls.get(i); for(int j = 0; j < call.length; j++){
		 * System.out.print(call[j]+"."); } System.out.print("\n"); }
		 */
		mException = ex;
		mMessage = mes;
	}

	public List<Integer> contains(List<String> something) {
		List<Integer> countedList = new ArrayList<>();
		for (int i = 0; i < something.size(); i++) {
			int count = 0;
			for (String call : mListCalls) {
				if (call.contains(something.get(i)))
					count++;
			}
			countedList.add(count);
		}
		return countedList;
	}

	// PDM!
	// compare similarity between two stackTraces using reBucket-icse2012:
	// Position Dependent Model (PDM)
	public double compareTo(StackTrace st2) {
		if (mException.equals(st2.mException)) {
			int m = mListCalls.size();
			int n = st2.mListCalls.size();
			double[][] M = new double[m + 1][n + 1];
			for (int i = 1; i <= m; i++) {
				for (int j = 1; j <= n; j++) {
					M[i][j] = M[i - 1][j - 1];
					if (mListCalls.get(i - 1).equals(st2.mListCalls.get(j - 1))) {
						M[i][j] += Math.exp((-c_Coefficent) * Math.min(i, j))
								* Math.exp((-o_Coefficent) * Math.abs(i - j));
					}
					if (M[i][j] < M[i - 1][j])
						M[i][j] = M[i - 1][j];
					if (M[i][j] < M[i][j - 1])
						M[i][j] = M[i][j - 1];
					// System.out.print(M[i][j] + ",");
				}
				// System.out.print("\n");
			}
			// int l = Math.min(m, n);
			// hamonic average
			long l = Math.round(2.0 / (1.0 / m + 1.0 / n));
			double sumOfExponetialDistance = 0;
			// j run from 1 to l, not 0 to l; This is slightly different from
			// the equation 4 and equation 1 in reBucket Paper
			for (double j = 1; j <= l; j++) {
				sumOfExponetialDistance += Math.exp((-c_Coefficent) * j);
			}
			return M[m][n] / sumOfExponetialDistance;
		} else
			return 0;
	}

}
