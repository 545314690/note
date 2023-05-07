package code.tree.NC16判断二叉树是否对称;

import java.util.*;

  class TreeNode {
    int val = 0;
    TreeNode left = null;
    TreeNode right = null;

      public TreeNode(int val) {
          this.val = val;
      }
  }

public class Solution {


        public boolean check (TreeNode left, TreeNode right) {
            if(left ==null && right ==null){
                return true;
            }
            if((left ==null && right !=null) || (left !=null && right ==null)){
                return false;
            }
            return left.val == right.val && check(left.left,right.right)&& check(left.right,right.left);
        }
        /**
         *
         * @param root TreeNode类
         * @return bool布尔型
         */
        public boolean isSymmetric (TreeNode root) {
            // write code here
            if(root==null){
                return true;
            }
            return check(root.left,root.right);
        }

    public static void main(String[] args) {
        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(2);
        root.right = new TreeNode(3);
        root.left.left = new TreeNode(3);
        root.right.left = new TreeNode(2);

        Solution solution = new Solution();
        boolean symmetric = solution.isSymmetric(root);
        System.out.println(symmetric);
    }
}