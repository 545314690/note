package code.括号序列;

import java.util.*;


public class Solution {
    /**
     * 
     * @param s string字符串 
     * @return bool布尔型
     */
    public boolean isValid (String s) {
        // write code here
        char [] arr = s.toCharArray();
        if((arr.length&1)!=0){
            return false;
        }
        Stack<Character> stack = new Stack<Character>();
        for(int i=0;i<arr.length;i++){
            char c = arr[i];
            if(c=='[' || c=='{'||c=='('){
               stack.push(c); 
            }else{
                if(c==']' && !stack.empty() &&  stack.pop()!='['){
                    return false;
                }else if(c==')' &&  !stack.empty() && stack.pop()!='('){
                    return false;
                }else if(c=='}' &&  !stack.empty() && stack.pop()!='{'){
                    return false;
                }
            }
            
        }
        if(stack.size()>0){
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        boolean valid = new Solution().isValid("}(])[{(}([[}])}]))})]]({{(])");
        System.out.println(valid);
    }
}