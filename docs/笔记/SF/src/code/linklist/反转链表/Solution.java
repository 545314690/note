package code.linklist.反转链表;

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

//输入一个链表，反转链表后，输出新链表的表头。
//        示例:
//        输入: {1,2,3}
//        输出: {3,2,1}
class Solution {


    public ListNode ReverseList(ListNode head) {
        ListNode cur = head;
        ListNode pre = null;
        ListNode next = null;
        while(cur!=null){
            next = cur.next;
            cur.next = pre;
            pre = cur;
            cur = next;
        }
        return pre;

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
        for (int i = 2; i < 4; i++) {
            ListNode node = new ListNode(i);
            cur.next = node;
            cur = node;
        }
        cur.next = null;
        solution.printList(head);
        ListNode listNode = solution.ReverseList(head);
        solution.printList(listNode);
    }
}