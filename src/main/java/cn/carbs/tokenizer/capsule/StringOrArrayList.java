package cn.carbs.tokenizer.capsule;

import java.util.ArrayList;

public class StringOrArrayList implements IStateful {

    private String str;

    private ArrayList<String> arr;

    // extra == 1 是被染色
    private int state = 0;

    public StringOrArrayList(String str) {
        this.str = str;
    }

    public StringOrArrayList(ArrayList<String> arr) {
        if (arr != null) {
            this.arr = new ArrayList<>();
            this.arr.addAll(arr);
        }
    }

    public void addString(String str) {
        if (str == null) {
            return;
        }
        if (this.arr != null) {
            this.arr.add(str);
        } else {
            if (this.str == null) {
                this.str = str;
            } else {
                this.arr = new ArrayList<>();
                this.arr.add(this.str);
                this.arr.add(str);
                this.str = null;
            }
        }
    }

    public boolean isArray() {
        if (arr != null && arr.size() > 0) {
            return true;
        }
        return false;
    }

    public ArrayList<String> getArr() {
        return this.arr;
    }

    public String getStr() {
        return this.str;
    }

    @Override
    public String toString() {
        if (isArray()) {
            StringBuilder builder = new StringBuilder();
            for (String str : this.arr) {
                builder.append(str);
                builder.append("\n");
            }
            return builder.toString();
        } else {
            return this.str;
        }
    }

    @Override
    public void setState(int newState) {
        this.state = newState;
    }

    @Override
    public int getState() {
        return this.state;
    }
}
