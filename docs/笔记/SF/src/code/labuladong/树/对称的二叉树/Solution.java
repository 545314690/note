package code.labuladong.树.对称的二叉树;

import code.labuladong.树.TreeNode;

import java.util.*;
public class Solution {


    public static void main(String[] args) {

        TreeNode root  = new TreeNode(1);
        root.left = new TreeNode(2, new TreeNode(3),null);
        root.right = new TreeNode(3, new TreeNode(2),null);
        System.out.println(root);

        System.out.println(new Solution().isSymmetrical(root));
    }
    boolean isSymmetrical(TreeNode pRoot) {
        if(pRoot == null){
            return true;
        }
        return compare(pRoot,pRoot);
        
    }

    boolean compare(TreeNode pRoot1, TreeNode pRoot2){
        if(pRoot1 == null && pRoot2 == null ){
            return true;
        }
        if(pRoot1 == null || pRoot2 == null ){
            return false;
        }
        return pRoot1.val == pRoot1.val && compare(pRoot1.left,pRoot2.right) && compare(pRoot1.right,pRoot2.left);

    }
}