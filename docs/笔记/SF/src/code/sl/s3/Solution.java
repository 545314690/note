package code.sl.s3;

class Solution {
    public int lengthOfLongestSubstring(String s) {
        if(s ==null){
            return 0;
        }
        char[] arr= s.toCharArray();
        int i=0,j=0,maxLen=0;
        
        while(i<arr.length && j<arr.length){
            String str = s.substring(i,j);
            if(str.contains(arr[j]+"")){
                i++;
            }else{
                j++;
            }
            maxLen = Math.max(j-i,maxLen);
        }
        return maxLen;

    }

    public static void main(String[] args) {
        System.out.println(new Solution().lengthOfLongestSubstring("pwwkew"));
    }
}