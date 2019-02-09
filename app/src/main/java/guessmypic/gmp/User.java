package guessmypic.gmp;

import java.util.Comparator;

/**
 * Created by HONGWEI on 2018/9/17.
 */

public class User {
    public String name;
    public int coin;
    public int winCount;
    public User(){}
    public User(String n,int c){
        this.name = n;
        this.coin = c;
        this.winCount = 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCoin() {
        return coin;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }

    public int getWinCount() {
        return winCount;
    }

    public void setWinCount(int winCount) {
        this.winCount = winCount;
    }
}
