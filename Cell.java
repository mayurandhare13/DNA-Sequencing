class Cell
{
    public int ins_score;
    public int del_score;
    public int sub_score;
    public int gap_score;

    public Cell(int i, int d, int s)
    {
        ins_score = i;
        del_score = d;
        sub_score = s;
    }

    public String toString()
    {
        return "Cell(ins_score: "+ ins_score + ", del_score: " + del_score + ", sub_score: " + sub_score +")";
    }
}