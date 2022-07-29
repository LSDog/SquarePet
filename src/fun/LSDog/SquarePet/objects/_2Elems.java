package fun.LSDog.SquarePet.objects;

import java.util.Objects;

public class _2Elems<E1,E2> {

    public E1 e1;
    public E2 e2;

    public _2Elems(E1 e1, E2 e2) {
        this.e1 = e1;
        this.e2 = e2;
    }

    public boolean allNull() {
        return e1 == null && e2 == null;
    }

    public boolean containsNull() {
        return e1 == null || e2 == null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        _2Elems<?, ?> o1 = (_2Elems<?, ?>) o;

        return (Objects.equals(e1, o1.e1) && Objects.equals(e2, o1.e2));
    }

    @Override
    public int hashCode() {
        int result = e1 != null ? e1.hashCode() : 0;
        result = 31 * result + (e2 != null ? e2.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "$2Elems{" +
                "e1=" + e1 +
                ", e2=" + e2 +
                '}';
    }
}
