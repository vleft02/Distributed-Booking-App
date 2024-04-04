package aueb.hestia.Helper;

import java.io.Serial;
import java.io.Serializable;

public class Pair<A,B> implements Serializable {
    private A key;
    private B value;

    public A getKey() {
        return key;
    }
    public void put(A key, B value) {
        this.key = key;
        this.value = value;
    }
    public void setKey(A key) {
        this.key = key;
    }

    public B getValue() {
        return value;
    }

    public void setValue(B value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "["+key+", "+value+"]";

    }
}
