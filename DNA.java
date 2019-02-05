import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

class DNA
{
    public static int gap_penalty;
    public static int h;
    public static int match = 1;
    public static int mismatch = -2;
    
    public static Cell[][] globalForword(char[] s1, char[] s2)
    {
        Cell[][] table = initGlobalCells(s1.length, s2.length);

        for(int i=1; i<table.length; i++)
        {
            for(int j=1; j<table[0].length; j++)
            {
                table[i][j] = new Cell(0, 0, 0);
                table[i][j].sub_score = substitution_score(table, i, j, s1, s2);

                table[i][j].del_score = deletion_score(table, i, j);

                table[i][j].ins_score = insertion_score(table, i, j);

                int max = Math.max(table[i][j].ins_score, Math.max(table[i][j].del_score, table[i][j].sub_score));
                if(max < 0)
                    table[i][j].score = 0;
                else
                    table[i][j].score = max;
            }
        }
        return table;
    }

    private static int deletion_score(Cell[][] table, int i, int j)
    {
        return Math.max(table[i-1][j].sub_score + (h + gap_penalty), Math.max(table[i-1][j].del_score + gap_penalty, table[i-1][j].ins_score + (h + gap_penalty)));
    }

    private static int insertion_score(Cell[][] table, int i, int j)
    {
        return Math.max(table[i][j-1].sub_score + (h + gap_penalty), Math.max(table[i][j-1].del_score + (h + gap_penalty), table[i][j-1].ins_score + gap_penalty));
    }

    private static int substitution_score(Cell[][] table, int i, int j, char[] s1, char[] s2)
    {
        return Math.max(table[i-1][j-1].sub_score, Math.max(table[i-1][j-1].del_score, table[i-1][j-1].ins_score)) + substitution(s1[i-1], s2[j-1]);
    }

    private static int substitution(char a, char b)
    {
        if(a == b)
            return match;

        return mismatch;
    }

    public static void globalBacktrack(char[] s1, char[] s2, Cell[][] table)
    {
        String AlignmentA = "";
        String AlignmentB = "";
        int i = s1.length;
        int j = s2.length;

        while (i > 0 || j > 0)
        {
            if (i > 0 && j > 0 && table[i][j].score == substitution_score(table, i, j, s1, s2))
            {
                AlignmentA = s1[i-1] + AlignmentA;
                AlignmentB = s2[j-1] + AlignmentB;
                i = i - 1;
                j = j - 1;
            }
            else if (i > 0 && table[i][j].score == deletion_score(table, i, j))
            {
                AlignmentA = s1[i-1] + AlignmentA;
                AlignmentB = "-" + AlignmentB;
                i = i - 1;
            }
            else
            {
                AlignmentA = "-" + AlignmentA;
                AlignmentB = s2[j-1] + AlignmentB;
                j = j - 1;
            }
        }
        printAlignment(AlignmentA, AlignmentB);
    }

    private static void printAlignment(String s1, String s2)
    {
        int no_of_gaps = 0;
        int no_of_matches = 0;
        int no_of_mismatch = 0;
        int no_of_opening_gaps = 0;
        StringBuilder middle = new StringBuilder(s1.length());

        int i = 0;
        while(i < s1.length())
        {
            if(s1.charAt(i) == s2.charAt(i))
            {
                middle.append('|');
                no_of_matches++;
            }
            else if(s1.charAt(i) == '-')
            {
                if(i>0 && s1.charAt(i-1) != '-')
                    no_of_opening_gaps++;

                middle.append(' ');
                no_of_gaps++;
            }
            else if(s2.charAt(i) == '-')
            {
                if(i>0 && s2.charAt(i-1) != '-')
                    no_of_opening_gaps++;

                middle.append(' ');
                no_of_gaps++;
            }
            else
            {
                middle.append(' ');
                no_of_mismatch++;
            }
            i++;
        }

        System.out.println(s1);
        System.out.println(middle.toString());
        System.out.println(s2);

        System.out.println("Matches: " + no_of_matches);
        System.out.println("Mismatches: " + no_of_mismatch);
        System.out.println("Gaps: " + no_of_gaps);
        System.out.println("Opening Gaps: "+ no_of_opening_gaps);
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

    private static Cell[][] initGlobalCells(int len1, int len2)
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

    private static Cell[][] initLocalCells(int len1, int len2)
    {
        Cell[][] table = new Cell[len1+1][len2+1];

        for(int j=0; j<table[0].length; j++)   // set 0th row 
        {
            table[0][j] = new Cell(0, 0, 0);
        }

        for(int i=0; i<table.length; i++)   // set 0th column 
        {
            table[i][0] = new Cell(0, 0, 0);
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

        Cell[][] table = globalForword(s1.toUpperCase().toCharArray(), s2.toUpperCase().toCharArray()); 
        globalBacktrack(s1.toCharArray(), s2.toCharArray(), table);

        
    }
}