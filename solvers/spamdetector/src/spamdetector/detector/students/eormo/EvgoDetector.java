package spamdetector.detector.students.eormo;

import spamdetector.detector.Classifier;
import spamdetector.detector.TestCase;
import tester.Solver;

public class EvgoDetector implements Solver<TestCase, Classifier> {
	@Override
	public Classifier solve(TestCase t) {
		Classifier classifier = new Classifier();

		int weight = t.getUsefulEmails().length + t.getSpamEmails().length;
		double[] w = new double[weight];
		double[] coef = new double[t.getWeakClassifiers().length];
		int[] indexs = new int[t.getWeakClassifiers().length];
		boolean[] flag = new boolean[t.getWeakClassifiers().length];

		for (int i = 0; i < w.length; i++) {
			w[i] = 1.0 / weight;
		}
		int count = 0;
		for (int i = 0; i < t.getWeakClassifiers().length; i++) {
			double minimal = 1;
			int index = -111111;
			for (int j = 0; j < t.getWeakClassifiers().length; j++) {
				if (!flag[j]) {
					String rate = t.getWeakClassifiers()[j];
					double retvalue = getMinRate(rate, t, w);
					if (retvalue < minimal) {
						minimal = retvalue;
						index = j;

					}
				}
			}
			if (minimal >= 1.0 / 2)
				break;
			else {
				indexs[i] = index;
				coef[i] = (1.0 / 2) * (Math.log((1.0 - minimal) / minimal));
				flag[index] = true;
				foundNewWeigth(t.getWeakClassifiers()[index], t, minimal, w);
				count++;
			}

		}

		int[] nindex = new int[count];
		for (int i = 0; i < nindex.length; i++) {
			nindex[i] = indexs[i];
		}

		classifier.setCoefs(coef);
		classifier.setRuleIds(nindex);
		return classifier;
	}

	private void foundNewWeigth(String classifer, TestCase t, double E,
			double[] w) {

		String[] tmp = new String[2];
		if (classifer.contains("|")) {
			tmp = classifer.split("\\|");
			for (int i = 0; i < t.getSpamEmails().length; i++) {
				if (!t.getSpamEmails()[i].contains(tmp[0])
						&& !t.getSpamEmails()[i].contains(tmp[1])) {
					w[i] = 1.0 / 2 * w[i] / E;
				} else {
					w[i] = 1.0 / 2 * w[i] / (1.0 - E);
				}
			}
			for (int i = 0; i < t.getUsefulEmails().length; i++) {
				if (t.getUsefulEmails()[i].contains(tmp[0])
						|| t.getUsefulEmails()[i].contains(tmp[1])) {
					w[i + t.getSpamEmails().length] = 1.0 / 2
							* w[i + t.getSpamEmails().length] / E;
				} else {
					w[i + t.getSpamEmails().length] = 1.0 / 2
							* w[i + t.getSpamEmails().length] / (1.0 - E);
				}
			}
		} else if (classifer.contains("&")) {
			tmp = classifer.split("&");
			for (int i = 0; i < t.getSpamEmails().length; i++) {
				if (!t.getSpamEmails()[i].contains(tmp[0])
						|| !t.getSpamEmails()[i].contains(tmp[1])) {
					w[i] = 1.0 / 2 * w[i] / E;
				} else {
					w[i] = 1.0 / 2 * w[i] / (1.0 - E);
				}
			}
			for (int i = 0; i < t.getUsefulEmails().length; i++) {
				if (t.getUsefulEmails()[i].contains(tmp[0])
						&& t.getUsefulEmails()[i].contains(tmp[1])) {
					w[i + t.getSpamEmails().length] = 1.0 / 2
							* w[i + t.getSpamEmails().length] / E;
				} else {
					w[i + t.getSpamEmails().length] = 1.0 / 2
							* w[i + t.getSpamEmails().length] / (1.0 - E);
				}
			}
		} else {
			for (int i = 0; i < t.getSpamEmails().length; i++) {
				if (!t.getSpamEmails()[i].contains(classifer)) {
					w[i] = 1.0 / 2 * w[i] / E;
				} else {
					w[i] = 1.0 / 2 * w[i] / (1.0 - E);
				}
			}
			for (int i = 0; i < t.getUsefulEmails().length; i++) {
				if (t.getUsefulEmails()[i].contains(classifer)) {
					w[i + t.getSpamEmails().length] = 1.0 / 2
							* w[i + t.getSpamEmails().length] / E;
				} else {
					w[i + t.getSpamEmails().length] = 1.0 / 2
							* w[i + t.getSpamEmails().length] / (1.0 - E);
				}
			}

		}
	}

	private double getMinRate(String classifer, TestCase t, double[] w) {

		String[] tmp = new String[2];
		double min = 0;
		if (classifer.contains("|")) {
			tmp = classifer.split("\\|");
			for (int i = 0; i < t.getSpamEmails().length; i++) {
				if (!t.getSpamEmails()[i].contains(tmp[0])
						&& !t.getSpamEmails()[i].contains(tmp[1])) {
					min += w[i];
				}
			}
			for (int i = 0; i < t.getUsefulEmails().length; i++) {
				if (t.getUsefulEmails()[i].contains(tmp[0])
						|| t.getUsefulEmails()[i].contains(tmp[1])) {
					min += w[t.getSpamEmails().length + i];
				}
			}

		} else if (classifer.contains("&")) {
			tmp = classifer.split("&");
			for (int i = 0; i < t.getSpamEmails().length; i++) {
				if (!t.getSpamEmails()[i].contains(tmp[0])
						|| !t.getSpamEmails()[i].contains(tmp[1])) {
					min += w[i];
				}
			}
			for (int i = 0; i < t.getUsefulEmails().length; i++) {
				if (t.getUsefulEmails()[i].contains(tmp[0])
						&& t.getUsefulEmails()[i].contains(tmp[1])) {
					min += w[t.getSpamEmails().length + i];
				}
			}

		} else {
			for (int i = 0; i < t.getSpamEmails().length; i++) {
				if (!t.getSpamEmails()[i].contains(classifer)) {
					min += w[i];
				}
			}
			for (int i = 0; i < t.getUsefulEmails().length; i++) {
				if (t.getUsefulEmails()[i].contains(classifer)) {
					min += w[t.getSpamEmails().length + i];
				}
			}
		}
		return min;
	}

}
