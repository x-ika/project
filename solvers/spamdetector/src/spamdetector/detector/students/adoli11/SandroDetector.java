package spamdetector.detector.students.adoli11;

import java.util.List;
import java.util.ArrayList;

import spamdetector.detector.TestCase;
import spamdetector.detector.Classifier;

import tester.Solver;

public class SandroDetector implements Solver<TestCase, Classifier> {
	private static final int TYPE_ONE  = 1;
	private static final int TYPE_TWO = 2;
	private static final int TYPE_THREE  = 3;

    private static int convertType(String rule) {
        if (rule.indexOf("&") != -1)
            return TYPE_THREE;

        if (rule.indexOf("|") != -1)
            return TYPE_TWO;

        return TYPE_ONE;
    }

    private static boolean spamCheck(ClassifierData classifier, String email) {
         switch (classifier.type) {
             case TYPE_ONE:
                return email.indexOf(classifier.rule) != -1;
             case TYPE_TWO:
                String one = classifier.rule.substring(0, classifier.rule.indexOf("|"));
                String two = classifier.rule.substring(classifier.rule.indexOf("|") + 1);
                return email.indexOf(one) != -1 || email.indexOf(two) != -1;
             case TYPE_THREE:
                one = classifier.rule.substring(0, classifier.rule.indexOf("&"));
                two = classifier.rule.substring(classifier.rule.indexOf("&") + 1);
                return email.indexOf(one) != -1 && email.indexOf(two) != -1;
         }

        return true;
    }

    private class SampleData {
        public String data;
        public double weight;

        public SampleData(String content, double weight) {
            this.data = content;
            this.weight = weight;
        }
    }

    private class ClassifierData {
        public String rule;
        public int type;
        public double error;
        public boolean spamClassified[];
        public boolean goodClassified[];
        public boolean isUsable;

        public ClassifierData(String rule) {
            this.rule = rule;
            this.type = convertType(rule);
            this.spamClassified = new boolean[spamEmails.length];
            this.goodClassified = new boolean[goodEmails.length];
            this.isUsable = true;
        }
    }

	private SampleData[] goodEmails;
	private SampleData[] spamEmails;
	private List<Integer> rules = new ArrayList<>();
	private List<Double>  coefs = new ArrayList<>();
	private ClassifierData classifiers[];
	private double error;

    private void start(TestCase test) {
        double inverse = ((1.0) / (test.getUsefulEmails().length + test.getSpamEmails().length));

        spamEmails = new SampleData[test.getSpamEmails().length];
        for (int i=0; i<spamEmails.length; i++)
            spamEmails[i] = new SampleData(test.getSpamEmails()[i], inverse);

        goodEmails = new SampleData[test.getUsefulEmails().length];
        for (int i=0; i<goodEmails.length; i++)
            goodEmails[i] = new SampleData(test.getUsefulEmails()[i], inverse);

        classifiers = new ClassifierData[test.getWeakClassifiers().length];
        for (int i=0; i<test.getWeakClassifiers().length; i++)
            classifiers[i] = new ClassifierData(test.getWeakClassifiers()[i]);
    }

	public Classifier solve(TestCase testCase) {
		start(testCase);
		
		int used = 0;
		while (used != classifiers.length) {
			int[] bestIndex = new int[] {0};
			error = 1;

			compute(bestIndex);
			
			if (error > 0.5)
				break;

			rules.add(bestIndex[0]);
			coefs.add(Math.log((1 - error) / error) / 2);

			generateWeights(classifiers[bestIndex[0]]);
			classifiers[bestIndex[0]].isUsable = false;
			used++;
		}
		
		return convertResult();
	}
	
	private void compute(int[] bestIndex) {
		for (int i=0; i<classifiers.length; i++) {
			if (!classifiers[i].isUsable)
				continue;
			
			classifiers[i].error = 0;
			
			for (int j=0; j<goodEmails.length; j++) {
				if (spamCheck(classifiers[i], goodEmails[j].data)) {
					classifiers[i].error += goodEmails[j].weight;
					classifiers[i].goodClassified[j] = false;
				} else {
					classifiers[i].goodClassified[j] = true;
				}
			}
			
			for (int j=0; j<spamEmails.length; j++) {
				if (!spamCheck(classifiers[i], spamEmails[j].data)) {
					classifiers[i].error += spamEmails[j].weight;
					classifiers[i].spamClassified[j] = false;
				} else {
					classifiers[i].spamClassified[j] = true;
				}
			}
			
			if (classifiers[i].error < error) {
				bestIndex[0] = i;
				error = classifiers[i].error;
			}
		}
	}

    private Classifier convertResult() {
        Classifier res = new Classifier();

        int rule[] = new int[rules.size()];
        double coef[] = new double[coefs.size()];

        for (int i=0; i<coef.length; i++)
            coef[i] = coefs.get(i);

        for (int i=0; i<rule.length; i++)
            rule[i] = rules.get(i);

        res.setCoefs(coef);
        res.setRuleIds(rule);

        return res;
    }

	private void generateWeights(ClassifierData classifier) {
		for (int i=0; i<spamEmails.length; i++) {
			if (classifier.spamClassified[i]) {
				spamEmails[i].weight = ((spamEmails[i].weight) / ((1 - error) * 2));
			} else {
				spamEmails[i].weight = ((spamEmails[i].weight) / (error * 2));
			}
		}
		
		for (int i=0; i<goodEmails.length; i++) {
			if (classifier.goodClassified[i]) {
				goodEmails[i].weight = ((goodEmails[i].weight) / ((1 - error) * 2));
			} else {
				goodEmails[i].weight = ((goodEmails[i].weight) / (error * 2));
			}
		}
	}
}