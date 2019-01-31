import java.util.Arrays;

class LCS
{
    public static int findSubs(char[] s1, char[] s2)
    {
        int[][] mat = new int[s1.length + 1][s2.length +1];
        int max = 0;

        for(int i=1; i<mat.length; i++)
        {
            for(int j=1; j<mat[0].length; j++)
            {
                if(s1[i-1] == s2[j-1])
                    mat[i][j] = mat[i-1][j-1] + 1;
                else
                    mat[i][j] = Math.max(mat[i-1][j], mat[i][j-1]);

                if(mat[i][j] > max)
                    max = mat[i][j];
            }
        }

        for(int[] r : mat)
        {
            System.out.println(Arrays.toString(r));
        }

        printSubs(mat, s1, s2, max);

        return max;
    }

    private static void printSubs(int[][] mat, char[] s1, char[] s2, int max)
    {
        char[] subs = new char[max];
        
        int i = mat.length-1, j = mat[0].length-1;
        while(mat[i][j] != 0)
        {
            if(mat[i][j] == Math.max(mat[i-1][j], mat[i][j-1]))
            {

                if(mat[i][j] == mat[i][j-1])
                    j = j-1;
                else
                    i = i-1;  
            }
            else if(mat[i][j] == mat[i-1][j] && mat[i][j] == mat[i][j-1])
            {
                i -= 1;
            }
            else if(mat[i][j] > Math.max(mat[i-1][j], mat[i][j-1]))
            {
                subs[--max] = s1[i-1];

                i--;
                j--;
            }
        }

        System.out.println(subs);
    }

    public static void main(String[] args) 
    {
        String s1 = "abcgtdaf";
        String s2 = "acgtbdcf";
        int maxLen = findSubs(s1.toCharArray(), s2.toCharArray()); 
        System.out.println(maxLen);
    }
}
