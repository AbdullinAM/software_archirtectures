package com.spbpu.facade;

/**
 * Created by kivi on 27.05.17.
 */
public class Triple<A,B, C> {
    private A first;
    private B second;
    private C third;

    public Triple(A a, B b, C c) {
        first = a;
        second = b;
        third = c;
    }

    public A getFirst() { return first; }
    public B getSecond() { return second; }
    public C getThird() { return third; }
    public void setFirst(A a) { first = a; }
    public void setSecond(B b) { second = b; }
    public void setThird(C c) { third = c; }

    public int hashCode() {
        int hashFirst = first != null ? first.hashCode() : 0;
        int hashSecond = second != null ? second.hashCode() : 0;
        int hashThird = third != null ? third.hashCode() : 0;

        return (hashFirst + hashSecond) * hashThird;
    }

    public boolean equals(Object other) {
        if (other instanceof Pair) {
            Triple otherTriple = (Triple) other;
            return ((  this.first == otherTriple.first ||
                    ( this.first != null && otherTriple.first != null &&
                            this.first.equals(otherTriple.first))) &&
                    (  this.second == otherTriple.second ||
                            ( this.second != null && otherTriple.second != null &&
                                    this.second.equals(otherTriple.second))) &&
                    (  this.third == otherTriple.third ||
                            ( this.third != null && otherTriple.third != null &&
                                    this.third.equals(otherTriple.third))) );
        }

        return false;
    }

    public String toString() {
        return "(" + first + ", " + second + ")";
    }
}
