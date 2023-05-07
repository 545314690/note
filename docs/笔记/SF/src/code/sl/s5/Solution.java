package code.sl.s5;

/**
 * @Author lisenmiao
 * @Date 2020/12/28 17:49
 */
class Solution {
    //TODO:
    public String longestPalindrome(String s) {
        char[] chars = s.toCharArray();
        int i=0,j=0;
        int maxLen = 0;
        int m=0,n=0;
        while (i<=j && i<s.length()&& j<s.length()){
            String str = s.substring(i,j+1);
            int q=0;
            int p=0;
            boolean ok = true;
//            if(str.length()%2==0){
                p=i;
                q=j;
                while (p<=q && p>=i && q<=(j-i)){
                    if(chars[p] != chars[q]){
                       ok = false;
                        break;
                    }
                        q--;
                        p++;
                }
//            }else {
//
//            }
            if(ok && maxLen<(j-i+1)){
                m = i;
                n=j;
                j++;
            }else {
                i++;
            }
        }
        return s.substring(m,n+1);
    }

    public static void main(String[] args) {
        String s = "baabad";
        System.out.println(new Solution().longestPalindrome(s));
    }
}