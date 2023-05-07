package code.tree.实现二叉树先序中序和后序遍历;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 题目描述
 * 分别按照二叉树先序，中序和后序打印所有的节点。
 * 示例1
 * 输入
 * {1,2,3}
 * 返回值
 * [[1,2,3],[2,1,3],[2,3,1]]
 */
 class TreeNode {
   int val = 0;
   TreeNode left = null;
   TreeNode right = null;

    public TreeNode(int val) {
        this.val = val;
    }
}

public class Solution {
    /**
     * 
     * @param root TreeNode类 the root of binary tree
     * @return int整型二维数组
     */
    public int[][] threeOrders (TreeNode root) {
        // write code here

        List<Integer> l1 = new ArrayList<>();
        preOrder(root,l1);
        int[][] orders  = new int[3][l1.size()];
        for (int i = 0; i < l1.size(); i++) {
            orders[0][i] = l1.get(i);
        }
        List<Integer> l2 = new ArrayList<>();
        midOrder(root,l2);
        for (int i = 0; i < l2.size(); i++) {
            orders[1][i] = l2.get(i);
        }
        List<Integer> l3 = new ArrayList<>();
        houOrder(root,l3);
        for (int i = 0; i < l3.size(); i++) {
            orders[2][i] = l3.get(i);
        }
        return orders;
    }

    private void preOrder(TreeNode root, List<Integer> list) {
        if(root==null){
            return;
        }
        list.add(root.val);
        preOrder(root.left,list);
        preOrder(root.right,list);
    }
    private void midOrder(TreeNode root, List<Integer> list) {
        if(root==null){
            return;
        }
        midOrder(root.left,list);
        list.add(root.val);
        midOrder(root.right,list);
    }
    private void houOrder(TreeNode root, List<Integer> list) {
        if(root==null){
            return;
        }
        houOrder(root.left,list);
        houOrder(root.right,list);
        list.add(root.val);
    }

    public static void main(String[] args) {
        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(2);
        root.right = new TreeNode(3);
        int[][] ints = new Solution().threeOrders(root);
        System.out.println(ints);
    }
}