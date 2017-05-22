/**
 * Created by kivi on 22.05.17.
 */

package com.spbpu.facade;


public class Pair<A, B> {
    private A first;
    private B second;

    public Pair(A a, B b) {
        first = a;
        second = b;
    }

    public A getFirst() { return first; }
    public B getSecond() { return second; }
    public void setFirst(A a) { first = a; }
    public void setSecond(B b) { second = b; }


    public int hashCode() {
        int hashFirst = first != null ? first.hashCode() : 0;
        int hashSecond = second != null ? second.hashCode() : 0;

        return (hashFirst + hashSecond) * hashSecond + hashFirst;
    }

    public boolean equals(Object other) {
        if (other instanceof Pair) {
            Pair otherPair = (Pair) other;
            return ((  this.first == otherPair.first ||
                    ( this.first != null && otherPair.first != null &&
                            this.first.equals(otherPair.first))) &&
                    (  this.second == otherPair.second ||
                            ( this.second != null && otherPair.second != null &&
                                    this.second.equals(otherPair.second))) );
        }

        return false;
    }

    public String toString() {
        return "(" + first + ", " + second + ")";
    }
}
