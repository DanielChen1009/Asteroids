package net.danielchen.core;

/**
 * A basic Pair class that can wrap two objects and be used as keys of Maps.
 */
public class Pair<A, B> {
    final A val0;
    final B val1;
    private static final int PRIME = 31;

    Pair(final A val0, final B val1) {
        this.val0 = val0;
        this.val1 = val1;
    }

    @Override
    public final int hashCode() {
        int result = PRIME + this.val0.hashCode();
        result = PRIME * result + this.val1.hashCode();
        return result;
    }

    @Override
    public final boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (this.getClass() != obj.getClass())
            return false;
        final Pair<A, B> other = (Pair<A, B>) obj;
        return this.val0.equals(other.val0) && this.val1.equals(other.val1);
    }
}
