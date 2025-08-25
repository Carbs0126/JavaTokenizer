package cn.carbs.tokenizer.capsule;

public class SameFilesInfo implements Comparable<SameFilesInfo> {

    public StringOrArrayList stringOrArrayList;

    public long sizeOfSameRedundantFileSize;

    public SameFilesInfo(StringOrArrayList stringOrArrayList, long sizeOfSameRedundantFileSize) {
        this.stringOrArrayList = stringOrArrayList;
        this.sizeOfSameRedundantFileSize = sizeOfSameRedundantFileSize;
    }

    // Collections.sort(list, Comparator.reverseOrder());
    @Override
    public int compareTo(SameFilesInfo other) {
        return Long.compare(this.sizeOfSameRedundantFileSize, other.sizeOfSameRedundantFileSize);
    }

}