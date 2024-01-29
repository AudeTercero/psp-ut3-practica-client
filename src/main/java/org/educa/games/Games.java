package org.educa.games;

public interface Games {
    public void play(int role) throws InterruptedException;
    public int getPlayerNeeded();
    public String getName();
}
