package code.tree.二叉排序树插入节点;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 *
 * 示例：
 *
 *     3
 *    / \
 *   2  20
 *     /  \
 *    7   15
 *
 */
class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;
    TreeNode() {}
    TreeNode(int val) { this.val = val; }
    TreeNode(int val, TreeNode left, TreeNode right) {
        this.val = val;
        this.left = left;
        this.right = right;
    }
}

class Solution {
    public boolean insertNode(TreeNode root,TreeNode node) {
        if(node==null){
            return false;
        }
        if(root==null){
            node = root;
            return true;
        }
        if(node.val>root.val){
            if(root.right==null){
                node = root.right;
                return true;
            }else {
                return insertNode(root.right,node);
            }
        }else {
            if(root.left==null){
                node = root.left;
                return true;
            }else {
                return insertNode(root.left,node);
            }
        }
    }
}