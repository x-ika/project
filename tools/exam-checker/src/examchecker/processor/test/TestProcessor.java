package examchecker.processor.test;

import examchecker.core.Constants;
import examchecker.processor.*;

import java.util.*;

public class TestProcessor {

    public ICheckResult calculateDivValues(ITestDefinition def, IRecResult recResult) {

        List<DivAndValue> values = new ArrayList<>();

        for (ITestDiv div : def.getTestDivs()) {

            if (!recResult.recognized(div.getId())) {
                values.add(new DivAndValue(div, "_"));
                continue;
            }

            switch (div.getType()) {
                case SEQUENCE:
                    int fill = getFillInd(recResult, div.getId(), 0, div.getLength(), 1);
                    String value = fill == -1 ? Constants.UNDEFINED : "" + (div.getRangeStart() + fill);
                    values.add(new DivAndValue(div, value));
                    break;
                case NUMBER:
                    int len = div.getLength() / 10;
                    char[] number = new char[len];
                    for (int i = 0; i < len; i++) {
                        int digit = getFillInd(recResult, div.getId(), i, 10, len);
                        number[i] = digit == -1 ? Constants.UNDEFINED.charAt(0) : digit == 9 ? '0' : (char) ('1' + digit);
                    }
                    values.add(new DivAndValue(div, new String(number)));
                    break;
                case TEST:
                    int checked = getFilledSet(recResult, div.getId(), 0, div.getLength(), 1);
                    int correct = 0, all = (1 << div.getLength()) - 1;
                    for (char c : div.getCorrectAnswer().toCharArray()) {
                        correct |= 1 << c - 'A';
                    }
                    boolean taken = correct == all || checked > 0 && (correct & checked) == checked;
                    values.add(new DivAndValue(div, taken ? div.getTestScore() : 0));
                    break;
                case TEST_SUM:
                    values.add(new DivAndValue(div, 0));
                    break;
                case FILE_NAME:
                    values.add(new DivAndValue(div, recResult.getFile().getName()));
                    break;
            }

        }

        for (DivAndValue value : values) {
            if (value.getDiv().getType() == DivType.TEST_SUM) {
                int sum = 0;
                for (DivAndValue divValue : values) {
                    if (divValue.getDiv().getType() == DivType.TEST) {
                        try {
                            sum += Integer.parseInt(divValue.getValue());
                        } catch (NumberFormatException e) {
                            // do nothing
                        }
                    }
                }
                value.setValue(sum);
            }
        }

        return new ICheckResultImpl(values);
    }

    private int getFillInd(IRecResult recResult, String id, int from, int length, int d) {
        int set = getFilledSet(recResult, id, from, length, d);
        return Integer.bitCount(set) != 1 ? -1 : Integer.numberOfTrailingZeros(set);
    }

    private int getFilledSet(IRecResult recResult, String id, int from, int length, int d) {
        int ret = 0;
        for (int i = 0; i < length; i++) {
            if (recResult.getIsFilled(id, from + i * d)) {
                ret |= 1 << i;
            }
        }
        return ret;
    }

}
