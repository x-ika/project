package examchecker.processor.test;

import com.simplejcode.commons.misc.util.StreamUtils;
import examchecker.processor.ITestDiv;
import examchecker.processor.ICheckResult;

import java.util.List;
import java.util.Map;

class ICheckResultImpl implements ICheckResult {

    private Map<String, DivAndValue> values;

    ICheckResultImpl(List<DivAndValue> values) {
        this.values = StreamUtils.list2map(values, t -> t.getDiv().getId());
    }

    public ITestDiv getDiv(String id) {
        return values.get(id).getDiv();
    }

    public String getValue(String id) {
        return values.get(id).getValue();
    }

}
