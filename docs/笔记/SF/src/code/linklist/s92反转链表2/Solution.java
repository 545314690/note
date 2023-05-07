package code.linklist.s92反转链表2;

import java.util.Stack;

/**
 * @Author lisenmiao
 * @Date 2021/1/25 11:04
 */
class ListNode {
    int val;
    ListNode next;
    ListNode() {}
    ListNode(int val) { this.val = val; }
    ListNode(int val, ListNode next) { this.val = val; this.next = next; }
}
//92. 反转链表 II
//        反转从位置 m 到 n 的链表。请使用一趟扫描完成反转。
//
//        说明:
//        1 ≤ m ≤ n ≤ 链表长度。
//
//        示例:
//        输入: 1->2->3->4->5->NULL, m = 2, n = 4
//        输出: 1->4->3->2->5->NULL
class Solution {
//    public ListNode reverseBetween(ListNode head, int m, int n) {
//        ListNode cur = head;
//        ListNode pre = null;
//        for (int j = 1; j < m; j++) {
//            pre = cur;
//            cur = cur.next;
//        }
//        ListNode tail1 = pre;
//        ListNode tail2 = cur;
//        //        输入: 1->2->3->4->5->NULL, m = 2, n = 4
////        输出: 1->4->3->2->5->NULL
//        ListNode next = cur;
//        for (int i = m; i <=n; i++) {
//            pre = cur;
//            cur = cur.next;
//            next = cur.next;
//            cur.next = pre;
//            cur = next;
//        }
//        ListNode head2 = cur;
//        tail1.next = head2;
//        return  head;
//    }

    /**
     * 将元素放入数组，对数组进行操作
     * @param head
     * @param m
     * @param n
     * @return
     */
    public ListNode reverseBetween2(ListNode head, int m, int n) {
        int len = 0;
        ListNode cur = head;
        while (cur!=null){
            cur = cur.next;
            len++;
        }
        int [] arr = new int[len];
        cur = head;
        len = 0;
        while (cur!=null){
            arr[len] = cur.val;
            cur = cur.next;
            len++;
        }
        int left = m-1;
        int right = n-1;
        while (left<right){
            int temp = arr[left];
            arr[left] = arr[right];
            arr[right] = temp;
            left++;
            right--;
        }
        head = new ListNode(arr[0]);
        cur = head;
        for (int i = 1; i < len; i++) {
            ListNode node = new ListNode(arr[i]);
            cur.next = node;
            cur = node;
        }
        cur.next = null;
        return head;
    }

    /**
     * 使用栈的方式
     * @param head
     * @param m
     * @param n
     * @return
     */
    public ListNode reverseBetween(ListNode head, int m, int n) {
        Stack<ListNode> stack = new Stack<>();
        int len = 0;
        ListNode cur = head;
        ListNode newHead = null;
        ListNode newTail = null;
        while (cur!=null){
            len++;
            stack.push(cur);
            cur = cur.next;
            //小于m，进栈立刻出栈，当在[m-n)区间时，只进栈，不出栈，等于n时，弹出m-n的node，大于n与小于m相同
            if(len<m || len>=n){
                while (!stack.empty()){
                    ListNode pop = stack.pop();
                    if(newHead==null){
                        pop.next = null;
                        newHead = pop;
                        newTail = newHead;
                    }else {
                        newTail.next = pop;
                        newTail = pop ;
                        newTail.next = null;
                    }

                }
            }
        }
        return newHead;
    }
    public void printList(ListNode head){
        while (head!=null){
            System.out.print( head.val + " ") ;
            head= head.next;
        }
        System.out.println();
    }
    public static void main(String[] args) {
        Solution solution = new Solution();
        ListNode head = new ListNode(1);
        ListNode cur = head;
        for (int i = 2; i < 6; i++) {
            ListNode node = new ListNode(i);
            cur.next = node;
            cur = node;
        }
        cur.next = null;
        solution.printList(head);
        ListNode listNode = solution.reverseBetween(head, 2, 4);
        solution.printList(listNode);
    }
}