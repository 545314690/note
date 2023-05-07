package code.tree.二叉搜索树转换成一个排序的双向链表;

class TreeNode {
    int val = 0;
    TreeNode left = null;
    TreeNode right = null;

    public TreeNode() {
    }
    public TreeNode(int val) {
        this.val = val;
    }
}
public class Solution {
    


    public TreeNode Convert(TreeNode pRootOfTree) {
        if(pRootOfTree==null)
            return null;
        Convert(pRootOfTree.right);
        if(pre==null)
        {
            pre=pRootOfTree;
        }else{
            pRootOfTree.right=pre;
            pre.left=pRootOfTree;
            pre=pRootOfTree;
        }
        Convert(pRootOfTree.left);
        return pre;
    }
    TreeNode head=null;
    TreeNode pre=null;
    public TreeNode Convert2(TreeNode pRootOfTree) {

        ConvertSub(pRootOfTree);
        return head;
    }
    public void ConvertSub(TreeNode pRootOfTree){
        if(pRootOfTree==null){
            return ;
        }
        ConvertSub(pRootOfTree.left);
        if(head==null){
            head = pRootOfTree;
            pre = pRootOfTree;
        }else {
            pRootOfTree.left = pre;
            pre.right = pRootOfTree;
            pre = pRootOfTree;
        }
        ConvertSub(pRootOfTree.right);
    }

    public static void main(String[] args) {
        test01();
    }
    private static void printList(TreeNode head) {
        while (head != null) {
            System.out.print(head.val + "->");
            head = head.right;
        }
        System.out.println("null");
    }
    private static void printTree(TreeNode root) {
        if (root != null) {
            printTree(root.left);
            System.out.print(root.val + "->");
            printTree(root.right);
        }
    }
    //            10
    //         /      \
    //        6        14
    //       /\        /\
    //      4  8     12  16
    private static void test01() {
        TreeNode node10 = new TreeNode();
        node10.val = 10;
        TreeNode node6 = new TreeNode();
        node6.val = 6;
        TreeNode node14 = new TreeNode();
        node14.val = 14;
        TreeNode node4 = new TreeNode();
        node4.val = 4;
        TreeNode node8 = new TreeNode();
        node8.val = 8;
        TreeNode node12 = new TreeNode();
        node12.val = 12;
        TreeNode node16 = new TreeNode();
        node16.val = 16;
        node10.left = node6;
        node10.right = node14;
        node6.left = node4;
        node6.right = node8;
        node14.left = node12;
        node14.right = node16;
        System.out.print("Before convert: ");
        printTree(node10);
        System.out.println("null");
//        TreeNode head = new Solution().Convert(node10);
        TreeNode head = new Solution().Convert2(node10);
        System.out.print("After convert : ");
        printList(head);
        System.out.println();
    }
}