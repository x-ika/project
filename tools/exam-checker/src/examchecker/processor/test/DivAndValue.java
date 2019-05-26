package examchecker.processor.test;

import examchecker.processor.ITestDiv;

class DivAndValue {

    private ITestDiv div;

    private Object value;

    DivAndValue(ITestDiv div, Object value) {
        this.div = div;
        this.value = value;
    }

    ITestDiv getDiv() {
        return div;
    }

    String getValue() {
        return value.toString();
    }

    void setValue(Object value) {
        this.value = value;
    }

}
