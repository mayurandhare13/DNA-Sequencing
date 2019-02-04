import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

class DNA
{
    public static int gap_penalty;
    public static int h;
    public static int match = 0;
    public static int mismatch = 0;
    
    public static int findSubs(char[] s1, char[] s2, Cell[][] table)
    {
        int max_score = 0;
        for(int i=1; i<table.length; i++)
        {
            for(int j=1; j<table[0].length; j++)
            {
                table[i][j] = new Cell(0, 0, 0);
                table[i][j].sub_score = Math.max(table[i-1][j-1].sub_score, Math.max(table[i-1][j-1].del_score, table[i-1][j-1].ins_score)) + substitution(s1[i-1], s2[j-1]);


                table[i][j].del_score = Math.max(table[i-1][j].sub_score + (h + gap_penalty), Math.max(table[i-1][j].del_score + gap_penalty, table[i-1][j].ins_score + (h + gap_penalty)));

                table[i][j].ins_score = Math.max(table[i][j-1].sub_score + (h + gap_penalty), Math.max(table[i][j-1].del_score + (h + gap_penalty), table[i][j-1].ins_score + gap_penalty));
            }
        }

        return Math.max(table[s1.length][s2.length].sub_score, Math.max(table[s1.length][s2.length].del_score, table[s1.length][s2.length].ins_score));
    }

    private static int substitution(char a, char b)
    {
        if(a == b)
            return 1;

        return -2;
    }

    public static String[] readFile(String file) throws IOException
    {
        String S1 = null;
        String S2 = null;
        String[] dnaStrings = new String[2];
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        try 
        {
            String line = reader.readLine();
            while ((line = reader.readLine()) != null)
            {
                if(line.startsWith(">"))
                {
                    S1 = builder.toString();
                    builder.delete(0, builder.length());
                    continue;
                }
                builder.append(line);
            }
            S2 = builder.toString();
        } 
        finally{
            dnaStrings[0] = S1;
            dnaStrings[1] = S2;
            reader.close();
        }
        return dnaStrings;
    }
/*
    private static void printSubs(int[][] table, char[] s1, char[] s2, int max)
    {
        char[] subs = new char[max];
        
        int i = table.length-1, j = table[0].length-1;
        while(table[i][j] != 0)
        {
            if(table[i][j] == Math.max(table[i-1][j], table[i][j-1]))
            {

                if(table[i][j] == table[i][j-1])
                    j = j-1;
                else
                    i = i-1;  
            }
            else if(table[i][j] == table[i-1][j] && table[i][j] == table[i][j-1])
            {
                i -= 1;
            }
            else if(table[i][j] > Math.max(table[i-1][j], table[i][j-1]))
            {
                subs[--max] = s1[i-1];

                i--;
                j--;
            }
        }

        System.out.println(subs);
    }
*/
    public static Cell[][] init(int len1, int len2)
    {
        Cell[][] table = new Cell[len1+1][len2+1];

        table[0][0] = new Cell(0, 0, 0);

        table[0][1] = new Cell(h+gap_penalty, Integer.MIN_VALUE+100, Integer.MIN_VALUE+100);
        for(int j=2; j<table[0].length; j++)   // set 0th row 
        {
            table[0][j] = new Cell(table[0][j-1].ins_score + gap_penalty, Integer.MIN_VALUE+100, Integer.MIN_VALUE+100);
        }

        table[1][0] = new Cell(Integer.MIN_VALUE+100, h+gap_penalty, Integer.MIN_VALUE+100);
        for(int i=2; i<table.length; i++)   // set 0th column 
        {
            table[i][0] = new Cell(Integer.MIN_VALUE+100, table[i-1][0].del_score + gap_penalty, Integer.MIN_VALUE+100);
        }

        return table;
    }
    
    public static void main(String[] args) throws IOException
    {
        // String inFile = null;
        // if (0 < args.length) 
        // {
        //     inFile = args[0];
        // } 
        // else 
        // {
        //     System.err.println("Invalid arguments count:" + args.length);
        //     System.exit(0);
        // }

        gap_penalty = -2;
        h = -5;
        // String[] dnaStrings = readFile(inFile);

        // init(dnaStrings[0].length(), dnaStrings[1].length());

        String s1 = "ACATGCTACACGTATCCGATACCCCGTAACCGATAACGATACACAGACCTCGTACGCTTGCTACAACGTACTCTATAACCGAGAACGATTGACATGCCTCGTACACATGCTACACGTACTCCGAT";
        String s2 = "ACATGCGACACTACTCCGATACCCCGTAACCGATAACGATACAGAGACCTCGTACGCTTGCTAATAACCGAGAACGATTGACATTCCTCGTACAGCTACACGTACTCCGAT";

        System.out.println(s1.length());
        System.out.println(s2.length());

        Cell[][] table = init(s1.length(), s2.length());
        int max_score = findSubs(s1.toUpperCase().toCharArray(), s2.toUpperCase().toCharArray(), table);

        System.out.println("score: " + max_score);
    }
}