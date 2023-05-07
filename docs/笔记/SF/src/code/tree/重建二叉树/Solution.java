package code.tree.重建二叉树;

/**
 * Definition for binary tree
 *
 */
class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;
    TreeNode(int x) { val = x; }
}
public class Solution {
    
    public int getInRootIndex(int [] pre,int [] in) {
        for(int i=0;i<in.length;i++){
            if(pre[0] == in[i]){
                return i;
            }
        }
        return 0;
    }
    
    
    public TreeNode reConstructBinaryTree(int [] pre,int [] in) {
        if(pre.length==0 || in.length==0){
            return null;
        }
        TreeNode root = new TreeNode(pre[0]);
        int inRootIndex = getInRootIndex(pre,in);
        int inLeftLength = inRootIndex;
        int inRightLength = in.length - inLeftLength - 1;;
        int []rightIn = new int[inRightLength];
        int []leftIn = new int[inLeftLength];

        int []leftPre = new int[inLeftLength];
        int []rightPre = new int[inRightLength];
        
        System.arraycopy(pre,1,leftPre,0,leftPre.length);
        System.arraycopy(in,0,leftIn,0,leftIn.length);
        
        System.arraycopy(pre,1+inRootIndex,rightPre,0,rightPre.length);
        System.arraycopy(in,1+inRootIndex,rightIn,0,rightIn.length);
        
        root.left = reConstructBinaryTree(leftPre,leftIn);
        root.right = reConstructBinaryTree(rightPre,rightIn);
        return root;
    }

    public static void main(String[] args) {
        Solution solution = new Solution();
        int[]  pre = new int[]{1,2,4,3,5,6};
        int[]  in = new int[]{4,2,1,5,3,6};
        TreeNode treeNode = solution.reConstructBinaryTree(pre, in);

        System.out.println(treeNode);
    }
}