package code.二叉树层序遍历;

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
    public ArrayList<ArrayList<Integer>> zigzagLevelOrder (TreeNode root) {
        // write code here
        if(root==null){
            return new ArrayList<ArrayList<Integer>>();
        }
        ArrayList<ArrayList<Integer>> all = new ArrayList<ArrayList<Integer>>();
        Queue<TreeNode> que = new LinkedList<TreeNode>();
        que.offer(root);
        boolean flag = true;
        while(que.size()>0){
            int size = que.size();
            ArrayList<Integer> list = new ArrayList<Integer>();
            for(int i=0;i<size;i++){
                TreeNode node = que.poll();
                if(node.left!=null){
                    que.offer(node.left);
                }
                if(node.right!=null){
                    que.offer(node.right);
                }
                if(flag){
                    list.add(node.val);
                }else{
                    list.add(0,node.val);
                }

            }
            all.add(list);
            flag=!flag;
        }
        return all;
    }

    public static void main(String[] args) {
        TreeNode node = new TreeNode(1);
        node.left = new TreeNode(2);
        node.right= new TreeNode(3);
        node.left.left = new TreeNode(4);
        node.right.right= new TreeNode(5);
        ArrayList<ArrayList<Integer>> arrayLists = new Solution().zigzagLevelOrder(node);
        System.out.println(arrayLists);
    }
}