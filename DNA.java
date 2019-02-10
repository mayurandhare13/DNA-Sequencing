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
    public static String finalS1 = "";
    public static String finalS2 = "";
    static int optimum_score = 0;
    
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
            }
        }
        System.out.println("global score "+ Math.max(table[s1.length][s2.length].ins_score, Math.max(table[s1.length][s2.length].del_score, table[s1.length][s2.length].sub_score)));
        return table;
    }

    public static Cell[][] localForword(char[] s1, char[] s2)
    {
        Cell[][] table = initLocalCells(s1.length, s2.length);

        for(int i=1; i<table.length; i++)
        {
            for(int j=1; j<table[0].length; j++)
            {
                table[i][j] = new Cell(0, 0, 0);
                table[i][j].sub_score = substitution_score(table, i, j, s1, s2);

                table[i][j].del_score = deletion_score(table, i, j);

                table[i][j].ins_score = insertion_score(table, i, j);

                int max = Math.max(table[i][j].ins_score, Math.max(table[i][j].del_score, table[i][j].sub_score));
                
            }
        }
        return table;
    }

    public static void globalBacktrack(char[] str1, char[] str2, Cell[][] table)
    {

        int m = str1.length;
        int n = str2.length;
        int i = m;
        int j = n;

        optimum_score = Math.max(table[m][n].ins_score, Math.max(table[m][n].del_score, table[m][n].sub_score));

        int current_value = Math.max(table[i][j].ins_score, Math.max(table[i][j].del_score, table[i][j].sub_score));


        while(i > 0 && j > 0)
        {

            int temp_s;
            int temp_d;
            int temp_i;
            //System.out.println("hi");

            if(table[i][j].ins_score == current_value)
            {
                finalS1 = "-" + finalS1;
                finalS2 = str2[j-1] + finalS2;


                temp_s = table[i][j - 1].sub_score + h + gap_penalty;
                temp_d = table[i][j - 1].del_score + h + gap_penalty;
                temp_i = table[i][j - 1].ins_score + gap_penalty;

                if(table[i][j].ins_score == temp_s)
                {
                    current_value = table[i][j - 1].sub_score;
                  
                }
                else if(table[i][j].ins_score == temp_d)
                {
                    current_value = table[i][j - 1].del_score;
                 
                }
                else
                {
                    current_value = table[i][j - 1].ins_score;
                  
                }
                j--;
            }
            else if(table[i][j].del_score == current_value )
            {
                finalS1 = str1[i-1] + finalS1;
                finalS2 = "-" + finalS2;


                temp_s = table[i - 1][j].sub_score + h + gap_penalty;
                temp_d = table[i - 1][j].del_score + gap_penalty;
                temp_i = table[i - 1][j].ins_score + h + gap_penalty;

                if(table[i][j].del_score == temp_s)
                {
                    current_value = table[i - 1][j].sub_score;
                   
                }
                else if(table[i][j].del_score == temp_d)
                {
                    current_value = table[i - 1][j].del_score;
                   
                }
                else
                {
                    current_value = table[i - 1][j].ins_score;
                    
                }
                i--;
            }
            else
            {
                finalS1 = str1[i-1] + finalS1;
                finalS2 = str2[j-1] + finalS2;


                temp_s = table[i - 1][j - 1].sub_score + substitution(str1[i-1], str2[j-1]);
                temp_d = table[i - 1][j - 1].del_score + substitution(str1[i-1], str2[j-1]);
                temp_i = table[i - 1][j - 1].ins_score + substitution(str1[i-1], str2[j-1]);

                if(table[i][j].sub_score == temp_s)
                {
                    current_value = table[i - 1][j - 1].sub_score;
                    
                }
                else if(table[i][j].sub_score == temp_d)
                {
                    current_value = table[i - 1][j - 1].del_score;
                
                }
                else
                {
                    current_value = table[i - 1][j - 1].ins_score;
                    
                }
                i--;
                j--;
            }

        }

    }

    public static void localBacktrack(char[] str1, char[] str2, Cell[][] table)
    {
        int m = str1.length;
        int n = str2.length;
        int i = m;
        int j = n;
        int max_i = 0;
        int max_j = 0;

        optimum_score = Integer.MIN_VALUE;
        for(int r = i; r > 0; r--)
        {
            for(int c = j; c > 0; c--)
            {
                int max = Math.max(table[r][c].ins_score, Math.max(table[r][c].del_score, table[r][c].sub_score));
                if(optimum_score < max)
                {
                    optimum_score = max;
                    max_i = r;
                    max_j = c;
                }
            }
        }
        
        int current_value = optimum_score;
        System.out.println(current_value);
        i = max_i;
        j = max_j;

        while(i > 0 && j > 0 && (current_value > 0))
        {

            int temp_s;
            int temp_d;
            int temp_i;

            if(table[i][j].ins_score == current_value)
            {
                finalS1 = "-" + finalS1;
                finalS2 = str2[j-1] + finalS2;


                temp_s = table[i][j - 1].sub_score + h + gap_penalty;
                temp_d = table[i][j - 1].del_score + h + gap_penalty;
                temp_i = table[i][j - 1].ins_score + gap_penalty;

                if(table[i][j].ins_score == temp_s)
                {
                    current_value = table[i][j - 1].sub_score;
                  
                }
                else if(table[i][j].ins_score == temp_d)
                {
                    current_value = table[i][j - 1].del_score;
                }
                else
                {
                    current_value = table[i][j - 1].ins_score;
                }
                j--;
            }
            else if(table[i][j].del_score == current_value )
            {
                finalS1 = str1[i-1] + finalS1;
                finalS2 = "-" + finalS2;


                temp_s = table[i - 1][j].sub_score + h + gap_penalty;
                temp_d = table[i - 1][j].del_score + gap_penalty;
                temp_i = table[i - 1][j].ins_score + h + gap_penalty;

                if(table[i][j].del_score == temp_s)
                {
                    current_value = table[i - 1][j].sub_score;
                   
                }
                else if(table[i][j].del_score == temp_d)
                {
                    current_value = table[i - 1][j].del_score;
                   
                }
                else
                {
                    current_value = table[i - 1][j].ins_score;
                    
                }
                i--;
            }
            else
            {
                finalS1 = str1[i-1] + finalS1;
                finalS2 = str2[j-1] + finalS2;


                temp_s = table[i - 1][j - 1].sub_score + substitution(str1[i-1], str2[j-1]);
                temp_d = table[i - 1][j - 1].del_score + substitution(str1[i-1], str2[j-1]);
                temp_i = table[i - 1][j - 1].ins_score + substitution(str1[i-1], str2[j-1]);

                if(table[i][j].sub_score == temp_s)
                {
                    current_value = table[i - 1][j - 1].sub_score;
                    
                }
                else if(table[i][j].sub_score == temp_d)
                {
                    current_value = table[i - 1][j - 1].del_score;
                
                }
                else
                {
                    current_value = table[i - 1][j - 1].ins_score;
                    
                }
                i--;
                j--;
            }

        }

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
        return a == b ? match : mismatch;
    }

    private static String calValues(String s1, String s2)
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

        System.out.println("Matches: " + no_of_matches);
        System.out.println("Mismatches: " + no_of_mismatch);
        System.out.println("Gaps: " + no_of_gaps);
        System.out.println("Opening Gaps: "+ no_of_opening_gaps);

        return middle.toString();
    }

    public static void printAlign(char[] s1, char[] s2, char[] middle)
    {
        int c1 = 0;
        int c2 = 0;
        int prev;
        int len = middle.length;

        for(int i=0; i< len; i+= 60)
        {
            int count = 0;
            if(i+60 >= len)
            {
                prev = s1[i+0] != '-' ? c1+1 : c1; 
                System.out.print("s1\t" + prev + "\t");
                for(int j=i+0; j < len; j++)
                {
                    if(s1[j] != '-')
                        c1++;
                    System.out.print(s1[j]);
                }
                System.out.print("\t" + c1 + "\n");

                System.out.print(" \t\t");
                for(int j=i+0; j < len; j++)
                    System.out.print(middle[j]);
                System.out.println();

                prev = s2[i+0] != '-' ? c2+1 : c2; 
                System.out.print("s2\t" + prev + "\t");
                for(int j=i+0; j < len; j++)
                {
                    if(s2[j] != '-')
                        c2++;
                    System.out.print(s2[j]);
                }
                System.out.print("\t" + c2 + "\n");
                break;
            }
            
            prev = s1[i+0] != '-' ? c1+1 : c1; 
            System.out.print("s1\t" + prev + "\t");
            for(int j=i+0; j < i+60; j++)
            {
                if(s1[j] != '-')
                    c1++;
                System.out.print(s1[j]);
            }
            System.out.print("\t" + c1 + "\n");

            System.out.print(" \t\t");
            for(int j=i+0; j < i+60; j++)
                System.out.print(middle[j]);
            System.out.println();

            prev = s2[i+0] != '-' ? c2+1 : c2; 
            System.out.print("s2\t" + prev + "\t");
            for(int j=i+0; j < i+60; j++)
            {
                if(s2[j] != '-')
                    c2++;
                System.out.print(s2[j]);
            }
            System.out.print("\t" + c2 + "\n\n");
        }
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
        String inFile = null;
        if (0 < args.length) 
        {
            inFile = args[0];
        } 
        else 
        {
            System.err.println("Invalid arguments count:" + args.length);
            System.exit(0);
        }

        gap_penalty = -2;
        h = -5;
        String[] dnaStrings = readFile(inFile);

        // String s1 = "ACATGCTACACGTATCCGATACCCCGTAACCGATAACGATACACAGACCTCGTACGCTTGCTACAACGTACTCTATAACCGAGAACGATTGACATGCCTCGTACACATGCTACACGTACTCCGAT";
        // String s2 = "ACATGCGACACTACTCCGATACCCCGTAACCGATAACGATACAGAGACCTCGTACGCTTGCTAATAACCGAGAACGATTGACATTCCTCGTACAGCTACACGTACTCCGAT";

       // System.out.println(dnaStrings[0].length());
       // System.out.println(dnaStrings[1].length());

        Cell[][] table = globalForword(dnaStrings[0].toUpperCase().toCharArray(), dnaStrings[1].toUpperCase().toCharArray()); 

       // String[] finAlign = globalBacktrack(dnaStrings[0].toCharArray(), dnaStrings[1].toCharArray(), table);
        globalBacktrack(dnaStrings[0].toCharArray(), dnaStrings[1].toCharArray(), table);
        
        String middle = calValues(finalS1, finalS2);
        printAlign(finalS1.toCharArray(), finalS2.toCharArray(), middle.toCharArray());


    //     Cell[][] table = globalForword(s1.toUpperCase().toCharArray(), s2.toUpperCase().toCharArray()); 

    //    // String[] finAlign = globalBacktrack(dnaStrings[0].toCharArray(), dnaStrings[1].toCharArray(), table);
    //     globalBacktrack1(s1.toCharArray(), s2.toCharArray(), table);
        
    //     String middle = calValues(finalS1, finalS2);
    //     printAlign(finalS1.toCharArray(), finalS2.toCharArray(), middle.toCharArray());
    }
}