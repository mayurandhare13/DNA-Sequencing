import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

class DNA
{
    int gap_penalty, h, match, mismatch, optimum_score, no_of_gaps, no_of_matches, no_of_mismatch, no_of_opening_gaps;
    String finalS1, finalS2, middle, seq1, seq2;
    String[] dnaStrings;
    File outFile;

    public DNA()
    {
        finalS1 = "";
        finalS2 = "";
        optimum_score = 0;
        middle = "";
        outFile = new File("output.txt");
        no_of_gaps = 0;
        no_of_matches = 0;
        no_of_mismatch = 0;
        no_of_opening_gaps = 0;
    }
    
    public Cell[][] globalForword(char[] s1, char[] s2)
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

        optimum_score =  Math.max(table[s1.length][s2.length].ins_score, Math.max(table[s1.length][s2.length].del_score, table[s1.length][s2.length].sub_score));
        return table;
    }

    public Cell[][] localForword(char[] s1, char[] s2)
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

                //int max = Math.max(table[i][j].ins_score, Math.max(table[i][j].del_score, table[i][j].sub_score));
                
            }
        }
        return table;
    }

    public void globalBacktrack(char[] str1, char[] str2, Cell[][] table)
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

    public void localBacktrack(char[] str1, char[] str2, Cell[][] table)
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

    private int deletion_score(Cell[][] table, int i, int j)
    {
        return Math.max(table[i-1][j].sub_score + (h + gap_penalty), Math.max(table[i-1][j].del_score + gap_penalty, table[i-1][j].ins_score + (h + gap_penalty)));
    }

    private int insertion_score(Cell[][] table, int i, int j)
    {
        return Math.max(table[i][j-1].sub_score + (h + gap_penalty), Math.max(table[i][j-1].del_score + (h + gap_penalty), table[i][j-1].ins_score + gap_penalty));
    }

    private int substitution_score(Cell[][] table, int i, int j, char[] s1, char[] s2)
    {
        return Math.max(table[i-1][j-1].sub_score, Math.max(table[i-1][j-1].del_score, table[i-1][j-1].ins_score)) + substitution(s1[i-1], s2[j-1]);
    }

    private int substitution(char a, char b)
    {
        return a == b ? match : mismatch;
    }

    private void generateReport(String s1, String s2, BufferedWriter writer)
    {

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

        printAlign(middle.toString().toCharArray(), writer);
    }

    private void printAlign(char[] middle, BufferedWriter writer)
    {
        char[] s1 = finalS1.toCharArray();
        char[] s2 = finalS2.toCharArray();
        int c1 = 0;
        int c2 = 0;
        int prev;
        int len = middle.length;

        try
        {
            writer.write("Scores:  match = " + match + ", mismatch = "+ mismatch + ", h = " + h + ", g = " +gap_penalty);
            writer.newLine();
            writer.write("\nSequence 1 = " + seq1 + "\nlength = " + dnaStrings[0].length() + " characters.");
            writer.write("\n\nSequence 2 = " + seq2 + "\nlength = " + dnaStrings[1].length() + " characters.");
            writer.write("\n\n");

            for(int i=0; i< len; i+= 60)
            {
                int count = 0;
                if(i+60 >= len)
                {
                    prev = s1[i+0] != '-' ? c1+1 : c1; 
                    writer.write("s1\t" + prev + "\t");
                    for(int j=i+0; j < len; j++)
                    {
                        if(s1[j] != '-')
                            c1++;
                        writer.write(s1[j]);
                    }
                    writer.write("\t" + c1 + "\n");

                    writer.write(" \t\t");
                    for(int j=i+0; j < len; j++)
                        writer.write(middle[j]);
                    writer.newLine();

                    prev = s2[i+0] != '-' ? c2+1 : c2; 
                    writer.write("s2\t" + prev + "\t");
                    for(int j=i+0; j < len; j++)
                    {
                        if(s2[j] != '-')
                            c2++;
                        writer.write(s2[j]);
                    }
                    writer.write("\t" + c2 + "\n");
                    break;
                }
                
                prev = s1[i+0] != '-' ? c1+1 : c1; 
                writer.write("s1\t" + prev + "\t");
                for(int j=i+0; j < i+60; j++)
                {
                    if(s1[j] != '-')
                        c1++;
                    writer.write(s1[j]);
                }
                writer.write("\t" + c1 + "\n");

                writer.write(" \t\t");
                for(int j=i+0; j < i+60; j++)
                    writer.write(middle[j]);
                writer.newLine();

                prev = s2[i+0] != '-' ? c2+1 : c2; 
                writer.write("s2\t" + prev + "\t");
                for(int j=i+0; j < i+60; j++)
                {
                    if(s2[j] != '-')
                        c2++;
                    writer.write(s2[j]);
                }
                writer.write("\t" + c2 + "\n\n");
            }

            writer.newLine();
            writer.write("Optimum Score: " + optimum_score +"\n");
            writer.write("Matches: " + no_of_matches + "\n");
            writer.write("Mismatches: " + no_of_mismatch + "\n");
            writer.write("Gaps: " + no_of_gaps + "\n");
            writer.write("Opening Gaps: "+ no_of_opening_gaps + "\n");
            int total = no_of_matches + no_of_mismatch + no_of_gaps;
            float idRes = (no_of_matches/(float)total)*100;
            writer.write(String.format("Identities = %s / %s (%2.0f%%)\n", no_of_matches, total, idRes));
            idRes = (no_of_gaps / (float)total)*100;
            writer.write(String.format("Gaps = %s / %s (%2.0f%%)", no_of_gaps, total, idRes));
        }
        catch(IOException e)
        {
            System.err.println("Error occur while processing file: " + e.getLocalizedMessage());
            e.printStackTrace();
        }
        
    }

    private String[] readFile(String file) throws IOException
    {
        String S1 = null;
        String S2 = null;
        String[] dnaStrings = new String[2];
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        
        try 
        {
            String line = reader.readLine();
            seq1 = line;
            while ((line = reader.readLine()) != null)
            {
                if(line.startsWith(">"))
                {
                    S1 = builder.toString();
                    seq2 = line;
                    builder.delete(0, builder.length());
                    continue;
                }
                builder.append(line);
            }
            S2 = builder.toString();
        }
        catch(FileNotFoundException e)
        {
            System.err.println("File Not found" + e.getLocalizedMessage());
        }
        finally{
            dnaStrings[0] = S1;
            dnaStrings[1] = S2;
        }
        return dnaStrings;
    }

    private void readParams(String paramFile) throws IOException
    {
        if(paramFile == null)
        {
            System.out.println("\tNo parameter file given. Results are with default parameters.");

            gap_penalty = -2;
            h = -5;
            match = 1;
            mismatch = -2;

            System.out.println(String.format("\tDefault Parameters: match: %s, mismatch: %s, h: %s, g: %s", match, mismatch, h, gap_penalty));
        }
        else
        {
            Properties p = new Properties();
            InputStream inProp = new FileInputStream(paramFile);
            p.load(inProp);

            match = Integer.parseInt(p.getProperty("match"));
            mismatch = Integer.parseInt(p.getProperty("mismatch"));
            h = Integer.parseInt(p.getProperty("h"));
            gap_penalty = Integer.parseInt(p.getProperty("g"));
        }
    }

    private Cell[][] initGlobalCells(int len1, int len2)
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

    private Cell[][] initLocalCells(int len1, int len2)
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

    private void mainHandler(String inFile, int align_type, String paramFile) throws IOException
    {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(outFile)))
        {
            dnaStrings = this.readFile(inFile);
            readParams(paramFile);

            if(align_type == 0)
            {
                Cell[][] table = this.globalForword(dnaStrings[0].toUpperCase().toCharArray(), dnaStrings[1].toUpperCase().toCharArray()); 
                this.globalBacktrack(dnaStrings[0].toCharArray(), dnaStrings[1].toCharArray(), table);
            
                this.generateReport(finalS1, finalS2, writer);
            }
            else if(align_type == 1)
            {
                Cell[][] table = this.localForword(dnaStrings[0].toUpperCase().toCharArray(), dnaStrings[1].toUpperCase().toCharArray()); 
                this.localBacktrack(dnaStrings[0].toCharArray(), dnaStrings[1].toCharArray(), table);
            
                this.generateReport(finalS1, finalS2, writer);
            }
            else
            {
                System.out.println("wrong option");
                System.exit(101);
            }

        }
        catch(IOException e)
        {
            System.err.println("Error occur while processing file: " + e.getLocalizedMessage());
            e.printStackTrace();
            System.exit(101);
        }

        System.out.println("Done! Please check file.");
    }
    
    public static void main(String[] args) throws IOException
    {
        String inFile = null;
        String paramFile = null;
        int align_type = 0;

        if (0 < args.length) 
        {
            inFile = args[0];
            try {
                align_type = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("You missed it. Alignment Numbers are --> Global: 0 and Local: 1");
                System.out.println(e.getMessage());
                System.exit(1);
            }
            if(args.length == 3)
                paramFile = args[2];
            
        } 
        else 
        {
            System.err.println("Invalid arguments count:" + args.length);
            System.exit(0);
        }

        DNA d = new DNA();
        d.mainHandler(inFile, align_type, paramFile);
        
    }
}